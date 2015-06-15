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
import bropals.processsketcher.data.Flowchart;
import bropals.processsketcher.data.Node;
import bropals.processsketcher.data.NodeLine;
import bropals.processsketcher.data.Selectable;
import static bropals.processsketcher.icons.IconManager.getIcon;
import bropals.processsketcher.listeners.AutoformatHorizontallyListener;
import bropals.processsketcher.listeners.AutoformatVerticallyListener;
import bropals.processsketcher.listeners.BorderColorListener;
import bropals.processsketcher.listeners.BorderSizeListener;
import bropals.processsketcher.listeners.CameraControls;
import bropals.processsketcher.listeners.CloseFlowchartListener;
import bropals.processsketcher.listeners.CopyListener;
import bropals.processsketcher.listeners.CreateShapeListener;
import bropals.processsketcher.listeners.CutListener;
import bropals.processsketcher.listeners.ExportChartToImageListener;
import bropals.processsketcher.listeners.ExportChartToPDFListener;
import bropals.processsketcher.listeners.FillColorListener;
import bropals.processsketcher.listeners.FitToViewListener;
import bropals.processsketcher.listeners.FontColorListener;
import bropals.processsketcher.listeners.FontListener;
import bropals.processsketcher.listeners.FontSizeListener;
import bropals.processsketcher.listeners.LineColorListener;
import bropals.processsketcher.listeners.LineSizeListener;
import bropals.processsketcher.listeners.LineStyleListener;
import bropals.processsketcher.listeners.NewFlowchartListener;
import bropals.processsketcher.listeners.OpenFlowchartListener;
import bropals.processsketcher.listeners.PasteListener;
import bropals.processsketcher.listeners.PrintFlowchartListener;
import bropals.processsketcher.listeners.ResetViewListener;
import bropals.processsketcher.listeners.SaveAsFlowchartListener;
import bropals.processsketcher.listeners.SaveFlowchartListener;
import bropals.processsketcher.listeners.SaveLineStylesListener;
import bropals.processsketcher.listeners.SaveNodeStylesListener;
import bropals.processsketcher.listeners.SavedLineStylesListener;
import bropals.processsketcher.listeners.SavedNodeStylesListener;
import bropals.processsketcher.listeners.SelectNextNodeListener;
import bropals.processsketcher.listeners.SelectPreviousNodeListener;
import bropals.processsketcher.listeners.ShapeListener;
import bropals.processsketcher.listeners.UndoListener;
import bropals.processsketcher.listeners.ZoomInListener;
import bropals.processsketcher.listeners.ZoomOutListener;
import bropals.processsketcher.style.LineType;
import bropals.processsketcher.style.Shape;
import bropals.processsketcher.util.BooleanBlinker;
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
import java.awt.image.BufferedImage;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Used for editing a Flowchart.
 *
 * @author Jonathon
 */
public class FlowchartWindow extends JFrame {

    public static final String FILE_EXTENSION = "prsf";

    /**
     * The distance between nodes on the same depth level.
     */
    private float autoformatInnerSpacing = 50;

    /**
     * The distance between different depth levels.
     */
    private float autoformatOuterSpacing = 70;

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

    //Node style saving buttons
    private JPanel savedNodeStylesPanel;
    private JComboBox<String> savedNodeStyles;
    private JButton saveNodeStyle;

    //Node style saving buttons
    private JPanel savedLineStylesPanel;
    private JComboBox<String> savedLineStyles;
    private JButton saveLineStyle;

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
     *
     * @param manager the FlowchartWindowManager.
     */
    public FlowchartWindow(FlowchartWindowManager manager) {
        this(manager, null, null);
    }

    /**
     * Creates a new FlowchartWindow, loading in flowchart data from an input
     * stream to edit it.
     *
     * @param manager the FlowchartWindowManager.
     * @param stream the stream to read the flowchart data from.
     * @param location the file location the flowchart data comes from, if it is
     * a file.
     */
    public FlowchartWindow(FlowchartWindowManager manager, InputStream stream, File location) {
        setIconImage(ProcessSketcher.mainIconSmaller);
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
        eventManager = new EventManager(this);
        buttonPanel = new JTabbedPane();
        buttonPanel.setPreferredSize(new Dimension(400, 125));
        camera = new Camera();
        view = new JComponent() {
            @Override
            public void paintComponent(Graphics g) {
                paintFlowchart(g, true);
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
     *
     * @param g the graphics context to draw with.
     * @param showSelection if the selected object should have a box around it.
     */
    public void paintFlowchart(Graphics g, boolean showSelection) {
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
                g.drawRect((int) (topLeftCorner.getX() - offset),
                        (int) (topLeftCorner.getY() - offset),
                        (int) (n.getWidth() / camera.getZoom() + (2 * offset)),
                        (int) (n.getHeight() / camera.getZoom() + (2 * offset)));
            }
            for (NodeLine nl : n.getLinesConnected()) {
                Point[] linePoints = nl.getStyle().getType().renderLine(nl, camera, g,
                        nl == eventManager.getSelectionManager().getLastSelected() && eventManager.getTextTypeManager().isCursorShowing(),
                        eventManager.getTextTypeManager().getLocationOfTypeCursor(), eventManager.getTextTypeManager().getLinePartTyping());
                if (eventManager.isSelected(nl) && showSelection) {
                    g.setColor(selectionColor);
                    float offset = camera.convertWorldToCanvasLength(3);
                    g.drawLine((int) (linePoints[0].getX() + offset),
                            (int) (linePoints[0].getY() + offset),
                            (int) (linePoints[1].getX() + offset),
                            (int) (linePoints[1].getY() + offset));
                    g.drawLine((int) (linePoints[0].getX() - offset),
                            (int) (linePoints[0].getY() - offset),
                            (int) (linePoints[1].getX() - offset),
                            (int) (linePoints[1].getY() - offset));
                }
            }
        }

        // draw the box for the selection
        if (eventManager.getDragManager().isBoxSelecting()
                && Math.abs(eventManager.getDragManager().getOffsetX()) > 3
                && Math.abs(eventManager.getDragManager().getOffsetY()) > 3) {
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
            if (showSelection) {
                g.setColor(Color.BLUE);
                g.drawRect(startX, startY, Math.abs(offsetX), Math.abs(offsetY));
            }
        }
    }

    /**
     * Get the camera being used to render for this window
     *
     * @return the camera being used to render for this window
     */
    public Camera getCamera() {
        return camera;
    }

    /**
     * Get the event manager handling events for this window
     *
     * @return the event manager handling events for this window
     */
    public EventManager getEventManager() {
        return eventManager;
    }

    /**
     * Get the camera controls
     *
     * @return the camera controls
     */
    public CameraControls getCameraControls() {
        return cameraControls;
    }

    /**
     * Gets the flowchart editor view.
     *
     * @return
     */
    public JComponent getView() {
        return view;
    }

    /**
     * Gets the flowchart currently being edited.
     *
     * @return the flowchart currently being edited.
     */
    public Flowchart getFlowchart() {
        return flowchart;
    }

    /**
     * Get the flowchart window manager
     *
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

        buttonPanel.addTab("File", wrapInScrollPane(fileTab));

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

        buttonPanel.addTab("Edit", wrapInScrollPane(editTab));

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

        buttonPanel.addTab("View", wrapInScrollPane(viewTab));

        stylesTab = new JPanel();
        stylesTab.setLayout(new BoxLayout(stylesTab, BoxLayout.X_AXIS));

        savedNodeStylesPanel = new JPanel();
        savedNodeStylesPanel.setLayout(new BoxLayout(savedNodeStylesPanel, BoxLayout.Y_AXIS));

        savedNodeStyles = new JComboBox(styleManager.listNodeStyleNames());
        saveNodeStyle = new JButton(getIcon("saveNodeStyleIcon.png"));

        savedNodeStylesPanel.add(savedNodeStyles);
        savedNodeStylesPanel.add(saveNodeStyle);

        savedNodeStyles.addActionListener(new SavedNodeStylesListener(this));
        saveNodeStyle.addActionListener(new SaveNodeStylesListener(this));

        savedNodeStyles.setToolTipText("Set the style of the currently selected node to a saved style");
        saveNodeStyle.setToolTipText("Save the style of the currently selected node for later use");

        stylesTab.add(savedNodeStylesPanel);

        savedLineStylesPanel = new JPanel();
        savedLineStylesPanel.setLayout(new BoxLayout(savedLineStylesPanel, BoxLayout.Y_AXIS));

        savedLineStyles = new JComboBox(styleManager.listLineStyleNames());
        saveLineStyle = new JButton(getIcon("saveLineStyleIcon.png"));

        savedLineStylesPanel.add(savedLineStyles);
        savedLineStylesPanel.add(saveLineStyle);

        savedLineStyles.addActionListener(new SavedLineStylesListener(this));
        saveLineStyle.addActionListener(new SaveLineStylesListener(this));

        savedLineStyles.setToolTipText("Set the style of the currently selected node line to a saved style");
        saveLineStyle.setToolTipText("Save the style of the currently selected node line for later use");

        stylesTab.add(savedLineStylesPanel);

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

        //Need to make the panels invisible initially
        nodeStylePanel.setVisible(false);
        savedNodeStylesPanel.setVisible(false);
        lineStylePanel.setVisible(false);
        savedLineStylesPanel.setVisible(false);
        fontStylePanel.setVisible(false);

        buttonPanel.addTab("Styles", wrapInScrollPane(stylesTab));
    }

    /**
     * Wraps a panel in a scroll pane.
     *
     * @param panel the panel to wrap.
     * @return a scroll panel that contains the given panel.
     */
    private JScrollPane wrapInScrollPane(JPanel panel) {
        JScrollPane pane = new JScrollPane(panel);
        pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return pane;
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
     *
     * @return The font style panel
     */
    public JPanel getFontStylePanel() {
        return fontStylePanel;
    }

    /**
     * Get the line style panel
     *
     * @return The line style panel
     */
    public JPanel getLineStylePanel() {
        return lineStylePanel;
    }

    /**
     * Get the node style panel
     *
     * @return The node style panel
     */
    public JPanel getNodeStylePanel() {
        return nodeStylePanel;
    }

    /**
     * Calls {@link #javax.swing.JComponent.revalidate revalidate} on the styles
     * tab.
     */
    public void revalidateStyles() {
        stylesTab.revalidate();
    }

    /**
     * Make all the tables invisible for the window.
     */
    public void makeAllStylesInvisible() {
        getFontStylePanel().setVisible(false);
        setLineRelatedPanelVisible(false);
        setNodeRelatedPanelVisible(false);
    }

    /**
     * Make all of the tabs visible for the window.
     */
    public void makeAllStylesVisible() {
        getFontStylePanel().setVisible(true);
        setLineRelatedPanelVisible(true);
        setNodeRelatedPanelVisible(true);
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
            for (int i = 0; i < eventManager.getSelectionManager().getSelected().size(); i++) {
                if (eventManager.getSelectionManager().getSelected().get(i) instanceof Node) {
                    hasNode = true;
                } else if (eventManager.getSelectionManager().getSelected().get(i) instanceof NodeLine) {
                    hasLine = true;
                }
            }
            getFontStylePanel().setVisible(true);
            if (hasNode) {
                setNodeRelatedPanelVisible(true);
            }
            if (hasLine) {
                setLineRelatedPanelVisible(true);
            }
        }
    }

    /**
     * Refreshes the styles tab GUI in the flowchart window using the current
     * selection.
     */
    public void refreshValuesOfStylesTabDueToUpdatedSelection() {
        ArrayList<Selectable> selected = getEventManager().getSelectionManager().getSelected();
        if (!selected.isEmpty()) {
            Font font = getSameFont(selected);
            int fontSize = getSameFontSize(selected);
            Color fontColor = getSameFontColor(selected);
            LineType lineType = getSameLineType(selected);
            Color lineColor = getSameLineColor(selected);
            int lineSize = getSameLineSize(selected);
            Shape shape = getSameShape(selected);
            int borderSize = getSameBorderSize(selected);
            Color borderColor = getSameBorderColor(selected);
            Color fillColor = getSameFillColor(selected);
            String nodeStyleName = getSameNodeStyleName(selected);
            String lineStyleName = getSameLineStyleName(selected);
            setFontPanelStyles(font, fontColor, fontSize);
            setNodePanelStyles(shape, borderColor, fillColor, borderSize);
            setLinePanelStyles(lineType, lineColor, lineSize);
            if (nodeStyleName != null) {
                getSavedNodeStylesComboBox().setSelectedItem(nodeStyleName);
            } else {
                getSavedNodeStylesComboBox().setSelectedIndex(-1);
            }
            if (lineStyleName != null) {
                getSavedLineStylesComboBox().setSelectedItem(lineStyleName);
            } else {
                getSavedLineStylesComboBox().setSelectedIndex(-1);
            }
        }
    }

    /**
     * Gets the font name that is the same for all selectables in the given
     * list, or <code>null</code> if they do not all have the same font.
     *
     * @param selected the list of selected objects.
     * @return the font name that is common to all selectables, or
     * <code>null</code> if the font name is not common.
     */
    private Font getSameFont(ArrayList<Selectable> selected) {
        Font font = selected.get(0).getFontStyle().getFontType();
        for (int i = 1; i < selected.size(); i++) {
            if (!selected.get(i).getFontStyle().getFontType().getFontName().equals(font.getFontName())) {
                return null;
            }
        }
        return font;
    }

    /**
     * Gets the font size that is the same for all selectables in the given
     * list, or <code>-1</code> if they do not all have the same font size.
     *
     * @param selected the list of selected objects.
     * @return the font size that is common to all selectables, or
     * <code>-1</code> if the font size is not common.
     */
    private int getSameFontSize(ArrayList<Selectable> selected) {
        int size = selected.get(0).getFontStyle().getFontSize();
        for (int i = 1; i < selected.size(); i++) {
            if (size != selected.get(i).getFontStyle().getFontSize()) {
                //The border size is not common
                return -1;
            }
        }
        return size;
    }

    /**
     * Gets the font color that is the same for all selectables in the given
     * list, or <code>null</code> if they do not all have the same font color.
     *
     * @param selected the list of selected objects.
     * @return the font color that is common to all selectables, or
     * <code>null</code> if the font color is not common.
     */
    private Color getSameFontColor(ArrayList<Selectable> selected) {
        Color color = selected.get(0).getFontStyle().getFontColor();
        for (int i = 1; i < selected.size(); i++) {
            if (!selected.get(i).getFontStyle().getFontColor().equals(color)) {
                return null;
            }
        }
        return color;
    }

    /**
     * Gets the line type that is the same for all selectables in the given
     * list, or <code>null</code> if they do not all have the same line type.
     *
     * @param selected the list of selected objects.
     * @return the line type that is common to all selectables, or
     * <code>null</code> if the line type is not common.
     */
    private LineType getSameLineType(ArrayList<Selectable> selected) {
        int firstNodeLine = indexOfFirstNodeLine(selected);
        if (firstNodeLine == -1) {
            //No nodes in this selection anyway
            return null;
        }
        LineType type = ((NodeLine) selected.get(firstNodeLine)).getStyle().getType();
        for (int i = firstNodeLine + 1; i < selected.size(); i++) {
            if (selected.get(i) instanceof NodeLine) {
                if (type != ((NodeLine) selected.get(i)).getStyle().getType()) {
                    //The border size is not common
                    return null;
                }
            }
        }
        return type;
    }

    /**
     * Gets the line color that is the same for all selectables in the given
     * list, or <code>null</code> if they do not all have the same line color.
     *
     * @param selected the list of selected objects.
     * @return the line color that is common to all selectables, or
     * <code>null</code> if the line color is not common.
     */
    private Color getSameLineColor(ArrayList<Selectable> selected) {
        int firstNodeLine = indexOfFirstNodeLine(selected);
        if (firstNodeLine == -1) {
            //No nodes in this selection anyway
            return null;
        }
        Color color = ((NodeLine) selected.get(firstNodeLine)).getStyle().getLineColor();
        for (int i = firstNodeLine + 1; i < selected.size(); i++) {
            if (selected.get(i) instanceof NodeLine) {
                if (color != ((NodeLine) selected.get(i)).getStyle().getLineColor()) {
                    //The border size is not common
                    return null;
                }
            }
        }
        return color;
    }

    /**
     * Gets the line size that is the same for all selectables in the given
     * list, or <code>-1</code> if they do not all have the same line size.
     *
     * @param selected the list of selected objects.
     * @return the line size that is common to all selectables, or
     * <code>-1</code> if the line size is not common.
     */
    private int getSameLineSize(ArrayList<Selectable> selected) {
        int firstNodeLine = indexOfFirstNodeLine(selected);
        if (firstNodeLine == -1) {
            //No nodes in this selection anyway
            return -1;
        }
        int size = ((NodeLine) selected.get(firstNodeLine)).getStyle().getLineSize();
        for (int i = firstNodeLine + 1; i < selected.size(); i++) {
            if (selected.get(i) instanceof NodeLine) {
                if (size != ((NodeLine) selected.get(i)).getStyle().getLineSize()) {
                    //The border size is not common
                    return -1;
                }
            }
        }
        return size;
    }

    /**
     * Gets the linked line style name that is the same for all selectables in
     * the given list, or <code>null</code> if they do not all have the same
     * linked line style name.
     *
     * @param selected the list of selected objects.
     * @return the linked line style name that is common to all selectables, or
     * <code>null</code> if the linked line style name is not common.
     */
    private String getSameLineStyleName(ArrayList<Selectable> selected) {
        int firstNodeLine = indexOfFirstNodeLine(selected);
        if (firstNodeLine == -1) {
            //No nodes in this selection anyway
            return null;
        }
        String name = ((NodeLine) selected.get(firstNodeLine)).getLinkedStyle();
        //return null if there is no name
        if (name == null) {
            return null;
        }
        for (int i = firstNodeLine + 1; i < selected.size(); i++) {
            if (selected.get(i) instanceof NodeLine) {
                if (!name.equals(((NodeLine) selected.get(i)).getLinkedStyle())) {
                    //The border size is not common
                    return null;
                }
            }
        }
        return name;
    }

    /**
     * Gets the shape that is the same for all selectables in the given list, or
     * <code>null</code> if they do not all have the same shape.
     *
     * @param selected the list of selected objects.
     * @return the shape that is common to all selectables, or <code>null</code>
     * if the shape is not common.
     */
    private Shape getSameShape(ArrayList<Selectable> selected) {
        int firstNode = indexOfFirstNode(selected);
        if (firstNode == -1) {
            //No nodes in this selection anyway
            return null;
        }
        Shape shape = ((Node) selected.get(firstNode)).getStyle().getShape();
        for (int i = firstNode + 1; i < selected.size(); i++) {
            if (selected.get(i) instanceof Node) {
                if (shape != ((Node) selected.get(i)).getStyle().getShape()) {
                    //The border size is not common
                    return null;
                }
            }
        }
        return shape;
    }

    /**
     * Gets the fill color that is the same for all selectables in the given
     * list, or <code>null</code> if they do not all have the same fill color.
     *
     * @param selected the list of selected objects.
     * @return the fill color that is common to all selectables, or
     * <code>null</code> if the fill color is not common.
     */
    private Color getSameFillColor(ArrayList<Selectable> selected) {
        int firstNode = indexOfFirstNode(selected);
        if (firstNode == -1) {
            //No nodes in this selection anyway
            return null;
        }
        Color color = ((Node) selected.get(firstNode)).getStyle().getFillColor();
        for (int i = firstNode + 1; i < selected.size(); i++) {
            if (selected.get(i) instanceof Node) {
                if (color != ((Node) selected.get(i)).getStyle().getFillColor()) {
                    //The border size is not common
                    return null;
                }
            }
        }
        return color;
    }

    /**
     * Gets the border color that is the same for all selectables in the given
     * list, or <code>null</code> if they do not all have the same border color.
     *
     * @param selected the list of selected objects.
     * @return the border color that is common to all selectables, or
     * <code>null</code> if the border color is not common.
     */
    private Color getSameBorderColor(ArrayList<Selectable> selected) {
        int firstNode = indexOfFirstNode(selected);
        if (firstNode == -1) {
            //No nodes in this selection anyway
            return null;
        }
        Color color = ((Node) selected.get(firstNode)).getStyle().getBorderColor();
        for (int i = firstNode + 1; i < selected.size(); i++) {
            if (selected.get(i) instanceof Node) {
                if (color != ((Node) selected.get(i)).getStyle().getBorderColor()) {
                    //The border size is not common
                    return null;
                }
            }
        }
        return color;
    }

    /**
     * Gets the border size that is the same for all selectables in the given
     * list, or <code>-1</code> if they do not all have the same border size.
     *
     * @param selected the list of selected objects.
     * @return the border size that is common to all selectables, or
     * <code>-1</code> if the border size is not common.
     */
    private int getSameBorderSize(ArrayList<Selectable> selected) {
        int firstNode = indexOfFirstNode(selected);
        if (firstNode == -1) {
            //No nodes in this selection anyway
            return -1;
        }
        int size = ((Node) selected.get(firstNode)).getStyle().getBorderSize();
        for (int i = firstNode + 1; i < selected.size(); i++) {
            if (selected.get(i) instanceof Node) {
                if (size != ((Node) selected.get(i)).getStyle().getBorderSize()) {
                    //The border size is not common
                    return -1;
                }
            }
        }
        return size;
    }

    /**
     * Gets the linked node style name that is the same for all selectables in
     * the given list, or <code>null</code> if they do not all have the same
     * linked node style name.
     *
     * @param selected the list of selected objects.
     * @return the linked node style name that is common to all selectables, or
     * <code>null</code> if the linked node style name is not common.
     */
    private String getSameNodeStyleName(ArrayList<Selectable> selected) {
        int firstNode = indexOfFirstNode(selected);
        if (firstNode == -1) {
            //No nodes in this selection anyway
            return null;
        }
        String name = ((Node) selected.get(firstNode)).getLinkedStyle();
        // return null if there is no linked style name
        if (name == null) {
            return null;
        }
        
        for (int i = firstNode + 1; i < selected.size(); i++) {
            if (selected.get(i) instanceof Node) {
                if (!name.equals(((Node)selected.get(i)).getLinkedStyle())) {
                    //The border size is not common
                    return null;
                }
            }
        }
        return name;
    }

    /**
     * Gets the index of the first selectable that is a node. If there is no
     * node, then this returns <code>-1</code>.
     *
     * @param selected the list of selectables.
     * @return the index of the first selectable that is a node.
     */
    private int indexOfFirstNode(ArrayList<Selectable> selected) {
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i) instanceof Node) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the index of the first selectable that is a node line. If there is
     * no node line, then this returns <code>-1</code>.
     *
     * @param selected the list of selectables.
     * @return the index of the first selectable that is a node line.
     */
    private int indexOfFirstNodeLine(ArrayList<Selectable> selected) {
        for (int i = 0; i < selected.size(); i++) {
            if (selected.get(i) instanceof NodeLine) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Inits the list of possible Shapes for a node.
     *
     * @return the list of possible Shapes for a node.
     */
    private String[] initShapeList() {
        return new String[]{
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
     *
     * @return the list of possible line types for a node line.
     */
    private String[] initLineTypeList() {
        return new String[]{
            LineType.SOLID.toString(),
            LineType.DASHED.toString(),
            LineType.DOTTED.toString()
        };
    }

    /**
     * Gets the list of all fonts for this system.
     *
     * @return the list of all possible fonts.
     */
    private Font[] initFontList() {
        return ProcessSketcher.allFonts;
    }

    /**
     * Sets the values for the font editing components in the styles panel.
     *
     * @param font the font to set the font editor to.
     * @param fontColor the color to set the font color editor to.
     * @param fontSize the font size to display in the font size editor.
     */
    public void setFontPanelStyles(Font font, Color fontColor, int fontSize) {
        if (font != null) {
            this.font.setSelectedItem(font);
        } else {
            this.font.setSelectedIndex(-1);
        }
        if (fontColor != null) {
            this.fontColor.setBackground(fontColor);
        } else {
            this.fontColor.setBackground(getBackground());
        }
        if (fontSize != -1) {
            this.fontSize.setValue(fontSize);
        } else {
            this.fontSize.setValue(0);
        }
    }

    /**
     * Sets the values for the node editing components in the styles panel.
     *
     * @param shape the shape to set the shape editor to.
     * @param borderColor the border color to set the border color editor to.
     * @param fillColor the fill color to set the fill color editor to.
     * @param borderSize the border size to display in the border size editor.
     */
    public void setNodePanelStyles(Shape shape, Color borderColor, Color fillColor, int borderSize) {
        if (shape != null) {
            this.shape.setSelectedItem(shape.toString());
        } else {
            this.shape.setSelectedIndex(-1);
        }
        if (borderColor != null) {
            this.borderColor.setBackground(borderColor);
        } else {
            this.borderColor.setBackground(getBackground());
        }
        if (fillColor != null) {
            this.fillColor.setBackground(fillColor);
        } else {
            this.fillColor.setBackground(getBackground());
        }
        if (borderSize != -1) {
            this.borderSize.setValue(borderSize);
        } else {
            this.borderSize.setValue(0);
        }
    }

    /**
     * Sets the values for the line editing components in the styles panel.
     *
     * @param lineType the line type to set he line type editor to.
     * @param lineColor the color to set the line color editor to.
     * @param lineSize the line size to display in the line size editor.
     */
    public void setLinePanelStyles(LineType lineType, Color lineColor, int lineSize) {
        if (lineType != null) {
            this.lineType.setSelectedItem(lineType.toString());
        } else {
            this.lineType.setSelectedIndex(-1);
        }
        if (lineColor != null) {
            this.lineColor.setBackground(lineColor);
        } else {
            this.lineColor.setBackground(getBackground());
        }
        if (lineSize != -1) {
            this.lineSize.setValue(lineSize);
        } else {
            this.lineSize.setValue(0);
        }
    }

    /**
     * For drawing fonts in the font drop down box.
     */
    class FontListCellRenderer implements ListCellRenderer<Font> {

        private DefaultListCellRenderer dlcr = new DefaultListCellRenderer();

        @Override
        public Component getListCellRendererComponent(JList<? extends Font> list, Font value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) dlcr.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setText(value.getFontName());
            label.setFont(value.deriveFont((float) 14));
            return label;
        }
    }

    /**
     * Sets the visibility of the node related panels.
     *
     * @param visible the visibility of the node related panel.
     */
    public void setNodeRelatedPanelVisible(boolean visible) {
        nodeStylePanel.setVisible(visible);
        savedNodeStylesPanel.setVisible(visible);
    }

    /**
     * Sets the visibility of the line related panels.
     *
     * @param visible the visibility of the line related panel.
     */
    public void setLineRelatedPanelVisible(boolean visible) {
        lineStylePanel.setVisible(visible);
        savedLineStylesPanel.setVisible(visible);
    }

    /**
     * Get the font combo box component
     *
     * @return The font combo box component
     */
    public JComboBox<Font> getFontComboBox() {
        return this.font;
    }

    /**
     * Get the font size spinner component
     *
     * @return The font size spinner component
     */
    public JSpinner getFontSizeSpinner() {
        return this.fontSize;
    }

    /**
     * Get the font color button
     *
     * @return the font color button
     */
    public JButton getFontColorButton() {
        return this.fontColor;
    }

    /**
     * Get the border color button
     *
     * @return the border color button
     */
    public JButton getBorderColorButton() {
        return this.borderColor;
    }

    /**
     * Get the fill color button
     *
     * @return the fill color button
     */
    public JButton getFillColorButton() {
        return this.fillColor;
    }

    /**
     * Get the border size spinner component
     *
     * @return the border size spinner component
     */
    public JSpinner getBorderSizeSpinner() {
        return this.borderSize;
    }

    /**
     * Get the shape combo box component
     *
     * @return Get the shape combo box component
     */
    public JComboBox<String> getShapeComboBox() {
        return shape;
    }

    /**
     * Get the line type combo box component
     *
     * @return the line type combo box component
     */
    public JComboBox<String> getLineTypeComboBox() {
        return lineType;
    }

    /**
     * Get the line size spinner component
     *
     * @return the line size spinner component
     */
    public JSpinner getLineSizeSpinner() {
        return lineSize;
    }

    /**
     * Get the line color button
     *
     * @return the line color button
     */
    public JButton getLineColorButton() {
        return lineColor;
    }

    /**
     * Gets the saved node styles combo box.
     *
     * @return the saved node styles combo box.
     */
    public JComboBox getSavedNodeStylesComboBox() {
        return savedNodeStyles;
    }

    /**
     * Gets the saved line styles combo box.
     *
     * @return the saved line styles combo box.
     */
    public JComboBox getSavedLineStylesComboBox() {
        return savedLineStyles;
    }

    /**
     * Sets the saved line styles box to no selection.
     */
    public void deselectLinkedLineStyle() {
        savedLineStyles.setSelectedIndex(-1);
    }

    /**
     * Sets the saved line styles box to no selection.
     */
    public void deselectLinkedNodeStyle() {
        savedNodeStyles.setSelectedIndex(-1);
    }

    /**
     * Refresh the GUI list of line styles to match the style manager's list.
     */
    public void refreshLineStyleList() {
        String selected = (String) savedLineStyles.getSelectedItem();
        DefaultComboBoxModel<String> box = (DefaultComboBoxModel<String>) savedLineStyles.getModel();
        box.removeAllElements();
        String[] lineStyles = getStyleManager().listLineStyleNames();
        for (String name : lineStyles) {
            box.addElement(name);
        }
        savedLineStyles.setSelectedItem(selected);
    }

    /**
     * Refresh the GUI list of node styles to match the style manager's list.
     */
    public void refreshNodeStyleList() {
        String selected = (String) savedNodeStyles.getSelectedItem();
        DefaultComboBoxModel<String> box = (DefaultComboBoxModel<String>) savedNodeStyles.getModel();
        box.removeAllElements();
        String[] nodeStyles = getStyleManager().listNodeStyleNames();
        for (String name : nodeStyles) {
            box.addElement(name);
        }
        savedNodeStyles.setSelectedItem(selected);
    }

    /**
     * Saves this FlowchartWindow's current flowchart to a destination stream.
     * This function flushes and closes the OutputStream.
     *
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
        } catch (ArrayIndexOutOfBoundsException aio) {
            System.err.println("Did not correctly predict the number of bytes: "
                    + aio + "\nexpected only " + data.length + " bytes");
        } catch (IOException e) {
            System.err.println("Could not write flowchart to output stream: " + e);
        }
    }

    /**
     * Refreshes the title of the window to show the file that is the "current"
     * file.
     */
    public void refreshWindowTitle() {
        setTitle("Process Sketcher | " + file.getName().substring(0, file.getName().length() - 4));
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
        //Configure the file chooser so it can only act on .prsf files.
        fc.setFileFilter(new FileNameExtensionFilter("Process Sketcher files (*." + FILE_EXTENSION + ")", FILE_EXTENSION));
        int response = fc.showSaveDialog(this);
        if (response == JFileChooser.APPROVE_OPTION) {
            file = fc.getSelectedFile();
            if (!file.getName().endsWith("." + FILE_EXTENSION)) {
                file = new File(file.getAbsolutePath() + "." + FILE_EXTENSION);
            }
            saveFlowchart();
        }
    }

    /**
     * Opens a file chooser so the user can open the current flowchart.
     */
    public void openFlowchart() {
        //Configure the file chooser so it can only act on .prsf files.
        fc.setFileFilter(new FileNameExtensionFilter("Process Sketcher files (*." + FILE_EXTENSION + ")", FILE_EXTENSION));
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
     *
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

    /**
     * Get the node that all other nodes are branching off from. A node that is
     * the parent node for all lines connected to it is defined as a root, and
     * this function returns the first root node that it can find.
     *
     * @return the root node, or <code>null</code> if there is no root node.
     */
    private Node getRootNode() {
        for (Node n : flowchart.getNodes()) {
            if (!n.getLinesConnected().isEmpty()) {
                boolean allParent = true;
                for (NodeLine nl : n.getLinesConnected()) {
                    if (!nl.getParent().equals(n)) {
                        allParent = false;
                    }
                }
                if (allParent) {
                    return n;
                }
            }
        }
        return null;
    }

    /**
     * Automatically sorts the flowchart in this window vertically.
     */
    public void autoformatVertically() {
        Node rootNode = getRootNode();
        ArrayList<Node> sorted = new ArrayList<>();
        // move the root node to the orgin
        sorted.add(rootNode);
        rootNode.setX(0);
        rootNode.setY(0);
        positionNodesVertically(rootNode, 
                rootNode.getChildNodes(), 
                sorted, 50, 150); 
        redrawView();
    }
    
    /**
     * A recursive method that auto-formats the flowchart vertically
     * @param parent The parent node to start positioning everything from
     * @param nodes The children nodes
     * @param sorted An array to keep track of the nodes that were already sorted
     * @param paddingX padding for the x axis
     * @param paddingY padding for the y axis
     */
    public void positionNodesVertically(Node parent, ArrayList<Node> nodes, ArrayList<Node> sorted, float paddingX, float paddingY) {
        float currentX = parent.getX();
        for (int i=0; i<nodes.size(); i++) {
            // position the node
            nodes.get(i).setX(currentX);
            currentX += nodes.get(i).getWidth() + paddingX;
            
            nodes.get(i).setY(parent.getY() + parent.getHeight() + paddingY);
            
            sorted.add(nodes.get(i));
            
            ArrayList<Node> children =  nodes.get(i).getChildNodes();
            if (!children.isEmpty()) {
                positionNodesVertically( nodes.get(i), children, sorted,paddingX, paddingY);
                // find the smallest and the largest Y of the newly sorted list
                Node smallestX = children.get(0);
                for (int j=1; j<children.size(); j++) {
                    if (children.get(j).getX() < smallestX.getX()) {
                        smallestX = children.get(j);
                    }
                }
                Node largestX = children.get(0);
                for (int j=1; j<children.size(); j++) {
                    if (children.get(j).getX() + children.get(j).getWidth() >
                            largestX.getX() + largestX.getWidth()) {
                        largestX = children.get(j);
                    }
                }
                
                // shift all previously positioned nodes by the height of the newly sorted children
                float shiftAmount = -(largestX.getX() + largestX.getWidth() - smallestX.getX());
                // shift all the sorted nodes up
                for (Node n : sorted) {
                    n.setX(n.getX() + (shiftAmount/2));
                }

                 // shift the base point for the unsorted nodes down
                currentX = nodes.get(i).getX() + nodes.get(i).getWidth() - (shiftAmount/2) + paddingX;
            }
        }
        // center the parent to the children when you're done
        float averageXPos = 0;
        for (Node n : nodes) {
            averageXPos += n.getX();
        }
        averageXPos = averageXPos / nodes.size();
        parent.setX(averageXPos);
    }
    
    /**
     * Automatically sorts the flowchart in this window horizontally.
     */
    public void autoformatHorizontally() {
        Node rootNode = getRootNode();
        ArrayList<Node> sorted = new ArrayList<>();
         // move the root node to the orgin
        sorted.add(rootNode);
        rootNode.setX(0);
        rootNode.setY(0);
        positionNodesHorizontally(rootNode, 
                rootNode.getChildNodes(), 
                sorted, 50, 150);
        redrawView();
    }
    
    /**
     * A recursive method that auto-formats the flowchart horizontally
     * @param parent The parent node to start positioning everything from
     * @param nodes The children nodes
     * @param sorted An array to keep track of the nodes that were already sorted
     * @param paddingX padding for the x axis
     * @param paddingY padding for the y axis
     */
    public void positionNodesHorizontally(Node parent, ArrayList<Node> nodes, ArrayList<Node> sorted, float paddingY, float paddingX) {
        float currentY = parent.getY();
        for (int i=0; i<nodes.size(); i++) {
            // position the node
            nodes.get(i).setY(currentY);
            currentY += nodes.get(i).getHeight() + paddingY;
            
            nodes.get(i).setX(parent.getX() + parent.getWidth() + paddingX);
            
            sorted.add(nodes.get(i));
            
            ArrayList<Node> children =  nodes.get(i).getChildNodes();
            if (!children.isEmpty()) {
                positionNodesHorizontally( nodes.get(i), children, sorted,paddingY, paddingX);
                // find the smallest and the largest Y of the newly sorted list
                Node smallestY = children.get(0);
                for (int j=1; j<children.size(); j++) {
                    if (children.get(j).getY() < smallestY.getY()) {
                        smallestY = children.get(j);
                    }
                }
                Node largestY = children.get(0);
                for (int j=1; j<children.size(); j++) {
                    if (children.get(j).getY() + children.get(j).getHeight() >
                            largestY.getY() + largestY.getHeight()) {
                        largestY = children.get(j);
                    }
                }
                
                // shift all previously positioned nodes by the height of the newly sorted children
                float shiftAmount = -(largestY.getY() + largestY.getHeight() - smallestY.getY());
                // shift all the sorted nodes up
                for (Node n : sorted) {
                    n.setY(n.getY() + (shiftAmount/2));
                }

                 // shift the base point for the unsorted nodes down
                currentY = nodes.get(i).getY() + nodes.get(i).getHeight() - (shiftAmount/2) + paddingY;
            }
        }
        // center the parent to the children when you're done
        float averageYPos = 0;
        for (Node n : nodes) {
            averageYPos += n.getY();
        }
        averageYPos = averageYPos / nodes.size();
        parent.setY(averageYPos);
    }
    
    
    /**
     * Shift all of the given nodes and their children recursively on the Y axis.
     * @param nodes The nodes and their children to shift.
     * @param amount The amount to shift it by.
     */
    public void shiftNodesVertically(ArrayList<Node> nodes, float amount) {
        if (nodes.isEmpty()) {
            return;
        }
        for (Node n : nodes) {
            n.setY(n.getY() + amount);
            shiftNodesVertically(n.getChildNodes(), amount);
        }
    }
    
     /**
     * Shift all of the given nodes and their children recursively on the X axis.
     * @param nodes The nodes and their children to shift.
     * @param amount The amount to shift it by.
     */
    public void shiftNodesHorizontally(ArrayList<Node> nodes, float amount) {
        if (nodes.isEmpty()) {
            return;
        }
        for (Node n : nodes) {
            n.setX(n.getX() + amount);
            shiftNodesHorizontally(n.getChildNodes(), amount);
        }
    }    
    /**
     * Gets the name of the file that this flowchart window is.
     * @return the name of this flowchart.
     */
    public String getFlowchartName() {
        return getTitle().substring("Process Sketcher | ".length());
    }
    
    /**
     * Exports the flowchart to an image.
     */
    public void exportToImage() {
        //Configure the file chooser so it can only act on .prsf files.
        fc.setFileFilter(new FileNameExtensionFilter("Portable Network Graphics image (*.png)", "png"));
        
        int response = fc.showSaveDialog(this);
        if (response == JFileChooser.APPROVE_OPTION) {
            try {
                BufferedImage image = flowchart.toImage(this);
                OutputStream os = Files.newOutputStream(fc.getSelectedFile().toPath());
                boolean status = ImageIO.write(image, "png", os);
                if (!status) {
                    System.err.println("Can't write this image because there"
                            + " is no writer for it.");
                }
                os.flush();
                os.close();
                if (status) {
                    System.out.println("Wrote image " + fc.getSelectedFile());
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Unable to export image to file: " + ex, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
   /**
    * Shows a print preview dialog.
    * @param marginLeft left margin, in 1/72 of an inch.
    * @param marginRight right margin, in 1/72 of an inch.
    * @param marginTop top margin, in 1/72 of an inch.
    * @param marginBottom bottom margin, in 1/72 of an inch
    * @param width the page width
    * @param height the page height
    * @param job the printer job.
    */
    public void showPrintPreview(int marginLeft, int marginRight, int marginTop, int marginBottom, int width, int height, PrinterJob job) {
        PrintPreviewDialog ppd = new PrintPreviewDialog(marginLeft, marginRight, marginTop, marginBottom, width, height, flowchart.toImage(this), job, this);
        ppd.setIconImage(ProcessSketcher.mainIconSmaller);
    }
}
