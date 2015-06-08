/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.listeners;

import bropals.flowy.FlowchartWindow;
import bropals.flowy.data.Selectable;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;

/**
 * The listener for the font color button.
 * @author Jonathon
 */
public class FontColorListener extends AbstractFlowyListener implements ActionListener {

    public FontColorListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Selectable s = getLastSelected();
        JButton fontColor = getFlowchartWindow().getFontColorButton();
        fontColor.setBackground(JColorChooser.showDialog(getFlowchartWindow(), "Pick a font Color", s.getFontStyle().getFontColor()));
        //Use the color chooser to change the color of the button
        s.getFontStyle().setFontColor(fontColor.getBackground());
        getFlowchartWindow().redrawView();
    }
    
}
