/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College XML export code
 * is also Copyright (C) 2009-2010 Nicolas Froment (aka Lasconic).
 *
 * Impro-Visor is free software; you can redistribute it and/or modifyc it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package imp.gui;

import static imp.Constants.C4;
import imp.ImproVisor;
import imp.com.PlayPartCommand;
import imp.com.PlayScoreCommand;
import imp.com.RectifyPitchesCommand;
import imp.data.ChordPart;
import java.util.ArrayList;
import imp.data.MelodyPart;
import imp.data.MidiSynth;
import imp.data.Note;
import imp.data.ResponseGenerator;
import imp.data.Rest;
import imp.data.Score;
import imp.gui.Notate;
import imp.lickgen.transformations.Transform;
import imp.util.GrammarFilter;
import imp.util.MidiPlayListener;
import imp.util.TransformFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import javax.sound.midi.InvalidMidiDataException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Stack;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import jm.midi.event.Event;

/**
 * This class embodies the UI and functionality  of interactive trading.
 * 
 * An instance of TradingWindow works by keeping a reference to the instance
 * of notate that instantiated itself. Each  instance of TradingWindow is 
 * destroyed when it's associated UI window is closed. When interactive trading
 * is initialized (the method '.startTrading()' is called),  notate is triggered
 * to play its current open chorus; trading is made possible 
 * by dispatching events based upon the position of the play head in said 
 * chorus. Events are scheduled in three main phases, each phase with 
 * its respective method: 
 *                  User turn  -    When the user plays. During this phase,
 *                                  the user input is recorded into 
 *                                  the instance variable 'tradeScore'.
 *                                      * associated method: .userTurn()
 *                  Processing -    When processing takes place. During this
 *                                  phase, user input is no longer recorded. 
 *                                  TradeScore is manipulated to produce a 
 *                                  finalized response for the computer to play.
 *                                      * associated method: .processInput()
 *                  Computer Turn - When the computer plays. During this phase,
 *                                  the response finalized in processing phase
 *                                  is played. When the computer is first, 
 *                                  some solo pre-generated solo is used.
 *                                      * associated method: .computerTurn()
 * @author Zachary Kondak
 */
public class TradingWindow
        extends javax.swing.JFrame {

    static Notate notate;

    private int melodyNum = 0;
    private ArrayList<MelodyPart> melodies = new ArrayList<MelodyPart>();
    private LinkedList<MelodyPart> hotswapper = new LinkedList<MelodyPart>();
    private Score tradeScore;
    private Stack<Integer> triggers = new Stack();
    //length of trading in measures

    private Integer scoreLength;
    private Integer slotsPerMeasure;
    private Integer slotsPerTurn;
    private Integer adjustedLength;
    private Integer slotsForProcessing;
    private Integer numberOfTurns;
    private Integer measures;
    private int[] metre;
    private ChordPart soloChords;
    private ChordPart responseChords;
    private MelodyPart aMelodyPart;
    private MidiSynth midiSynth;
    private long slotDelay;
    private boolean isTrading;
    private boolean isUserInputError;
    private boolean firstPlay;
    private boolean isUserLeading;
    private TradePhase phase;
    private String tradeMode;
    private Transform transform;
    private PlayScoreCommand playCommand;

    //magic values
    private int endLimitIndex = -1;
    private boolean isSwing = false;
    private Integer snapResolution = 2;
    public static final int zero = 0;
    public static final int one = 1;
    public static final int DEFAULT_TRADE_LENGTH = 4;
    
    
    public enum TradePhase {
        USER_TURN,
        PROCESS_INPUT,
        COMPUTER_TURN
    }
    
    public enum TradeMode {
        REPEAT,
        REPEAT_AND_RECTIFY
    }
    
    
    /**
     * Creates new form TradingWindow
     * @param notate
     */
    public TradingWindow(Notate notate) {
        initComponents();
        this.notate = notate;
        tradeScore = new Score();
        
        //defaults on open
        tradeMode = (String) tradeModeSelector.getSelectedItem();
        firstPlay = true;
        measures = DEFAULT_TRADE_LENGTH;
        isUserLeading = true;
    }


    /**
     * **no longer used, delay is now set automatically.**
     * Method converts a delay given in a factor of beats to delay in slots,
     * then this delay is saved into the instance variable 'slotDelay'
     * @param beatDelay delay (given in a factor of a beat) that is
     *                  incurred by calling the playScoreCommand
     */
    private void setSlotDelay(double beatDelay) {
        double doubleSlotsPerMeasure = (double) tradeScore.getSlotsPerMeasure();
        double beatsPerMeasure = (double) tradeScore.getBeatsPerMeasure();
        double slotsPerBeat = doubleSlotsPerMeasure / beatsPerMeasure;

        long newDelay = Math.round(slotsPerBeat * beatDelay);
        slotDelay = newDelay;
        //System.out.println(slotDelay);
    }

    /**
     * Called continuously throughout trading. This method acts as a primitive 
     * scheduler. Uses the stack 'triggers' to check if it is time to change
     * phases (userTurn, processInput, computerTurn)
     * @param e 
     */
    public void trackPlay(ActionEvent e) {
        long currentPosition = notate.getSlotInPlayback();
        //System.out.println(currentPosition);
        
        if (triggers.isEmpty() || currentPosition == scoreLength) {
            stopTrading();
        } else {
            long nextTrig = (long) triggers.peek();
            if (nextTrig <= currentPosition) {
                //System.out.println("long: " + nextTrig);
                triggers.pop();
                switchTurn();
            }
            //else System.out.println(triggers);
        }
    }

    /**
     * Switches between phases (userTurn, processInput, computerTurn). Called
     * by trackPlay.
     */
    public void switchTurn() {
        switch (phase) {
            case USER_TURN:
                processInput();
                break;
            case PROCESS_INPUT:
                computerTurn();
                break;
            case COMPUTER_TURN:
                userTurn();
                break;
            default:
                break;
                
        }
    }

    public void userTurn() {
        //System.out.println("User turn at slot: " + notate.getSlotInPlayback());
        phase = TradePhase.USER_TURN;
        int nextSection;
        if (triggers.isEmpty()) {
            nextSection = adjustedLength;
        } else {
            nextSection = triggers.peek() + slotsForProcessing;
        }
        //System.out.println("Chords extracted from chord prog from : " + nextSection + " to " + (nextSection + slotsPerTurn - one));
        soloChords = notate.getScore().getChordProg().extract(nextSection - slotsPerTurn, nextSection - one);
        responseChords = notate.getScore().getChordProg().extract(nextSection, nextSection + slotsPerTurn - one);
        aMelodyPart = new MelodyPart(slotsPerTurn);
        tradeScore = new Score("trading", notate.getTempo(), zero);
        tradeScore.setChordProg(responseChords);
        tradeScore.addPart(aMelodyPart);
        notate.initTradingRecorder(aMelodyPart);
        notate.enableRecording();
    }
    
    public void processInput() {
        //System.out.println("Process input at slot: " + notate.getSlotInPlayback());
        phase = TradePhase.PROCESS_INPUT;
        notate.stopRecording();
        
        tradeScore.setBassMuted(true);
        tradeScore.delPart(0);
        
        //snap? aMelodyPart = aMelodyPart.applyResolution(120);
        //System.out.println(chords);
        applyTradingMode();
        tradeScore.deleteChords();
        
        Long delayCopy = new Long(slotDelay);
        aMelodyPart = aMelodyPart.extract(delayCopy.intValue(), slotsPerTurn - one, true, true);
        tradeScore.addPart(aMelodyPart);
        
        playCommand = new PlayScoreCommand(
                tradeScore,
                zero,
                isSwing,
                midiSynth,
                notate,
                zero,
                notate.getTransposition(),
                false,
                endLimitIndex
        );
    }

    public void computerTurn() {
        
        long slotsBefore = notate.getSlotInPlayback();
        
        playCommand.execute();
        
        long slotsAfter = notate.getSlotInPlayback();
        
        //update delay
        slotDelay = (slotDelay + (slotsAfter - slotsBefore)) / 2;
        
        phase = TradePhase.COMPUTER_TURN;
    }

    /**
     * Starts interactive trading
     */
    public void startTrading() {
        //make this more general
        String musician = (String) musicianChooser.getSelectedItem();
        File directory = ImproVisor.getTransformDirectory();
        File file = new File(directory, musician+TransformFilter.EXTENSION);
        //String dir = System.getProperty("user.dir");
        //File file = new File(dir + "/transforms/"+musician+".transform");
        transform = new Transform(file);
        
        setIsUserLeading(userFirstButton.isSelected());
        slotsForProcessing = 240; // TODO, make editable
        startTradingButton.setText("StopTrading");
        isTrading = true;
        midiSynth = new MidiSynth(notate.getMidiManager());
        scoreLength = notate.getScoreLength();
        slotsPerMeasure = notate.getScore().getSlotsPerMeasure();
        metre = notate.getScore().getMetre();
        slotsPerTurn = measures * slotsPerMeasure;
        adjustedLength = scoreLength - (scoreLength % slotsPerTurn);
        numberOfTurns = adjustedLength / slotsPerTurn;
        populateTriggers();
        initDelay();
        //TODO Generate solo if computer goes first
        //make sure to mute chords
        notate.playScore();
        
        if (isUserLeading) {
            phase = TradePhase.COMPUTER_TURN;
        } else {
            //TODO make a nice comment
            phase = TradePhase.PROCESS_INPUT;
        }
    }
    
    private void initDelay(){
        ChordPart testChords = notate.getScore().copy().getChordProg().extract(zero, slotsPerMeasure - one);
        MelodyPart testMelody = new MelodyPart(slotsPerMeasure);
        Score testScore = new Score();
        testScore.addPart(testMelody);
        testScore.setChordProg(testChords);
        MelodyPart testSolo = new MelodyPart(slotsPerMeasure / 2);
        Note newNote = new Note(C4, true, slotsPerMeasure - one); 
        testSolo.addNote(newNote);
        Score soloScore = new Score();
        soloScore.addPart(testSolo);
        
        MidiSynth testMidiSynth = new MidiSynth(notate.getMidiManager());
        
        testMidiSynth.setMasterVolume(zero);
        
        PlayScoreCommand testCommand = new PlayScoreCommand(
                soloScore,
                zero,
                isSwing,
                midiSynth,
                notate,
                zero,
                notate.getTransposition(),
                false,
                endLimitIndex
        );
        
        testCommand.execute();
        
        new PlayScoreCommand(
                testScore,
                zero,
                isSwing,
                testMidiSynth,
                notate,
                zero,
                notate.getTransposition(),
                false,
                endLimitIndex
        ).execute();
        
        int oldVolume = midiSynth.getMasterVolume();
        
        midiSynth.setMasterVolume(zero);
        
        
        
        testCommand.execute();
        long slot1 = testMidiSynth.getSlot();
        testCommand.execute();
        long slot2 = testMidiSynth.getSlot();
        //System.out.println(slot2 - slot1);
        
        slotDelay = slot2 - slot1;
        
        midiSynth.stop("Trading Delay Initialization Complete");
        midiSynth.setMasterVolume(oldVolume);
    }
    
    private void populateTriggers(){
        //clear triggers
        triggers.clear();
        //populate trigger stack (scheduler)
        boolean computerTurnNext;
        if (numberOfTurns % 2 == zero) {
            //even number of turns
            if (isUserLeading) {
                //user turn first.
                //this seems couter-intuitive, but this is the case since we're
                //working from the end of the score (backwards)
                computerTurnNext = false;
                
            } else {
                //computer turn first.
                computerTurnNext = true;
            }
        } else {
            //odd number of turns
            if (isUserLeading) {
                //user turn first.
                computerTurnNext = true;
            } else {
                //computer turn first.
                computerTurnNext = false;
            }
        }
        for (int trigSlot = adjustedLength; trigSlot >= zero; trigSlot = trigSlot - slotsPerTurn) {
            triggers.push(trigSlot);
            if (computerTurnNext) {
                computerTurnNext = false;
                if (trigSlot != zero) {
                    triggers.push(trigSlot - slotsForProcessing);
                }
            } else {
                computerTurnNext = true;
            }
        }
        //System.out.println(triggers);
    }

    /**
     * Stops interactive trading
     */
    public void stopTrading() {
        startTradingButton.setText("StartTrading");
        //System.out.println("hi");
        isTrading = false;
        notate.stopRecording();
        notate.stopPlaying("stop trading");
        notate.getMidiRecorder().setDestination(null);
    }

    

    private void applyTradingMode() {
        tradeMode = (String) tradeModeSelector.getSelectedItem();
        ResponseGenerator generator = new ResponseGenerator(aMelodyPart, soloChords, responseChords, notate, metre);
        aMelodyPart = generator.response(transform, tradeMode);
        notate.getCurrentMelodyPart().altPasteOver(aMelodyPart, triggers.peek());
        notate.getCurrentMelodyPart().altPasteOver(new MelodyPart(slotsPerTurn), triggers.peek() + slotsPerTurn);
//        switch (tradeMode) {
//            case REPEAT_AND_RECTIFY:
//                repeatAndRectify();
//                break;
//            default:
//                break;
//        }
    }
    
//    private void changeTradeMode(String newMode) {
//        if (newMode.equals("Repeat")) {
//            tradeMode = TradeMode.REPEAT;
//        } else if (newMode.equals("Repeat and Rectify")) {
//            tradeMode = TradeMode.REPEAT_AND_RECTIFY;
//        } else {
//            tradeMode = TradeMode.REPEAT;
//            //System.out.println("Not a valid mode");
//        }
//    }
    
    private void changeTradeLength(String newLength){
        measures = Integer.parseInt(newLength);
        //System.out.println("TRADE LENGTH SET: " + newLength);
    }
    
    private void setIsUserLeading(boolean isUserLeading){
        this.isUserLeading = isUserLeading;
        //System.out.println("USER FIRST: " + isUserLeading);
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

        leadingSelector = new javax.swing.ButtonGroup();
        startTradingButton = new javax.swing.JButton();
        tradeModeSelector = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        tradeLenthSelector = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        userFirstButton = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jSeparator1 = new javax.swing.JSeparator();
        musicianChooser = new javax.swing.JComboBox();
        musicianLabel = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setMinimumSize(new java.awt.Dimension(500, 200));
        setPreferredSize(new java.awt.Dimension(500, 200));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        startTradingButton.setLabel("Start Trading");
        startTradingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startTradingButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.ipadx = 38;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(6, 19, 11, 10);
        getContentPane().add(startTradingButton, gridBagConstraints);
        startTradingButton.getAccessibleContext().setAccessibleDescription("");

        tradeModeSelector.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Repeat", "Repeat and Rectify", "Flatten", "Random Modify", "Flatten, Modify, Rectify", "Charlie Parker", "Trade with a Musician", "Abstract" }));
        tradeModeSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeModeSelectorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(tradeModeSelector, gridBagConstraints);

        jLabel1.setText("Mode:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        getContentPane().add(jLabel1, gridBagConstraints);

        tradeLenthSelector.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1", "2", "4", "8", "16" }));
        tradeLenthSelector.setSelectedIndex(2);
        tradeLenthSelector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradeLenthSelectorActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(tradeLenthSelector, gridBagConstraints);

        jLabel2.setText("Length:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        getContentPane().add(jLabel2, gridBagConstraints);

        leadingSelector.add(userFirstButton);
        userFirstButton.setSelected(true);
        userFirstButton.setText("User First");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        getContentPane().add(userFirstButton, gridBagConstraints);

        leadingSelector.add(jRadioButton2);
        jRadioButton2.setText("Impro-Visor First");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        getContentPane().add(jRadioButton2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 396;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.insets = new java.awt.Insets(18, 12, 0, 14);
        getContentPane().add(jSeparator1, gridBagConstraints);

        populateMusicianList();
        musicianChooser.setVisible(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(musicianChooser, gridBagConstraints);

        musicianLabel.setText("Musician:");
        musicianLabel.setVisible(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        getContentPane().add(musicianLabel, gridBagConstraints);

        jTextField1.setText("jTextField1");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.FIRST_LINE_START;
        getContentPane().add(jTextField1, gridBagConstraints);

        jLabel3.setText("jLabel3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LAST_LINE_START;
        getContentPane().add(jLabel3, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void populateMusicianList()
  {
    File directory = ImproVisor.getTransformDirectory();
    //System.out.println("populating from " + directory);
    if( directory.isDirectory() )
      {
        String fileName[] = directory.list();

        // 6-25-13 Hayden Blauzvern
        // Fix for Linux, where the file list is not in alphabetic order
        Arrays.sort(fileName, new Comparator<String>()
        {
        public int compare(String s1, String s2)
          {
            return s1.toUpperCase().compareTo(s2.toUpperCase());
          }

        });
       
        // Add names of grammar files
        for (String name : fileName) {
            if( name.endsWith(TransformFilter.EXTENSION) )
            {
                int len = name.length();
                String stem = name.substring(0, len - TransformFilter.EXTENSION.length());
                musicianChooser.addItem(stem);
            }
        }
      }
  }
    
    
    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        //
    }//GEN-LAST:event_formWindowClosed

    private void startTradingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startTradingButtonActionPerformed
        if (!isUserInputError) {
            if (!isTrading) {
                startTrading();
            } else {
                stopTrading();
            }
        }
    }//GEN-LAST:event_startTradingButtonActionPerformed

    private void tradeModeSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeModeSelectorActionPerformed
        //changeTradeMode((String)tradeModeSelector.getSelectedItem());
        if(((String)tradeModeSelector.getSelectedItem()).equals("Trade with a Musician")){
            musicianLabel.setVisible(true);
            musicianChooser.setVisible(true);
        }else{
            musicianLabel.setVisible(false);
            musicianChooser.setVisible(false);
        }
    }//GEN-LAST:event_tradeModeSelectorActionPerformed

    private void tradeLenthSelectorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradeLenthSelectorActionPerformed
        changeTradeLength((String) tradeLenthSelector.getSelectedItem());
    }//GEN-LAST:event_tradeLenthSelectorActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private double tryDouble(String number) {
        double newNumber;
        try {
            newNumber = Double.parseDouble(number);
            isUserInputError = false;
        } catch (Exception e) {
            isUserInputError = true;
            newNumber = 0;
        }
        return newNumber;
    }

//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(TradingWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(TradingWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(TradingWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(TradingWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new TradingWindow(notate).setVisible(true);
//            }
//        });
//    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.ButtonGroup leadingSelector;
    private javax.swing.JComboBox musicianChooser;
    private javax.swing.JLabel musicianLabel;
    private javax.swing.JButton startTradingButton;
    private javax.swing.JComboBox tradeLenthSelector;
    private javax.swing.JComboBox tradeModeSelector;
    private javax.swing.JRadioButton userFirstButton;
    // End of variables declaration//GEN-END:variables

}
