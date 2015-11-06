
import javax.swing.JProgressBar;

/**
 *
 * @author lbarnett
 */
public abstract class SingleStepSimMgr extends SimulationManager {
    
    public SingleStepSimMgr()
    {
        // We have only one shelter, so we want to be able to use it 
        // to try everything out.
        Shelter s = shelters.getShelterAt(0, 0);
        s.setIsFormalShelter(true);
        s.setLaundryAvailable(true);
        s.setShowersAvailable(true);
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
     */
    //======================================================================
    public void start(int maxTime, 
                      int maxAgents, 
                      boolean animateFast,
                      AgentCanvas theCanvas
                      )
    {
        rng.setSeed( Parameters.getRNGInitialSeed() );    

        // if the user pushes start while a simulation is running, we
        // start again, which could cause strange thread conflicts; we could
        // handle this, but will rely instead on the coder to disable the
        // start button once it is pressed, only to enable once the simulation
        // thread is finished (either forcefully or naturally 'stop'ped)

        // set the instance variables using the arguments passed in
        maxSimulatedTime = maxTime;
        fastAnimation    = animateFast;
        canvas           = theCanvas;
        progressBar      = new JProgressBar();

        // Redo these because we may start multiple times.
        time = 0;

        nextHostID = 0;
        nextVectorID = 0;
        
        reset();    // Does agentList.clean() and zeros counters
        //agentList.clear();
        //agentQueue.clear();  // just in case, clear out the agent list -- if user
                            // hits run button twice, must start over

        // Build the list of agents 

        // New host infected with UTF and infested with lice.
        Host agent1 = new Host( "H" + nextHostID, true, true );
        agent1.setChangesOfClothes(1);
        agent1.setForSingleStep();
        setNumInfestedHosts(getNumInfestedHosts() + 1);
        numInfectedHosts++;
        nextHostID++;

        //agentQueue.add( agent );
        agentList.add( agent1 );

        // Uninfected louse
        Louse v1 = 
                new Louse( "V" + nextVectorID,
                                     false, agent1,
                                     Louse.CREATE_ADULT, 
                                     0.0 );
        v1.setForSingleStep();
        // Add new louse to Host's list
        agentList.add( v1 );
        nextVectorID++;

        // Infected louse
        Louse v2 = 
                new Louse( "V" + nextVectorID,
                                     true, agent1,
                                     Louse.CREATE_ADULT, 
                                     0.0 );
        v2.setForSingleStep();
        // Add new louse to Host's list
        agentList.add( v2 );
        nextVectorID++;

        // New host infected with UTF and infested with lice.
        Host agent2 = new Host( "H" + nextHostID, true, false );
        setNumInfestedHosts(getNumInfestedHosts() + 1);
        agent2.setForSingleStep();
        nextHostID++;

        //agentQueue.add( agent );
        agentList.add( agent2 );

        // Uninfected louse
        Louse v3 = 
                new Louse( "V" + nextVectorID,
                                     false, agent2,
                                     Louse.CREATE_ADULT, 
                                     0.0 );
        v3.setForSingleStep();
        // Add new louse to Host's list
        agentList.add( v3 );
        nextVectorID++;

        // Infected louse
        Louse v4 = 
                new Louse( "V" + nextVectorID,
                                     true, agent2,
                                     Louse.CREATE_ADULT, 
                                     0.0 );
        v4.setForSingleStep();
        // Add new louse to Host's list
        agentList.add( v4 );
        nextVectorID++;

        setNumUninfestedHosts(0);
        //setNumUninfestedHosts(totalAgents - getNumInfestedHosts());

        // place all agents at random and then draw them
        // placeAgentsAtRandom();
        // canvas.repaint();
        Shelter s = shelters.getShelterAt( 0, 0 );
        s.add(agent1);
        s.add(agent2);
        canvas.repaint();

        // initialize the simulation and start a new Thread for executing the
        // simulation code
        state = PAUSED;
        thisThread = new Thread(this);
        thisThread.start();
    }

    //======================================================================
    //* public abstract void simulationFinished();
    //* Any class extending this class must implement a simulationFinished()
    //* method.  This is our mechanism of signalling the implementing class
    //* that the simulation is done without the implementing class having
    //* to worry about threads, interrupts, etc.
    //======================================================================
    public abstract void simulationFinished();

}
