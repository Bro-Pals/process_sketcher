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
package bropals.flowy;

import bropals.flowy.data.Node;
import bropals.flowy.data.NodeLine;
import bropals.flowy.style.LineStyle;
import bropals.flowy.style.NodeStyle;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.JOptionPane;

/**
 * Manages the styles that are to be saved.
 * @author Jonathon
 */
public class StyleManager {

    private HashMap<String, NodeStyle> nodeStyles;
    private HashMap<String, LineStyle> lineStyles;
    
    public StyleManager() {
        nodeStyles = new HashMap<>();
        lineStyles = new HashMap<>();
    }
    
    /**
     * Save a node style to the style manager.
     * @param name the name to save the style as.
     * @param ns the node style.
     */
    public void saveNodeStyle(String name, NodeStyle ns) {
        nodeStyles.put(name, (NodeStyle)ns.clone());
    }
    
     /**
     * Save a line style to the style manager.
     * @param name the name to save the style as.
     * @param ls the line style.
     */
    public void saveLineStyle(String name, LineStyle ls) {
        lineStyles.put(name, (LineStyle)ls.clone());
    }
    
    /**
     * Removes a node style from the style manager.
     * @param name the name of the style to remove.
     */
    public void removeNodeStyle(String name) {
        nodeStyles.remove(name);
    }
    
    /**
     * Removes a line style from the style manager.
     * @param name the name of the style to remove.
     */
    public void removeLineStyle(String name) {
        lineStyles.remove(name);
    }
    
    /**
     * Lists the saved node styles as an array.
     * @return a list of the saved node styles.
     */
    public NodeStyle[] listNodeStyles() {
        Collection ns = nodeStyles.values();
        return (NodeStyle[])ns.toArray(new NodeStyle[0]);
    }
    
    /**
     * Lists the saved node style names as an array.
     * @return a list of the saved node style names.
     */
    public String[] listNodeStyleNames() {
        Collection ns = nodeStyles.keySet();
        return (String[])ns.toArray(new String[0]);
    }
    
    /**
     * Lists the saved line styles as an array.
     * @return a list of the saved line styles.
     */
    public LineStyle[] listLineStyles() {
        Collection ns = lineStyles.values();
        return (LineStyle[])ns.toArray(new LineStyle[0]);
    }
    
    /**
     * Lists the saved line style names as an array.
     * @return a list of the saved line style names.
     */
    public String[] listLineStyleNames() {
        Collection ns = lineStyles.keySet();
        return (String[])ns.toArray(new String[0]);
    }
    
    /**
     * Checks the name to see if it is OK to use.
     * @param name the name to test.
     * @param style the style that is to have that name.
     * @param window the flowchart window.
     * @return whether or not the name is OK to use.
     */
    public boolean isValidNodeStyle(String name, NodeStyle style, FlowchartWindow window) {
        if (name.equals("")) {
            JOptionPane.showMessageDialog(window, "Invalid name: can't be an empty string.", "Unable to save Node style", JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if (nodeStyles.containsKey(name)) {
            JOptionPane.showMessageDialog(window, "Invalid name: that name already exists.", "Unable to save Node style", JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if (nodeStyles.containsValue(style)) {
            JOptionPane.showMessageDialog(window, "Invalid style: that style already exists under the name " + getNameOf(style) + ".", "Unable to save Node style", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        return true;
    }
    
    /**
     * Checks the name to see if it is OK to use.
     * @param name the name to test.
     * @return whether or not the name is OK to use.
     */
    public boolean isValidLineStyle(String name, LineStyle style, FlowchartWindow window) {
        if (name.equals("")) {
            JOptionPane.showMessageDialog(window, "Invalid name: can't be an empty string.", "Unable to save Line style", JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if (lineStyles.containsKey(name)) {
            JOptionPane.showMessageDialog(window, "Invalid name: that name already exists.", "Unable to save Line style", JOptionPane.INFORMATION_MESSAGE);
            return false;
        } else if (lineStyles.containsValue(style)) {
            JOptionPane.showMessageDialog(window, "Invalid style: that style already exists under the name " + getNameOf(style) + ".", "Unable to save Line style", JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        return true;
    }
    
    /**
     * Get the name of the given node style.
     * @param style the node style to get the name of.
     * @return the name of the node style, or <code>null</code> if it is
     * not in the style manager.
     */
    public String getNameOf(NodeStyle style) {
        String[] styles = listNodeStyleNames();
        for (String styleName : styles) {
            if (nodeStyles.get(styleName).equals(style)) {
                return styleName;
            }
        }
        return null;
    }
    
    /**
     * Get the name of the given line style.
     * @param style the line style to get the name of.
     * @return the name of the line style, or <code>null</code> if it is
     * not in the style manager.
     */
    public String getNameOf(LineStyle style) {
        String[] styles = listLineStyleNames();
        for (String styleName : styles) {
            if (lineStyles.get(styleName).equals(style)) {
                return styleName;
            }
        }
        return null;
    }
    
    /**
     * Checks for the existence of a given node style.
     * @param name the name of the style to check
     * @return if the node style exists.
     */
    public boolean hasNodeStyle(String name) {
        return nodeStyles.containsKey(name);
    }
    
    /**
     * Checks for the existence of a given line style.
     * @param name the name of the style to check
     * @return if the line style exists.
     */
    public boolean hasLineStyle(String name) {
        return lineStyles.containsKey(name);
    }
    
    /**
     * Assigns a style to a node.
     * @param style the style to assign
     * @param node the node to assign it to.
     */
    public void assignStyle(String style, Node node) {
        node.assignStyle(style);
        node.setStyle(nodeStyles.get(style));
    }
    
    /**
     * Assigns a style to a node line.
     * @param style the style to assign
     * @param nodeLine the node line to assign it to.
     */
    public void assignStyle(String style, NodeLine nodeLine) {
        nodeLine.assignStyle(style);
        nodeLine.setStyle(lineStyles.get(style));
    }
}
