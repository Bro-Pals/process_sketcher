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
public class LineStyle extends FontStyle {
    
    private LineType type;
    private Color lineColor;
    private int lineSize;

    public LineStyle() {
        type = LineType.DASHED;
        lineColor = Color.BLACK;
        lineSize = 1;
    }
    
    public LineType getType() {
        return type;
    }

    public void setType(LineType type) {
        this.type = type;
    }

    public Color getLineColor() {
        return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public int getLineSize() {
        return lineSize;
    }

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
