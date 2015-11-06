
import java.util.ArrayList;
import java.util.Random;

/**
 * This class implements a generic agent in our agent-based simulation.
 * The agent can be infected or not. 
 *
 * Essentially the purpose of this class is to hide the simulation event
 * details from the student, while allowing the student to implement an
 * Agent class that handles modeling movement, infection with B. quintana,
 * and transfer of lice between host agents.
 * 
 * @author Barry Lawson and Lewis Barnett
 * @version 10/9/2013
 */
public abstract class AbstractAgent{
    // static (i.e., class)  variables -- common to (shared by) all agents

    // Turn debugging output for this class on/off
    private static final boolean DEBUG = false;
    
    // A local reference to the global RNG object from SimulationManager
    private static Random generator =
            SimulationManager.getRandomNumberGenerator();
    
    private String id;         // name of the agent
    
    /**
     * Current shelter location. Needed for both Hosts and Vectors, as 
     * sometimes Vectors go walkabout and have no current host. We need
     * to know the shelter they're in so we can decide if they "rehost."
     */
    protected Shelter currentShelter;

    /**
     * Agent is infected with Bartonella quintana.
     */
    protected boolean infected;
    
    // event times for a particular agent
    protected double  nextEventTime;      // minimum of times below
    protected int     nextEventType;      // of those listed in Simulation.java
    protected double  nextMoveTime;       // Move to new position
    protected double  timeOfDeath;        // Self explanatory...
    protected double  timeOfInfection;    // used to determine when AB can be applied
    
    // These two are primarily for debugging purposes
    private double    timeOfLastEvent;
    private int       typeOfLastEvent;

    /** 
     * Parent-class constructor for an agent.  Note that the constructor
     * for the extending class _MUST_ call this constructor as its first
     * statement. 
     * 
     * @param id A unique ID string for this agent
     * @param getInfected True if the agent is initally infected, false
     *                      otherwise.
     */
    public AbstractAgent(String id, boolean isInfected) {
        this.infected = isInfected;

        // set the agent's name
        this.id = id;

        timeOfDeath = Double.MAX_VALUE;  // initially, infinity
        timeOfInfection = Double.MAX_VALUE;

        if (isInfected) {
            timeOfInfection = 0;
        }
        
        timeOfLastEvent = 0.0;
        typeOfLastEvent = SimulationManager.NO_EVENT;
    }
    
    /**
     * Check to see if the agent should become infected at this time.
     * How this happens differs between Hosts and Vectors, but both 
     * need to be able to do this. The method calls an abstract method
     * to do the Host/Vector specific checking and handles the aspect
     * of the checks related to the simulation engine's operation.
     * 
     * @param time  Simulation time at which the check occurs.
     * 
     * @return True if agent becomes infected, false otherwise.
     */
    public boolean checkInfection( double time ) {
        // Shouldn't be called if agent is already infected
        //assert( ! infected );
        if ( infected ) return false;
        
        boolean nowInfected = checkInfection( );
        if ( nowInfected ) {
            this.timeOfInfection = time;
        }
        
        return nowInfected;
    }
    
    public abstract boolean checkInfection();
    
    /**
     * Produce a string representation of the agent.
     * 
     * @return The string representation.
     */
    public String toString()
    {
        String description = this.getId();
        /*
        description += "\tnext time: " + nextEventTime + "  type: " +
                SimulationManager.getEventName(nextEventType) + "\n" +
                "\tlast time: " + timeOfLastEvent + "  type: " +
                SimulationManager.getEventName(typeOfLastEvent);
                * */

        return description;

    }

    public double getTimeOfInfection() {
        return timeOfInfection;
    }

    public void setTimeOfDeath(double timeToDie) {
        timeOfDeath = timeToDie;
    }

    public double getTimeOfDeath(){
        return timeOfDeath;
    }
    
    public double getNextEventTime() {
        return nextEventTime;
    }

    public int getNextEventType() {
        return nextEventType;
    }

    public void setNextMoveTime() {
        nextMoveTime += getNextInterMoveTime();
    }
    
    /**
     * Set the time of the next move event directly. Should be used 
     * only for debugging purposes. 
     * 
     * @param t 
     */
    public void setNextMoveTime(double t) {
        nextMoveTime = t;
    }
    
    public double getNextMoveTime(){
        return nextMoveTime;
    }

    public Shelter getCurrentShelter()
    { 
        return currentShelter; 
    }
    
    public void setCurrentShelter( Shelter s )
    {
        currentShelter = s;
    }
    
    /**
     * Returns the infection status of this agent
     * @return the infection status
     */
    public boolean isInfected()
    {
        return infected;
    }

    /**
     * Returns the infection status for this agent
     * 
     * @return True if infected, false otherwise
     */
    public boolean getInfected()
    {
        return infected;
    }
    
    /**
     * Change the infection status of this agent
     * 
     * @param infected the new value for the infection status
     */
    public void setInfected(boolean isInfected) {
        this.infected = isInfected;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the timeOfLastEvent
     */
    public double getTimeOfLastEvent() {
        return timeOfLastEvent;
    }

    /**
     * @param timeOfLastEvent the timeOfLastEvent to set
     */
    public void setTimeOfLastEvent(double timeOfLastEvent) {
        this.timeOfLastEvent = timeOfLastEvent;
    }

    /**
     * @return the typeOfLastEvent
     */
    public int getTypeOfLastEvent() {
        return typeOfLastEvent;
    }

    /**
     * @param typeOfLastEvent the typeOfLastEvent to set
     */
    public void setTypeOfLastEvent(int typeOfLastEvent) {
        this.typeOfLastEvent = typeOfLastEvent;
    }

    // Abstract methods, must be implemented in derived classes.
    public abstract void setNextEvent(); 

    protected abstract double getNextInterMoveTime(); 

}
