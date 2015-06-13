/*
 * Process Sketcher is a simple flowchart making software.
 * Copyright (C) 2015  Jonathon Prehn, Kevin Prehn
 * 
 * This file is a part of Process Sketcher.
 *
 * Process Sketcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Process Sketcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Process Sketcher.  If not, see <http://www.gnu.org/licenses/>.
 */
package bropals.processsketcher.listeners;

import bropals.processsketcher.FlowchartWindow;
import bropals.processsketcher.data.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * The listener for the fit to view button.
 * @author Jonathon
 */
public class FitToViewListener extends AbstractProcessSketcherListener implements ActionListener {

    public FitToViewListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<Node> nodes = getFlowchartWindow().getFlowchart().getNodes();
        // find the bounds of the entire flowchart in world units
        float smallestX = nodes.get(0).getX(); // world units
        float smallestY = nodes.get(0).getY(); // world units
        float largestX = nodes.get(0).getX() + nodes.get(0).getWidth(); // world units
        float largestY = nodes.get(0).getY() + nodes.get(0).getHeight(); // world units
        
        // search through all the nodes, finding the smallest and largest bounds...
        for (int i=1; i<nodes.size(); i++) {
            if (nodes.get(i).getX() < smallestX) {
                smallestX = nodes.get(i).getX();
            }
            
            if (nodes.get(i).getY() < smallestY) {
                smallestY = nodes.get(i).getY();
            }
            
            if (nodes.get(i).getX() + nodes.get(i).getWidth() > largestX) {
                largestX = nodes.get(i).getX() + nodes.get(i).getWidth();
            }
            
            if (nodes.get(i).getY() + nodes.get(i).getHeight() > largestY) {
                largestY = nodes.get(i).getY() + nodes.get(i).getHeight();
            }
        }
        
        float padding = 30; // world units of padding
        // add the padding to the bounds
        smallestX = smallestX - padding;
        smallestY = smallestY - padding;
        largestX = largestX + padding;
        largestY = largestY + padding;
        
        int sizeToFitTo = 0;
        int translateX = 0;
        int translateY = 0;
        if (getFlowchartWindow().getView().getWidth() > getFlowchartWindow().getView().getHeight()) {
            sizeToFitTo = getFlowchartWindow().getView().getHeight();
            translateX = (sizeToFitTo - getFlowchartWindow().getView().getWidth())/2;
        } else {
            sizeToFitTo = getFlowchartWindow().getView().getWidth();
            translateY = (sizeToFitTo - getFlowchartWindow().getView().getHeight())/2;
        }
        
        getFlowchartWindow().getCamera().setWorldLocationX(smallestX);
        getFlowchartWindow().getCamera().setWorldLocationY(smallestY);
        if (largestX - smallestX > largestY - smallestY) {
            getFlowchartWindow().getCamera().setZoom((largestX - smallestX + (padding * 3)) / sizeToFitTo);
        } else {
            getFlowchartWindow().getCamera().setZoom((largestY - smallestY + (padding * 3)) / sizeToFitTo);
        }

        // if translate X is still 0... center the flowchart to the window on the x axis
        if (translateX == 0) {
            translateX =  (int)((largestX - smallestX) / getFlowchartWindow().getCamera().getZoom()) - sizeToFitTo;
            translateX = translateX / 2;
        } else {
            translateY = (int)((largestY - smallestY) / getFlowchartWindow().getCamera().getZoom()) - sizeToFitTo;
            translateY = translateY / 2;
        }
        
        getFlowchartWindow().getCamera().setCanvasLocationX(
                getFlowchartWindow().getCamera().getCanvasLocationX() + 
                        (translateX));
        
        getFlowchartWindow().getCamera().setCanvasLocationY(
                getFlowchartWindow().getCamera().getCanvasLocationY() +
                        (translateY));
        
        System.out.println("New zoom: " + getFlowchartWindow().getCamera().getZoom());
        
        getFlowchartWindow().redrawView();
    }
    
}