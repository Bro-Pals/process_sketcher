/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy;

import bropals.flowy.data.Node;
import bropals.flowy.data.NodeLine;
import bropals.flowy.data.Selectable;
import java.awt.Font;
import java.util.ArrayList;

/**
 * Manages the current selection and enables other objects to 
 * act on them.
 * @author Jonathon
 */
public class SelectionManager {
    
    private ArrayList<Selectable> selected;
    private FlowchartWindow instance;
    
    public SelectionManager(FlowchartWindow window) {
        selected = new ArrayList<>();
        this.instance = window;
    }
    
    public ArrayList<Selectable> getSelected() {
        return selected;
    }
    
    public void select(Selectable selectable) {
        selected.add(selectable);
        refreshStylesTabVisiblity();
        refreshValuesOfStylesTabDueToNewSelection(selectable);
    }
    
    public void deselect(Selectable selectable) {
        selected.remove(selectable);
        refreshStylesTabVisiblity();
    }
    
    public void clearSelection() {
        selected.clear();
        makeAllStylesInvisible();
        instance.revalidateStyles();
    }
    
    private void makeAllStylesInvisible() {
        instance.getFontStylePanel().setVisible(false);
        instance.getLineStylePanel().setVisible(false);
        instance.getNodeStylePanel().setVisible(false);
    }
    
    private void makeAllStylesVisible() {
        instance.getFontStylePanel().setVisible(true);
        instance.getLineStylePanel().setVisible(true);
        instance.getNodeStylePanel().setVisible(true);
    }
    
    private void refreshStylesTabVisiblity() {
        if (selected.isEmpty()) {
            makeAllStylesInvisible();
        } else {
            boolean hasNode = false;
            boolean hasLine = false;
            for (int i=0; i<selected.size(); i++) {
                if (selected.get(i) instanceof Node) {
                    hasNode = true;
                } else if (selected.get(i) instanceof NodeLine) {
                    hasLine = true;
                }
                if (hasNode && hasLine) {
                    makeAllStylesVisible();
                    instance.revalidateStyles();
                    return;
                }
            }
            instance.getFontStylePanel().setVisible(true);
            if (hasNode) {
                instance.getNodeStylePanel().setVisible(true);
            } else if (hasLine) {
                instance.getLineStylePanel().setVisible(true);
            }
        }
        instance.revalidateStyles();
    }
    
    private void refreshValuesOfStylesTabDueToNewSelection(Selectable s) {
        Node n = null;
        NodeLine nl = null;
        if (s instanceof Node) {
            n = (Node)s;
        } else if (s instanceof NodeLine) {
            nl = (NodeLine)s;
        }
        instance.setFontPanelStyles(s.getFontStyle().getFontType(), s.getFontStyle().getFontColor(), s.getFontStyle().getFontSize());
        if (n != null) {
            //Node
            instance.setNodePanelStyles(n.getStyle().getShape(), n.getStyle().getBorderColor(), n.getStyle().getFillColor(), n.getStyle().getBorderSize());
        } else if (nl != null) {
            //NodeLine
            instance.setLinePanelStyles(nl.getStyle().getType(), nl.getStyle().getLineColor(), nl.getStyle().getLineSize());
        }
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
