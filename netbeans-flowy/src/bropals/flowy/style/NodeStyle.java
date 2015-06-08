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
package bropals.flowy.style;

import java.awt.Color;

/**
 * Controls how a node looks.
 * @author Jonathon
 */
public class NodeStyle extends FontStyle{
    
    private Shape shape;
    private Color fillColor;
    private Color borderColor;
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
