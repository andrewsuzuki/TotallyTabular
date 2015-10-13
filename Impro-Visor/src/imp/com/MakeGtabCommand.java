// !!TT!!
package imp.com;

import javax.swing.JFrame;

import imp.data.MelodyPart;
import imp.gui.GtabDialog;
import imp.util.Gtab;
import imp.util.GtabGenerator;

public class MakeGtabCommand implements Command {
	
	private JFrame parentWindow;
	
	private GtabDialog dialog;
	
	private boolean dialogModalityType = false; // main window should still be usable
	
	private MelodyPart mp;
	
	private boolean undoable = false; // cannot undo this command
	
	public MakeGtabCommand(JFrame parentWindow, MelodyPart mp) {
		this.parentWindow = parentWindow;
		this.mp = mp;
	}

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

	public void undo() {
		throw new UnsupportedOperationException("Undo unsupported for MakeGtab.");
	}

	public void redo() {
		throw new UnsupportedOperationException("Redo unsupported for MakeGtab.");
	}

	public boolean isUndoable() {
		return undoable;
	}

}
