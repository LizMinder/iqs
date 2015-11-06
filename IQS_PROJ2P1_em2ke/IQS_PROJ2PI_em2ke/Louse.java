
/**
 * Class describing the behavior of a louse in the Urban Trench Fever simulation.
 * 
 * @author Liz Minder
 * @version 10/21/15
 */
import java.util.ArrayList;

public class Louse extends AbstractVector
{
    //The Host that the vector infects
    Host h;

    //The clothes set that the vector is living in
    int clothesSet;

    //Debug flag
    private final boolean debug = true;

    /**
     * Constructor for objects of class Louse
     */
    public Louse( String id, boolean isInfected, Host currentHost, int creationMode, 
    double creationTime )
    {
        super(id, isInfected, creationMode, creationTime);
        
        h = currentHost;
        
        if (debug) {
            System.out.println("Louse created.");
        }
    }

    /**
     * Check to see if this Louse has become infected since the last check.
     * 
     * 
     * @return True if the Host became infected since the last time the method was called,
     * false if otherwise.
     */
    public boolean checkInfection()
    {
        return false;
    }

    /** 
     * Called when a vector hatches from an egg
     */
    public void hatch()
    {
    }

    /** 
     * Called when a vector matures into an adult
     */
    public void mature()
    {
    }

    /** 
     * @returns the gender of the vector
     */
    public int getGender()
    {
        return gender;
    }

    /** 
     * @returns a reference to the Host that the vector infests
     */
    public Host getHost()
    {
        return h;
    }

    /** 
     * @Changes the value of the host that the vector belongs to
     */
    public void setHost(Host myHost)
    {
        h = myHost;
    }

    /** 
     * @returns the value of the vector's life stage
     */
    public int getLifeStage()
    {
        return lifeStage;
    }

    /** 
     * Changes the value of the vector's life stage
     */
    public void setLifeStage(int newLifeStage)
    {
        lifeStage = newLifeStage;
    }

    /** 
     * @returns the index for the set of clothes that the vector lives in
     */
    public int getClothesSet()
    {
        return clothesSet;
    }

    /** 
     * Changes the clothes set that the vector belongs to
     */
    public void setClothesSet(int newClothesSet)
    {
        clothesSet= newClothesSet;
    }

    /** 
     * Things that should happen when a vector dies
     */
    public void die()
    {
    }
}
