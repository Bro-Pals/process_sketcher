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

import bropals.processsketcher.action.Selected;
import bropals.processsketcher.action.ScaledNode;
import bropals.processsketcher.action.Cutted;
import bropals.processsketcher.action.MovedNodes;
import bropals.processsketcher.action.EditedNodeLineText;
import bropals.processsketcher.action.Pasted;
import bropals.processsketcher.action.Deselected;
import bropals.processsketcher.action.ConnectedNodes;
import bropals.processsketcher.action.SelectedTabbed;
import bropals.processsketcher.action.HistoryManager;
import bropals.processsketcher.action.CreatedConnectedNodeTabbed;
import bropals.processsketcher.action.Deleted;
import bropals.processsketcher.action.EditedNodeText;
import bropals.processsketcher.action.CreatedConnectedNode;
import bropals.processsketcher.action.*;
import bropals.processsketcher.data.Node;
import bropals.processsketcher.data.NodeLine;
import bropals.processsketcher.data.Selectable;
import bropals.processsketcher.style.NodeStyle;
import bropals.processsketcher.style.Shape;
import bropals.processsketcher.util.BooleanBlinkListener;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * Gets events from the view and StyleManager and handles them.
 *
 * @author Jonathon
 */
public class EventManager implements KeyListener, MouseListener, MouseMotionListener, BooleanBlinkListener {

    /**
     * The SelectionManager used by this object and the window.
     */
    private final SelectionManager selectionManager;
    /**
     * The window that this event manager is a listener for.
     */
    private final FlowchartWindow window;
    /**
     * The DragManager for containing information about dragging things.
     */
    private final DragManager dragManager;
    /**
     * Contains all the information and methods for typing text.
     */
    private final TextTypeManager textTypeManager;
    /**
     * The HistoryManager to manage the history and implement the undo feature.
     */
    private final HistoryManager historyManager;
    /**
     * The variable to track if the spacebar is held down or not.
     */
    private boolean spacebar;

    /**
     * How close the mouse has to be to an edge of a node to be able to resize
     * it, in world coordinates.
     */
    public final static float DRAG_RESIZE_DISTANCE = 6.0f;

    /**
     * Create a new EventManager 
     * @param instance The window that this event manager is listening to.
     */
    public EventManager(FlowchartWindow instance) {
        window = instance;
        selectionManager = new SelectionManager(instance);
        dragManager = new DragManager(selectionManager);
        textTypeManager = new TextTypeManager();
        historyManager = new HistoryManager(instance);
    }

    /**
     * See if the given Selectable is selected
     * @param s The selectable
     * @return True if the selectable is selected; returns false if it's not selected.
     */
    public boolean isSelected(Selectable s) {
        return selectionManager.getSelected().contains(s);
    }
    
    /**
     * Get the selection manager used by this event manager
     * @return the selection manager used by this event manager
     */
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }


    /**
     * ***** Methods for all the actions (as a level of indirection) ******
     */
    /**
     * Delete all the selected nodes and deselect
     */
    public void deleteSelected() {
        ArrayList<Selectable> deletedThings = new ArrayList<>();
        deletedThings.addAll(selectionManager.getSelected());
        
        // add a nodeline to the deleted list of it's parent and child are being deleted too
        for (NodeLine nl : window.getFlowchart().getNodeLines()) {
            if (!deletedThings.contains(nl) && (deletedThings.contains(nl.getChild()) || 
                    deletedThings.contains(nl.getParent()))) {
                deletedThings.add(nl);
            }
        }
        selectionManager.removeSelectables(deletedThings);
        // add it to history
        historyManager.addToHistory(new Deleted(deletedThings));
        
        selectionManager.clearSelection();
        window.redrawView();
    }

    /**
     * Make a new flowchart window
     */
    public void createNewFlowchart() {
        window.getFlowchartWindowManager().newFlowchart();
        window.redrawView();
    }

    /**
     * Add what's selected to the clipboard
     */
    public void copySelected() {
        if (!selectionManager.getSelected().isEmpty()) {
            dragManager.setStuffInClipboard(selectionManager.getSelected());
        }
    }

    /**
     * Cut the selected items, adding them to the clipboard and deleting them
     */
    public void cutSelected() {
        if (!selectionManager.getSelected().isEmpty()) {
            ArrayList<Selectable> oldClipboard = new ArrayList<>();
            oldClipboard.addAll(dragManager.getClipboard());
            ArrayList<Selectable> deletedThings = new ArrayList<>();
            deletedThings.addAll(selectionManager.getSelected());

            // add a nodeline to the deleted list of it's parent and child are being deleted too
            for (NodeLine nl : window.getFlowchart().getNodeLines()) {
                if (!deletedThings.contains(nl) && (deletedThings.contains(nl.getChild()) || 
                        deletedThings.contains(nl.getParent()))) {
                    deletedThings.add(nl);
                }
            }
            dragManager.setStuffInClipboard(deletedThings);
            selectionManager.removeSelectables(deletedThings);
            // add the cut action to hisotory
            
            
            historyManager.addToHistory(new Cutted(new Deleted(deletedThings), oldClipboard));
            selectionManager.clearSelection();
        }
        window.redrawView();
    }

    /**
     * Past what's in the clipboard, placing it offset from the original copy
     */
    public void pasteClipboard() { 
        if (!dragManager.getClipboard().isEmpty()) {
            // copy every node so it's all in the same order
            // look through the original array, and connect lines
            //    in the next array to match the original.
            //    both the chil and parrent node in the line have to be
            //    in the originally copied array to copy the line
            // translate the entire copied array slightly to offset it
            ArrayList<Node> pastedNodes = new ArrayList<>();
            
            // clone in the nodes
            for (int i=0; i<dragManager.getClipboard().size(); i++) {
                if (dragManager.getClipboard().get(i) instanceof Node) {
                    pastedNodes.add((Node)(((Node)dragManager.getClipboard().get(i)).clone()));
                }
            }
            
            // clone in the node lines if the parent and child nodes of the line are in the pasted array
            for (int i=0; i<pastedNodes.size(); i++) {
                // copy the array of the original lines from the original array
                ArrayList<NodeLine> linesConnected = ((Node)(dragManager.getClipboard().get(i))).getLinesConnected();
                for (int l=0; l<linesConnected.size(); l++) {
                    int childIndex = -1;
                    int parentIndex = -1;
                    // find the location of the child in the original array for the original line
                    for (int n=0; n<dragManager.getClipboard().size(); n++) {
                        if (dragManager.getClipboard().get(n) == linesConnected.get(l).getChild()) {
                            childIndex = n;
                        } else if (dragManager.getClipboard().get(n) == linesConnected.get(l).getParent()) {
                            parentIndex = n;
                        }
                        // finish early if both parent and child are found
                        if (parentIndex != -1 && childIndex != -1) {
                            break;
                        }
                    }
                    
                    // if the child was found in the original array then add the new 
                    //  line using the same (copy) of the parent and childs
                    if (childIndex != -1 && parentIndex != -1) {
                        NodeLine clonedLine = (NodeLine)(linesConnected.get(l).clone());
                        clonedLine.setParent(pastedNodes.get(parentIndex));
                        clonedLine.setChild(pastedNodes.get(childIndex));
                        pastedNodes.get(parentIndex).getLinesConnected().add(clonedLine);
                        pastedNodes.get(childIndex).getLinesConnected().add(clonedLine);
                    }
                }
            }
            
            selectionManager.clearSelection();
            
            // add everything to the flowchart, translating slightly, and selecting them
            for (int i=0; i<pastedNodes.size(); i++) {
                window.getFlowchart().getNodes().add(pastedNodes.get(i));
                pastedNodes.get(i).setX(pastedNodes.get(i).getX() + 30);
                pastedNodes.get(i).setY(pastedNodes.get(i).getY() + 30);
                
                // select the newly added nodes and their connected lines
                selectionManager.select(pastedNodes.get(i));
                for (NodeLine nl : pastedNodes.get(i).getLinesConnected())
                    selectionManager.select(nl);
            }
            
            ArrayList<Selectable> pastedThings = new ArrayList<>();
            pastedThings.addAll(pastedNodes);
            historyManager.addToHistory(new Pasted(pastedThings));
        }
    }
    
    /**
     * Select everything in the flowchart
     */
    public void selectAll() {
        ArrayList<Selectable> currentSelection = new ArrayList<>();
        currentSelection.addAll(selectionManager.getSelected());
        
        selectionManager.clearSelection();
        for (int i = 0; i < window.getFlowchart().getNodes().size(); i++) {
            if (!selectionManager.getSelected().contains(window.getFlowchart().getNodes().get(i))) {
                selectionManager.select(window.getFlowchart().getNodes().get(i));
            }
        }
        for (int i = 0; i < window.getFlowchart().getNodeLines().size(); i++) {
            selectionManager.select(window.getFlowchart().getNodeLines().get(i));
        }
        
        // make a list of all the things newly added to the selection
        ArrayList<Selectable> newlySelected = new ArrayList<>();
        for (Selectable s : selectionManager.getSelected()) {
            if (!currentSelection.contains(s)) {
                newlySelected.add(s);
            }
        }
        
        // add the newly added things only if there are any
        if (!newlySelected.isEmpty()) {
            historyManager.addToHistory(new Selected(newlySelected));
        }
    }

    /**
     * Selects the next or previous node in relation to the last selected node.
     * If there are no next nodes but you're trying to select one, it creates a
     * node.s
     *
     * @param previous If you're going to select the previous node. If it's
     * false, it will try to select the next node or create one if there is none
     * yet.
     */
    public void selectConnectedNode(boolean previous) {
        if (selectionManager.getLastSelected() != null
                && selectionManager.getLastSelected() instanceof Node) {
            Node selectedNode = (Node) selectionManager.getLastSelected();
             
            ArrayList<Selectable> initialSelected = new ArrayList<>();
            initialSelected.addAll(selectionManager.getSelected());
            
            boolean nextNodeIsThere = false;
            if (!selectedNode.getLinesConnected().isEmpty()) {
                Node nextNode = null;
                if (!previous) { // find the next child if shift was being held down
                    for (int i = 0; i < selectedNode.getLinesConnected().size(); i++) {
                        if (selectedNode.getLinesConnected().get(i).getChild() != selectedNode
                                && selectedNode.getLinesConnected().get(i).getChild() != null) {
                            nextNode = selectedNode.getLinesConnected().get(i).getChild();
                            break;
                        }
                    }
                } else { // if shift is being held down then find the next parent
                    for (int i = 0; i < selectedNode.getLinesConnected().size(); i++) {
                        if (selectedNode.getLinesConnected().get(i).getParent() != selectedNode
                                && selectedNode.getLinesConnected().get(i).getParent() != null) {
                            nextNode = selectedNode.getLinesConnected().get(i).getParent();
                            break;
                        }
                    }
                }

                // if you found the next node, select it
                if (nextNode != null) {
                    nextNodeIsThere = true;
                    selectionManager.clearSelection();
                    selectionManager.select(nextNode);
                }
            }

            if (!nextNodeIsThere && !previous) { /// ... if shift was not held down
                // create a new node
                Node createdNode = (Node) selectedNode.clone();
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
                selectionManager.clearSelection();
                selectionManager.select(createdNode);
                // add creating a node through tabbing to history
                historyManager.addToHistory((new CreatedConnectedNodeTabbed(createdNode, line, initialSelected)));
            } else {
                // if no node is created the action was a selection from tabbing
                historyManager.addToHistory(new SelectedTabbed(initialSelected));
            }
        }
        window.redrawView();
    }

    /**
     * Remove a character at the location of the cursor from the string.
     * @param original The original string
     * @param cursorLocation The location of the cursor. If the cursorLocation is
     *          equal to the length of the string, it will delete the character
     *          at the end of the string. If it's 0, it will not delete anything.
     * @return The resulting string
     */
    private String deleteCharacter(String original, int cursorLocation) {
        // don't delete if it's at the start of the string
        if (cursorLocation <= 0) {
            return original;
        } else if (cursorLocation == 1) {
            return original.substring(1);
        } else if (cursorLocation == original.length()) {
            return original.substring(0, original.length() - 1);
        }
        
        
        // wo|rd   ->   w|rd   (location 2)
        return (original.substring(0, cursorLocation - 1) + original.substring(cursorLocation));
    }
    
    /**
     * Inserts a character at the location of the cursor into the string
     * @param original The original string
     * @param cursorLocation The location of the cursor. The character is inserted 
     *              at the character in front of the one whose index is cursorLocation - 1.
     * @return The resulting string
     */
    private String insertCharacter(String original, String character, int cursorLocation) {
        // |word + A  ->  A|word
        // word| + A -> wordA|
        // wo|rd + A -> woArd  (location 2)
        if (cursorLocation == 0) {
            return character + original;
        } else if (cursorLocation == original.length()) {
            return original + character;
        }
        return (original.substring(0, cursorLocation) + character + original.substring(cursorLocation));
    }
    
    /**
     * Open a print dialog for printing the flowchart
     */
    public void printFlowchart() {
        if (PrinterJob.lookupPrintServices().length > 0) {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Process Sketcher: " +window.getFlowchartName());
            window.getFlowchart().passInstance(window);
            PageFormat instance = new PageFormat();
            PageFormat old = instance;
            
            if ( (instance = job.pageDialog(old)) != old) {
                window.showPrintPreview(
                    (int)(instance.getImageableX()),
                    (int)(instance.getWidth()-instance.getImageableX()-instance.getImageableWidth()),
                    (int)(instance.getImageableY()),
                    (int)(instance.getHeight()-instance.getImageableY()-instance.getImageableHeight()),
                    (int)(instance.getImageableWidth()),
                    (int)(instance.getImageableHeight()),
                    job
                );
            }
        } else {
            JOptionPane.showMessageDialog(window, "There are no printers on this computer.", "Can't print", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Reset the camera view back to the orgin with 100% zoom
     */
    public void resetView() {
        window.getCamera().resetView();
        window.redrawView();
    }
    
    /**
     * Transform the camera so the entire flowchart fits in view of the window
     */
    public void fitFlowchartToView() {
        ArrayList<Node> nodes = window.getFlowchart().getNodes();
        // find the bounds of the entire flowchart in world units
        float smallestX = nodes.get(0).getX(); // world units
        float smallestY = nodes.get(0).getY(); // world units
        float largestX = nodes.get(0).getX() + nodes.get(0).getWidth(); // world units
        float largestY = nodes.get(0).getY() + nodes.get(0).getHeight(); // world units
        
        // search through all the nodes, finding the smallest and largest bounds...
        for (int i=1; i<nodes.size(); i++) {
            if (nodes.get(i).getX() < smallestX) {
                smallestX = nodes.get(i).getX();
            }
            
            if (nodes.get(i).getY() < smallestY) {
                smallestY = nodes.get(i).getY();
            }
            
            if (nodes.get(i).getX() + nodes.get(i).getWidth() > largestX) {
                largestX = nodes.get(i).getX() + nodes.get(i).getWidth();
            }
            
            if (nodes.get(i).getY() + nodes.get(i).getHeight() > largestY) {
                largestY = nodes.get(i).getY() + nodes.get(i).getHeight();
            }
        }
        
        float padding = 30; // world units of padding
        // add the padding to the bounds
        smallestX = smallestX - padding;
        smallestY = smallestY - padding;
        largestX = largestX + padding;
        largestY = largestY + padding;
        
        int sizeToFitTo = 0;
        int translateX = 0;
        int translateY = 0;
        if (window.getView().getWidth() > window.getView().getHeight()) {
            sizeToFitTo = window.getView().getHeight();
            translateX = (sizeToFitTo - window.getView().getWidth())/2;
        } else {
            sizeToFitTo = window.getView().getWidth();
            translateY = (sizeToFitTo - window.getView().getHeight())/2;
        }
        
        window.getCamera().setWorldLocationX(smallestX);
        window.getCamera().setWorldLocationY(smallestY);
        if (largestX - smallestX > largestY - smallestY) {
            window.getCamera().setZoom((largestX - smallestX + (padding * 3)) / sizeToFitTo);
        } else {
            window.getCamera().setZoom((largestY - smallestY + (padding * 3)) / sizeToFitTo);
        }

        // if translate X is still 0... center the flowchart to the window on the x axis
        if (translateX == 0) {
            translateX =  (int)((largestX - smallestX) / window.getCamera().getZoom()) - sizeToFitTo;
            translateX = translateX / 2;
        } else {
            translateY = (int)((largestY - smallestY) / window.getCamera().getZoom()) - sizeToFitTo;
            translateY = translateY / 2;
        }
        
        window.getCamera().setCanvasLocationX(
                window.getCamera().getCanvasLocationX() + 
                        (translateX));
        
        window.getCamera().setCanvasLocationY(
                window.getCamera().getCanvasLocationY() +
                        (translateY));
        
        System.out.println("New zoom: " + window.getCamera().getZoom());
        
        window.redrawView();
    }
    
    /**
     * Make a new default node that isn't connected to anything and place it to the center of the view.
     */
    public void createNode() {
        // add a shape, placing it in the center of the screen
        Node node = new Node(0, 0);
        window.getFlowchart().getNodes().add(node);
        
        // get the center of the screen in world coordinates
        float centerX = window.getCamera().convertCanvasToWorldX(
                window.getView().getWidth()/2);
        float centerY = window.getCamera().convertCanvasToWorldY(
                window.getView().getHeight()/2);
        
        // position the node to the center
        node.setX(centerX - (node.getWidth()/2));
        node.setY(centerY - (node.getHeight()/2));
        
        // track it in the history
        window.getEventManager().getHistoryManager().addToHistory(new CreatedNode(node));
        
        // redraw the view
        window.redrawView();
    }
    
    @Override
    public void keyTyped(KeyEvent e) {

        // if you're selecting soemthing that you aren't holding alt or control (for a different action)
        if (selectionManager.getLastSelected() != null && !e.isAltDown() && !e.isControlDown()) {
            if (selectionManager.getSelected().size() > 1) {
                Selectable lastSelected = selectionManager.getLastSelected();
                selectionManager.clearSelection();
                selectionManager.select(lastSelected);
            }

            // if a TAB key was typed
            if (e.getKeyChar() == '\t') {
                return; // nothing else happens...
            }

            // if you're selecting a node...
            if (selectionManager.getLastSelected() instanceof Node) {
                Node editNode = (Node) selectionManager.getLastSelected();
                // make sure textTypeManager.getLocationOfTypeCursor() is in the bounds of the text
                
                if (textTypeManager.getLocationOfTypeCursor() > editNode.getInnerText().length() || textTypeManager.getLocationOfTypeCursor() < 0)
                    textTypeManager.setLocationOfTypeCursor(editNode.getInnerText().length());

                String oldText = editNode.getInnerText();
                
                if (((int) e.getKeyChar()) == KeyEvent.VK_BACK_SPACE && editNode.getInnerText().length() > 0) {
                    // take off the character at the location of the string
                    editNode.setInnerText(deleteCharacter(editNode.getInnerText(), textTypeManager.getLocationOfTypeCursor()));
                    if (textTypeManager.getLocationOfTypeCursor() > 0) {
                        textTypeManager.decrementLocationOfTypeCursor();
                    }
                } else if (((int) e.getKeyChar()) != KeyEvent.VK_BACK_SPACE) {
                    // add the typed character to the end
                    editNode.setInnerText(insertCharacter(editNode.getInnerText(), 
                            "" + e.getKeyChar(), textTypeManager.getLocationOfTypeCursor()));
                    textTypeManager.incrementLocationOfTypeCursor();
                }
                
                if (!oldText.equals(editNode.getInnerText())) {
                    historyManager.addToHistory(new EditedNodeText(editNode, oldText));
                }
                
            // if you're selecting a node line...
            } else if (selectionManager.getLastSelected() != null
                    && selectionManager.getLastSelected() instanceof NodeLine) {
                NodeLine editLine = (NodeLine) selectionManager.getLastSelected();

                String oldText = "";
                
                int lengthOfEditingLine = 0;
                switch (textTypeManager.getLinePartTyping()) {
                    case TextTypeManager.CENTER:
                        lengthOfEditingLine = editLine.getCenterText().length();
                        oldText = editLine.getCenterText();
                        break;
                    case TextTypeManager.TAIL:
                        lengthOfEditingLine = editLine.getTailText().length();
                        oldText = editLine.getTailText();
                        break;
                    case TextTypeManager.HEAD:
                        lengthOfEditingLine = editLine.getHeadText().length();
                        oldText = editLine.getHeadText();
                        break;
                }

                // move the cursor to the end of the newly selected line
                if (textTypeManager.getLocationOfTypeCursor() > lengthOfEditingLine || textTypeManager.getLocationOfTypeCursor() < 0)
                    textTypeManager.setLocationOfTypeCursor(lengthOfEditingLine);
                                
                if (((int) e.getKeyChar()) == KeyEvent.VK_BACK_SPACE && lengthOfEditingLine > 0) {
                    // take off the last character in the string
                    switch (textTypeManager.getLinePartTyping()) {
                        case TextTypeManager.CENTER:
                            editLine.setCenterText(deleteCharacter(editLine.getCenterText(), textTypeManager.getLocationOfTypeCursor()));
                            break;
                        case TextTypeManager.TAIL:
                            editLine.setTailText(deleteCharacter(editLine.getTailText(), textTypeManager.getLocationOfTypeCursor()));
                            break;
                        case TextTypeManager.HEAD:
                            editLine.setHeadText(deleteCharacter(editLine.getHeadText(), textTypeManager.getLocationOfTypeCursor()));
                            break;
                    }
                    textTypeManager.decrementLocationOfTypeCursor();
                } else if (((int) e.getKeyChar()) == KeyEvent.VK_ENTER) {
                    // cycle what part of the line that is being edited
                    if (textTypeManager.getLinePartTyping() < 2) {
                        textTypeManager.setLinePartTyping(textTypeManager.getLinePartTyping() + 1);
                    } else {
                        textTypeManager.setLinePartTyping(0);
                    }
                    switch (textTypeManager.getLinePartTyping()) {
                        case TextTypeManager.CENTER:
                            textTypeManager.setLocationOfTypeCursor(editLine.getCenterText().length());
                            break;
                        case TextTypeManager.TAIL:
                            textTypeManager.setLocationOfTypeCursor(editLine.getTailText().length());
                            break;
                        case TextTypeManager.HEAD:
                            textTypeManager.setLocationOfTypeCursor(editLine.getHeadText().length());
                            break;
                    }
                } else if (((int) e.getKeyChar()) != KeyEvent.VK_BACK_SPACE) {
                    // add the typed character to the end
                    switch (textTypeManager.getLinePartTyping()) {
                        case TextTypeManager.CENTER:
                            editLine.setCenterText(
                                    insertCharacter(editLine.getCenterText(), 
                                        "" + e.getKeyChar(), textTypeManager.getLocationOfTypeCursor()));
                            break;
                        case TextTypeManager.TAIL:
                            editLine.setTailText(
                                    insertCharacter(editLine.getTailText(), 
                                        "" + e.getKeyChar(), textTypeManager.getLocationOfTypeCursor()));
                            break;
                        case TextTypeManager.HEAD:
                            editLine.setHeadText(
                                    insertCharacter(editLine.getHeadText(), 
                                        "" + e.getKeyChar(), textTypeManager.getLocationOfTypeCursor()));
                            break;
                    }
                    textTypeManager.incrementLocationOfTypeCursor();
                }
                System.out.println("Old Text: " + oldText + " Line part: " + textTypeManager.getLinePartTyping());
                if ((int)e.getKeyChar() != KeyEvent.VK_ENTER) {
                    historyManager.addToHistory(new EditedNodeLineText(editLine, oldText, textTypeManager.getLinePartTyping()));
                }
            }
        }

        window.redrawView();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                spacebar = true;
                break;
            case KeyEvent.VK_DELETE:
                deleteSelected();
                break;
            case KeyEvent.VK_P:
                if (e.isControlDown()) {
                    printFlowchart();
                }
                break;
            case KeyEvent.VK_Z:
                historyManager.undoLastAction();
                break;
            case KeyEvent.VK_N:
                if (e.isControlDown()) {
                    createNewFlowchart();
                }
                break;
            case KeyEvent.VK_C:
                if (e.isControlDown()) {
                    copySelected();
                }
                break;
            case KeyEvent.VK_X:
                if (e.isControlDown()) {
                    cutSelected();
                }
                break;
            case KeyEvent.VK_V:
                if (e.isControlDown()) {
                    pasteClipboard();
                }
                break;
            case KeyEvent.VK_TAB:
                selectConnectedNode(e.isShiftDown());
                break;
            case KeyEvent.VK_A:
                if (e.isControlDown()) {
                    selectAll();
                }
                window.redrawView();
                break;
            case KeyEvent.VK_S:
                if (e.isControlDown() && e.isShiftDown()) {
                    window.saveAsFlowchart();
                } else if (e.isControlDown()) {
                    window.saveFlowchart();
                }
                break;
            case KeyEvent.VK_O:
                if (e.isControlDown()) {
                    window.openFlowchart();
                }
                break;
            case KeyEvent.VK_I:
                if (e.isControlDown()) {
                    window.exportToImage();
                }
            case KeyEvent.VK_F:
                if (e.isControlDown()) {
                    fitFlowchartToView();
                }
                break;
            case KeyEvent.VK_R:
                if (e.isControlDown()) {
                    resetView();
                }
            case KeyEvent.VK_H:
                if (e.isControlDown()) {
                    createNode();
                }
            case KeyEvent.VK_Q:
                if (e.isControlDown()) {
                    window.closeWindow();
                }
            case KeyEvent.VK_LEFT:
                textTypeManager.decrementLocationOfTypeCursor();
                window.redrawView();
                break;
            case KeyEvent.VK_RIGHT:
                textTypeManager.incrementLocationOfTypeCursor();
                window.redrawView();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_SPACE:
                spacebar = false;
                break;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point.Float mousePosition = window.getCamera().convertCanvasToWorld(e.getPoint());

        Selectable clickedOnThing = selectionManager.getSelectableUnderPoint(mousePosition);

        Node node = null;
        NodeLine nodeLine = null;
        if (clickedOnThing instanceof Node) {
            node = (Node) clickedOnThing;
        } else if (clickedOnThing instanceof NodeLine) {
            nodeLine = (NodeLine) clickedOnThing;
        }
        //Is this is resize operation? (Resizing need not have a nearby item
        Node resizing = null;
        if (e.getButton() == MouseEvent.BUTTON1 && (resizing = findNodeCloseEnoughForResize(mousePosition.x, mousePosition.y)) != null) {
            //Resizing "resizing"
            dragManager.setLeftMouseDown(true);
            dragManager.setDragging(true);
            if (canResizeLeft(mousePosition.x, mousePosition.y, resizing)) {
                dragManager.startDragResizeLeft(resizing);
                window.resizeLeftCursor();
            } else if (canResizeTop(mousePosition.x, mousePosition.y, resizing)) {
                dragManager.startDragResizeTop(resizing);
                window.resizeTopCursor();
            } else if (canResizeBottom(mousePosition.x, mousePosition.y, resizing)) {
                dragManager.startDragResizeBottom(resizing);
                window.resizeBottomCursor();
            } else if (canResizeRight(mousePosition.x, mousePosition.y, resizing)) {
                dragManager.startDragResizeRight(resizing);
                window.resizeRightCursor();
            }
            historyManager.saveDimension(new Dimension((int)resizing.getWidth(), (int)resizing.getHeight()));
            historyManager.savePoint(new Point((int)resizing.getX(), (int)resizing.getY()));
            return;
        }
        // actions involving a node
        if (node != null) {
            if (!dragManager.isDragging()) { // not yet dragging anything...
                dragManager.setDragging(true); // start dragging
                dragManager.setInitialX((int) mousePosition.getX());
                dragManager.setInitialY((int) mousePosition.getY());
                dragManager.setOffsetX(0); // initially
                dragManager.setOffsetY(0);
                if (e.getButton() == MouseEvent.BUTTON3) {
                    Node createdNode = (Node) node.clone();
                    if (!e.isControlDown()) {
                        NodeStyle noStyle = new NodeStyle();
                        createdNode.setWidth(1);
                        createdNode.setHeight(1);
                        noStyle.setShape(Shape.NONE);
                        createdNode.setStyle(noStyle);
                    }
                    createdNode.setX(dragManager.getInitialX());
                    createdNode.setY(dragManager.getInitialY());
                    createdNode.getLinesConnected().clear();
                    window.getFlowchart().getNodes().add(createdNode);
                    // connect the two nodes with a line
                    NodeLine line = new NodeLine(node, createdNode);
                    createdNode.getLinesConnected().add(line);
                    node.getLinesConnected().add(line);
                    createdNode.setInnerText("");
                    dragManager.setNewlyMadeNode(createdNode);
                    dragManager.setRightMouseDown(true);
                    window.redrawView();
                }
            }

            if (e.getButton() == MouseEvent.BUTTON1) {
                if (!selectionManager.getSelected().contains(clickedOnThing)) {
                    if (!e.isShiftDown()) {
                        selectionManager.clearSelection();
                    }
                    selectionManager.select(clickedOnThing);
                    //record that this was added to the selection
                    historyManager.addToHistory(new Selected(clickedOnThing));
                }
                
                if (selectionManager.getSelectedNodes().contains(node)) {
                    // if shift was not held down, only select and transform the clicked on node
                    if (!e.isShiftDown()) {
                        selectionManager.clearSelection();
                        selectionManager.select(node);
                    }
                    dragManager.startDragMove(mousePosition.x, mousePosition.y);
                    dragManager.setLeftMouseDown(true);
                } else {
                    dragManager.endDragMove();
                }
            }
        } else if (nodeLine != null) { // actions only for node lines
            if (!selectionManager.getSelected().contains(clickedOnThing)) {
                if (!e.isShiftDown()) {
                    selectionManager.clearSelection();
                }
                selectionManager.select(clickedOnThing);
                // record that it was added to the selection
                historyManager.addToHistory(new Selected(clickedOnThing));
            }
        }

        if (node == null && nodeLine == null && e.getButton() == MouseEvent.BUTTON1) {
            // clear selection if you don't click anything
            if (!e.isShiftDown()) {
                ArrayList<Selectable> deselectedThings = new ArrayList<>();
                deselectedThings.addAll(selectionManager.getSelected());
                //track how many things were deselected only if something was deselected
                if (!deselectedThings.isEmpty()) {
                    historyManager.addToHistory(new Deselected(deselectedThings));
                }
                selectionManager.deselect(deselectedThings);
            }

            // you only start dragging a box when the spacebar is not held down
            // begin dragging a box for selection in a box
            if (!spacebar && !dragManager.isDragging()) {
                dragManager.setInitialX((int) mousePosition.getX());
                dragManager.setInitialY((int) mousePosition.getY());
                dragManager.setOffsetX(0);
                dragManager.setOffsetY(0);
                dragManager.setBoxSelecting(true);
                dragManager.setDragging(true);
                window.getCamera().lock();
            }
        }
        
        // move the cursor that's editing to the end of the text
        if (selectionManager.getLastSelected() != null) {
            if (selectionManager.getLastSelected() instanceof Node) {
                textTypeManager.setLocationOfTypeCursor(((Node)selectionManager.getLastSelected()).getInnerText().length());
            } else if (selectionManager.getLastSelected() instanceof NodeLine) {
                switch (textTypeManager.getLinePartTyping()) {
                    case 0: 
                        textTypeManager.setLocationOfTypeCursor(((NodeLine)selectionManager.getLastSelected()).getCenterText().length());
                        break;
                    case 1:
                        textTypeManager.setLocationOfTypeCursor(((NodeLine)selectionManager.getLastSelected()).getTailText().length());
                        break;
                    case 2:
                        textTypeManager.setLocationOfTypeCursor(((NodeLine)selectionManager.getLastSelected()).getHeadText().length());
                        break;
                }
            }
        }
        
        window.redrawView();
    }

    /**
     * Checks to see if the mouse is in the Y region of a shape to resize it.
     * For right / left resize check functions.
     *
     * @param my the y position of the mouse in world coordinates.
     * @param node the node to check against.
     * @return if the X position is ok for right/left resize
     */
    private boolean inYRegionResize(float my, Node node) {
        return my > node.getY() - DRAG_RESIZE_DISTANCE && my < node.getY() + node.getHeight() + DRAG_RESIZE_DISTANCE;
    }

    /**
     * Checks to see if the mouse is in the X region of a shape to resize it.
     * For top / bottom resize check functions.
     *
     * @param mx the x position of the mouse in world coordinates.
     * @param node the node to check against.
     * @return if the X position is ok for top/bottom resize
     */
    private boolean inXRegionResize(float mx, Node node) {
        return mx > node.getX() - DRAG_RESIZE_DISTANCE && mx < node.getX() + node.getWidth() + DRAG_RESIZE_DISTANCE;
    }

    /**
     * Checks to see if the mouse location is close enough to the top edge of a
     * node to resize it.
     *
     * @param mx the x position of the mouse in world coordinates.
     * @param my the y position of the mouse in world coordinates.
     * @param node the node to check against.
     * @return if the node is able to be resized on the top edge
     */
    private boolean canResizeTop(float mx, float my, Node node) {
        return inXRegionResize(mx, node) && my > node.getY() - DRAG_RESIZE_DISTANCE && my < node.getY() + DRAG_RESIZE_DISTANCE;
    }

    /**
     * Checks to see if the mouse location is close enough to the bottom edge of
     * a node to resize it.
     *
     * @param mx the x position of the mouse in world coordinates.
     * @param my the y position of the mouse in world coordinates.
     * @param node the node to check against.
     * @return if the node is able to be resized on the bottom edge
     */
    private boolean canResizeBottom(float mx, float my, Node node) {
        return inXRegionResize(mx, node) && my > node.getY() + node.getHeight() - DRAG_RESIZE_DISTANCE && my < node.getY() + node.getHeight() + DRAG_RESIZE_DISTANCE;
    }

    /**
     * Checks to see if the mouse location is close enough to the left edge of a
     * node to resize it.
     *
     * @param mx the x position of the mouse in world coordinates.
     * @param my the y position of the mouse in world coordinates.
     * @param node the node to check against.
     * @return if the node is able to be resized on the left edge
     */
    private boolean canResizeLeft(float mx, float my, Node node) {
        return inYRegionResize(my, node) && mx > node.getX() - DRAG_RESIZE_DISTANCE && mx < node.getX() + DRAG_RESIZE_DISTANCE;
    }

    /**
     * Checks to see if the mouse location is close enough to the right edge of
     * a node to resize it.
     *
     * @param mx the x position of the mouse in world coordinates.
     * @param my the y position of the mouse in world coordinates.
     * @param node the node to check against.
     * @return if the node is able to be resized on the right edge
     */
    private boolean canResizeRight(float mx, float my, Node node) {
        return inYRegionResize(my, node) && mx > node.getX() + node.getWidth() - DRAG_RESIZE_DISTANCE && mx < node.getX() + node.getWidth() + DRAG_RESIZE_DISTANCE;
    }

    /**
     * Checks to see if the mouse location is close enough to the edge of a node
     * for a resize operation.
     *
     * @param mx the x position of the mouse in world coordinates.
     * @param my the y position of the mouse in world coordinates.
     * @param node the node to check against.
     * @return if the node is able to be resized on any edge
     */
    private boolean closeEnoughForResize(float mx, float my, Node node) {
        return canResizeLeft(mx, my, node)
                || canResizeTop(mx, my, node)
                || canResizeRight(mx, my, node)
                || canResizeBottom(mx, my, node);
    }

    /**
     * Searches through all nodes and finds one that the mouse is close enough
     * to do a resize operation on.
     *
     * @param mx the x position of the mouse in world coordinates.
     * @param my the y position of the mouse in world coordinates.
     * @return the first found node that is able to be resized by the mouse's
     * current position.
     */
    private Node findNodeCloseEnoughForResize(float mx, float my) {
        for (Node checking : window.getFlowchart().getNodes()) {
            if (closeEnoughForResize(mx, my, checking)) {
                return checking;
            }
        }
        return null;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point.Float mousePosition = window.getCamera().convertCanvasToWorld(e.getPoint());

        if (dragManager.isDragging()) {
            if (e.getButton() == MouseEvent.BUTTON3 && dragManager.getNewlyMadeNode() != null) {
                dragManager.setRightMouseDown(false);
                if (dragManager.getNewlyMadeNode().getStyle().getShape() == Shape.NONE)  {
                    Node guideNode = dragManager.getNewlyMadeNode();
                    NodeLine connection = guideNode.getLinesConnected().get(0);
                    // remove the guide node
                    window.getFlowchart().getNodes().remove(guideNode);
                    Selectable select = selectionManager.getSelectableUnderPoint(mousePosition);
                    if (select != null && select != connection.getParent() && select instanceof Node) {
                        connection.setChild((Node)select);
                        ((Node)select).getLinesConnected().add(connection);
                        // record that a connection was mad
                        historyManager.addToHistory(new ConnectedNodes(connection));
                    } else {
                        connection.getParent().getLinesConnected().remove(connection);
                    }
                } else {
                    historyManager.addToHistory(new CreatedConnectedNode(
                            dragManager.getNewlyMadeNode(), 
                            dragManager.getNewlyMadeNode().getLinesConnected().get(0)));
                }
                dragManager.setNewlyMadeNode(null);
                dragManager.setDragging(false); // done dragging the newly created node
            }
            if (dragManager.isDragResizing()) {
                // record the scale to history only if it changed
                if ((int)dragManager.getResizingNode().getWidth() != (int)historyManager.getSavedDimension().getWidth() ||
                        (int)dragManager.getResizingNode().getHeight() != (int)historyManager.getSavedDimension().getHeight()) {
                    historyManager.addToHistory(new ScaledNode(dragManager.getResizingNode(), 
                            historyManager.getSavedDimension(), historyManager.getSavedPoint()));
                }
                dragManager.endDragResize();
                window.defaultCursor();
            }
            if (dragManager.isDragMoving()) {
                // record the moving of nodes to history if it did move
                if (dragManager.getOffsetX() != 0 || dragManager.getOffsetY() != 0) {
                    historyManager.addToHistory(new MovedNodes(
                            dragManager.getMoveDraggingNodesArray(), 
                            dragManager.getMoveDraggingOffsetsArray(), 
                            dragManager.getInitialX(), 
                            dragManager.getInitialY()));
                }
                dragManager.endDragMove();
            }
        }
        if (e.getButton() == MouseEvent.BUTTON1 && dragManager.isBoxSelecting()) {
            // Find everything that was selected using the box select
            ArrayList<Selectable> selectedItems = selectionManager.getSelectablesUnderBox(
                    new Point((int) dragManager.getInitialX(), (int) dragManager.getInitialY()),
                    dragManager.getOffsetX(), dragManager.getOffsetY());
            // clear selection
            if (!e.isShiftDown()) {
                selectionManager.clearSelection();
            }
            // make a list of all the things newly selected
            ArrayList<Selectable> addedToSelection = new ArrayList<>();
            for (int i = 0; i < selectedItems.size(); i++) {
                if (!selectionManager.getSelected().contains(selectedItems.get(i))) {
                    addedToSelection.add(selectedItems.get(i));
                }
            }
            
            selectionManager.select(addedToSelection);
            
            // record the newly selected items only if there was anything newly selected
            if (!addedToSelection.isEmpty()) {
                historyManager.addToHistory(new Selected(addedToSelection));
            }
            
            dragManager.setBoxSelecting(false);
            dragManager.setDragging(false);
            dragManager.setLeftMouseDown(false);
            window.getCamera().unlock();
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
            dragManager.setOffsetX((int) mousePos.getX() - dragManager.getInitialX());
            dragManager.setOffsetY((int) mousePos.getY() - dragManager.getInitialY());

            if (dragManager.isRightMouseDown() && dragManager.getNewlyMadeNode() != null) {
                dragManager.getNewlyMadeNode().setX((int) mousePos.getX());
                dragManager.getNewlyMadeNode().setY((int) mousePos.getY());
            } else if (dragManager.isLeftMouseDown()) {
                if (dragManager.isDragMoving()) {
                    dragManager.updateDragMove(mousePos.x, mousePos.y);
                } else if (dragManager.isDragResizing()) {
                    dragManager.updateDragResize(mousePos.x, mousePos.y);
                }
            }
        }

        window.redrawView();
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    /**
     * Get the drag manager used by this event manager
     * @return the drag manager used by this event manager
     */
    public DragManager getDragManager() {
        return dragManager;
    }

    @Override
    public void booleanSwitch(boolean value) {
        textTypeManager.setCursorShowing(value);
        window.redrawView();
    }
    
    /**
     * Get the text type manager used by this event manager
     * @return the text type manager used by this event manager
     */
    public TextTypeManager getTextTypeManager() {
        return textTypeManager;
    }

    /**
     * Get the history manager used by this event manager
     * @return the history manager used by this event manager
     */
    public HistoryManager getHistoryManager() {
        return historyManager;
    }
    
}
