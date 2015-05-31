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
    private int x, y, width, height;
    private ArrayList<NodeLine> linesConnected;
    private String innerText;
    
    public Node(int x, int y) {
        style = null; // ???
        this.x = x;
        this.y = y;
        width = 100;
        height = 80;
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

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
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


}
