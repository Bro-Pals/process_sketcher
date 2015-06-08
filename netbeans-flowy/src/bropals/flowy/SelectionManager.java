/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy;

import bropals.flowy.data.Flowchart;
import bropals.flowy.data.Node;
import bropals.flowy.data.NodeLine;
import bropals.flowy.data.Selectable;
import java.awt.Font;
import java.awt.Point;
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
    
    /**
     * Get a list of all the NodeLines currently selected. Adding
     * @return 
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
        for (Node n : instance.getFlowchart().getNodes()) {
            if (p.getX() > n.getX() && p.getX() < n.getX() + n.getWidth()
                    && p.getY() > n.getY() && p.getY() < n.getY() + n.getHeight()) {
                return n;
            }
        }

        // it must be a minimum of 10 pixels away
        double nearestDistance = 10 * (instance.getCamera().getZoom());
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

            if (distance < nearestDistance) {
                nearestDistance = distance;
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
                            for (NodeLine nl : other.getLinesConnected()) {
                                // if the line references the removed node AT ALL
                                if (other != removedNode && nl.getChild() == removedNode || nl.getParent() == removedNode) {
                                    other.getLinesConnected().remove(nl);
                                    break;
                                }
                            }
                        }
                         // break out of removing all the lines for that node
                        break;
                    }
                }
            } else if (s instanceof NodeLine) {
                for (int i=0; i<instance.getFlowchart().getNodes().size(); i++) {
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
