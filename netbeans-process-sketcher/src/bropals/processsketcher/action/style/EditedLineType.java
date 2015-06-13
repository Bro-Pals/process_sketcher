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
package bropals.processsketcher.action.style;

import bropals.processsketcher.FlowchartWindow;
import bropals.processsketcher.data.NodeLine;
import bropals.processsketcher.data.Selectable;
import bropals.processsketcher.style.LineType;
import java.util.ArrayList;

/**
 * An action that occurs when the line type on one or more nodes lines are changed.
 * @author Kevin
 */
public class EditedLineType extends EditedStyle {

    private ArrayList<LineType> types;
    
    public EditedLineType(ArrayList<Selectable> editedSelectables, ArrayList<LineType> oldTypes) {
        super(editedSelectables);
        types = oldTypes;
    }

    @Override
    public void undo(FlowchartWindow instance) {
        System.out.println("Undo line type edited");
        ArrayList<NodeLine> lines = getEditedNodeLines();
        for (int i=0; i<lines.size(); i++) {
            lines.get(i).getStyle().setType(types.get(i));
        }
    }
    
}
