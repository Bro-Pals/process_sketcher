/*
 * Flowy is a simple flowchart making software.
 * Copyright (C) 2015  Jonathon Prehn, Kevin Prehn
 * 
 * This file is a part of Flowy.
 * 
 * Flowy is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Flowy is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Flowy.  If not, see <http://www.gnu.org/licenses/>.
 */
package bropals.flowy.listeners;

import bropals.flowy.FlowchartWindow;
import bropals.flowy.data.NodeLine;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 * Listener for saving a line style.
 * 
 * @author Jonathon
 */
public class SaveLineStylesListener extends AbstractFlowyListener implements ActionListener {

    public SaveLineStylesListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NodeLine n = (NodeLine)getLastSelected();
        String name = JOptionPane.showInputDialog("Name this style", "style name");
        if (getFlowchartWindow().getStyleManager().isValidLineStyle(name, n.getStyle(), getFlowchartWindow())) {
            getFlowchartWindow().getStyleManager().saveLineStyle(name, n.getStyle());
            getFlowchartWindow().refreshLineStyleList();
        }
    }
}
