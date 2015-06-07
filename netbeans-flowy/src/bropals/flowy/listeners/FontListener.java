/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.listeners;

import bropals.flowy.FlowchartWindow;
import bropals.flowy.data.Selectable;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Jonathon
 */
public class FontListener extends AbstractFlowyListener implements ActionListener {

    public FontListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Selectable s = getLastSelected();
        s.getFontStyle().setFontType(((Font)getFlowchartWindow().getFontComboBox().getSelectedItem()));
        getFlowchartWindow().redrawView();
    }
    
}
