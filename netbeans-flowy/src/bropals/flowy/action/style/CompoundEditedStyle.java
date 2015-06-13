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
import bropals.flowy.action.Action;

/**
 * An action that occurs when multiple style properties are changed in one action. 
 * Used when a saved style has been applied to a node or node line
 * @author Kevin
 */
public class CompoundEditedStyle extends Action {

    EditedStyle[] styleEdits;
    
    public CompoundEditedStyle(EditedStyle[] styleActions) {
        styleEdits = styleActions;
    }
    
    @Override
    public void undo(FlowchartWindow instance) {
        System.out.println("Undoing many style in one action");
        for (EditedStyle es : styleEdits) {
            es.undo(instance);
        }
        System.out.println("Finished undoing many styles");
    }
    
}
