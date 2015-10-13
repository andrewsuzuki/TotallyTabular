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
	
	public int getFrets() {
		return frets;
	}
	
	/**
	 * Corrects for the fact that guitar music is typically
	 * written one octave higher than it sounds
	 * @param pitch
	 */
	private int transposePitchForGuitar(int pitch) {
		return pitch - 12;
	}
	
	/**
	 * Get all possible placements for a note
	 * @return
	 */
	private ArrayList<GtabbedNote> getPossiblePlacements(Note note) {
		int pitch = transposePitchForGuitar(note.getPitch());
		
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
	 * Place note at lowest position possible
	 * @param note
	 */
	public void placeLowest(Note note) {
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
	 * Place note at most comfortable position given recently-placed notes
	 * @param note
	 * @param last
	 */
	public void placeBasedOnRecent(Note note) {
		// TODO
		// Currently just places it at lowest position
		placeLowest(note);
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
		// TODO
		// Currently just renders single-stave version
		return renderAsSingleStave();
	}
	
	/**
	 * Render this tab as a single stave. Could possibly be very wide.
	 * @return
	 */
	public String renderAsSingleStave() {
		StringBuilder[] sbs = {
				null, // pad beginning so that array index = string #
				new StringBuilder("e|"),
				new StringBuilder("B|"),
				new StringBuilder("G|"),
				new StringBuilder("D|"),
				new StringBuilder("A|"),
				new StringBuilder("E|")
		};
		
		// Loop all placed notes
		for (GtabbedNote gtn : fns) {			
			// If it's an invalid note, denote it as such
			// TODO handle rests
			if (gtn.getStringNum() < 0) {
				// Add exclamation marks to each line
				for (int i = 1; i < sbs.length; i++) {
					sbs[i].append("!-");
				}
				continue;
			}
			
			String fret = gtn.getFret() + "-";
			// Make a hyphen spacer for the other strings; same length as fret number length
			String spacer = new String(new char[fret.length()]).replace("\0", "-");
			
			// Loop strings
			for (int i = 1; i < sbs.length; i++) {
				if (i == gtn.getStringNum()) {
					// If note is on string, give it the fret number
					sbs[i].append(fret);
				} else {
					// Otherwise, add a spacer of the same length
					sbs[i].append(spacer);
				}
			}
		}
		
		// Build final stave string
		StringBuilder stave = new StringBuilder();
					
		for (int i = 1; i < sbs.length; i++) {
			// Append string
			stave.append(sbs[i]);
			// Add final stave bar and newlines after each line
			stave.append("|\n");
		}
		
		// Render and return stave string
		return stave.toString();
	}
	
	/**
	 * Return a render with a 50-character width
	 */
	public String toString() {
		return render(50);
	}
	
	/**
	 * Tabbed note represented by a string and fret
	 * @author andrew
	 */
	private class GtabbedNote {
		private int string;
		private int fret;
		
		public GtabbedNote(int string, int fret) {			
			this.string = string;
			this.fret = fret;
		}
		
		public int getStringNum() {
			return string;
		}
		
		public int getFret() {
			return fret;
		}
		
		public String toString() {
			return "[s" + string + "f" + fret + "]";
		}
	}
}
