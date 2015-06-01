/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 * Holds the view transformation.
 * @author Jonathon
 */
public class Camera {
    
    public static final float ZOOM_MIN = 0.1f;
    public static final float ZOOM_MAX = 10;
    
    // 1 = no zoom
    private float zoom;
    private float x; //Camera X position (World coordinates)
    private float y; //Camera Y position (World coordinates)
    private boolean locked = false;
    
    public Camera() {
        zoom = 1;
        x = 0;
        y = 0;
    }
    
    public Point.Float convertCanvasToWorld(Point p) {
        return new Point.Float(
                convertCanvasToWorldX(p.x),
                convertCanvasToWorldY(p.y)
        );
        
    }
    
    public Point convertWorldToCanvas(Point.Float p) {
        return new Point(
                convertWorldToCanvasX(p.x),
                convertWorldToCanvasY(p.y)
        );
    }
    
    public float convertCanvasToWorldX(int x) {
        return ((float)x*zoom) + this.x;
    }
    
    public float convertCanvasToWorldY(int y) {
        return ((float)y*zoom) + this.y;
    }
    
    public int convertWorldToCanvasX(float x) {
        return (int)((x - this.x)/zoom);
    }
    
    public int convertWorldToCanvasY(float y) {
        return (int)((y - this.y)/zoom);
    }
    
    public void setWorldLocationX(float x) {
        if (!locked) {
            this.x = x;
        }
    }
    
    public void setWorldLocationY(float y) {
        if (!locked) {
            this.y = y;
        }
    }
    
    public float getWorldLocationX() {
        return x;
    }
    
    public float getWorldLocationY() {
        return y;
    }

    public int getCanvasLocationX() {
        return convertWorldToCanvasX(x);
    }
    
    public int getCanvasLocationY() {
        return convertWorldToCanvasY(y);
    }
    
    public void setCanvasLocationX(int x) {
        if (!locked) {
            this.x = convertCanvasToWorldX(x);
        }
    }
    
    public void setCanvasLocationY(int y) {
        if (!locked) {
            this.y = convertCanvasToWorldY(y);
        }
    }

    public float getZoom() {
        return zoom;
    }
    
    public void setZoom(float zoom) {
        if (!locked) {
            this.zoom = zoom;
        }
    }
    
    public void zoom(float zoom) {
        if (!locked) {
            this.zoom += zoom;
            if (this.zoom < ZOOM_MIN) {
                this.zoom = ZOOM_MIN;
            } else if (this.zoom > ZOOM_MAX) {
                this.zoom = ZOOM_MAX;
            }
        }
    }
    
    public void lock() {
        locked = true;
    }
    
    public void unlock() {
        locked = false;
    }
}
