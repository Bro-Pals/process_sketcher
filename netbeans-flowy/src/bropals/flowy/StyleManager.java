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

import bropals.flowy.style.LineStyle;
import bropals.flowy.style.NodeStyle;
import java.util.Collection;
import java.util.HashMap;

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
}
