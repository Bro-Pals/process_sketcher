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
 * Controls how a node line looks.
 * @author Jonathon
 */
public class LineStyle extends FontStyle implements BinaryData {
    
    /**
     * The type of the line.
     */
    private LineType type;
    /**
     * The color of the line.
     */
    private Color lineColor;
    /**
     * The thickness/stoke of the line.
     */
    private int lineSize;
    
    
    /**
     * Creates the default line sytle, which is a solid black 1 point size line.
     */
    public LineStyle() {
        type = LineType.SOLID;
        lineColor = Color.BLACK;
        lineSize = 1;
    }
    
    /**
     * Gets the type of the line, which may be either SOLID, DASHED, or DOTTED.
     * @return the current line type.
     */
    public LineType getType() {
        return type;
    }

    /**
     * Sets the type of the line, which may be either SOLID, DASHED, or DOTTED.
     * @param type the new type of the line.
     */
    public void setType(LineType type) {
        this.type = type;
    }

    /**
     * Gets the color of this line.
     * @return the color of this line.
     */
    public Color getLineColor() {
        return lineColor;
    }

    /**
     * Sets the color of this line.
     * @param lineColor the color of this line.
     */
    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    /**
     * Gets the line size for this line.
     * @return the line size for this line.
     */
    public int getLineSize() {
        return lineSize;
    }

    /**
     * Sets the line size.
     * @param lineSize the new line size.
     */
    public void setLineSize(int lineSize) {
        this.lineSize = lineSize;
    }
    
    @Override
    public Object clone() {
        LineStyle other = new LineStyle();
        other.setLineColor(getLineColor());
        other.setLineSize(getLineSize());
        other.setType(type);
        other.setFontColor(getFontColor());
        other.setFontType(getFontType());
        other.setFontSize(getFontSize());
        return other;
    }
    
    @Override
    public boolean equals(Object other) {
        if (!(other instanceof LineStyle)) {
            return false;
        }
        LineStyle ls = (LineStyle)other;
        return ls.getLineColor().equals(this.getLineColor()) &&
               ls.getLineSize() == this.getLineSize() &&
               ls.getType() == this.getType() &&
               ls.getFontColor().equals(this.getFontColor()) &&
               ls.getFontSize() == this.getFontSize() &&
               ls.getFontType().equals(this.getFontType());
    }

    @Override
    public int bytes() {
        return 8 + super.bytes();
    }
    
    @Override
    public void toBinary(byte[] arr, int pos) {
        arr[pos] = type.toByte();
        BinaryUtil.colorToBytes(lineColor, arr, pos+1);
        BinaryUtil.intToBytes(lineSize, arr, pos+4);
        super.toBinary(arr, pos+8);
    }

    @Override
    public void fromBinary(byte[] arr, int pos, FlowchartWindow window) {
        type = LineType.fromByte(arr[pos]);
        lineColor = BinaryUtil.bytesToColor(arr, pos+1);
        lineSize = BinaryUtil.bytesToInt(arr, pos+4);
        super.fromBinary(arr, pos+8, window);
    }    
    
    /**
     * Copies the other style into this style so they are the same.
     * @param style the style to set this one to.
     */
    public void setTo(LineStyle style) {
        setFontType(style.getFontType());
        setFontColor(style.getFontColor());
        setFontSize(style.getFontSize());
        setLineColor(style.getLineColor());
        setLineSize(style.getLineSize());
        setType(style.getType());
    }
}
