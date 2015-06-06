/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.style;

import bropals.flowy.Camera;
import bropals.flowy.data.Node;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.ArrayList;

/**
 *
 * @author Jonathon
 */
public enum Shape {
    /**
     * Represents a step of a larger process.
     *//**
     * Represents a step of a larger process.
     *//**
     * Represents a step of a larger process.
     *//**
     * Represents a step of a larger process.
     *//**
     * Represents a step of a larger process.
     *//**
     * Represents a step of a larger process.
     *//**
     * Represents a step of a larger process.
     *//**
     * Represents a step of a larger process.
     */
    ACTION, 
    /**
     * Indicates a step where two or more sub-lists or sub-processes
     * become one.
     */
    MERGE, 
    /**
     * Marks the start or end point of a system.
     */
    START_END, 
    /**
     * A decision or branching point.
     */
    DECISION, 
    /**
     * Indicates a delay in a process
     */
    DELAY;

    private Polygon canvasPolyFromWorldCoordinates(Point.Float[] points, Camera c) {
        int x[] = new int[points.length];
        int y[] = new int[points.length];
        for (int i=0; i<points.length; i++) {
            x[i] = c.convertWorldToCanvasX(points[i].x);
            y[i] = c.convertWorldToCanvasY(points[i].y);
        }
        return new Polygon(x, y, points.length);
    }
    
    /**
     * Render a node according to what the style is.
     *
     * @param node The node that is being rendered
     * @param camera The camera used to transform it
     * @param g The graphics that it will be drawn to
     * @param blinkCursor Whether or not the cursor will be showing for editing
     * text
     * @param cursorLocation The lcoation of the cursor for editing text
     */
    public void renderShape(Node node, Camera camera, Graphics g, boolean blinkCursor, int cursorLocation) {
        Graphics2D g2 = (Graphics2D) g;

        if (cursorLocation > node.getInnerText().length()) {
            cursorLocation = node.getInnerText().length();
        }
        Point.Float p1, p2, p3, p4;
        Point ip1, ip2, ip3, ip4;
        switch (this) {
            case ACTION:
                p1 = new Point.Float(node.getX(), node.getY());
                p3 = new Point.Float(node.getX() + node.getWidth(), node.getY() + node.getHeight());

                // use canvas units to draw the node
                ip1 = camera.convertWorldToCanvas(p1);
                ip3 = camera.convertWorldToCanvas(p3);

                g.setColor(node.getStyle().getFillColor());
                g.fillRect(ip1.x, ip1.y, ip3.x-ip1.x, ip3.y-ip1.y);
                
                g2.setStroke(new BasicStroke(node.getStyle().getBorderSize()));
                g.setColor(node.getStyle().getBorderColor());
                g.drawRect(ip1.x, ip1.y, ip3.x-ip1.x, ip3.y-ip1.y);

                g2.setStroke(new BasicStroke(1));
                break;
            case DECISION:
                p1 = new Point.Float(node.getX() + (node.getWidth()/2), node.getY());
                p2 = new Point.Float(node.getX() + node.getWidth(), node.getY() + (node.getHeight()/2));
                p3 = new Point.Float(node.getX() + (node.getWidth()/2), node.getY() + node.getHeight());
                p4 = new Point.Float(node.getX(), node.getY() + (node.getHeight()/2));
                
                Polygon diamond = canvasPolyFromWorldCoordinates(new Point.Float[]{p1, p2, p3, p4}, camera);
                g.setColor(node.getStyle().getFillColor());
                g.fillPolygon(diamond);
                
                g2.setStroke(new BasicStroke(node.getStyle().getBorderSize()));
                g.setColor(node.getStyle().getBorderColor());
                g.drawPolygon(diamond);
                g2.setStroke(new BasicStroke(1));
                break;
            case START_END:
                p1 = new Point.Float(node.getX(), node.getY());
                p3 = new Point.Float(node.getX() + node.getWidth(), node.getY() + node.getHeight());

                // use canvas units to draw the node
                ip1 = camera.convertWorldToCanvas(p1);
                ip3 = camera.convertWorldToCanvas(p3);
                
                g.setColor(node.getStyle().getFillColor());
                g.fillOval(ip1.x, ip1.y, ip3.x-ip1.x, ip3.y-ip1.y);
                
                g2.setStroke(new BasicStroke(node.getStyle().getBorderSize()));
                g.setColor(node.getStyle().getBorderColor());
                g.drawOval(ip1.x, ip1.y, ip3.x-ip1.x, ip3.y-ip1.y);

                g2.setStroke(new BasicStroke(1));
                break;
            case MERGE:
                p1 = new Point.Float(node.getX() + (node.getWidth()/2), node.getY() + node.getHeight() );
                p2 = new Point.Float(node.getX(), node.getY() );
                p3 = new Point.Float(node.getX() + node.getWidth(), node.getY());
                                
                Polygon triangle = canvasPolyFromWorldCoordinates(new Point.Float[]{p1, p2, p3}, camera);
                
                g.setColor(node.getStyle().getFillColor());
                g.fillPolygon(triangle);
                
                g2.setStroke(new BasicStroke(node.getStyle().getBorderSize()));
                g.setColor(node.getStyle().getBorderColor());
                g.drawPolygon(triangle);
                g2.setStroke(new BasicStroke(1));
                
                break;
            case DELAY:
                
                break;
        }

        if (node.getInnerText().length() > 0) {
            g2.setColor(node.getStyle().getFontColor());
            Font transformedFont = node.getStyle().getFontType().deriveFont(node.getStyle().getFontSize() / camera.getZoom());
            g2.setFont(transformedFont);

            float padding = 5;
            float startEverythingX = (float) camera.convertWorldToCanvasX(node.getX()) + padding; // canvas units
            float startEverythingY = (float) camera.convertWorldToCanvasX(node.getY()) + g2.getFontMetrics().getHeight() + padding; // canvas units
            // the width each row can't get longer than (in canvas units)
            float width = node.getWidth() - (padding * 2);

            // seperate the text into rows according to their lengths, not extending canvas units
            // the array for the text. Every element of the array represents one row of text
            ArrayList<String> text = new ArrayList<>();
            String[] words = node.getInnerText().split(" ");
            text.add(words[0]); // first word goes into the first row

            // what row is currently being filled
            int rowOn = 0;
            for (int i = 1; i < words.length; i++) {
                String testResultRow = text.get(rowOn) + " " + words[i]; // add a space after each word
                // if the word is NOT a new line character and the new word will fit
                if (!words[i].equals("\n")
                        && g.getFontMetrics().getStringBounds(testResultRow, g).getWidth() < width) {
                    // remove the old row
                    text.remove(rowOn);
                    // make this the new row
                    text.add(rowOn, testResultRow);
                    // otherwise if there isn't enough space on this new row
                } else {
                    // add the word to the next row
                    text.add(words[i]);
                    rowOn++;
                }
            }

            String entireString = "";
            for (String str : text) {
                entireString += str;
            }

            int lineHeight = g.getFontMetrics().getHeight();

            int sumOfCharsPrevRows = 0;
            int rowCursorInside = 0;

            // draw according to how the row was organized
            for (int r = 0; r < text.size(); r++) {
                g.drawString(text.get(r), (int) startEverythingX,
                        (int) startEverythingY + (r * lineHeight));
                if (sumOfCharsPrevRows + text.get(r).length() >= cursorLocation) {
                    rowCursorInside = r;
                } else {
                    sumOfCharsPrevRows += text.get(r).length();
                }
            }

            if (blinkCursor) {
                // get the offset for where to draw the cursor
                int thisRowOffset = (int) g.getFontMetrics().getStringBounds(
                        (entireString.substring(sumOfCharsPrevRows, cursorLocation)), g).getWidth();
                //draw the cursor
                g2.fillRect((int) startEverythingX + thisRowOffset,
                        (int) startEverythingY + (lineHeight * (rowCursorInside - 1)) - 2,
                        3, lineHeight + 4);
            }
        }
    }
}
