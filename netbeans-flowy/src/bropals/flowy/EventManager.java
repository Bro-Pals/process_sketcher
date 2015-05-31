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
        dragManager = new DragManager();
        selectionManager = new SelectionManager();
    }

    public boolean isSelected(Selectable s) {
        return selectionManager.getSelected().contains(s);
    }
    
    /**
     * Find the selectable selected
     * @param p A point that is in world units
     * @return The thing that is closest under the mouse, or null if nothing was close.
     */
    private Selectable getSelectableUnderPoint(Point p) {
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

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("PRESSED A KEY " + e.getKeyCode());
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
                    if (!selectedNode.getLinesConnected().isEmpty()) {
                        Node nextNode = null;
                        if (!e.isShiftDown()) { // find the next child if shift was being held down
                            for (int i=0; i<selectedNode.getLinesConnected().size(); i++) {
                                if (selectedNode.getLinesConnected().get(i).getChild() != null) {
                                    nextNode = selectedNode.getLinesConnected().get(i).getChild();
                                    break;
                                }
                            }
                        } else { // if shift is being held down then find the next parent
                            for (int i=0; i<selectedNode.getLinesConnected().size(); i++) {
                                if (selectedNode.getLinesConnected().get(i).getParent() != null) {
                                    nextNode = selectedNode.getLinesConnected().get(i).getParent();
                                    break;
                                }
                            }
                        }
                        
                        // if you found the next node, select it
                        if (nextNode != null) {
                            selectionManager.getSelected().clear();
                            selectionManager.getSelected().add(nextNode);
                        } else { // otherwise create a new node
                            if (!e.isShiftDown()) { /// ... if shift was not held down
                                
                            }
                        }
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
        Point mousePosition = window.getCamera().convertCanvasToWorld(e.getPoint());
        
        Selectable clickedOnThing = getSelectableUnderPoint(mousePosition);
        
        Node node = null;
        NodeLine nodeLine = null;
        if (clickedOnThing instanceof Node) {
            node = (Node)clickedOnThing;
        } else if (clickedOnThing instanceof NodeLine) {
            nodeLine = (NodeLine)clickedOnThing;
        }
        System.out.println(node);
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
                    createdNode.setX(dragManager.getInitialX());
                    createdNode.setY(dragManager.getInitialY());
                    window.getFlowchart().getNodes().add(createdNode);
                    // connect the two nodes with a line
                    createdNode.getLinesConnected().add(new NodeLine(node, createdNode));
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
        window.redrawView();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point mousePosition = window.getCamera().convertCanvasToWorld(e.getPoint());
        
        if (dragManager.isDragging()) {
            if (dragManager.getNewlyMadeNode() != null) {
                dragManager.setNewlyMadeNode(null);
                dragManager.setDragging(false); // done dragging the newly created node
            }
        }
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
            Point mousePos = window.getCamera().convertCanvasToWorld(e.getPoint());
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
    
    
}
