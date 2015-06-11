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

import bropals.flowy.data.BinaryUtil;
import bropals.flowy.data.Flowchart;
import bropals.flowy.data.Node;
import bropals.flowy.data.NodeLine;
import bropals.flowy.data.Selectable;
import static bropals.flowy.icons.IconManager.getIcon;
import bropals.flowy.listeners.AutoformatHorizontallyListener;
import bropals.flowy.listeners.AutoformatVerticallyListener;
import bropals.flowy.listeners.BorderColorListener;
import bropals.flowy.listeners.BorderSizeListener;
import bropals.flowy.listeners.CameraControls;
import bropals.flowy.listeners.CloseFlowchartListener;
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
import bropals.flowy.style.LineType;
import bropals.flowy.style.Shape;
import bropals.flowy.util.BooleanBlinker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Used for editing a Flowchart.
 * @author Jonathon
 */
public class FlowchartWindow extends JFrame {
    /**
     * The window manager for all windows.
     */
    private final FlowchartWindowManager flowchartWindowManager;
    /**
     * The flowchart that this FlowchartWindow is currently editing.
     */
    private Flowchart flowchart;
    /**
     * The current flowchart file location.
     */
    private File file;
    /**
     * Manages events related to the view.
     */
    private final EventManager eventManager;
    /**
     * Handles the saved styles.
     */
    private final StyleManager styleManager;
    /**
     * The file chooser.
     */
    private JFileChooser fc;
    /**
     * Handles the camera controls.
     */
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
    
    private JPanel stylesTab;
    
    //Font style buttons
    private JPanel fontStylePanel;
    private JSpinner fontSize;
    private JButton fontColor;
    private JComboBox<Font> font;
    
    //Line style buttons
    private JPanel lineStylePanel;
    private JComboBox<String> lineType;
    private JButton lineColor;
    private JSpinner lineSize;
    
    //Node style buttons
    private JPanel nodeStylePanel;
    private JComboBox<String> shape;
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
    private JButton newFlowchart;
    private JButton saveFlowchart;
    private JButton saveAsFlowchart;
    private JButton openFlowchart;
    private JButton closeFlowchart;
    private JButton printFlowchart;
    private JButton exportChartToPDF;
    private JButton exportChartToImage;
    private JButton autoformatHorizontally;
    private JButton autoformatVertically;
    
    /**
     * Creates a new FlowchartWindow with an empty flowchart.
     * @param manager the FlowchartWindowManager.
     */
    public FlowchartWindow(FlowchartWindowManager manager) {
        this(manager, null, null);
    }
    
    /**
     * Creates a new FlowchartWindow, loading in flowchart data from
     * an input stream to edit it.
     * @param manager the FlowchartWindowManager.
     * @param stream the stream to read the flowchart data from.
     * @param location the file location the flowchart data comes from, if 
     * it is a file.
     */
    public FlowchartWindow(FlowchartWindowManager manager, InputStream stream, File location) {
        styleManager = new StyleManager();
        if (stream != null) {
            flowchart = Flowchart.readFlowchartData(stream, this);
        } else {
            flowchart = new Flowchart(true); // a new empty flowchart with one default node
            flowchart.passStyleManager(styleManager);
        }
        flowchartWindowManager = manager;
        fc = new JFileChooser();
        if (location != null) {
            file = location;
            fc.setCurrentDirectory(location.getParentFile());
        }
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setMultiSelectionEnabled(false);
        //Configure the file chooser so it can only act on .fwy files.
        fc.setFileFilter(new FileNameExtensionFilter("Flowy flowchart files (*.fwy)", "fwy"));
        eventManager = new EventManager(this);
        buttonPanel = new JTabbedPane();
        buttonPanel.setPreferredSize(new Dimension(400, 105));
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
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(800, 600);
        view.setFocusable(true);
        view.setFocusTraversalKeysEnabled(false);
        view.setBackground(Color.WHITE);
        
        BooleanBlinker blinker = new BooleanBlinker(280); // for the blinking in the cursor
        blinker.addListener(eventManager);
        blinkThread = new Thread(blinker);
        revalidate();
        
        blinkThread.start();
    }
    
    /**
     * Redraw the flowchart editor view.
     */
    public void redrawView() {
        view.repaint();
    }
    
    /**
     * The paint function for the flowchart editor view.
     * @param g the graphics context to draw with.
     */
    public void paintView(Graphics g) {
        // draw the background
        g.setColor(view.getBackground());
        g.fillRect(0, 0, view.getWidth(), view.getHeight());
        // the color used for showing that something is selected
        Color selectionColor = Color.RED;
        for (Node n : flowchart.getNodes()) {
            n.getStyle().getShape().renderShape(n, camera, g, 
                n == eventManager.getSelectionManager().getLastSelected() && eventManager.getTextTypeManager().isCursorShowing(),
                eventManager.getTextTypeManager().getLocationOfTypeCursor(), view.getBackground());
            if (eventManager.isSelected(n)) {
                // draw the box around the node if it's being selected
                g.setColor(selectionColor);
                float offset = camera.convertWorldToCanvasLength(3); // for the selection box
                Point topLeftCorner = camera.convertWorldToCanvas(new Point.Float(n.getX(), n.getY()));
                g.drawRect((int)(topLeftCorner.getX() - offset), 
                        (int)(topLeftCorner.getY() - offset), 
                        (int)(n.getWidth()/camera.getZoom() + (2*offset)),
                        (int)(n.getHeight()/camera.getZoom() + (2*offset)));
            }
            for (NodeLine nl : n.getLinesConnected()) {
                Point[] linePoints = nl.getStyle().getType().renderLine(nl, camera, g, 
                        nl == eventManager.getSelectionManager().getLastSelected() && eventManager.getTextTypeManager().isCursorShowing(),
                        eventManager.getTextTypeManager().getLocationOfTypeCursor(), eventManager.getTextTypeManager().getLinePartTyping());
                if (eventManager.isSelected(nl)) {
                    g.setColor(selectionColor);
                    float offset = camera.convertWorldToCanvasLength(3);
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
            int offsetX = camera.convertWorldToCanvasLength(eventManager.getDragManager().getOffsetX());
            int offsetY = camera.convertWorldToCanvasLength(eventManager.getDragManager().getOffsetY());
            
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
    
    /**
     * Get the camera being used to render for this window
     * @return the camera being used to render for this window
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Get the event manager handling events for this window
     * @return the event manager handling events for this window
     */
    public EventManager getEventManager() {
        return eventManager;
    }
    
    /**
     * Get the camera controls
     * @return the camera controls
     */
    public CameraControls getCameraControls() {
        return cameraControls;
    }

    /**
     * Gets the flowchart editor view.
     * @return 
     */
    public JComponent getView() {
        return view;
    }

    
    
    /**
     * Gets the flowchart currently being edited.
     * @return the flowchart currently being edited.
     */
    public Flowchart getFlowchart() {
        return flowchart;
    }

    /**
     * Get the flowchart window manager
     * @return The flowchart window manager
     */
    public FlowchartWindowManager getFlowchartWindowManager() {
        return flowchartWindowManager;
    }
    
    /**
     * Format and creates all GUI elements for this FlowchartWindow.
     */
    private void formatTabs() {
        JPanel fileTab = new JPanel();
        fileTab.setLayout(new BoxLayout(fileTab, BoxLayout.X_AXIS));
        
        newFlowchart = new JButton(getIcon("newFlowchartIcon.png"));
        saveFlowchart = new JButton(getIcon("saveFlowchartIcon.png"));
        saveAsFlowchart = new JButton(getIcon("saveAsFlowchartIcon.png"));
        openFlowchart = new JButton(getIcon("openFlowchartIcon.png"));
        closeFlowchart = new JButton(getIcon("closeFlowchartIcon.png"));
        printFlowchart = new JButton(getIcon("printFlowchartIcon.png"));
        exportChartToPDF = new JButton(getIcon("exportChartToPDFIcon.png"));
        exportChartToImage = new JButton(getIcon("exportChartToImageIcon.png"));
        
        newFlowchart.addActionListener(new NewFlowchartListener(this));
        saveFlowchart.addActionListener(new SaveFlowchartListener(this));
        saveAsFlowchart.addActionListener(new SaveAsFlowchartListener(this));
        openFlowchart.addActionListener(new OpenFlowchartListener(this));
        closeFlowchart.addActionListener(new CloseFlowchartListener(this));
        printFlowchart.addActionListener(new PrintFlowchartListener(this));
        exportChartToPDF.addActionListener(new ExportChartToPDFListener(this));
        exportChartToImage.addActionListener(new ExportChartToImageListener(this));
        
        newFlowchart.setToolTipText("Create a new flowchart");
        saveFlowchart.setToolTipText("Saves this flowchart to disk");
        saveAsFlowchart.setToolTipText("Saves this flowchart to disk as");
        openFlowchart.setToolTipText("Open a flowchart from disk");
        closeFlowchart.setToolTipText("Closes this flowchart");
        printFlowchart.setToolTipText("Print this flowchart");
        exportChartToPDF.setToolTipText("Export this flowchart as a PDF");
        exportChartToImage.setToolTipText("Export this flowchart as an image");
        
        fileTab.add(newFlowchart);
        fileTab.add(saveFlowchart);
        fileTab.add(saveAsFlowchart);
        fileTab.add(openFlowchart);
        fileTab.add(closeFlowchart);
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
        
        stylesTab = new JPanel();
        stylesTab.setLayout(new BoxLayout(stylesTab, BoxLayout.X_AXIS));
        
        //Need to make different panels to separate the buttons
        fontStylePanel = new JPanel();
        fontStylePanel.setLayout(new GridLayout(2, 2));
        
        font = new JComboBox<>(initFontList());//Need to get list of fonts here
        font.setRenderer(new FontListCellRenderer());
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
        
        lineType = new JComboBox(initLineTypeList()); //Need to get list of lines here
        lineSize = new JSpinner();
        lineColor = new JButton(getIcon("lineColorIcon.png"));
        
        lineStylePanel.add(lineType);
        lineStylePanel.add(lineSize);
        lineStylePanel.add(lineColor);
        
        lineType.addActionListener(new LineStyleListener(this));
        lineSize.addChangeListener(new LineSizeListener(this));
        lineColor.addActionListener(new LineColorListener(this));
        
        lineType.setToolTipText("Sets the line style of the selected lines(s)");
        lineSize.setToolTipText("Sets the line size of the selected lines(s)");
        lineColor.setToolTipText("Sets the line color of the selected lines(s)");
        
        stylesTab.add(lineStylePanel);
        
        nodeStylePanel = new JPanel();
        nodeStylePanel.setLayout(new GridLayout(2, 2));
        
        shape = new JComboBox(initShapeList()); //Need to get a list of shapes
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
    
    /**
     * Sets the view's cursor to the top edge resize cursor.
     */
    public void resizeTopCursor() {
        view.setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
    }
    
    /**
     * Sets the view's cursor to the bottom edge resize cursor.
     */
    public void resizeBottomCursor() {
        view.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
    }
    
    /**
     * Sets the view's cursor to the left edge resize cursor.
     */
    public void resizeLeftCursor() {
        view.setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
    }
    
    /**
     * Sets the view's cursor to the right edge resize cursor.
     */
    public void resizeRightCursor() {
        view.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
    }
    
    /**
     * Sets the view's cursor to the system's default cursor.
     */
    public void defaultCursor() {
        view.setCursor(Cursor.getDefaultCursor());
    }

    /**
     * Get the font style panel
     * @return The font style panel
     */
    public JPanel getFontStylePanel() {
        return fontStylePanel;
    }

    /**
     * Get the line style panel
     * @return The line style panel
     */
    public JPanel getLineStylePanel() {
        return lineStylePanel;
    }

    /**
     * Get the node style panel
     * @return The node style panel
     */
    public JPanel getNodeStylePanel() {
        return nodeStylePanel;
    }
    
    /**
     * Calls {@link #javax.swing.JComponent.revalidate revalidate} on
     * the styles tab.
     */
    public void revalidateStyles() {
        stylesTab.revalidate();
        buttonPanel.repaint();
    }
    
    
    /**
     * Make all the tables invisible for the window.
     */
    public void makeAllStylesInvisible() {
        getFontStylePanel().setVisible(false);
        getLineStylePanel().setVisible(false);
        getNodeStylePanel().setVisible(false);
    }
    
    /**
     * Make all of the tabs visible for the window.
     */
    public void makeAllStylesVisible() {
        getFontStylePanel().setVisible(true);
        getLineStylePanel().setVisible(true);
        getNodeStylePanel().setVisible(true);
    }
    
    /**
     * Refresh what tabs are visible according to what's selected.
     */
    public void refreshStylesTabVisiblity() {
        if (eventManager.getSelectionManager().hasEmptySelection()) {
            makeAllStylesInvisible();
        } else {
            boolean hasNode = false;
            boolean hasLine = false;
            for (int i=0; i<eventManager.getSelectionManager().getSelected().size(); i++) {
                if (eventManager.getSelectionManager().getSelected().get(i) instanceof Node) {
                    hasNode = true;
                } else if (eventManager.getSelectionManager().getSelected().get(i) instanceof NodeLine) {
                    hasLine = true;
                }
            }
            getFontStylePanel().setVisible(true);
            if (hasNode) {
                getNodeStylePanel().setVisible(true);
            }
            if (hasLine) {
                getLineStylePanel().setVisible(true);
            }
        }
    }
    
    /**
     * Refreshes the styles tab GUI in the flowchart window using a Selectable
     * @param s The selectable whose values will be used in updating the GUI.
     */
    public void refreshValuesOfStylesTabDueToNewSelection(Selectable s) {
        Node n = null;
        NodeLine nl = null;
        if (s instanceof Node) {
            n = (Node)s;
        } else if (s instanceof NodeLine) {
            nl = (NodeLine)s;
        }
        setFontPanelStyles(s.getFontStyle().getFontType(), s.getFontStyle().getFontColor(), s.getFontStyle().getFontSize());
        if (n != null) {
            //Node
            setNodePanelStyles(n.getStyle().getShape(), n.getStyle().getBorderColor(), n.getStyle().getFillColor(), n.getStyle().getBorderSize());
        } else if (nl != null) {
            //NodeLine
            setLinePanelStyles(nl.getStyle().getType(), nl.getStyle().getLineColor(), nl.getStyle().getLineSize());
        }
    }
    
    /**
     * Inits the list of possible Shapes for a node.
     * @return the list of possible Shapes for a node.
     */
    private String[] initShapeList() {
        return new String[] { 
            Shape.ACTION.toString(),
            Shape.DECISION.toString(),
            Shape.DELAY.toString(),
            Shape.DOCUMENT.toString(),
            Shape.INPUT_OUTPUT.toString(),
            Shape.MERGE.toString(),
            Shape.START_END.toString()
        };
    }
    
    /**
     * Inits the list of possible line types for a node line.
     * @return the list of possible line types for a node line.
     */
    private String[] initLineTypeList() {
        return new String[] { 
            LineType.SOLID.toString(),
            LineType.DASHED.toString(),
            LineType.DOTTED.toString()
        };
    }   
    
    /**
     * Gets the list of all fonts for this system.
     * @return the list of all possible fonts.
     */
    private Font[] initFontList() {
        return Flowy.allFonts;
    }
    
    /**
     * Sets the values for the font editing components in the styles panel.
     * @param font the font to set the font editor to.
     * @param fontColor the color to set the font color editor to.
     * @param fontSize the font size to display in the font size editor.
     */
    public void setFontPanelStyles(Font font, Color fontColor, int fontSize) {
        this.font.setSelectedItem(font);
        this.fontColor.setBackground(fontColor);
        this.fontSize.setValue(fontSize);
    }
    
    /**
     * Sets the values for the node editing components in the styles panel.
     * @param shape the shape to set the shape editor to.
     * @param borderColor the border color to set the border color editor to.
     * @param fillColor the fill color to set the fill color editor to.
     * @param borderSize the border size to display in the border size editor.
     */
    public void setNodePanelStyles(Shape shape, Color borderColor, Color fillColor, int borderSize) {
        this.shape.setSelectedItem(shape.toString());
        this.borderColor.setBackground(borderColor);
        this.fillColor.setBackground(fillColor);
        this.borderSize.setValue(borderSize);
    }
    
    /**
     * Sets the values for the line editing components in the styles panel.
     * @param lineType the line type to set he line type editor to.
     * @param lineColor the color to set the line color editor to.
     * @param lineSize the line size to display in the line size editor.
     */
    public void setLinePanelStyles(LineType lineType, Color lineColor, int lineSize) {
        this.lineType.setSelectedItem(lineType.toString());
        this.lineColor.setBackground(lineColor);
        this.lineSize.setValue(lineSize);
    }
    
    /**
     * For drawing fonts in the font drop down box.
     */
    class FontListCellRenderer implements ListCellRenderer<Font> {

        private DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
        
        @Override
        public Component getListCellRendererComponent(JList<? extends Font> list, Font value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel)dlcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setText(value.getFontName());
            label.setFont(value.deriveFont((float)14));
            return label;
        }
    }
    
    /**
     * Get the font combo box component
     * @return The font combo box component
     */
    public JComboBox<Font> getFontComboBox() {
        return this.font;
    }
    
    /**
     * Get the font size spinner component
     * @return The font size spinner component
     */
    public JSpinner getFontSizeSpinner() {
        return this.fontSize;
    }
    
    /**
     * Get the font color button
     * @return the font color button
     */
    public JButton getFontColorButton() {
        return this.fontColor;
    }
    
    /**
     * Get the border color button
     * @return the border color button
     */
    public JButton getBorderColorButton() {
        return this.borderColor;
    }
    
    /**
     * Get the fill color button
     * @return the fill color button
     */
    public JButton getFillColorButton() {
        return this.fillColor;
    }
    
    /**
     * Get the border size spinner component
     * @return the border size spinner component
     */
    public JSpinner getBorderSizeSpinner() {
        return this.borderSize;
    }
    
    /**
     * Get the shape combo box component
     * @return Get the shape combo box component
     */
    public JComboBox<String> getShapeComboBox() {
        return shape;
    }
    
    /**
     * Get the line type combo box component
     * @return the line type combo box component
     */
    public JComboBox<String> getLineTypeComboBox() {
        return lineType;
    }
    
    /**
     * Get the line size spinner component
     * @return the line size spinner component
     */
    public JSpinner getLineSizeSpinner() {
        return lineSize;
    }
    
    /**
     * Get the line color button
     * @return the line color button
     */
    public JButton getLineColorButton() {
        return lineColor;
    }
    
    
    /**
     * Saves this FlowchartWindow's current flowchart to a destination
     * stream.
     * This function flushes and closes the OutputStream.
     * @param stream the destination stream, opened.
     */
    public void writeFlowchartData(OutputStream stream) {
        int fileSize = flowchart.bytes();
        byte[] data = new byte[fileSize];
        flowchart.toBinary(data, 0);
        try {
            byte[] fileSizeBytes = new byte[4];
            BinaryUtil.intToBytes(fileSize, fileSizeBytes, 0);
            stream.write(fileSizeBytes);
            stream.write(data);
            stream.flush();
            stream.close();
        } catch(ArrayIndexOutOfBoundsException aio) { 
            System.err.println("Did not correctly predict the number of bytes: "
                    + aio + "\nexpected only " + data.length + " bytes");
        } catch(IOException e) {
            System.err.println("Could not write flowchart to output stream: " + e);
        }
    }
    
    /**
     * Refreshes the title of the window to show the file that is the "current" 
     * file.
     */
    public void refreshWindowTitle() {
        setTitle("Flowy | " + file.getName().substring(0, file.getName().length()-4));
    }
    
    /**
     * Opens a file chooser so the user can save the current flowchart.
     */
    public void saveFlowchart() {
        if (file == null) {
            saveAsFlowchart();
        } else {
            try {
                refreshWindowTitle();
                writeFlowchartData(Files.newOutputStream(file.toPath()));
                System.out.println("Saved flowchart data to " + file);
            } catch (IOException ex) {
                System.err.println("Could not open output stream to " + file.toString() + ": " + ex);
            }
        }
    }
    
    /**
     * Opens a file chooser so the user can save as the current flowchart.
     */
    public void saveAsFlowchart() {
        int response = fc.showSaveDialog(this);
        if (response == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            if (!file.getName().endsWith(".fwy")) {
                file = new File(file.getAbsolutePath() + ".fwy");
            }
            saveFlowchart();
        }
    }
    
    /**
     * Opens a file chooser so the user can open the current flowchart.
     */
    public void openFlowchart() {
        int response = fc.showOpenDialog(this);
        if (response == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            try {
                flowchart = Flowchart.readFlowchartData(Files.newInputStream(file.toPath()), this);
                refreshWindowTitle();
                System.out.println("Read flowchart data from " + file);
            } catch (IOException ex) {
                System.err.println("Unable to read flowchart data from " + file + ", " + ex);
            }
        }
    }
    
    /**
     * Gets the style manager for this window.
     * @return the style manager.
     */
    public StyleManager getStyleManager() {
        return styleManager;
    }
    
    /**
     * Closes this window.
     */
    public void closeWindow() {
        flowchartWindowManager.tryCloseWindow(this);
    }
}
