/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy;

import bropals.flowy.data.Node;
import bropals.flowy.data.NodeLine;
import bropals.flowy.data.Selectable;
import bropals.flowy.style.NodeStyle;
import bropals.flowy.style.Shape;
import bropals.flowy.util.BooleanBlinkListener;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

/**
 * Gets events from the view and StyleManager and handles them.
 *
 * @author Jonathon
 */
public class EventManager implements KeyListener, MouseListener, MouseMotionListener, BooleanBlinkListener {

    public static String newLineSequence = "`";
    private SelectionManager selectionManager;
    private FlowchartWindow window;
    /**
     *
     * NOTE: Make the camera not able to be transformed when dragging to the
     * drag offsets work
     *
     */
    private DragManager dragManager;
    /**
     * The variable to track if the spacebar is held down or not
     */
    private boolean spacebar;

    /**
     * Keep track of where on the line you're adding text (tail, head, or
     * center) 0 = center, 1 = tail, and 2 = head, It will rotate between these three
     * in the order 0 -> 1 -> 2 -> 0 ...
     */
    private int linePartTyping;
    /**
     * The location of the cursor on the string. The cursor is located behind the 
     * character whose index is this number. This number is 0 for the start of 
     * the string, and is equal to str.length() for a String str when it's at
     * the end of the word.
     * 
     * For a string "word" and the location of "1" the cursor is positioned as
     * "w|ord"
     */
    private int locationOfTypeCursor;
    private boolean showCursor;

    /**
     * How close the mouse has to be to an edge of a node to be able to resize
     * it, in world coordinates.
     */
    public final static float DRAG_RESIZE_DISTANCE = 6.0f;

    public EventManager(FlowchartWindow instance) {
        window = instance;
        selectionManager = new SelectionManager(instance);
        dragManager = new DragManager(selectionManager);
        showCursor = false;
        linePartTyping = 0;
        locationOfTypeCursor = 0;
    }

    public boolean isSelected(Selectable s) {
        return selectionManager.getSelected().contains(s);
    }
    
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    /**
     * Find the selectable selected
     *
     * @param p A point that is in world units
     * @return The thing that is closest under the mouse, or null if nothing was
     * close.
     */
    private Selectable getSelectableUnderPoint(Point.Float p) {
        Selectable thing = null; // initially nothing
        for (Node n : window.getFlowchart().getNodes()) {
            if (p.getX() > n.getX() && p.getX() < n.getX() + n.getWidth()
                    && p.getY() > n.getY() && p.getY() < n.getY() + n.getHeight()) {
                return n;
            }
        }

        // it must be a minimum of 10 pixels away
        double nearestDistance = 10 * (window.getCamera().getZoom());
        // if no nodes were found, find the nearest line.
        for (NodeLine nl : window.getFlowchart().getNodeLines()) {
            // drag so we can get the points of the line
            Point[] linePoints = nl.getStyle().getType().renderLine(nl, window.getCamera(), 
                    window.getView().getGraphics(), 
                    isCursorShowing() && nl == selectionManager.getLastSelected(), 
                    locationOfTypeCursor, linePartTyping);
            Point.Float p1 = window.getCamera().convertCanvasToWorld(linePoints[0]);
            Point.Float p2 = window.getCamera().convertCanvasToWorld(linePoints[1]);

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
            if (p.getX() < n.getX() && p.getX() + width > n.getX() + n.getWidth()
                    && p.getY() < n.getY() && p.getY() + height > n.getY() + n.getHeight()) {
                list.add(n);
            }
        }
        for (NodeLine nl : window.getFlowchart().getNodeLines()) {
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
    private void removeSelectables(ArrayList<Selectable> stuff) {
        for (Selectable s : stuff) {
            if (s instanceof Node) {
                for (int i=0; i<window.getFlowchart().getNodes().size(); i++) {

                    if (window.getFlowchart().getNodes().get(i) == s) {
                        Node removedNode = window.getFlowchart().getNodes().get(i);
                        window.getFlowchart().getNodes().remove(i);
                        // look through the other nodes...
                        for (Node other : window.getFlowchart().getNodes()) {
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
                for (int i=0; i<window.getFlowchart().getNodes().size(); i++) {
                    for (int l=0; l<window.getFlowchart().getNodes().get(i).getLinesConnected().size(); l++) {
                        if (((NodeLine)s) == window.getFlowchart().getNodes().get(i).getLinesConnected().get(l)) {
                            window.getFlowchart().getNodes().get(i).getLinesConnected().remove(l);
                        }
                    }
                }
            }
        }
    }

    /**
     * ***** Methods for all the actions (as a level of indirection) ******
     */
    /**
     * Delete all the selected nodes and deselect
     */
    public void deleteSelected() {
        removeSelectables(selectionManager.getSelected());
        selectionManager.clearSelection();
        window.redrawView();
    }

    /**
     * Make a new flowchart window
     */
    public void createNewFlowchart() {
        System.out.println("Creating a flowchart");
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
            dragManager.setStuffInClipboard(selectionManager.getSelected());
            removeSelectables(selectionManager.getSelected());
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
                int parentIndex = i;
                // copy the array of the original lines from the original array
                ArrayList<NodeLine> linesConnected = ((Node)(dragManager.getClipboard().get(i))).getLinesConnected();
                for (int l=0; l<linesConnected.size(); l++) {
                    int childIndex = -1;
                    // find the location of the child in the original array for the original line
                    for (int n=0; n<dragManager.getClipboard().size(); n++) {
                        if (n != parentIndex && dragManager.getClipboard().get(n) == linesConnected.get(l).getChild()) {
                            childIndex = n;
                            break;
                        }
                    }
                    
                    // if the child was found in the original array then add the new 
                    //  line using the same (copy) of the parent and childs
                    if (childIndex != -1) {
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
            
            System.out.println("Pasted " + pastedNodes.size() + " nodes");
        }
    }
    
    /**
     * Select everything in the flowchart
     */
    public void selectAll() {
        selectionManager.clearSelection();
        for (int i = 0; i < window.getFlowchart().getNodes().size(); i++) {
            if (!selectionManager.getSelected().contains(window.getFlowchart().getNodes().get(i))) {
                selectionManager.select(window.getFlowchart().getNodes().get(i));
            }
        }
        for (int i = 0; i < window.getFlowchart().getNodeLines().size(); i++) {
            selectionManager.select(window.getFlowchart().getNodeLines().get(i));
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
                    //System.out.println("selecting next node");
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
                //System.out.println("New node " + createdNode + " from the node " + selectedNode);
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
    
    @Override
    public void keyTyped(KeyEvent e) {
        //System.out.println("Typed a character: " + e.getKeyChar());

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
                // make sure locationOfTypeCursor is in the bounds of the text
                
                if (locationOfTypeCursor > editNode.getInnerText().length() || locationOfTypeCursor < 0)
                    locationOfTypeCursor = editNode.getInnerText().length();

                if (((int) e.getKeyChar()) == KeyEvent.VK_BACK_SPACE && editNode.getInnerText().length() > 0) {
                    // take off the character at the location of the string
                    editNode.setInnerText(deleteCharacter(editNode.getInnerText(), locationOfTypeCursor));
                    if (locationOfTypeCursor > 0) {
                        locationOfTypeCursor--;
                    }
                } else if (((int) e.getKeyChar()) == KeyEvent.VK_ENTER) {
                    // add a new line
                    /*System.out.println("insert a new line");
                    editNode.setInnerText(insertCharacter(editNode.getInnerText(), 
                            " " + newLineSequence + " ", locationOfTypeCursor));
                    locationOfTypeCursor +=2;
                    */
                } else if (((int) e.getKeyChar()) != KeyEvent.VK_BACK_SPACE) {
                    // add the typed character to the end
                    editNode.setInnerText(insertCharacter(editNode.getInnerText(), 
                            "" + e.getKeyChar(), locationOfTypeCursor));
                    locationOfTypeCursor++;
                }
                
            // if you're selecting a node line...
            } else if (selectionManager.getLastSelected() != null
                    && selectionManager.getLastSelected() instanceof NodeLine) {
                NodeLine editLine = (NodeLine) selectionManager.getLastSelected();

                int lengthOfEditingLine = 0;
                switch (linePartTyping) {
                    case 0:
                        lengthOfEditingLine = editLine.getCenterText().length();
                        break;
                    case 1:
                        lengthOfEditingLine = editLine.getTailText().length();
                        break;
                    case 2:
                        lengthOfEditingLine = editLine.getHeadText().length();
                        break;
                }

                // move the cursor to the end of the newly selected line
                if (locationOfTypeCursor > lengthOfEditingLine || locationOfTypeCursor < 0)
                    locationOfTypeCursor = lengthOfEditingLine;
                
                if (((int) e.getKeyChar()) == KeyEvent.VK_BACK_SPACE && lengthOfEditingLine > 0) {
                    // take off the last character in the string
                    switch (linePartTyping) {
                        case 0:
                            editLine.setCenterText(deleteCharacter(editLine.getCenterText(), locationOfTypeCursor));
                            break;
                        case 1:
                            editLine.setTailText(deleteCharacter(editLine.getTailText(), locationOfTypeCursor));
                            break;
                        case 2:
                            editLine.setHeadText(deleteCharacter(editLine.getHeadText(), locationOfTypeCursor));
                            break;
                    }
                    locationOfTypeCursor--;

                } else if (((int) e.getKeyChar()) == KeyEvent.VK_ENTER) {
                    // cycle what part of the line that is being edited
                    if (linePartTyping < 2) {
                        linePartTyping++;
                    } else {
                        linePartTyping = 0;
                    }
                    switch (linePartTyping) {
                        case 0:
                            locationOfTypeCursor = editLine.getCenterText().length();
                            break;
                        case 1:
                            locationOfTypeCursor = editLine.getTailText().length();
                            break;
                        case 2:
                            locationOfTypeCursor = editLine.getHeadText().length();
                            break;
                    }
                } else if (((int) e.getKeyChar()) != KeyEvent.VK_BACK_SPACE) {
                    // add the typed character to the end
                    switch (linePartTyping) {
                        case 0:
                            editLine.setCenterText(
                                    insertCharacter(editLine.getCenterText(), 
                                        "" + e.getKeyChar(), locationOfTypeCursor));
                            break;
                        case 1:
                            editLine.setTailText(
                                    insertCharacter(editLine.getTailText(), 
                                        "" + e.getKeyChar(), locationOfTypeCursor));
                            break;
                        case 2:
                            editLine.setHeadText(
                                    insertCharacter(editLine.getHeadText(), 
                                        "" + e.getKeyChar(), locationOfTypeCursor));
                            break;
                    }
                    locationOfTypeCursor++;
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
            case KeyEvent.VK_LEFT:
                locationOfTypeCursor--;
                window.redrawView();
                break;
            case KeyEvent.VK_RIGHT:
                locationOfTypeCursor++;
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

        Selectable clickedOnThing = getSelectableUnderPoint(mousePosition);

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
            return;
        }
        //System.out.println(node);
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
                    dragManager.setNewlyMadeNode(createdNode);
                    dragManager.setRightMouseDown(true);
                    //System.out.println("New node " + createdNode + " from the node " + node);
                    window.redrawView();
                }
            }

            if (e.getButton() == MouseEvent.BUTTON1) {
                if (!e.isShiftDown()) {
                    selectionManager.clearSelection();
                }

                if (!selectionManager.getSelected().contains(clickedOnThing)) {
                    selectionManager.select(clickedOnThing);
                }
                if (selectionManager.getSelectedNodes().contains(node)) {
                    dragManager.startDragMove(mousePosition.x, mousePosition.y);
                    dragManager.setLeftMouseDown(true);
                } else {
                    dragManager.endDragMove();
                }
            }
        } else if (nodeLine != null) { // actions only for node lines
            if (!e.isShiftDown()) {
                selectionManager.clearSelection();
            }
            if (!selectionManager.getSelected().contains(clickedOnThing)) {
                selectionManager.select(clickedOnThing);
            }
        }

        if (node == null && nodeLine == null && e.getButton() == MouseEvent.BUTTON1) {
            // clear selection if you don't click anything
            if (!e.isShiftDown()) {
                selectionManager.clearSelection();
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
                locationOfTypeCursor = ((Node)selectionManager.getLastSelected()).getInnerText().length();
            } else if (selectionManager.getLastSelected() instanceof NodeLine) {
                switch (linePartTyping) {
                    case 0: 
                        locationOfTypeCursor = ((NodeLine)selectionManager.getLastSelected()).getCenterText().length();
                        break;
                    case 1:
                        locationOfTypeCursor = ((NodeLine)selectionManager.getLastSelected()).getTailText().length();
                        break;
                    case 2:
                        locationOfTypeCursor = ((NodeLine)selectionManager.getLastSelected()).getHeadText().length();
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
                    window.getFlowchart().getNodes().remove(guideNode);
                    Selectable select = getSelectableUnderPoint(mousePosition);
                    if (select != null && select != connection.getParent() && select instanceof Node) {
                        connection.setChild((Node)select);
                        ((Node)select).getLinesConnected().add(connection);
                    } else {
                        connection.getParent().getLinesConnected().remove(connection);
                    }
                }
                dragManager.setNewlyMadeNode(null);
                dragManager.setDragging(false); // done dragging the newly created node
            }
            if (dragManager.isDragResizing()) {
                dragManager.endDragResize();
                window.defaultCursor();
            }
            if (dragManager.isDragMoving()) {
                dragManager.endDragMove();
            }
        }
        if (e.getButton() == MouseEvent.BUTTON1 && dragManager.isBoxSelecting()) {
            // Find everything that was selected using the box select
            ArrayList<Selectable> selectedItems = getSelectablesUnderBox(
                    new Point((int) dragManager.getInitialX(), (int) dragManager.getInitialY()),
                    dragManager.getOffsetX(), dragManager.getOffsetY());
            // clear selection
            if (!e.isShiftDown()) {
                selectionManager.clearSelection();
            }
            for (int i = 0; i < selectedItems.size(); i++) {
                if (!selectionManager.getSelected().contains(selectedItems.get(i))) {
                    selectionManager.select(selectedItems.get(i));
                }
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

    public DragManager getDragManager() {
        return dragManager;
    }

    @Override
    public void booleanSwitch(boolean value) {
        showCursor = value;
        window.redrawView();
    }
    
    public boolean isCursorShowing() {
        return showCursor;
    }
    
    public int getCursorLocation() {
        return locationOfTypeCursor;
    }

    public int getLinePartTyping() {
        return linePartTyping;
    }

}
