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
                g.drawLine(par.getX() + (par.getWidth()/2), 
                        par.getY() + (par.getHeight()/2), 
                        chi.getX() + (chi.getWidth()/2), 
                        chi.getY() + (chi.getHeight()/2));
                break;
        }
    }
}
