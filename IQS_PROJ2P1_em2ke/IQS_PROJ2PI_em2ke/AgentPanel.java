
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
/**
 * Class to display fields of the AbstractAgent class
 * @author lbarnett
 */
public class AgentPanel extends JPanel {
    public static final int AP_DEF_FIELD_WIDTH = 15;
    protected AbstractAgent agent;
    
    protected GridBagLayout gbl;
    protected GridBagConstraints gbc;
    protected JTextField idField;
    protected JTextField lastEventTmField;
    protected JTextField lastEventTypeField;
    protected JTextField infectedField;
    protected Font labelFont;
    protected Font fieldFont;
    
    public AgentPanel()
    {
        this(null);
    }
    
    public AgentPanel(AbstractAgent a)
    {
        agent = a;
        
        gbl = new GridBagLayout();
        setLayout( gbl );
        gbc = new GridBagConstraints();
        gbc.weightx = 100;
        gbc.weighty = 100;
        // Insets(top, left, bottom, right)
        gbc.insets = new Insets(-2, 2, -2, 2);

        gbc.anchor = GridBagConstraints.WEST;
        int gbRow = 0;
        int gbCol = 0;
        
        labelFont = new Font("SanSerif", Font.BOLD, 10);
        fieldFont = new Font("Monospaced", Font.PLAIN, 10);
        
        JLabel l = new JLabel("ID:");
        l.setFont(labelFont);
        addToGB( this, l, gbl, gbc, gbCol, gbRow, 1, 1 );
        gbCol++;
        
        idField = new JTextField(AP_DEF_FIELD_WIDTH);
        idField.setEditable(false);
        idField.setFont(fieldFont);
        addToGB( this, idField, gbl, gbc, gbCol, gbRow, 1, 1 );
        gbRow++;
        gbCol = 0;
        
        l = new JLabel("Last Event Type:");
        l.setFont(labelFont);
        addToGB( this, l, gbl, gbc, gbCol, gbRow, 1, 1 );
        gbCol++;
        
        lastEventTypeField = new JTextField(AP_DEF_FIELD_WIDTH);
        lastEventTypeField.setEditable(false);
        lastEventTypeField.setFont(fieldFont);
        addToGB( this, lastEventTypeField, gbl, gbc, gbCol, gbRow, 1, 1 );
        gbRow++;
        gbCol = 0;
        
        l = new JLabel("Last Event Time:");
        l.setFont(labelFont);
        addToGB( this, l, gbl, gbc, gbCol, gbRow, 1, 1 );
        gbCol++;
        
        lastEventTmField = new JTextField(AP_DEF_FIELD_WIDTH);
        lastEventTmField.setEditable(false);
        lastEventTmField.setFont(fieldFont);
        addToGB( this, lastEventTmField, gbl, gbc, gbCol, gbRow, 1, 1 );
        gbRow++;
        gbCol = 0;
        
        l = new JLabel("Infected:");
        l.setFont(labelFont);
        addToGB( this, l, gbl, gbc, gbCol, gbRow, 1, 1 );
        gbCol++;
        
        infectedField = new JTextField(AP_DEF_FIELD_WIDTH);
        infectedField.setEditable(false);
        infectedField.setFont(fieldFont);
        addToGB( this, infectedField, gbl, gbc, gbCol, gbRow, 1, 1 );
        gbRow++;
        gbCol = 0;
        
        // Fill in text fields
        update();
    }
    
    public void update()
    {
        if (getAgent() == null) return;
        
        idField.setText(getAgent().getId());
        lastEventTmField.setText("" + getAgent().getTimeOfLastEvent());
        lastEventTypeField.setText(SimulationManager.getEventName(getAgent().getTypeOfLastEvent()));
        infectedField.setText("" + getAgent().getInfected());
        
    }
    
    /*
     * A helper method for adding things to a GridBagLayout.
     */
    protected static void addToGB(Container cont, Component c, GridBagLayout gbl,
            GridBagConstraints gbc, int x, int y, int w, int h) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = w;
        gbc.gridheight = h;
        gbl.setConstraints(c, gbc);
        cont.add(c);
    }

    /**
     * Get a reference to the agent that is displayed in this panel.
     * 
     * @return the agent
     */
    public AbstractAgent getAgent() {
        return agent;
    }

    /**
     * Change the agent that is displayed in this panel.
     * 
     * @param agent the agent to set
     */
    public void setAgent(AbstractAgent agent) {
        this.agent = agent;
    }
    
}
