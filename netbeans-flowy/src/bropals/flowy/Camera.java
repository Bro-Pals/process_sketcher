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
    
    public Point convertCanvasToWorld(Point p) {
        p.x = (int)(p.x*zoom) + x;
        p.y = (int)(p.y*zoom) + y;
        return p;
    }
    
    public Point convertWorldToCanvas(Point p) {
        p.x = (int)((p.x - x)/zoom);
        p.y = (int)((p.y - y)/zoom);
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
