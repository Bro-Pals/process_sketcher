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
import bropals.flowy.style.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The listener for the shape chooser in the styles tab.
 * @author Jonathon
 */
public class ShapeListener extends AbstractFlowyListener implements ActionListener {

    public ShapeListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (getFlowchartWindow().getShapeComboBox().getSelectedIndex() != -1) {
            for (Node n : getSelectedNodes()) {
                n.getStyle().setShape(Shape.fromString((String)getFlowchartWindow().getShapeComboBox().getSelectedItem()));
            }
            getFlowchartWindow().redrawView();
        }
    }
    
}
