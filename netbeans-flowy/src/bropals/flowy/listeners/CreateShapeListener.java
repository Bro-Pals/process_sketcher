/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.listeners;

import bropals.flowy.FlowchartWindow;
import bropals.flowy.data.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
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
        int centerX = getFlowchartWindow().getCamera().convertCanvasToWorldX(
                getFlowchartWindow().getView().getWidth()/2);
        int centerY = getFlowchartWindow().getCamera().convertCanvasToWorldY(
                getFlowchartWindow().getView().getHeight()/2);
        
        // position the node to the center
        node.setX(centerX - (node.getWidth()/2));
        node.setY(centerY - (node.getHeight()/2));
        
        // redraw the view
        getFlowchartWindow().redrawView();
    }
    
}
