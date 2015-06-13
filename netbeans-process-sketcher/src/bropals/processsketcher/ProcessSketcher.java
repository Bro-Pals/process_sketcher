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
package bropals.processsketcher;

import bropals.processsketcher.data.BinaryUtil;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 * The main class.
 * @author Jonathon
 */
public class ProcessSketcher {

    public static Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FlowchartWindowManager fwm = new FlowchartWindowManager();
    }
    
}
