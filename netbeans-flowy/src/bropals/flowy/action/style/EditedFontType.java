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
package bropals.flowy.action.style;

import bropals.flowy.FlowchartWindow;
import bropals.flowy.data.Selectable;
import java.awt.Font;
import java.util.ArrayList;

/**
 * An action that occurs when the font type is edited.
 * @author Kevin
 */
public class EditedFontType extends EditedStyle {

    private ArrayList<Font> fonts;
    
    public EditedFontType(ArrayList<Selectable> editedSelectables, ArrayList<Font> oldFonts) {
        super(editedSelectables);
        fonts = oldFonts;
    }

    @Override
    public void undo(FlowchartWindow instance) {
        System.out.println("Undo editing font type");
    }
    
}
