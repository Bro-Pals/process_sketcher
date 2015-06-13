/*
 * Process Sketcher is a simple flowchart making software.
 * Copyright (C) 2015  Jonathon Prehn, Kevin Prehn
 * 
 * This file is a part of Process Sketcher.
 * 
 * Process Sketcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Process Sketcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Process Sketcher.  If not, see <http://www.gnu.org/licenses/>.
 */
package bropals.processsketcher.listeners;

import bropals.processsketcher.FlowchartWindow;
import bropals.processsketcher.data.NodeLine;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 * Listener for saving a line style.
 * 
 * @author Jonathon
 */
public class SaveLineStylesListener extends AbstractProcessSketcherListener implements ActionListener {

    public SaveLineStylesListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NodeLine n = (NodeLine)getSelectedNodeLines().get(0);
        String name = JOptionPane.showInputDialog("Name this style", "style name");
        if (name != null) {
            if (getFlowchartWindow().getStyleManager().isValidLineStyle(name, n.getStyle(), getFlowchartWindow())) {
                getFlowchartWindow().getStyleManager().saveLineStyle(name, n.getStyle());
                getFlowchartWindow().getStyleManager().assignStyle(name, n);
                getFlowchartWindow().refreshLineStyleList();
                getFlowchartWindow().getSavedLineStylesComboBox().setSelectedItem(name);
            }
        }
    }
}
