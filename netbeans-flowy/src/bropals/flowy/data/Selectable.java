/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.data;

import java.awt.Graphics;
import bropals.flowy.Camera;
import bropals.flowy.style.FontStyle;

/**
 * Represents an object that can be selected and edited.
 * @author Jonathon
 */
public interface Selectable {
    void render(Camera camera, Graphics g);
    /**
     * Gets the style that controls the font of the selectable.
     * @return 
     */
    public FontStyle getFontStyle();
}
