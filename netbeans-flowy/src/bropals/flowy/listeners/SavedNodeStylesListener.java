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
import bropals.flowy.data.Node;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Listener for when an option is selected in the list of saved nodes.
 * @author Jonathon
 */
public class SavedNodeStylesListener extends AbstractFlowyListener implements ActionListener {

    public SavedNodeStylesListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String styleName = (String)getFlowchartWindow().getSavedNodeStylesComboBox().getSelectedItem();
        if (styleName != null) {
            for (Node n : getSelectedNodes()) {
                getFlowchartWindow().getStyleManager().assignStyle(styleName, n);
            }
            getFlowchartWindow().redrawView();
        }
    }
}
