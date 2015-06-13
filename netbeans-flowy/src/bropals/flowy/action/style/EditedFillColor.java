/*
 * Flowy is a simple flowchart making software.
 * Copyright (C) 2015  Jonathon Prehn, Kevin Prehn
 * 
 * This file is a part of Flowy.
 * 
 * Flowy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Flowy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Flowy.  If not, see <http://www.gnu.org/licenses/>.
 */
package bropals.flowy.action.style;

import bropals.flowy.FlowchartWindow;
import bropals.flowy.data.Node;
import bropals.flowy.data.Selectable;
import java.awt.Color;
import java.util.ArrayList;

/**
 * An action that occurs when the fill color of one or more nodes are changed
 * @author Kevin
 */
public class EditedFillColor extends EditedStyle {

    private ArrayList<Color> colors;
    
    public EditedFillColor(ArrayList<Selectable> editedSelectables, ArrayList<Color> oldColors) {
        super(editedSelectables);
        colors = oldColors;
    }

    @Override
    public void undo(FlowchartWindow instance) {
        System.out.println("Undo edited fill color");
        ArrayList<Node> nodes = getEditedNodes();
        for (int i=0; i<nodes.size(); i++) {
            nodes.get(i).getStyle().setFillColor(colors.get(i));
        }
    }
    
}