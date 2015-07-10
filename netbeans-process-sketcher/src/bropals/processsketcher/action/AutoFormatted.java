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

import bropals.processsketcher.FlowchartWindow;
import bropals.processsketcher.data.Node;
import java.awt.Point;
import java.util.ArrayList;

/**
 * An action that occurs when you auto-format the flowchart
 * @author Kevin
 */
public class AutoFormatted extends Action {

    private ArrayList<Node> nodes;
    private ArrayList<Point> positions;
    
    public AutoFormatted(ArrayList<Node> nodesMoved, ArrayList<Point> oldPositions) {
        nodes = nodesMoved;
        positions = oldPositions;
    }
    
    @Override
    public void undo(FlowchartWindow instance) {
        //System.out.println("Undo autoformatting");
        for (int i=0; i<nodes.size(); i++) {
            nodes.get(i).setX((float)positions.get(i).getX());
            nodes.get(i).setY((float)positions.get(i).getY());
        }
    }
    
}
