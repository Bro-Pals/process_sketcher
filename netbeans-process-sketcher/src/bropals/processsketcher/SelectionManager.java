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
package bropals.processsketcher;

import bropals.processsketcher.data.Flowchart;
import bropals.processsketcher.data.Node;
import bropals.processsketcher.data.NodeLine;
import bropals.processsketcher.data.Selectable;
import java.awt.Font;
import java.awt.Point;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages the current selection and enables other objects to 
 * act on them.
 * @author Jonathon
 */
public class SelectionManager {
    
    /**
     * A list of everything being selected
     */
    private ArrayList<Selectable> selected;
    /**
     * The FlowChart window that is using this SelectionManager
     */
    private FlowchartWindow instance;
    
    /**
     * Create a new selection manager for a window.
     * @param window The window that will be using this SelectionManager
     */
    public SelectionManager(FlowchartWindow window) {
        selected = new ArrayList<>();
        this.instance = window;
    }
    
    /**
     * Get a list of everything that is selected. Adding selectables to this 
     * list will add them to the selected, but it is advised to use the select()
     * and deselect() methods
     * @return 
     */
    public ArrayList<Selectable> getSelected() {
        return selected;
    }
    
    /**
     * Select a single selectable
     * @param selectable The selectable being selected
     */
    public void select(Selectable selectable) {
        selected.add(selectable);
        instance.refreshStylesTabVisiblity();
        instance.refreshValuesOfStylesTabDueToUpdatedSelection();
        instance.revalidateStyles();
    }
    
    /**
     * Select many selectables
     * @param selectables A list of all the selectables being selected
     */
    public void select(ArrayList<Selectable> selectables) {
        // don't do anything if nothing is being changed
        if (selectables == null || selectables.isEmpty()) {
            return;
        }
        
        for (Selectable sel : selectables) {
            if (!selected.contains(sel)) {
                selected.add(sel);
            }
        }
        instance.refreshStylesTabVisiblity();
        instance.refreshValuesOfStylesTabDueToUpdatedSelection();
        instance.revalidateStyles();
    }
    
    /**
     * Deselect a single selectable
     * @param selectable The selectable being deselected
     */
    public void deselect(Selectable selectable) {
        selected.remove(selectable);
        instance.refreshStylesTabVisiblity();
        instance.revalidateStyles();
    }
    
    /**
     * Deselect many selectables
     * @param selectables A list of all the selectables being deselected
     */
    public void deselect(ArrayList<Selectable> selectables) {
        // don't do anything if nothing is being changed
        if (selectables == null || selectables.isEmpty()) {
            return;
        }
        
        selected.removeAll(selectables);
        instance.refreshStylesTabVisiblity();
        instance.revalidateStyles();
    }
    
    /**
     * Deselect everything that is selected
     */
    public void clearSelection() {
        selected.clear();
        instance.makeAllStylesInvisible();
        instance.revalidateStyles();
    }
  
    /**
     * Get all the nodes being selected. Adding a Node to this flowchart WON'T 
     * add it to the flowchart. This array might be empty if no Nodes are selected.
     * @return A list of all the NodeLines in the selection.
     */
    public ArrayList<Node> getSelectedNodes() {
        ArrayList<Node> nodes = new ArrayList<>();
        for (int i=0; i<selected.size(); i++) {
            if (selected.get(i) instanceof Node) {
                nodes.add((Node)selected.get(i));
            }
        }
        return nodes;
    }
    
    /**
     * Get a list of all the NodeLines currently selected. Adding a NodeLine
     * to this array WON'T add it to the flowchart. This array might be empty.
     * @return A list of all the NodeLines in the selection.
     */
    public ArrayList<NodeLine> getSelectedNodeLines() {
        ArrayList<NodeLine> nodelines = new ArrayList<>();
        for (int i=0; i<selected.size(); i++) {
            if (selected.get(i) instanceof NodeLine) {
                nodelines.add((NodeLine)selected.get(i));
            }
        }
        return nodelines;
    }
    
    /**
     * Get the most recently selected Selectable. This can be a Node
     * Or a NodeLine. If nothing is selected, this returns null.
     * @return The most recently selected Selectable, or returns null 
     *      if nothing is selected
     */
    public Selectable getLastSelected() {
        if (!selected.isEmpty()) {
            return selected.get(selected.size() - 1);
        } else {
            return null;
        }
    }
    
    /**
     * Find the selectable selected
     *
     * @param p A point that is in world units
     * @param flowchart The flowchart to search through
     * @return The thing that is closest under the mouse, or null if nothing was
     * close.
     */
    public Selectable getSelectableUnderPoint(Point.Float p) {
        Selectable thing = null; // initially nothing
        
        for (int i=instance.getFlowchart().getNodes().size()-1; i>=0; i--) {
            Node n = instance.getFlowchart().getNodes().get(i);
            if (p.getX() > n.getX() && p.getX() < n.getX() + n.getWidth()
                    && p.getY() > n.getY() && p.getY() < n.getY() + n.getHeight()) {
                return n;
            }
        }

        // it must be a minimum of 10 pixels away
        double maxDistance = instance.getCamera().getZoom() * 10;
        // keep track of the closest line's distance from the point
        double nearestDist = maxDistance;
        // if no nodes were found, find the nearest line.
        for (NodeLine nl : instance.getFlowchart().getNodeLines()) {
            // drag so we can get the points of the line
            Point[] linePoints = nl.getStyle().getType().renderLine(nl, instance.getCamera(), 
                    instance.getView().getGraphics(), 
                    instance.getEventManager().getTextTypeManager().isCursorShowing()&& nl == getLastSelected(), 
                    instance.getEventManager().getTextTypeManager().getLocationOfTypeCursor(), 
                    instance.getEventManager().getTextTypeManager().getLinePartTyping());
            Point.Float p1 = instance.getCamera().convertCanvasToWorld(linePoints[0]);
            Point.Float p2 = instance.getCamera().convertCanvasToWorld(linePoints[1]);

            // copied the formula from:
            // http://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line
            double distance = Math.abs(((p2.getY() - p1.getY()) * p.getX())
                    - ((p2.getX() - p1.getX()) * p.getY()) + (p2.getX() * p1.getY())
                    - (p2.getY() * p1.getX())) / Math.sqrt(Math.pow(p2.getY() - p1.getY(), 2)
                            + Math.pow(p2.getX() - p1.getX(), 2));

            float lowerX = 0;
            float higherX = 0;
            float lowerY = 0;
            float higherY = 0;
            
            if (p1.getX() < p2.getX()) {
                lowerX = (float)(p1.getX() - maxDistance);
                higherX = (float)(p2.getX() + maxDistance);
            } else {
                lowerX = (float)(p2.getX() - maxDistance);
                higherX = (float)(p1.getX() + maxDistance);
            }
            
            if (p1.getY() < p2.getY()) {
                lowerY = (float)(p1.getY() - maxDistance);
                higherY = (float)(p2.getY() + maxDistance);
            } else {
                lowerY = (float)(p2.getY() - maxDistance);
                higherY = (float)(p1.getY() + maxDistance);
            }
            
            if (distance < nearestDist && p.getX() > lowerX && p.getX() < higherX
                    && p.getY() > lowerY && p.getY() < higherY) {
                nearestDist = distance;
                thing = nl;
            }
        }

        return thing;
    }

    /**
     * Get a list of all the selectables that are completely inside of the given
     * box
     *
     * @param p The top left corner of the box in world units
     * @param width The width of the box in world units
     * @param height The height of the box in world units
     * @return A list of all selectables (nodes and node-lines) that were
     * completely inside of the box
     */
    public ArrayList<Selectable> getSelectablesUnderBox(Point p, float width, float height) {
        // readjust the position of the box with negative width or heights
        if (width < 0) {
            p.setLocation(p.getX() + width, p.getY());
            width = Math.abs(width);
        }
        if (height < 0) {
            p.setLocation(p.getX(), p.getY() + height);
            height = Math.abs(height);
        }

        ArrayList<Selectable> list = new ArrayList<>();
        for (Node n : instance.getFlowchart().getNodes()) {
            if (p.getX() < n.getX() && p.getX() + width > n.getX() + n.getWidth()
                    && p.getY() < n.getY() && p.getY() + height > n.getY() + n.getHeight()) {
                list.add(n);
            }
        }
        for (NodeLine nl : instance.getFlowchart().getNodeLines()) {
            if (list.contains(nl.getChild()) && list.contains(nl.getParent())
                    && !list.contains(nl)) {
                list.add(nl);
            }
        }

        return list;
    }

    /**
     * Remove everything in stuff from the flowchart.
     *
     * @param stuff What will be removed from flowchart.
     */
    public void removeSelectables(ArrayList<Selectable> stuff) {
        for (Selectable s : stuff) {
            if (s instanceof Node) {
                for (int i=0; i<instance.getFlowchart().getNodes().size(); i++) {

                    if (instance.getFlowchart().getNodes().get(i) == s) {
                        Node removedNode = instance.getFlowchart().getNodes().get(i);
                        instance.getFlowchart().getNodes().remove(i);
                        // look through the other nodes...
                        for (Node other : instance.getFlowchart().getNodes()) {
                            // to delete any node line that references the node we're removing
                            if (other != removedNode) {
                                for (NodeLine nl : other.getLinesConnected()) {
                                    // if the line references the removed node AT ALL
                                    if (nl.getChild() == removedNode || nl.getParent() == removedNode) {
                                        other.getLinesConnected().remove(nl);
                                        break;
                                    }
                                }
                            }
                        }
                         // break out of removing all the lines for that node
                        break;
                    }
                }
            } else if (s instanceof NodeLine) {
                for (int i=0; i<instance.getFlowchart().getNodes().size(); i++) {
                    // only remove the lines from nodes that are not being deleted
                    if (!stuff.contains(instance.getFlowchart().getNodes().get(i)) ) {
                        for (int l=0; l<instance.getFlowchart().getNodes().get(i).getLinesConnected().size(); l++) {
                            if (((NodeLine)s) == instance.getFlowchart().getNodes().get(i).getLinesConnected().get(l)) {
                                instance.getFlowchart().getNodes().get(i).getLinesConnected().remove(l);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Checks to see if selection manager is not selecting anything.
     * @return <code>true</code> if nothing is selected.
     */
    public boolean hasEmptySelection() {
        return selected.isEmpty();
    }
}
