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
 *
 * @author Jonathon
 */
public class SelectionManager {
    
    private ArrayList<Selectable> selected;
    
    public ArrayList<Selectable> getSelected() {
        return selected;
    }
    
    public ArrayList<Node> getSelectedNodes() {
        return null;
    }
    
    public ArrayList<NodeLine> getSelectedNodeLines() {
        return null;
    }
    
    public Selectable getLastSelected() {
        return null;
    }
    
}
