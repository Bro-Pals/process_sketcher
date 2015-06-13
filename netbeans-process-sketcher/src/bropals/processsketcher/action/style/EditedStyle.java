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

import bropals.processsketcher.action.Action;
import bropals.processsketcher.data.Node;
import bropals.processsketcher.data.NodeLine;
import bropals.processsketcher.data.Selectable;
import java.util.ArrayList;

/**
 * A type of action that occurs when any style is changed
 * @author Kevin
 */
public abstract class EditedStyle extends Action {
    
    /**
     * The selectable whose style was changed. This could be anything that implements Selectable.
     */
    private ArrayList<Selectable> edited;
    
    public EditedStyle(ArrayList<Selectable> editedSelectables) {
        edited = editedSelectables;
    }
    
    public EditedStyle(Selectable editedSelectable) {
        edited = new ArrayList<>();
        edited.add(editedSelectable);
    }
    
    /**
     * Get a list of all the nodes that were edited. This array may be empty if 
     * no nodes were edited.
     * @return list of the nodes edited. The list might be empty.
     */
    public ArrayList<Node> getEditedNodes() {
        ArrayList<Node> nodes = new ArrayList<>();
        for (Selectable s : edited) {
            if (s instanceof Node) {
                nodes.add((Node)s);
            }
        }
        return nodes;
    }
    
    /**
     * Get a list of all the node lines that were edited. This array may be empty if 
     * no node lines were edited.
     * @return list of the nodes lines edited. The list might be empty.
     */
    public ArrayList<NodeLine> getEditedNodeLines() {
        ArrayList<NodeLine> nodes = new ArrayList<>();
        for (Selectable s : edited) {
            if (s instanceof NodeLine) {
                nodes.add((NodeLine)s);
            }
        }
        return nodes;
    }
    
    /**
     * Get the list of all the selectables that were edited
     * @return a list of all the selectables that were edited
     */
    public ArrayList<Selectable> getSelectables() {
        return edited;
    }
}
