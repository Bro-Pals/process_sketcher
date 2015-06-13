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
import bropals.processsketcher.action.style.EditedBorderColor;
import bropals.processsketcher.data.Node;
import bropals.processsketcher.data.Selectable;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JColorChooser;

/**
 *
 * @author Jonathon
 */
public class BorderColorListener extends AbstractProcessSketcherListener implements ActionListener {

    public BorderColorListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton borderColor = getFlowchartWindow().getBorderColorButton();
        borderColor.setBackground(JColorChooser.showDialog(getFlowchartWindow(), "Pick the border color", borderColor.getBackground()));
        
        ArrayList<Selectable> changedSelectables = new ArrayList<>();
        ArrayList<Color> oldValues = new ArrayList<>();
        Color colorToChangeTo = borderColor.getBackground();
        
        for (Node n : getSelectedNodes()) {
            if (!n.getStyle().getBorderColor().equals(colorToChangeTo)) {
                n.unlink();
                getFlowchartWindow().deselectLinkedNodeStyle();
                changedSelectables.add(n);
                oldValues.add(n.getStyle().getBorderColor());
                n.getStyle().setBorderColor(colorToChangeTo);
            }
        }
        
         // record it to the history if anything changed
        if (!changedSelectables.isEmpty()) {
            getFlowchartWindow().getEventManager().getHistoryManager().addToHistory(new EditedBorderColor(changedSelectables, oldValues));
        }
        
        getFlowchartWindow().redrawView();
    }
    
}
