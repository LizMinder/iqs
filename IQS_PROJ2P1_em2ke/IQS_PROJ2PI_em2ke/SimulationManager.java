import squint.*;                  // for GUIManager
import java.util.ArrayList;       // for, duh, Arraylist
import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.Random;          // for, (see comment above), Random
import javax.swing.JProgressBar;  // for, (again?), JProgressBar

/**
 * Simulation of the spread of an insect-borne disease with a single animal
 * reservoir, such as urban trench fever.
 * 
 * This (abstract) class acts in the place of GUIManager from the Williams
 * Squint library by, first, extending GUIManager so we get all of its bells 
 * and whistles, and, second, implementing the guts of the agent-based 
 * simulation.
 * 
 * This class provides four methods that a class extending this class
 * (i.e., SimulationWindow) should use to control the execution
 * of the simulation:
 *        start()    resume()    stop()     pause()
 * Details on these methods can be found below.
 *
 * In the run() method provided in this class are the details for
 * the next-event simulation that drives the agent-based model.
 * 
 * @author Barry Lawson and Lewis Barnett
 * @version 2.0(2013-02-28)
 */

public abstract class SimulationManager extends GUIManager 
                                        implements Runnable
{   
    public static final boolean DEBUG = false;
    public static final boolean DEBUG_TREATED = true;
    protected static final boolean CHECK_CONSISTENCY = true;
    protected static final boolean CHECK_QUEUE_INTEGRITY = false;
    
    protected static Random rng;
    
    static {
        int seed = Parameters.getRNGInitialSeed();

        if ( seed == -1 ) {
            rng = new Random();
        } else {
            rng = new Random( seed );
        }
    }

    // instance variables
    protected ShelterGrid shelters;    // Shelters that form the grid
    protected AgentCanvas   canvas;      // need a reference for drawing
    protected JProgressBar  progressBar; // need to update based on sim time

    // a reference and boolean to handle fast/slow animation
    protected Thread  thisThread;
    protected boolean fastAnimation = true;

    // the following are to determine the state of the simulation in response
    // to user button presses
    protected static final int RUNNING      = 0;
    protected static final int PAUSED       = 1;
    protected static final int HALTED       = 2;
    protected static final int SINGLE_STEP  = 3;
    protected int    state;

    //private Host [][] occupied; // a 2D array to determine where agents are

    protected ArrayList<AbstractAgent> agentList; 
    //protected PriorityQueue<AbstractAgent> agentQueue; 
        // A list of all agents in the simulation; this is declared as
        // protected because we access it directly (gasp!) from within
        // AgentCanvas.  Why?  Because we only access it to draw the agents,
        // and given that the list may be large, it doesn't make sense to
        // make a copy and return that copy to AgentCanvas.
    
    
    // Don't know yet if treatment/mitigation should be part of this project.
    // Set up to support it anyway.
    protected int numHosts;
    protected int numInfectedHosts;    // infected and uninfected agents are kept 
    protected int numUninfectedHosts;  // in same list (above), just keep counts
    protected int numInfestedHosts;   
    protected int numUninfestedHosts;
    protected int numTreatedHosts;
    
    protected int numInfectedVectors;
    protected int numUninfectedVectors;

    protected double time;              // the simulation clock
    protected double maxSimulatedTime;  // max that time can be
    
    // Track some info for debugging
    protected AbstractAgent lastActiveAgent;
    protected double lastEventTime;
    protected int lastEventType;

    /**
     * Counters used to generate host and vector ID numbers
     */
    protected int nextHostID;
    protected int nextVectorID;
    
    private TimeSeriesDialog timeSeriesDialog;  // a reference to the time 
                                                //  series window

    // event types for the next-event simulation
    protected static final int HOST_MOVE_EVENT             = 0;
    protected static final int HOST_INTERACT_EVENT         = 1;
    protected static final int HOST_SHOWER_EVENT           = 2;
    protected static final int HOST_ANTIBIOTIC_APP_EVENT   = 3;
    protected static final int HOST_ANTIBIOTIC_CLEAR_EVENT = 4;
    protected static final int HOST_LAUNDRY_EVENT          = 5;
    protected static final int HOST_CLOTHES_CHANGE_EVENT   = 6;
    protected static final int HOST_CHECK_INFECTION_EVENT  = 7;
    protected static final int HOST_DEATH_EVENT            = 8;
    protected static final int VECTOR_HATCH_EVENT          = 9;
    protected static final int VECTOR_MATURE_EVENT         = 10;
    protected static final int VECTOR_MOVE_EVENT           = 11;
    protected static final int VECTOR_OVIPOSITION_EVENT    = 12;  // Lays eggs
    protected static final int VECTOR_DEATH_EVENT          = 13;
    protected static final int NO_EVENT                    = 14;
    
    protected static final int HOST_EVENT_MIN = HOST_MOVE_EVENT;
    protected static final int HOST_EVENT_MAX = HOST_DEATH_EVENT;
    protected static final int VECTOR_EVENT_MIN = VECTOR_HATCH_EVENT;
    protected static final int VECTOR_EVENT_MAX = VECTOR_DEATH_EVENT;
    
    protected static final String [] EVENT_NAMES =
    {
        "HOST_MOVE", 
        "HOST_INTERACT",
        "HOST_SHOWER",
        "HOST_AB_APP",
        "HOST_AB_CLEAR",
        "HOST_LAUNDRY",
        "HOST_CLOTHES_CH",
        "HOST_CHECK_INF",
        "HOST_DEATH",
        "VECTOR_HATCH",
        "VECTOR_MATURE",
        "VECTOR_MOVE",
        "VECTOR_OVIPOS",
        "VECTOR_DEATH",
        "NO_EVENT"
    };
    
    /**
     * Time to separate two events. Roughly one minute
     */
    public static final double EVENT_EPSILON = 7.0e-4;
    
    public static String getEventName( int eventCode )
    {
        String name = null;
        if ( eventCode >= HOST_EVENT_MIN && eventCode <= VECTOR_EVENT_MAX ){
            name = EVENT_NAMES[eventCode];
        } else {
            name = "UNKNOWN_EVENT";
        }
        
        return name;
    }

    /**
     * The constructor for the class.  Only initializes the agentQueue to
     * an empty list and sets time to be 0.
     *
     * Remember that the default constructor of the parent class is 
     * automatically called before the derived class constructor is called.
     * The class extending this class will need to take care of any other
     * necessary initialization details.
     */
    public SimulationManager()
    {
        /*
        int seed = Parameters.getRNGInitialSeed();

        if ( seed == -1 ) {
            rng = new Random();
        } else {
            rng = new Random( seed );
        }
        */

        shelters = new ShelterGrid();
        //agentQueue = new PriorityQueue<AbstractAgent>();
        agentList = new ArrayList<AbstractAgent>();
        time = 0;
        
        nextVectorID = 0;
        nextHostID = 0;
        
        // Info for debugging
        lastActiveAgent = null;
        lastEventTime = time;
        lastEventType = NO_EVENT;
    } // end of SimulationManager()

    //======================================================================
    //* public static Random getRandomNumberGenerator()
    //* Returns a reference to the simulation's RNG.
    //======================================================================
    public static Random getRandomNumberGenerator()
    {
        return SimulationManager.rng;
    }

    //======================================================================
    //* public void run()
    //* This is a required method because this class implements Runnable.
    //* Whenever we start() a new Thread of this class (see the start()
    //* method below), this method will be invoked.
    //*
    //* This method houses the next-event engine that drives the agent-
    //* based model.  Essentially, as long as we haven't passed the maximum
    //* simulated time and there are still agents living, we grab the next
    //* event in simulated time, process that event (which has a corresponding 
    //* agent), and then redraw the grid as necessary.
    //======================================================================
    public void run()
    {
        int nextTimeStep = 1;  // next time to update counts in time series window
        //double lastEventTime = 0.0;
        int lastNumTreated = numTreatedHosts;
        int type = VECTOR_EVENT_MAX + 1;

        // if the state is RUNNING, we want to do normal sim stuff;
        // if the state is PAUSED, we want run() to keep going, but
        //    not do normal sim stuff;
        // if the state is HALTED, we want to drop out altogether.
        while (state != HALTED){
          try {
            // this is the main simulation loop; keep going so long as
            // the user hasn't interrupted, we're not past the maximum
            // time, and there are still living agents
            while (state == RUNNING || state == SINGLE_STEP){
                // uncomment this line if you want to see text-based info
                // about the agent/event being handled
                // logAgentInfo();  // debugging info

                if (DEBUG_TREATED){
                    if ( numTreatedHosts > lastNumTreated + 1 ){
                        System.out.println( "More than 1 host treated in event loop");
                        System.out.println( "\ttime: " + time + "  type " + 
                                            EVENT_NAMES[type]);
                    }
                    
                    lastNumTreated = numTreatedHosts;
                }
                
                // first, find the agent with most imminent event time;
                // break out of the running loop if all agents have died
                //AbstractAgent agent = agentQueue.poll(); //getNextEvent();
                AbstractAgent agent = getNextEvent();
                
                if (agent == null) { state = HALTED; break; }

                // update the simulation clock; break out if beyond max
                time = agent.getNextEventTime();
                
                if ( DEBUG ) {
                    if ( lastEventTime > time ) {
                        System.out.println(
                                "EVENT ORDERING ERROR: last = " + lastEventTime 
                                +" current = " + time );
                    }
                }
                
                if (time >= maxSimulatedTime) { state = HALTED; break; }
                
                if (time > nextTimeStep)
                {
                    // we've crossed another integer time boundary, so
                    // bump the counter and update the counts in the 
                    // time series window
                    nextTimeStep++;
                    numHosts = shelters.getTotalOccupancy();
                    numInfectedHosts = shelters.getInfectedCount();
                        setNumInfestedHosts(shelters.getInfestedCount());
                    numUninfectedHosts = numHosts - numInfectedHosts;
                        setNumUninfestedHosts(numHosts - getNumInfestedHosts());

                    timeSeriesDialog.updateCounts( numInfectedHosts,
                                                   numUninfectedHosts, getNumInfestedHosts(), getNumUninfestedHosts(),
                                                   numTreatedHosts );
                                                   
                    if ( DEBUG ){
                        printSummary( nextTimeStep - 1 );
                    }
                }

                // now process the event based on its type.
                // Note that any code that changes the time that something
                // happens for an agent should call that agent's 
                // setNextEvent method to update the nextEvent time and
                // type.
                Host h = null;
                Louse v = null;
                Shelter s = null;
                type = agent.getNextEventType();
                
                lastActiveAgent = agent;
                lastEventTime = time;
                lastEventType = type;

                if ( DEBUG ) {
                    System.out.printf("[%9.4f] %s(%s)\n",
                            time, agent.getId(), getEventName( type ) );

                    /* */
                    if (  time >= 5.491731133 && agent.getId().equals("V138")){
                        System.out.println("  ^==Event of interest");
                        //checkQueueForTimeConsistency();
                    }
                    /* */
                }
                

                agent.setTimeOfLastEvent( time );
                agent.setTypeOfLastEvent( type );

                
                switch (type)
                {
                    case SimulationManager.HOST_MOVE_EVENT:
                        // move the agent a random amt in a random direction
                        h = (Host) agent;
                        moveHost(h);

                        // agent at least tried, so try again in future
                        agent.setNextMoveTime();

                        s = h.getCurrentShelter();
                        checkInfectionStatus(h);
                        
                        // Conditions for treating agent:
                        // * Agent is in a shelter (this should always be true)
                        // * Medical care is available in the shelter
                        // * The agent is actually infected
                        // * The agent has not previously been treated.
                        if ( s != null && s.isMedicalCareAvailable() &&
                                h.getInfected() && ! h.getTreated() ) {
                            /*
                            h.treatAgent( time );
                            numTreatedHosts++;
                            */
                            h.setTimeAntibioticApplied(time);
                        }
                        agent.setNextEvent();
                        break;

                    case SimulationManager.HOST_INTERACT_EVENT:
                        // then interact with neighbor agents (if any)
                        //interactWithNeighborhood(agent);
                        h = (Host) agent;
                        interact( h, time );
                        checkInfectionStatus(h);
                        agent.setNextEvent();
                        break;

                    case SimulationManager.HOST_DEATH_EVENT:
                        // remove from the agent list and update counts
                        // No need with PriorityQueue
                        //agentQueue.remove(agent);
                        //occupied[agent.getRow()][agent.getCol()] = null;
                        h = (Host) agent;
                        s = h.getCurrentShelter();
                        s.remove( h );

                        if (h.getInfected())
                            numInfectedHosts--;
                        else
                            numUninfectedHosts--;
                        
                        if (h.getInfested() )
                            setNumInfestedHosts(getNumInfestedHosts() - 1);
                        else
                            setNumUninfestedHosts(getNumUninfestedHosts() - 1);

                        if (h.getTreated()) numTreatedHosts--;
                        
                        agentList.remove( agent );

                        break;

                    case SimulationManager.HOST_ANTIBIOTIC_APP_EVENT:
                        // time for an agent to receive antibiotic
                        // Have already called treatAgent with this host - 
                        // that now happens when they move to a shelter w/
                        // medical care.
                        //if (agent.treatAgent(time))
                        h = (Host) agent;
                        // Regardless of whether this host ends up being treated, we
                        // should clear this antibiotic application event - otherwise,
                        // we could get into an infinite loop with this host.
                        h.setTimeAntibioticApplied(Double.MAX_VALUE);
                        if (!h.getTreated() && h.treatAgent(time) ){
                            numTreatedHosts++;
                        }
                        /*
                        h.setTreated(true);
                        // Infections happen only once (immune thereafter)
                        // so we should never have to reapply antibiotics;
                        h.setTimeAntibioticApplied(Double.MAX_VALUE);
                        numTreatedHosts++;
                        h.setTimeAntibioticClears( 
                                time + 
                                SimulationManager.Uniform(
                                    Parameters.getTreatmentLengthMin(), 
                                    Parameters.getTreatmentLengthMax()));
                        */
                        // Has happened during move handling at the same time
                        //checkInfectionStatus(h);
                        agent.setNextEvent();
                        break;

                    case SimulationManager.HOST_ANTIBIOTIC_CLEAR_EVENT:
                        // an agent's antibiotic is wearing off
                        h = (Host) agent;
                        h.clearAntibiotic(Double.MAX_VALUE);
                        h.setInfected( false );
                        numTreatedHosts--;
                        numInfectedHosts--;
                        numUninfectedHosts++;
                        //checkInfectionStatus(agent);
                        // Assume that treatment clears infection
                        Shelter shelter = h.getCurrentShelter();
                        shelter.decrementInfected();
                        agent.setNextEvent();
                        break;
                        
                    case SimulationManager.HOST_LAUNDRY_EVENT:
                        h = (Host) agent;
                        s = h.getCurrentShelter();
                        
                        if (s.isLaundryAvailable()){
                            // Assume laundry means changing clothes, if clothes change is
                            // available.
                            if ( h.getChangesOfClothes() > 0 ) {
                                h.changeClothes( time );
                            }
            
                            h.doLaundry(time);
                        } else {
                            // Try tomorrow
                            h.setTimeNextLaundry(time + 1.0);
                        }
                        h.setNextEvent();
                        break;
                        
                    case SimulationManager.HOST_CLOTHES_CHANGE_EVENT:
                        h = (Host) agent;
                        if ( h.getChangesOfClothes() > 0 ) {
                            //ArrayList<AbstractAgent> killedByClothesChange = 
                                    h.changeTheClothes(time);
                            //requeueAgents( killedByClothesChange );
                            h.setNextEvent();
                        } else {
                            // No change of clothes, make sure we don't get
                            // this event again.
                            h.setTimeNextClothesChange(Double.MAX_VALUE);
                        }
                        h.setNextEvent();
                        break;
                        
                    case SimulationManager.HOST_SHOWER_EVENT:
                        h = (Host) agent;
                        s = h.getCurrentShelter();
                        if ( s != null && s.isShowersAvailable()){
                            h.shower( time );
                        } else {
                            // Try again tomorrow
                            h.setTimeNextShower(time + 1.0 );
                        }
                        h.setNextEvent();
                        break;
                        
                    case SimulationManager.HOST_CHECK_INFECTION_EVENT:
                        h = (Host) agent;
                        if ( h.checkInfection( time ) ) {
                            // No need to check again for infection.
                            //h.setTimeNextInfectionCheck( Double.MAX_VALUE );
                            h.setTimeInfected( time );
                            Shelter sh = h.getCurrentShelter();
                            sh.incrementInfected();
                            numInfectedHosts++;
                            numUninfectedHosts--;
                            /* Do this only upon move now.
                            if ( sh.isMedicalCareAvailable() ) {
                                h.treatAgent( time );
                            }
                            */
                        }
                        
                        h.setTimeNextInfectionCheck( time + 1.0 );
                        
                        h.setNextEvent();
                        break;
                        
                    case SimulationManager.VECTOR_HATCH_EVENT:
                        v = (Louse) agent;
                        v.hatch();
                        
                        // Only happens once per agent. Reschedule at +Infinity
                        // so setNextEvent() doesn't get confused.
                        v.setTimeHatching( Double.POSITIVE_INFINITY );
                        
                        v.setNextEvent();
                        
                        Host vHost = v.getHost();
                        vHost.vectorHatched();
                        checkInfestationCap(time, vHost);
                        break;
                        
                    case SimulationManager.VECTOR_MATURE_EVENT:
                        v = (Louse) agent;
                        v.mature();
                        
                        // Only happens once per agent. Reschedule at +Infinity
                        // so setNextEvent() doesn't get confused.
                        v.setTimeMaturing( Double.POSITIVE_INFINITY );
                        v.setNextEvent();
                        break;

                    case SimulationManager.VECTOR_MOVE_EVENT:
                        v = (Louse) agent;
                        moveVector( v, time );
                        v.setNextEvent();
                        break;

                    case SimulationManager.VECTOR_OVIPOSITION_EVENT:
                        // Handle this all here.
                        // Mature female lice lay about eight eggs per day.
                        // This code assumes that the event happens about 
                        // once per day and that all the eggs are laid in
                        // one event. This may not be accurate.
                        v = (Louse) agent;
                        
                        // Sanity check
                        if ( v.getGender() != Louse.FEMALE ) {
                            System.out.println(
                                    "VECTOR_OVIPOSITION_EVENT for male vector: " +
                                    v.getId() + " at time " + time );
                            continue;
                        }
                        
                        // Reschedule if feeding or if not hosted
                        double prob = rng.nextDouble();
                        if ( prob < Parameters.getProbFeeding() ||
                                v.getHost() == null ) {
                            //double feedingEnds = v.getTimeFeedingEnds();
                            double mealTimeRemaining = 
                                    Uniform( 0.0, Parameters.getMealDurationMax() );
                            v.setTimeNextOviposition( time + mealTimeRemaining + EVENT_EPSILON );
                        } else {

                            Host thisHost = v.getHost();
                            int clothesSet = v.getClothesSet();

                            // How many eggs?
                            int clutchSize = (int) Math.round(
                                    Uniform( Parameters.getEggsPerDayMin(),
                                             Parameters.getEggsPerDayMax() ) );

                            for ( int i = 0; i < clutchSize; i++ ){
                                String newId = 
                                        "V" + nextVectorID;
                                nextVectorID++;
                                Louse nit =
                                        new Louse( newId,
                                                             false,
                                                             thisHost,
                                                             Louse.CREATE_EGG,
                                                             time );
                                
                                // Eggs are laid in same set of clothes where
                                // mother resides.
                                nit.setClothesSet( clothesSet );
                                
                                nit.setNextEvent();
                                //agentQueue.add( nit );
                                agentList.add( nit );
                                
                                // Was host previously infested? If not, 
                                // update state and shelter stats.
                                if ( thisHost.getTotalVectors() == 1 ){
                                    thisHost.setInfested( true );
                                    thisHost.getCurrentShelter().incrementInfested();
                                }
                            }
                            
                            /*
                            if ( CHECK_QUEUE_INTEGRITY ) {
                               ArrayList<AbstractAgent> dups = checkQueueForDuplicates();
                               if ( dups.size() > 0) {
                                   System.out.println("start(): duplicate discovered in queue.");
                               }
                           }
                           */
                            
                           // Schedule next oviposition. We assume this 
                            // happens roughly once per day
                            double oviposDelta = Exponential( 1.0 );
                            v.setTimeNextOviposition( time + oviposDelta );
                        }
                        
                        v.setNextEvent();
                        
                        break;
                        
                    case SimulationManager.VECTOR_DEATH_EVENT:
                        v = (Louse) agent;
                        h = v.getHost();
                        
                        v.die();
                        
                        // Remove from the list of agents. No further events
                        // will be processed.
                        // With PriorityQueue, we've already removed - just
                        // don't re-enqueue.
                        // agentQueue.remove( v );
                        agentList.remove( v );
                        
                        // Happens only once, no need to call setNextEvent
                        // because nothing else will ever happen to this 
                        // agent.
                        break;
                        
                }
                
                if ( CHECK_CONSISTENCY ){
                    if ( ! sheltersConsistent( time ) ){
                        System.out.println("Shelter inconsistency discovered.");
                    }
                }
                lastEventTime = time;
                
                // update the drawing
                canvas.repaint();
                progressBar.setValue((int)time);

                // slow it down if the user doesn't want a speedy redraw
                if (!fastAnimation)
                    thisThread.sleep(1);
                
                // If we're single-stepping, switch immediately back to PAUSED
                if (state == SINGLE_STEP) state = PAUSED;
            }
          }
          catch (Exception e) { e.printStackTrace(); }
        }

        canvas.repaint();       // paint one last time, just in case...

        simulationFinished();   // this method will be implemented by the
                                // extending class, to do whatever should
                                // be done (e.g., activate buttons) when
                                // the simulation is finished

    } // run()

    //======================================================================
    //* public void pause()
    //* Called by simulation window code to pause the running simulation.
    //* This simple implementation presumes that enabling/disabling of
    //* the start/pause/resume/stop buttons has been handled correctly
    //* elsewhere (simulation window code).
    //======================================================================
    public void pause()
    {
        state = PAUSED;
    }

    //======================================================================
    //* public void stop()
    //* Called by simulation window code to stop the simulation altogether.
    //* This simple implementation presumes that enabling/disabling of
    //* the start/pause/resume/stop buttons has been handled correctly
    //* elsewhere (simulation window code).
    //======================================================================
    public void stop()
    {
        state = HALTED;
        // the while (state == RUNNING) loop
        // above will become false, as will the while (state != HALTED)
        // loop, causing us to leave the run() method (i.e., the thread dies)
    }

    //======================================================================
    //* public void resume()
    //* Called by simulation window code to resume a paused simulation.
    //* This simple implementation presumes that enabling/disabling of
    //* the start/pause/resume/stop buttons has been handled correctly
    //* elsewhere (simulation window code).
    //======================================================================
    public void resume()
    {
        state = RUNNING;
        // the while (state == RUNNING) loop
        // above was already false, but we're still inside the while (state !=
        // HALTED) loop; setting the state back to RUNNING now makes us jump
        // back and execute within the inner loop
    }

    /** Called by simulation window code to start the simulation initially.
     * This implementation presumes that enabling/disabling of
     * the start/pause/resume/stop buttons has been handled correctly
     * elsewhere (simulation window code).
     * 
     * @param maxTime       Total running time (in days) for the simulation
     * @param maxAgents     Maximum number of Hosts allowed
     * @param totalAgents   Number of Hosts to create for this run
     * @param pctInfested   Percentage of Hosts infested with lice
     * @param pctInfected   Percentage of Hosts infected with B. quintana
     * @param pctVectorsInfected    Percentage of lice infected w/ B. q.
     * @param pctTreated            Percentage of Hosts initially treated
     * @param animateFast   Run the simulator fast (affects visuals)
     * @param theCanvas     Canvas displaying the environment
     * @param theBar        Progress bar for setup.
     */
    //======================================================================
    public void start(int maxTime, 
                      int maxAgents, 
                      int totalAgents, 
                      double pctInfested,
                      double pctInfected, 
                      double pctVectorsInfected,
                      double pctTreated,
                      boolean animateFast,
                      AgentCanvas theCanvas,
                      JProgressBar theBar)
    {
        rng.setSeed( Parameters.getRNGInitialSeed() );    

        // create a new window for displaying time series
        // Base y axis on maximum allowed agent population
        //timeSeriesDialog = new TimeSeriesDialog(maxAgents, maxTime);
        // Base y axis on actual number of agents in population
        timeSeriesDialog = new TimeSeriesDialog(
                Parameters.getHostPopulationSize(), maxTime);

        // if the user pushes start while a simulation is running, we
        // start again, which could cause strange thread conflicts; we could
        // handle this, but will rely instead on the coder to disable the
        // start button once it is pressed, only to enable once the simulation
        // thread is finished (either forcefully or naturally 'stop'ped)

        // set the instance variables using the arguments passed in
        maxSimulatedTime = maxTime;
        fastAnimation    = animateFast;
        canvas           = theCanvas;
        progressBar      = theBar;

        time = 0;

        nextHostID = 0;
        nextVectorID = 0;
        
        reset();    // Does agentList.clean() and zeros counters
        //agentList.clear();
        //agentQueue.clear();  // just in case, clear out the agent list -- if user
                            // hits run button twice, must start over

        // Build the list of agents -- this needs to come after the antigens
        // and antibiotics are created because the agent constructor uses the
        // former.
        double proportionInfested = pctInfested / 100.0;
        double proportionInfected = pctInfected / 100.0;
        double proportionVectorsInfected = pctVectorsInfected / 100.0;
        double proportionTreated  = pctTreated / 100.0;
        
        // Also stash these in Parameters, in case we need them other places...
        Parameters.setPercentOfHostsInfested( proportionInfested );
        Parameters.setPercentOfHostsInfected( proportionInfected );
        Parameters.setPercentOfVectorsInfected( proportionVectorsInfected );

        /*
        numInfectedHosts   = 0; //(int)(totalAgents * proportionInfected);
        //numUninfectedHosts = totalAgents - numInfectedHosts;
        setNumInfestedHosts(0); //(int)(totalAgents * proportionInfested);
        //numUninfestedHosts = totalAgents - numInfestedHosts;

        // only treat the infected agents
        numTreatedHosts    = 0; //(int)(numInfectedHosts * proportionTreated);
        /* */
        
        // Generate Hosts.
        for ( int i = 0; i < totalAgents; i++ ){
            boolean infested = ( proportionInfested > rng.nextDouble() );
            boolean infected = ( proportionInfected > rng.nextDouble() );
            boolean treated = false;
            
            if ( infested ) setNumInfestedHosts(getNumInfestedHosts() + 1);
            if ( infected ) {
                numInfectedHosts++;
                /* Do we want some hosts to be initially treated? 
                 * Tentatively, no.
                 */
                /*
                treated = ( proportionTreated > rng.nextDouble() );
                if ( treated ) numTreatedHosts++;
                */
            }
            
            Host agent = new Host( "H" + nextHostID,
                                            infested, infected );
            nextHostID++;
            
            if ( treated ) agent.treatAgent( time );
            
            //agentQueue.add( agent );
            agentList.add( agent );
            
            // Infest the host
            if ( infested ) {
                int numVectors = 
                        (int) Math.round( SimulationManager.Uniform( 
                                    Parameters.getAvgInfestationSizeMin(),
                                    Parameters.getAvgInfestationSizeMax() ) );

                for ( int j = 0; j < numVectors; j++ ){
                    double prob = Uniform( 0.0, 1.0 );
                    boolean vectorInfected = 
                            ( prob < Parameters.getPercentOfVectorsInfected() );

                    Louse v = 
                            new Louse( "V" + nextVectorID,
                                                 vectorInfected, agent,
                                                 Louse.CREATE_ADULT, 
                                                 0.0 );
                    // Add new louse to Host's list
                    // Now done in InfectionVector's constructor
                    // agent.addVector ( v );
                    //agentQueue.add( v );
                    agentList.add( v );
                    nextVectorID++;
                }

            }
        }
        
        setNumUninfestedHosts(totalAgents - getNumInfestedHosts());
        // Duplicated line?
        //setNumUninfestedHosts(totalAgents - getNumInfestedHosts());

        // place all agents at random and then draw them
        placeAgentsAtRandom();
        canvas.repaint();

        // initialize the simulation and start a new Thread for executing the
        // simulation code
        state = RUNNING;
        thisThread = new Thread(this);
        thisThread.start();
    }

    //======================================================================
    //* private void logAgentInfo()
    //* Private method just to print out debugging information about the
    //* agents running amok on the landscape.  In a typical run, this
    //* method won't be called.
    //======================================================================
    private void logAgentInfo()
    {
        System.out.println("AGENTS:");
        //for (int i = 0; i < agentQueue.size(); i++)
        //for ( AbstractAgent a: agentQueue )
        for ( AbstractAgent a: agentList )
        {
            //AbstractAgent a = agentQueue.get(i);
            
            if ( a instanceof Host ) {
                Host h = (Host) a;
            /*
                System.out.println(a.getId() + "(" + a.getRow() + "," + a.getCol() + ") " +
                    " E:" + a.nextEventTime + " || " +
                    " M:" + a.nextMoveTime + " I:" + a.nextInteractTime +
                    " D:" + a.timeOfDeath +
                    " A: " + a.timeAntibioticClears + "\n"); */
                System.out.printf("%s (%d, %d) E:%8.4f  M:%8.4f  I:%8.4f  L:%8.4f  C:%8.4f  S:%8.4f  D:%8.4e\n", 
                                  h.getId(), h.getCol(), h.getRow(), h.getNextEventTime(), h.getNextMoveTime(),
                                  h.getNextInteractTime(), h.getTimeNextLaundry(), h.getTimeNextClothesChange(),
                                  h.getTimeNextShower(), h.getTimeOfDeath());
            } else {
                Louse v = (Louse) a;
                Host h = v.getHost();
                String hostID = h.getId();
                //System.out.printf("%s [%s] E:%8.4f  M:%8.4f  E:%8.4f  R:%8.4f  O:%8.4f  D:%8.4f\n",
                System.out.printf("%s (%d, %d) E:%8.4f  M:%8.4f  O:%8.4f  D:%8.4f\n",
                                  v.getId(), hostID, v.getNextEventTime(), v.getNextMoveTime(),
                                  //v.getTimeNextFeeding(), v.getTimeFeedingEnds(),
                                  v.getTimeNextOviposition(), v.getTimeOfDeath());
            }
        }
    }

    //======================================================================
    //* public void reset()
    //* Just do a hard reset in terms of clearing out agents -- this is
    //* a result of the canvas changing its grid dimensions, or other 
    //* parameter values changing. Called from AgentCanvas.updateGrid()
    //======================================================================
    public void reset()
    {
        // Because we superimpose a grid on the background, let's make sure
        // that the last row/col will look right with respect to the width of
        // the agents -- bump the width/height of the image up to the next
        // multiple of the agentGUISize parameter.
        //agentQueue.clear();  // change background, must start again
        agentList.clear();
        shelters.reset();
        
        setNumInfestedHosts(0);
        numUninfestedHosts = 0;
        numInfectedHosts = 0;
        numUninfectedHosts = 0;
        numInfectedVectors = 0;
        numUninfectedVectors = 0;
        numTreatedHosts = 0;
    }

    //======================================================================
    //* private void placeAgentsAtRandom()
    //* Just drop the agents at random onto an unoccupied spot in the grid.
    //======================================================================
    private void placeAgentsAtRandom()
    {
        // any previous information of where the agents are will be wiped
        // out by this -- OK, because we will drop them all at random below
        int gridWidth = canvas.getGridWidth();
        int gridHeight = canvas.getGridHeight();
        
        // This doesn't really work if you can have multiple hosts at a given
        // location - need to refer to AgentCanvas Shelter grid instead
        //occupied = new Host[gridHeight][gridWidth];

        // int numAgents = agentQueue.size();
        //for (int i = 0; i < numAgents; i++)
        //for ( AbstractAgent agent : agentQueue )
        for ( AbstractAgent agent : agentList )
        {
            // grab an agent...
            //AbstractAgent agent = agentQueue.get(i);
            
            // Skip InfectionVectors - they don't need to be placed
            if ( ! ( agent instanceof Host ) ) {
                continue;
            }

            Host h = (Host) agent;
            
            boolean done = false;
            while (!done)
            {
                // pick a (row,col) location at random...
                int row = rng.nextInt(gridHeight); // an int in [0,gridWidth-1]
                int col = rng.nextInt(gridWidth);  // an int in [0,gridHeight-1]
                
                // get a reference to the shelter at that location...
                Shelter s = shelters.getShelterAt( col, row );

                // and if not full, plop the agent there
                if ( s.add( h ) )
                {
                    done = true;
                    h.setRowCol(row, col); // Ever used?
                    //h.setCurrentShelter( s );
                }
            }
        }
        
    } // end of placeAgentsAtRandom()
    
    /**
     * Interaction between two hosts. Doesn't apply to Vectors.
     * @param host Host that the interaction event occurred for 
     * @param time  Time at which the event occurred
     */
    private void interact( AbstractHost host, double time ) {
        // Hosts pick another person in their location to interact with.
        // Model whether lice are exchanged during the interaction.
        
        // We aren't modeling interaction between lice, so this should
        // only be called with Host instances
        Host h = (Host) host;
        Shelter s = h.getCurrentShelter();
        
        // Can't interact if no one else is around
        if ( s != null && s.getCurrentOccupancy() > 1 ) {
            double prob = rng.nextDouble();
            
            // Only bother if the interaction would result in an exchange
            // of vectors
            if ( prob < Parameters.getProbabilityLouseTransfer() ){
                Host otherHost = h;
                while ( h == otherHost ){
                    // Pick a host at random until it's someone else
                    int hostIdx = rng.nextInt(s.getCurrentOccupancy());
                    otherHost = s.getOccupant( hostIdx );
                }
                
                transferLice( h, otherHost );
                
                // We haven't accounted for all the ways lice die, so this
                // is kind of a catch-all way to keep infestations from
                // growing without bound.
                checkInfestationCap( time, h );
                checkInfestationCap( time, otherHost );
            }
        }
        
        // Decide whether this interaction
                
        host.setNextInteractTime();
    }
    
    private void transferLice( Host from, Host to )
    {
        double pctToTransfer = 
                Uniform( Parameters.getPercentageLiceTransferredMin(),
                         Parameters.getPercentageLiceTransferredMax() );

        // Assume transfer is one way, just to simplify things
        int louseCount = from.getInfestationSize();
        int numLiceToTransfer = (int) ( louseCount * 
                pctToTransfer);
        int transferred = 0;

        while ( transferred < numLiceToTransfer ){
            Louse v = from.getRandomVector();
            boolean foundHatchedLouse = false;
            
            while( ! foundHatchedLouse ){
                if ( v.getLifeStage() != Louse.EGG )
                    foundHatchedLouse = true;
                else
                    v = from.getRandomVector();
            }
            
            if ( !v.getInfected() && from.getInfected()) {
                if ( probabilityMet(
                        Parameters.getProbabilityInfectedByFeeding())){
                    v.setInfected(true);
                }
            }
            from.removeVector(v);
            if ( ! from.getInfested() ){
                Shelter s = from.getCurrentShelter();
                s.decrementInfested();
            }
            
            to.addVector(v);
            v.setHost( to );
            v.setClothesSet( to.getCurrentClothesSet() );
            int toLouseCount = to.getInfestationSize();
            if ( toLouseCount == 1 ){
                // Host was newly infested.
                Shelter s = to.getCurrentShelter();
                s.incrementInfested();
                // Should happen in addVector() - to.setInfested( true );
            }
            transferred++;
        }
    }

    //======================================================================
    //* private void checkForTreatment(Agent agent)
    //* Method to see if this agent should be treated.
    //======================================================================
    private void checkForTreatment(AbstractHost agent)
    {
        // if agent is not already treated with an antibiotic, do so @ random;
        // but, we want to allow for a delay in antibiotic application (based
        // on symptoms arising)
        if (agent.getInfected() && !agent.getTreated() &&
            rng.nextDouble() < Parameters.getProbTreatment())
        {
            assert( agent.getTimeOfInfection() != Double.MAX_VALUE );

            // this may have been an agent who was unlucky enough to not be AB
            // treated for a long time since infection;  if so, we need to
            // make sure that the time of treatment is not sometime in the
            // past; in most cases, though, it will be a time in the future
            double earliestTreatmentTime = 
              Math.max(
                agent.getTimeOfInfection() + SimulationManager.Uniform( 
                      Parameters.getTimeToTransmissibilityMin(),
                      Parameters.getTimeToTransmissibilityMax() ),
                time );

            // schedule for the agent to be treated
            agent.setTimeOfTreatment( earliestTreatmentTime );
        }

    } // end of checkForTreatment

    //======================================================================
    //* private void moveHost(Agent agent)
    //* Method that has the agent pick a direction at random and then
    //* move as far in that direction as its FOV allows without stepping
    //* on another agent's head.
    //======================================================================
    private void moveHost(AbstractAgent agent)
    {
        Host h = (Host) agent;
        
        // Save a reference to the old shelter.
        Shelter oldShelter = h.getCurrentShelter();
        
        if ( h == null || oldShelter == null )
            return;
        
        //h.setCurrentShelter( null );

        // pick a direction (N,S,E,W,NE,SE,NW,SW) at random
        int direction = rng.nextInt(8);
        // pick a random distance to move (but at least one step)
        int distance = rng.nextInt(Parameters.getFieldOfView()) + 1;

        int row = h.getRow();
        int col = h.getCol();

        int gridHeight = canvas.getGridHeight();
        int gridWidth = canvas.getGridWidth();
        
        // now move as far in that direction as possible, choosing the
        // farthest unoccupied cell within the distance
        int toRow = 0, toCol = 0;
        boolean done = false;
        do {
            switch (direction)
            {
                case 0:  // N
                    toRow = row - distance;  toCol = col;             break;
                case 1:  // S
                    toRow = row + distance;  toCol = col;             break;
                case 2:  // E
                    toRow = row;             toCol = col + distance;  break;
                case 3:  // W
                    toRow = row;             toCol = col - distance;  break;
                case 4:  // NE
                    toRow = row - distance;  toCol = col + distance;  break;
                case 5:  // SE
                    toRow = row + distance;  toCol = col + distance;  break;
                case 6:  // NW
                    toRow = row - distance;  toCol = col - distance;  break;
                case 7:  // SW
                    toRow = row + distance;  toCol = col - distance;  break;
            }

            // treat the landscape as a torus, so that an agent falling off the 
            // bottom reappears at the top and so on
            toRow = (toRow + gridHeight) % gridHeight;
            toCol = (toCol + gridWidth)  % gridWidth;
            Shelter s = shelters.getShelterAt( toCol, toRow);

            if (  ! s.isFull() ) // not full
            {
                oldShelter.remove( h );
                s.add( h );
                
                // move the agent
                h.setRowCol(toRow, toCol);
                done = true;
            }
            else
            {
                // try one cell closer
                distance--;
            }

        } while (!done && distance > 0);

    } // end of moveHost()
    
    /**
     * Move a louse. Only done if the louse isn't currently
     * living in someone's clothes. 
     * 
     * @param v 
     */
    private void moveVector( AbstractAgent agent, double time ) {
        Louse v = (Louse) agent;
        
        // If currently associated with a host and in the set of clothes 
        // being worn, just reschedule.
        Host h = v.getHost();
        if ( h != null ) {
            // Check to see which clothes set this louse belongs to
            int myClothesSet = v.getClothesSet();
            int hostWearing = h.getCurrentClothesSet();
            
            // Only move if not in the clothes the host is currrently wearing
            if ( myClothesSet != hostWearing ){
                // Leaving current host
                // Check to see if this vector should be infected

                if ( !v.getInfected() && h.getInfected()) {
                    if ( probabilityMet(
                            Parameters.getProbabilityInfectedByFeeding())){
                        v.setInfected(true);
                    }
                }
                h.removeVector( v );
                if ( h.getInfestationSize() == 0 ){
                    // Last louse just got out of Dodge. Update shelter stats.
                    h.getCurrentShelter().decrementInfested();
                }
                v.setHost( null );
                h.setCurrentShelter( h.getCurrentShelter() );
            }
            
            // Assume it takes one more move to find a new host
        } else {
            // See if we find another host
            Shelter s = v.getCurrentShelter();
            Host newHost = null;
            
            if ( s != null ){
                for ( int i = 0; i < s.getCurrentOccupancy(); i++ ){
                    double prob = rng.nextDouble();
                    if ( prob < Parameters.getProbabilityLouseRehosts() ){
                        newHost = s.getOccupant( i );
                        break;  // Break out of loop if you find someone
                    }
                }
            }
            
            if ( newHost != null ){
                newHost.addVector(v);
                // Assume we feed immediately after jumping aboard
                //v.setTimeNextFeeding( time + EVENT_EPSILON );
                if ( newHost.getInfestationSize() == 1 ) {
                    s.incrementInfested();
                }
                
                checkInfestationCap( time, newHost );
            } else {
                // We might die of starvation if we didn't re-host
                /*
                double deltaFeeding = time - v.getTimeOfLastMeal();
                double timeToStarvation = 
                        Uniform( Parameters.getLouseLongevityAwayFromHostMin(),
                                 Parameters.getLouseLongevityAwayFromHostMax() );
                if ( timeToStarvation < deltaFeeding ){
                    v.setTimeOfDeath(time);
                }
                */
                double prob = rng.nextDouble();
                if ( prob < Parameters.getProbUnhostedVectorStarves() ) {
                    v.setTimeOfDeath( time );
                }
            }
            
        }
        
        v.setNextMoveTime();
        // code that called us will call setNextEvent, so no need to do it here
    }


    //======================================================================
    //* private void checkInfectionStatus(Agent agent)
    //* Method to see if, presuming an infected agent,  any of the agent's
    //* vector instances becomes infected.  It may be
    //* that the agent dies (probabilistically) because of the antigen.
    //======================================================================
    private void checkInfectionStatus(AbstractHost agent)
    {
        if (agent.getInfected())
        {
            // See if vectors are infected
            Host h = (Host) agent;
            
            for ( Louse v: h.getVectorList() ){
                v.checkInfection(time);
            }
            // when continuously monitoring for AB application,
            // see if it is to randomly receive treatment -- note that the
            // method below checks to see if the agent is infected
            if (Parameters.isABMonitoredContinuously())
                checkForTreatment(agent);
            
            // if the antibodies/antibiotic haven't eliminated the antigen, may die
            // B. quintana isn't usually fatal, either to Hosts or to 
            // InfectionVectors. Guess we should still check on the hosts.
            boolean goingToDie = checkForDeath(agent); 

            // this method is only called within the main simulation loop,
            // after which we immediately set the next event, so no need
            // to call the method updating the next event here
        }

    } // end of checkInfectionStatus()

    //======================================================================
    //* private void checkForDeath(Agent agent)
    //* If the agent is infected, flip a weighted coin.  Tails means death.
    //======================================================================
    private boolean checkForDeath(AbstractAgent agent)
    {
        // handle potential agent death
        double value = rng.nextDouble();
        if (value < Parameters.getProbInfectedAgentDies())
        {
            if ( DEBUG ) {
                System.out.println(agent + " dies of infection at " + time );
            }
            agent.setTimeOfDeath(time);  // agent dies now
                    // just sets up the death event to occur in run() loop
            return true;
        }
        return false;

    } // end of checkForDeath()


    //======================================================================
    //* private Agent getNextAgentToMove()
    //* Search through the list of agents and pick the one whose time to
    //* move is most imminent.
    //======================================================================
    /*
    private AbstractAgent getNextEvent()
    {
        // a linear search is not the way to do this, but it sure is easy...
        // find the next agent to do something (in simulated time)
        int    minIndex = -1;
        double minTime  = Double.MAX_VALUE;
        for (int i = 0; i < agentQueue.size(); i++)
        {
            double eventTime = agentQueue.get(i).getNextEventTime();
            if (eventTime < minTime)
            {
                minIndex = i;
                minTime = eventTime;
            }
        }

        AbstractAgent agent = (minIndex >= 0 ? agentQueue.get(minIndex) : null);
        return agent;

    } // end of getNextEvent()
    */
    
    public AbstractAgent getAgentById(String name)
    {
        AbstractAgent namedAgent = null;
        
        for (AbstractAgent a: agentList){
            if (a.getId().equalsIgnoreCase(name)){
                namedAgent = a;
                break;
            }
        }
        
        return namedAgent;
    }

    private AbstractAgent getNextEvent()
    {
        // a linear search is not the way to do this, but it sure is easy...
        // find the next agent to do something (in simulated time)
        int    minIndex = -1;
        double minTime  = Double.MAX_VALUE;
        for (int i = 0; i < agentList.size(); i++)
        {
            double eventTime = agentList.get(i).getNextEventTime();
            if (eventTime < minTime)
            {
                minIndex = i;
                minTime = eventTime;
            }
        }

        AbstractAgent agent = (minIndex >= 0 ? agentList.get(minIndex) : null);
        return agent;

    } // end of getNextEvent()

    protected Shelter getShelterAt( int row, int col ){
        return shelters.getShelterAt( col, row );
    }

    /**
     * Generates an exponentially distributed random variate with mean mu.
     * 
     * @param   mu  mean of the distribution
     * @return An exponentially distributed random variable.
     */
    protected static double Exponential(double mu)
    {
        return (-mu * Math.log(1.0 - rng.nextDouble()));
    }

    /**
     * Generates a uniformly distributed random variate with mean (a+b)/2.
     * 
     * @param   a low endpoint of range
     * @param   b high endpoint of range
     * @return  A random value uniformly distributed over the request range.
     */
    protected static double Uniform(double a, double b)
    {
        return (a + (b - a) * rng.nextDouble());
    }

    /**
     * Returns a normal (Gaussian) distributed real number.
     * NOTE: use s > 0.0
     * Uses a very accurate approximation of the normal idf due to Odeh & Evans, 
     * J. Applied Statistics, 1974, vol 23, pp 96-97.
     * 
     * @param m Mean of the desired distribution
     * @param s Standard deviation of the desired distribution
     * 
     * @return A random number drawn from the normal distribution with the
     *         supplied paramters.
     */
    protected static double Normal(double m, double s) 
    {
        final double p0 = 0.322232431088;
        final double q0 = 0.099348462606;
        final double p1 = 1.0;
        final double q1 = 0.588581570495;
        final double p2 = 0.342242088547;
        final double q2 = 0.531103462366;
        final double p3 = 0.204231210245e-1;
        final double q3 = 0.103537752850;
        final double p4 = 0.453642210148e-4;
        final double q4 = 0.385607006340e-2;
        double u, t, p, q, z;

        u = rng.nextDouble();
        if (u < 0.5) {
            t = Math.sqrt(-2.0 * Math.log(u));
        } else {
            t = Math.sqrt(-2.0 * Math.log(1.0 - u));
        }
        p = p0 + t * (p1 + t * (p2 + t * (p3 + t * p4)));
        q = q0 + t * (q1 + t * (q2 + t * (q3 + t * q4)));
        if (u < 0.5) {
            z = (p / q) - t;
        } else {
            z = t - (p / q);
        }
        return (m + s * z);
    }
    
    public static boolean probabilityMet( double probability )
    {
        double estimate = rng.nextDouble();
        
        return ( estimate < probability );
    }

    // Some simple accessor methods
    public int    getNumInfectedHumans()   { return numInfectedHosts; }
    public int    getNumUninfectedAgents() { return numUninfectedHosts; }
    public int    getNumTreatedAgents()    { return numTreatedHosts; }
    public double getTime()                { return time; }

    //======================================================================
    //* public abstract void simulationFinished();
    //* Any class extending this class must implement a simulationFinished()
    //* method.  This is our mechanism of signalling the implementing class
    //* that the simulation is done without the implementing class having
    //* to worry about threads, interrupts, etc.
    //======================================================================
    public abstract void simulationFinished();
    
    // Debugging support
    private boolean sheltersConsistent( double time ){
        boolean consistent = true;
        
        // Iterate over shelters
        int gridWidth = canvas.getGridWidth();
        int gridHeight = canvas.getGridHeight();
        
        for ( int col = 0; col < gridWidth; col++ ){
            for (int row = 0; row < gridHeight; row++ ){
                Shelter s = shelters.getShelterAt(row, col);
                
                int capacity = s.getMaxCapacity();
                int occupancy = s.getCurrentOccupancy();
                int infested = s.getInfestedCount();
                int infected = s.getInfectedCount();
                
                if ( ! s.checkConsistency() ) {
                    consistent = false;
                }
                
                if ( occupancy > capacity ||
                     infested > occupancy ||
                     infected > occupancy ||
                     occupancy < 0 ||
                     infested < 0 ||
                     infected < 0 ){
                    consistent = false;
                }
            }
        }
        
        return consistent;
    }
    
    public void printSummary(int timeStep ){
        System.out.println("\nTimestep = " + timeStep);
        int vectorCount = 0;
        //for ( AbstractAgent a: agentQueue ){
        for ( AbstractAgent a : agentList ){
            if ( a instanceof Host ) {
                Host h = (Host) a;
                int totalVectors = h.getTotalVectors();
                int lice = h.getInfestationSize();
                System.out.printf("%s: infested(%d)  infected(%d) vectors: lice:%d  nits:%d%n",
                        h.getId(), h.getInfested()?1:0, h.getInfected()?1:0, lice, totalVectors - lice );
            } else {
                vectorCount++;
            }
        }
        System.out.println("Total vector pop (lice and nits): " + vectorCount);
        System.out.println("");
    }

    /**
     * Check to see if the host's infestation has passed the cap. If so,
     * some randomly selected lice are set to die now.
     *
     * @param time Current simulation time
     */
    public boolean checkInfestationCap(double time, Host host) {
        int cap = Parameters.getInfestationCap();
        int lice = host.getInfestationSize();
        
        if ( lice <= cap) {
            return false;
        }
        
        int surplus = lice - cap;
        for (int i = 0; i < surplus; i++) {
            boolean done = false;
            while (!done) {
                Louse v = host.getRandomVector();
                if ( v.getLifeStage() != Louse.EGG ) {
                    v.setTimeOfDeath(time);
                    v.setNextEvent();
                    
                    // We've changed the next event time for this louse, 
                    // so it is in the wrong place
                    // in the queue. Remove and then add again to reorg.
                    //requeue( v );
                    done = true;
                }
            }
        }
        return true;
    }
    
    /**
     * @return the numInfestedHosts
     */
    public int getNumInfestedHosts() {
        return numInfestedHosts;
    }

    /**
     * @param numInfestedHosts the numInfestedHosts to set
     */
    public void setNumInfestedHosts(int numInfestedHosts) {
        this.numInfestedHosts = numInfestedHosts;
    }

    /**
     * @return the numUninfestedHosts
     */
    public int getNumUninfestedHosts() {
        return numUninfestedHosts;
    }

    /**
     * @param numUninfestedHosts the numUninfestedHosts to set
     */
    public void setNumUninfestedHosts(int numUninfestedHosts) {
        this.numUninfestedHosts = numUninfestedHosts;
    }
    
} // end of class SimulationManager
