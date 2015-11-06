
import java.util.ArrayList;

/**
 * A command-line tester for the Host and InfectionVector classes.
 * This won't compile until the two classes are present and all of the
 * abstract methods inherited from their parents exist, at least as
 * stubs.
 * 
 * This tester will instantiate objects of each type and then call all
 * of the methods that are abstract in the parent classes to be sure
 * they are present.
 * 
 * @author lbarnett
 * @version 9/29/2015
 */
public class TestAgents {
    public static void main( String [] args ){
        System.out.println("Testing Host ----------------------- ");
        Host h = new Host("TestHost01", true, true);
        
        boolean retVal = h.checkInfection();
        System.out.println("checkInfection returned " + retVal + 
                ". Expected false.");
        
        retVal = h.treatAgent();
        System.out.println("treatAgent returned " + retVal + 
                ". Expected true.");
        
        // Test both possible parameter values - if we only tested
        // one, the return value could just reflect the initial value,
        // not the action of setTreated().
        h.setTreated(false);
        retVal = h.getTreated();
        System.out.println("getTreated returned " + retVal + 
                ". Expected false.");

        h.setTreated(true);
        retVal = h.getTreated();
        System.out.println("getTreated returned " + retVal + 
                ". Expected true.");
        
        h.clearAntibiotic();
        
        h.changeClothes(2);
        
        h.shower();
        h.doLaundry();
        h.delouse();
        
        // Once again, make two changes to be sure we didn't just accidentally
        // hit upon the initial value.
        h.setChangesOfClothes(1);
        int intRetVal = h.getChangesOfClothes();
        System.out.println("getChangesOfClothes returned " + intRetVal + 
                ". Expected 1.");
        
        h.setChangesOfClothes(2);
        intRetVal = h.getChangesOfClothes();
        System.out.println("getChangesOfClothes returned " + intRetVal + 
                ". Expected 2.");
        
        // This is currently an empty method, so no tests possible.
        h.setCurrentClothesSet(1);
        
        ArrayList<Louse> vecs = h.getVectorList();
        System.out.println("Size of vector list = " + vecs.size() +
                ". Expected 0.");
        
        // Empty method at this stage, so no need to create an instance
        // of infection vector. If you want to use this tester for stage 2,
        // replace null with a newly created instance of InfectionVector.
        h.addVector(null);
        
        retVal = h.removeVector(null);
        System.out.println("removeVector returned " + retVal + 
                ". Expected true.");
        
        // Constructor should have set this from parameter. We passed 
        // true for that parameter.
        retVal = h.getInfested();
        System.out.println("getInfested returned " + retVal + 
                ". Expected true.");
        
        h.setInfested(false);
        
        retVal = h.getInfested();
        System.out.println("getInfested returned " + retVal + 
                ". Expected false.");
        
        intRetVal = h.getInfestationSize();
        System.out.println("Size of infestation = " + intRetVal +
                ". Expected 0.");
        
        intRetVal = h.getTotalVectors();
        System.out.println("Total vectors = " + intRetVal +
                ". Expected 0.");
                
        h.setCurrentShelter(null);
        
        Louse v = h.getRandomVector();
        System.out.println("Random vector = " + v + ". Should be null");
        
        h.vectorHatched();
        
        System.out.println("\nTesting Louse ------------------ ");
        Louse iv = new Louse("TestVector01", true, h, 
            Louse.EGG, 0.0);
        
        
        retVal = iv.checkInfection();
       
        System.out.println("checkInfection returned " + retVal + 
                ". Expected false.");
        
        // Blank methods for Part 1, so nothing really to test.
        iv.hatch();
        iv.mature();
        
        intRetVal = iv.getGender();
        System.out.println("getGender returned " + intRetVal);
        
        Host ivHost = iv.getHost();
        // Should equal h
        if (h != ivHost){
            System.out.println("getHost: Unexpected return value");
        } else {
            System.out.println("getHost: OK");
        }
        
        Host h2 = new Host("TestHost01", true, true);
        iv.setHost(h2);

        ivHost = iv.getHost();
        // Should equal h
        if (h2 != ivHost){
            System.out.println("getHost: Unexpected return value");
        } else {
            System.out.println("getHost: OK");
        }
        
        int stage = iv.getLifeStage();
        System.out.println("getLifeStage returned " + stage +
                "  Expected " + Louse.EGG);
        
        iv.setLifeStage(Louse.LARVA);
        stage = iv.getLifeStage();
        System.out.println("getLifeStage returned " + stage +
                "  Expected " + Louse.LARVA);
        
        iv.setClothesSet(1);
        intRetVal = iv.getClothesSet();
        System.out.println("getClothesSet returned " + intRetVal +
                "  Expected " + 1);
        
        // Blank method, so nothing to test
        iv.die();
        
     }
}
