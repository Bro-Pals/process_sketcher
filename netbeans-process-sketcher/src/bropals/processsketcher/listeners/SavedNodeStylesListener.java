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
import bropals.processsketcher.action.style.EditedNodeStyle;
import bropals.processsketcher.data.Node;
import bropals.processsketcher.data.Selectable;
import bropals.processsketcher.style.NodeStyle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Listener for when an option is selected in the list of saved nodes.
 * @author Jonathon
 */
public class SavedNodeStylesListener extends AbstractProcessSketcherListener implements ActionListener {

    public SavedNodeStylesListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String styleName = (String)getFlowchartWindow().getSavedNodeStylesComboBox().getSelectedItem();
        
        if (styleName != null) {
            ArrayList<Selectable> selectablesChanged = new ArrayList<>();
            selectablesChanged.addAll(getSelectedNodes());
            NodeStyle styleToChangeTo = getFlowchartWindow().getStyleManager().getNodeStyle(styleName);
            ArrayList<NodeStyle> oldStyles = new ArrayList<>();
            
            for (Node n : getSelectedNodes()) {
                if (!n.getStyle().equals(styleToChangeTo)) {
                    selectablesChanged.add(n);
                    oldStyles.add(n.getStyle());
                    getFlowchartWindow().getStyleManager().assignStyle(styleName, n);
                }
            }
            
            if (!selectablesChanged.isEmpty()) {
                getFlowchartWindow().getEventManager().getHistoryManager().addToHistory(new EditedNodeStyle(selectablesChanged, oldStyles));
            }
            
            getFlowchartWindow().redrawView();
        }
    }
}
