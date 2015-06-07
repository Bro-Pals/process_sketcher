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
import javax.swing.JButton;
import javax.swing.JColorChooser;

/**
 *
 * @author Jonathon
 */
public class FillColorListener extends AbstractFlowyListener implements ActionListener {

    public FillColorListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Node n = (Node)getLastSelected();
        JButton borderColor = getFlowchartWindow().getFillColorButton();
        borderColor.setBackground(JColorChooser.showDialog(getFlowchartWindow(), "Pick the fill color", n.getStyle().getFillColor()));
        n.getStyle().setFillColor(borderColor.getBackground());
        getFlowchartWindow().redrawView();
    }
    
}
