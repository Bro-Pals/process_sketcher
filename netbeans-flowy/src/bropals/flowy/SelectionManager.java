/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy;

import bropals.flowy.data.Node;
import bropals.flowy.data.NodeLine;
import bropals.flowy.data.Selectable;
import java.util.ArrayList;

/**
 * Manages the current selection and enables other objects to 
 * act on them.
 * @author Jonathon
 */
public class SelectionManager {
    
    private ArrayList<Selectable> selected;
    
    public SelectionManager() {
        selected = new ArrayList<>();
    }
    
    public ArrayList<Selectable> getSelected() {
        return selected;
    }
    
    public ArrayList<Node> getSelectedNodes() {
        ArrayList<Node> nodes = new ArrayList<>();
        for (int i=0; i<selected.size(); i++) {
            if (selected.get(i) instanceof Node) {
                nodes.add((Node)selected.get(i));
            }
        }
        return nodes;
    }
    
    public ArrayList<NodeLine> getSelectedNodeLines() {
        ArrayList<NodeLine> nodelines = new ArrayList<>();
        for (int i=0; i<selected.size(); i++) {
            if (selected.get(i) instanceof NodeLine) {
                nodelines.add((NodeLine)selected.get(i));
            }
        }
        return nodelines;
    }
    
    public Selectable getLastSelected() {
        if (!selected.isEmpty()) {
            return selected.get(selected.size() - 1);
        } else {
            return null;
        }
    }
    
}
