import javax.swing.*;      // for all the Swing stuff (JWhatevers)

import java.io.*;          // for File, IOException
import java.util.*;        // for ArrayList
import java.awt.*;         // for Graphics, Graphics2D
import java.awt.image.*;   // for BufferedImage
import java.awt.event.*;   // for MouseListener
import java.awt.geom.*;    // for Rectangle2D
import javax.imageio.*;    // for ImageIO


//======================================================================
//* This class implements the agent canvas for our agent-based simulation,
//* specifically drawing the grid and (eventually) drawing the agents
//* that will run rampant thereupon.
//======================================================================
class AgentCanvas extends JPanel implements MouseListener
{
    public static final boolean DEBUG = true;
    
    // Constants for drawing a cell in the grid.
    private static final int OCCUPANCY_BAR_WIDTH = 
            Parameters.getAgentGUISize() / 10;
    private static final int GUI_XPAD = 0;
    private static final int GUI_YPAD = 0;
    
    private static final int STATUS_BAR_WIDTH =
            Parameters.getAgentGUISize() - OCCUPANCY_BAR_WIDTH;
    private static final boolean GRID_LEGEND_HORIZONTAL =
            Parameters.isGridLegendHorizontal();
    private boolean legend_visible;
    
    //private Shelter [][] shelterGrid;  // Grid of shelter locations
    ShelterGrid shelters;
    private int viewportX;  // where in the viewport to start drawing image/grid
    private int viewportY;  // (we want the image centered)

    private int gridWidth;  // width of grid in cells
    private int gridHeight; // height of grid in cells

    private SimulationManager simulation; // a reference to the simulation object

    //======================================================================
    //* public AgentCanvas()
    //* Constructor for the agent canvas.  Needs a reference to the 
    //* simulation manager.
    //======================================================================
    public AgentCanvas(SimulationManager theSimulation, ShelterGrid theShelters)
    {
        simulation = theSimulation;
        shelters = theShelters;
        legend_visible = true;

        addMouseListener(this);  // to handle mouse clicks
        
        // Create shelter grid
        gridWidth = Parameters.getDefaultGridSize();
        gridHeight = gridWidth; // We're square.
        //shelterGrid = new Shelter[gridWidth][gridHeight];
        
        updateGrid(shelters);
    }

    //======================================================================
    //* public int getGridWidth()
    //* public int getGridHeight()
    //* Simple accessor methods.
    //======================================================================
    public int getGridWidth()  { return gridWidth; }
    public int getGridHeight() { return gridHeight; }
    
    //======================================================================
    //* public void changeBackground()
    //* I used to allow the user to select an image background.  Dropped it,
    //* but if we ever back to it, we should take any existing agents and 
    //* reposition at random b/c the image is likely a different size.
    //======================================================================
    public void updateGrid(ShelterGrid shelters)
    {
        this.shelters = shelters;
        gridWidth = Parameters.getDefaultGridSize();
        gridHeight = gridWidth;

        simulation.reset();  // remove all agents, etc.

        // call repaint to redisplay the new background, then agents & grid
        repaint();
    }

    //======================================================================
    //* public void paintComponent(Graphics g)
    //* What happens whenever the agent canvas is (re)drawn.
    //======================================================================
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // safest to create a copy of the graphics component -- one must
        // ensure that no changes are made to the original
        Graphics2D    graphics = (Graphics2D) g.create();

        JViewport   viewport = null;
        JScrollPane scrollPane = null;
        Insets      borders = null;

        int viewportWidth  = 0;
        int viewportHeight = 0;
        int agentGUISize   = Parameters.getAgentGUISize();
        int imageWidth     = gridWidth * agentGUISize;
        int imageHeight    = gridHeight * agentGUISize;

        // make sure that we're grabbing onto the viewport of the scroll pane
        Component ancestor = getParent();
        if (ancestor == null || !(ancestor instanceof JViewport))
        {
            //Exception e = new Exception(
            //    "AgentCanvas instance must be within JScrollPane instance");
            //e.printStackTrace();
            //return;

            viewportWidth  = imageWidth;
            viewportHeight = imageHeight;

            borders = new Insets(5,5,5,5);
        }
        else
        {
            // presumably we have the viewport of scroll pane containing 'this'
            viewport  = (JViewport) ancestor;

            viewportWidth  = viewport.getWidth();
            viewportHeight = viewport.getHeight();

            scrollPane = (JScrollPane) viewport.getParent();
            borders = scrollPane.getInsets();
        }

        // Note that drawImage automatically scales the image to fit that 
        // rectangle.
        int renderWidth  = gridWidth * agentGUISize;
        int renderHeight = gridHeight * agentGUISize;

        // determine the starting (x,y) in the viewport where the image
        // will be drawn
        viewportX = 10 + Math.max((viewportWidth - renderWidth)  / 2, 0);
        viewportY = 20 + Math.max((viewportHeight - renderHeight) / 2, 0);

        // in case there was a previous image, clear things out
        //graphics.clearRect(0, 0, viewportWidth, viewportHeight);
        //graphics.clearRect(viewportX, viewportY, viewportWidth, viewportHeight);
        graphics.clearRect(viewportX, viewportY, renderWidth, renderHeight);

        // now draw the shelters
        for (int col = 0; col < gridWidth; col++ )
        {
            for (int row = 0; row < gridHeight; row++ )
            {
                Shelter s = shelters.getShelterAt( col, row ); //shelterGrid[col][row];

                // make sure not to draw any agent outside the image boundaries;
                // remember that graphics x corresponds to column and graphics y
                // corresponds to row
                if ((row >= 0) && (col >= 0) &&
                    ((row * agentGUISize) + agentGUISize <= renderHeight) &&
                    ((col * agentGUISize) + agentGUISize <= renderWidth))
                {
                    int guiX = GUI_XPAD + viewportX + (col * agentGUISize);
                    int guiY = GUI_YPAD + viewportY + (row * agentGUISize);

                    int occupants = s.getCurrentOccupancy();
                    int capacity = s.getMaxCapacity();
                    int infested = s.getInfestedCount();
                    int infected = s.getInfectedCount();
                    
                    if ( DEBUG ){
                        if ( infested > occupants || infected > occupants ) {
                            System.out.println(
                                    "Shelter(" + col + ", " + row + ") " + 
                                    " occupants = " + occupants + 
                                    "  infested = " + infested + 
                                    "  infected = " + infected );
                        }
                    }
                    
                    double proportionInfested = infested / (double) occupants;
                    double proportionInfected = infected / (double) occupants;
                    if ( occupants == 0 ){
                        proportionInfested = 0.0;
                        proportionInfected = 0.0;
                    }
                    double proportionOccupied = occupants / (double) capacity;
                    double proportionUnoccupied = 1.0 - proportionOccupied;
                    
                    // Shelter Occupancy info
                    
                    graphics.setPaint( Color.white );
                    int unoccupiedHeight = 
                            (int) Math.round(agentGUISize * proportionUnoccupied);
                    int occupiedHeight = agentGUISize - unoccupiedHeight;
                    
                    // Proportion of shelter currently not in use
                    graphics.fillRect( guiX, guiY, 
                                       OCCUPANCY_BAR_WIDTH, unoccupiedHeight );
                    
                    graphics.setPaint( Color.black );
                    graphics.fillRect(guiX, guiY + unoccupiedHeight, 
                                      OCCUPANCY_BAR_WIDTH, occupiedHeight );
                    
                    if (occupants > 0){
                        graphics.setPaint( Color.white );
                        String str = ""+occupants;
                        graphics.drawString(str, guiX + 2, guiY + unoccupiedHeight + 15);
                        graphics.setPaint( Color.black );
                    }
                    
                    // Infestation info
                    
                    // Color of upper half of grid square should get more purple
                    // as more Hosts are infested
                    int red = 255 - (int) Math.round( 255 * proportionInfested);
                    int green = 255 - (int) Math.round( 255 * proportionInfested);
                    int blue = 255;
                    
                    Color infestColor = null;
                    try{
                        infestColor = new Color( red, green, blue );
                    } catch (IllegalArgumentException ie ) {
                        System.out.println("Weird color for infested block: (" 
                                + red + ", " + green + ", " + blue + ")");
                        System.out.println("Shelter at (" + row + ", " + col +")");
                        System.out.println("Usually means infested count is off.");
                        infestColor = Color.white;
                    }

                    graphics.setPaint( infestColor );

                    graphics.fillRect(guiX + OCCUPANCY_BAR_WIDTH, guiY, STATUS_BAR_WIDTH, agentGUISize / 2);
                    
                    String data = "" + occupants + "/" + infested;
                    graphics.setPaint( Color.black );
                    graphics.drawString(data, guiX + OCCUPANCY_BAR_WIDTH + 5, guiY + agentGUISize/2 - 10 );
                    
                    // Infection info
                    
                    // Color of lower half of grid square should get redder
                    // as more Hosts are infected.
                    red = 255;
                    green = 255 - (int) Math.round( 255 * proportionInfected);
                    blue = 255 - (int) Math.round( 255 * proportionInfected);
                    
                    Color infectColor = null;
                    try {
                        infectColor = new Color( red, green, blue );
                    } catch (IllegalArgumentException ie) {
                        System.out.println("Weird color for infected block: (" 
                                + red + ", " + green + ", " + blue + ")");
                        System.out.println("Shelter at (" + row + ", " + col +")");
                        System.out.println("Usually means infected count is off.");
                        infestColor = Color.white;                        
                    }

                    graphics.setPaint( infectColor );

                    graphics.fillRect(guiX + OCCUPANCY_BAR_WIDTH, 
                                      guiY + agentGUISize/2, 
                                      STATUS_BAR_WIDTH, 
                                      agentGUISize / 2);
                    
                    data = "" + occupants + "/" + infected;
                    graphics.setPaint( Color.black );
                    graphics.drawString(data, guiX + OCCUPANCY_BAR_WIDTH + 5, guiY + agentGUISize - 10 );
                    
                    // Color of lower half of grid square should get bluer
                    // as more Hosts are infected

                    /*
                    if (a.isTreated())
                    {
                        // if treated with antibiotic, draw a little dot in the
                        // middle of the rendered agent
                        int dotSize = 2;
                        graphics.setPaint(Color.black);
                        graphics.fillRect(guiX + ((agentGUISize - dotSize) / 2),
                                          guiY + ((agentGUISize - dotSize) / 2),
                                          dotSize, dotSize);
                    }
                    */
                    
                    drawShelterFrame(graphics, 
                                     guiX, guiY, agentGUISize, agentGUISize );
                }
            }
        }

        // draw the grid last so that it will overlay the agent squares 
        //drawGrid(graphics, viewportX, viewportY, renderWidth, renderHeight);
        drawGrid(graphics, viewportX + GUI_XPAD, viewportY + GUI_YPAD, renderWidth, renderHeight);

        // show the number of infected/uninfected agents
        drawAgentInfo(graphics, viewportX, viewportY, 
                renderWidth, renderHeight, borders);

        revalidate();

        // get rid of the graphics copy
        graphics.dispose();
    }

    //======================================================================
    //* private void drawGrid(Graphics2D graphics)
    //* Draw a grid on top of the background and agents.
    //======================================================================
    private void drawGrid(Graphics2D graphics, int x, int y,
                          int width, int height)
    {
        graphics.setPaint(Color.black);

        // the columns
        graphics.setStroke(new BasicStroke(2));
        int agentGUISize = Parameters.getAgentGUISize();
        for (int row = 0; row < width / agentGUISize; row++)
            graphics.drawLine(x + (row * agentGUISize), y, 
                              x + (row * agentGUISize), y + height - 1);

        // the rows
        for (int col = 0; col < height / agentGUISize; col++){
            graphics.drawLine(x, y + (col * agentGUISize),
                              x + width - 1, y + (col * agentGUISize));
        }
        
        // the border
        graphics.drawLine(x, y, x, y + height);
        graphics.drawLine(x, y, x + width, y);
        graphics.drawLine(x + width, y, x + width, y + height);
        graphics.drawLine(x, y + height, x + width, y + height);
        graphics.setStroke(new BasicStroke());
    }
    
    private void drawShelterFrame( Graphics2D graphics, int x, int y, 
                                   int width, int height )
    {
        graphics.setPaint( Color.black );
        
        // Occupancy bar right border
        graphics.drawLine(x + OCCUPANCY_BAR_WIDTH, y, 
                          x + OCCUPANCY_BAR_WIDTH, y + height) ;
        
        // Status separator
        graphics.drawLine( x + OCCUPANCY_BAR_WIDTH, y + height/2,
                           x + width, y + height/2 );
    }

    /**
     * Draw the "caption" for the ShelterGrid
     * 
     * @param graphics
     * @param x
     * @param y
     * @param width
     * @param height
     * @param borders 
     */
    private void drawAgentInfo(Graphics2D graphics, int x, int y,
                               int width, int height, Insets borders)
    {
        final int verticalSpaceBeforeText = 20;
        // Find the size of string in the font being used by the current
        // Graphics2D context.
        FontMetrics font = graphics.getFontMetrics();
        String agentInfo = null;
        int textWidth = 0;
        int textHeight = 0;
        int totalTextHeight = 0;
        int maxTextWidth = 0;

        if (GRID_LEGEND_HORIZONTAL) {
            agentInfo = "Infected: " + simulation.getNumInfectedHumans() +
                 "   " +
                 "Uninfected: " + simulation.getNumUninfectedAgents() +
                 "   " +
                 "Infested: " + simulation.getNumInfestedHosts() +
                 "   " +
                 "Uninfested: " + simulation.getNumUninfestedHosts() +
                 "   " +
                 "Treated: " + simulation.getNumTreatedAgents();
            
            Rectangle2D rect = font.getStringBounds(agentInfo, graphics);

            textWidth     = (int)(rect.getWidth());
            textHeight    = (int)(rect.getHeight());
            totalTextHeight = textHeight;
            maxTextWidth = textWidth;
            int startStringAt = (width - textWidth)  / 2;

            // center text horizontally (max sure left side at least draws w/in
            // the viewport window -- i.e., x at least 0)
            graphics.drawString(agentInfo, Math.max(x + startStringAt, 0), 
                                           y + height + verticalSpaceBeforeText);

        } else {
            agentInfo = "Infected: " + simulation.getNumInfectedHumans();
            Rectangle2D rect = font.getStringBounds(agentInfo, graphics);

            textWidth     = (int)(rect.getWidth());
            textHeight    = (int)(rect.getHeight());
            totalTextHeight = textHeight;
            maxTextWidth = textWidth;
            
            int startStringAt = (width - textWidth)  / 2;

            graphics.drawString(agentInfo, Math.max(x + startStringAt, 0), 
                                           y + height + totalTextHeight + verticalSpaceBeforeText);
            
            agentInfo = "Uninfected: " + simulation.getNumUninfectedAgents();
            rect = font.getStringBounds(agentInfo, graphics);

            textWidth     = (int)(rect.getWidth());
            textHeight    = (int)(rect.getHeight());
            totalTextHeight += textHeight;
            if (textWidth > maxTextWidth){
                maxTextWidth = textWidth;
            }
            //startStringAt = (width - textWidth)  / 2;

            graphics.drawString(agentInfo, Math.max(x + startStringAt, 0), 
                                           y + height + totalTextHeight + verticalSpaceBeforeText);
            
            agentInfo = "Infested: " + simulation.getNumInfestedHosts();
            rect = font.getStringBounds(agentInfo, graphics);

            textWidth     = (int)(rect.getWidth());
            textHeight    = (int)(rect.getHeight());
            totalTextHeight += textHeight;
            if (textWidth > maxTextWidth){
                maxTextWidth = textWidth;
            }
             //startStringAt = (width - textWidth)  / 2;
            
            graphics.drawString(agentInfo, Math.max(x + startStringAt, 0), 
                                           y + height + totalTextHeight + verticalSpaceBeforeText);
            
            agentInfo = "Uninfested: " + simulation.getNumUninfestedHosts();
            rect = font.getStringBounds(agentInfo, graphics);

            textWidth     = (int)(rect.getWidth());
            textHeight    = (int)(rect.getHeight());
            totalTextHeight += textHeight;
            if (textWidth > maxTextWidth){
                maxTextWidth = textWidth;
            }
             //startStringAt = (width - textWidth)  / 2;
            
            graphics.drawString(agentInfo, Math.max(x + startStringAt, 0), 
                                           y + height + totalTextHeight + verticalSpaceBeforeText);
            agentInfo = "Treated: " + simulation.getNumTreatedAgents();
            rect = font.getStringBounds(agentInfo, graphics);

            textWidth     = (int)(rect.getWidth());
            textHeight    = (int)(rect.getHeight());
            totalTextHeight += textHeight;
            if (textWidth > maxTextWidth){
                maxTextWidth = textWidth;
            }
             //startStringAt = (width - textWidth)  / 2;
            
            graphics.drawString(agentInfo, Math.max(x + startStringAt, 0), 
                                           y + height + totalTextHeight + verticalSpaceBeforeText);
        }

        // Make sure the image plus text (which may be a new one loaded in) is
        // visible in the scroll pane.  If this isn't somewhere, scrollbars 
        // won't work in the main screen's encompassing JScrollPane.
        setPreferredSize(
            new Dimension(
                    Math.max(width + borders.left + borders.right, maxTextWidth + 10),
                     height + borders.top + borders.bottom 
                            + verticalSpaceBeforeText + totalTextHeight + 10));

    }

    //======================================================================
    //* private Point convertToLogical(int x, int y)
    //* This takes physical (x,y) and converts it to logical (x,y) in the
    //* context of the agent/grid size.
    //======================================================================
    private Point convertToLogical(int x, int y)
    {
        // convert to next lowest multiple of agentGUISize using int division
        int agentGUISize = Parameters.getAgentGUISize();
        int newX = (x - viewportX) / agentGUISize;
        int newY = (y - viewportY) / agentGUISize;

        return new Point(newX, newY);
    }

    //======================================================================
    //* private void inspectShelterAt(Point p)
    //======================================================================
    private void inspectShelterAt(Point p)
    {
        // Ignore clicks outside the actual grid of shelters
        int col = (int) p.getX();
        if ( col < 0 ) return;
        if ( col >= shelters.getGridWidth() ) return;
        
        int row = (int) p.getY();
        if ( row < 0 ) return;
        if ( row >= shelters.getGridHeight() ) return;
        
        Shelter s = shelters.getShelterAt( col, row );//shelterGrid[col][row];

        if (s != null)
        {
            // use hash map to see if the window is already created, and if so,
            // just focus; otherwise, create a new inspector window
            double time = simulation.getTime();
            String id = s.getRow() + "," + s.getCol() + " " + time;
            ShelterInspector window = ShelterInspector.windowList.get(id);
            if (window == null)
            {
                window = new ShelterInspector(s, time, id);
                ShelterInspector.windowList.put(id, window);
            }
            window.setVisible(true);
        }
    }

    //************************************************************
    //* The following methods are for implementing MouseListener.
    //* We're really only interested in mouseClicked().
    //************************************************************

    public void mouseClicked(MouseEvent e)
    {
        Point p = convertToLogical(e.getX(), e.getY());
        inspectShelterAt(p);
    }

    public void mousePressed(MouseEvent e)  {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e)  {}
    public void mouseExited(MouseEvent e)   {}
}
