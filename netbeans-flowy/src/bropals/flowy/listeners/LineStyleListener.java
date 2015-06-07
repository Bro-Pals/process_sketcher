/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.listeners;

import bropals.flowy.FlowchartWindow;
import bropals.flowy.data.NodeLine;
import bropals.flowy.style.LineType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Jonathon
 */
public class LineStyleListener extends AbstractFlowyListener implements ActionListener {

    public LineStyleListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NodeLine n = (NodeLine)getLastSelected();
        n.getStyle().setType(LineType.fromString((String)getFlowchartWindow().getLineTypeComboBox().getSelectedItem()));
        getFlowchartWindow().redrawView();
    }
    
}
