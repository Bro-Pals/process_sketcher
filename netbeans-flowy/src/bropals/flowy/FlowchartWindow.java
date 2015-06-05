/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy;

import bropals.flowy.data.Flowchart;
import bropals.flowy.data.Node;
import bropals.flowy.data.NodeLine;
import static bropals.flowy.icons.IconManager.getIcon;
import bropals.flowy.listeners.AutoformatHorizontallyListener;
import bropals.flowy.listeners.AutoformatVerticallyListener;
import bropals.flowy.listeners.BorderColorListener;
import bropals.flowy.listeners.BorderSizeListener;
import bropals.flowy.listeners.CameraControls;
import bropals.flowy.listeners.CopyListener;
import bropals.flowy.listeners.CreateShapeListener;
import bropals.flowy.listeners.CutListener;
import bropals.flowy.listeners.ExportChartToImageListener;
import bropals.flowy.listeners.ExportChartToPDFListener;
import bropals.flowy.listeners.FillColorListener;
import bropals.flowy.listeners.FitToViewListener;
import bropals.flowy.listeners.FontColorListener;
import bropals.flowy.listeners.FontListener;
import bropals.flowy.listeners.FontSizeListener;
import bropals.flowy.listeners.LineColorListener;
import bropals.flowy.listeners.LineSizeListener;
import bropals.flowy.listeners.LineStyleListener;
import bropals.flowy.listeners.NewFlowchartListener;
import bropals.flowy.listeners.OpenFlowchartListener;
import bropals.flowy.listeners.PasteListener;
import bropals.flowy.listeners.PrintFlowchartListener;
import bropals.flowy.listeners.ResetViewListener;
import bropals.flowy.listeners.SaveAsFlowchartListener;
import bropals.flowy.listeners.SaveFlowchartListener;
import bropals.flowy.listeners.SelectNextNodeListener;
import bropals.flowy.listeners.SelectPreviousNodeListener;
import bropals.flowy.listeners.ShapeListener;
import bropals.flowy.listeners.UndoListener;
import bropals.flowy.listeners.ZoomInListener;
import bropals.flowy.listeners.ZoomOutListener;
import bropals.flowy.util.BooleanBlinker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;

/**
 * Used for editing a Flowchart.
 * @author Jonathon
 */
public class FlowchartWindow extends JFrame {
    
    private FlowchartWindowManager flowchartWindowManager;
    private Flowchart flowchart;
    private EventManager eventManager;
    private CameraControls cameraControls;
    private JTabbedPane buttonPanel;
    private JComponent view;
    private Camera camera;
    private Thread blinkThread;
    /*
        The mudball of GUI elements
    */
    private JButton createShape;
    private JButton selectNextNode;
    private JButton selectPreviousNode;
    
    //Font style buttons
    private JPanel fontStylePanel;
    private JSpinner fontSize;
    private JButton fontColor;
    private JComboBox font;
    
    //Line style buttons
    private JPanel lineStylePanel;
    private JComboBox lineStyle;
    private JButton lineColor;
    private JSpinner lineSize;
    
    //Node style buttons
    private JPanel nodeStylePanel;
    private JComboBox shape;
    private JButton borderColor;
    private JButton fillColor;
    private JSpinner borderSize;
    
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
        buttonPanel = new JTabbedPane();
        camera = new Camera();
        view = new JComponent() {
            @Override
            public void paintComponent(Graphics g) {
                paintView(g);
            }
        };
        formatTabs();
        view.addMouseListener(eventManager);
        view.addKeyListener(eventManager);
        view.addMouseMotionListener(eventManager);
        cameraControls = new CameraControls(camera, this);
        view.addMouseMotionListener(cameraControls);
        view.addKeyListener(cameraControls);
        view.addMouseWheelListener(cameraControls);
        view.addMouseListener(cameraControls);
        view.addMouseListener(new MouseAdapter() {
           @Override
           public void mouseEntered(MouseEvent e) {
               view.requestFocus();
           }
        });
        add(buttonPanel, BorderLayout.NORTH);
        add(view, BorderLayout.CENTER);
        setSize(800, 600);
        view.setFocusable(true);
        view.setFocusTraversalKeysEnabled(false);
        
        BooleanBlinker blinker = new BooleanBlinker(400); // for the blinking in the cursor
        blinker.addListener(eventManager);
        blinkThread = new Thread(blinker);
        revalidate();
        
        blinkThread.start();
    }
    
    public void redrawView() {
        view.repaint();
    }
    
    public void paintView(Graphics g) {
        // draw the background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, view.getWidth(), view.getHeight());
        // the color used for showing that something is selected
        Color selectionColor = Color.RED;
        for (Node n : flowchart.getNodes()) {
            n.getStyle().getShape().renderShape(n, camera, g, 
                n == eventManager.getSelectionManager().getLastSelected() && eventManager.isCursorShowing());
            if (eventManager.isSelected(n)) {
                // draw the box around the node if it's being selected
                g.setColor(selectionColor);
                float offset = 3 / camera.getZoom(); // for the selection box
                Point topLeftCorner = camera.convertWorldToCanvas(new Point.Float(n.getX(), n.getY()));
                g.drawRect((int)(topLeftCorner.getX() - offset), 
                        (int)(topLeftCorner.getY() - offset), 
                        (int)(n.getWidth()/camera.getZoom() + (2*offset)),
                        (int)(n.getHeight()/camera.getZoom() + (2*offset)));
            }
            for (NodeLine nl : n.getLinesConnected()) {
                Point[] linePoints = nl.getStyle().getType().renderLine(nl, camera, g, 
                        nl == eventManager.getSelectionManager().getLastSelected() && eventManager.isCursorShowing());
                if (eventManager.isSelected(nl)) {
                    g.setColor(selectionColor);
                    float offset = 3 / camera.getZoom();
                    g.drawLine((int)(linePoints[0].getX() + offset), 
                            (int)(linePoints[0].getY() + offset), 
                            (int)(linePoints[1].getX() + offset), 
                            (int)(linePoints[1].getY() + offset));
                    g.drawLine((int)(linePoints[0].getX() - offset), 
                            (int)(linePoints[0].getY() - offset), 
                            (int)(linePoints[1].getX() - offset), 
                            (int)(linePoints[1].getY() - offset));
                }
            }
        }
        
        // draw the box for the selection
        if (eventManager.getDragManager().isBoxSelecting() &&
                Math.abs(eventManager.getDragManager().getOffsetX()) > 3 &&
                Math.abs(eventManager.getDragManager().getOffsetY()) > 3) {
            int startX = camera.convertWorldToCanvasX(eventManager.getDragManager().getInitialX());
            int startY = camera.convertWorldToCanvasY(eventManager.getDragManager().getInitialY());
            int offsetX = (int)(eventManager.getDragManager().getOffsetX() / camera.getZoom());
            int offsetY = (int)(eventManager.getDragManager().getOffsetY() / camera.getZoom());
            
            // adjust values if there are negative values for the offset
            if (offsetX < 0) {
                startX = startX + offsetX;
            }
            if (offsetY < 0) {
                startY = startY + offsetY;
            }
            
            g.setColor(Color.BLUE);
            g.drawRect(startX, startY, Math.abs(offsetX), Math.abs(offsetY));
        }
    }
    
    public Camera getCamera() {
        return camera;
    }

    public EventManager getEventManager() {
        return eventManager;
    }
    
    public CameraControls getCameraControls() {
        return cameraControls;
    }

    public JComponent getView() {
        return view;
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
    
    private final void formatTabs() {
        JPanel fileTab = new JPanel();
        fileTab.setLayout(new BoxLayout(fileTab, BoxLayout.X_AXIS));
        
        newFlowchart = new JButton(getIcon("newFlowchartIcon.png"));
        saveFlowchart = new JButton(getIcon("saveFlowchartIcon.png"));
        saveAsFlowchart = new JButton(getIcon("saveAsFlowchartIcon.png"));
        openFlowchart = new JButton(getIcon("openFlowchartIcon.png"));
        printFlowchart = new JButton(getIcon("printFlowchartIcon.png"));
        exportChartToPDF = new JButton(getIcon("exportChartToPDFIcon.png"));
        exportChartToImage = new JButton(getIcon("exportChartToImageIcon.png"));
        
        newFlowchart.addActionListener(new NewFlowchartListener(this));
        saveFlowchart.addActionListener(new SaveFlowchartListener(this));
        saveAsFlowchart.addActionListener(new SaveAsFlowchartListener(this));
        openFlowchart.addActionListener(new OpenFlowchartListener(this));
        printFlowchart.addActionListener(new PrintFlowchartListener(this));
        exportChartToPDF.addActionListener(new ExportChartToPDFListener(this));
        exportChartToImage.addActionListener(new ExportChartToImageListener(this));
        
        newFlowchart.setToolTipText("Create a new flowchart");
        saveFlowchart.setToolTipText("Saves this flowchart to disk");
        saveAsFlowchart.setToolTipText("Saves this flowchart to disk as");
        openFlowchart.setToolTipText("Open a flowchart from disk");
        printFlowchart.setToolTipText("Print this flowchart");
        exportChartToPDF.setToolTipText("Export this flowchart as a PDF");
        exportChartToImage.setToolTipText("Export this flowchart as an image");
        
        fileTab.add(newFlowchart);
        fileTab.add(saveFlowchart);
        fileTab.add(saveAsFlowchart);
        fileTab.add(openFlowchart);
        fileTab.add(printFlowchart);
        fileTab.add(exportChartToPDF);
        fileTab.add(exportChartToImage);
    
        buttonPanel.addTab("File", fileTab);
        
        JPanel editTab = new JPanel();
        editTab.setLayout(new BoxLayout(editTab, BoxLayout.X_AXIS));
        
        copy = new JButton(getIcon("copyIcon.png"));
        cut = new JButton(getIcon("cutIcon.png"));
        paste = new JButton(getIcon("pasteIcon.png"));
        undo = new JButton(getIcon("undoIcon.png"));
        createShape = new JButton(getIcon("createShapeIcon.png"));
        selectNextNode = new JButton(getIcon("selectNextNodeIcon.png"));
        selectPreviousNode = new JButton(getIcon("selectPreviousNodeIcon.png"));
        autoformatHorizontally = new JButton(getIcon("autoformatHorizontallyIcon.png"));
        autoformatVertically = new JButton(getIcon("autoformatVerticallyIcon.png"));
        
        editTab.add(copy);
        editTab.add(cut);
        editTab.add(paste);
        editTab.add(undo);
        editTab.add(createShape);
        editTab.add(selectNextNode);
        editTab.add(selectPreviousNode);
        editTab.add(autoformatHorizontally);
        editTab.add(autoformatVertically);
        
        copy.addActionListener(new CopyListener(this));
        cut.addActionListener(new CutListener(this));
        paste.addActionListener(new PasteListener(this));
        undo.addActionListener(new UndoListener(this));
        createShape.addActionListener(new CreateShapeListener(this));
        selectNextNode.addActionListener(new SelectNextNodeListener(this));
        selectPreviousNode.addActionListener(new SelectPreviousNodeListener(this));
        autoformatHorizontally.addActionListener(new AutoformatHorizontallyListener(this));
        autoformatVertically.addActionListener(new AutoformatVerticallyListener(this));
        
        copy.setToolTipText("Copies the selection to the clipboard");
        cut.setToolTipText("Cuts the selection to the clipboard");
        paste.setToolTipText("Pastes the clipboard's contents");
        undo.setToolTipText("Undo the last action taken");
        createShape.setToolTipText("Create a new node");
        selectNextNode.setToolTipText("Select the next node in this process");
        selectPreviousNode.setToolTipText("Select the previous node in this process");
        autoformatHorizontally.setToolTipText("Formats the flowchart horizontally");
        autoformatVertically.setToolTipText("Formats the flowchart vertically");
        
        buttonPanel.addTab("Edit", editTab);
        
        JPanel viewTab = new JPanel();
        viewTab.setLayout(new BoxLayout(viewTab, BoxLayout.X_AXIS));
        
        zoomIn = new JButton(getIcon("zoomInIcon.png"));
        zoomOut = new JButton(getIcon("zoomOutIcon.png"));
        resetView = new JButton(getIcon("resetViewIcon.png"));
        fitToView = new JButton(getIcon("fitToViewIcon.png"));
        
        zoomIn.addActionListener(new ZoomInListener(this));
        zoomOut.addActionListener(new ZoomOutListener(this));
        resetView.addActionListener(new ResetViewListener(this));
        fitToView.addActionListener(new FitToViewListener(this));
        
        zoomIn.setToolTipText("Zooms the camera in");
        zoomOut.setToolTipText("Zooms the camera out");
        resetView.setToolTipText("Reset the camera view");
        fitToView.setToolTipText("Fits the entire flowchart in the view of the camera");
        
        viewTab.add(zoomIn);
        viewTab.add(zoomOut);
        viewTab.add(resetView);
        viewTab.add(fitToView);
        
        buttonPanel.addTab("View", viewTab);
        
        JPanel stylesTab = new JPanel();
        stylesTab.setLayout(new BoxLayout(stylesTab, BoxLayout.X_AXIS));
        
        //Need to make different panels to separate the buttons
        fontStylePanel = new JPanel();
        fontStylePanel.setLayout(new GridLayout(2, 2));
        
        font = new JComboBox();//Need to get list of fonts here
        fontSize = new JSpinner();
        fontColor = new JButton(getIcon("fontColorIcon.png"));
        
        fontStylePanel.add(font);
        fontStylePanel.add(fontSize);
        fontStylePanel.add(fontColor);
        
        font.addActionListener(new FontListener(this));
        fontSize.addChangeListener(new FontSizeListener(this));
        fontColor.addActionListener(new FontColorListener(this));
        
        font.setToolTipText("Sets the font of the selected object(s)");
        fontSize.setToolTipText("Sets the font size of the selected object(s)");
        fontColor.setToolTipText("Sets the font color of the selected object(s)");
        
        stylesTab.add(fontStylePanel);
        
        lineStylePanel = new JPanel();
        lineStylePanel.setLayout(new GridLayout(2, 2));
        
        lineStyle = new JComboBox(); //Need to get list of lines here
        lineSize = new JSpinner();
        lineColor = new JButton(getIcon("lineColorIcon.png"));
        
        lineStylePanel.add(lineStyle);
        lineStylePanel.add(lineSize);
        lineStylePanel.add(lineColor);
        
        lineStyle.addActionListener(new LineStyleListener(this));
        lineSize.addChangeListener(new LineSizeListener(this));
        lineColor.addActionListener(new LineColorListener(this));
        
        lineStyle.setToolTipText("Sets the line style of the selected lines(s)");
        lineSize.setToolTipText("Sets the line size of the selected lines(s)");
        lineColor.setToolTipText("Sets the line color of the selected lines(s)");
        
        stylesTab.add(lineStylePanel);
        
        nodeStylePanel = new JPanel();
        nodeStylePanel.setLayout(new GridLayout(2, 2));
        
        shape = new JComboBox(); //Need to get a list of shapes
        borderColor = new JButton(getIcon("borderColorIcon.png"));
        fillColor = new JButton(getIcon("fillColorIcon.png"));
        borderSize = new JSpinner();
        
        nodeStylePanel.add(shape);
        nodeStylePanel.add(borderColor);
        nodeStylePanel.add(fillColor);
        nodeStylePanel.add(borderSize);
        
        shape.addActionListener(new ShapeListener(this));
        borderColor.addActionListener(new BorderColorListener(this));
        fillColor.addActionListener(new FillColorListener(this));
        borderSize.addChangeListener(new BorderSizeListener(this));
        
        shape.setToolTipText("Sets the shape of the selected node(s)");
        borderColor.setToolTipText("Sets the border color of the selected node(s)");
        fillColor.setToolTipText("Sets the fill color of the selected node(s)");
        borderSize.setToolTipText("Sets the border size of the selected node(s)");
        
        stylesTab.add(nodeStylePanel);
        
        buttonPanel.addTab("Styles", stylesTab);
    }
    
    public void resizeTopCursor() {
        view.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
    }
    
    public void resizeBottomCursor() {
        view.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
    }
    
    public void resizeLeftCursor() {
        view.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
    }
    
    public void resizeRightCursor() {
        view.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
    }
    
    public void defaultCursor() {
        view.setCursor(Cursor.getDefaultCursor());
    }
}
