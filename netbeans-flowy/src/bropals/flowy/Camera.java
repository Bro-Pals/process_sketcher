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
    /**
     * The minimum zoom factor.
     */
    public static final float ZOOM_MIN = 0.1f;
    /**
     * The maximum zoom value.
     */
    public static final float ZOOM_MAX = 10;
    
    /**
     * The zoom factor of this Camera.
     */
    private float zoom;
    /**
     * The X position of the camera in world coordinates.
     */
    private float x;
    /**
     * The Y position of the camera in world coordinates.
     */
    private float y;
    /**
     * Whether or not this camera can be transformed by an outside client.
     */
    private boolean locked = false;
    
    /**
     * Creates a Camera with default values. (Position = (0, 0), Zoom = 1).
     */
    public Camera() {
        zoom = 1;
        x = 0;
        y = 0;
    }
    
    /**
     * Converts the given point from canvas coordinates to world coordinates.
     * @param p the point in canvas coordinates.
     * @return the same point but in world coordinates.
     */
    public Point.Float convertCanvasToWorld(Point p) {
        return new Point.Float(
                convertCanvasToWorldX(p.x),
                convertCanvasToWorldY(p.y)
        );
        
    }
    
    /**
     * Converts the given point from world coordinates to canvas coordinates.
     * @param p the point in world coordinates.
     * @return the same point but in canvas coordinates.
     */
    public Point convertWorldToCanvas(Point.Float p) {
        return new Point(
                convertWorldToCanvasX(p.x),
                convertWorldToCanvasY(p.y)
        );
    }
    
    /**
     * Converts a X position from canvas coordinates to world coordinates.
     * @param x a X position in canvas coordinates.
     * @return the same X position but in world coordinates.
     */
    public float convertCanvasToWorldX(int x) {
        return ((float)x*zoom) + this.x;
    }
    
    /**
     * Converts a Y position from canvas coordinates to world coordinates.
     * @param y a Y position in canvas coordinates.
     * @return the same Y position but in world coordinates.
     */
    public float convertCanvasToWorldY(int y) {
        return ((float)y*zoom) + this.y;
    }
    
    /**
     * Converts an X position from world coordinates to canvas coordinates.
     * @param x an X position in world coordinates.
     * @return the same X position but in canvas coordinates.
     */
    public int convertWorldToCanvasX(float x) {
        return (int)((x - this.x)/zoom);
    }
    
    /**
     * Converts an Y position from world coordinates to canvas coordinates.
     * @param y an Y position in world coordinates.
     * @return the same X position but in canvas coordinates.
     */
    public int convertWorldToCanvasY(float y) {
        return (int)((y - this.y)/zoom);
    }
    
    /**
     * Sets the X location of the camera, given a X world coordinate.
     * @param x the new X location of the camera in world coordinates.
     */
    public void setWorldLocationX(float x) {
        if (!locked) {
            this.x = x;
        }
    }
    
    /**
     * Sets the Y location of the camera, given a Y world coordinate.
     * @param y the new Y location of the camera in world coordinates.
     */
    public void setWorldLocationY(float y) {
        if (!locked) {
            this.y = y;
        }
    }
    
    /**
     * Gets the X position of the camera in world coordinates.
     * @return the X position of the camera in world coordinates.
     */
    public float getWorldLocationX() {
        return x;
    }
    
    /**
     * Gets the Y position of the camera in world coordinates.
     * @return the Y position of the camera in world coordinates.
     */
    public float getWorldLocationY() {
        return y;
    }

    /**
     * Gets the X position of the camera in canvas coordinates.
     * @return the X position of the camera in canvas coordinates.
     */
    public int getCanvasLocationX() {
        return convertWorldToCanvasX(x);
    }
    
    /**
     * Gets the Y position of the camera in canvas coordinates.
     * @return the Y position of the camera in canvas coordinates.
     */
    public int getCanvasLocationY() {
        return convertWorldToCanvasY(y);
    }
    
    /**
     * Sets the X location of the camera, given a X canvas coordinate.
     * @param x the new X location of the camera in canvas coordinates.
     */
    public void setCanvasLocationX(int x) {
        if (!locked) {
            this.x = convertCanvasToWorldX(x);
        }
    }
    
    /**
     * Sets the Y location of the camera, given a Y canvas coordinate.
     * @param y the new Y location of the camera in canvas coordinates.
     */
    public void setCanvasLocationY(int y) {
        if (!locked) {
            this.y = convertCanvasToWorldY(y);
        }
    }

    /**
     * Gets the zoom factor of the camera. A zoom level of 1 means that
     * there is no difference in size when comparing world coordinates to
     * canvas coordinates.
     * @return the zoom factor of the camera.
     */
    public float getZoom() {
        return zoom;
    }
    
    /**
     * Sets the zoom factor of the camera. The zoom factor is the ratio
     * of world scale divided by canvas scale. A zoom factor &gt; 1 means
     * that objects will appear larger than they actually are in the
     * view, while a zoom factor &lt; means that objects will appear
     * smaller than they actually are in the view.
     * <p>
     * The zoom cannot be greater than
     * {@link #bropals.flowy.Camera.ZOOM_MAX ZOOM_MAX} or less than
     * {@link #bropals.flowy.Camera.ZOOM_MIN ZOOM_MIN}.
     * @param zoom the zoom factor to set the camera to.
     */
    public void setZoom(float zoom) {
        if (!locked) {
            this.zoom = zoom;
            clampZoom();
        }
    }
    
    /**
     * Increments or decrements the zoom factor by the given amount, depending
     * on the sign of the zoom value given.
     * <p>
     * The zoom cannot be greater than
     * {@link #bropals.flowy.Camera.ZOOM_MAX ZOOM_MAX} or less than
     * {@link #bropals.flowy.Camera.ZOOM_MIN ZOOM_MIN}.
     * @param zoom the amount to add to the current zoom factor.
     */
    public void zoom(float zoom) {
        if (!locked) {
            this.zoom += zoom;
            clampZoom();
        }
    }
    
    /**
     * Clamps the zoom factor to its allowed range. If the zoom factor
     * is less than {@link #bropals.flowy.Camera.ZOOM_MIN ZOOM_MIN}, then
     * the factor is set to {@link #bropals.flowy.Camera.ZOOM_MIN ZOOM_MIN}. If
     * the zoom factor is greater than 
     * {@link #bropals.flowy.Camera.ZOOM_MAX ZOOM_MAX}, then the zoom factor
     * is set to{@link #bropals.flowy.Camera.ZOOM_MAX ZOOM_MAX}.
     */
    private void clampZoom() {
        if (this.zoom < ZOOM_MIN) {
            this.zoom = ZOOM_MIN;
        } else if (this.zoom > ZOOM_MAX) {
            this.zoom = ZOOM_MAX;
        }
    }
    
    /**
     * Locks the camera so that its position and zoom cannot be changed.
     */
    public void lock() {
        locked = true;
    }
    
    /**
     * Unlocks the camera so that its position and zoom can be changed.
     */
    public void unlock() {
        locked = false;
    }
    
    /**
     * Sets the camera position to the origin and sets the zoom factor to 1.
     */
    public void resetView() {
        if (!locked) {
            zoom = 1f;
            x = 0;
            y = 0;
        }
    }
}
