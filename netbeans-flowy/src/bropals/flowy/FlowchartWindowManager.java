/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
     */
    public void openFlowchart(InputStream stream, String name) {
        FlowchartWindow window = new FlowchartWindow(this, stream);
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
