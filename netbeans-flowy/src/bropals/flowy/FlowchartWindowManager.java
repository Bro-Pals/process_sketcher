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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * Keeps track of and manages all open FlowchartWindows.
 * @author Jonathon
 */
public class FlowchartWindowManager implements WindowListener {
    
    /**
     * A list of all the windows that are visible and can be edited
     */
    private ArrayList<FlowchartWindow> windows;
    /**
     * The welcome window that intially pops up.
     */
    private JFrame welcomeWindow;
    /**
     * How many flowcharts that have been opened this session. 
     * The actual number will be one less than this value.
     */
    private int chartCounter = 1;
    
    /**
     * Creates a new window manager and opens a welcome window.
     */
    public FlowchartWindowManager() {
        windows = new ArrayList<>();
        welcomeWindow = new JFrame("Welcome to Flowy");
        welcomeWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton newFlowchart = new JButton("New Flowchart");
        welcomeWindow.add(newFlowchart);
        newFlowchart.addActionListener(new NewFlowchartListener());
        welcomeWindow.setSize(400, 300);
        welcomeWindow.setLocationRelativeTo(null);
        welcomeWindow.setVisible(true);
    }
    
    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        FlowchartWindow window = (FlowchartWindow)e.getWindow();
        
        //Be sure to handle saving before closing here
        
        windows.remove(window);
        window.dispose();
    }

    @Override
    public void windowClosed(WindowEvent e) {
        if (windows.isEmpty()) {
            welcomeWindow.setVisible(true);
        }
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
    
    /**
     * Creates a new flowchart.
     */
    public void newFlowchart() {
        FlowchartWindow window = new FlowchartWindow(this);
        window.setTitle("Flowy | Untitled" + chartCounter);
        chartCounter++;
        addToStack(window);
    }
    
    /**
     * Opens a flowchart file.
     * @param stream the input stream to read the flowchart data from.
     * @param name the name of the chart to open.
     * @param file the file the flowchart came from, if any.
     */
    public void openFlowchart(InputStream stream, String name, File file) {
        FlowchartWindow window = new FlowchartWindow(this, stream, file);
        window.setTitle("Flowy | " + name);
        addToStack(window);
    }
    
    private void addToStack(FlowchartWindow window) {
        window.addWindowListener(this);
        windows.add(window);
        welcomeWindow.setVisible(false);
        window.setLocationRelativeTo(null);
        window.setVisible(true);
    }
    
    /**
     * The action listener for the welcome window's button.
     */
    class NewFlowchartListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            newFlowchart();
        }
    }
}
