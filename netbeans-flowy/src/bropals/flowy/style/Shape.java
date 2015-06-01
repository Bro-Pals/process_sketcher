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
                Point.Float p1 = new Point.Float(node.getX(), node.getY());
                Point.Float p2 = new Point.Float(node.getX() + node.getWidth(), node.getY());
                Point.Float p3 = new Point.Float(node.getX() + node.getWidth(), node.getY() + node.getHeight());
                Point.Float p4 = new Point.Float(node.getX(), node.getY() + node.getHeight());
                
                // use canvas units to draw the node
                Point _p1 = camera.convertWorldToCanvas(p1);
                Point _p2 = camera.convertWorldToCanvas(p2);
                Point _p3 = camera.convertWorldToCanvas(p3);
                Point _p4 = camera.convertWorldToCanvas(p4);
                
                g.drawLine((int)_p1.getX(), (int)_p1.getY(),(int)_p2.getX(), (int)_p2.getY());
                g.drawLine((int)_p2.getX(), (int)_p2.getY(),(int)_p3.getX(), (int)_p3.getY());
                g.drawLine((int)_p3.getX(), (int)_p3.getY(),(int)_p4.getX(), (int)_p4.getY());
                g.drawLine((int)_p4.getX(), (int)_p4.getY(),(int)_p1.getX(), (int)_p1.getY());
                break;
        }
    }
}
