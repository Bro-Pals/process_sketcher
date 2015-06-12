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
import bropals.flowy.action.style.EditedFontColor;
import bropals.flowy.data.Selectable;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JColorChooser;

/**
 * The listener for the font color button.
 * @author Jonathon
 */
public class FontColorListener extends AbstractFlowyListener implements ActionListener {

    public FontColorListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton fontColor = getFlowchartWindow().getFontColorButton();
        fontColor.setBackground(JColorChooser.showDialog(getFlowchartWindow(), "Pick a font Color", fontColor.getBackground()));
        
        ArrayList<Selectable> changedSelectables = new ArrayList<>();
        ArrayList<Color> oldValues = new ArrayList<>();
        Color colorToChangeTo = fontColor.getBackground();
        
        //Use the color chooser to change the color of the button
        for (Selectable s : getSelected()) {
            if (!s.getFontStyle().getFontColor().equals(colorToChangeTo)) {
                changedSelectables.add(s);
                oldValues.add(s.getFontStyle().getFontColor());
                s.getFontStyle().setFontColor(colorToChangeTo);
            }
        }
        
         // record it to the history if anything changed
        if (!changedSelectables.isEmpty()) {
            getFlowchartWindow().getEventManager().getHistoryManager().addToHistory(new EditedFontColor(changedSelectables, oldValues));
        }
        
        getFlowchartWindow().redrawView();
    }
    
}
