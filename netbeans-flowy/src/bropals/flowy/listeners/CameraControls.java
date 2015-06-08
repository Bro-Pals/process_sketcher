/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.listeners;

import bropals.flowy.Camera;
import bropals.flowy.FlowchartWindow;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Manages the camera controls.
 * @author Jonathon
 */
public class CameraControls implements MouseMotionListener, KeyListener, MouseListener, MouseWheelListener {
    /**
     * The camera that is being controlled.
     */
    private Camera camera;
    /**
     * The flowchart window.
     */
    private FlowchartWindow window;
    /**
     * The X mouse position in world coordinates when a pan has begun.
     */
    private float panStartX;
     /**
     * The Y mouse position in world coordinates when a pan has begun.
     */
    private float panStartY;
    /**
     * The camera X position in world coordinates when a pan has begun.
     */
    private float camStartX;
    /**
     * The camera Y position in world coordinates when a pan has begun.
     */
    private float camStartY;
    /**
     * A flag to indicate if the user is holding space.
     */
    private boolean holdingSpace = false;
    /**
     * A flag to indicate if the camera is being dragged.
     */
    private boolean draggingCamera = false;
    
    /**
     * Creates a CameraControls object for controlling a camera.
     * @param camera the camera to control.
     * @param window the flowchart window.
     */
    public CameraControls(Camera camera, FlowchartWindow window) {
        this.camera = camera;
        this.window = window;
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (draggingCamera) {
            float deltaX = camera.convertCanvasToWorldX(e.getX())-panStartX;
            float deltaY = camera.convertCanvasToWorldY(e.getY())-panStartY;
            camera.setWorldLocationX(camStartX - deltaX);
            camera.setWorldLocationY(camStartY -  deltaY);
            window.redrawView();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            holdingSpace = true;
        }
        //    window.redrawView();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            holdingSpace = false;
        }
        //window.redrawView();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        draggingCamera = dragging(e.getButton());
        if (draggingCamera) {
            panStartX = camera.convertCanvasToWorldX(e.getX());
            panStartY = camera.convertCanvasToWorldY(e.getY());
            camStartX = camera.getWorldLocationX();
            camStartY = camera.getWorldLocationY();
        }
        //window.redrawView();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON2 || e.getButton() == MouseEvent.BUTTON1) {
            draggingCamera = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    /**
     * Checks to see if the user is to begin a camera pan.
     * @param mouseButton the mouse button.
     * @return if the user is beginning a pan.
     */
    private boolean dragging(int mouseButton) {
        return (holdingSpace && mouseButton == MouseEvent.BUTTON1) || mouseButton == MouseEvent.BUTTON2;
    }

    /**
     * Zooms the camera in.
     */
    public void zoomIn() {
        camera.zoom(-1 / 4.0f);
    }
    
    /**
     * Zooms the camera out.
     */
    public void zoomOut() {
        camera.zoom(1 / 4.0f);
    }
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() > 0) {
            zoomOut();
        } else {
            zoomIn();
        }
        window.redrawView();
    }
}
