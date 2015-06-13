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
import bropals.processsketcher.data.Node;
import bropals.processsketcher.data.Selectable;
import bropals.processsketcher.style.Shape;
import java.util.ArrayList;

/**
 * An action that occurs when the shape of one or more nodes are changed.
 * @author Kevin
 */
public class EditedShape extends EditedStyle {

    private ArrayList<Shape> shapes;
    
    public EditedShape(ArrayList<Selectable> editedSelectables, ArrayList<Shape> oldShapes) {
        super(editedSelectables);
        shapes = oldShapes;
    }

    @Override
    public void undo(FlowchartWindow instance) {
        System.out.println("Undo edted shape");
        ArrayList<Node> nodes = getEditedNodes();
        for (int i=0; i<getEditedNodes().size(); i++) {
            nodes.get(i).getStyle().setShape(shapes.get(i));
        }
    }
    
}
