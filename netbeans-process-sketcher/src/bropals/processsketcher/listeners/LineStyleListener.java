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
import bropals.processsketcher.action.style.EditedLineType;
import bropals.processsketcher.data.NodeLine;
import bropals.processsketcher.data.Selectable;
import bropals.processsketcher.style.LineType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * The listener for the line style chooser.
 * @author Jonathon
 */
public class LineStyleListener extends AbstractProcessSketcherListener implements ActionListener {

    public LineStyleListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (getFlowchartWindow().getLineTypeComboBox().getSelectedIndex() != -1) {
            ArrayList<Selectable> changedSelectables = new ArrayList<>();
            ArrayList<LineType> oldTypes = new ArrayList<>();
            LineType typeToChangeTo = LineType.fromString((String)getFlowchartWindow().getLineTypeComboBox().getSelectedItem());
            for (NodeLine n : getSelectedNodeLines()) {
                if (n.getStyle().getType() != typeToChangeTo) {
                    n.unlink();
                    getFlowchartWindow().deselectLinkedLineStyle();
                    changedSelectables.add(n);
                    oldTypes.add(n.getStyle().getType());
                    n.getStyle().setType(typeToChangeTo);
                }
            }
            // add to history if anything changed
            if (!changedSelectables.isEmpty()) {
                getFlowchartWindow().getEventManager().getHistoryManager().addToHistory(new EditedLineType(changedSelectables, oldTypes));
            }
            
            getFlowchartWindow().redrawView();
        }
    }
    
}
