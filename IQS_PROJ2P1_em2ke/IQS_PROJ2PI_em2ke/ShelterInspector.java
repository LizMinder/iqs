
import java.awt.BorderLayout;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import squint.GUIManager;

/**
 *
 * @author lbarnett
 */
public class ShelterInspector extends GUIManager {
    // constant variable declarations
    private final int WIDTH  = 250;
    private final int HEIGHT = 300;

    // instance variables

    // used to keep track of inspector windows already open
    protected static HashMap<String, ShelterInspector> windowList =
        new HashMap<String, ShelterInspector>();

    private String windowID; // to uniquely identify windows within hash map
    private double time;

  
    public ShelterInspector( Shelter s, double time, String id )
    {
         this.createWindow(WIDTH, HEIGHT, "Shelter Inspector");
        contentPane.setLayout(new BorderLayout());

        windowID = id;
        this.time = time;

        // dump the info about the agetn into a string 
        String info = new String("Shelter: (" + s.getCol() + ", " + 
                                    s.getRow() + ")\n\n");
        info += "Time:\t" + time + "\n\n";
        info += "Shelter type:\t";
        
        if ( s.isFormalShelter() ){
            info += "formal\n";
        } else {
            info += "informal\n";
        }

        info += "Laundry:\t";
        if ( s.isLaundryAvailable() ){
            info += "yes\n";
        } else {
            info += "no\n";
        }

        info += "Showers:\t";
        if ( s.isShowersAvailable() ){
            info += "yes\n";
        } else {
            info += "no\n";
        }
        
        info += "Medical:\t";
        if ( s.isLaundryAvailable() ){
            info += "yes\n";
        } else {
            info += "no\n";
        }
        
        info += "Capacity:\t" + s.getMaxCapacity() + 
                "\nOccupancy:\t" + s.getCurrentOccupancy() + "\n";
        info += "Infested:\t" + s.getInfestedCount() +
                "\nInfected:\t" + s.getInfectedCount() + "\n";
        
        // create a read-only text area using the string, and place
        // in the center of the window
        JTextArea area = new JTextArea(info);
        area.setEditable(false);  // user cannot write to area
        contentPane.add(new JScrollPane(area), BorderLayout.CENTER);
        
        JPanel bottomPanel = new JPanel();
        
        JLabel occupantLabel = new JLabel( "Occupants" );
        bottomPanel.add ( occupantLabel );
        
        // Load up a combobox with the hosts for this shelter
        
        JComboBox<Host> hostComboBox = new JComboBox<Host>();
        
        for ( Host h : s.getCurrentOccupants() ){
            hostComboBox.addItem( h );
        }

        bottomPanel.add( hostComboBox );
        // add a button to close the window
        JButton button = new JButton("Close");
        bottomPanel.add ( button );
        add(bottomPanel, BorderLayout.SOUTH);

    }

    public void buttonClicked()
    {
        // dismiss the window and remove from the window list
        this.close();
        windowList.remove(this.windowID);

    } // end of buttonClicked()
    
    public void menuItemSelected( JComboBox menu ){
        Host h = (Host) menu.getSelectedItem();
        
        if (h != null)
        {
            // use hash map to see if the window is already created, and if so,
            // just focus; otherwise, create a new inspector window
            String id = h.getId();
            AgentInspector window = AgentInspector.windowList.get(id);
            if (window == null)
            {
                window = new HostInspector(h, time, id);
                AgentInspector.windowList.put(id, window);
            }
            window.setVisible(true);
        }
        
    }
}
