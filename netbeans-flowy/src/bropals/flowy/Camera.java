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
    
    public static final float ZOOM_MIN = 0.1f;
    public static final float ZOOM_MAX = 10;
    
    // 1 = no zoom
    private float zoom;
    private int x; //Camera X position (World coordinates)
    private int y; //Camera Y position (World coordinates)
    
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
    
    public void setWorldLocationX(int x) {
        this.x = x;
    }
    
    public void setWorldLocationY(int y) {
        this.y = y;
    }
    
    public int getWorldLocationX() {
        return x;
    }
    
    public int getWorldLocationY() {
        return y;
    }

    public int getCanvasLocationX() {
        return convertWorldToCanvasX(x);
    }
    
    public int getCanvasLocationY() {
        return convertWorldToCanvasY(y);
    }
    
    public void setCanvasLocationX(int x) {
        this.x = convertCanvasToWorldX(x);
    }
    
    public void setCanvasLocationY(int y) {
        this.y = convertCanvasToWorldY(x);
    }

    public float getZoom() {
        return zoom;
    }
    
    public void setZoom(float zoom) {
        this.zoom = zoom;
    }
    
    public void zoom(float zoom) {
        this.zoom += zoom;
        if (this.zoom < ZOOM_MIN) {
            this.zoom = ZOOM_MIN;
        } else if (this.zoom > ZOOM_MAX) {
            this.zoom = ZOOM_MAX;
        }
    }
}
