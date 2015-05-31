/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy;

import bropals.flowy.data.Flowchart;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Used for editing a Flowchart.
 * @author Jonathon
 */
public class FlowchartWindow extends JFrame {
    
    private Flowchart flowchart;
    private EventManager eventManager;
    private JPanel buttonPanel;
    private JComponent view;
    private Camera camera;
    /*
        The mudball of GUI elements
    */
    private JButton createShape;
    private JButton selectNextNode;
    private JButton selectPreviousNode;
    
    private JButton undo;
    private JButton resetView;
    private JButton fitToView;
    private JButton zoomIn;
    private JButton zoomOut;
    private JButton copy;
    private JButton cut;
    private JButton paste;
    private JLabel nodesSelectedCounter;
    private JButton newFlowchart;
    private JButton saveFlowchart;
    private JButton saveAsFlowchart;
    private JButton openFlowchart;
    private JButton printFlowchart;
    private JButton exportChartToPDF;
    private JButton exportChartToImage;
    private JButton autoformatHorizontally;
    private JButton autoformatVertically;
    
    public FlowchartWindow() {
        this(null);
    }
    
    public FlowchartWindow(File file) {
        if (file != null) {
            //Load the flowchart here
        } else {
            flowchart = null;
        }
        eventManager = new EventManager(this);
        buttonPanel = new JPanel();
        camera = new Camera();
        view = new JComponent() {
            @Override
            public void paintComponent(Graphics g) {
                paintView(g);
            }
        };
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        view.addMouseListener(eventManager);
        view.addKeyListener(eventManager);
        view.addMouseMotionListener(eventManager);
        add(buttonPanel, BorderLayout.NORTH);
        add(view, BorderLayout.CENTER);
        setSize(640, 480);
    }
    
    public void paintView(Graphics g) {
        
    }
    
    public Camera getCamera() {
        return camera;
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * The flowchart currently being edited.
     * @return 
     */
    public Flowchart getFlowchart() {
        return flowchart;
    }
    
    
}
