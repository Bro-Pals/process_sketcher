/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.data;

import bropals.flowy.Camera;
import bropals.flowy.style.NodeStyle;
import java.awt.Graphics;
import java.util.ArrayList;

/**
 *
 * @author Jonathon
 */
public class Node implements Selectable {
    
    /**
     * If the style is null, revert to a default style.
     */
    private NodeStyle style;
    private float x, y, width, height;
    private ArrayList<NodeLine> linesConnected;
    private String innerText;
    
    public Node(float x, float y) {
        style = new NodeStyle(); // ???
        this.x = x;
        this.y = y;
        width = 100;
        height = 80;
        linesConnected = new ArrayList<>();
        innerText = "default";
    }
    
    @Override
    public void render(Camera camera, Graphics g) {
        
    }
    
    public NodeStyle getStyle() {
        return style;
    }

    public void setStyle(NodeStyle style) {
        this.style = style;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public ArrayList<NodeLine> getLinesConnected() {
        return linesConnected;
    }

    public String getInnerText() {
        return innerText;
    }

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


}
