/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.data;

import bropals.flowy.style.LineStyle;
import java.awt.Graphics;
import bropals.flowy.Camera;
import bropals.flowy.style.FontStyle;

/**
 * An object to represent a process flow between two nodes.
 * The arrow points on the head side of the line.
 * @author Jonathon
 */
public class NodeLine implements Selectable {
    
    private Node parent;
    private Node child;
    private LineStyle style;
    private String tailText;
    private String centerText;
    private String headText;

    /**
     * Creates a new NodeLine to relate two nodes together. 
     * @param parent the node that is on the tail side of the node line.
     * @param child the node that is on the head side of the node line.
     */
    public NodeLine(Node parent, Node child) {
        this.parent = parent;
        this.child = child;
        style = new LineStyle();
        tailText = "";
        centerText = "";
        headText = "";
    }
    
    @Override
    public void render(Camera camera, Graphics g) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    /**
     * Gets the parent node (the node on the tail side).
     * @return the parent node.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Sets the parent node (the node on the tail side).
     * @param parent the new parent node.
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Gets the child node (the node on the head side).
     * @return the child node.
     */
    public Node getChild() {
        return child;
    }

    /**
     * Sets the child node (the node on the head side).
     * @param child the new child node.
     */
    public void setChild(Node child) {
        this.child = child;
    }

    /**
     * Gets the line style that is controlling the look of this line.
     * @return the line style that is controlling the look of this style.
     */
    public LineStyle getStyle() {
        return style;
    }

    /**
     * Sets the line style to control the look of this line.
     * @param style the line style to control the look of this line.
     */
    public void setStyle(LineStyle style) {
        this.style = style;
    }

    /**
     * Gets the text that is on the tail side of the line.
     * @return the text that is on the tail side of the line.
     */
    public String getTailText() {
        return tailText;
    }

    /**
     * Sets the text that is on the tail side of the line.
     * @param tailText the text that is on the tail side of the line.
     */
    public void setTailText(String tailText) {
        this.tailText = tailText;
    }

    /**
     * Gets the text that is on the center of the line.
     * @return the text that is on the center of the line.
     */
    public String getCenterText() {
        return centerText;
    }

    /**
     * Sets the text that is in the center of the line.
     * @param centerText the text on the center of the line.
     */
    public void setCenterText(String centerText) {
        this.centerText = centerText;
    }

    /**
     * Gets the text that is on the head side of the line.
     * @return the text that is on the head side of the line.
     */
    public String getHeadText() {
        return headText;
    }

    /**
     * Sets the text that is on the head side of the line.
     * @param headText the text that is on the head side of the line.
     */
    public void setHeadText(String headText) {
        this.headText = headText;
    }
    
    @Override
    public Object clone() {
        NodeLine other = new NodeLine(parent, child);
        other.setHeadText(headText);
        other.setTailText(tailText);
        other.setCenterText(centerText);
        other.setStyle((LineStyle)(style.clone()));
        return other;
    }
   
    @Override
    public FontStyle getFontStyle() {
        return style;
    }
}
