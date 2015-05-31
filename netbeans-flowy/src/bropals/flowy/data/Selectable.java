/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.data;

import java.awt.Graphics;
import javafx.scene.Camera;

/**
 *
 * @author Jonathon
 */
public interface Selectable {
    void render(Camera camera, Graphics g);
}
