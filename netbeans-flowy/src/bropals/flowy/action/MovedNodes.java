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
package bropals.flowy.action;

import bropals.flowy.FlowchartWindow;
import bropals.flowy.data.Node;

/**
 * An action that occurs when you move one or more nodes.
 * @author Kevin
 */
public class MovedNodes extends Action {

    private Node[] nodesMoved;
    /**
     * How much to offset each node from the initial x and y position
     */
    private float[][] offsetsOfNodes;
    private float initialXPos;
    private float initialYPos;
    
    public MovedNodes(Node[] nodesMoved, float[][] offsetsOfNodes, float initialX, float initialY) {
        this.nodesMoved = nodesMoved;
        this.offsetsOfNodes = offsetsOfNodes;
        this.initialXPos = initialX;
        this.initialYPos = initialY;
    }
    
    @Override
    public void undo(FlowchartWindow instance) {
        System.out.println("Undo moving Nodes");
    }
    
}
