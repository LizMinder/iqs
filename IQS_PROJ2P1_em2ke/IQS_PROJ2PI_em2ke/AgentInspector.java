import squint.*;       // for GUIManager
import javax.swing.*;  // for JWhatevers
import java.awt.*;     // for BorderLayout
import java.util.*;    // for HashMap

//********************************************************
//* Class to pop up a dialog with info about an agent.
//* Called from within mouseClicked() inside AgentCanvas
//* whenever user clicks on an agent.
//********************************************************
public class AgentInspector extends GUIManager
{
    // constant variable declarations
    private final int WIDTH  = 350;
    private final int HEIGHT = 350;

    // instance variables

    // used to keep track of inspector windows already open
    protected static HashMap<String, AgentInspector> windowList =
        new HashMap<String, AgentInspector>();

    protected String    windowID; // to uniquely identify windows within hash map
    protected JPanel    centerPanel;
    protected JTextArea infoArea;
    protected double time;

  
    //======================================================================
    //* public AgentInspector(Agent agent, double time, String id)
    //* Constructor for the class.  Pops up a new window with info about
    //* the agent and current time.
    //======================================================================
    public AgentInspector(AbstractAgent agent, double time, String id, 
                          String windowTitle)
    {
        this.createWindow(WIDTH, HEIGHT, windowTitle);
        setLayout(new BorderLayout());

        windowID = id;
        this.time = time;

        // dump the info about the agent into a string 
        String description = new String("Id:\t" + agent.getId() + "\n\n");
        description += "Sim Time:\t" + time + "\n\n";
        description += "Last Event Time:\t" + agent.getTimeOfLastEvent() + "\n";
        description += "Type:\t\t" + 
                SimulationManager.getEventName(agent.getTypeOfLastEvent()) +
                "\n";
        description += "Next Event Time:\t" + agent.getNextEventTime() + "\n";
        description += "Type:\t\t" + 
                SimulationManager.getEventName(agent.getNextEventType()) +
                "\n";
        
        centerPanel = new JPanel();
        centerPanel.setLayout( new GridLayout( 0, 1 ) );
        
        // create a read-only text area using the string, and place
        // in the center of the window
        infoArea = new JTextArea( description );
        infoArea.setEditable(false);  // user cannot write to area
        
        centerPanel.add( infoArea );
        
        
        add(new JScrollPane( centerPanel ), BorderLayout.CENTER);

        // add a button to close the window
        JButton button = new JButton("Close");
        contentPane.add(button, BorderLayout.SOUTH);

    } // end of AgentInspector()

    //======================================================================
    //* public void buttonClicked()
    //* Called whenever a button (the close button, in this case) is
    //* clicked.
    //======================================================================
    public void buttonClicked()
    {
        // dismiss the window and remove from the window list
        this.close();
        windowList.remove(this.windowID);

    } // end of buttonClicked()

} // end of class AgentInspector
