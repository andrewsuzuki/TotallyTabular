package imp.util;

import java.util.ArrayList;

import imp.data.Note;

/**
 * Representation of a guitar tab
 * that can be rendered as a string
 * @author andrew
 */
public class Gtab {
	/**
	 * Number of frets on the guitar
	 */
	private int frets;
	
	/**
	 * Midi note values for open strings (standard tuning)
	 */
	private final int s1_min = 64; // e
	private final int s2_min = 59; // B
	private final int s3_min = 55; // G
	private final int s4_min = 50; // D
	private final int s5_min = 45; // A
	private final int s6_min = 40; // E
	
	/**
	 * GtabbedNote sequence
	 */
	private ArrayList<GtabbedNote> fns;
	
	/**
	 * Default constructor
	 */
	public Gtab() {
		this(21); // assume 21-fret guitar
	}
	
	/**
	 * Constructor with specified number of frets
	 * @param frets
	 */
	public Gtab(int frets) {
		// Only accept guitars with at least 4 frets
		// Otherwise default to 21 frets
		this.frets = (frets < 4) ? 21 : frets;
		
		// Initialize list of GtabbedNotes
		this.fns = new ArrayList<GtabbedNote>();
	}
	
	/**
	 * Get the number of frets of the guitar intended for this tab
	 * @return
	 */
	public int getFrets() {
		return frets;
	}
	
	/**
	 * Helper method that corrects for the fact that guitar music
	 * is typically written one octave higher than it sounds
	 * @param pitch
	 */
	private int transposePitchForGuitar(int pitch) {
		return pitch - 12;
	}
	
	/**
	 * Get a collection of all possible placements for a note
	 * @return
	 */
	private ArrayList<GtabbedNote> getPossiblePlacements(Note note) {
		// Correct pitch (transpose 8vb)
		int pitch = transposePitchForGuitar(note.getPitch());
		
		// Initialize collection of possible tab notes
		ArrayList<GtabbedNote> pp = new ArrayList<GtabbedNote>();
		
		// Check for possibility on each string
		if (s1_min <= pitch && s1_min + frets >= pitch) {
			GtabbedNote gtn1 = new GtabbedNote(1, pitch - s1_min);
			pp.add(gtn1);
		}
		if (s2_min <= pitch && s2_min + frets >= pitch) {
			GtabbedNote gtn2 = new GtabbedNote(2, pitch - s2_min);
			pp.add(gtn2);
		}
		if (s3_min <= pitch && s3_min + frets >= pitch) {
			GtabbedNote gtn3 = new GtabbedNote(3, pitch - s3_min);
			pp.add(gtn3);
		}
		if (s4_min <= pitch && s4_min + frets >= pitch) {
			GtabbedNote gtn4 = new GtabbedNote(4, pitch - s4_min);
			pp.add(gtn4);
		}
		if (s5_min <= pitch && s5_min + frets >= pitch) {
			GtabbedNote gtn5 = new GtabbedNote(5, pitch - s5_min);
			pp.add(gtn5);
		}
		if (s6_min <= pitch && s6_min + frets >= pitch) {
			GtabbedNote gtn6 = new GtabbedNote(6, pitch - s6_min);
			pp.add(gtn6);
		}
		
		return pp;
	}
	
	/**
	 * Place a note at the lowest position possible
	 * @param note
	 */
	public void placeLowest(Note note) {
		// Keep track of the lowest possibility
		GtabbedNote lowest = null;
		
		// Get all possibilities for note
		for (GtabbedNote gtn : getPossiblePlacements(note)) {
			// Check if this tabbed note is lower than the current lowest
			if (lowest == null || gtn.getFret() < lowest.getFret()) {
				// If so, update lowest
				lowest = gtn;
			}
		}
		
		// If lowest is still null, it means note is not on guitar
		if (lowest == null) {
			// Place an "invalid" note denoting its impossibility
			placeInvalidNote();
		} else {
			// Place lowest note
			fns.add(lowest);
		}
	}
		
	/**
	 * Place an invalid note, denoting it as not playable on guitar
	 */
	public void placeInvalidNote() {
		fns.add(new GtabbedNote(-1, -1));
	}
	
	/**
	 * Render this tab as a multiple-stave string with given width
	 * @return
	 */
	public String render(int width) {
		ArrayList<StringBuilder[]> staves = new ArrayList<StringBuilder[]>();

		int noteIndex = 0;

		// Loop all placed notes
		for (GtabbedNote gtn : fns) {
			int staveNumber = noteIndex / width; // 0 is the first stave, the one on top, 1 is the one below, etc
			int xPos = noteIndex % width; // gets the column number
			
			// Every "width" number of notes, add a new stave to the stave list.
			// This will add the initial stave the first time we go through.
			if (xPos == 0) {
				StringBuilder[] sbs = {
						null, // pad beginning so that array index = string #
						new StringBuilder("e|"),
						new StringBuilder("B|"),
						new StringBuilder("G|"),
						new StringBuilder("D|"),
						new StringBuilder("A|"),
						new StringBuilder("E|")
				};
				
				staves.add(sbs);
			}

			//gets the current stave
			StringBuilder[] currentStave = staves.get(staveNumber);

			// If it's an invalid note, denote it as such
			if (gtn.getStringNum() < 0) {
				// Add exclamation marks to each line
				for (int i = 1; i < currentStave.length; i++) {
					currentStave[i].append("!-");
				}
				continue;
			}

			String fret = gtn.getFret() + "-";
			// Make a hyphen spacer for the other strings; same length as fret number length
			String spacer = new String(new char[fret.length()]).replace("\0", "-");

			// Loop strings
			for (int i = 1; i < currentStave.length; i++) {
				if (i == gtn.getStringNum()) {
					// If note is on string, give it the fret number
					currentStave[i].append(fret);
				} else {
					// Otherwise, add a spacer of the same length
					currentStave[i].append(spacer);
				}
			}			
			
			// Set the current stave that we processed back into the Staves array.
			staves.set(staveNumber,currentStave);
			
			// once this note is processed, move on
			noteIndex++;
		}

		// Build final stave string
		StringBuilder stave = new StringBuilder();
		for (StringBuilder[] currentStave:staves) {
			for (int i = 1; i < currentStave.length; i++) {
				// Append string
				stave.append(currentStave[i]);
				// Add final stave bar and newlines after each line
				stave.append("|\n");
			}
			
			// Add a line between each stave
			stave.append("\n");
		}

		// Render and return stave string
		return stave.toString();
	}
	
	/**
	 * Tabbed note represented by a string and fret
	 * @author andrew
	 */
	private class GtabbedNote {
		
		/**
		 * The string # (1-6, or < 0 for invalid notes)
		 */
		private int string;
		
		/**
		 * The fret # (0-frets, or < 0 for invalid notes)
		 */
		private int fret;
		
		/**
		 * Class constructor
		 * @param string
		 * @param fret
		 */
		public GtabbedNote(int string, int fret) {			
			this.string = string;
			this.fret = fret;
		}
		
		/**
		 * Get string #
		 * @return
		 */
		public int getStringNum() {
			return string;
		}
		
		/**
		 * Get fret #
		 * @return
		 */
		public int getFret() {
			return fret;
		}
		
		/**
		 * Return string representing string/fret combination
		 */
		public String toString() {
			return "[s" + string + "f" + fret + "]";
		}
	}
}
