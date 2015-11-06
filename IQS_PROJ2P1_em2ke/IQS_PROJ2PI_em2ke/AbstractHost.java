
import java.util.ArrayList;

/**
 * Abstract class representing the host in the disease system.
 * 
 * @author lbarnett
 * @version 10/2/2013
 */
public abstract class AbstractHost extends AbstractAgent {
    // instance variables
    
    // Positions are in terms of the grid of shelter locations. Possibly
    // not relevant for vectors, only for hosts.
    protected int row; // vertical position of the agent
    protected int column; // horizontal position of the agent
        
    protected double  nextInteractTime;   // Interact with another agent
    private double  timeAntibioticApplied;  // Treatment time
    private double    timeAntibioticClears;   // Time treatment ends
    
    /**
     * Time of last clothes change
     */
    private double timeLastClothesChange;
    
    /**
     * Next time to check to see if infection has occurred
     */
    private double timeNextInfectionCheck;
    
    /**
     * If infected, when did the infection occur
     */
    private double timeInfected;
    
    /**
     * Time laundry will next be done
     */
    private double timeNextLaundry;
    
    /**
     * Time clothes will next be changed
     */
    private double timeNextClothesChange;
    
    /**
     * Time of next shower
     */
    private double timeNextShower;

    public AbstractHost(String id, boolean isInfected) {
        super( id, isInfected );
        
        this.row = 0;
        this.column = 0;

        // use a stationary Poisson arrival process to drive the movement
        // times, i.e., interarrival times are drawn from an Exponential
        // distribution with mean mu
        nextMoveTime = nextInteractTime = 0.0;  // initialize
        nextMoveTime += getNextInterMoveTime();
        nextInteractTime += generateNextInterInteractTime();  // funny, huh?

        timeAntibioticApplied = Double.MAX_VALUE;
        timeAntibioticClears = Double.MAX_VALUE;

        if ( isInfected ) {
            timeInfected = -SimulationManager.Uniform(
                Parameters.getDaysSinceInfectedMin(), 
                Parameters.getDaysSinceInfectedMax());
            //timeNextInfectionCheck = Double.POSITIVE_INFINITY;
        } else {
            // Not currently infected. Set up daily checks
            timeInfected = Double.POSITIVE_INFINITY;
        }
        
        timeNextInfectionCheck = 1.0;
        
        timeLastClothesChange = - (int) Math.round( 
                                SimulationManager.Uniform( 0.0, 7.0 ) );
        // If this Host ends up not having a change of clothes, this will
        // just be ignored.
        timeNextClothesChange = 7.0 + timeLastClothesChange;

        timeNextLaundry = (int) Math.round( 
                                SimulationManager.Uniform( 0.0, 7.0 ) );
        timeNextShower = (int) Math.round( 
                                SimulationManager.Uniform( 0.0, 7.0 ) );

        this.setNextEvent();  
    }
    
    /**
     * Change to the Host's alternate set of clothes. This method handles 
     * pieces of the process that the simulation engine is concerned with,
     * the calls an abstract method that will be implemented in the
     * concrete Host class for the pieces it needs to know about.
     * 
     * Note that this one couldn't be called changeClothes() like the
     * abstract method, because that method needs to know how long the
     * clothes were unworn, which gave it the same signature as this
     * method.
     * 
     * @param time Current simulation time.
     */
    public void changeTheClothes( double time )
    {
        double daysClothesStored = time - timeLastClothesChange;
        changeClothes( daysClothesStored );
        timeLastClothesChange = time;
        // Assumes clothes are changed once a week
        this.setTimeNextClothesChange( time + 7.0 );
    }
    
    //======================================================================
    //* The subclass will have a similarly named method, but without the
    //* time parameter.  This allows the student to implement a
    //* method but not have to worry about event time handling.
    //======================================================================
    public void shower( double time )
    {
        shower();
        
        // Assume showers once per week if possible
        this.setTimeNextShower(time + 7.0);
    }

    //======================================================================
    //* The subclass will have a similarly named method, but without the
    //* time parameter.  This allows the student to implement a
    //* method but not have to worry about event time handling.
    //======================================================================
    public void doLaundry( double time )
    {
        doLaundry();
        this.setTimeNextLaundry(time + 7.0 );
    }
    
    //======================================================================
    //* The subclass will have a similarly named method, but without the
    //* time parameter.  This allows the student to implement a
    //* method but not have to worry about event time handling.
    //======================================================================
    public void clearAntibiotic(double time) {
        clearAntibiotic();
        setTimeAntibioticClears(Double.MAX_VALUE); // Double.MAX_VALUE -- see above
    }

    //======================================================================
    //* The subclass will have a similarly named method, but without the
    //* time parameter.  This allows the student to implement a
    //* method but not have to worry about event time handling.
    //======================================================================
    public boolean treatAgent(double treatmentTime ) {
        setTimeAntibioticApplied(Double.MAX_VALUE); // clear this event time
        boolean treated = treatAgent();           // in subclass
        if (treated) {
            //timeOfTreatment = treatmentTime; // for computing AB's effectiveness
            setTimeOfTreatment(this.nextEventTime);
            setTimeAntibioticClears(treatmentTime + generateAntibioticClearTime());
            return true;
        }
        return false;
    }
        
    protected double generateNextInterInteractTime() // funny name, no?
    {
        return SimulationManager.Exponential(Parameters.getAvgTimeBtwnInteract());
    }


    protected double getNextInterMoveTime() {
        return SimulationManager.Exponential(Parameters.getAvgTimeBtwnHostMove());
    }

    protected double generateAntibioticClearTime() {
        return SimulationManager.Uniform(Parameters.getTreatmentLengthMin(),
                                         Parameters.getTreatmentLengthMax());
    }

    //======================================================================
    //* public void setTimeOfTreatment(double time)
    //======================================================================
    public void setTimeOfTreatment(double theTime) {
        setTimeAntibioticApplied(theTime);
    }

    //======================================================================
    //* public void setNextInteractTime()
    //======================================================================
    public void setNextInteractTime() {
        nextInteractTime += generateNextInterInteractTime();
    }
    
    public void setNextInteractTime(Double t) {
        nextInteractTime = t;
    }
    
    public double getNextInteractTime(){
        return nextInteractTime;
    }

    /**
     * @return the timeAntibioticClears
     */
    public double getTimeAntibioticClears() {
        return timeAntibioticClears;
    }

    /**
     * @param timeAntibioticClears the timeAntibioticClears to set
     */
    public void setTimeAntibioticClears(double timeAntibioticClears) {
        this.timeAntibioticClears = timeAntibioticClears;
    }
    /**
     * Get the row of the location of the shelter this host is currently using.
     * 
     * @return The row of the shelter in the grid.
     */
    public int getRow(){ return row; }

    /**
     * Get the column of the location of the shelter this host is currently 
     * using.
     * 
     * @return The column of the shelter in the grid.
     */
    public int getCol(){ return column; }
    
    /**
     * Change the position of this Host. This essentially means moving the
     * host to a new shelter.
     * 
     * @param theRow    Row of the shelter coordinates
     * @param theCol    Column of the shelter coordinates
     */
    public void setRowCol(int theRow, int theCol)
    {
        row = theRow;
        column = theCol;
    }
    
    /**
     * @return the timeNextLaundry
     */
    public double getTimeNextLaundry() {
        return timeNextLaundry;
    }

    /**
     * @param timeNextLaundry the timeNextLaundry to set
     */
    public void setTimeNextLaundry(double timeNextLaundry) {
        this.timeNextLaundry = timeNextLaundry;
    }

    /**
     * @return the timeNextClothesChange
     */
    public double getTimeNextClothesChange() {
        return timeNextClothesChange;
    }

    /**
     * @param timeNextClothesChange the timeNextClothesChange to set
     */
    public void setTimeNextClothesChange(double timeNextClothesChange) {
        this.timeNextClothesChange = timeNextClothesChange;
    }

    /**
     * @return the timeNextInfectionCheck
     */
    public double getTimeNextInfectionCheck() {
        return timeNextInfectionCheck;
    }

    /**
     * @param timeNextInfectionCheck the timeNextInfectionCheck to set
     */
    public void setTimeNextInfectionCheck(double timeNextInfectionCheck) {
        this.timeNextInfectionCheck = timeNextInfectionCheck;
    }

    /**
     * @return the timeInfected
     */
    public double getTimeInfected() {
        return timeInfected;
    }

    /**
     * @param timeInfected the timeInfected to set
     */
    public void setTimeInfected(double timeInfected) {
        this.timeInfected = timeInfected;
    }
    
    /**
     * @return the timeNextShower
     */
    public double getTimeNextShower() {
        return timeNextShower;
    }

    /**
     * @param timeNextShower the timeNextShower to set
     */
    public void setTimeNextShower(double timeNextShower) {
        this.timeNextShower = timeNextShower;
    }

    /**
     * @return the timeAntibioticApplied
     */
    public double getTimeAntibioticApplied() {
        return timeAntibioticApplied;
    }

    /**
     * @param timeAntibioticApplied the timeAntibioticApplied to set
     */
    public void setTimeAntibioticApplied(double timeAntibioticApplied) {
        this.timeAntibioticApplied = timeAntibioticApplied;
    }

    public double getCurrentEventTime()
    {
        return this.nextEventTime;
    }
    
    /**
     * Prepare the Host for execution of a single, selected event. Useful
     * for debugging. Sets event times for all events to MAX_VALUE, so 
     * that the one event we subsequently add a time for will be the next
     * event executed for this host.
     */
    public void setForSingleStep(){
        // We are about to set the time for some particular event that
        // we want the simulation to execute next. Make all "next event"
        // times as big as possible so that the one we choose will be 
        // the next event processed for this host.
        timeNextClothesChange = Double.MAX_VALUE;
        timeNextInfectionCheck = Double.MAX_VALUE;
        timeNextLaundry = Double.MAX_VALUE;
        timeNextShower = Double.MAX_VALUE;
        timeAntibioticApplied = Double.MAX_VALUE;
        timeAntibioticClears = Double.MAX_VALUE;
        nextMoveTime = Double.MAX_VALUE;
        nextInteractTime = Double.MAX_VALUE;
        nextEventTime = Double.MAX_VALUE;
        nextEventType = SimulationManager.NO_EVENT;
    }
    
    /**
     * Called after actions on the Host that modify event times. 
     * This sets the value of the nextEventTime to the smallest
     * of the event times for this Host, then sets nextEventType
     * according to the type of that event.
     */
    public void setNextEvent() {
        // we'll presume death is the highest priority event; this way, if
        // an agent is busy doing some event and turns out the agent should
        // die right now, the "some event" won't take precedence
        nextEventTime = timeOfDeath;
        nextEventType = SimulationManager.HOST_DEATH_EVENT;

        if (nextMoveTime < nextEventTime) {
            nextEventTime = nextMoveTime;
            nextEventType = SimulationManager.HOST_MOVE_EVENT;
        }
        if (nextInteractTime < nextEventTime) {
            nextEventTime = nextInteractTime;
            nextEventType = SimulationManager.HOST_INTERACT_EVENT;
        }
        if (getTimeAntibioticApplied() < nextEventTime) {
            nextEventTime = getTimeAntibioticApplied();
            nextEventType = SimulationManager.HOST_ANTIBIOTIC_APP_EVENT;
        }
        if (getTimeAntibioticClears() < nextEventTime) {
            nextEventTime = getTimeAntibioticClears();
            nextEventType = SimulationManager.HOST_ANTIBIOTIC_CLEAR_EVENT;
        }
        if (getTimeNextShower() < nextEventTime) {
            nextEventTime = getTimeNextShower();
            nextEventType = SimulationManager.HOST_SHOWER_EVENT;
        }
        if (getTimeNextClothesChange() < nextEventTime) {
            nextEventTime = getTimeNextClothesChange();
            nextEventType = SimulationManager.HOST_CLOTHES_CHANGE_EVENT;
        }
        if (getTimeNextInfectionCheck() < nextEventTime ){
            nextEventTime = getTimeNextInfectionCheck();
            nextEventType = SimulationManager.HOST_CHECK_INFECTION_EVENT;
        }
        if (getTimeNextLaundry() < nextEventTime ){
            nextEventTime = getTimeNextLaundry();
            nextEventType = SimulationManager.HOST_LAUNDRY_EVENT;
        }
    }
    
    // Methods to be implemented in the concrete derived class.
    public abstract boolean treatAgent();
    public abstract boolean getTreated();
    /**
     * Change value of treated 
     * 
     * @param value new value for treated.
     */
    public abstract void setTreated( boolean value );
    public abstract void clearAntibiotic();
    public abstract void changeClothes(double daysClothesStored );
    public abstract void shower();
    public abstract void doLaundry();
    public abstract void delouse();
    public abstract int getChangesOfClothes();
    public abstract void setChangesOfClothes(int numChanges);
    public abstract int getCurrentClothesSet();
    public abstract void setCurrentClothesSet(int clothesSet);
    public abstract void addVector( Louse v );
    public abstract boolean removeVector( Louse v );
    public abstract ArrayList<Louse> getVectorList();
    public abstract boolean getInfested();
    public abstract void setInfested( boolean value );
    public abstract int getInfestationSize();
    public abstract int getTotalVectors();
    public abstract Louse getRandomVector();
    public abstract void vectorHatched();
}
