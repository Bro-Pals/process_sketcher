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
 * Controls how a node line looks.
 * @author Jonathon
 */
public class LineStyle extends FontStyle {
    
    private LineType type;
    private Color lineColor;
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
    
    
}
