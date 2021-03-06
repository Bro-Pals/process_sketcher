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
import bropals.processsketcher.data.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 * Listener for saving a node style.
 * @author Jonathon
 */
public class SaveNodeStylesListener extends AbstractProcessSketcherListener implements ActionListener {

    public SaveNodeStylesListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Node n = (Node)getSelectedNodes().get(0);
        String name = JOptionPane.showInputDialog("Name this style", "style name");
        if (name != null) {
            if (getFlowchartWindow().getStyleManager().isValidNodeStyle(name, n.getStyle(), getFlowchartWindow())) {
                getFlowchartWindow().getStyleManager().saveNodeStyle(name, n.getStyle());
                getFlowchartWindow().getStyleManager().assignStyle(name, n);
                getFlowchartWindow().refreshNodeStyleList();
                getFlowchartWindow().getSavedNodeStylesComboBox().setSelectedItem(name);
            }
        }
    }
}
