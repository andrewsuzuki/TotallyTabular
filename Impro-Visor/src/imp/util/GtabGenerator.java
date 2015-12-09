package imp.util;

import imp.data.MelodyPart;
import imp.data.Note;

/**
 * Generator for a guitar tab given a MelodyPart
 * @author andrew
 */
public class GtabGenerator {
	
	/**
	 * Make a smart guitar tab given a MelodyPart
	 * @param mp
	 * @return
	 */
	public static Gtab makeGtabFromMelodyPart(MelodyPart mp) {
		// Initialize Gtab
		Gtab gtab = new Gtab();
		
		// Get notes in melody part and add to tab
		for (Note note : mp.getNoteList()) {
			// Skip rests
			if (note.isRest()) {
				continue;
			}
			
			// Place note at lowest position in tab
			gtab.placeLowest(note);
		}
		
		// Return tab
		return gtab;
	}
}
