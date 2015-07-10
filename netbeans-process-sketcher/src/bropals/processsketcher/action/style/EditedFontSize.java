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
package bropals.processsketcher.action.style;

import bropals.processsketcher.FlowchartWindow;
import bropals.processsketcher.data.Selectable;
import java.util.ArrayList;

/**
 * An action that occurs when the font size is changed
 * @author Kevin
 */
public class EditedFontSize extends EditedStyle {

    private ArrayList<Integer> sizes;
    
    public EditedFontSize(ArrayList<Selectable> editedSelectables, ArrayList<Integer> oldSizes) {
        super(editedSelectables);
        sizes = oldSizes;
    }

    @Override
    public void undo(FlowchartWindow instance) {
        //System.out.println("Undo editing the font size");
        ArrayList<Selectable> things = getSelectables();
        for (int i=0; i<things.size(); i++) {
            things.get(i).getFontStyle().setFontSize(sizes.get(i));
        }
    }
    
    
}
