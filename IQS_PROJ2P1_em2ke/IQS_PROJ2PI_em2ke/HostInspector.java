
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Display information about a Host instance
 * 
 * @author lbarnett
 */
public class HostInspector extends AgentInspector {
    
    public HostInspector ( Host h, double time, String id ) 
    {
        super ( h, time, id, "Host Inspector" );
        
        String description = new String(
                "\nShelter:\t(" + h.getRow() + "," + h.getCol() + ")\n" +
                "\nInfested:\t" + h.getInfested() + 
                "\nInfected:\t" + h.getInfected() + "\n");
        
        description += "Clothes changes:\t" + h.getChangesOfClothes() + "\n";
        
        description += "Infestation size:\t" + h.getInfestationSize();
        
        infoArea.setText( infoArea.getText() + description );
        
        if ( h.getInfested() ){
            // Add a comboBox for InfectionVectors
            JComboBox<Louse> vectorComboBox = new JComboBox<Louse>();
            
            for ( Louse v : h.getVectorList() ){
                vectorComboBox.addItem( v );
            }

            JPanel vectorPanel = new JPanel();
            vectorPanel.add( new JLabel( "Vectors: " ));
            vectorPanel.add( vectorComboBox );
            
            centerPanel.add( vectorPanel );
        }
    }
    
    public void menuItemSelected( JComboBox menu ){
        Louse v = (Louse) menu.getSelectedItem();
        
        if (v != null)
        {
            // use hash map to see if the window is already created, and if so,
            // just focus; otherwise, create a new inspector window
            String id = v.getId();
            AgentInspector window = AgentInspector.windowList.get(id);
            if (window == null)
            {
                window = new LouseInspector(v, time, id);
                LouseInspector.windowList.put(id, window);
            }
            window.setVisible(true);
        }
        
    }
}
