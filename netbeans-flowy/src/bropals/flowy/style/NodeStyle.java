/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.style;

import java.awt.Color;

/**
 *
 * @author Jonathon
 */
public class NodeStyle extends FontStyle{
    
    private Shape shape;
    private Color fillColor;
    private Color borderColor;
    private int borderSize;

    public NodeStyle() {
        super();
        shape = Shape.MERGE;
        fillColor = Color.WHITE;
        borderColor = Color.BLACK;
        borderSize = 1;
    }
    
    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public int getBorderSize() {
        return borderSize;
    }

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
