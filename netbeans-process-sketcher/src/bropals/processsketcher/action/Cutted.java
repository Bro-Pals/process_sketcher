/*
 * Process Sketcher is a simple flowchart making software.
 * Copyright (C) 2015  Jonathon Prehn, Kevin Prehn
 * 
 * This file is a part of Process Sketcher.
 * 
 * Process Sketcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Process Sketcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Process Sketcher.  If not, see <http://www.gnu.org/licenses/>.
 */
package bropals.processsketcher.action;

import bropals.processsketcher.action.Action;
import bropals.processsketcher.FlowchartWindow;
import bropals.processsketcher.data.Selectable;
import java.util.ArrayList;

/**
 * An action that occurs when you cut nodes
 * @author Kevin
 */
public class Cutted extends Action {

    /**
     * The deleted action is a part of the cutting action
     */
    private Deleted deletedAction;
    private ArrayList<Selectable> clipboard;
    
    public Cutted(Deleted deleted, ArrayList<Selectable> oldClipboard) {
        clipboard = oldClipboard;
        deletedAction = deleted;
    }
    
    @Override
    public void undo(FlowchartWindow instance) {
        //System.out.println("Undo the cut action");
        // reset the clipboard back to where it was
        instance.getEventManager().getDragManager().setStuffInClipboard(clipboard);
        
        // undo the cutted things being deleted
        deletedAction.undo(instance);
    }
    
}
