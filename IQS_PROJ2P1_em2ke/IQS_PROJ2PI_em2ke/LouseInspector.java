/**
 * Display information about an InfectionVector instance
 * 
 * @author lbarnett
 */
public class LouseInspector extends AgentInspector {
    public LouseInspector( Louse v, double time, String id)
    {
        super ( v, time, id, "Louse Inspector" );
        
        String description = new String(
                "\nInfected:\t" + v.getInfected() + "\n");
        
        description += "Gender:\t" + v.getGenderString() + "\n";
        
        description += "Life stage:\t" + v.getLifeStageString() + "\n";
        
        //description += "State:\t" + v.getStateString() + "\n";
        
        infoArea.setText( infoArea.getText() + description );

    }
}
