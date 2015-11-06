/*************************************************************************
 * This class provides a central location for initializing/controlling
 * the parameters for the agent-based simulation.  All instance variables
 * and all accessor and mutator methods are static.  
 *
 * Default values for all the parameters are provided below.
 ************************************************************************/

public class Parameters
{
    //*******************************************************************
    // Parameters for simulator operation
    //*******************************************************************
    private static int rngInitialSeed = 8675309;  // initial seed for the RNG
    
    private static int agentGUISize    = 50; // an agent is an mxm square
    // Normal operation
    private static int defaultGridSize = 8; // NxN square grid for agents
    // Testing
    //private static int defaultGridSize = 2; // NxN square grid for agents
    // Display the legend for the shelter grid as one long horizontal string.
    private static boolean gridLegendHorizontal = true;

    private static int fieldOfView = 3;  // how far agents see in each dir.

    // each type of event has its own arrival process, and these are the
    // average times between events for those processes
    // Hosts move daily - not sure if this is a good assumption
    private static double avgTimeBtwnHostMove     = 1.0;
    // Lice can be transferred  by casual contact for heavily infested 
    // individuals
    private static double avgTimeBtwnInteract = 3.0;
    // Lice move away from clothing not currently worn pretty quickly, so
    // we only apply this when that's the case. 
    private static double avgTimeBtwnVectorMove = 0.2;

    //*******************************************************************
    // Parameters related to Shelters
    //*******************************************************************
    
    /**
     * Percentage of shelter areas that are formal homeless shelters.
     * Others are just places where people sleep.
     */
    private static double percentFormalShelters = 0.2;
    
    /**
     * Percentage of formal shelters where showers are available
     */
    private static double percentageShowersAvailable = 0.0;
    
    /**
     * Percentage of formal shelters where laundry facilities are available
     */
    private static double percentageLaundryAvailable = 0.0;
    
    /**
     * Percentage of formal shelters where medical care is available
     */
    private static double percentageMedicalAvailable = 0.0;
    
    /**
     * Percentage of formal shelters where intake delousing is performed
     */
    private static double percentageIntakeDelousing = 0.0;
    
    /**
     * Minimum capacity of formal shelters
     */
    private static int avgShelterCapFormalMin = 5;
    
    /**
     * Maximum capacity of formal shelters
     */
    private static int avgShelterCapFormalMax = 20;
    
    /**
     * Minimum capacity of informal shelters
     */
    private static int avgShelterCapInformalMin = 1;
    
    /**
     * Maximum capacity of informal shelters
     */
    private static int avgShelterCapInformalMax = 10;
    

    //*******************************************************************
    // Parameters related to Hosts
    //*******************************************************************
    
    private static int hostPopSizeMax = 200;

    /**
     * Initial host population size
     */
    // Normal operation
    private static int hostPopulationSize = 100;
    
    // Testing
    //private static int hostPopulationSize = 10;
    
    /**
     * Initial percentage of hosts infested with lice [0, 1.0]
     */
    private static double percentOfHostsInfested = 0.2;
    
    /**
     * Initial percentage of hosts infected with B. quintana [0, 1.0]
     */
    private static double percentOfHostsInfected = 0.1;
    
    /**
     * Minimum value for Host.daysSinceInfected for a host who is infected
     * when the simulation begins.
     */
    private static double daysSinceInfectedMin = 0.0;
    
    /**
     * Maximum value for Host.daysSinceInfected for a host who is infected
     * when the simulation begins. Related to timeToTransmissibilityMin/Max
     */
    private static double daysSinceInfectedMax = 20.0;
    
    
    /**
     * Percentage of hosts who have a change of clothes [0, 1.0]
     */
    private static double percentOfHostsWithClothesChange = 0.0;
    
    /**
     * Average number of lice on an initially infested host. 18 From 
     * [Bonilla 2009]. Exponential distribution leads to some "super
     * infestations" which really bog the simulation down. We will cap
     * infestations at a certain reasonable maximum to keep things
     * tractable.
     */
    private static double avgInfestationSizeMin = 5;
    private static double avgInfestationSizeMax = 20;
    private static int    infestationCap = 30;
    
    /**
     * Probability that one or more lice transfer between agents during
     * an interaction between two human hosts.  [0, 1.0]
     */
    private static double probabilityLouseTransfer = 0.3;
    
    /**
     * Percentage of lice transferred if transfer occurs - min  [0, 1.0]
     */
    private static double percentageLiceTransferredMin = 0.07;
    
    /**
     * Percentage of lice transferred if transfer occurs - max  [0, 1.0]
     */
    private static double percentageLiceTransferredMax = 0.2;
    
    
    /**
     * Probability per infected louse that host is infected during a 
     * day. There is no published information on this value, should be
     * calibrated by experiment.  [0, 1.0]
     */
    private static double probabilityInfestedIsInfected = 0.1;
    

    /**
     * Mortality probability for infected host. This should diminish with
     * length of infection, but no data on that either.
     */
    private static double probabilityInfectedAgentDies = 0.0001;
    
    /**
     * Probability that the host is treated with appropriate antibiotics.
     */
    private static double probabilityTreatment = 0.0;
    
    private static boolean continuousABMonitoring = false;
      // when false, only one chance at AB application: upon infection
      // when true, continually checking to see if AB should be applied

    /**
     * Minimum time from infection of human host to transmissibility to 
     * louse vectors. Simulation clock runs at one day per tick, so units
     * are days. Default values from [Raoult 1999]
     */
    private static double timeToTransmissibilityMin = 15; 

    /**
     * Maximum time from infection of human host to transmissibility to 
     * louse vectors. 
     */
    private static double timeToTransmissibilityMax = 25;

    // 4-7 days
    //private static double timeToSymptomsMin  =  96.0;
    //private static double timeToSymptomsMax  = 168.0;

    // 2-3 weeks to clear with antibiotic treatment
    /**
     * Minimum length in days of antibiotic treatment
     */
    private static double treatmentLengthMin = 14;
    
    /**
     * Minimum length in days of antibiotic treatment
     */
    private static double treatmentLengthMax = 21;

    //*******************************************************************
    // Parameters related to InfectionVectors (lice)
    //*******************************************************************
    /**
     * Gender ratio for lice - value derived from [Lang 1976], which was
     * about head lice. [0, 1.0]
     */
    private static double percentFemales = 0.7;
    
    /**
     * Percentage of lice initially infected with B. quintana [0, 1.0]
     */
    private static double percentOfVectorsInfected = 0.1;
    
    /**
     * Minimum of time between vector feeding episodes, in days. Lice feed 
     * about five times a day [Raoult 1999]. In days.
     */
    //private static double timeBetweenBloodMealsMin = 0.1;
    
    /**
     * Maximum of time between vector feeding episodes, in days. 
     */
    //private static double timeBetweenBloodMealsMax = 0.3;
    
    /**
     * Probability that a louse is feeding at a given instant. They spend 
     * about 13% of their time feeding. [Fuller 1949]
     */
    private static double probFeeding = 0.13;
    
    /**
     * Probability that a vector not currenly on a host starves during 
     * a move event. I made this up.
     */
    private static double probUnhostedVectorStarves = 0.15;
    
    // One general article said "Bloodsucking may continue for a 
    // long period if the louse is not disturbed." [Fuller 1949] gave
    // ranges for beginning and ending of feeding after being placed in
    // proximity to food source. These numbers are derived from that source.
    /**
     * Minimum duration of a blood meal (days)
     */
    private static double mealDurationMin = 0.017; // approx 25 mins.
    
    /**
     * Maximum duration of a blood meal (days)
     */
    private static double mealDurationMax = 0.024; // approx 35 mins.
    
    /**
     * Minimum eggs produced per day by mature louse.
     */
    private static double eggsPerDayMin = 2;
    
    /**
     * Maximum eggs produced per day by mature louse.
     */
    private static double eggsPerDayMax = 10;
    
    /**
     * Minimum time for eggs to hatch.
     */
    private static double nitGestationTimeMin = 6.0;
    
    /**
     * Maximum time for eggs to hatch.
     */
    private static double nitGestationTimeMax = 9.0;
    
    /**
     * Mean time from hatch to adult stage (days) [Evans 1952]
     */
    private static double meanTimeToAdult = 12.81;
    
    /**
     * Standard deviation of hatch to adult period (days) [Evans 1952]
     */
    private static double stDevTimeToAdult = 0.67;
    
    /**
     * Average of natural louse longevity (days). Loosely based on [Evans 1952]
     */
    private static double louseLongevityMean = 17.6;
    
    /**
     * Standard deviation of natural louse longevity (days). [Evans 1952]
     */
    private static double louseLongevityStDev = 8.6;
    
    /**
     * Minimum louse longevity away from host (days)
     */
    private static double louseLongevityAwayFromHostMin = 1.0; 
    
    /**
     * Maximum louse longevity away from host
     */
    private static double louseLongevityAwayFromHostMax = 2.0;
    
    /**
     * Minimum days of nit viability in unused clothing
     */
    private static double nitLongevityAwayFromHostMin = 6.0;
    
    /**
     * Maximum days of nit viability in unused clothing
     */
    private static double nitLongevityAwayFromHostMax = 8.0;
    
    /**
     * Probability that a louse feeding on an infected host becomes infected.
     */
    private static double probabilityInfectedByFeeding = 0.6;
    
    /**
     * Probability a feeding louse is removed by grooming
     */
    private static double probabilityRemovedByGrooming = 0.65;
    
    /**
     * Probability a "free louse" (not associated with any host) finds
     * a new host. This is a probability per host at a location
     */
    private static double probabilityLouseRehosts = 0.03;
    
    //************************************************************
    //* ACESSOR METHODS BELOW -- just return appropriate value
    //************************************************************

    public static int getRNGInitialSeed()      { return rngInitialSeed; }
    public static int getAgentGUISize()        { return agentGUISize; }
    public static int getDefaultGridSize()     { return defaultGridSize; }

    public static double getProbInfectedAgentDies()        
    { 
        return getProbabilityInfectedAgentDies(); 
    }
    
    public static double getProbTreatment()                
    { 
        return getProbabilityTreatment(); 
    }

    public static double getTreatmentLengthMin()  
    { 
        return treatmentLengthMin; 
    }
    
    public static double getTreatmentLengthMax()  
    { 
        return treatmentLengthMax; 
    }

    public static int    getFieldOfView() { return fieldOfView; }

    public static double getAvgTimeBtwnHostMove()
    { 
        return avgTimeBtwnHostMove; 
    }
    
    public static double getAvgTimeBtwnVectorMove()     
    { 
        return avgTimeBtwnVectorMove; 
    }
    
    public static double getAvgTimeBtwnInteract() 
    { 
        return avgTimeBtwnInteract; 
    }

    public static boolean isABMonitoredContinuously() 
    { 
        return continuousABMonitoring; 
    }

    //******************************************************************
    //* MUTATOR METHODS BELOW -- set appropriate value using given input
    //******************************************************************

    public static void setRNGInitialSeed(int seed)   { rngInitialSeed = seed; }

    public static void setAgentGUISize(int size)    { agentGUISize = size; }
    public static void setDefaultGridSize(int size) { defaultGridSize = size; }

    public static void setProbInfectedAgentDies(double prob) 
    { 
        probabilityInfectedAgentDies = prob; 
    }
    
    public static void setProbTreatment(double prob)         
    { 
        probabilityTreatment = prob; 
    }

    public static void setTreatmentLengthMin(double min) 
    { 
        treatmentLengthMin = min; 
    }
    
    public static void setTreatmentLengthMax(double max) 
    { 
        treatmentLengthMax = max; 
    }

    public static void setFieldOfView(int fov) { fieldOfView = fov; }

    public static void setAvgTimeBtwnMove(double time)     
    { 
        avgTimeBtwnHostMove = time; 
    }
    
    public static void setAvgTimeBtwnInteract(double time) 
    { 
        avgTimeBtwnInteract = time; 
    }
    

    public static void setABMonitoredContinuously(boolean isIt) 
    { 
        continuousABMonitoring = isIt; 
    }

    /**
     * @return the hostPopulationSize
     */
    public static int getHostPopulationSize() {
        return hostPopulationSize;
    }

    /**
     * @param aHostPopulationSize the hostPopulationSize to set
     */
    public static void setHostPopulationSize(int aHostPopulationSize) {
        hostPopulationSize = aHostPopulationSize;
    }

    /**
     * @return the percentOfHostsInfested
     */
    public static double getPercentOfHostsInfested() {
        return percentOfHostsInfested;
    }

    /**
     * @param aPercentOfHostsInfested the percentOfHostsInfested to set
     */
    public static void setPercentOfHostsInfested(
            double aPercentOfHostsInfested) {
        percentOfHostsInfested = aPercentOfHostsInfested;
    }

    /**
     * @return the percentOfHostsInfected
     */
    public static double getPercentOfHostsInfected() {
        return percentOfHostsInfected;
    }

    /**
     * @param aPercentOfHostsInfected the percentOfHostsInfected to set
     */
    public static void setPercentOfHostsInfected(
            double aPercentOfHostsInfected) {
        percentOfHostsInfected = aPercentOfHostsInfected;
    }

    /**
     * @return the percentOfHostsWithClothesChange
     */
    public static double getPercentOfHostsWithClothesChange() {
        return percentOfHostsWithClothesChange;
    }

    /**
     * @param aPercentOfHostsWithClothesChange the 
     *  percentOfHostsWithClothesChange to set
     */
    public static void setPercentOfHostsWithClothesChange(
            double aPercentOfHostsWithClothesChange) {
        percentOfHostsWithClothesChange = aPercentOfHostsWithClothesChange;
    }

    /**
     * @return the percentOfVectorsInfected
     */
    public static double getPercentOfVectorsInfected() {
        return percentOfVectorsInfected;
    }

    /**
     * @param aPercentOfVectorsInfected the percentOfVectorsInfected to set
     */
    public static void setPercentOfVectorsInfected(
            double aPercentOfVectorsInfected) {
        percentOfVectorsInfected = aPercentOfVectorsInfected;
    }

    /**
     * @return the probabilityLouseTransfer
     */
    public static double getProbabilityLouseTransfer() {
        return probabilityLouseTransfer;
    }

    /**
     * @param aProbabilityLouseTransfer the probabilityLouseTransfer to set
     */
    public static void setProbabilityLouseTransfer(
            double aProbabilityLouseTransfer) {
        probabilityLouseTransfer = aProbabilityLouseTransfer;
    }

    /**
     * @return the percentageLiceTransferredMin
     */
    public static double getPercentageLiceTransferredMin() {
        return percentageLiceTransferredMin;
    }

    /**
     * @param aPercentageLiceTransferredMin the percentageLiceTransferredMin to set
     */
    public static void setPercentageLiceTransferredMin(
            double aPercentageLiceTransferredMin) {
        percentageLiceTransferredMin = aPercentageLiceTransferredMin;
    }

    /**
     * @return the percentageLiceTransferredMax
     */
    public static double getPercentageLiceTransferredMax() {
        return percentageLiceTransferredMax;
    }

    /**
     * @param aPercentageLiceTransferredMax the percentageLiceTransferredMax to set
     */
    public static void setPercentageLiceTransferredMax(
            double aPercentageLiceTransferredMax) {
        percentageLiceTransferredMax = aPercentageLiceTransferredMax;
    }

    /**
     * @return the probabilityInfestedIsInfected
     */
    public static double getProbabilityInfestedIsInfected() {
        return probabilityInfestedIsInfected;
    }

    /**
     * @param aProbabilityInfestedIsInfected the probabilityInfestedIsInfected 
     *     to set
     */
    public static void setProbabilityInfestedIsInfected(
            double aProbabilityInfestedIsInfected) {
        probabilityInfestedIsInfected = aProbabilityInfestedIsInfected;
    }

    /**
     * @return the eggsPerDayMin
     */
    public static double getEggsPerDayMin() {
        return eggsPerDayMin;
    }

    /**
     * @return the eggsPerDayMax
     */
    public static double getEggsPerDayMax() {
        return eggsPerDayMax;
    }

    /**
     * @return the nitGestationTimeMin
     */
    public static double getNitGestationTimeMin() {
        return nitGestationTimeMin;
    }

    /**
     * @return the nitGestationTimeMax
     */
    public static double getNitGestationTimeMax() {
        return nitGestationTimeMax;
    }

    /**
     * @return the meanTimeToAdult
     */
    public static double getMeanTimeToAdult() {
        return meanTimeToAdult;
    }

    /**
     * @return the stDevTimeToAdult
     */
    public static double getStDevTimeToAdult() {
        return stDevTimeToAdult;
    }

    /**
     * @return the louseLongevityMean
     */
    public static double getLouseLongevityMean() {
        return louseLongevityMean;
    }

    /**
     * @return the louseLongevityStDev
     */
    public static double getLouseLongevityStDev() {
        return louseLongevityStDev;
    }

    /**
     * @return the louseLongevityAwayFromHostMin
     */
    public static double getLouseLongevityAwayFromHostMin() {
        return louseLongevityAwayFromHostMin;
    }

    /**
     * @return the louseLongevityAwayFromHostMax
     */
    public static double getLouseLongevityAwayFromHostMax() {
        return louseLongevityAwayFromHostMax;
    }

    /**
     * @return the nitLongevityAwayFromHostMin
     */
    public static double getNitLongevityAwayFromHostMin() {
        return nitLongevityAwayFromHostMin;
    }

    /**
     * @return the nitLongevityAwayFromHostMax
     */
    public static double getNitLongevityAwayFromHostMax() {
        return nitLongevityAwayFromHostMax;
    }

    /**
     * @return the probabilityInfectedByFeeding
     */
    public static double getProbabilityInfectedByFeeding() {
        return probabilityInfectedByFeeding;
    }

    /**
     * @param aProbabilityInfectedByFeeding the probabilityInfectedByFeeding 
     *  to set
     */
    public static void setProbabilityInfectedByFeeding(
            double aProbabilityInfectedByFeeding) {
        probabilityInfectedByFeeding = aProbabilityInfectedByFeeding;
    }

    /**
     * @return the probabilityInfectedAgentDies
     */
    public static double getProbabilityInfectedAgentDies() {
        return probabilityInfectedAgentDies;
    }

    /**
     * @param aProbabilityInfectedAgentDies the probabilityInfectedAgentDies 
     * to set
     */
    public static void setProbabilityInfectedAgentDies(
            double aProbabilityInfectedAgentDies) {
        probabilityInfectedAgentDies = aProbabilityInfectedAgentDies;
    }

    /**
     * @return the probabilityTreatment
     */
    public static double getProbabilityTreatment() {
        return probabilityTreatment;
    }

    /**
     * @param aProbabilityTreatment the probabilityTreatment to set
     */
    public static void setProbabilityTreatment(double aProbabilityTreatment) {
        probabilityTreatment = aProbabilityTreatment;
    }

    /**
     * @return the timeToTransmissibilityMin
     */
    public static double getTimeToTransmissibilityMin() {
        return timeToTransmissibilityMin;
    }

    /**
     * @return the timeToTransmissibilityMax
     */
    public static double getTimeToTransmissibilityMax() {
        return timeToTransmissibilityMax;
    }

    /**
     * @return the daysSinceInfectedMin
     */
    public static double getDaysSinceInfectedMin() {
        return daysSinceInfectedMin;
    }

    /**
     * @param aDaysSinceInfectedMin the daysSinceInfectedMin to set
     */
    public static void setDaysSinceInfectedMin(double aDaysSinceInfectedMin) {
        daysSinceInfectedMin = aDaysSinceInfectedMin;
    }

    /**
     * @return the daysSinceInfectedMax
     */
    public static double getDaysSinceInfectedMax() {
        return daysSinceInfectedMax;
    }

    /**
     * @param aDaysSinceInfectedMax the daysSinceInfectedMax to set
     */
    public static void setDaysSinceInfectedMax(double aDaysSinceInfectedMax) {
        daysSinceInfectedMax = aDaysSinceInfectedMax;
    }

    /**
     * @return the mealDurationMin
     */
    public static double getMealDurationMin() {
        return mealDurationMin;
    }

    /**
     * @param aMealDurationMin the mealDurationMin to set
     */
    public static void setMealDurationMin(double aMealDurationMin) {
        mealDurationMin = aMealDurationMin;
    }

    /**
     * @return the mealDurationMax
     */
    public static double getMealDurationMax() {
        return mealDurationMax;
    }

    /**
     * @param aMealDurationMax the mealDurationMax to set
     */
    public static void setMealDurationMax(double aMealDurationMax) {
        mealDurationMax = aMealDurationMax;
    }

    /**
     * @return the percentFemales
     */
    public static double getPercentFemales() {
        return percentFemales;
    }

    /**
     * @param aPercentFemales the percentFemales to set
     */
    public static void setPercentFemales(double aPercentFemales) {
        percentFemales = aPercentFemales;
    }

    /**
     * @return the probabilityRemovedByGrooming
     */
    public static double getProbabilityRemovedByGrooming() {
        return probabilityRemovedByGrooming;
    }

    /**
     * @param aProbabilityRemovedByGrooming the probabilityRemovedByGrooming 
     *      to set
     */
    public static void setProbabilityRemovedByGrooming(
            double aProbabilityRemovedByGrooming) {
        probabilityRemovedByGrooming = aProbabilityRemovedByGrooming;
    }

    /**
     * @return the probabilityLouseRehosts
     */
    public static double getProbabilityLouseRehosts() {
        return probabilityLouseRehosts;
    }

    /**
     * @param aProbabilityLouseRehosts the probabilityLouseRehosts to set
     */
    public static void setProbabilityLouseRehosts(
            double aProbabilityLouseRehosts) {
        probabilityLouseRehosts = aProbabilityLouseRehosts;
    }

    /**
     * @return the percentFormalShelters
     */
    public static double getPercentFormalShelters() {
        return percentFormalShelters;
    }

    /**
     * @param aPercentFormalShelters the percentFormalShelters to set
     */
    public static void setPercentFormalShelters(double aPercentFormalShelters) {
        percentFormalShelters = aPercentFormalShelters;
    }

    /**
     * @return the percentageShowersAvailable
     */
    public static double getPercentageShowersAvailable() {
        return percentageShowersAvailable;
    }

    /**
     * @param aPercentageShowersAvailable the percentageShowersAvailable to set
     */
    public static void setPercentageShowersAvailable(
            double aPercentageShowersAvailable) {
        percentageShowersAvailable = aPercentageShowersAvailable;
    }

    /**
     * @return the percentageLaundryAvailable
     */
    public static double getPercentageLaundryAvailable() {
        return percentageLaundryAvailable;
    }

    /**
     * @param aPercentageLaundryAvailable the percentageLaundryAvailable to set
     */
    public static void setPercentageLaundryAvailable(
            double aPercentageLaundryAvailable) {
        percentageLaundryAvailable = aPercentageLaundryAvailable;
    }

    /**
     * @return the percentageMedicalAvailable
     */
    public static double getPercentageMedicalAvailable() {
        return percentageMedicalAvailable;
    }

    /**
     * @param aPercentageMedicalAvailable the percentageMedicalAvailable to set
     */
    public static void setPercentageMedicalAvailable(
            double aPercentageMedicalAvailable) {
        percentageMedicalAvailable = aPercentageMedicalAvailable;
    }

    /**
     * @return the avgShelterCapFormalMin
     */
    public static int getAvgShelterCapFormalMin() {
        return avgShelterCapFormalMin;
    }

    /**
     * @param aAvgShelterCapFormalMin the avgShelterCapFormalMin to set
     */
    public static void setAvgShelterCapFormalMin(int aAvgShelterCapFormalMin) {
        avgShelterCapFormalMin = aAvgShelterCapFormalMin;
    }

    /**
     * @return the avgShelterCapFormalMax
     */
    public static int getAvgShelterCapFormalMax() {
        return avgShelterCapFormalMax;
    }

    /**
     * @param aAvgShelterCapFormalMax the avgShelterCapFormalMax to set
     */
    public static void setAvgShelterCapFormalMax(int aAvgShelterCapFormalMax) {
        avgShelterCapFormalMax = aAvgShelterCapFormalMax;
    }

    /**
     * @return the avgShelterCapInformalMin
     */
    public static int getAvgShelterCapInformalMin() {
        return avgShelterCapInformalMin;
    }

    /**
     * @param aAvgShelterCapInformalMin the avgShelterCapInformalMin to set
     */
    public static void setAvgShelterCapInformalMin(
            int aAvgShelterCapInformalMin) {
        avgShelterCapInformalMin = aAvgShelterCapInformalMin;
    }

    /**
     * @return the avgShelterCapInformalMax
     */
    public static int getAvgShelterCapInformalMax() {
        return avgShelterCapInformalMax;
    }

    /**
     * @param aAvgShelterCapInformalMax the avgShelterCapInformalMax to set
     */
    public static void setAvgShelterCapInformalMax(
            int aAvgShelterCapInformalMax) {
        avgShelterCapInformalMax = aAvgShelterCapInformalMax;
    }

    /**
     * @return the avgInfestationSizeMin
     */
    public static double getAvgInfestationSizeMin() {
        return avgInfestationSizeMin;
    }

    /**
     * @param aAvgInfestationSizeMin the avgInfestationSizeMin to set
     */
    public static void setAvgInfestationSizeMin(double aAvgInfestationSizeMin) {
        avgInfestationSizeMin = aAvgInfestationSizeMin;
    }

    /**
     * @return the avgInfestationSizeMax
     */
    public static double getAvgInfestationSizeMax() {
        return avgInfestationSizeMax;
    }

    /**
     * @param aAvgInfestationsizeMax the avgInfestationSizeMax to set
     */
    public static void setAvgInfestationSizeMax(double aAvgInfestationsizeMax) {
        avgInfestationSizeMax = aAvgInfestationsizeMax;
    }

    /**
     * @return the infestationCap
     */
    public static int getInfestationCap() {
        return infestationCap;
    }

    /**
     * @param aInfestationCap the infestationCap to set
     */
    public static void setInfestationCap(int aInfestationCap) {
        infestationCap = aInfestationCap;
    }

    /**
     * @return the probFeeding
     */
    public static double getProbFeeding() {
        return probFeeding;
    }

    /**
     * @param aProbFeeding the probFeeding to set
     */
    public static void setProbFeeding(double aProbFeeding) {
        probFeeding = aProbFeeding;
    }

    /**
     * @return the probUnhostedVectorStarves
     */
    public static double getProbUnhostedVectorStarves() {
        return probUnhostedVectorStarves;
    }

    /**
     * @param aProbUnhostedVectorStarves the probUnhostedVectorStarves to set
     */
    public static void setProbUnhostedVectorStarves(
            double aProbUnhostedVectorStarves) {
        probUnhostedVectorStarves = aProbUnhostedVectorStarves;
    }

    /**
     * @return the hostPopSizeMax
     */
    public static int getHostPopSizeMax() {
        return hostPopSizeMax;
    }

    /**
     * @param aHostPopSizeMax the hostPopSizeMax to set
     */
    public static void setHostPopSizeMax(int aHostPopSizeMax) {
        hostPopSizeMax = aHostPopSizeMax;
    }

    /**
     * @return the gridLegendHorizontal
     */
    public static boolean isGridLegendHorizontal() {
        return gridLegendHorizontal;
    }

    /**
     * @param aGridLegendHorizontal the gridLegendHorizontal to set
     */
    public static void setGridLegendHorizontal(boolean aGridLegendHorizontal) {
        gridLegendHorizontal = aGridLegendHorizontal;
    }

    /**
     * Access percentage of formal shelters performing intake delousing.
     * 
     * @return the percentageIntakeDelousing
     */
    public static double getPercentageIntakeDelousing() {
        return percentageIntakeDelousing;
    }

    /**
     * Change the percentage of formal shelters performing intake delousing.
     * 
     * @param aPercentageIntakeDelousing the percentageIntakeDelousing to set
     */
    public static void setPercentageIntakeDelousing(double aPercentageIntakeDelousing) {
        percentageIntakeDelousing = aPercentageIntakeDelousing;
    }

} // end class Parameters

