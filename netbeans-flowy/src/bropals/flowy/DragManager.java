/*
 * Flowy is a simple flowchart making software.
 * Copyright (C) 2015  Jonathon Prehn, Kevin Prehn
 * 
 * This file is a part of Flowy.
 *
 * Flowy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Flowy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Flowy.  If not, see <http://www.gnu.org/licenses/>.
 */
package bropals.flowy;

import bropals.flowy.data.Node;
import bropals.flowy.data.Selectable;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Manages the drag-move operation.
 * @author Jonathon
 */
public class DragManager {
    /**
     * The selection manager for the current FlowchartWindow.
     */
    private final SelectionManager selectionManager;
    /**
     * The selectables that are currently on the clipboard.
     */
    private ArrayList<Selectable> clipboard;
    /**
     * Indicates if the user is performing a drag operation.
     */
    private boolean dragging;
    /**
     * Indicates if the user is performing a box-select.
     */
    private boolean boxSelecting;
    /**
     * Indicates if the user's left mouse button is down.
     */
    private boolean leftMouseDown;
    /**
     * Indicates if the user's right mouse button is down.
     */
    private boolean rightMouseDown;
    /**
     * Indicates if the user is resizing the left edge of a node.
     */
    private boolean resizingLeft;
    /**
     * Indicates if the user is resizing the right edge of a node.
     */
    private boolean resizingRight;
    /**
     * Indicates if the user is resizing the top edge of a node.
     */
    private boolean resizingTop;
    /**
     * Indicates if the user is resizing the bottom edge of a node.
     */
    private boolean resizingBottom;
    /**
     * A stored X offset value in world coordinates.
     */
    private float offsetX;
    /**
     * A stored Y offset value in world coordinates.
     */
    private float offsetY;
    /**
     * The initial X position of the user's mouse when a drag
     * operation started, in world coordinates.
     */
    private float initialX;
    /**
     * The initial Y position of the user's mouse when a drag
     * operation started, in world coordinates.
     */
    private float initialY;
    /**
     * The node that was most recently created for a create-node-drag operation.
     */
    private Node newlyMadeNode;
    /**
     * The node that is currently being resized.
     */
    private Node resizing;
    /**
     * The array of nodes that are currently being drag-moved.
     */
    private Node[] moveDragging;
    /**
     * The array of stored offsets from the mouse position for
     * each node currently being drag-moved, in world coordinates.
     */
    private float[][] moveDragOffsets;
    
    /**
     * Creates a drag manager for the purpose of keeping track of
     * the drag logic.
     * @param manager the selection manager, which DragManager needs
     * a reference to.
     */
    public DragManager(SelectionManager manager) {
        dragging = false;
        offsetX = 0;
        offsetY = 0;
        initialX = 0;
        initialY = 0;
        newlyMadeNode = null;
        boxSelecting = false;
        moveDragging = null;
        selectionManager = manager;
        leftMouseDown = false;
        rightMouseDown = false;
        clipboard = new ArrayList<>();
    }

    /**
     * Sets the selectables in the clipboard.
     * @param selectables the selectables to occupy the clipboard
     */
    public void setStuffInClipboard(ArrayList<Selectable> selectables) {
        clipboard.clear();
        clipboard.addAll(selectables);
    }
    
    /**
     * Gets the selectables currently in the clipboard.
     * @return the selectables currently in the clipboard.
     */
    public ArrayList<Selectable> getClipboard() {
        return clipboard;
    }
    
    /**
     * Set the box-selecting flag in this drag manager.
     * @param boxSelecting the new state of the box-select flag.
     */
    public void setBoxSelecting(boolean boxSelecting) {
        this.boxSelecting = boxSelecting;
    }

    /**
     * Gets the value of the box-select flag.
     * @return the value of the box-select flag.
     */
    public boolean isBoxSelecting() {
        return boxSelecting;
    }
    
    /**
     * Gets the value of the drag flag.
     * @return the value of the drag flag.
     */
    public boolean isDragging() {
        return dragging;
    }

    /**
     * Sets the value of the drag flag.
     * @param dragging the new value of the drag flag.
     */
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    /**
     * Gets the stored X offset, in world coordinates.
     * @return the stored X offset, in world coordinates.
     */
    public float getOffsetX() {
        return offsetX;
    }

    /**
     * Sets the stored X offset, in world coordinates.
     * @param offsetX the new stored X offset.
     */
    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    /**
     * Gets the stored Y offset, in world coordinates.
     * @return the stored Y offset, in world coordinates.
     */
    public float getOffsetY() {
        return offsetY;
    }

    /**
     * Sets the stored Y offset, in world coordinates.
     * @param offsetY the new stored Y offset.
     */
    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    /**
     * Gets the X position of the user's mouse when the current
     * drag operation began.
     * @return the X position of the user's mouse when the
     * current drag operation began.
     */
    public float getInitialX() {
        return initialX;
    }

    /**
     * Sets the X position of the user's mouse for the drag operation
     * that is to begin.
     * @param initialX the X position of the user's mouse for the drag operation
     * that is to begin.
     */
    public void setInitialX(float initialX) {
        this.initialX = initialX;
    }

    /**
     * Gets the Y position of the user's mouse when the current
     * drag operation began.
     * @return the Y position of the user's mouse when the
     * current drag operation began.
     */
    public float getInitialY() {
        return initialY;
    }

    /**
     * Sets the Y position of the user's mouse for the drag operation
     * that is to begin.
     * @param initialY the Y position of the user's mouse for the drag operation
     * that is to begin.
     */
    public void setInitialY(float initialY) {
        this.initialY = initialY;
    }

    /**
     * Get the left mouse down flag.
     * @return the left mouse down flag.
     */
    public boolean isLeftMouseDown() {
        return leftMouseDown;
    }

    /**
     * Set the left mouse down flag.
     * @param leftMouseDown the left mouse down flag.
     */
    public void setLeftMouseDown(boolean leftMouseDown) {
        this.leftMouseDown = leftMouseDown;
    }

    public boolean isRightMouseDown() {
        return rightMouseDown;
    }

    public void setRightMouseDown(boolean rightMouseDown) {
        this.rightMouseDown = rightMouseDown;
    }
    
    public Node getNewlyMadeNode() {
        return newlyMadeNode;
    }

    public void setNewlyMadeNode(Node newlyMadeNode) {
        this.newlyMadeNode = newlyMadeNode;
    }
    
    public void startDragResizeTop(Node resizing) {
        resizingTop = true;
        this.resizing = resizing;
    }
    
    public void startDragResizeBottom(Node resizing) {
        resizingBottom = true;
        this.resizing = resizing;
    }
    
    public void startDragResizeLeft(Node resizing) {
        resizingLeft = true;
        this.resizing = resizing;
    }
    
    public void startDragResizeRight(Node resizing) {
        resizingRight = true;
        this.resizing = resizing;
    }
    
    /**
     * Return the node that is being resized, if there is one.
     * @return the node being resized. If there are no nodes being resized, it returns null.
     */
    public Node getResizingNode() {
        return resizing;
    }
    
    public boolean isDragResizing() {
        return (resizingRight || resizingLeft || resizingTop || resizingBottom) && dragging;
    }
    
    /**
     * Start move dragging. It will move every node that is selected.
     * @param mouseX the current mouse X position in world coordinates.
     * @param mouseY the current mouse Y position in world coordinates.
     */
    public void startDragMove(float mouseX, float mouseY) {
        moveDragging = (Node[])selectionManager.getSelectedNodes().toArray(
                new Node[0]);
        
        initialX = mouseX;
        initialY = mouseY;
        offsetX = 0;
        offsetY = 0;
        moveDragOffsets = new float[moveDragging.length][2];
        for (int i=0; i<moveDragging.length; i++) {
            moveDragOffsets[i][0] = moveDragging[i].getX() - mouseX;
            moveDragOffsets[i][1] = moveDragging[i].getY() - mouseY;
        }
        dragging = true;
    }
    
    public boolean isDragMoving() {
        return moveDragging != null;
    }
    
    public void updateDragMove(float mouseX, float mouseY) {
        for (int i=0; i<moveDragging.length; i++) {
            moveDragging[i].setX(initialX+offsetX+moveDragOffsets[i][0]);
            moveDragging[i].setY(initialY+offsetY+moveDragOffsets[i][1]);
        }
    }
    
    
    public Node[] getMoveDraggingNodesArray() {
        return moveDragging;
    }
    
    public float[][] getMoveDraggingOffsetsArray() {
        return moveDragOffsets;
    }
    
    public void updateDragResize(float mouseX, float mouseY) {
        if (resizingLeft) {
            float right = resizing.getX() + resizing.getWidth();
            float expectedWidth = right - mouseX;
            if (expectedWidth >= Node.MINIMUM_SIZE) {
                resizing.setX(mouseX);
                resizing.setWidth(expectedWidth);
            }
        } else if (resizingRight) {
            resizing.setWidth(mouseX - resizing.getX());
        } else if (resizingTop) {
            float bottom = resizing.getY() + resizing.getHeight();
            float expectedHeight = bottom - mouseY;
            if (expectedHeight >= Node.MINIMUM_SIZE) {
                resizing.setY(mouseY);
                resizing.setHeight(expectedHeight);
            }
        } else if (resizingBottom) {
            resizing.setHeight(mouseY - resizing.getY());
        }
    }
    
    public void endDragResize() {
        resizingLeft = false;
        resizingRight = false;
        resizingTop = false;
        resizingBottom = false;
        setLeftMouseDown(false);
        dragging = false;
        resizing = null;
        
    }
    
    public void endDragMove() {
        moveDragging = null;
        moveDragOffsets = null;
        dragging = false;
    }
}
