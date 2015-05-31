/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * Gets events from the view and StyleManager and handles them.
 * @author Jonathon
 */
public class EventManager implements KeyListener, MouseListener, MouseMotionListener {
 
    private SelectionManager selectionManager;
    private FlowchartWindow window;
    private DragManager dragManager;
    
    public EventManager(FlowchartWindow instance) {
        window = instance;
        dragManager = new DragManager();
        selectionManager = new SelectionManager();
    }


    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("PRESSED A KEY " + e.getKeyCode());
        switch (e.getKeyCode()) {
            case KeyEvent.VK_N:
                if (e.isControlDown()) {
                    System.out.println("Creating a flowchart");
                    window.getFlowchartWindowManager().newFlowchart();
                }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Point mousePosition = window.getCamera().convertCanvasToWorld(e.getPoint());
        
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        Point mousePosition = window.getCamera().convertCanvasToWorld(e.getPoint());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
    
    
}
