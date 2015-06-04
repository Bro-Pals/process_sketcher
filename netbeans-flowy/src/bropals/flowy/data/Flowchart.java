/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.data;

import java.util.ArrayList;

/**
 *
 * @author Jonathon
 */
public class Flowchart {
    
    private ArrayList<Node> nodes;

    public Flowchart() {
        nodes = new ArrayList<>();
        Node firstNode = new Node(100, 100);
        nodes.add(firstNode);
    }
    
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
