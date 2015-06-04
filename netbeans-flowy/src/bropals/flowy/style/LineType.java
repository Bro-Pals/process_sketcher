/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.style;

import bropals.flowy.Camera;
import bropals.flowy.data.Node;
import bropals.flowy.data.NodeLine;
import java.awt.Graphics;
import java.awt.Point;

/**
 *
 * @author Kevin
 */
public enum LineType {
    SOLID, DASHED, DOTTED;
    
    /**
     * Renders this line tpe, changing the color of the graphics to match the style.
     * @param n The node line to render
     * @param camera The camera in the flowchart
     * @param g The graphics to draw it to
     * @return An array of two points of integers, representing the start and end point of
     *      the line in canvas units. 
     */
    public Point[] renderLine(NodeLine n, Camera camera, Graphics g) {
        Node par = n.getParent();
        Node chi = n.getChild();

        /*
        Point.Float p1 = new Point.Float((par.getX() + ((par.getWidth() / camera.getZoom())/2)), 
                par.getY() + ((par.getHeight() / camera.getZoom())/2));
        Point.Float p2 = new Point.Float(chi.getX() + ((chi.getWidth() / camera.getZoom())/2), 
                chi.getY() + ((chi.getHeight() / camera.getZoom())/2));
        */
        
        // child's point
        Point.Float cp = new Point.Float();
        // parent's point
        Point.Float pp = new Point.Float();
        
        // if the child is down and to the right
        if (par.getX() >= chi.getX() && par.getY() >= chi.getY()) {
            cp.setLocation(chi.getX() + chi.getWidth(), chi.getY() + chi.getHeight());
            pp.setLocation(par.getX(), par.getY());
        // if the child is up and to the right
        } else if (par.getX() >= chi.getX() && par.getY() < chi.getY()) {
            cp.setLocation(chi.getX() + chi.getWidth(), chi.getY());
            pp.setLocation(par.getX(), par.getY() + par.getHeight());
        // if the child is up and to the left
        } else if (par.getX() < chi.getX() && par.getY() < chi.getY()) {
            cp.setLocation(chi.getX(), chi.getY());
            pp.setLocation(par.getX() + par.getWidth(), par.getY() + par.getHeight());
        // if the child is down and to the left (no conditional needed)
        } else {
            cp.setLocation(chi.getX(), chi.getY() + chi.getHeight());
            pp.setLocation(par.getX() + par.getWidth(), par.getY());
        }
        
        Point int_p1 = camera.convertWorldToCanvas(pp);
        Point int_p2 = camera.convertWorldToCanvas(cp);
        
        // create a vector for the direction of the line
        float diffX = (float)(cp.getX() - pp.getX());
        float diffY = (float)(cp.getY() - pp.getY());
        float lineLength = (float)Math.sqrt((diffX * diffX) + (diffY * diffY));
        diffX = diffX / lineLength;
        diffY = diffY / lineLength;
        
        float tailDist = 0.20f; // 20% of the length
        float centerDist = 0.5f; // 50% of the length
        float headDist = 0.8f; // 80% of the length
        
        switch(this) {
            case SOLID:
                g.setColor(n.getStyle().getLineColor());
                g.drawLine((int)int_p1.getX(),(int)int_p1.getY(), 
                        (int)int_p2.getX(), (int)int_p2.getY());
                break;
        }
        
        // set the font for drawing the font
        g.setFont(n.getStyle().getFontType());
        g.setColor(n.getStyle().getFontColor());
        
        // draw the tail, center, and head texts
        
        if (n.getTailText().length() > 0) {
            g.drawString(n.getTailText(), 
                (int)camera.convertWorldToCanvasX((float)pp.getX() + (tailDist * diffX * lineLength)), 
                (int)camera.convertWorldToCanvasY((float)pp.getY() + (tailDist * diffY * lineLength)));
        }
        
        if (n.getCenterText().length() > 0) {
            g.drawString(n.getCenterText(), 
                (int)camera.convertWorldToCanvasX((float)pp.getX() + (centerDist * diffX * lineLength)), 
                (int)camera.convertWorldToCanvasY((float)pp.getY() + (centerDist * diffY * lineLength)));
        }
        
        if (n.getHeadText().length() > 0) {
            g.drawString(n.getHeadText(), 
                (int)camera.convertWorldToCanvasX((float)pp.getX() + (headDist * diffX * lineLength)), 
                (int)camera.convertWorldToCanvasY((float)pp.getY() + (headDist * diffY * lineLength)));
        }
        
        return new Point[]{int_p1, int_p2};
    }
}
