/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.listeners;

import bropals.flowy.FlowchartWindow;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The listener for the paste clipboard button.
 * @author Jonathon
 */
public class PasteListener extends AbstractFlowyListener implements ActionListener {

    public PasteListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getFlowchartWindow().getEventManager().pasteClipboard();
        getFlowchartWindow().redrawView();
    }
    
}
