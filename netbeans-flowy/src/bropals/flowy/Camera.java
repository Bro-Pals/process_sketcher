/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy;

import java.awt.Point;

/**
 * Holds the view transformation.
 * @author Jonathon
 */
public class Camera {
    
    // 1 = no zoom
    private float zoom;
    private int x; //Camera X position (World coordinates)
    private int y; //Camera Y position (World coordinates)
    
    public Camera() {
        zoom = 1;
        x = 0;
        y = 0;
    }
    
    public Point convertCanvasToWorld(Point p) {
        p.x = convertCanvasToWorldX(p.x);
        p.y = convertCanvasToWorldY(p.y);
        return p;
    }
    
    public Point convertWorldToCanvas(Point p) {
        p.x = convertWorldToCanvasX(p.x);
        p.y = convertWorldToCanvasY(p.y);
        return p;
    }
    
    public int convertCanvasToWorldX(int x) {
        return (int)(x*zoom) + this.x;
    }
    
    public int convertCanvasToWorldY(int y) {
        return (int)(y*zoom) + this.y;
    }
    
    public int convertWorldToCanvasX(int x) {
        return (int)((x - this.x)/zoom);
    }
    
    public int convertWorldToCanvasY(int y) {
        return (int)((y - this.y)/zoom);
    }
}
