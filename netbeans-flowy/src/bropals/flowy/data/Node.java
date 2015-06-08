/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.data;

import bropals.flowy.Camera;
import bropals.flowy.EventManager;
import bropals.flowy.style.FontStyle;
import bropals.flowy.style.NodeStyle;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 * An object to represent a node in a flowchart.
 * @author Jonathon
 */
public class Node implements Selectable {
    
    /**
     * The smallest value for the width / height of a node.
     */
    public final static float MINIMUM_SIZE = (EventManager.DRAG_RESIZE_DISTANCE*2) + 4;
    
    /**
     * If the style is null, revert to a default style.
     */
    private NodeStyle style;
    private float x, y, width, height;
    private ArrayList<NodeLine> linesConnected;
    private String innerText;
    
    /**
     * Creates the default node in the specified world coordinate position.
     * @param x the X position of the node in world coordinates.
     * @param y the Y position of the node in world coordinates.
     */
    public Node(float x, float y) {
        style = new NodeStyle(); // ???
        this.x = x;
        this.y = y;
        width = 100;
        height = 80;
        linesConnected = new ArrayList<>();
        innerText = "";
    }
    
    /**
     * Gets the node style that is controlling the look of this Node.
     * @return the style controlling the look of this Node.
     */
    public NodeStyle getStyle() {
        return style;
    }

    /**
     * Sets the node style that is to control the look of this Node.
     * @param style the style of this Node.
     */
    public void setStyle(NodeStyle style) {
        this.style = style;
    }

    /**
     * Gets the X position of this node, in world coordinates.
     * @return the X position of the node, in world coordinates.
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the X position of this node, in world coordinates.
     * @param x the new X position of this node, in world coordinates.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Gets the Y position of this node, in world coordinates.
     * @return the Y position of the node, in world coordinates.
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the Y position of this node, in world coordinates.
     * @param y the new Y position of this node, in world coordinates.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Gets the width of this node, in world coordinates.
     * @return the width of this node, in world coordinates.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Sets the width of this node, in world coordinates.
     * @param width the new width of this node, in world coordinates.
     */
    public void setWidth(float width) {
        if (width >= MINIMUM_SIZE) {
            this.width = width;
        }
    }

    /**
     * Gets the height of this node, in world coordinates.
     * @return the height of this node, in world coordinates.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Sets the height of this node, in world coordinates.
     * @param height the new height of this node, in world coordinates.
     */
    public void setHeight(float height) {
        if (height >= MINIMUM_SIZE) {
            this.height = height;
        }
    }

    /**
     * Gets the list of node lines that are connected to this node.
     * @return the list of node lines connected to this node.
     */
    public ArrayList<NodeLine> getLinesConnected() {
        return linesConnected;
    }

    /**
     * Gets the text that this node contains.
     * @return the text that this node contains.
     */
    public String getInnerText() {
        return innerText;
    }

    /**
     * Sets the text that this node contains.
     * @param innerText the new body of text for this node to contain.
     */
    public void setInnerText(String innerText) {
        this.innerText = innerText;
    }
    
    @Override
    public Object clone() {
        Node other = new Node(getX(), getY());
        other.setWidth(getWidth());
        other.setHeight(getHeight());
        other.setStyle((NodeStyle)style.clone()); // same style
        other.setInnerText(getInnerText()); // initially no text
        return other;
    }

    @Override
    public FontStyle getFontStyle() {
        return style;
    }
}
