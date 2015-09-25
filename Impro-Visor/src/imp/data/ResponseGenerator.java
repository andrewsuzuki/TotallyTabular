/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import static imp.Constants.BEAT;
import imp.com.InvertCommand;
import imp.com.RectifyPitchesCommand;
import imp.com.ReverseCommand;
import static imp.data.AbstractMelodyExtractor.getAbstractMelody;
import imp.gui.Notate;
import imp.lickgen.transformations.Transform;
import imp.lickgen.transformations.TransformLearning;
import java.io.File;
import java.util.Random;

/**
 *
 * @author muddCS15
 */
public class ResponseGenerator {
    
    private MelodyPart response;
    private static final int start = 0;
    private int stop;
    private ChordPart soloChords;
    private ChordPart responseChords;
    private Notate notate;
    private final BeatFinder beatFinder;
    private final TransformLearning flattener;
    private static final boolean ONLY_CHORD_TONES = true;
    private static final boolean ALL_TONES = false;
    
    public ResponseGenerator(MelodyPart response,ChordPart soloChords, ChordPart responseChords, Notate notate, int [] metre){
        this.response = response;
        this.stop = response.size()-1;
        this.soloChords = soloChords;
        this.responseChords = responseChords;
        this.notate = notate;
        this.beatFinder = new BeatFinder(metre);
        this.flattener = new TransformLearning();
    }
    
    //STEP 0 - load the solo and the chords the response will be played over
    
    //set response
    public void setResponse(MelodyPart response){
        this.response = response;
        this.stop = response.size()-1;
    }

    //set chords
    public void setChords(ChordPart responseChords){
        this.responseChords = responseChords;
    }
    
    //STEP 1 - flatten the solo
    
    //Flatten a solo to the default resolution
    //currently flatten to every beat
    public void flattenSolo(){
        flattenSolo(beatFinder.EVERY_BEAT);
    }

    //Flatten solo to a specified resolution
    //Resolutions specified by strings must be converted
    //Examples:
    //beatFinder.EVERY_BEAT
    //beatFinder.MEASURE_LENGTH
    //beatFinder.STRONG_BEATS
    public void flattenSolo(String resolution){
        flattenSolo(beatFinder.getResolution(resolution));
    }

    //Flatten solo to specified resolution
    //(flattens based on response chords)
    //Examples:
    //Constants.WHOLE
    //Constants.HALF
    public void flattenSolo(int resolution){
        response = flattener.flattenByResolution(response, responseChords, resolution, start, stop, false);
    }

    //STEP 2 - modify the flattened solo (inversion/retrograde/retrograde inversion/no change)
    
    //Modify the solo in a simple way
    //i.e. invert, reverse, transpose
    public void modifySolo(){
        int options = 4;
        Random r = new Random();
        int selection = r.nextInt(options);
        switch(selection){
            case 0:
                //inversion
                invertSolo();
                break;
            case 1:
                //retrograde
                reverseSolo();
                break;
            case 2:
                //retrograde inversion
                invertSolo();
                reverseSolo();
                break;
            case 3:
                //original
                break;
        }
    }

    public void abstractify() {
        String abstractMelody = AbstractMelodyExtractor.getAbstractMelody(
                0,
                responseChords.getSize() / BEAT,
                false,
                false,
                response,
                soloChords
        );
        response = notate.getLickgenFrame().fillAndReturnMelodyFromText(abstractMelody, responseChords);
    }

    //invert the solo
    public void invertSolo(){
        InvertCommand cmd = new InvertCommand(response, start, stop, false);
        cmd.execute();
    }
    
    //reverse the solo
    public void reverseSolo(){
        ReverseCommand cmd = new ReverseCommand(response, start, stop, false);
        cmd.execute();
    }
    
    //STEP 3 - transform/embellish the solo (in the style of a particular musician)
    
    //transform solo using specified transform
    //(in gui, select this from a drop down menu)
    public void transformSolo(Transform musician){
        response = musician.applySubstitutionsToMelodyPart(response, responseChords, true);
    }
    
    //STEP 4 - rectify the solo to chord/color tones
    
    //rectify solo to response chords
    //allows chord, color, and approach tones
    //allows repeat pitches
    public void rectifySolo(boolean onlyChordTones){
        RectifyPitchesCommand cmd;
        boolean chord, color, approach;
        chord = true;
        if(onlyChordTones){
            color = false;
            approach = false;
        }else{
            color = true;
            approach = true;
        }
        cmd = new RectifyPitchesCommand(response, 0, response.size()-1, responseChords, false, false, chord, color, approach);
        cmd.execute();
    }
    
    public void rectifySolo(){
        rectifySolo(false);
    }

    //STEP 5 - retreive the response
    
    //retreive response
    public MelodyPart getResponse(){
        return response;
    }
    
    //ALL THE STEPS TOGETHER
    public MelodyPart musicianResponse(Transform musician){

        //STEP 1
        flattenSolo();
        //STEP 2
        modifySolo();
        rectifySolo(ONLY_CHORD_TONES);
        //STEP 3
        transformSolo(musician);
        //STEP 4
        rectifySolo(ALL_TONES);
        //STEP 5
        return getResponse();
    }
    
    public MelodyPart response(Transform musician, String tradeMode){
        if (tradeMode.equals("Flatten")){
            flattenSolo();
        } else if (tradeMode.equals("Repeat and Rectify")) {
            rectifySolo();
        } else if (tradeMode.equals("Random Modify")){
            modifySolo();
            rectifySolo(ALL_TONES);
        } else if (tradeMode.equals("Flatten, Modify, Rectify")) {
            flattenSolo();
            modifySolo();
            rectifySolo();
        } else if (tradeMode.equals("Trade with a Musician")) {
            musicianResponse(musician);
            rectifySolo();
        } else if (tradeMode.equals("Abstract")) {
            abstractify();
        } else {
            System.out.println("did nothing");
        }
        return getResponse();
    }
    
    
}
