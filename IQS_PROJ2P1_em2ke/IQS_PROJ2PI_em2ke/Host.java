/**
 * Class describing the behavior of a human host in the Urban Trench Fever simulation.
 * 
 * @author Liz Minder
 * @version 10/19/2015
 */

import java.util.ArrayList;

public class Host extends AbstractHost
{
    //Infestation status of host: true if lice are present, false if lice are absent.
    private boolean infested;

    //Treatment status of host: true if host has been treated, false if not.
    private boolean treated;

    //Keeps track of lice on host.
    private ArrayList<Louse> lice;

    //Changing status of host: true if host has changed, false if not.
    private boolean changed;

    //Returns how many sets of clothes the agent has
    int changesOfClothes;

    //The set of clothes currently being worn by the Host
    int currentClothesSet;

    //The number of non-egg lice
    int infestationSize;

    //The number of all vectors
    int totalVectors;

    //A randomly selected vector
    Louse randomVector;

    //Debugging flag
    private final boolean debug = true;

    
    /**
     * Constructor for objects of class Host
     */
    public Host(String id, boolean isInfested, boolean isInfected)
    {
        super(id, isInfected);

        infested = isInfested;
        treated = false;

        lice = new ArrayList<Louse>();

        if (debug) {
            System.out.println("Host created.");
        }
    }

    /** 
     * Check to see if this Host has become infected since the last check.
     * 
     * @return True if this Host became infected since the last time the method was called, 
     *         false otherwise.
     */
    public boolean checkInfection()
    {
        return false;
    }

    /**
     * Treatment status of the Host.
     */
    public boolean treatAgent()
    {
        return true;
    }

    /**
     * Check to see if this Host has been treated with an Antibiotic.
     * 
     * @return True if treated, false otherwise.
     */
    public boolean getTreated() //Accessor method
    {
        return treated;
    }

    /**
     * Changes treatment status of the host.
     */
    public void setTreated( boolean newTreatedStatus )//Mutator method - change instance variable
    {
        this.treated = newTreatedStatus;
    }

    /**
     * Shows which lice are currently on the host
     */
    public ArrayList<Louse> getVectorList()
    {
        return lice;
    }

    /**
     * Adds a new louse to the host
     */
    public void addVector (Louse v)
    {
    }

    /**
     * Removes one louse from the host
     */
    public boolean removeVector (Louse v)
    {
        return true;
    }

    public void clearAntibiotic()
    {
    }

    public void changeClothes(double daysClothesStored)
    {
    }

    public void shower()
    {
    }

    public void doLaundry()
    {
    }

    public void delouse()
    {
    }

    /**
     * Check to see how many sets of clothes the Agent has
     * 
     */
    public int getChangesOfClothes()
    {
        return changesOfClothes;
    }

    /**
     * Changes the number of changes of clothes that the host is wearing.
     */
    public void setChangesOfClothes( int numChanges )//Mutator method - change instance variable
    {
        changesOfClothes = numChanges;
    }

    /**
     * Check what set of clothes the Host is currently wearing.
     */
    public int getCurrentClothesSet() 
    {
        return currentClothesSet;
    }

    /**
     * Changes the set of clothes that the host is wearing.
     */
    public void setCurrentClothesSet (int newClothesSet)
    {
        currentClothesSet = newClothesSet;
    }

    /**
     * Returns the infestation status of the host
     */
    public boolean getInfested()
    {
        return infested;
    }

    /**
     * Changes the infestation status of the host
     */

    public void setInfested (boolean newInfested)
    {
        infested = newInfested;
    }

    /**
     * Returns the number of non-egg lice
     */

    public int getInfestationSize()
    {
        return 0;
    }

    /**
     * Returns the number of all vectors
     */

    public int getTotalVectors()
    {
        return 0;
    }

    /**
     * Returns a randomly selected vector
     */

    public Louse getRandomVector()
    {
        return null;
    }

     /**
     * Changes the shelter where the Host is residing
     */

    public void setCurrentShelter(Shelter s)
    {
        currentShelter = s;
        
        if (debug) {
        System.out.println("setCurrentShelter to " + s);
       }
    }
    
    /**
     * Whatever changes are required when an egg hatches on a Host
     */

    public void vectorHatched()
    {
    }
}

