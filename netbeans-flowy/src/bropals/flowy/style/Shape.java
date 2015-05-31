/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.style;

import bropals.flowy.Camera;
import bropals.flowy.data.Node;
import java.awt.Graphics;
import java.awt.Point;

/**
 *
 * @author Jonathon
 */
public enum Shape {
    SQUARE, TRIANGLE, OVAL, DIAMOND, THING;
    
    public void renderShape(Node node, Camera camera, Graphics g) {
        switch(this) {
            case SQUARE:
                g.setColor(node.getStyle().getBorderColor());
                Point p1 = new Point(node.getX(), node.getY());
                Point p2 = new Point(node.getX() + node.getWidth(), node.getY());
                Point p3 = new Point(node.getX() + node.getWidth(), node.getY() + node.getHeight());
                Point p4 = new Point(node.getX(), node.getY() + node.getHeight());
                
                // use canvas units to draw the node
                p1 = camera.convertWorldToCanvas(p1);
                p2 = camera.convertWorldToCanvas(p2);
                p3 = camera.convertWorldToCanvas(p3);
                p4 = camera.convertWorldToCanvas(p4);
                
                g.drawLine((int)p1.getX(), (int)p1.getY(),(int)p2.getX(), (int)p2.getY());
                g.drawLine((int)p2.getX(), (int)p2.getY(),(int)p3.getX(), (int)p3.getY());
                g.drawLine((int)p3.getX(), (int)p3.getY(),(int)p4.getX(), (int)p4.getY());
                g.drawLine((int)p4.getX(), (int)p4.getY(),(int)p1.getX(), (int)p1.getY());
                break;
        }
    }
}
