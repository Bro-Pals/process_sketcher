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
    
    public void renderLine(NodeLine n, Camera camera, Graphics g) {
        switch(this) {
            case SOLID:
                g.setColor(n.getStyle().getLineColor());
                Node par = n.getParent();
                Node chi = n.getChild();
                
                Point p1 = new Point(par.getX() + (int)((par.getWidth() / camera.getZoom())/2), 
                        par.getY() + (int)((par.getHeight() / camera.getZoom())/2));
                Point p2 = new Point(chi.getX() + (int)((chi.getWidth() / camera.getZoom())/2), 
                        chi.getY() + (int)((chi.getHeight() / camera.getZoom())/2));
                
                p1 = camera.convertWorldToCanvas(p1);
                p2 = camera.convertWorldToCanvas(p2);
                
                g.drawLine((int)p1.getX(), (int)p1.getY(), 
                        (int)p2.getX(), (int)p2.getY());
                break;
        }
    }
}
