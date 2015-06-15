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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
    private BufferedImage flowchartImage;
    private BufferedImage[] split;
    private PrinterJob job;

    /**
     * Displays a print preview. All pages have 1 inch margins.
     *
     * @param marginLeft left margin, in 1/72 of an inch.
     * @param marginRight right margin, in 1/72 of an inch.
     * @param marginTop top margin, in 1/72 of an inch.
     * @param pageWidth the width of each page, in 1/72 of an inch.
     * @param pageHeight the height of each page, in 1/72 of an inch.
     * @param flowchartImage the image to print preview of the flowchart
     * @param marginBottom bottom margin, in 1/72 of an inch
     * @param job the printer job.
     * @param owner the flowchart window
     */
    public PrintPreviewDialog(int marginLeft, int marginRight, int marginTop, int marginBottom, int pageWidth, int pageHeight, BufferedImage flowchartImage, PrinterJob job, JFrame owner) {
        super(owner);
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
        this.marginTop = marginTop;
        this.marginBottom = marginBottom;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
        this.flowchartImage = flowchartImage;
        this.job = job;
        int pagesWide = ((flowchartImage.getWidth() / pageWidth) + 1);
        int pagesHigh = ((flowchartImage.getHeight() / pageHeight) + 1);
        split = new BufferedImage[pagesWide*pagesHigh];
        for (int y=0; y<pagesHigh; y++) {
            for (int x=0; x<pagesWide; x++) {
                int width = pageWidth;
                int height = pageHeight;
                if (y == pagesHigh-1) {
                    height = flowchartImage.getHeight() - (y*pageHeight);
                }
                if (x == pagesWide-1) {
                    width = flowchartImage.getWidth() - (x*pageWidth);
                }
                split[(y*pagesWide)+x] = flowchartImage.getSubimage(
                        x*pageWidth, y*pageHeight, width, height);
            }
        }
        JComponent view = new JComponent() {
            @Override
            public void paintComponent(Graphics g) {
                drawPrintPreview(g, getWidth(), getHeight());
            }
        };
        view.setPreferredSize(new Dimension(pageWidth+marginLeft+marginRight+50, pageHeight+marginTop+marginBottom+(pagesHigh*(pageHeight+marginLeft+marginBottom+25))));
        JScrollPane pane = new JScrollPane(view);
        pane.setPreferredSize(new Dimension((int)view.getPreferredSize().width+80, 500));
        add(pane, BorderLayout.CENTER);
        JPanel panel = new JPanel(new FlowLayout());
        JButton print = new JButton("Print flowchart");
        print.addActionListener(new PrintButtonListener(this));
        panel.add(print);
        add(panel, BorderLayout.NORTH);
        pack();
        setTitle("Print Preview");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    /**
     * Draws the print preview.
     *
     * @param g the graphics context to draw.
     * @param width the component width
     * @param height the component height
     */
    public void drawPrintPreview(Graphics g, int width, int height) {
        Graphics2D g2d = (Graphics2D)g;
        g.setColor(new Color(153, 153, 255));
        g.fillRect(0, 0, width, height);
        int y = 0;
        for (int i=0; i<split.length; i++) {
            g.setColor(Color.WHITE);
            g.fillRect(25, y+25, marginLeft+pageWidth+marginRight, marginTop+pageHeight+marginBottom);
            g.setColor(Color.BLACK);
            g.drawRect(25, y+25, marginLeft+pageWidth+marginRight, marginTop+pageHeight+marginBottom);
            drawPage(g, 25, y+25, i);
            g2d.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{ 12 }, 20));
            g.drawRect(25+marginLeft, y+25+marginTop, pageWidth, pageHeight);
            g2d.setStroke(new BasicStroke(1));
            y += (50+pageHeight+marginTop+marginBottom);
        }
    }

    /**
     * Draws a page with the given graphics
     * @param g the graphics context
     * @param x the page x loc
     * @param y the page y loc
     * @param pageCount which page it is
     */
    public void drawPage(Graphics g, int x, int y, int pageCount) {
        g.drawImage(split[pageCount], x+marginLeft, y+marginTop, null);   
    }
    
    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex<split.length) {
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
            job.setPrintable(ppd);
            if (job.printDialog()) {
                try {
                    job.print();
                    dispose();
                } catch (PrinterException pe) {
                    System.err.println("Unable to print: " + pe);
                }
            }
        }

    }
}
