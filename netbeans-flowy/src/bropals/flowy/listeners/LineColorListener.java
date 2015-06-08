/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.listeners;

import bropals.flowy.FlowchartWindow;
import bropals.flowy.data.NodeLine;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;

/**
 * The listener for the line color button.
 * @author Jonathon
 */
public class LineColorListener extends AbstractFlowyListener implements ActionListener {

    public LineColorListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NodeLine n = (NodeLine)getLastSelected();
        JButton lineColor = getFlowchartWindow().getLineColorButton();
        lineColor.setBackground(JColorChooser.showDialog(getFlowchartWindow(), "Pick the line color", n.getStyle().getLineColor()));
        n.getStyle().setLineColor(lineColor.getBackground());
        getFlowchartWindow().redrawView();
    }
    
}
