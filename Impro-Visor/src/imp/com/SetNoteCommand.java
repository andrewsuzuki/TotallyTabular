/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.com;

import java.util.*;
import imp.*;
import imp.data.*;

/**
 * An undoable Command that places a Note at a certain position in a MelodyPart.
 * @see         Command
 * @see         CommandManager
 * @see         Note
 * @see         MelodyPart
 * @author      Stephen Jones
 */
public class SetNoteCommand
        implements Command, Constants
  {
  /**
   * the MelodyPart in which to place the Note
   */
  private MelodyPart melodyPart;

  /**
   * the Note to place in the MelodyPart
   */
  private Note note;

  /**
   * the index at which to place the Note
   */
  private int slotIndex;

  /**
   * true if the setNote call inserted a Rest to stop auto-expansion
   */
  private boolean restInserted = false;

  /**
   * stack with the slots of rests deleted when inserting a Note
   */
  private Stack<Integer> restsDeleted = new Stack<Integer>();

  /**
   * the index to insert a rest if a rest is inserted
   */
  private int stopIndex;

  /**
   * true since this Command can be undone
   */
  private boolean undoable = true;

  /**
   * the Note that used to be at the specified position
   */
  private Note oldNote;

  /**
   * Creates a new Command that can set the Note in a MelodyPart at the
   * specified indices.
   * @param sI        the index of the Note to place
   * @param nte       the Note to place
   * @param prt       the MelodyPart in which to place the Note
   */
  public SetNoteCommand(int sI, Note nte, MelodyPart prt)
    {
    melodyPart = prt;
    note = nte;
    slotIndex = sI;
    }

  /**
   * Places the Note at the specified position in the MelodyPart.
   */
  public void execute()
    {
    //Trace.log(0, "executing SetNoteCommand, slotIndex = " + slotIndex);

    int[] metre = melodyPart.getMetre();
    int beatValue = (WHOLE / metre[1]); // metre[1] is the type
                                        // of note that gets the beat
    int measureLength = metre[0] * beatValue;

    if( note != null && note.nonRest() )
      {
        if (melodyPart.getAutoFill())  
            stopIndex = (slotIndex / measureLength + 2) * measureLength;
        else
            stopIndex = slotIndex + melodyPart.getNoteLength();
      }
    oldNote = melodyPart.getNote(slotIndex);
    melodyPart.setNote(slotIndex, note);
    }

  /**
   * Replaces the previously placed Note with whatever used to be there. 
   */
  public void undo()
    {
    //Trace.log(0, "executing undo of SetNoteCommand, slotIndex = " + slotIndex);
    melodyPart.setNote(slotIndex, oldNote);
    if( restInserted )
      {
      melodyPart.delUnit(stopIndex);
      while( !restsDeleted.empty() )
        {
        melodyPart.setRest(restsDeleted.pop());
        }
      }
    }

  /**
   * Places the note at the specified position in the melodyPart.
   */
  public void redo()
    {
    if( restInserted )
      {
      for( int i = slotIndex; i < stopIndex; i++ )
        {
        if( melodyPart.getNote(i) != null &&
                melodyPart.getNote(i).isRest() )
          {
          restsDeleted.push(i);
          melodyPart.delUnit(i);
          }
        }
      }

    melodyPart.setNote(slotIndex, note);
    }

  public boolean isUndoable()
    {
    return undoable;
    }

  }
