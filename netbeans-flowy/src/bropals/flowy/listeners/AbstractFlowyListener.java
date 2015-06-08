/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.listeners;

import bropals.flowy.FlowchartWindow;
import bropals.flowy.data.Selectable;

/**
 * The abstract listener for all GUI element listeners. Provides
 * convenience functions and a reference to the FlowchartWindow.
 * @author Jonathon
 */
public abstract class AbstractFlowyListener {
    
    private FlowchartWindow flowchartWindow;
    
    /**
     * Constructs a listener with a reference to the flowchart window.
     * @param window the flowchart window.
     */
    public AbstractFlowyListener(FlowchartWindow window) {
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
}
