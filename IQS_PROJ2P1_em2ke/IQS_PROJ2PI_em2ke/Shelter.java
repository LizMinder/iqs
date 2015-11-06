
import java.util.ArrayList;

/**
 * Class describing an available shelter.
 * 
 * @author lbarnett
 */
public class Shelter {
    public static final boolean DEBUG = true;
    
    private int     column;
    private int     row;
    
    /**
     * Formal shelter is a shelter run by an organization. Informal is 
     * just a place where people sleep.
     */
    private boolean isFormalShelter;
    
    /**
     * Does the shelter have showers. Formal might, informal wouldn't.
     */
    private boolean showersAvailable;
    
    /**
     * Does the shelter have laundry facilities. Formal might, informal
     * wouldn't.
     */
    private boolean laundryAvailable;
    
    /**
     * Is medical care available at this location.
     */
    private boolean medicalCareAvailable;
    
    /**
     * Is routine intake delousing performed at this shelter?
     */
    private boolean delousingAvailable;
    
    /**
     * How many people will this shelter hold.
     */
    private int        maxCapacity;
    
    private int        infectedCount;
    private int        infestedCount;
    
    // Debugging
    private double     timeLastAdd;
    private double     timeLastRemove;
    
    private Host       lastAdded;
    private Host       lastRemoved;
    
    /**
     * People currently using this shelter.
     */
    private ArrayList<Host> currentOccupants;
    
    public Shelter( boolean formal, boolean showers, boolean laundry, 
                    boolean medical, boolean delousing, int capacity, 
                    int theCol, int theRow )
    {
        column = theCol;
        row = theRow;
        isFormalShelter = formal;
        showersAvailable = showers;
        laundryAvailable = laundry;
        medicalCareAvailable = medical;
        delousingAvailable = delousing;
        maxCapacity = capacity;
        currentOccupants = new ArrayList<Host>( maxCapacity );
        infectedCount = 0;
        infestedCount = 0;
        timeLastAdd = 0;
        timeLastRemove = 0;
        lastAdded = null;
        lastRemoved = null;
    }
    
    public void reset()
    {
        currentOccupants.clear();
        infectedCount = 0;
        infestedCount = 0;
        timeLastAdd = 0;
        timeLastRemove = 0;
        lastAdded = null;
        lastRemoved = null;
    }
    
    
    //*******************************************************************
    // Accessor methods
    //*******************************************************************
        
    /**
     * Is this shelter at capacity?
     * 
     * @return true if shelter is at capacity, false if there is space
     */
    public boolean isFull(){
        return ( currentOccupants.size() == maxCapacity );
    }
    
    /**
     * @return the isFormalShelter
     */
    public boolean isFormalShelter() {
        return isFormalShelter;
    }

    /**
     * @return the showersAvailable
     */
    public boolean isShowersAvailable() {
        return showersAvailable;
    }

    /**
     * @return the laundryAvailable
     */
    public boolean isLaundryAvailable() {
        return laundryAvailable;
    }

    /**
     * @return the medicalCareAvailable
     */
    public boolean isMedicalCareAvailable() {
        return medicalCareAvailable;
    }

    /**
     * Does this shelter provide routine intake delousing?
     * 
     * @return the delousingAvailable
     */
    public boolean isDelousingAvailable() {
        return delousingAvailable;
    }

    /**
     * @return the maxCapacity
     */
    public int getMaxCapacity() {
        return maxCapacity;
    }
    
    /**
     * Get the number of Host instances currently in this shelter location.
     * 
     * @return the current occupancy
     */
    public int getCurrentOccupancy() {
        return currentOccupants.size();
    }
    
    /**
     * Return the list of current occupants of this shelter.
     * Not a real great idea; better if this just returned an iterator.
     * 
     * @return The list of occupants.
     */
    public ArrayList<Host> getCurrentOccupants(){
        return currentOccupants;
    }
    
    /**
     * Get a reference to one of the occupants of the shelter.
     * 
     * @param idx index of the occupant you want
     * 
     * @return The requested Host. Returns null if idx is out of bounds.
     */
    public Host getOccupant( int idx )
    {
        if ( idx >= 0 && idx < currentOccupants.size() ) {
            return currentOccupants.get( idx );
        } else {
            return null;
        }
    }
    
    /**
     * Get a count of the number of occupants who are infested with lice
     * 
     * @return How many occupants are infested with lice.
     */
    public int getInfestedCount()
    {
        return infestedCount;
    }
    
    public int getInfectedCount()
    {
        return infectedCount;
    }
    
    public int getCol()
    {
        return column;
    }
    
    public int getRow()
    {
        return row;
    }

    //*******************************************************************
    // Mutator methods
    //*******************************************************************
    
    /**
     * @param maxCapacity the maxCapacity to set
     */
    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
    
    /**
     * @param isFormalShelter the isFormalShelter to set
     */
    public void setIsFormalShelter(boolean isFormalShelter) {
        this.isFormalShelter = isFormalShelter;
    }

    /**
     * @param showersAvailable the showersAvailable to set
     */
    public void setShowersAvailable(boolean showersAvailable) {
        this.showersAvailable = showersAvailable;
    }

    /**
     * @param laundryAvailable the laundryAvailable to set
     */
    public void setLaundryAvailable(boolean laundryAvailable) {
        this.laundryAvailable = laundryAvailable;
    }

    /**
     * @param medicalCareAvailable the medicalCareAvailable to set
     */
    public void setMedicalCareAvailable(boolean medicalCareAvailable) {
        this.medicalCareAvailable = medicalCareAvailable;
    }

    /**
     * Change state of whether this shelter provides delousing
     * 
     * @param delousingAvailable the delousingAvailable to set
     */
    public void setDelousingAvailable(boolean delousingAvailable) {
        this.delousingAvailable = delousingAvailable;
    }

    /**
     * Add a new Host instance to this shelter location if there is room.
     * 
     * @param h the Host instance to add to the location
     * 
     * @return true if Host h was successfully added, false otherwise.
     */
    public boolean add( Host h ) {
        if ( currentOccupants.size() < maxCapacity ) {
            currentOccupants.add( h );
            // Give the host a reference to this Shelter.
            h.setCurrentShelter(this);
            
            if (this.isDelousingAvailable() ){
                h.delouse();
            }
            
            if ( h.getInfected() ) infectedCount++;
            if ( h.getInfested() ) infestedCount++;
            
            if ( DEBUG ){
                lastAdded = h;
                timeLastAdd = h.getTimeOfLastEvent();
            }
            
            // Check to see if shelter has medical, and if so, check
            // to see if the new Host needs medical treatment
            /* This is done in SimulationManager
            if ( this.isMedicalCareAvailable() && 
                    h.isInfected() &&
                    !h.getTreated()){
                h.treatAgent(); // Sets time of treatment
                h.setNextEvent();
            }
            */
            
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Remove a Host instance from this shelter.
     * 
     * @param h The Host instance to remove
     * 
     * @return true if the requested Host instance is found, false, otherwise.
     */
    public boolean remove( Host h ){
        if ( currentOccupants.contains(h)){
            h.setCurrentShelter( null );
            
            if ( h.getInfected() ) infectedCount--;
            if ( h.getInfested() ) infestedCount--;
            
            if ( DEBUG ){
                lastRemoved = h;
                timeLastRemove = h.getTimeOfLastEvent();
            }
            
            return currentOccupants.remove( h );
        } else {
            return false;
        }
    }
    
    public void incrementInfected()
    {
        if ( infectedCount < currentOccupants.size())
            infectedCount++;
    }
    
    public void decrementInfected()
    {
        if ( infectedCount > 0 )
            infectedCount--;
    }
    
    public void incrementInfested()
    {
        if ( infestedCount < currentOccupants.size())
            infestedCount++;
    }
    
    public void decrementInfested()
    {
        if ( infestedCount > 0 )
            infestedCount--;
    }
    
    public boolean checkConsistency(){
        int actualInfected = 0;
        int actualInfested = 0;
        boolean consistent = true;
        
        for ( Host h: currentOccupants ){
//            h.checkConsistency();
            
            if ( h.getInfected())
                actualInfected++;
            if ( h.getInfested() )
                actualInfested++;
        }
        
        if ( actualInfected != infectedCount || 
                actualInfested != infestedCount ) {
            consistent = false;
        }
        
        return consistent;
    }

}
