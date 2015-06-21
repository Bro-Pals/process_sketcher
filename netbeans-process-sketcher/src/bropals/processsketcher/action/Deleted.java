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
import bropals.processsketcher.data.Node;
import bropals.processsketcher.data.NodeLine;
import bropals.processsketcher.data.Selectable;
import java.util.ArrayList;

/**
 * An action that occurs when you delete one or more Selectables
 * @author Kevin
 */
public class Deleted extends Action {

    private ArrayList<Selectable> deleted;
    
    public Deleted(ArrayList<Selectable> deletedSelectables) {
        deleted = deletedSelectables;
    }
    
    @Override
    public void undo(FlowchartWindow instance) {
        //System.out.println("Undo deletion");
        
        ArrayList<Node> addedNodes = new ArrayList<>();
        ArrayList<NodeLine> addedLines = new ArrayList<>();

        for (int i=0; i<deleted.size(); i++) {
            if (deleted.get(i) instanceof Node) {
                addedNodes.add((Node)deleted.get(i));
            } else {
                addedLines.add((NodeLine)deleted.get(i));
            }
        }
        
        instance.getFlowchart().getNodes().addAll(addedNodes);
        
        for (int i=0; i<addedLines.size(); i++) {
            if (!addedLines.get(i).getChild().getLinesConnected().contains(addedLines.get(i))) {
                addedLines.get(i).getChild().getLinesConnected().add(addedLines.get(i));
            }
            if (!addedLines.get(i).getParent().getLinesConnected().contains(addedLines.get(i))) {
                addedLines.get(i).getParent().getLinesConnected().add(addedLines.get(i));
            }
        }
    }
    
}
