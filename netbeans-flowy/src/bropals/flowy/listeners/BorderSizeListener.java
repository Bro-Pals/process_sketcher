/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.listeners;

import bropals.flowy.FlowchartWindow;
import bropals.flowy.data.Node;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Jonathon
 */
public class BorderSizeListener extends AbstractFlowyListener implements ChangeListener {

    public BorderSizeListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int value = (Integer)getFlowchartWindow().getBorderSizeSpinner().getValue();
        if (value > 0) {
            Node n = (Node)getLastSelected();
            n.getStyle().setBorderSize(value);
            getFlowchartWindow().redrawView();
        } else {
            getFlowchartWindow().getBorderSizeSpinner().setValue(1);
        }
    }
    
}
