import squint.*;            // for GUIManager
import javax.swing.*;       // for all the Swing stuff (JWhatevers)
import java.util.*;
import java.io.*;           // for File, IOException
import java.awt.*;          // for Graphics, Graphics2D
import java.awt.geom.*;     // for Rectangle2D
import java.util.ArrayList; // for ArrayList

//======================================================================
//* This class provides a simple graphical interface that will display
//* the time-series information of number of infected, uninfected, and
//* treated agents at each integer time step.
//*
//* The constructor expects the maximum time and maximum number of agents
//* as arguments.
//*
//* The updateCounts() method will be called from within the simulation
//* code each time the simulation time crosses an integer boundary.
//*
//* Author: Barry Lawson (blawson)
//* Date:   23 September 2009
//======================================================================
class TimeSeriesDialog extends GUIManager
{
    // define how big the entire window will be
    private final int WINDOW_WIDTH  = 650;
    private final int WINDOW_HEIGHT = 450;

    // provide the (x,y) locations for the corners of the rectangle
    // that comprise the graph
    private final int GRAPH_UPPER_LEFT_X  =  75;
    private final int GRAPH_UPPER_LEFT_Y  =  75;
    private final int GRAPH_LOWER_RIGHT_X = 475;
    private final int GRAPH_LOWER_RIGHT_Y = 375;
    
    private final int LEGEND_UPPER_LEFT_X = 500;
    private final int LEGEND_UPPER_LEFT_Y = 100;
    private final int LEGEND_LOWER_RIGHT_X = 630;
    private final int LEGEND_LOWER_RIGHT_Y = 200;

    // these are needed to properly scale the axes
    private int maxAgents;
    private int maxTime;

    private double horizontalStep;  // one step along horizontal, scaled to max
    private double verticalStep;    // one step along vertical, scaled to max

    // arrays of ints for the counts for display time series info
    private ArrayList<Integer> infectedCounts;
    private ArrayList<Integer> uninfectedCounts;
    private ArrayList<Integer> infestedCounts;
    private ArrayList<Integer> uninfestedCounts;
    private ArrayList<Integer> treatedCounts;
    
    private JButton saveButton;
    private JButton loadButton;

    //======================================================================
    //* public TimeSeriesDialog(int agents, int time)
    //* Constructor for the class.  Just creates three new array lists to
    //* hold the counts for displaying the time-series info, and sets up
    //* a few instance variables used when drawing the time-series curves.
    //* Finally, pops up a new window right before your very eyes!
    //======================================================================
    public TimeSeriesDialog(int agents, int time)
    {
        maxAgents = agents;
        maxTime   = time;

        // determine what one step in the horizontal and vertical directions
        // means by taking the total distance and dividing it by the maximum
        // associated with that direction
        horizontalStep = (GRAPH_LOWER_RIGHT_X - GRAPH_UPPER_LEFT_X) 
                          / (double) maxTime;

        verticalStep   = (GRAPH_LOWER_RIGHT_Y - GRAPH_UPPER_LEFT_Y) 
                          / (double) maxAgents;

        infectedCounts   = new ArrayList<Integer>();
        uninfectedCounts = new ArrayList<Integer>();
        infestedCounts   = new ArrayList<Integer>();
        uninfestedCounts = new ArrayList<Integer>();
        treatedCounts    = new ArrayList<Integer>();

        this.createWindow( WINDOW_WIDTH, WINDOW_HEIGHT, "Agent Time Series" );
        
        saveButton = new JButton( "Save" );
        add( saveButton );
        
        loadButton = new JButton( "Load" );
        add( loadButton );
        

    } // end TimeSeriesDialog constructor
    
    public void buttonClicked( JButton whichButton )
    {
        if ( whichButton == saveButton) {
            saveCounts();
        } else if ( whichButton == loadButton ){
            loadCounts();
        }
    }
    
    public void saveCounts()
    {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showSaveDialog(this);
        if ( returnVal == JFileChooser.APPROVE_OPTION ){
            // Picked a target file, now save the counts in CSV format
            File outFile = chooser.getSelectedFile();
            try {
                PrintWriter output = new PrintWriter( outFile );
                
                // Write out a header line
                output.println(
                        "Time,Infected,Uninfected,Infested,Uninfested,Treated" );
                
                for (int i = 0; i < infectedCounts.size(); i++ ){
                    output.println(
                            "" + i + "," +
                            infectedCounts.get(i) + "," +
                            uninfectedCounts.get(i) + "," +
                            infestedCounts.get(i) + "," +
                            uninfestedCounts.get(i) + "," +
                            treatedCounts.get(i));
                }
                
                output.close();
            } catch ( FileNotFoundException e ){
                e.printStackTrace();
            }
        }
    }
    
    public void loadCounts()
    {
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if ( returnVal == JFileChooser.APPROVE_OPTION ){
            // Picked a target file, now save the counts in CSV format
            File inFile = chooser.getSelectedFile();
            
            // Clear out the ArrayLists
            infectedCounts.clear();
            uninfectedCounts.clear();
            infestedCounts.clear();
            uninfestedCounts.clear();
            treatedCounts.clear();

            try {
                Scanner input = new Scanner( inFile );
                
                // Skip the column headers
                String line = input.nextLine();
                
                while( input.hasNextLine()) {
                    line = input.nextLine();
                    
                    // Set up a scanner to handle one line delimited by ","
                    Scanner s = new Scanner( line );
                    s.useDelimiter(",");
                    // Skip the time.
                    int time = s.nextInt();
                    infectedCounts.add(s.nextInt());
                    uninfectedCounts.add(s.nextInt());
                    infestedCounts.add(s.nextInt());
                    uninfestedCounts.add(s.nextInt());
                    treatedCounts.add(s.nextInt());
                }
                
                repaint();
            } catch ( FileNotFoundException e ){
                e.printStackTrace();
            }
        }
    }

    //======================================================================
    //* public void updateCounts( int infected, int uninfected, int treated )
    //* This method will be invoked by the simulation code to update the
    //* counts at each integer time step.  It will then repaint the window,
    //* displaying the updated information.
    //======================================================================
    public void updateCounts( int infected, int uninfected, 
                              int infested, int uninfested, int treated )
    {
        // this should only be called at integer time steps
        infectedCounts.add( new Integer(infected) );
        uninfectedCounts.add( new Integer(uninfected) );
        infestedCounts.add( infested );
        uninfestedCounts.add( uninfested );
        treatedCounts.add( new Integer(treated) );

        repaint();  // rediplay with new counts on screen
        
    } // end updateCounts()

    //======================================================================
    //* public void paintComponent(Graphics g)
    //* What happens whenever the time series window is (re)drawn.
    //======================================================================
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        // safest to create a copy of the graphics component -- one must
        // ensure that no changes are made to the original
        Graphics2D graphics = (Graphics2D) g.create();

        graphics.clearRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
        drawTimeSeries(graphics);
        drawAxes(graphics);
        drawLegend(graphics);

        revalidate();

        // get rid of the graphics copy
        graphics.dispose();

    } // end paintComponent()

    //======================================================================
    //* private void drawTimeSeries(Graphics2D graphics)
    //* This method draws three time-series curves by plotting three 2x2 
    //* rectangles at each integer time point: a green one to indicate
    //* the number of uninfected agents; a red one to indicate
    //* the number of infected agents; and a black one to
    //* indicate the number of treated agents.
    //======================================================================
    private void drawTimeSeries(Graphics2D graphics)
    {
        // first draw the infecteds
        graphics.setPaint(Color.red);
        for (int i = 0; i < infectedCounts.size(); i++)
        {
            graphics.fillRect( 
               GRAPH_UPPER_LEFT_X + (int)((i + 1) * horizontalStep),
               GRAPH_LOWER_RIGHT_Y - 
                  (int)(infectedCounts.get(i).intValue() * verticalStep),
               2, 2 );  // a 2x2 rectangle
        }

        // then draw the uninfecteds
        graphics.setPaint(Color.green);
        for (int i = 0; i < uninfectedCounts.size(); i++)
        {
            graphics.fillRect( 
               GRAPH_UPPER_LEFT_X + (int)((i + 1) * horizontalStep),
               GRAPH_LOWER_RIGHT_Y - 
                  (int)(uninfectedCounts.get(i).intValue() * verticalStep),
               2, 2 );  // a 2x2 rectangle
        }
        
        // the infested
        graphics.setPaint(Color.blue);
        for (int i = 0; i < infestedCounts.size(); i++)
        {
            graphics.fillRect( 
               GRAPH_UPPER_LEFT_X + (int)((i + 1) * horizontalStep),
               GRAPH_LOWER_RIGHT_Y - 
                  (int)(infestedCounts.get(i).intValue() * verticalStep),
               2, 2 );  // a 2x2 rectangle
        }

        // then draw the uninfecteds
        graphics.setPaint(Color.magenta);
        for (int i = 0; i < uninfestedCounts.size(); i++)
        {
            graphics.fillRect( 
               GRAPH_UPPER_LEFT_X + (int)((i + 1) * horizontalStep),
               GRAPH_LOWER_RIGHT_Y - 
                  (int)(uninfestedCounts.get(i).intValue() * verticalStep),
               2, 2 );  // a 2x2 rectangle
        }
        
        

        // finally draw the treated
        graphics.setPaint(Color.black);
        for (int i = 0; i < treatedCounts.size(); i++)
        {
            graphics.fillRect( 
               GRAPH_UPPER_LEFT_X + (int)((i + 1) * horizontalStep),
               GRAPH_LOWER_RIGHT_Y - 
                  (int)(treatedCounts.get(i).intValue() * verticalStep),
               2, 2 );  // a 2x2 rectangle
        }

    } // end drawTimeSeries()

    //======================================================================
    //* private void drawAxes(Graphics2D graphics)
    //* This method draws the vertical and horizontal axes, tick marks,
    //* and labels for the time series graphic.
    //======================================================================
    private void drawAxes(Graphics2D graphics)
    {
        graphics.setPaint(Color.black);

        // the left axis
        graphics.drawLine( GRAPH_UPPER_LEFT_X, GRAPH_UPPER_LEFT_Y,
                           GRAPH_UPPER_LEFT_X, GRAPH_LOWER_RIGHT_Y );

        // the right axis
        graphics.drawLine( GRAPH_UPPER_LEFT_X, GRAPH_LOWER_RIGHT_Y,
                           GRAPH_LOWER_RIGHT_X, GRAPH_LOWER_RIGHT_Y );

        // the static labels etc.
        graphics.drawString( "Agents", 
            10,
            GRAPH_UPPER_LEFT_Y + (GRAPH_LOWER_RIGHT_Y - GRAPH_UPPER_LEFT_Y) / 2 );

        graphics.drawString( "0", 
            GRAPH_UPPER_LEFT_X - 10, 
            GRAPH_LOWER_RIGHT_Y + 10 );

        graphics.drawString( "Time", 
            GRAPH_UPPER_LEFT_X + (GRAPH_LOWER_RIGHT_X - GRAPH_UPPER_LEFT_X) / 2,
            GRAPH_LOWER_RIGHT_Y + 30 );


        // put label for max number of agents near upper left
        FontMetrics font  = graphics.getFontMetrics();
        String      label = String.valueOf( maxAgents );
        Rectangle2D rect  = font.getStringBounds(label, graphics);

        graphics.drawString( label, 
            GRAPH_UPPER_LEFT_X - 10 - (int)(rect.getWidth()),
            GRAPH_UPPER_LEFT_Y + ((int)(rect.getHeight()) / 2) );

        // put label for max time near upper left
        label = String.valueOf( maxTime );
        rect  = font.getStringBounds(label, graphics);

        graphics.drawString( label, 
            GRAPH_LOWER_RIGHT_X + 10,
            GRAPH_LOWER_RIGHT_Y + ((int)(rect.getHeight()) / 2) );

        // put tick marks on the left axis -- ten of 'em each just for estimating
        int tickSeparation = (GRAPH_LOWER_RIGHT_Y - GRAPH_UPPER_LEFT_Y) / 10;
        for (int i = 0; i < 10; i++)
        {
            graphics.drawLine( GRAPH_UPPER_LEFT_X - 5, 
                               GRAPH_UPPER_LEFT_Y + (i * tickSeparation),
                               GRAPH_UPPER_LEFT_X,      
                               GRAPH_UPPER_LEFT_Y + (i * tickSeparation) );
        }

        // put tick marks on the left axis -- ten of 'em each just for estimating
        tickSeparation = (GRAPH_LOWER_RIGHT_X - GRAPH_UPPER_LEFT_X) / 10;
        for (int i = 0; i < 10; i++)
        {
            graphics.drawLine( GRAPH_LOWER_RIGHT_X - (i * tickSeparation),
                               GRAPH_LOWER_RIGHT_Y,
                               GRAPH_LOWER_RIGHT_X - (i * tickSeparation),
                               GRAPH_LOWER_RIGHT_Y + 5 );
        }

    } // end drawAxes()
    
    private void drawLegend(Graphics2D graphics)
    {
        graphics.setPaint(Color.black);
        
        int legendWidth = LEGEND_LOWER_RIGHT_X - LEGEND_UPPER_LEFT_X;
        int legendHeight = LEGEND_LOWER_RIGHT_Y - LEGEND_UPPER_LEFT_Y;
        
        int textLeftMargin = LEGEND_UPPER_LEFT_X + 10;
        int indicatorLeftMargin = textLeftMargin + 75;
        int indicatorLength = 35;
        int lineHeight = 18;
        
        // Draw the box
        graphics.drawLine(LEGEND_UPPER_LEFT_X, LEGEND_UPPER_LEFT_Y, 
                          LEGEND_LOWER_RIGHT_X, LEGEND_UPPER_LEFT_Y);
        graphics.drawLine(LEGEND_LOWER_RIGHT_X, LEGEND_UPPER_LEFT_Y,
                          LEGEND_LOWER_RIGHT_X, LEGEND_LOWER_RIGHT_Y);
        graphics.drawLine(LEGEND_LOWER_RIGHT_X, LEGEND_LOWER_RIGHT_Y, 
                          LEGEND_UPPER_LEFT_X, LEGEND_LOWER_RIGHT_Y);
        graphics.drawLine(LEGEND_UPPER_LEFT_X, LEGEND_LOWER_RIGHT_Y, 
                          LEGEND_UPPER_LEFT_X, LEGEND_UPPER_LEFT_Y);
        
        //String label = "Infected";
        //Rectangle2D rect  = font.getStringBounds(label, graphics);

        graphics.drawString("Infected", 
                            textLeftMargin, LEGEND_UPPER_LEFT_Y + lineHeight);
        
        graphics.drawString("Uninfected", 
                            textLeftMargin, LEGEND_UPPER_LEFT_Y + lineHeight * 2);
        
        graphics.drawString("Infested", 
                            textLeftMargin, LEGEND_UPPER_LEFT_Y + lineHeight * 3);
        
        graphics.drawString("Uninfested", 
                            textLeftMargin, LEGEND_UPPER_LEFT_Y + lineHeight * 4);
        
        graphics.drawString("Treated", 
                            textLeftMargin, LEGEND_UPPER_LEFT_Y + lineHeight * 5);
        
        int lineX = indicatorLeftMargin;
        int lineY = LEGEND_UPPER_LEFT_Y + lineHeight - 4;
        
        graphics.setPaint(Color.red);
        graphics.drawLine(lineX, lineY, lineX + indicatorLength, lineY);
        
        lineY += lineHeight;
        graphics.setPaint(Color.green);
        graphics.drawLine(lineX, lineY, lineX + indicatorLength, lineY);
        
        lineY += lineHeight;
        graphics.setPaint(Color.blue);
        graphics.drawLine(lineX, lineY, lineX + indicatorLength, lineY);
        
        lineY += lineHeight;
        graphics.setPaint(Color.magenta);
        graphics.drawLine(lineX, lineY, lineX + indicatorLength, lineY);
        
        lineY += lineHeight;
        graphics.setPaint(Color.black);
        graphics.drawLine(lineX, lineY, lineX + indicatorLength, lineY);
        
    }

} // end class TimeSeriesDialog
