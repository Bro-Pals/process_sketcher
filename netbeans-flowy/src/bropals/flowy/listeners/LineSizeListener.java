/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.listeners;

import bropals.flowy.FlowchartWindow;
import bropals.flowy.data.NodeLine;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The listener for the line size spinner.
 * @author Jonathon
 */
public class LineSizeListener extends AbstractFlowyListener implements ChangeListener {

    public LineSizeListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int value = (Integer)getFlowchartWindow().getLineSizeSpinner().getValue();
        if (value > 0) {
            NodeLine n = (NodeLine)getLastSelected();
            n.getStyle().setLineSize(value);
            getFlowchartWindow().redrawView();
        } else {
            getFlowchartWindow().getLineSizeSpinner().setValue(1);
        }
    }
    
}
