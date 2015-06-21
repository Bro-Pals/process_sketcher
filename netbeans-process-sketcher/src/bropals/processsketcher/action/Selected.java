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
 * An action that occurs when you select one more more Selectables
 * @author Kevin
 */
public class Selected extends Action {

    /**
     * The Selectables that will be removed from the selected when this is undone.
     */
    private ArrayList<Selectable> selected;
    
    public Selected(Selectable addedToSelection) {
        selected = new ArrayList<>();
        selected.add(addedToSelection);
    }
    
    public Selected(ArrayList<Selectable> addedToSelection) {
        selected = addedToSelection;
    }
    
    @Override
    public void undo(FlowchartWindow instance) {
        //System.out.println("Undo selection action");
        instance.getEventManager().getSelectionManager().deselect(selected);
    }
    
}
