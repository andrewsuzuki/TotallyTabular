package imp.util;

import imp.data.MelodyPart;
import imp.data.Note;

/**
 * Generator for a guitar tab given a MelodyPart
 * @author andrew
 */
public class GtabGenerator {
	public static Gtab makeGtabFromMelodyPart(MelodyPart mp) {
		// Init Gtab
		Gtab gtab = new Gtab();
		
		// Get notes in melody part and add to tab
		for (Note note : mp.getNoteList()) {
			// Skip rests
			if (note.isRest()) {
				continue;
			}
			gtab.placeBasedOnRecent(note);
		}
		
		// Return tab
		return gtab;
	}
}