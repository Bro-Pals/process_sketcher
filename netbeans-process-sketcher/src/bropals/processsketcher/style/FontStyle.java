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
import java.awt.Font;

/**
 * Controls the look of the text on a node or a node line.
 * @author Jonathon
 */
public class FontStyle implements BinaryData {
    
    /**
     * The color of the text when it's rendered.
     */
    private Color fontColor;
    /**
     * The font that will be used when rendering the text.
     */
    private Font fontType;

    /**
     * Creates the default font style, which is black text an 12 point Arial
     * font.
     */
    public FontStyle() {
        fontColor = Color.BLACK;
        fontType = new Font("Arial", Font.PLAIN, 12);
    }
    
    /**
     * Gets the point size of the font.
     * @return the point size of the font.
     */
    public int getFontSize() {
        return fontType.getSize();
    }

    /**
     * Sets the point size of this font.
     * @param fontSize the new point size of this font.
     */
    public void setFontSize(int fontSize) {
        fontType = fontType.deriveFont((float)fontSize);
    }

    /**
     * Gets the color to define the color of the text that uses this FontStyle.
     * @return the color of this font style.
     */
    public Color getFontColor() {
        return fontColor;
    }

    /**
     * Sets the color of the text that uses this font style.
     * @param fontColor the color of this font style.
     */
    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    /**
     * Gets the font that is being used for this FontStyle.
     * @return the font to use.
     */
    public Font getFontType() {
        return fontType;
    }

    /**
     * Sets the font for this FontStyle to use.
     * @param fontType the font for this FontStyle to use.
     */
    public void setFontType(Font fontType) {
        this.fontType = fontType.deriveFont((float)getFontSize());
    }

    @Override
    public int bytes() {
        return 7 + BinaryUtil.bytesForFont(fontType);
    }

    @Override
    public void toBinary(byte[] arr, int pos) {
        BinaryUtil.colorToBytes(getFontColor(), arr, pos);
        BinaryUtil.intToBytes(getFontSize(), arr, pos+3);
        BinaryUtil.fontToBytes(getFontType(), arr, pos+7);   
    }

    @Override
    public void fromBinary(byte[] arr, int pos, FlowchartWindow window) {
        setFontColor(BinaryUtil.bytesToColor(arr, pos));
        int fontSize = BinaryUtil.bytesToInt(arr, pos+3);
        setFontType(BinaryUtil.bytesToFont(arr, pos+7));
        setFontSize(fontSize);    
    }
}
