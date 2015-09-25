/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.voicing;

import imp.ImproVisor;
import imp.voicing.AutomaticVoicingSettings;
import imp.voicing.AVPFileCreator;
import imp.data.Note;
import imp.data.NoteSymbol;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *Graphical user interface to allow user to modify voicing settings
 * @author Daniel Scanteianu, Errick Jackson
 */
public class ControlPanelFrame extends javax.swing.JFrame implements Serializable{

    /**
     * Creates new form ControlPanelFrame
     */
    public ControlPanelFrame(AutomaticVoicingSettings avs1) {
        this.avs=avs1;
        initComponents();
        associateSliders();
        syncFromSettings();
        setSlidersToVariables();
        setLabels();
        System.out.println("Lower Left hand: "+avs.getLeftHandLowerLimit());
        this.setTitle("Automated Voicing Generator Settings Editor");
        jButton1.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
               saveSlidersToVariables();
           }
       });  
         jButton2.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
               setSlidersToVariables();
           }
       });
        jButton3.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
              resetValues();
           }
       }); 
       jButton4.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
               File openFile=null;
                JFileChooser chooser = new JFileChooser(ImproVisor.getUserDirectory());
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Auto Voicing Preset Files", "avp");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    openFile=chooser.getSelectedFile();
                }
                
                AVPFileCreator.fileToSettings( openFile, avs);
                setSlidersToVariables();
                setTitle(openFile.getName());
                
       
                 
            
            //setSlidersToVariables();
            
           }
       });
        jButton5.addActionListener(new ActionListener() {

           public void actionPerformed(ActionEvent e)
           {
                File saveFile=null;
                JFileChooser chooser = new JFileChooser(ImproVisor.getUserDirectory());
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Auto Voicing Preset Files", "avp");
                chooser.setFileFilter(filter);
                int returnVal = chooser.showSaveDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    saveFile=chooser.getSelectedFile();
                    if(!saveFile.getName().toLowerCase().endsWith(".avp"))
                        saveFile=new File(saveFile.getAbsolutePath()+".avp");
                }
                syncToSettings();
                AVPFileCreator.settingsToFile(avs,saveFile);
                
            }
       });
       for(JSlider slider:handLimits)
       {
           slider.addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e)
           {
              setLabels();
           }
       });
           
       }
    }

    public ControlPanelFrame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void setVoicingFile(File file)
    { AVPFileCreator.settingsToFile(avs, file);
                setSlidersToVariables();
    }
    /**
     * hard coded defaults for when defaults button is pressed.
     */
    private void setDefaults()
    {
        leftHandLowerLimit=46;
        rightHandLowerLimit=60;
        leftHandUpperLimit=67;
        rightHandUpperLimit=81;
        leftHandSpread=9;
        rightHandSpread=9;
        leftHandMinNotes=1;
        leftHandMaxNotes=2;
        rightHandMinNotes=1;
        rightHandMaxNotes=4;
        //voice leading controls
        preferredMotion=0;
        preferredMotionRange=3;
        previousVoicingMultiplier=4;// multiplier for notes used in previous voicing
        halfStepAwayMultiplier=3;
        fullStepAwayMultiplier=2;
        //voicing control
        leftColorPriority=0;//priority of any color note
        rightColorPriority=0;
        maxPriority=6;//max priority a note in the priority array can have
        priorityMultiplier=.667;//should be between 0 and 1, multiply this by the index in priority array, subtract result from max priority to get note priority
        repeatMultiplier=.3;
        halfStepReducer=0;
        fullStepReducer=.7;
        syncToSettings();
    }
    /**
     * Sets the values in the automatic voicing settings object to the variables
     */
    public void syncToSettings()
    {
        avs.setLeftHandLowerLimit(leftHandLowerLimit);
        avs.setRightHandLowerLimit(rightHandLowerLimit);
        avs.setLeftHandUpperLimit(leftHandUpperLimit);
        avs.setRightHandUpperLimit(rightHandUpperLimit);
        avs.setLeftHandSpread(leftHandSpread);
        avs.setRightHandSpread(rightHandSpread);
        avs.setLeftHandMinNotes(leftHandMinNotes);
        avs.setLeftHandMaxNotes(leftHandMaxNotes);
        avs.setRightHandMinNotes(rightHandMinNotes);
        avs.setRightHandMaxNotes(rightHandMaxNotes);
        //voice leading controls
        avs.setPreferredMotion(preferredMotion);
        avs.setPreferredMotionRange(preferredMotionRange);
        avs.setPreviousVoicingMultiplier(previousVoicingMultiplier);
        avs.setHalfStepAwayMultiplier(halfStepAwayMultiplier);
        avs.setFullStepAwayMultiplier(fullStepAwayMultiplier);
        //voicing control
        avs.setLeftColorPriority(leftColorPriority);
        avs.setRightColorPriority(rightColorPriority);
        avs.setMaxPriority(maxPriority);
        avs.setPriorityMultiplier(priorityMultiplier);
        avs.setRepeatMultiplier(repeatMultiplier);
        avs.setHalfStepReducer(halfStepReducer);
        avs.setFullStepReducer(fullStepReducer);
        avs.setInvertM9(invertM9);
    }
    /**
     * sets the variables to the values in the settings object
     */
    public void syncFromSettings()
    {
        leftHandLowerLimit=avs.getLeftHandLowerLimit();
        rightHandLowerLimit=avs.getRightHandLowerLimit();
        leftHandUpperLimit=avs.getLeftHandUpperLimit();
        rightHandUpperLimit=avs.getRightHandUpperLimit();
        leftHandSpread=avs.getLeftHandSpread();
        rightHandSpread=avs.getRightHandSpread();
        leftHandMinNotes=avs.getLeftHandMinNotes();
        leftHandMaxNotes=avs.getLeftHandMaxNotes();
        rightHandMinNotes=avs.getRightHandMinNotes();
        rightHandMaxNotes=avs.getRightHandMaxNotes();
        //voice leading controls
        preferredMotion=avs.getPreferredMotion();
        preferredMotionRange=avs.getPreferredMotionRange();
        previousVoicingMultiplier=avs.getPreviousVoicingMultiplier();// multiplier for notes used in previous voicing
        halfStepAwayMultiplier=avs.getHalfStepAwayMultiplier();
        fullStepAwayMultiplier=avs.getFullStepAwayMultiplier();
        //voicing control
        leftColorPriority=avs.getLeftColorPriority();//priority of any color note
        rightColorPriority=avs.getRightColorPriority();
        maxPriority=avs.getMaxPriority();//max priority a note in the priority array can have
        priorityMultiplier=avs.getPriorityMultiplier();//should be between 0 and 1, multiply this by the index in priority array, subtract result from max priority to get note priority
        repeatMultiplier=avs.getRepeatMultiplier();
        halfStepReducer=avs.getHalfStepReducer();
        fullStepReducer=avs.getFullStepReducer();
        invertM9=avs.getInvertM9();
    }
        /*
    private void setupHandParams()
    {
        handLimits=new JSlider[4];
        handLimits[0]=new JSlider(21,108,leftHandLowerLimit);
        handLimits[1]=new JSlider(21,108,leftHandUpperLimit);
        handLimits[2]=new JSlider(21,108,rightHandLowerLimit);
        handLimits[3]=new JSlider(21,108,rightHandUpperLimit);
        handSpreads=new JSlider[2];
        handSpreads[0]=new JSlider(7,19,leftHandSpread);
        handSpreads[1]=new JSlider(7,19,rightHandSpread);
        handNotes=new JSlider[4];
        handNotes[0]=new JSlider(0,5, leftHandMinNotes);
        handNotes[1]=new JSlider(0,5, leftHandMaxNotes);
        handNotes[2]=new JSlider(0,5, rightHandMinNotes);
        handNotes[3]=new JSlider(0,5, rightHandMaxNotes);
        
    }
    private void setupVoicingParams()
    {
        colorPrioritySlider=new JSlider(0,10,colorPriority);
        maxPrioritySlider=new JSlider(0,10,maxPriority);
        repeatedNoteProbability=new JSlider(0,10,(int)(repeatMultiplier*10));
        priorityWeighting=new JSlider(0,15,(int)(colorPriority*10));
    }
    private void setupLeadingParams()
    {
        voiceLeadingWeights=new JSlider[3];//index=change in half steps
        voiceLeadingWeights[0]=new JSlider(0,50,(int)(previousVoicingMultiplier*10));//0 prevents previous note from being played
        voiceLeadingWeights[1]=new JSlider(0,50,(int)(halfStepAwayMultiplier*10));
        voiceLeadingWeights[2]=new JSlider(0,50,(int)(fullStepAwayMultiplier*10));
        preferredMotionSlider=new JSlider(-5,5,preferredMotion);
        motionRange=new JSlider(0,5,preferredMotionRange);
    }
        */
    public void resetValues()
    {
        setDefaults();
        setSlidersToVariables();
    }
    /**
     * updates the note limit labels to the note on the sliders
     */
    public void setLabels()
    {
        for(int i=0; i<4; i++)
        {
            limitLabels[i].setText((NoteSymbol.makeNoteSymbol(new Note(handLimits[i].getValue())).toString()));
        }
    }
    /**
     * Sets the sliders to the values in the variables
     */
    public void setSlidersToVariables()
    {
        syncFromSettings();
        handLimits[0].setValue(leftHandLowerLimit);
        handLimits[2].setValue(rightHandLowerLimit);
        handLimits[1].setValue(leftHandUpperLimit);
        handLimits[3].setValue(rightHandUpperLimit);
        handSpreads[0].setValue(leftHandSpread);
        handSpreads[1].setValue(rightHandSpread);
        handNotes[0].setValue(leftHandMinNotes);
        handNotes[1].setValue(leftHandMaxNotes);
        handNotes[2].setValue(rightHandMinNotes);
        handNotes[3].setValue(rightHandMaxNotes);
        //voice leading controls
        preferredMotionSlider.setValue(preferredMotion);
        motionRange.setValue(preferredMotionRange);
        voiceLeadingWeights[0].setValue((int)(previousVoicingMultiplier*10));// multiplier for notes used in previous voicing
        voiceLeadingWeights[1].setValue((int)(halfStepAwayMultiplier*10));
        voiceLeadingWeights[2].setValue((int)(fullStepAwayMultiplier*10));
        //voicing control
        leftColorPrioritySlider.setValue(leftColorPriority);//priority of any color note
        rightColorPrioritySlider.setValue(rightColorPriority);//priority of any color note
        maxPrioritySlider.setValue(maxPriority);//max priority a note in the priority array can have
        priorityWeighting.setValue((int)(priorityMultiplier*10));//should be between 0 and 1, multiply this by the index in priority array, subtract result from max priority to get note priority
        repeatedNoteProbability.setValue((int)(repeatMultiplier*10));
        closeNoteReducer[0].setValue((int)(halfStepReducer*10));
        closeNoteReducer[1].setValue((int)(fullStepReducer*10));
        invertBox.setState(invertM9);
    }
    /**
     * points sliders we declared and named to auto-created sliders from gui designer.
     */
     public void associateSliders()
    {
        handLimits=new JSlider[4];
        handSpreads=new JSlider[2];
        handNotes=new JSlider[4];
        voiceLeadingWeights=new JSlider[3];
        handLimits[0]=LHLLSlider;
        handLimits[1]=LHULSlider;
        handLimits[2]=RHLLSlider;
        handLimits[3]=RHULSlider;
        handSpreads[0]=LHStretchSlider;
        handSpreads[1]=RHStretchSlider;
        handNotes[0]=LHMinNotesSlider;
        handNotes[1]=LHMaxNotesSlider;
        handNotes[2]=RHMinNotesSlider;
        handNotes[3]=RHMaxNotesSlider;
        
        //voice leading controls
        preferredMotionSlider=PrefMotionDirSlider;
        motionRange=PrefMotionDistSlider;
        voiceLeadingWeights[0]=PrevChordPrioritySlider;// multiplier for notes used in previous voicing
        voiceLeadingWeights[1]=HalfStepAwaySlider;
        voiceLeadingWeights[2]=WholeStepAwaySlider;
        //voicing control
        leftColorPrioritySlider=LHColorPriority;//priority of any color note
        rightColorPrioritySlider=RHColorPriority;
        maxPrioritySlider=ChordToneMaxPrioritySlider;
        priorityWeighting=ChordTonePriorityWeightSlider;
        repeatedNoteProbability=ProbSameNoteTwoOctavesSlider;
        limitLabels=new JLabel[4];
        limitLabels[0]=LHLLNote;
        limitLabels[1]=LHULNote;
        limitLabels[2]=RHLLNote;
        limitLabels[3]=RHULNote;
        closeNoteReducer=new JSlider[2];
        closeNoteReducer[0]=HalfStepRedSlider;
        closeNoteReducer[1]=WholeStepRedSlider;
        
        
    }
     /**
      * ensures the user's settings make sense logically (avoids sending the voicing program impossible settings
     */
    public void checkSliders()
    {
       if(handLimits[1].getValue()<handLimits[0].getValue()+handSpreads[0].getValue())
           handLimits[1].setValue(handLimits[0].getValue()+handSpreads[0].getValue());
       if(handLimits[3].getValue()<handLimits[2].getValue()+handSpreads[1].getValue())
           handLimits[3].setValue(handLimits[2].getValue()+handSpreads[1].getValue());
       if(handNotes[0].getValue()>handNotes[1].getValue())
           handNotes[0].setValue(handNotes[1].getValue());
       if(handNotes[2].getValue()>handNotes[3].getValue())
           handNotes[2].setValue(handNotes[3].getValue());
       
    }
    /**
     * saves values in sliders to variables.
     */
    public void saveSlidersToVariables()
    {
        checkSliders();
        leftHandLowerLimit=handLimits[0].getValue();
        rightHandLowerLimit=handLimits[2].getValue();
        leftHandUpperLimit=handLimits[1].getValue();
        rightHandUpperLimit=handLimits[3].getValue();
        leftHandSpread=handSpreads[0].getValue();
        rightHandSpread=handSpreads[1].getValue();
        leftHandMinNotes=handNotes[0].getValue();
        leftHandMaxNotes=handNotes[1].getValue();
        rightHandMinNotes=handNotes[2].getValue();
        rightHandMaxNotes=handNotes[3].getValue();
        //voice leading controls
        preferredMotion=preferredMotionSlider.getValue();
        preferredMotionRange=motionRange.getValue();
        previousVoicingMultiplier=voiceLeadingWeights[0].getValue()/10.0;// multiplier for notes used in previous voicing
        halfStepAwayMultiplier=voiceLeadingWeights[1].getValue()/10.0;
        fullStepAwayMultiplier=voiceLeadingWeights[2].getValue()/10.0;
        //voicing control
        leftColorPriority=leftColorPrioritySlider.getValue();//priority of any color note
        rightColorPriority=rightColorPrioritySlider.getValue();
        maxPriority=maxPrioritySlider.getValue();//max priority a note in the priority array can have
        priorityMultiplier=priorityWeighting.getValue()/10.0;//should be between 0 and 1, multiply this by the index in priority array, subtract result from max priority to get note priority
        repeatMultiplier=repeatedNoteProbability.getValue()/10.0;
        halfStepReducer=closeNoteReducer[0].getValue()/10.0;
        fullStepReducer=closeNoteReducer[1].getValue()/10.0;
        invertM9=invertBox.getState();
        syncToSettings();
        
    }
    private JSlider handLimits[];
    private JSlider handSpreads[];
    private JSlider handNotes[];
    private JSlider voiceLeadingWeights[];//index=change in half steps
    private JSlider preferredMotionSlider;
    private JSlider motionRange;
    private JSlider leftColorPrioritySlider;
    private JSlider rightColorPrioritySlider;
    private JSlider maxPrioritySlider;
    private JSlider repeatedNoteProbability;
    private JSlider priorityWeighting;
    private JSlider closeNoteReducer[];
    private JLabel limitLabels[];
    //hand params
    private int leftHandLowerLimit;
    private int rightHandLowerLimit;
    private int leftHandUpperLimit;
    private int rightHandUpperLimit;
    private int leftHandSpread;
    private int rightHandSpread;
    private int leftHandMinNotes;
    private int leftHandMaxNotes;
    private int rightHandMinNotes;
    private int rightHandMaxNotes;
    //voice leading controls
    private int preferredMotion;
    private int preferredMotionRange;
    private double previousVoicingMultiplier;// multiplier for notes used in previous voicing
    private double halfStepAwayMultiplier;
    private double fullStepAwayMultiplier;
    //voicing control
    private int leftColorPriority;//priority of any color note
    private int rightColorPriority;//priority of any color note
    private int maxPriority;//max priority a note in the priority array can have
    private double priorityMultiplier;//should be between 0 and 1, multiply this by the index in priority array, subtract result from max priority to get note priority
    private double repeatMultiplier;
    private double halfStepReducer;
    private double fullStepReducer;
    private boolean invertM9;
    private AutomaticVoicingSettings avs;

    public AutomaticVoicingSettings getVoicingSettings() {
        return avs;
    }

    public void setAutomaticVoicingSettings(AutomaticVoicingSettings avs) {
        this.avs = avs;
    }

    public double getHalfStepReducer() {
        return halfStepReducer;
    }

    public void setHalfStepReducer(double halfStepReducer) {
        this.halfStepReducer = halfStepReducer;
    }

    public double getFullStepReducer() {
        return fullStepReducer;
    }

    public void setFullStepReducer(double fullStepReducer) {
        this.fullStepReducer = fullStepReducer;
    }

    public int getLeftHandLowerLimit() {
        return leftHandLowerLimit;
    }

    public void setLeftHandLowerLimit(int leftHandLowerLimit) {
        this.leftHandLowerLimit = leftHandLowerLimit;
    }

    public int getRightHandLowerLimit() {
        return rightHandLowerLimit;
    }

    public void setRightHandLowerLimit(int rightHandLowerLimit) {
        this.rightHandLowerLimit = rightHandLowerLimit;
    }

    public int getLeftHandUpperLimit() {
        return leftHandUpperLimit;
    }

    public void setLeftHandUpperLimit(int leftHandUpperLimit) {
        this.leftHandUpperLimit = leftHandUpperLimit;
    }

    public int getRightHandUpperLimit() {
        return rightHandUpperLimit;
    }

    public void setRightHandUpperLimit(int rightHandUpperLimit) {
        this.rightHandUpperLimit = rightHandUpperLimit;
    }

    public int getLeftHandSpread() {
        return leftHandSpread;
    }

    public void setLeftHandSpread(int leftHandSpread) {
        this.leftHandSpread = leftHandSpread;
    }

    public int getRightHandSpread() {
        return rightHandSpread;
    }

    public void setRightHandSpread(int rightHandSpread) {
        this.rightHandSpread = rightHandSpread;
    }

    public int getLeftHandMinNotes() {
        return leftHandMinNotes;
    }

    public void setLeftHandMinNotes(int leftHandMinNotes) {
        this.leftHandMinNotes = leftHandMinNotes;
    }

    public int getLeftHandMaxNotes() {
        return leftHandMaxNotes;
    }

    public void setLeftHandMaxNotes(int leftHandMaxNotes) {
        this.leftHandMaxNotes = leftHandMaxNotes;
    }

    public int getRightHandMinNotes() {
        return rightHandMinNotes;
    }

    public void setRightHandMinNotes(int rightHandMinNotes) {
        this.rightHandMinNotes = rightHandMinNotes;
    }

    public int getRightHandMaxNotes() {
        return rightHandMaxNotes;
    }

    public void setRightHandMaxNotes(int rightHandMaxNotes) {
        this.rightHandMaxNotes = rightHandMaxNotes;
    }

    public int getPreferredMotion() {
        return preferredMotion;
    }

    public void setPreferredMotion(int preferredMotion) {
        this.preferredMotion = preferredMotion;
    }

    public int getPreferredMotionRange() {
        return preferredMotionRange;
    }

    public void setPreferredMotionRange(int preferredMotionRange) {
        this.preferredMotionRange = preferredMotionRange;
    }

    public double getPreviousVoicingMultiplier() {
        return previousVoicingMultiplier;
    }

    public void setPreviousVoicingMultiplier(double previousVoicingMultiplier) {
        this.previousVoicingMultiplier = previousVoicingMultiplier;
    }

    public double getHalfStepAwayMultiplier() {
        return halfStepAwayMultiplier;
    }

    public void setHalfStepAwayMultiplier(double halfStepAwayMultiplier) {
        this.halfStepAwayMultiplier = halfStepAwayMultiplier;
    }

    public double getFullStepAwayMultiplier() {
        return fullStepAwayMultiplier;
    }

    public void setFullStepAwayMultiplier(double fullStepAwayMultiplier) {
        this.fullStepAwayMultiplier = fullStepAwayMultiplier;
    }

    public int getLeftColorPriority() {
        return leftColorPriority;
    }

    public void setLeftColorPriority(int leftColorPriority) {
        this.leftColorPriority = leftColorPriority;
    }

    public int getRightColorPriority() {
        return rightColorPriority;
    }

    public void setRightColorPriority(int rightColorPriority) {
        this.rightColorPriority = rightColorPriority;
    }

   

    public int getMaxPriority() {
        return maxPriority;
    }

    public void setMaxPriority(int maxPriority) {
        this.maxPriority = maxPriority;
    }

    public double getPriorityMultiplier() {
        return priorityMultiplier;
    }

    public void setPriorityMultiplier(double priorityMultiplier) {
        this.priorityMultiplier = priorityMultiplier;
    }

    public double getRepeatMultiplier() {
        return repeatMultiplier;
    }

    public void setRepeatMultiplier(double repeatMultiplier) {
        this.repeatMultiplier = repeatMultiplier;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 10), new java.awt.Dimension(0, 10), new java.awt.Dimension(32767, 10));
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        LHULSlider = new javax.swing.JSlider();
        LHULNote = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        LHLLSlider = new javax.swing.JSlider();
        LHLLNote = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        LHStretchSlider = new javax.swing.JSlider();
        jPanel6 = new javax.swing.JPanel();
        LHMinNotesSlider = new javax.swing.JSlider();
        jPanel7 = new javax.swing.JPanel();
        LHMaxNotesSlider = new javax.swing.JSlider();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        RHULSlider = new javax.swing.JSlider();
        RHULNote = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        RHLLSlider = new javax.swing.JSlider();
        RHLLNote = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        RHStretchSlider = new javax.swing.JSlider();
        jPanel12 = new javax.swing.JPanel();
        RHMinNotesSlider = new javax.swing.JSlider();
        jPanel13 = new javax.swing.JPanel();
        RHMaxNotesSlider = new javax.swing.JSlider();
        jPanel14 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        PrevChordPrioritySlider = new javax.swing.JSlider();
        jPanel16 = new javax.swing.JPanel();
        HalfStepAwaySlider = new javax.swing.JSlider();
        jPanel17 = new javax.swing.JPanel();
        WholeStepAwaySlider = new javax.swing.JSlider();
        jPanel18 = new javax.swing.JPanel();
        PrefMotionDirSlider = new javax.swing.JSlider();
        jPanel19 = new javax.swing.JPanel();
        PrefMotionDistSlider = new javax.swing.JSlider();
        jPanel20 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        ChordToneMaxPrioritySlider = new javax.swing.JSlider();
        jPanel22 = new javax.swing.JPanel();
        ChordTonePriorityWeightSlider = new javax.swing.JSlider();
        jPanel23 = new javax.swing.JPanel();
        LHColorPriority = new javax.swing.JSlider();
        jPanel24 = new javax.swing.JPanel();
        RHColorPriority = new javax.swing.JSlider();
        jPanel25 = new javax.swing.JPanel();
        ProbSameNoteTwoOctavesSlider = new javax.swing.JSlider();
        jPanel26 = new javax.swing.JPanel();
        HalfStepRedSlider = new javax.swing.JSlider();
        jPanel27 = new javax.swing.JPanel();
        WholeStepRedSlider = new javax.swing.JSlider();
        jPanel28 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        closeB = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        invertBox = new java.awt.Checkbox();

        setBounds(new java.awt.Rectangle(0, 0, 1300, 500));
        getContentPane().setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(filler1, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pianist Hand Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 14))); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Left Hand", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 12))); // NOI18N
        jPanel4.setName(""); // NOI18N
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Upper Limit"));
        jPanel2.setToolTipText("Absolute Upper note limits for voicings in a hand.");
        jPanel2.setLayout(new java.awt.GridBagLayout());

        LHULSlider.setMajorTickSpacing(12);
        LHULSlider.setMaximum(108);
        LHULSlider.setMinimum(21);
        LHULSlider.setMinorTickSpacing(1);
        LHULSlider.setPaintTicks(true);
        LHULSlider.setSnapToTicks(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(LHULSlider, gridBagConstraints);

        LHULNote.setText("A#1");
        LHULNote.setMinimumSize(null);
        LHULNote.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(LHULNote, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(jPanel2, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Lower Limit"));
        jPanel3.setToolTipText("Absolute Lower note limits for voicings in a hand.");
        jPanel3.setLayout(new java.awt.GridBagLayout());

        LHLLSlider.setMajorTickSpacing(12);
        LHLLSlider.setMaximum(108);
        LHLLSlider.setMinimum(21);
        LHLLSlider.setMinorTickSpacing(1);
        LHLLSlider.setPaintTicks(true);
        LHLLSlider.setSnapToTicks(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(LHLLSlider, gridBagConstraints);

        LHLLNote.setText("A#1");
        LHLLNote.setMinimumSize(null);
        LHLLNote.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(LHLLNote, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(jPanel3, gridBagConstraints);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Stretch Limit (Semitones)"));
        jPanel5.setToolTipText("The distance between the lowest and highest note for one voicing in a hand.");
        jPanel5.setLayout(new java.awt.GridBagLayout());

        LHStretchSlider.setMajorTickSpacing(7);
        LHStretchSlider.setMaximum(19);
        LHStretchSlider.setMinimum(7);
        LHStretchSlider.setMinorTickSpacing(1);
        LHStretchSlider.setPaintLabels(true);
        LHStretchSlider.setPaintTicks(true);
        LHStretchSlider.setSnapToTicks(true);
        LHStretchSlider.setMinimumSize(null);
        LHStretchSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel5.add(LHStretchSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(jPanel5, gridBagConstraints);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Minimum Notes"));
        jPanel6.setToolTipText("Minimum number of notes in the hand's voicing.");
        jPanel6.setLayout(new java.awt.GridBagLayout());

        LHMinNotesSlider.setMajorTickSpacing(5);
        LHMinNotesSlider.setMaximum(5);
        LHMinNotesSlider.setMinorTickSpacing(1);
        LHMinNotesSlider.setPaintLabels(true);
        LHMinNotesSlider.setPaintTicks(true);
        LHMinNotesSlider.setSnapToTicks(true);
        LHMinNotesSlider.setMinimumSize(null);
        LHMinNotesSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel6.add(LHMinNotesSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(jPanel6, gridBagConstraints);

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Maximum Notes"));
        jPanel7.setToolTipText("Maximum number of notes in the hand's voicing.");
        jPanel7.setLayout(new java.awt.GridBagLayout());

        LHMaxNotesSlider.setMajorTickSpacing(5);
        LHMaxNotesSlider.setMaximum(5);
        LHMaxNotesSlider.setMinorTickSpacing(1);
        LHMaxNotesSlider.setPaintLabels(true);
        LHMaxNotesSlider.setPaintTicks(true);
        LHMaxNotesSlider.setSnapToTicks(true);
        LHMaxNotesSlider.setMinimumSize(null);
        LHMaxNotesSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel7.add(LHMaxNotesSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel4.add(jPanel7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(jPanel4, gridBagConstraints);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Right Hand", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 12))); // NOI18N
        jPanel8.setName(""); // NOI18N
        jPanel8.setLayout(new java.awt.GridBagLayout());

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Upper Limit"));
        jPanel9.setToolTipText("Absolute Upper note limits for voicings in a hand.");
        jPanel9.setLayout(new java.awt.GridBagLayout());

        RHULSlider.setMajorTickSpacing(12);
        RHULSlider.setMaximum(108);
        RHULSlider.setMinimum(21);
        RHULSlider.setMinorTickSpacing(1);
        RHULSlider.setPaintTicks(true);
        RHULSlider.setSnapToTicks(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(RHULSlider, gridBagConstraints);

        RHULNote.setText("A#1");
        RHULNote.setMinimumSize(null);
        RHULNote.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel9.add(RHULNote, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel8.add(jPanel9, gridBagConstraints);

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Lower Limit"));
        jPanel10.setToolTipText("Absolute Lower note limits for voicings in a hand.");
        jPanel10.setLayout(new java.awt.GridBagLayout());

        RHLLSlider.setMajorTickSpacing(12);
        RHLLSlider.setMaximum(108);
        RHLLSlider.setMinimum(21);
        RHLLSlider.setMinorTickSpacing(1);
        RHLLSlider.setPaintTicks(true);
        RHLLSlider.setSnapToTicks(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel10.add(RHLLSlider, gridBagConstraints);

        RHLLNote.setText("A#1");
        RHLLNote.setMinimumSize(null);
        RHLLNote.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel10.add(RHLLNote, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel8.add(jPanel10, gridBagConstraints);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Stretch Limit (Semitones)"));
        jPanel11.setToolTipText("The distance between the lowest and highest note for one voicing in a hand.");
        jPanel11.setLayout(new java.awt.GridBagLayout());

        RHStretchSlider.setMajorTickSpacing(5);
        RHStretchSlider.setMaximum(19);
        RHStretchSlider.setMinimum(7);
        RHStretchSlider.setMinorTickSpacing(1);
        RHStretchSlider.setPaintLabels(true);
        RHStretchSlider.setPaintTicks(true);
        RHStretchSlider.setSnapToTicks(true);
        RHStretchSlider.setMinimumSize(null);
        RHStretchSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel11.add(RHStretchSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel8.add(jPanel11, gridBagConstraints);

        jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Minimum Notes"));
        jPanel12.setToolTipText("Minimum number of notes in the hand's voicing.");
        jPanel12.setLayout(new java.awt.GridBagLayout());

        RHMinNotesSlider.setMajorTickSpacing(5);
        RHMinNotesSlider.setMaximum(5);
        RHMinNotesSlider.setMinorTickSpacing(1);
        RHMinNotesSlider.setPaintLabels(true);
        RHMinNotesSlider.setPaintTicks(true);
        RHMinNotesSlider.setSnapToTicks(true);
        RHMinNotesSlider.setMinimumSize(null);
        RHMinNotesSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel12.add(RHMinNotesSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel8.add(jPanel12, gridBagConstraints);

        jPanel13.setBorder(javax.swing.BorderFactory.createTitledBorder("Maximum Notes"));
        jPanel13.setToolTipText("Maximum number of notes in the hand's voicing.");
        jPanel13.setLayout(new java.awt.GridBagLayout());

        RHMaxNotesSlider.setMajorTickSpacing(5);
        RHMaxNotesSlider.setMaximum(5);
        RHMaxNotesSlider.setMinorTickSpacing(1);
        RHMaxNotesSlider.setPaintLabels(true);
        RHMaxNotesSlider.setPaintTicks(true);
        RHMaxNotesSlider.setSnapToTicks(true);
        RHMaxNotesSlider.setMinimumSize(null);
        RHMaxNotesSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel13.add(RHMaxNotesSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel8.add(jPanel13, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel1.add(jPanel8, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel1, gridBagConstraints);

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Voice Leading Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 14))); // NOI18N
        jPanel14.setLayout(new java.awt.GridBagLayout());

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Previous Chord Priority"));
        jPanel15.setToolTipText("Rating of last chord's significance for voice leading.");
        jPanel15.setLayout(new java.awt.GridBagLayout());

        PrevChordPrioritySlider.setMajorTickSpacing(10);
        PrevChordPrioritySlider.setMaximum(50);
        PrevChordPrioritySlider.setMinorTickSpacing(1);
        PrevChordPrioritySlider.setPaintTicks(true);
        PrevChordPrioritySlider.setSnapToTicks(true);
        PrevChordPrioritySlider.setMinimumSize(null);
        PrevChordPrioritySlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel15.add(PrevChordPrioritySlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel14.add(jPanel15, gridBagConstraints);

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("Half Step Away Priority"));
        jPanel16.setToolTipText("Mulitplier for any note a half-step away from previously voiced note.");
        jPanel16.setLayout(new java.awt.GridBagLayout());

        HalfStepAwaySlider.setMajorTickSpacing(10);
        HalfStepAwaySlider.setMaximum(50);
        HalfStepAwaySlider.setMinorTickSpacing(1);
        HalfStepAwaySlider.setPaintTicks(true);
        HalfStepAwaySlider.setSnapToTicks(true);
        HalfStepAwaySlider.setMinimumSize(null);
        HalfStepAwaySlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel16.add(HalfStepAwaySlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel14.add(jPanel16, gridBagConstraints);

        jPanel17.setBorder(javax.swing.BorderFactory.createTitledBorder("Whole Step Away Priority"));
        jPanel17.setToolTipText("Mulitplier for any note a whole step away from previously voiced note.");
        jPanel17.setLayout(new java.awt.GridBagLayout());

        WholeStepAwaySlider.setMajorTickSpacing(10);
        WholeStepAwaySlider.setMaximum(50);
        WholeStepAwaySlider.setMinorTickSpacing(1);
        WholeStepAwaySlider.setPaintTicks(true);
        WholeStepAwaySlider.setSnapToTicks(true);
        WholeStepAwaySlider.setMinimumSize(null);
        WholeStepAwaySlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel17.add(WholeStepAwaySlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel14.add(jPanel17, gridBagConstraints);

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder("Preferred Motion Direction"));
        jPanel18.setToolTipText("Preferred direction of chord progression");
        jPanel18.setLayout(new java.awt.GridBagLayout());

        PrefMotionDirSlider.setMaximum(5);
        PrefMotionDirSlider.setMinimum(-5);
        PrefMotionDirSlider.setMinorTickSpacing(1);
        PrefMotionDirSlider.setPaintLabels(true);
        PrefMotionDirSlider.setPaintTicks(true);
        PrefMotionDirSlider.setSnapToTicks(true);
        PrefMotionDirSlider.setMinimumSize(null);
        PrefMotionDirSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel18.add(PrefMotionDirSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel14.add(jPanel18, gridBagConstraints);

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder("Preferred Motion Distance"));
        jPanel19.setToolTipText("Preferred distance of chord progression");
        jPanel19.setLayout(new java.awt.GridBagLayout());

        PrefMotionDistSlider.setMaximum(5);
        PrefMotionDistSlider.setMinorTickSpacing(1);
        PrefMotionDistSlider.setPaintLabels(true);
        PrefMotionDistSlider.setPaintTicks(true);
        PrefMotionDistSlider.setSnapToTicks(true);
        PrefMotionDistSlider.setMinimumSize(null);
        PrefMotionDistSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel19.add(PrefMotionDistSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel14.add(jPanel19, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel14, gridBagConstraints);

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Voicing Settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 3, 14))); // NOI18N
        jPanel20.setLayout(new java.awt.GridBagLayout());

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder("Chord Tone Maximum Priority"));
        jPanel21.setToolTipText("Highest priority weighting for any note in the chord.");
        jPanel21.setLayout(new java.awt.GridBagLayout());

        ChordToneMaxPrioritySlider.setMaximum(10);
        ChordToneMaxPrioritySlider.setMinorTickSpacing(1);
        ChordToneMaxPrioritySlider.setPaintLabels(true);
        ChordToneMaxPrioritySlider.setPaintTicks(true);
        ChordToneMaxPrioritySlider.setSnapToTicks(true);
        ChordToneMaxPrioritySlider.setMinimumSize(null);
        ChordToneMaxPrioritySlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel21.add(ChordToneMaxPrioritySlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel20.add(jPanel21, gridBagConstraints);

        jPanel22.setBorder(javax.swing.BorderFactory.createTitledBorder("Chord Tone Priority Weighting"));
        jPanel22.setToolTipText("Desired weighting for priority notes in a chord");
        jPanel22.setLayout(new java.awt.GridBagLayout());

        ChordTonePriorityWeightSlider.setMajorTickSpacing(10);
        ChordTonePriorityWeightSlider.setMaximum(15);
        ChordTonePriorityWeightSlider.setMinorTickSpacing(1);
        ChordTonePriorityWeightSlider.setPaintTicks(true);
        ChordTonePriorityWeightSlider.setSnapToTicks(true);
        ChordTonePriorityWeightSlider.setMinimumSize(null);
        ChordTonePriorityWeightSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel22.add(ChordTonePriorityWeightSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel20.add(jPanel22, gridBagConstraints);

        jPanel23.setBorder(javax.swing.BorderFactory.createTitledBorder("Left Hand Color Note Priority"));
        jPanel23.setToolTipText("Desired weighting for color notes in this hand.");
        jPanel23.setLayout(new java.awt.GridBagLayout());

        LHColorPriority.setMaximum(10);
        LHColorPriority.setMinorTickSpacing(1);
        LHColorPriority.setPaintLabels(true);
        LHColorPriority.setPaintTicks(true);
        LHColorPriority.setSnapToTicks(true);
        LHColorPriority.setMinimumSize(null);
        LHColorPriority.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel23.add(LHColorPriority, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel20.add(jPanel23, gridBagConstraints);

        jPanel24.setBorder(javax.swing.BorderFactory.createTitledBorder("Right Hand Color Note Priority"));
        jPanel24.setToolTipText("Desired weighting for color notes in this hand.");
        jPanel24.setLayout(new java.awt.GridBagLayout());

        RHColorPriority.setMaximum(10);
        RHColorPriority.setMinorTickSpacing(1);
        RHColorPriority.setPaintTicks(true);
        RHColorPriority.setSnapToTicks(true);
        RHColorPriority.setMinimumSize(null);
        RHColorPriority.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel24.add(RHColorPriority, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel20.add(jPanel24, gridBagConstraints);

        jPanel25.setBorder(javax.swing.BorderFactory.createTitledBorder("Probability of Same Note In Two Octaves"));
        jPanel25.setToolTipText("Probability of playing the same note in two separate octaves.");
        jPanel25.setLayout(new java.awt.GridBagLayout());

        ProbSameNoteTwoOctavesSlider.setMaximum(10);
        ProbSameNoteTwoOctavesSlider.setMinorTickSpacing(1);
        ProbSameNoteTwoOctavesSlider.setPaintLabels(true);
        ProbSameNoteTwoOctavesSlider.setPaintTicks(true);
        ProbSameNoteTwoOctavesSlider.setSnapToTicks(true);
        ProbSameNoteTwoOctavesSlider.setMinimumSize(null);
        ProbSameNoteTwoOctavesSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel25.add(ProbSameNoteTwoOctavesSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel20.add(jPanel25, gridBagConstraints);

        jPanel26.setBorder(javax.swing.BorderFactory.createTitledBorder("Half Step Reduction"));
        jPanel26.setToolTipText("Probability that two notes a half-step apart will be played.");
        jPanel26.setLayout(new java.awt.GridBagLayout());

        HalfStepRedSlider.setMaximum(10);
        HalfStepRedSlider.setMinorTickSpacing(1);
        HalfStepRedSlider.setPaintTicks(true);
        HalfStepRedSlider.setSnapToTicks(true);
        HalfStepRedSlider.setMinimumSize(null);
        HalfStepRedSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel26.add(HalfStepRedSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel20.add(jPanel26, gridBagConstraints);

        jPanel27.setBorder(javax.swing.BorderFactory.createTitledBorder("Whole Step Reduction"));
        jPanel27.setToolTipText("Probability that two notes a whole-step apart will be played.");
        jPanel27.setLayout(new java.awt.GridBagLayout());

        WholeStepRedSlider.setMaximum(10);
        WholeStepRedSlider.setMinorTickSpacing(1);
        WholeStepRedSlider.setPaintTicks(true);
        WholeStepRedSlider.setSnapToTicks(true);
        WholeStepRedSlider.setMinimumSize(null);
        WholeStepRedSlider.setPreferredSize(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        jPanel27.add(WholeStepRedSlider, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        jPanel20.add(jPanel27, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(jPanel20, gridBagConstraints);

        jPanel28.setLayout(new java.awt.GridLayout(15, 1));

        jButton1.setText("Apply");
        jButton1.setMinimumSize(null);
        jButton1.setPreferredSize(null);
        jPanel28.add(jButton1);

        jButton2.setText("Cancel");
        jButton2.setMinimumSize(null);
        jButton2.setPreferredSize(null);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel28.add(jButton2);

        jButton3.setText("Reset Defaults");
        jButton3.setMinimumSize(null);
        jButton3.setPreferredSize(null);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel28.add(jButton3);

        jButton4.setText("Load Preset");
        jButton4.setMinimumSize(null);
        jButton4.setPreferredSize(null);
        jPanel28.add(jButton4);

        jButton5.setText("Save Settings");
        jButton5.setMinimumSize(null);
        jButton5.setPreferredSize(null);
        jPanel28.add(jButton5);

        closeB.setText("Close");
        closeB.setMinimumSize(null);
        closeB.setPreferredSize(null);
        closeB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBActionPerformed(evt);
            }
        });
        jPanel28.add(closeB);

        jButton8.setText("Load Style Default");
        jPanel28.add(jButton8);

        jButton6.setText("Save To Style Default");
        jPanel28.add(jButton6);

        jButton7.setText("Set Style Default");
        jButton7.setToolTipText("");
        jPanel28.add(jButton7);

        invertBox.setLabel("Invert Minor 9ths");
        invertBox.setName("invertMinorNinth"); // NOI18N
        jPanel28.add(invertBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        getContentPane().add(jPanel28, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton3ActionPerformed

    private void closeBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeBActionPerformed
        this.dispose();
    }//GEN-LAST:event_closeBActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ControlPanelFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ControlPanelFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ControlPanelFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ControlPanelFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ControlPanelFrame(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider ChordToneMaxPrioritySlider;
    private javax.swing.JSlider ChordTonePriorityWeightSlider;
    private javax.swing.JSlider HalfStepAwaySlider;
    private javax.swing.JSlider HalfStepRedSlider;
    private javax.swing.JSlider LHColorPriority;
    private javax.swing.JLabel LHLLNote;
    private javax.swing.JSlider LHLLSlider;
    private javax.swing.JSlider LHMaxNotesSlider;
    private javax.swing.JSlider LHMinNotesSlider;
    private javax.swing.JSlider LHStretchSlider;
    private javax.swing.JLabel LHULNote;
    private javax.swing.JSlider LHULSlider;
    private javax.swing.JSlider PrefMotionDirSlider;
    private javax.swing.JSlider PrefMotionDistSlider;
    private javax.swing.JSlider PrevChordPrioritySlider;
    private javax.swing.JSlider ProbSameNoteTwoOctavesSlider;
    private javax.swing.JSlider RHColorPriority;
    private javax.swing.JLabel RHLLNote;
    private javax.swing.JSlider RHLLSlider;
    private javax.swing.JSlider RHMaxNotesSlider;
    private javax.swing.JSlider RHMinNotesSlider;
    private javax.swing.JSlider RHStretchSlider;
    private javax.swing.JLabel RHULNote;
    private javax.swing.JSlider RHULSlider;
    private javax.swing.JSlider WholeStepAwaySlider;
    private javax.swing.JSlider WholeStepRedSlider;
    private javax.swing.JButton closeB;
    private javax.swing.Box.Filler filler1;
    private java.awt.Checkbox invertBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    // End of variables declaration//GEN-END:variables
}
