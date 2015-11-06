/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author lbarnett
 */
public abstract class AbstractVector extends AbstractAgent {
    /**
     * Constant for InfectionVector instance that is currently an egg
     */
    public static final int EGG = 0;

    /**
     * Constant for an InfectionVector instance that is currently in the
     * larval stage. Instances at this stage of life can't reproduce, but
     * can eat, become infected, and transmit the infection.
     */
    public static final int LARVA = 1;

    /**
     * Constant for an infectionVector instance that is currently an adult.
     */
    public static final int ADULT = 2;
    
    public static final String [] STAGE_LABELS = {"EGG", "LARVA", "ADULT"};
    
    /**
     * Creation mode: created as an egg
     */
    public static final int CREATE_EGG = 0;
    
    /**
     * Creation mode: created at simulation initiation
     */
    public static final int CREATE_ADULT = 1;
    
    /**
     * Constant for female gender.
     */
    public static int FEMALE = 0;
    
    /**
     * Constant for male gender.
     */
    public static int MALE = 1;
    
    public static final String[] GENDER_LABELS = {"FEMALE", "MALE"};
    
    // Event times
    private double timeHatching;
    private double timeMaturing;
    private double timeNextOviposition;
    
    /**
     * Life span of this individual in days
     */
    protected double lifeSpan;
    
    /**
     * Timestamp for birth of this individual
     */
    protected double birthTime;
    
    /**
     * Gender of this InfectionVector instance.
     */
    protected int gender;
    
    /**
     * Current stage of life for this InfectionVector instance. One of 
     * EGG, LARVA or ADULT
     */
    protected int lifeStage;
    
    public AbstractVector (String id, boolean isInfected,
                            int creationMode, double creationTime ) {
        super( id, isInfected );
        
        // Determine gender
        double prob = SimulationManager.Uniform(0.0, 1.0);
        boolean isFemale = ( prob < Parameters.getPercentFemales() );
        if ( isFemale )
            gender = FEMALE;
        else
            gender = MALE;
        
                // Until we find we need these, put them at infinity.
        timeHatching = Double.MAX_VALUE;
        timeMaturing = Double.MAX_VALUE;
        timeNextOviposition = Double.MAX_VALUE;
        
        // How long this louse should live. Need to take into account that
        // Normal distribution can generate negative values. Keep trying 
        // until we get one that makes sense.
        lifeSpan = -1.0;
        
        while ( lifeSpan < 0.0 )
            lifeSpan = 
                SimulationManager.Normal(
                    Parameters.getLouseLongevityMean(), 
                    Parameters.getLouseLongevityStDev());

        double timeOfMaturity = -1.0;
        while ( timeOfMaturity < 0.0 )
            timeOfMaturity = 
                SimulationManager.Normal(
                    Parameters.getMeanTimeToAdult(), 
                    Parameters.getStDevTimeToAdult() );
            
        // Life stage, etc.
        if ( creationMode == CREATE_EGG ) {
            lifeStage = EGG;
            timeHatching = creationTime +
                    SimulationManager.Uniform(
                        Parameters.getNitGestationTimeMin(),
                        Parameters.getNitGestationTimeMax());
            timeMaturing = timeHatching + timeOfMaturity;
            timeOfDeath = timeHatching + lifeSpan;
            
            // First move time happens some time after hatching
            nextMoveTime = timeHatching;
            getNextMoveTime();

            // Set up first oviposition event - typically doesn't happen 
            // first day after maturing.
            if ( this.gender == FEMALE ) {
                double oviposDelta = SimulationManager.Exponential( 1.0 );
                timeNextOviposition = timeMaturing + 1.0 + oviposDelta;
            } else {
                timeNextOviposition = Double.POSITIVE_INFINITY;
            }
        } else {
            // Creation during simulation initialization
            
            // Where the louse currently is in its life span. We assume 
            // all lice created in this process have hatched.
            double currentAge = 
                    SimulationManager.Uniform(0.0, lifeSpan);
            
            if ( currentAge > timeOfMaturity ) {
                lifeStage = ADULT;
                timeOfDeath = creationTime + (lifeSpan - currentAge);
                timeMaturing = timeHatching = Double.POSITIVE_INFINITY;
                if ( isFemale ) {
                    // Assume females lay eggs just once a day
                    timeNextOviposition = SimulationManager.Exponential( 1.0 );;
                }
            } else {
                lifeStage = LARVA;
                // Already hatched, don't care about this
                timeHatching = timeNextOviposition = Double.POSITIVE_INFINITY;
                timeMaturing = timeOfMaturity - currentAge;
                timeOfDeath = creationTime + (lifeSpan - currentAge);
            }
            
            // When do we move?
            nextMoveTime = SimulationManager.Exponential(
                        Parameters.getAvgTimeBtwnVectorMove());

        }
        this.setNextEvent();

    }
    
    public double generateNitLifespan()
    {
        return SimulationManager.Uniform(
                                Parameters.getNitLongevityAwayFromHostMin(),
                                Parameters.getNitLongevityAwayFromHostMax());
    }
    
    public double generateLouseSurvivalTime()
    {
        return SimulationManager.Uniform(
                                Parameters.getLouseLongevityAwayFromHostMin(),
                                Parameters.getLouseLongevityAwayFromHostMax());
    }
    
    public boolean wasFeeding()
    {
        double prob = SimulationManager.getRandomNumberGenerator().nextDouble();
        return ( prob <= Parameters.getProbFeeding() );
    }
    
    public boolean removedByGrooming()
    {
        double prob = SimulationManager.getRandomNumberGenerator().nextDouble();
        return ( prob <= Parameters.getProbFeeding() );
    }

    protected double getNextInterMoveTime() {
        return SimulationManager.Exponential(Parameters.getAvgTimeBtwnVectorMove());
    }

    /**
     * @return the timeHatching
     */
    public double getTimeHatching() {
        return timeHatching;
    }

    /**
     * @return the timeMaturing
     */
    public double getTimeMaturing() {
        return timeMaturing;
    }

    /**
     * @return the timeNextOviposition
     */
    public double getTimeNextOviposition() {
        return timeNextOviposition;
    }

    /**
     * Called after actions on the Vector that modify event times. 
     * This sets the value of the nextEventTime to the smallest
     * of the event times for this Vector, then sets nextEventType
     * according to the type of that event.
     */
    public void setNextEvent() {
        // we'll presume death is the highest priority event; this way, if
        // an agent is busy doing some event and turns out the agent should
        // die right now, the "some event" won't take precedence
        nextEventTime = timeOfDeath;
        nextEventType = SimulationManager.VECTOR_DEATH_EVENT;
        if (getTimeHatching() < nextEventTime) {
            nextEventTime = getTimeHatching();
            nextEventType = SimulationManager.VECTOR_HATCH_EVENT;
        }
        /*
        if ( timeNextFeeding < nextEventTime ) {
        nextEventTime = timeNextFeeding;
        nextEventType = SimulationManager.VECTOR_BEGIN_FEEDING_EVENT;
        }
        if ( timeFeedingEnds < nextEventTime ) {
        nextEventTime = timeFeedingEnds;
        nextEventType = SimulationManager.VECTOR_END_FEEDING_EVENT;
        }
         */
        if (getTimeMaturing() < nextEventTime) {
            nextEventTime = getTimeMaturing();
            nextEventType = SimulationManager.VECTOR_MATURE_EVENT;
        }
        if (getTimeNextOviposition() < nextEventTime) {
            nextEventTime = getTimeNextOviposition();
            nextEventType = SimulationManager.VECTOR_OVIPOSITION_EVENT;
        }
        if (nextMoveTime < nextEventTime) {
            nextEventTime = nextMoveTime;
            nextEventType = SimulationManager.VECTOR_MOVE_EVENT;
        }
    }

    /**
     * @param timeHatching the timeHatching to set
     */
    public void setTimeHatching(double timeHatching) {
        this.timeHatching = timeHatching;
    }

    /**
     * @param timeMaturing the timeMaturing to set
     */
    public void setTimeMaturing(double timeMaturing) {
        this.timeMaturing = timeMaturing;
    }

    /**
     * @param timeNextOviposition the timeNextOviposition to set
     */
    public void setTimeNextOviposition(double timeNextOviposition) {
        this.timeNextOviposition = timeNextOviposition;
    }
    
    public String getLifeStageString(){
        if ( getLifeStage() == EGG )
            return "EGG";
        else if ( getLifeStage() == LARVA )
            return "LARVA";
        else
            return "ADULT";
    }

    public String getGenderString()
    {
        if ( getGender() == FEMALE )
            return "Female";
        else
            return "Male";
    }

    /**
     * @param gender the gender to set
     */
    public void setGender(int gender) {
        this.gender = gender;
    }

    /**
     * Set up the process of killing this InfectionVector. 
     */
    public void initiateDeath()
    {
        // Get the host and remove this vector from its list of vectors
        Host h = this.getHost();
        
        if ( h == null ){
            System.out.println(
                    "initiateDeath() - host is null (shouldn't happen)");
        }
        
        double currentTime = h.getCurrentEventTime();
        timeOfDeath = currentTime;
        
        // The event that causes this is happening to another agent, so
        // we need to call setNextEvent() here. It won't happen otherwise,
        // and we'd wind up with out-of-order events.
        this.setNextEvent();
    }
    
    /**
     * Prepare the Vector for execution of a single, selected event. Useful
     * for debugging. Sets event times for all events to MAX_VALUE, so 
     * that the one event we subsequently add a time for will be the next
     * event executed for this host.
     */
    public void setForSingleStep(){
        // We are about to set the time for some particular event that
        // we want the simulation to execute next. Make all "next event"
        // times as big as possible so that the one we choose will be 
        // the next event processed for this host.
        nextMoveTime = Double.MAX_VALUE;
        timeOfDeath = Double.MAX_VALUE;
        timeHatching = Double.MAX_VALUE;
        timeMaturing = Double.MAX_VALUE;
        timeNextOviposition = Double.MAX_VALUE;
        nextEventTime = Double.MAX_VALUE;
        nextEventType = SimulationManager.NO_EVENT;
    }
    
    public abstract void mature();
    public abstract void hatch();
    public abstract int getGender();
    public abstract Host getHost();
    public abstract void setHost(Host myHost);
    public abstract int getLifeStage();
    public abstract void setLifeStage(int lifeStage);
    public abstract int getClothesSet();
    public abstract void setClothesSet(int clothesSet);
    public abstract void die();
}
