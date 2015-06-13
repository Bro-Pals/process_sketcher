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
import bropals.processsketcher.action.style.EditedFontType;
import bropals.processsketcher.action.style.EditedShape;
import bropals.processsketcher.data.Node;
import bropals.processsketcher.data.Selectable;
import bropals.processsketcher.style.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * The listener for the shape chooser in the styles tab.
 * @author Jonathon
 */
public class ShapeListener extends AbstractProcessSketcherListener implements ActionListener {

    public ShapeListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<Selectable> changedSelectables = new ArrayList<>();
        ArrayList<Shape> oldValues = new ArrayList<>();
        String shapeStr = (String)getFlowchartWindow().getShapeComboBox().getSelectedItem();
        if (shapeStr != null) {
            Shape shapeToChangeTo = Shape.fromString(shapeStr);

            if (getFlowchartWindow().getShapeComboBox().getSelectedIndex() != -1) {
                for (Node n : getSelectedNodes()) {
                    if (n.getStyle().getShape() != shapeToChangeTo) {
                        n.unlink();
                        getFlowchartWindow().deselectLinkedNodeStyle();
                        changedSelectables.add(n);
                        oldValues.add(n.getStyle().getShape());
                        n.getStyle().setShape(shapeToChangeTo);
                    }
                }
                // record it to the history if anything changed
                if (!changedSelectables.isEmpty()) {
                    getFlowchartWindow().getEventManager().getHistoryManager().addToHistory(new EditedShape(changedSelectables, oldValues));
                }

                getFlowchartWindow().redrawView();
            }
        }
    }
    
}
