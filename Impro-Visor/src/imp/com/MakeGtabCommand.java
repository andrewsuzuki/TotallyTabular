// !!TT!!
package imp.com;

import javax.swing.JFrame;

import imp.data.MelodyPart;
import imp.gui.GtabDialog;
import imp.util.Gtab;
import imp.util.GtabGenerator;

public class MakeGtabCommand implements Command {
	
	/**
	 * Holds the highest JFrame to attach the guitar tab dialog to
	 */
	private JFrame parentWindow;
	
	/**
	 * Holds our JDialog
	 */
	private GtabDialog dialog;
	
	/**
	 * The modality type for the guitar tab dialog
	 * false = main window still usable when active
	 */
	private boolean dialogModalityType = false;
	
	/**
	 * The melody part to be converted to tab
	 */
	private MelodyPart mp;
	
	/**
	 * Whether this command can be undone (it can't)
	 */
	private boolean undoable = false; // cannot undo this command
	
	/**
	 * Class constructor
	 * @param parentWindow
	 * @param mp
	 */
	public MakeGtabCommand(JFrame parentWindow, MelodyPart mp) {
		this.parentWindow = parentWindow;
		this.mp = mp;
	}
	
	/**
	 * Execute interface method
	 * usually called immediately after instantiation
	 */
	public void execute() {
		// Generate tab
		Gtab gtab = GtabGenerator.makeGtabFromMelodyPart(mp);
				
		// Make new dialog
		dialog = new GtabDialog(parentWindow, dialogModalityType);
		
		// Give it our tab
		dialog.setGtab(gtab);
		
		// Show dialog
		dialog.setSize(600, 750);
		dialog.setLocationRelativeTo(parentWindow);
		dialog.setVisible(true);
	}
	
	/**
	 * Undo interface method (not supported)
	 */
	public void undo() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Undo unsupported for MakeGtab.");
	}
	
	/**
	 * Redo interface method (not supported)
	 */
	public void redo() throws UnsupportedOperationException {
		throw new UnsupportedOperationException("Redo unsupported for MakeGtab.");
	}
	
	/**
	 * Whether this method is undo-able (it isn't)
	 */
	public boolean isUndoable() {
		return undoable;
	}

}
