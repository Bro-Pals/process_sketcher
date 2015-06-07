/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.listeners;

import bropals.flowy.FlowchartWindow;
import bropals.flowy.data.Node;
import bropals.flowy.style.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Jonathon
 */
public class ShapeListener extends AbstractFlowyListener implements ActionListener {

    public ShapeListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Node n = (Node)getLastSelected();
        n.getStyle().setShape(Shape.fromString((String)getFlowchartWindow().getShapeComboBox().getSelectedItem()));
        getFlowchartWindow().redrawView();
    }
    
}
