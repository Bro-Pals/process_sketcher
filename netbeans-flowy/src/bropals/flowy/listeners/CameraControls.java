/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.listeners;

import bropals.flowy.Camera;
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
    
    private int panStartX; //In world coords
    private int panStartY; //In world coords
    private int camStartX;
    private int camStartY;
    private boolean holdingSpace = false;
    
    public CameraControls(Camera camera) {
        this.camera = camera;
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        if (dragging(e.getButton())) {
            int deltaX = camera.convertCanvasToWorldX(e.getX())-panStartX;
            int deltaY = camera.convertCanvasToWorldY(e.getY())-panStartY;
            camera.setWorldLocationX(camStartX + deltaX);
            camera.setWorldLocationY(camStartY + deltaY);
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
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            holdingSpace = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON2) {
            panStartX = camera.convertCanvasToWorldX(e.getX());
            panStartY = camera.convertCanvasToWorldY(e.getY());
            camStartX = camera.getWorldLocationX();
            camStartY = camera.getWorldLocationY();
        }
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
    }
}
