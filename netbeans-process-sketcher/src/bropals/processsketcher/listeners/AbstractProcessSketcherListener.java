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
import bropals.processsketcher.data.Node;
import bropals.processsketcher.data.NodeLine;
import bropals.processsketcher.data.Selectable;
import java.util.ArrayList;

/**
 * The abstract listener for all GUI element listeners. Provides
 * convenience functions and a reference to the FlowchartWindow.
 * @author Jonathon
 */
public abstract class AbstractProcessSketcherListener {
    
    private FlowchartWindow flowchartWindow;
    
    /**
     * Constructs a listener with a reference to the flowchart window.
     * @param window the flowchart window.
     */
    public AbstractProcessSketcherListener(FlowchartWindow window) {
        flowchartWindow = window;
    }

    /**
     * Gets the flowchart window that this listener belongs to.
     * @return the flowchart window.
     */
    public FlowchartWindow getFlowchartWindow() {
        return flowchartWindow;
    }
    
    /**
     * Gets the last selected node. If there is none, then it returns 
     * <code>null</code>.
     * @return the last selected node.
     */
    public Selectable getLastSelected() {
        return getFlowchartWindow().getEventManager().getSelectionManager().getLastSelected();
    }
    
    /**
     * Gets the list of selected nodes.
     * @return the list of selected nodes.
     */
    public ArrayList<Node> getSelectedNodes() {
        return getFlowchartWindow().getEventManager().getSelectionManager().getSelectedNodes();
    }
    
    /**
     * Gets the list of selected node lines.
     * @return the list of selected node lines.
     */
    public ArrayList<NodeLine> getSelectedNodeLines() {
        return getFlowchartWindow().getEventManager().getSelectionManager().getSelectedNodeLines();
    }
    
    /**
     * Gets a list of all selected selectables.
     * @return a list of all selected selectables.
     */
    public ArrayList<Selectable> getSelected() {
        return getFlowchartWindow().getEventManager().getSelectionManager().getSelected();
    }
}
