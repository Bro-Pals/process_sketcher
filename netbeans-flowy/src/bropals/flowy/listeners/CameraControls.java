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
 *
 * @author Jonathon
 */
public class CameraControls implements MouseMotionListener, KeyListener, MouseListener, MouseWheelListener {

    private Camera camera;
    private FlowchartWindow window;
    
    private int panStartX; //In world coords
    private int panStartY; //In world coords
    private int camStartX;
    private int camStartY;
    private boolean holdingSpace = true;
    
    public CameraControls(Camera camera, FlowchartWindow window) {
        this.camera = camera;
        this.window = window;
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragging(e.getButton())) {
            int deltaX = camera.convertCanvasToWorldX(e.getX())-panStartX;
            int deltaY = camera.convertCanvasToWorldY(e.getY())-panStartY;
            camera.setWorldLocationX(camStartX + deltaX);
            camera.setWorldLocationY(camStartY + deltaY);
            System.out.println("Camera: " + camera.getWorldLocationX() + 
                    ", " + camera.getWorldLocationY());
        }
        window.redrawView();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("yay im camera controls key pressed");
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            holdingSpace = true;
        }
        //    window.redrawView();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            holdingSpace = true;
        }
        //window.redrawView();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (dragging(e.getButton())) {
            panStartX = camera.convertCanvasToWorldX(e.getX());
            panStartY = camera.convertCanvasToWorldY(e.getY());
            camStartX = camera.getWorldLocationX();
            camStartY = camera.getWorldLocationY();
        }
        //window.redrawView();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
    
    private boolean dragging(int mouseButton) {
        return (holdingSpace && mouseButton == MouseEvent.BUTTON1) || mouseButton == MouseEvent.BUTTON2;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        camera.zoom(e.getWheelRotation());
        window.redrawView();
    }
}
