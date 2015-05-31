/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy;

import bropals.flowy.data.Flowchart;
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
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
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
    private JTabbedPane buttonPanel;
    private JComponent view;
    private Camera camera;
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
            flowchart = null;
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
        CameraControls cameraControls = new CameraControls(camera);
        view.addMouseMotionListener(cameraControls);
        view.addKeyListener(cameraControls);
        add(buttonPanel, BorderLayout.NORTH);
        add(view, BorderLayout.CENTER);
        revalidate();
        view.setFocusable(true);
        view.requestFocus();
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
        
        stylesTab.add(nodeStylePanel);
        
        buttonPanel.addTab("Styles", stylesTab);
    }
}
