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

import bropals.processsketcher.data.Flowchart;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.regex.Pattern;
import javax.print.attribute.standard.PrinterResolution;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

/**
 * Displays a print preview.
 *
 * @author Jonathon
 */
public class PrintPreviewDialog extends JDialog implements Printable {

    private int marginLeft;
    private int marginRight;
    private int marginTop;
    private int marginBottom;
    private int pageWidth;
    private int pageHeight;
    private Flowchart flowchart;
    private BufferedImage[] split;
    private PrinterJob job;
    private FlowchartWindow window;
    private JComponent view;
    private PageFormat format;
    private JCheckBox margins;
    private JCheckBox pageCount;
    private JCheckBox fitToPage;
    private JTextField ignore;
    private ArrayList<Integer> ignoring;
    
    /**
     * Displays a print preview. All pages have 1 inch margins.
     *
     * @param marginLeft left margin, in 1/72 of an inch.
     * @param marginRight right margin, in 1/72 of an inch.
     * @param marginTop top margin, in 1/72 of an inch.
     * @param pageWidth the width of each page, in 1/72 of an inch.
     * @param pageHeight the height of each page, in 1/72 of an inch.
     * @param flowchart the flowchart to print
     * @param marginBottom bottom margin, in 1/72 of an inch
     * @param job the printer job.
     * @param owner the flowchart window
     * @param format the page format to use
     */
    public PrintPreviewDialog(int marginLeft, int marginRight, int marginTop, int marginBottom, int pageWidth, int pageHeight, Flowchart flowchart, PrinterJob job, FlowchartWindow owner, PageFormat format) {
        super(owner);
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.flowchart = flowchart;
        this.job = job;
        this.format = format;
        window = owner;
        view = new JComponent() {
            @Override
            public void paintComponent(Graphics g) {
                drawPrintPreview(g, getWidth(), getHeight());
            }
        };
        JScrollPane pane = new JScrollPane(view);
        pane.setPreferredSize(new Dimension(pageWidth + marginLeft + marginRight + 100, pageHeight + marginBottom + marginTop + 100));
        add(pane, BorderLayout.CENTER);
        JPanel panel = new JPanel(new FlowLayout());
        JButton print = new JButton("Print flowchart");
        print.addActionListener(new PrintButtonListener(this));
        margins = new JCheckBox("Draw Margins");
        margins.addActionListener(new RedrawViewListener());
        pageCount = new JCheckBox("Draw page count");
        pageCount.addActionListener(new RedrawViewListener());
        fitToPage = new JCheckBox("Fit to page");
        fitToPage.addActionListener(new RedrawViewListener());
        fitToPage.setSelected(true);
        ignore = new JTextField("Space separated list of pages to ignore");
        panel.add(print);
        panel.add(margins);
        panel.add(pageCount);
        panel.add(fitToPage);
        panel.add(ignore);
        add(panel, BorderLayout.NORTH);
        pack();
        setTitle("Print Preview");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Prepares the flowchart for being displayed and printed.
     * @param imageDPI the DPI of the printed image.
     */
    private void preparePrintImages() {
        BufferedImage flowchartImage = flowchart.toImage(window, marginTop);
        int pagesWide = 1;
        int pagesHigh = 1;
        if (fitToPage.isSelected()) {
            BufferedImage scaledImage = null;
            // scale the image to fit the page
            float scaleRatio = 1.0f;
            // if the page is landscape
            //if (pageWidth > pageHeight) {
                // if the image height is larger, scale according to the height
                if (flowchartImage.getHeight() >= flowchartImage.getWidth()) {
                    scaleRatio = (float)pageHeight / (float)flowchartImage.getHeight();
                } else {
                    // if the width is larger, scale according to the width
                    scaleRatio = (float)pageWidth / (float)flowchartImage.getWidth();
                }
            /*} else { // if the page is portrait
                // if the image height is smaller, scale according to the width
                if (flowchartImage.getHeight() <= flowchartImage.getWidth()) {
                    scaleRatio = (float)pageHeight / (float)flowchartImage.getHeight();
                } else {
                    // if the width is smaller, scale according to the width
                    scaleRatio = (float)pageWidth / (float)flowchartImage.getWidth();
                }
            }*/
            scaledImage = new BufferedImage((int)(scaleRatio * flowchartImage.getWidth()), 
                    (int)(scaleRatio * flowchartImage.getHeight()), BufferedImage.OPAQUE);
            scaledImage.getGraphics().drawImage(flowchartImage, 0, 0, 
                    scaledImage.getWidth(), scaledImage.getHeight(), null);
            flowchartImage = scaledImage;
        } else {
            pagesWide = (int)Math.ceil((double)flowchartImage.getWidth() / (double)pageWidth);
            pagesHigh = (int)Math.ceil((double)flowchartImage.getHeight() / (double)pageHeight);
        }
        split = new BufferedImage[pagesWide * pagesHigh];
        //System.out.println("Page count: " + split.length + " (Pages wide: " + pagesWide + ", Pages high: " + pagesHigh + ")");
        for (int y = 0; y < pagesHigh; y++) {
            for (int x = 0; x < pagesWide; x++) {
                int width = pageWidth;
                int height = pageHeight;
                if (y == pagesHigh - 1) {
                    height = flowchartImage.getHeight() - (y * pageHeight);
                }
                if (x == pagesWide - 1) {
                    width = flowchartImage.getWidth() - (x * pageWidth);
                }
                split[(y * pagesWide) + x] = flowchartImage.getSubimage(
                        x * pageWidth, y * pageHeight, width, height);
            }
        }
        view.setPreferredSize(new Dimension(pageWidth + marginLeft + marginRight + 50, split.length * (pageHeight + marginLeft + marginBottom + 25)));
    }

    /**
     * Draws the print preview.
     *
     * @param g the graphics context to draw.
     * @param width the component width
     * @param height the component height
     */
    public void drawPrintPreview(Graphics g, int width, int height) {
//        if (!fitToPage.isSelected() && split == null) {
            preparePrintImages();
//        }
        Graphics2D g2d = (Graphics2D) g;
        g.setColor(new Color(153, 153, 255));
        g.fillRect(0, 0, width, height);
        int y = 0;
        for (int i = 0; i < split.length; i++) {
            g.setColor(Color.WHITE);
            g.fillRect(25, y + 25, marginLeft + pageWidth + marginRight, marginTop + pageHeight + marginBottom);
            g.setColor(Color.BLACK);
            g.drawRect(25, y + 25, marginLeft + pageWidth + marginRight, marginTop + pageHeight + marginBottom);
            drawPage(g, 25, y + 25, i);
            y += (50 + pageHeight + marginTop + marginBottom);
        }
    }

    /**
     * Draws a page with the given graphics
     *
     * @param g the graphics context
     * @param x the page x loc
     * @param y the page y loc
     * @param pageCount which page it is
     */
    public void drawPage(Graphics g, int x, int y, int pageCount) {
        Graphics2D g2d = (Graphics2D)g;
        g.drawImage(split[pageCount], x + marginLeft, y + marginTop, null);
        if (margins.isSelected()) {
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{12}, 20));
            g.drawRect(x + marginLeft, y + marginTop, pageWidth, pageHeight);
            g2d.setStroke(new BasicStroke(1));
        }
        if (this.pageCount.isSelected()) {
            g.setFont(new Font(Font.SERIF, Font.PLAIN, 12));
            g.drawString("" + (pageCount+1), 
                    x + marginLeft + 5,
                    y + marginTop + g.getFontMetrics().getHeight() + 5);
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        while (ignoring.contains(pageIndex) && pageIndex < split.length) {
            pageIndex++;
        }
        if (pageIndex < split.length) {
            Graphics2D g2d = (Graphics2D)graphics;
            g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            drawPage(graphics, 0,
                    0, pageIndex);
            return PAGE_EXISTS;
        } else {
            return NO_SUCH_PAGE;
        }
    }

    class PrintButtonListener implements ActionListener {

        private PrintPreviewDialog ppd;

        public PrintButtonListener(PrintPreviewDialog ppd) {
            this.ppd = ppd;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (job.printDialog()) {
                try {
                    preparePrintImages();
                    job.setPrintable(ppd);
                    //Add pages to ignore
                    ignoring = new ArrayList<>();
                    String[] split = ignore.getText().split(Pattern.quote(" "));
                    for (int i=0; i<split.length; i++) {
                        try {
                            int toIgnore = Integer.parseInt(split[i])-1;
                            ignoring.add(toIgnore);
                        } catch(NumberFormatException nfe) {
                            //Just leave it
                        }
                    }
                    job.print();
                    dispose();
                } catch (PrinterException pe) {
                    System.err.println("Unable to print: " + pe);
                }
            }
        }

    }
    
    class RedrawViewListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Redraw");
            view.repaint();
        }
    }
}
