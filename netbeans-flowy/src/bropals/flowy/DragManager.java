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
    
    private boolean dragging, boxSelecting;
    private int offsetX;
    private int offsetY;
    private int initialX;
    private int initialY;
    private Node newlyMadeNode;
    
    public DragManager() {
        dragging = false;
        offsetX = 0;
        offsetY = 0;
        initialX = 0;
        initialY = 0;
        newlyMadeNode = null;
        boxSelecting = false;
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

    public int getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    public int getInitialX() {
        return initialX;
    }

    public void setInitialX(int initialX) {
        this.initialX = initialX;
    }

    public int getInitialY() {
        return initialY;
    }

    public void setInitialY(int initialY) {
        this.initialY = initialY;
    }

    public Node getNewlyMadeNode() {
        return newlyMadeNode;
    }

    public void setNewlyMadeNode(Node newlyMadeNode) {
        this.newlyMadeNode = newlyMadeNode;
    }
    
    
}
