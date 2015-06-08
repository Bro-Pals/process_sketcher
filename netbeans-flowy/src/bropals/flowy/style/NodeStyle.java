/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.style;

import java.awt.Color;

/**
 * Controls how a node looks.
 * @author Jonathon
 */
public class NodeStyle extends FontStyle{
    
    /**
     * The shape of the Node.
     */
    private Shape shape;
    /**
     * The fill color of the Node.
     */
    private Color fillColor;
    /**
     * The color of the Node's border.
     */
    private Color borderColor;
    /**
     * The thickness/stoke of the Node's border..
     */
    private int borderSize;

    /**
     * Creates the default node, which is an Action shape with white fill,
     * and a 1 point black border.
     */
    public NodeStyle() {
        super();
        shape = Shape.ACTION;
        fillColor = Color.WHITE;
        borderColor = Color.BLACK;
        borderSize = 1;
    }
    
    /**
     * Gets the shape of this node style: its name indicates what it means
     * on a flowchart.
     * @return the shape of this node style.
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * Sets the shape of this node style.
     * @param shape the new shape of this node style.
     */
    public void setShape(Shape shape) {
        this.shape = shape;
    }

    /**
     * Gets the fill color of this node style.
     * @return the fill color of this node style.
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * Sets the fill color of this node style.
     * @param fillColor the new fill color of this node style.
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * Gets the border color of this node style.
     * @return the border color of this node style.
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * Sets the border color of this node style.
     * @param borderColor the new border color of this node style.
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * Gets the border point size of this node style.
     * @return the border point size of this node style.
     */
    public int getBorderSize() {
        return borderSize;
    }

    /**
     * Sets the border point size of this node style.
     * @param borderSize the border point size of this node style.
     */
    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
    }
    
    @Override
    public Object clone() {
        NodeStyle other = new NodeStyle();
        other.setBorderColor(borderColor);
        other.setBorderSize(borderSize);
        other.setFillColor(fillColor);
        other.setShape(shape);
        other.setFontColor(getFontColor());
        other.setFontType(getFontType());
        other.setFontSize(getFontSize());
        return other;
    }
    
}
