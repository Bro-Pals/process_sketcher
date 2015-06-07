/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.style;

import java.awt.Color;
import java.awt.Font;

/**
 *
 * @author Jonathon
 */
public class FontStyle {
    
    private Color fontColor;
    private Font fontType;

    public FontStyle() {
        fontColor = Color.BLACK;
        fontType = new Font("Arial", Font.PLAIN, 12);
    }
    
    public int getFontSize() {
        return fontType.getSize();
    }

    public void setFontSize(int fontSize) {
        fontType = fontType.deriveFont((float)fontSize);
    }

    public Color getFontColor() {
        return fontColor;
    }

    public void setFontColor(Color fontColor) {
        this.fontColor = fontColor;
    }

    public Font getFontType() {
        return fontType;
    }

    public void setFontType(Font fontType) {
        this.fontType = fontType.deriveFont((float)getFontSize());
    }
}
