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
import bropals.flowy.action.style.EditedFontSize;
import bropals.flowy.data.Selectable;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The listener for the font size spinner.
 * @author Jonathon
 */
public class FontSizeListener extends AbstractFlowyListener implements ChangeListener {

    public FontSizeListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int value = (Integer)getFlowchartWindow().getFontSizeSpinner().getValue();
        if (value > 0) {
            ArrayList<Selectable> changedSelectables = new ArrayList<>();
            ArrayList<Integer> oldValues = new ArrayList<>();
            for (int i=0; i<getSelected().size(); i++) {
                // if the value changed then add it to the list of changed values
                if (getSelected().get(i).getFontStyle().getFontSize() != value) {
                    getSelected().get(i).unlink();
                    getFlowchartWindow().deselectLinkedNodeStyle();
                    getFlowchartWindow().deselectLinkedLineStyle();
                    changedSelectables.add(getSelected().get(i));
                    oldValues.add(getSelected().get(i).getFontStyle().getFontSize());
                    getSelected().get(i).getFontStyle().setFontSize(value);
                }
            }
            // record into history if the font size changed for any number of elements
            if (!changedSelectables.isEmpty()) {
                getFlowchartWindow().getEventManager().getHistoryManager().addToHistory(new EditedFontSize(changedSelectables, oldValues));
            }
            getFlowchartWindow().redrawView();
        } else {
            getFlowchartWindow().getFontSizeSpinner().setValue(1);
        }
    }
    
}
