/*
 * Flowy is a simple flowchart making software.
 * Copyright (C) 2015  Jonathon Prehn, Kevin Prehn
 * 
 * This file is a part of Flowy.
 *
 * Flowy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Flowy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Flowy.  If not, see <http://www.gnu.org/licenses/>.
 */
package bropals.flowy.listeners;

import bropals.flowy.FlowchartWindow;
import bropals.flowy.data.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The listener for the create shape button.
 * @author Jonathon
 */
public class CreateShapeListener extends AbstractFlowyListener implements ActionListener {

    public CreateShapeListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Creating shape");
        
        // add a shape, placing it in the center of the screen
        Node node = new Node(0, 0);
        getFlowchartWindow().getFlowchart().getNodes().add(node);
        
        // get the center of the screen in world coordinates
        float centerX = getFlowchartWindow().getCamera().convertCanvasToWorldX(
                getFlowchartWindow().getView().getWidth()/2);
        float centerY = getFlowchartWindow().getCamera().convertCanvasToWorldY(
                getFlowchartWindow().getView().getHeight()/2);
        
        // position the node to the center
        node.setX(centerX - (node.getWidth()/2));
        node.setY(centerY - (node.getHeight()/2));
        
        // redraw the view
        getFlowchartWindow().redrawView();
    }
    
}
