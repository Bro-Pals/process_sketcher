package bropals.flowy;

import bropals.flowy.data.Node;

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
    
    private SelectionManager selectionManager;
    private boolean dragging, boxSelecting;
    private float offsetX; //In world coords
    private float offsetY; //In world coords
    private float initialX; //In world coords
    private float initialY; //In world coords
    private Node newlyMadeNode;
    private Node[] moveDragging; //List of stuff that is being dragged
    private float[][] moveDragOffsets; //World coords
    
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
    }

    public void setBoxSelecting(boolean boxSelecting) {
        this.boxSelecting = boxSelecting;
    }

    public boolean isBoxSelecting() {
        return boxSelecting;
    }
    
    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public float getInitialX() {
        return initialX;
    }

    public void setInitialX(float initialX) {
        this.initialX = initialX;
    }

    public float getInitialY() {
        return initialY;
    }

    public void setInitialY(float initialY) {
        this.initialY = initialY;
    }

    public Node getNewlyMadeNode() {
        return newlyMadeNode;
    }

    public void setNewlyMadeNode(Node newlyMadeNode) {
        this.newlyMadeNode = newlyMadeNode;
    }
    
    /**
     * Start move dragging.
     * @param mouseX the current mouse X position in world coordinates.
     * @param mouseY the current mouse Y position in world coordinates.
     */
    public void startDragMove(float mouseX, float mouseY) {
        moveDragging = (Node[])selectionManager.getSelectedNodes().toArray(new Node[0]);
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
    
    public void endDragMove() {
        moveDragging = null;
        moveDragOffsets = null;
        dragging = false;
    }
}
