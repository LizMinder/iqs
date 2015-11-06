/**
 * Grid of shelters about which the Agents move. This is the "model" for
 * the AgentCanvas class, and is used by SimulationManager.
 * 
 * @author lbarnett
 */
public class ShelterGrid {

    private Shelter [][] shelterGrid;  // Grid of shelter locations

    private int gridWidth;  // width of grid in cells
    private int gridHeight; // height of grid in cells
    
    public ShelterGrid()
    {
        // Create shelter grid
        gridWidth = Parameters.getDefaultGridSize();
        gridHeight = gridWidth; // We're square.
        shelterGrid = new Shelter[gridWidth][gridHeight];
        
        // Create the shelters
        for ( int col = 0; col < gridWidth; col++ ){
            for ( int row = 0; row < gridHeight; row++ ){
                boolean formalShelter =
                        SimulationManager.probabilityMet(
                            Parameters.getPercentFormalShelters() );
                boolean showersAvailable = false;
                boolean laundryAvailable = false;
                boolean medicalAvailable = false;
                boolean delousingAvailable = false;
                
                int capacity = 0;
                if ( formalShelter ) {
                    showersAvailable = 
                            SimulationManager.probabilityMet(
                                Parameters.getPercentageShowersAvailable() );
                    laundryAvailable = 
                            SimulationManager.probabilityMet(
                                Parameters.getPercentageLaundryAvailable() );
                    medicalAvailable = 
                            SimulationManager.probabilityMet(
                                Parameters.getPercentageMedicalAvailable() );
                    delousingAvailable =
                            SimulationManager.probabilityMet(
                                Parameters.getPercentageIntakeDelousing() );
                    capacity = 
                        (int) Math.round(
                            SimulationManager.Uniform(
                                Parameters.getAvgShelterCapFormalMin(), 
                                Parameters.getAvgShelterCapFormalMax() ) );
                            
                } else {
                    capacity = 
                        (int) Math.round(
                            SimulationManager.Uniform(
                                Parameters.getAvgShelterCapInformalMin(), 
                                Parameters.getAvgShelterCapInformalMax() ) );
                    
                }
                        
                shelterGrid[col][row] = 
                        new Shelter( formalShelter,
                                     showersAvailable,
                                     laundryAvailable,
                                     medicalAvailable,
                                     delousingAvailable,
                                     capacity, col, row );
            }
        }

    }
    
    /**
     * Clear out all of the people in the shelters.
     */
    public void reset()
    {
        for ( int col = 0; col < gridWidth; col++ ){
            for ( int row = 0; row < gridHeight; row++ ){
                shelterGrid[col][row].reset();
            }
        }
    }
    
    public int getGridWidth()  { return gridWidth; }
    public int getGridHeight() { return gridHeight; }
    
    /**
     * Get a reference to a Shelter from it's position on the grid of shelters.
     * 
     * @param col   Requested column
     * @param row   Requested row
     * 
     * @return  The reference to the requested shelter.
     */
    public Shelter getShelterAt( int col, int row ) {
        return shelterGrid[col][row];
    }

    public int getInfectedCount()
    {
        int count = 0;
        
        for ( int col = 0; col < this.getGridWidth(); col++ ){
            for ( int row = 0; row < this.getGridHeight(); row++ ) {
                count += shelterGrid[col][row].getInfectedCount();
            }
        }
        
        return count;
    }

    public int getInfestedCount()
    {
        int count = 0;
        
        for ( int col = 0; col < this.getGridWidth(); col++ ){
            for ( int row = 0; row < this.getGridHeight(); row++ ) {
                count += shelterGrid[col][row].getInfestedCount();
            }
        }
        
        return count;
    }

    public int getTotalOccupancy()
    {
        int count = 0;
        
        for ( int col = 0; col < this.getGridWidth(); col++ ){
            for ( int row = 0; row < this.getGridHeight(); row++ ) {
                count += shelterGrid[col][row].getCurrentOccupancy();
            }
        }
        
        return count;
    }
}
