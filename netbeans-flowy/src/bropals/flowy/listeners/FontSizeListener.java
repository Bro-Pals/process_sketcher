/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.listeners;

import bropals.flowy.FlowchartWindow;
import bropals.flowy.data.Selectable;
import java.awt.Font;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author Jonathon
 */
public class FontSizeListener extends AbstractFlowyListener implements ChangeListener {

    public FontSizeListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int value = (Integer)getFlowchartWindow().getFontSizeSpinner().getValue();
        if (value > 0) {
            Selectable s = getLastSelected();
            s.getFontStyle().setFontSize(value);
            getFlowchartWindow().redrawView();
        } else {
            getFlowchartWindow().getFontSizeSpinner().setValue(1);
        }
    }
    
}
