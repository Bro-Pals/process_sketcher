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
import java.awt.Dimension;
import java.awt.Point;

/**
 * An action that occurs when you scale a Node
 * @author Kevin
 */
public class ScaledNode extends Action {

    private Node node;
    private Dimension dimension;
    private Point position;
    
    public ScaledNode(Node editedNode, Dimension oldDimension, Point initialPosition) {
        node = editedNode;
        dimension = oldDimension;
        position = initialPosition;
    }
    
    @Override
    public void undo(FlowchartWindow instance) {
        //System.out.println("Undid scaling a node");
        node.setWidth((float)dimension.getWidth());
        node.setHeight((float)dimension.getHeight());
        node.setX((float)position.getX());
        node.setY((float)position.getY());
    }
    
}
