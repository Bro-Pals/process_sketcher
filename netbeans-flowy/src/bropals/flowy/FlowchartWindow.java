/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy;

import bropals.flowy.data.Flowchart;
import bropals.flowy.data.Node;
import bropals.flowy.data.NodeLine;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Used for editing a Flowchart.
 * @author Jonathon
 */
public class FlowchartWindow extends JFrame {
    
    private FlowchartWindowManager flowchartWindowManager;
    private Flowchart flowchart;
    private EventManager eventManager;
    private JPanel buttonPanel;
    private JComponent view;
    private Camera camera;

    public FlowchartWindow(FlowchartWindowManager manager) {
        this(manager, null);
    }
    
    public FlowchartWindow(FlowchartWindowManager manager, File file) {
        if (file != null) {
            //Load the flowchart here
        } else {
            flowchart = new Flowchart(); // a new empty flowchart with one default node
        }
        flowchartWindowManager = manager;
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
        view.setFocusable(true);
        view.requestFocus();
    }
    
    public void redrawView() {
        paintView(view.getGraphics());
    }
    
    public void paintView(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, view.getWidth(), view.getHeight());
        for (Node n : flowchart.getNodes()) {
            n.getStyle().getShape().renderShape(n, camera,  g);
            if (eventManager.isSelected(n)) {
                g.setColor(Color.red);
                int offset = 3; // for the selection box
                g.drawRect(n.getX() - offset, n.getY() - offset, 
                        n.getWidth() + (2*offset), n.getHeight() + (2*offset));
            }
            for (NodeLine nl : n.getLinesConnected()) {
                nl.getStyle().getType().renderLine(nl, camera, g);
            }
        }
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

    public FlowchartWindowManager getFlowchartWindowManager() {
        return flowchartWindowManager;
    }
    
}
