/*
 * Process Sketcher is a simple flowchart making software.
 * Copyright (C) 2015  Jonathon Prehn, Kevin Prehn
 * 
 * This file is a part of Process Sketcher.
 *
 * Process Sketcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Process Sketcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Process Sketcher.  If not, see <http://www.gnu.org/licenses/>.
 */
package bropals.processsketcher.style;

import bropals.processsketcher.FlowchartWindow;
import bropals.processsketcher.data.BinaryUtil;
import bropals.processsketcher.data.BinaryData;
import java.awt.Color;

/**
 * Controls how a node looks.
 * @author Jonathon
 */
public class NodeStyle extends FontStyle implements BinaryData {
    
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
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof NodeStyle)) {
            return false;
        }
        NodeStyle ns = (NodeStyle)other;
        return ns.getShape() == this.getShape() &&
               ns.getBorderColor().equals(this.getBorderColor()) &&
               ns.getBorderSize() == this.getBorderSize() &&
               ns.getFillColor().equals(this.getFillColor()) &&
               ns.getFontColor().equals(this.getFontColor()) &&
               ns.getFontSize() == this.getFontSize() &&
               ns.getFontType().equals(this.getFontType());
    }

    @Override
    public int bytes() {
        return 11 + super.bytes();
    }
    
    @Override
    public void toBinary(byte[] arr, int pos) {
        arr[pos] = shape.toByte();
        BinaryUtil.colorToBytes(borderColor, arr, pos+1);
        BinaryUtil.colorToBytes(fillColor, arr, pos+4);
        BinaryUtil.intToBytes(borderSize, arr, pos+7);
        super.toBinary(arr, pos+11);
    }

    @Override
    public void fromBinary(byte[] arr, int pos, FlowchartWindow window) {
        shape = Shape.fromByte(arr[pos]);
        borderColor = BinaryUtil.bytesToColor(arr, pos+1);
        fillColor = BinaryUtil.bytesToColor(arr, pos+4);
        borderSize = BinaryUtil.bytesToInt(arr, pos+7);
        super.fromBinary(arr, pos+11, window);
    }
    
    /**
     * Sets the this style into the given style so they are the same.
     * @param style the style to set this one to.
     */
    public void setTo(NodeStyle style) {
        setFontType(style.getFontType());
        setFontColor(style.getFontColor());
        setFontSize(style.getFontSize());
        setBorderColor(style.getBorderColor());
        setFillColor(style.getFillColor());
        setBorderSize(style.getBorderSize());
        setShape(style.getShape());
    }
}
