import squint.*;
import javax.swing.*;  // for all the JWhatevers
import java.awt.*;     // for BorderLayout
import java.util.Random;

//======================================================================
//* This class implements the GUI front-end for the simulation model,
//* including adding buttons, sliders, text fields, etc. and handling
//* mouse/keyboard interaction.
//======================================================================
public class SimulationWindow extends SimulationManager
{
    // default window width and height defined as constants
    private final int WINDOW_WIDTH  = 600;
    private final int WINDOW_HEIGHT = 850;

    private AgentCanvas canvas;  // the canvas on which agents are drawn

    // instance variables below are all GUI window components, and should
    // be self-explanatory
    private JSlider    numAgentsSlider;
    private JTextField numAgentsField;
    
    private JSlider    pctHostsInfestedSlider;
    private JTextField pctHostsInfestedField;

    private JSlider    pctHostsInfectedSlider;
    private JTextField pctHostsInfectedField;

    private JSlider    pctVectorsInfectedSlider;
    private JTextField pctVectorsInfectedField;

    /*
    private JSlider    pctTreatedSlider;
    private JTextField pctTreatedField;
    */

    private JTextField   timeField;

    private JButton paramsButton;

    private JRadioButton fastButton;
    private JRadioButton slowButton;
    private ButtonGroup  buttonGroup;

    private JButton startButton;
    private JButton pauseButton;
    private JButton resumeButton;
    private JButton stopButton;
    
    /* For testing. */
    private JButton maxSlidersButton;

    private JProgressBar progressBar;

    //======================================================================
    //* public SimulationWindow()
    //* Constructor for the front end GUI.  Lays out the components on the
    //* window.
    //======================================================================
    public SimulationWindow()
    {
        this.createWindow(WINDOW_WIDTH, WINDOW_HEIGHT, 
                          "Urban Trench Fever Simulation", 
                          WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout()); // java.awt.*

        canvas = new AgentCanvas(this, shelters);

        // divide the window into two panels
        JPanel northPanel = new JPanel();
        JPanel southPanel = new JPanel();
        northPanel.setLayout(new GridLayout(6, 1));
        southPanel.setLayout(new GridLayout(3, 1));

        // create the GUI components to go in the top...
        numAgentsSlider   = new JSlider(1, 
            Parameters.getHostPopSizeMax(),
            Parameters.getHostPopulationSize());
        pctHostsInfectedSlider = new JSlider(0, 100, 
            (int) Math.round( Parameters.getPercentOfHostsInfected() * 100.0));
        pctHostsInfestedSlider = new JSlider(0, 100, 
            (int) Math.round( Parameters.getPercentOfHostsInfested() * 100.0));
        pctVectorsInfectedSlider = new JSlider(0, 100, 
            (int) Math.round( Parameters.getPercentOfVectorsInfected() * 100.0));
        //pctTreatedSlider  = new JSlider(0, 100, 0);
        timeField              = new JTextField(7);
        // Default to a year
        timeField.setText("365");

        // create a panel for the row containing timeField info, and add to the top panel
        JPanel timePanel = new JPanel();
        timePanel.add( new JLabel("Time: ") );
        timePanel.add( timeField );
        
        // Secret button to max out all the sliders
        maxSlidersButton = new JButton();
        maxSlidersButton.setOpaque(false);
        maxSlidersButton.setContentAreaFilled(false);
        maxSlidersButton.setBorderPainted(false);
        timePanel.add(maxSlidersButton);
        
        northPanel.add( timePanel);

        // create a panel for the row containing agents info, and add to the top panel
        JPanel sliderPanel = new JPanel();
        sliderPanel.add( new JLabel("             Agents: ") );
        sliderPanel.add( numAgentsSlider );
        numAgentsField = new JTextField(7);
        numAgentsField.setText("" + numAgentsSlider.getValue());
        sliderPanel.add( numAgentsField );
        northPanel.add( sliderPanel);
        
        // create a panel for the row containing % hosts infested, add to top
        sliderPanel = new JPanel();
        sliderPanel.add( new JLabel("% Hosts Infested: ") );
        sliderPanel.add( pctHostsInfestedSlider );
        pctHostsInfestedField = new JTextField(7);
        pctHostsInfestedField.setText("" + pctHostsInfestedSlider.getValue());
        sliderPanel.add( pctHostsInfestedField );
        northPanel.add( sliderPanel );

        // create a panel for the row containing % infected info, and add to the top panel
        sliderPanel = new JPanel();
        sliderPanel.add( new JLabel("% Hosts Infected: ") );
        sliderPanel.add( pctHostsInfectedSlider );
        pctHostsInfectedField = new JTextField(7);
        pctHostsInfectedField.setText("" + pctHostsInfectedSlider.getValue());
        sliderPanel.add( pctHostsInfectedField );
        northPanel.add( sliderPanel );
        
        // create a panel for the row containing % treated info, and add to the top panel
        /*
        sliderPanel = new JPanel();
        sliderPanel.add( new JLabel("          % Treated: ") );
        sliderPanel.add( pctTreatedSlider );
        pctTreatedField = new JTextField(7);
        pctTreatedField.setText("" + pctTreatedSlider.getValue());
        sliderPanel.add( pctTreatedField );
        northPanel.add( sliderPanel );
        */

        // create a panel for the row containing the params button, and add to the top panel
        sliderPanel = new JPanel();
        paramsButton = new JButton("More Parameters");
        sliderPanel.add( paramsButton );
        northPanel.add(sliderPanel);

        // create a panel for the row with fast/slow radio buttons, and add to the bottom panel
        JPanel radioPanel = new JPanel();
        radioPanel.add(new JLabel("Animation Speed: "));
        fastButton = new JRadioButton("Fast");
        fastButton.setActionCommand("Fast");
        slowButton = new JRadioButton("Slow");
        slowButton.setActionCommand("Slow");
        radioPanel.add(fastButton);
        radioPanel.add(slowButton);
        southPanel.add(radioPanel);

        // need a button group for the radio buttons so that selection is mutually exclusive
        buttonGroup = new ButtonGroup();
        buttonGroup.add(fastButton);
        buttonGroup.add(slowButton);
        fastButton.setSelected(true);

        // create a panel for the main control buttons, and add to the bottom panel
        JPanel buttonPanel = new JPanel();
        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        resumeButton = new JButton("Resume");
        stopButton = new JButton("Stop");
        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resumeButton);
        buttonPanel.add(stopButton);

        pauseButton.setEnabled(false);
        resumeButton.setEnabled(false);
        stopButton.setEnabled(false);

        southPanel.add( buttonPanel);

        // create a panel for the progress bar, and add to the bottom panel
        JPanel progressPanel = new JPanel();
        progressBar = new JProgressBar();
        progressPanel.add( new JLabel("Progress: ") );
        progressPanel.add( progressBar );
        southPanel.add( progressPanel );

        // Add these together at the end or the canvas will appear center
        // first, then bump up when the south panel is added.
        //
        // NOTE: DO NOT put the JScrollPane for the canvas inside another
        // JPanel or the scrolling will not work.
        contentPane.add( northPanel, BorderLayout.NORTH);
        contentPane.add( new JScrollPane(canvas), BorderLayout.CENTER);
        contentPane.add( southPanel, BorderLayout.SOUTH );
    }

    //======================================================================
    //* public void buttonClicked(JButton whichButton)
    //* This method is called whenever the user clicks a button on the GUI.
    //* Just determine which button, and do the right thing.
    //======================================================================
    public void buttonClicked(JButton whichButton)
    {
        if (whichButton == startButton)
        {
            // if start, turn on/off the right buttons, and then start
            // the simulation

            boolean fast = true;
            if (buttonGroup.getSelection().getActionCommand().equals(
                slowButton.getActionCommand()))
            {
                fast = false;
            }

            fastButton.setEnabled(false);
            slowButton.setEnabled(false);

            paramsButton.setEnabled(false);
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
            stopButton.setEnabled(true);
            pauseButton.requestFocus();

            this.start(Integer.parseInt(timeField.getText()),
                      numAgentsSlider.getMaximum(),
                      numAgentsSlider.getValue(),
                      pctHostsInfestedSlider.getValue(),
                      pctHostsInfectedSlider.getValue(),
                      pctVectorsInfectedSlider.getValue(),
                      //pctTreatedSlider.getValue(),
                      0,
                      fast,
                      canvas, progressBar);
        }
        else if (whichButton == pauseButton)
        {
            // if pause, turn on/off the right buttons and pause...

            this.pause(); 

            pauseButton.setEnabled(false);
            resumeButton.setEnabled(true);
            resumeButton.requestFocus();
        }
        else if (whichButton == resumeButton)
        {
            // if resume, turn on/off the right buttons and go again...

            this.resume();

            pauseButton.setEnabled(true);
            resumeButton.setEnabled(false);
            pauseButton.requestFocus();
        }
        else if (whichButton == stopButton)
        {
            // if stop, turn on/off the right buttons and reset the progress bar

            this.stop();

            fastButton.setEnabled(true);
            slowButton.setEnabled(true);

            paramsButton.setEnabled(true);
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            stopButton.setEnabled(false);
            resumeButton.setEnabled(false);
            startButton.requestFocus();

            progressBar.setValue(0);
        }
        else if (whichButton == paramsButton)
        {
            // if params button, pop up a new dialog to allow parameters to be
            // modified, update the canvas and associated GUI fields

            new ParametersDialog();
            this.shelters = new ShelterGrid();
            canvas.updateGrid(shelters);
            numAgentsSlider.setMaximum(
                Parameters.getHostPopSizeMax());
            numAgentsField.setText("" + numAgentsSlider.getValue());
            startButton.requestFocus();
        } else if (whichButton == maxSlidersButton)
        {
            //System.out.println("Maxing out sliders.");
            numAgentsSlider.setValue(Parameters.getHostPopSizeMax());
            numAgentsField.setText( "" + Parameters.getHostPopSizeMax() );
            pctHostsInfectedSlider.setValue(100);
            pctHostsInfectedField.setText("100");
            pctHostsInfestedSlider.setValue(100);
            pctHostsInfestedField.setText("100");
            // pctVectorsInfectedSlider.setValue(100);
            // pctVectorsInfectedField.setText("100");
        }
    }

    //======================================================================
    //* public void focusLost()
    //* This method is called whenever a field (or the entire window)
    //* loses focus.  We just want to update the sliders appropriately.
    //======================================================================
    public void focusLost()
    {
        // why? unless
            // the user presses return in the text field (e.g., types in info
            // then clicks start) the slider will not be updated; if instead
            // the user changes the slider, the text field will automatically
            // be updated.  This will just make sure everything is consistent.
        numAgentsSlider.setValue(Integer.parseInt(numAgentsField.getText()));
        pctHostsInfectedSlider.setValue(
                Integer.parseInt(pctHostsInfectedField.getText()));
        //pctTreatedSlider.setValue(Integer.parseInt(pctTreatedField.getText()));
        progressBar.setMaximum(Integer.parseInt(timeField.getText()));
    }

    //======================================================================
    //* public void sliderChanged(JSlider whichSlider)
    //* This method is called when the user messes with a slider.  Update
    //* the text fields appropriately.
    //======================================================================
    public void sliderChanged(JSlider whichSlider)
    {
        if (whichSlider == numAgentsSlider)
        {
            int value = numAgentsSlider.getValue();
            numAgentsField.setText( "" + value );
        }
        else if (whichSlider == pctHostsInfectedSlider)
        {
            int value = pctHostsInfectedSlider.getValue();
            pctHostsInfectedField.setText( "" + value );
        }
        /*
        else if (whichSlider == pctTreatedSlider)
        {
            int value = pctTreatedSlider.getValue();
            pctTreatedField.setText( "" + value );
        }
        */
    }

    //======================================================================
    //* public void textEntered(JTextField whichField)
    //* This method is called when the user presses return within a text
    //* field.  Update the sliders appropriately.
    //*
    //* If the user presses tab or just uses the mouse to click start
    //* without hitting return, this method is not called, so we use
    //* focusLost() -- see above.
    //======================================================================
    public void textEntered(JTextField whichField)
    {
        if (whichField == timeField)
        {
            numAgentsSlider.requestFocus();
        }
        else if (whichField == numAgentsField)
        {
            numAgentsSlider.setValue(
                Integer.parseInt(numAgentsField.getText()));
        }
        else if (whichField == pctHostsInfectedField)
        {
            pctHostsInfectedSlider.setValue(
                Integer.parseInt(pctHostsInfectedField.getText()));
        }
        /*
        else if (whichField == pctTreatedField)
        {
            pctTreatedSlider.setValue(
                Integer.parseInt(pctTreatedField.getText()));
        }
        */
    }

    //======================================================================
    //* public void simulationFinished()
    //* Called when the simulation has naturally finished its run timeField.
    //* We need to reset the GUI components to start again.
    //======================================================================
    public void simulationFinished()
    {
            fastButton.setEnabled(true);
            slowButton.setEnabled(true);

            paramsButton.setEnabled(true);
            startButton.setEnabled(true);
            pauseButton.setEnabled(false);
            stopButton.setEnabled(false);
            resumeButton.setEnabled(false);
            startButton.requestFocus();

            progressBar.setValue(0);
    }

    //======================================================================
    //* public static void main(String[] args)
    //* Just including main so that the simulation can be executed from the
    //* command prompt.  Note that main just creates a new instance of this
    //* class, which will start the GUI window and then we're off and
    //* running...
    //======================================================================
    public static void main(String[] args)
    {
        new SimulationWindow();
    }

} // end class SimulationWindow
