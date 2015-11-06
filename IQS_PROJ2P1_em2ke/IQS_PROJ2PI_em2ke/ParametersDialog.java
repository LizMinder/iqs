import javax.swing.*;     // for all the JWhatevers
import java.awt.*;        // for BorderLayout and GridLayout
import java.awt.event.*;  // for FocusListener
import javax.swing.event.*;

//************************************************************************
//* This purpose of this class is to provide a pop-up dialog to allow the 
//* user to change the agent-based simulation input parameters.  This
//* class presumes the existence of a separate class, Parameters, that
//* provides static methods for setting the (static) instance variables
//* in the Parameters class.
//************************************************************************

public class ParametersDialog extends JDialog implements ActionListener,
                                                         ChangeListener
{
    // constant variable declarations
    private final int FIELD_WIDTH = 5;
    private final int GRID_ROWS   = 0;  // for GridLayout, use 0 to allow as 
                                        //   many rows as necessary
    private final int GRID_COLS   = 2;  // labels will be in the left column, 
                                        //   fields in right

    // instance variable declarations
    private JTextField rngSeedField;             // to hold seed for RNG

    private JTextField probabilityDiesField;     // prob an infected agent dies
    private JTextField probabilityTreatField;    // prob infected agent is treated
    private JTextField probabilityHostInfectedField;   // host infected by feces
    private JTextField probabilityVectorInfectedField; // louse infected by 
                                                       //   feeding

    private JTextField gridSizeField;            // number of cells along a 
                                                 // row or column in grid
    private JTextField agentSizeField;           // how big should the agent 
                                                 // be drawn
    private JTextField fovSizeField;             // # of cells in each 
                                                 // direction agent can "see"
    
    private JTextField probabilityLiceTransferField; // xfer during interaction
    private JTextField pctLiceTransferredMinField; // min pct lice transferred 
                                                   //   during interaction
    private JTextField pctLiceTransferredMaxField; // max pct lice transferred 
                                                   //   during interaction

    private JTextField treatmentLengthMinField;  // min time antibiotic is 
                                                 //   active in an agent
    private JTextField treatmentLengthMaxField;  // max time antibiotic is 
                                                 //   active in an agent

    private JTextField avgTimeBtwnMoveField;     // per agent, avg time b/w 
                                                 //   movements
    private JTextField avgTimeBtwnInteractField; // per agent, avg time b/w
                                                 //   interactions
    
    private JSlider     shelterFormalSlider;     // % formal shelters
    private JTextField  shelterFormalField;
    
    private JSlider     shelterMedicalSlider;    // % shelters with medical
    private JTextField  shelterMedicalField;
    
    private JSlider     shelterLaundrySlider;    // % shelters with laundry
    private JTextField  shelterLaundryField;
    
    private JSlider     shelterShowerSlider;     // % shelters with showers
    private JTextField  shelterShowerField;
    
    private JSlider     shelterDelousingSlider;     // % shelters with showers
    private JTextField  shelterDelousingField;
    
    
    private JSlider     hostsClothesChSlider;    // % Hosts w/ clothes change
    private JTextField  hostsClothesChField;

    private JButton maxButton;                   // Max out shelter params
    private JButton minButton;                   // Set shelter params to min
    /*
    private JRadioButton abMonitorTrueButton;    // one will be selected to see
    private JRadioButton abMonitorFalseButton;   //  if continuous monitoring 
    *                                            //  for AB application
    */

    private JButton    okButton;                 // click to signify accepting 
                                                 //   parameter values
    private JButton    cancelButton;             // click to keep previous 
                                                 //   parameter values

    //======================================================================
    //* public ParametersDialog()
    //* This is the constructor for the Parameters Dialog class.
    //======================================================================
    public ParametersDialog()
    {
        this.setTitle( "Simulation Parameters" );
        this.setModal( true );  // this window always has focus until dismissed

        // create new panel with GridLayout for  the center of the content pane
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout( new GridLayout( GRID_ROWS, GRID_COLS ) );

        // add RNG seed label/field pair to the center panel; the initial
        // value is set by pulling from the Parameters class
        JLabel nextLabel = new JLabel( "  RNG initial seed: " );
        nextLabel.setToolTipText(
                "Leave as is for debugging. " + 
                "Change for each run of an experiment." + 
                "Initial value -1 randomizes seed for each run" );
        centerPanel.add( nextLabel );
        rngSeedField = new JTextField( FIELD_WIDTH );
        rngSeedField.setText( Integer.toString( Parameters.getRNGInitialSeed()) );
        centerPanel.add( rngSeedField );

        // add two empty JLabels just for some separation
        centerPanel.add( new JLabel( "" ) );
        centerPanel.add( new JLabel( "" ) );


        // add grid size label/field pair; initial value from Parameters class
        centerPanel.add( new JLabel( "  Grid Size (N x N): " ) );
        gridSizeField = new JTextField( FIELD_WIDTH );
        gridSizeField.setText( Integer.toString( Parameters.getDefaultGridSize() ) );
        centerPanel.add( gridSizeField );

        // add size-of-drawn-agent label/field pair; initial value from Parameters
        centerPanel.add( new JLabel( "  Agent GUI Size (m x m): " ) );
        agentSizeField = new JTextField( FIELD_WIDTH );
        agentSizeField.setText( Integer.toString( Parameters.getAgentGUISize() ) );
        centerPanel.add( agentSizeField );

        // add agent field of view label/field pair
        // Not sure if this is still needed?
        centerPanel.add( new JLabel( "  Agent Field of View (cells): " ) );
        fovSizeField = new JTextField( FIELD_WIDTH );
        fovSizeField.setText( Integer.toString(Parameters.getFieldOfView() ) );
        centerPanel.add( fovSizeField );

        centerPanel.add( 
                new JLabel( "  Antibiotic Treatment Length (min/max): " ) );

        JPanel minMaxPanel = new JPanel();
        minMaxPanel.setLayout( new GridLayout( GRID_ROWS, GRID_COLS ) );

        treatmentLengthMinField = new JTextField( FIELD_WIDTH );
        treatmentLengthMinField.setText( 
                Double.toString( Parameters.getTreatmentLengthMin() ) );
        minMaxPanel.add( treatmentLengthMinField );

        treatmentLengthMaxField = new JTextField( FIELD_WIDTH );
        treatmentLengthMaxField.setText( 
                Double.toString( Parameters.getTreatmentLengthMax() ) );
        minMaxPanel.add( treatmentLengthMaxField );

        centerPanel.add( minMaxPanel );

        // add two empty JLabels just for some separation
        centerPanel.add( new JLabel( "" ) );
        centerPanel.add( new JLabel( "" ) );
        
        // Sliders
        
        nextLabel = new JLabel( "   % formal shelters" );
        nextLabel.setToolTipText("% of shelters that have some facilites " +
                                    "beyond just a place to sleep.");        
        centerPanel.add( nextLabel );
        centerPanel.add( new JLabel( "" ) );
        shelterFormalSlider = new JSlider( 0, 100, 
            (int) Math.round( Parameters.getPercentFormalShelters() * 100 ));
        shelterFormalSlider.addChangeListener(this);
        centerPanel.add( shelterFormalSlider );
        shelterFormalField = new JTextField( 7 );
        shelterFormalField.setText( "" + shelterFormalSlider.getValue());
        centerPanel.add( shelterFormalField );
        
        
        nextLabel = new JLabel( "   % of shelters with medical" );
        nextLabel.setToolTipText("% of shelters that have some medical " +
                                    "services available. Applies only to " +
                                    "formal shelters.");
        centerPanel.add( nextLabel);
        centerPanel.add( new JLabel( "" ) );
        shelterMedicalSlider   = new JSlider(0, 100,
            (int) Math.round( Parameters.getPercentageMedicalAvailable() * 100));
        shelterMedicalSlider.addChangeListener(this);
        centerPanel.add( shelterMedicalSlider );
        shelterMedicalField = new JTextField( 7 );
        shelterMedicalField.setText("" + shelterMedicalSlider.getValue());
        centerPanel.add( shelterMedicalField );
                
        
        nextLabel = new JLabel( "   % of shelters with laundry" );
        nextLabel.setToolTipText("% of shelters that have laundry " +
                                    "facilities available. Applies only to " +
                                    "formal shelters.");
        centerPanel.add( nextLabel);
        centerPanel.add( new JLabel( "" ) );
        shelterLaundrySlider   = new JSlider(0, 100,
            (int) Math.round( Parameters.getPercentageLaundryAvailable() * 100));
        shelterLaundrySlider.addChangeListener(this);
        centerPanel.add( shelterLaundrySlider );
        shelterLaundryField = new JTextField( 7 );
        shelterLaundryField.setText("" + shelterLaundrySlider.getValue());
        centerPanel.add( shelterLaundryField );
                
        nextLabel = new JLabel( "   % of shelters with showers" );
        nextLabel.setToolTipText("% of shelters that have shower " +
                                    "facilities available. Applies only to " +
                                    "formal shelters.");
        centerPanel.add( nextLabel);
        centerPanel.add( new JLabel( "" ) );
        shelterShowerSlider   = new JSlider(0, 100,
            (int) Math.round( Parameters.getPercentageShowersAvailable() * 100));
        shelterShowerSlider.addChangeListener(this);
        centerPanel.add( shelterShowerSlider );
        shelterShowerField = new JTextField( 7 );
        shelterShowerField.setText("" + shelterShowerSlider.getValue());
        centerPanel.add( shelterShowerField );
                
        nextLabel = new JLabel( "   % of formal shelters with delousing" );
        nextLabel.setToolTipText("% of shelters that have delousing " +
                                    "facilities available. Applies only to " +
                                    "formal shelters.");
        centerPanel.add( nextLabel);
        centerPanel.add( new JLabel( "" ) );
        shelterDelousingSlider   = new JSlider(0, 100,
            (int) Math.round( Parameters.getPercentageIntakeDelousing() * 100));
        shelterDelousingSlider.addChangeListener(this);
        centerPanel.add( shelterDelousingSlider );
        shelterDelousingField = new JTextField( 7 );
        shelterDelousingField.setText("" + shelterDelousingSlider.getValue());
        centerPanel.add( shelterDelousingField );
                
        nextLabel = new JLabel( "   % Hosts w/ clothes change" );
        nextLabel.setToolTipText("% of hosts that have one spare set of " +
                                    "clothes.");
        centerPanel.add( nextLabel);
        centerPanel.add( new JLabel( "" ) );
        hostsClothesChSlider   = new JSlider(0, 100,
            (int) Math.round( Parameters.getPercentOfHostsWithClothesChange() * 100));
        hostsClothesChSlider.addChangeListener(this);
        centerPanel.add( hostsClothesChSlider );
        hostsClothesChField = new JTextField( 7 );
        hostsClothesChField.setText("" + hostsClothesChSlider.getValue());
        centerPanel.add( hostsClothesChField );
        
        minButton = new JButton("Zero Sliders");
        minButton.setActionCommand("zerosliders");
        minButton.addActionListener(this);
        centerPanel.add(minButton);
                
        maxButton = new JButton("Max Sliders");
        maxButton.setActionCommand("maxsliders");
        maxButton.addActionListener(this);
        centerPanel.add(maxButton);
                
        // add two empty JLabels just for some separation
        centerPanel.add( new JLabel( "" ) );
        centerPanel.add( new JLabel( "" ) );

        // the next five input fields will all be probabilities, so just put in a 
        // reminder note of the range
        centerPanel.add( new JLabel( "" ) );
        centerPanel.add( new JLabel( " Probabilities: (0 < p < 1)" ) );

        nextLabel = new JLabel( "  Host Infected: " );
        nextLabel.setToolTipText("Probability host is infected by exposure to louse feces " +
                "during a day");
        centerPanel.add( nextLabel );
        probabilityHostInfectedField = new JTextField( FIELD_WIDTH );
        probabilityHostInfectedField.setText( 
                Double.toString( Parameters.getProbabilityInfestedIsInfected() ) );
        centerPanel.add( probabilityHostInfectedField );

        centerPanel.add( new JLabel( "  Infected Agent Receives Treatment: " ) );
        probabilityTreatField = new JTextField( FIELD_WIDTH );
        probabilityTreatField.setText( Double.toString( Parameters.getProbTreatment() ) );
        centerPanel.add( probabilityTreatField );

        centerPanel.add( new JLabel( "  Infected Agent Dies: " ) );
        probabilityDiesField = new JTextField( FIELD_WIDTH );
        probabilityDiesField.setText( Double.toString( Parameters.getProbInfectedAgentDies() ) );
        centerPanel.add( probabilityDiesField );

        nextLabel = new JLabel( "  Vector Infected: " );
        nextLabel.setToolTipText("Probability vector is infected by feeding on infected host " +
                "during a day");
        centerPanel.add( nextLabel );
        probabilityVectorInfectedField = new JTextField( FIELD_WIDTH );
        probabilityVectorInfectedField.setText( 
                Double.toString( Parameters.getProbabilityInfectedByFeeding() ) );
        centerPanel.add( probabilityVectorInfectedField );

        nextLabel = new JLabel( "  Probability lice transfer: " );
        nextLabel.setToolTipText("Probability that lice transferred to other host " +
                "during an interaction");
        centerPanel.add( nextLabel );
        probabilityLiceTransferField = new JTextField( FIELD_WIDTH );
        probabilityLiceTransferField.setText(
                "" + Parameters.getProbabilityLouseTransfer() );
        centerPanel.add( probabilityLiceTransferField );
        
        nextLabel = new JLabel( "  Pct Lice Transferred (min/max): " );
        nextLabel.setToolTipText("Percentage of lice transferred to other host " +
                "during an interaction");
        centerPanel.add( nextLabel );
        
        minMaxPanel = new JPanel();
        minMaxPanel.setLayout( new GridLayout( GRID_ROWS, GRID_COLS ) );

        pctLiceTransferredMinField = new JTextField( FIELD_WIDTH );
        pctLiceTransferredMinField.setText( 
                Double.toString( Parameters.getPercentageLiceTransferredMin() ) );
        minMaxPanel.add( pctLiceTransferredMinField );

        pctLiceTransferredMaxField = new JTextField( FIELD_WIDTH );
        pctLiceTransferredMaxField.setText( 
                Double.toString( Parameters.getPercentageLiceTransferredMax() ) );
        minMaxPanel.add( pctLiceTransferredMaxField );

        centerPanel.add( minMaxPanel );

        // add two empty JLabels just for some separation
        centerPanel.add( new JLabel( "" ) );
        centerPanel.add( new JLabel( "" ) );

        // add the radio buttons for continuous AB monitoring
        /*
        centerPanel.add( new JLabel( " Continuous Monitoring for AB: " ) );
        abMonitorTrueButton = new JRadioButton( "Yes" );
        abMonitorFalseButton = new JRadioButton( "No" );
        JPanel radioButtonPanel = new JPanel();
        radioButtonPanel.add( abMonitorTrueButton );
        radioButtonPanel.add( abMonitorFalseButton );
        centerPanel.add( radioButtonPanel );

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( abMonitorTrueButton );
        buttonGroup.add( abMonitorFalseButton );

        if ( Parameters.isABMonitoredContinuously() )
          abMonitorTrueButton.setSelected( true );
        else
          abMonitorFalseButton.setSelected( true );
          */

        // add two empty JLabels just for some separation
        centerPanel.add( new JLabel( "" ) );
        centerPanel.add( new JLabel( "" ) );

        // the next three input fields will all be event rates, so just put in a note
        centerPanel.add( new JLabel( "" ) );
        centerPanel.add( new JLabel( " Event Times: (avg time between events)" ) );

        centerPanel.add( new JLabel( "  Agent Movement: " ) );
        avgTimeBtwnMoveField = new JTextField( FIELD_WIDTH );
        avgTimeBtwnMoveField.setText( Double.toString( Parameters.getAvgTimeBtwnHostMove() ) );
        centerPanel.add( avgTimeBtwnMoveField );

        centerPanel.add( new JLabel( "  Agent Interaction: " ) );
        avgTimeBtwnInteractField = new JTextField( FIELD_WIDTH );
        avgTimeBtwnInteractField.setText( Double.toString( Parameters.getAvgTimeBtwnInteract() ) );
        centerPanel.add( avgTimeBtwnInteractField );

        // add the panel containing all the input fields to the center of the content pane
        this.add( new JScrollPane( centerPanel), BorderLayout.CENTER );

        // create a panel for the OK/Cancel buttons in the south of the window
        JPanel southPanel = new JPanel();
        okButton          = new JButton( "OK" );
        cancelButton      = new JButton( "Cancel" );
        southPanel.add( okButton );
        southPanel.add( cancelButton );

        okButton.setActionCommand( "OK" );
        cancelButton.setActionCommand("Cancel");

        // add listeners so we'll know when one of the buttons is pressed
        okButton.addActionListener( this );
        cancelButton.addActionListener( this );

        // add the panel with the buttons to the south of the window
//        this.add( southPanel, BorderLayout.SOUTH );
        this.add( southPanel, BorderLayout.NORTH );

        // draw all the visual components
        this.pack();
        this.setVisible( true );

    } // end of ParametersDialog()


    @Override
    public void stateChanged(ChangeEvent event)
    {
        JSlider whichSlider = (JSlider) event.getSource();
        if ( whichSlider == shelterFormalSlider ){
            int value = shelterFormalSlider.getValue();
            shelterFormalField.setText( "" + value );
        } else if ( whichSlider == shelterMedicalSlider ){
            int value = shelterMedicalSlider.getValue();
            shelterMedicalField.setText( "" + value );
        } else if ( whichSlider == shelterLaundrySlider ){
            int value = shelterLaundrySlider.getValue();
            shelterLaundryField.setText( "" + value );
        } else if ( whichSlider == shelterShowerSlider ){
            int value = shelterShowerSlider.getValue();
            shelterShowerField.setText( "" + value );
        } else if ( whichSlider == hostsClothesChSlider ){
            int value = hostsClothesChSlider.getValue();
            hostsClothesChField.setText( "" + value );
        } else if ( whichSlider == shelterDelousingSlider ){
            int value = shelterDelousingSlider.getValue();
            shelterDelousingField.setText( "" + value );
        }
    }
    
    //======================================================================
    //* public void actionPerformed(ActionEvent event)
    //* This method is called whenever an action event is captured by Java.
    //* Because we only added listeners to the OK and Cancel buttons, this
    //* method will be called when one of those two buttons is clicked.
    //======================================================================
    public void actionPerformed( ActionEvent event )
    {
        if ( event.getActionCommand().equals( cancelButton.getActionCommand() ) ) {
            // if cancel, just close the window with no changes
            this.dispose();
        } else if (event.getActionCommand().equals(minButton.getActionCommand() )){
            shelterFormalSlider.setValue(0);
            shelterFormalField.setText("0");
            shelterMedicalSlider.setValue(0);
            shelterMedicalField.setText("0");
            shelterLaundrySlider.setValue(0);
            shelterLaundryField.setText("0");
            shelterShowerSlider.setValue(0);
            shelterShowerField.setText("0");
            hostsClothesChSlider.setValue(0);
            hostsClothesChField.setText("0");
            shelterDelousingSlider.setValue(0);
            shelterDelousingField.setText("0");
            
        } else if (event.getActionCommand().equals(maxButton.getActionCommand() )){
            shelterFormalSlider.setValue(100);
            shelterFormalField.setText("100");
            shelterMedicalSlider.setValue(100);
            shelterMedicalField.setText("100");
            shelterLaundrySlider.setValue(100);
            shelterLaundryField.setText("100");
            shelterShowerSlider.setValue(100);
            shelterShowerField.setText("100");
            hostsClothesChSlider.setValue(100);
            hostsClothesChField.setText("100");
            shelterDelousingSlider.setValue(100);
            shelterDelousingField.setText("100");
        } else {
            // if OK, get all of the values from the input fields...
            int initialSeed           = Integer.parseInt( rngSeedField.getText() );

            int gridSize              = Integer.parseInt( gridSizeField.getText() );
            int agentSize             = Integer.parseInt( agentSizeField.getText() );
            int agentFOV              = Integer.parseInt( fovSizeField.getText() );

            double treatmentLengthMin = Double.parseDouble( treatmentLengthMinField.getText() );
            double treatmentLengthMax = Double.parseDouble( treatmentLengthMaxField.getText() );
            
            double pctSheltersFormal =
                    Double.parseDouble( shelterFormalField.getText()) / 100.0 ;
            double pctSheltersWithMedical =
                    Double.parseDouble( shelterMedicalField.getText()) / 100.0;
            double pctSheltersWithLaundry =
                    Double.parseDouble( shelterLaundryField.getText()) / 100.0;
            double pctSheltersWithShowers =
                    Double.parseDouble( shelterShowerField.getText()) / 100.0;
            double pctHostsWithClothesChange =
                    Double.parseDouble( hostsClothesChField.getText()) / 100.0;
            double pctSheltersWithDelousing = 
                    Double.parseDouble( shelterDelousingField.getText()) / 100.0;
                    

            double probabilityDies    = Double.parseDouble( probabilityDiesField.getText() );
            double probabilityTreat   = Double.parseDouble( probabilityTreatField.getText() );
            double probabilityLiceTransfer = 
                                        Double.parseDouble( probabilityLiceTransferField.getText() );
            double probabilityInfectedByFeeding = 
                                        Double.parseDouble( probabilityVectorInfectedField.getText() );
            double probabilityHostInfected = 
                                        Double.parseDouble( probabilityHostInfectedField.getText() );

            double avgTimeBtwnMove     = Double.parseDouble( avgTimeBtwnMoveField.getText() );
            double avgTimeBtwnInteract = Double.parseDouble( avgTimeBtwnInteractField.getText() );
            
            double percentageLiceTransferMin =
                                         Double.parseDouble( pctLiceTransferredMinField.getText() );
            double percentageLiceTransferMax =
                                         Double.parseDouble( pctLiceTransferredMaxField.getText() );

            // make sure parameters are in appropriate ranges; if not, pop up
            // an error dialog
            if ( initialSeed < -1 || initialSeed == 0 )
                error( "RNG initial seed must be a positive integer or -1 (0 is not allowed)" );
            else if ( gridSize <= 0 || agentSize <= 0 )
                error( "Grid/agent size must be positive" );
            else if ( agentFOV <= 0 )
                error( "Field of view must be positive" );
            else if ( treatmentLengthMin <= 0 || treatmentLengthMax <= 0 || 
                      treatmentLengthMax < treatmentLengthMin )
                error( "Treatment length min/max must be positive with min < max" );
            else if (probabilityDies    <  0 || probabilityDies    > 1 ||
                     probabilityTreat   <  0 || probabilityTreat   > 1 ||
                     probabilityLiceTransfer < 0 || probabilityLiceTransfer > 1 ||
                     probabilityHostInfected < 0 || probabilityHostInfected > 1 ||
                     probabilityInfectedByFeeding < 0 || probabilityInfectedByFeeding > 1 )
                error( "Probabilities must be > 0 and < 1" );
            else if ( avgTimeBtwnMove <= 0 || avgTimeBtwnInteract <= 0 )
                error( "Average inter-event times must be positive" );
            else if ( percentageLiceTransferMin < 0 || percentageLiceTransferMin > 100 || 
                      percentageLiceTransferMax < 0 || percentageLiceTransferMax > 100 ) 
                error( "Percentages must be between 0 and 100 " );
            else if ( percentageLiceTransferMin > percentageLiceTransferMax )
                error( "Minimum percentage must be <= maximum percentage" );
            else
            {
                // all the values are within appropriate ranges, so just set
                // the values in the Parameters class and then close the window
                Parameters.setRNGInitialSeed( initialSeed );

                Parameters.setDefaultGridSize( gridSize );
                Parameters.setAgentGUISize( agentSize );
                Parameters.setFieldOfView( agentFOV );

                Parameters.setTreatmentLengthMin( treatmentLengthMin );
                Parameters.setTreatmentLengthMax( treatmentLengthMax );

                Parameters.setProbInfectedAgentDies( probabilityDies );
                Parameters.setProbTreatment( probabilityTreat );
                
                Parameters.setPercentFormalShelters(pctSheltersFormal);
                Parameters.setPercentageMedicalAvailable( pctSheltersWithMedical );
                Parameters.setPercentageShowersAvailable( pctSheltersWithShowers );
                Parameters.setPercentageLaundryAvailable( pctSheltersWithLaundry );
                Parameters.setPercentageIntakeDelousing( pctSheltersWithDelousing );
                Parameters.setPercentOfHostsWithClothesChange(pctHostsWithClothesChange);
                
                /*
                Parameters.setABMonitoredContinuously( abMonitorTrueButton.isSelected());
                */

                Parameters.setAvgTimeBtwnMove( avgTimeBtwnMove );
                Parameters.setAvgTimeBtwnInteract( avgTimeBtwnInteract );
                
                Parameters.setProbabilityLouseTransfer(probabilityLiceTransfer);
                Parameters.setPercentageLiceTransferredMin(percentageLiceTransferMin);
                Parameters.setPercentageLiceTransferredMax(percentageLiceTransferMax);
                Parameters.setProbabilityInfestedIsInfected(probabilityHostInfected);
                Parameters.setProbabilityInfectedByFeeding(probabilityInfectedByFeeding);

                this.dispose();  // kill the dialog window
            }
        } 

    } // end actionPerformed() method

    //======================================================================
    //* public void error( String message )
    //* This method just pops up a dialog window displaying the provided
    //* error message.
    //======================================================================
    public void error( String message )
    {
       JOptionPane.showMessageDialog( this, message );
    }

} // end ParametersDialog class

