/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy;

import bropals.flowy.data.Node;
import bropals.flowy.data.NodeLine;
import bropals.flowy.data.Selectable;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

/**
 * Gets events from the view and StyleManager and handles them.
 * @author Jonathon
 */
public class EventManager implements KeyListener, MouseListener, MouseMotionListener {
 
    private SelectionManager selectionManager;
    private FlowchartWindow window;
    /**
     * 
     * NOTE: Make the camera not able to be transformed when dragging to the drag offsets work
     * 
     */
    private DragManager dragManager;
    
    public EventManager(FlowchartWindow instance) {
        window = instance;
        selectionManager = new SelectionManager();
        dragManager = new DragManager(selectionManager);
    }

    public boolean isSelected(Selectable s) {
        return selectionManager.getSelected().contains(s);
    }
    
    /**
     * Find the selectable selected
     * @param p A point that is in world units
     * @return The thing that is closest under the mouse, or null if nothing was close.
     */
    private Selectable getSelectableUnderPoint(Point.Float p) {
        Selectable thing = null; // initially nothing
        for (Node n : window.getFlowchart().getNodes()) {
            if (p.getX() > n.getX() && p.getX() < n.getX() + n.getWidth() &&
                    p.getY() > n.getY() && p.getY() < n.getY() + n.getHeight()) {
                thing = n;
                return thing;
            }
        }
        
        // if no nodes were found, find the nearest line.
        
        // do that stuff here
        
        
        return thing;
    }
    
    /**
     * Get a list of all the selectables that are completely inside of the given box
     * @param p The top left corner of the box in world units
     * @param width The width of the box in world units
     * @param height The height of the box in world units
     * @return A list of all selectables (nodes and node-lines) that were completely inside of the box
     */
    private ArrayList<Selectable> getSelectablesUnderBox(Point p, float width, float height) {
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
        for (Node n : window.getFlowchart().getNodes()) {
            if (p.getX() < n.getX() && p.getX() + width > n.getX() + n.getWidth() &&
                    p.getY() < n.getY() && p.getY() + height > n.getY() + n.getHeight()) {
                list.add(n);
            }
        }
        for (NodeLine nl : window.getFlowchart().getNodeLines()) {
            if (list.contains(nl.getChild()) && list.contains(nl.getParent())
                    && !list.contains(nl)) {
                System.out.println("Adding a line to the selection");
                list.add(nl);
            }
        }
        
        return list;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        //System.out.println("PRESSED A KEY " + e.getKeyCode());
        switch (e.getKeyCode()) {
            case KeyEvent.VK_N:
                if (e.isControlDown()) {
                    System.out.println("Creating a flowchart");
                    window.getFlowchartWindowManager().newFlowchart();
                }
                break;
            case KeyEvent.VK_TAB:
                System.out.println("TAB");
                if (selectionManager.getLastSelected() != null &&
                        selectionManager.getLastSelected() instanceof Node) {
                    Node selectedNode = (Node)selectionManager.getLastSelected();
                    
                    boolean nextNodeIsThere = false;
                    if (!selectedNode.getLinesConnected().isEmpty()) {
                        Node nextNode = null;
                        if (!e.isShiftDown()) { // find the next child if shift was being held down
                            for (int i=0; i<selectedNode.getLinesConnected().size(); i++) {
                                if (selectedNode.getLinesConnected().get(i).getChild() != selectedNode &&
                                        selectedNode.getLinesConnected().get(i).getChild()!= null) {
                                    nextNode = selectedNode.getLinesConnected().get(i).getChild();
                                    break;
                                }
                            }
                        } else { // if shift is being held down then find the next parent
                            for (int i=0; i<selectedNode.getLinesConnected().size(); i++) {
                                if (selectedNode.getLinesConnected().get(i).getParent() != selectedNode &&
                                        selectedNode.getLinesConnected().get(i).getParent()!= null) {
                                    nextNode = selectedNode.getLinesConnected().get(i).getParent();
                                    break;
                                }
                            }
                        }
                        
                        // if you found the next node, select it
                        if (nextNode != null) {
                            nextNodeIsThere = true;
                            selectionManager.getSelected().clear();
                            selectionManager.getSelected().add(nextNode);
                            //System.out.println("selecting next node");
                        }
                    }
                    
                    if (!nextNodeIsThere && !e.isShiftDown()) { /// ... if shift was not held down
                        // create a new node
                        Node createdNode = (Node)selectedNode.clone();
                       // System.out.println(node + " and " + createdNode);
                        // position it to the right of the previously selected node
                        createdNode.setX(selectedNode.getX() + selectedNode.getWidth() + 120);
                        createdNode.setY(selectedNode.getY());
                        createdNode.getLinesConnected().clear();
                        window.getFlowchart().getNodes().add(createdNode);
                        // connect the two nodes with a line
                        NodeLine line = new NodeLine(selectedNode, createdNode);
                        createdNode.getLinesConnected().add(line);
                        selectedNode.getLinesConnected().add(line);
                        dragManager.setNewlyMadeNode(createdNode);
                         // select your newly created node
                        selectionManager.getSelected().clear();
                        selectionManager.getSelected().add(createdNode);
                        // redraw the window
                        window.redrawView();
                    }
                }
                window.redrawView();
                break;
            case KeyEvent.VK_A:
                if (e.isControlDown()) {
                    // select everything with ctrl+a
                    selectionManager.getSelected().clear();
                    for (int i=0; i<window.getFlowchart().getNodes().size(); i++) {
                        selectionManager.getSelected().add(window.getFlowchart().getNodes().get(i));
                    }
                    for (int i=0; i<window.getFlowchart().getNodeLines().size(); i++) {
                        selectionManager.getSelected().add(window.getFlowchart().getNodeLines().get(i));
                    }
                }
                window.redrawView();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point.Float mousePosition = window.getCamera().convertCanvasToWorld(e.getPoint());
        //System.out.println(mousePosition);
        //System.out.println(e.getPoint());
        
        Selectable clickedOnThing = getSelectableUnderPoint(mousePosition);
        
        Node node = null;
        NodeLine nodeLine = null;
        if (clickedOnThing instanceof Node) {
            node = (Node)clickedOnThing;
        } else if (clickedOnThing instanceof NodeLine) {
            nodeLine = (NodeLine)clickedOnThing;
        }
        //System.out.println(node);
        // actions involving a node
        if (node != null) {
            if (!dragManager.isDragging()) { // not yet dragging anything...
                if (e.getButton() == MouseEvent.BUTTON3) {
                    dragManager.setDragging(true); // start dragging
                    dragManager.setInitialX((int)mousePosition.getX());
                    dragManager.setInitialY((int)mousePosition.getY());
                    dragManager.setOffsetX(0); // initially
                    dragManager.setOffsetY(0);
                    
                    Node createdNode = (Node)node.clone();
                  //  System.out.println(node + " and " + createdNode);
                    createdNode.setX(dragManager.getInitialX());
                    createdNode.setY(dragManager.getInitialY());
                    createdNode.getLinesConnected().clear();
                    window.getFlowchart().getNodes().add(createdNode);
                    // connect the two nodes with a line
                    NodeLine line = new NodeLine(node, createdNode);
                    createdNode.getLinesConnected().add(line);
                    node.getLinesConnected().add(line);
                    dragManager.setNewlyMadeNode(createdNode);
                    window.redrawView();
                }
                
            }
            
            if (e.getButton() == MouseEvent.BUTTON1) {
                if (!e.isShiftDown()) {
                    selectionManager.getSelected().clear();
                }
                selectionManager.getSelected().add(clickedOnThing);
            }
        } else if (nodeLine != null) { // actions only for node lines
            
        }
        
        if (node == null && nodeLine == null && e.getButton() == MouseEvent.BUTTON1) {
             // clear selection if you don't click anything
            if (!e.isShiftDown()) {
                selectionManager.getSelected().clear();
            }
            
            // begin dragging a box for selection in a box
            if (!dragManager.isDragging()) {
                System.out.println("Start box selecting");
                dragManager.setInitialX((int)mousePosition.getX());
                dragManager.setInitialY((int)mousePosition.getY());
                dragManager.setOffsetX(0);
                dragManager.setOffsetY(0);
                dragManager.setBoxSelecting(true);
                dragManager.setDragging(true);
            }
        }
        window.redrawView();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point.Float mousePosition = window.getCamera().convertCanvasToWorld(e.getPoint());
        
        if (dragManager.isDragging()) {
            if (e.getButton() == MouseEvent.BUTTON3 && dragManager.getNewlyMadeNode() != null) {
                dragManager.setNewlyMadeNode(null);
                dragManager.setDragging(false); // done dragging the newly created node
            }
        }
        if (e.getButton() == MouseEvent.BUTTON1 && dragManager.isBoxSelecting()) {
            System.out.println("Finished box selecting");
            ArrayList<Selectable> selectedItems = getSelectablesUnderBox(
                    new Point((int)dragManager.getInitialX(), (int)dragManager.getInitialY()), 
                    dragManager.getOffsetX(), dragManager.getOffsetY());
            // clear selection
            if (!e.isShiftDown()) {
                selectionManager.getSelected().clear();
            }
            System.out.println("Adding " + selectedItems.size() + " to the selection");
            selectionManager.getSelected().addAll(selectedItems);
            dragManager.setBoxSelecting(false);
            dragManager.setDragging(false);
        }
        window.redrawView();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragManager.isDragging()) {
            Point.Float mousePos = window.getCamera().convertCanvasToWorld(e.getPoint());
            dragManager.setOffsetX((int)mousePos.getX() - dragManager.getInitialX());
            dragManager.setOffsetY((int)mousePos.getY() - dragManager.getInitialY());
            
            if (dragManager.getNewlyMadeNode() != null) {
                dragManager.getNewlyMadeNode().setX((int)mousePos.getX());
                dragManager.getNewlyMadeNode().setY((int)mousePos.getY());
            }
        }
                
        window.redrawView();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
    }
    
    public DragManager getDragManager() {
        return dragManager;
    }
}
