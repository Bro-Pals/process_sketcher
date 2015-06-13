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
import bropals.processsketcher.data.Selectable;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * The listener for the font chooser.
 * @author Jonathon
 */
public class FontListener extends AbstractProcessSketcherListener implements ActionListener {

    public FontListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<Selectable> changedSelectables = new ArrayList<>();
        ArrayList<Font> oldValues = new ArrayList<>();
        Font fontToChangeTo = (Font)getFlowchartWindow().getFontComboBox().getSelectedItem();
        for (Selectable s : getSelected()) {
            if (!s.getFontStyle().getFontType().equals(fontToChangeTo)) {
                s.unlink();
                getFlowchartWindow().deselectLinkedNodeStyle();
                getFlowchartWindow().deselectLinkedLineStyle();
                changedSelectables.add(s);
                oldValues.add(s.getFontStyle().getFontType());
                s.getFontStyle().setFontType(fontToChangeTo);
            }
        }
        // record it to the history if anything changed
        if (!changedSelectables.isEmpty()) {
            getFlowchartWindow().getEventManager().getHistoryManager().addToHistory(new EditedFontType(changedSelectables, oldValues));
        }
        
        getFlowchartWindow().redrawView();
    }
    
}
