/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.data;

import bropals.flowy.style.LineStyle;
import java.awt.Graphics;
import bropals.flowy.Camera;

/**
 *
 * @author Jonathon
 */
public class NodeLine implements Selectable {
    
    private Node parent;
    private Node child;
    private LineStyle style;
    private String tailText;
    private String centerText;
    private String headText;

    public NodeLine(Node parent, Node child) {
        this.parent = parent;
        this.child = child;
        style = null;
        tailText = "";
        centerText = "";
        headText = "";
    }
    
    @Override
    public void render(Camera camera, Graphics g) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getChild() {
        return child;
    }

    public void setChild(Node child) {
        this.child = child;
    }

    public LineStyle getStyle() {
        return style;
    }

    public void setStyle(LineStyle style) {
        this.style = style;
    }

    public String getTailText() {
        return tailText;
    }

    public void setTailText(String tailText) {
        this.tailText = tailText;
    }

    public String getCenterText() {
        return centerText;
    }

    public void setCenterText(String centerText) {
        this.centerText = centerText;
    }

    public String getHeadText() {
        return headText;
    }

    public void setHeadText(String headText) {
        this.headText = headText;
    }
   
}
