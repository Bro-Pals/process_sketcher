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
package bropals.flowy.data;

import java.util.ArrayList;

/**
 * An object to represent a flowchart.
 * @author Jonathon
 */
public class Flowchart {
    /**
     * The nodes that make up this Flowchart.
     */
    private ArrayList<Node> nodes;

    /**
     * Create the default flowchart.
     */
    public Flowchart() {
        nodes = new ArrayList<>();
        Node firstNode = new Node(100, 100);
        nodes.add(firstNode);
    }
    
    /**
     * Gets the nodes that make up this flowchart.
     * @return the nodes that make up this flowchart.
     */
    public ArrayList<Node> getNodes() {
        return nodes;
    }

    /**
     * This is only used for getting the node lines. Adding a NodeLine 
     * to the returned array will NOT add the line to the flowchart.
     * @return 
     */
    public ArrayList<NodeLine> getNodeLines() {
        ArrayList<NodeLine> lines = new ArrayList<>();
        for (Node n : getNodes()) {
            for (NodeLine nl : n.getLinesConnected()) {
                if (!lines.contains(nl)) {
                    lines.add(nl);
                }
            }
        }
        return lines;
    }
    
    
}
