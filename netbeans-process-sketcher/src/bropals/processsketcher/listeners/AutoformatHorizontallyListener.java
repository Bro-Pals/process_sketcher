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
import bropals.processsketcher.action.AutoFormatted;
import bropals.processsketcher.data.Node;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 *
 * @author Jonathon
 */
public class AutoformatHorizontallyListener extends AbstractProcessSketcherListener implements ActionListener {

    public AutoformatHorizontallyListener(FlowchartWindow window) {
        super(window);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ArrayList<Node> nodesChanged = new ArrayList<>();
        nodesChanged.addAll(getFlowchartWindow().getFlowchart().getNodes());
        
        ArrayList<Point> positions = new ArrayList<>();
        for (int i=0; i<nodesChanged.size(); i++) {
            positions.add(new Point((int)nodesChanged.get(i).getX(), (int)nodesChanged.get(i).getY()));
        }
        getFlowchartWindow().getEventManager().getHistoryManager().addToHistory(new AutoFormatted(nodesChanged, positions));
        
        getFlowchartWindow().autoformatHorizontally();
    }
    
}
