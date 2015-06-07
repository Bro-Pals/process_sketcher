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
public class BorderColorListener extends AbstractFlowyListener implements ActionListener {

    public BorderColorListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Node n = (Node)getLastSelected();
        JButton borderColor = getFlowchartWindow().getBorderColorButton();
        borderColor.setBackground(JColorChooser.showDialog(getFlowchartWindow(), "Pick the border color", n.getStyle().getBorderColor()));
        n.getStyle().setBorderColor(borderColor.getBackground());
        getFlowchartWindow().redrawView();
    }
    
}
