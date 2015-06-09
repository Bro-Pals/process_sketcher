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
package bropals.flowy.style;

import bropals.flowy.Camera;
import bropals.flowy.EventManager;
import bropals.flowy.data.Node;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;

/**
 * Represents the shape of a node, which also indicates its meaning
 * to the flowchart.
 * @author Jonathon
 */
public enum Shape {
    /**
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
     * Indicates a delay in a process.
     */
    DELAY,
    /**
     * Represents material or information entering or leaving a system.
     */
    INPUT_OUTPUT,
    /**
     * A printed document or report.
     */
    DOCUMENT,
    /**
     * A Shape that doesn't render anything
     */
    NONE;
    
    /**
     * Gets the meaning of the shape.
     * @return the meaning of the shape.
     */
    public String getMeaning() {
        switch(this) {
            case ACTION:
                return "Represents a step of a larger process.";
            case DECISION:
                return "A decision or branching point.";
            case DELAY:
                return "Indicates a delay in a process.";
            case DOCUMENT:
                return "A printed document or report.";
            case INPUT_OUTPUT:
                return "Represents material or information entering or "
                        + "leaving a system.";
            case MERGE:
                return "Indicates a step where two or more sub-lists or "
                        + "sub-processes become one.";
            case START_END:
                return "Marks the start or end point of a system.";
        }
        return null;
    }

    /**
     * Gets the byte representation of this shape.
     * @return the byte representation of this shape.
     */
    public byte toByte() {
        switch(this) {
            case ACTION:
                return 0;
            case DECISION:
                return 1;
            case DELAY:
                return 2;
            case DOCUMENT:
                return 3;
            case INPUT_OUTPUT:
                return 4;
            case MERGE:
                return 5;
            case START_END:
                return 6;
        }
        return -1;
    }
    
    /**
     * Interprets the given byte as a shape.
     * @param b the byte to interpret.
     * @return the shape that the byte represents.
     */
    public static Shape fromByte(byte b) {
        switch(b) {
            case 0:
                return ACTION;
            case 1:
                return DECISION;
            case 2:
                return DELAY;
            case 3:
                return DOCUMENT;
            case 4:
                return INPUT_OUTPUT;
            case 5:
                return MERGE;
            case 6:
                return START_END;
        }
        return null;
    }
    
    @Override
    public String toString() {
        switch(this) {
            case ACTION:
                return "Action";
            case DECISION:
                return "Decision";
            case DELAY:
                return "Delay";
            case DOCUMENT:
                return "Document";
            case INPUT_OUTPUT:
                return "Input/Output";
            case MERGE:
                return "Merge";
            case START_END:
                return "Start/End";
        }
        return null;
    }
    
    /**
     * Interprets a string as a shape.
     * @param str the string to interpret.
     * @return the interpreted shape, or <code>null</code> if no shape
     * was determined.
     */
    public static Shape fromString(String str) {
        switch(str) {
            case "Action":
                return ACTION;
            case "Decision":
                return DECISION;
            case "Delay":
                return DELAY;
            case "Document":
                return DOCUMENT;
            case "Input/Output":
                return INPUT_OUTPUT;
            case "Merge":
                return MERGE;
            case "Start/End":
                return START_END;
        }
        return null;
    }
    
    /**
     * Creates a polygon in canvas coordinates from a set of points in world
     * coordinates.
     * @param points a set of points in world coordinates to construct
     * the polygon out of.
     * @param c the camera for the view.
     * @return a polygon in canvas coordinates, constructed from the given 
     * set of world coordinate points.
     */
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
     * Constructs and draws a canvas coordinate polygon from a set of
     * world coordinate points.
     * @param g the graphics context to draw the polygon.
     * @param node the node that the polygon represents.
     * @param points the set of points to construct the polygon from.
     * @param c the camera for the view.
     */
    private void drawPolygonFromPoints(Graphics2D g, Node node, Point.Float[] points, Camera c) {
        Polygon shape = canvasPolyFromWorldCoordinates(points, c);

        g.setColor(node.getStyle().getFillColor());
        g.fillPolygon(shape);

        g.setStroke(new BasicStroke(node.getStyle().getBorderSize()));
        g.setColor(node.getStyle().getBorderColor());
        g.drawPolygon(shape);
        g.setStroke(new BasicStroke(1));
    }
    
    /**
     * Render a node according to what the style is.
     *
     * @param node The node that is being rendered
     * @param camera The camera used to transform it
     * @param g The graphics that it will be drawn to
     * @param blinkCursor Whether or not the cursor will be showing for editing
     * text
     * @param backgroundColor the background color of the view to draw this shape in.
     * @param cursorLocation The location of the cursor for editing text
     */
    public void renderShape(Node node, Camera camera, Graphics g, boolean blinkCursor, int cursorLocation, Color backgroundColor) {
        // don't draw anything if the shape is nothing
        if (node.getStyle().getShape() == Shape.NONE) {
            return;
        }
        
        Graphics2D g2 = (Graphics2D) g;

        Font transformedFont = node.getStyle().getFontType().deriveFont(node.getStyle().getFontSize() / camera.getZoom());
        g2.setFont(transformedFont);   
        FontMetrics fm = g2.getFontMetrics();
        
        //Text wrapping values. Don't edit them to use these default values
        int padding = 5;
        int startEverythingX = camera.convertWorldToCanvasX(node.getX()) + padding; // canvas units
        int startEverythingY = camera.convertWorldToCanvasY(node.getY()) + fm.getHeight() + padding; // canvas units
        // the width each row can't get longer than (in canvas units)
        int width = camera.convertWorldToCanvasX(node.getWidth()) - (padding * 2);
        
        if (cursorLocation > node.getInnerText().length()) {
            cursorLocation = node.getInnerText().length();
        } else if (cursorLocation < 0) {
            cursorLocation = 0;
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
                
                drawPolygonFromPoints(g2, node, new Point.Float[] { p1, p2, p3, p4 }, camera);
                
                startEverythingX += (node.getWidth()/4) / camera.getZoom();
                startEverythingY += (node.getHeight()/4) / camera.getZoom();
                width -= (node.getWidth()/2) / camera.getZoom();
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
                
                float constant = (float)(1-(Math.sqrt(2)/2))/2;
                startEverythingX += (node.getWidth()*constant) / camera.getZoom();
                startEverythingY += (node.getHeight()*constant) / camera.getZoom();
                width -= (node.getWidth()*constant*2) / camera.getZoom();
                break;
            case MERGE:
                p1 = new Point.Float(node.getX() + (node.getWidth()/2), node.getY() + node.getHeight() );
                p2 = new Point.Float(node.getX(), node.getY() );
                p3 = new Point.Float(node.getX() + node.getWidth(), node.getY());
                                
                drawPolygonFromPoints(g2, node, new Point.Float[]{p1, p2, p3}, camera);
                
                startEverythingX += (node.getWidth()/4) / camera.getZoom();
                width -= (node.getWidth()/2) / camera.getZoom();
                break;
            case DELAY:
                p1 = new Point.Float(node.getX(), node.getY());
                p2 = new Point.Float(node.getX() + (int)((float)node.getWidth()*2/3), node.getY());
                p3 = new Point.Float(node.getX(), node.getY() + node.getHeight());
                p4 = new Point.Float(node.getX() + (int)((float)node.getWidth()*2/3), node.getY() + node.getHeight());
                Point.Float p5 = new Point.Float(node.getX() + node.getWidth(), node.getY() + node.getHeight());
                
                ip1 = camera.convertWorldToCanvas(p1);
                ip2 = camera.convertWorldToCanvas(p2);
                ip3 = camera.convertWorldToCanvas(p3);
                ip4 = camera.convertWorldToCanvas(p4);
                Point ip5 = camera.convertWorldToCanvas(p5);
                
                g.setColor(node.getStyle().getFillColor());
                g.fillRect(ip1.x, ip1.y, ip4.x-ip1.x, ip4.y-ip1.y);
                int third = (ip5.x-ip2.x);
                g2.fillArc(ip2.x-third, ip2.y, third*2, ip5.y-ip2.y, 90, -180);
                
                g.setColor(node.getStyle().getBorderColor());
                g2.setStroke(new BasicStroke(node.getStyle().getBorderSize()));
                g.drawLine(ip1.x, ip1.y, ip2.x, ip2.y);
                g.drawLine(ip1.x, ip1.y, ip3.x, ip3.y);
                g.drawLine(ip3.x, ip3.y, ip4.x, ip4.y);
                g2.drawArc(ip2.x-third, ip2.y, third*2, ip5.y-ip2.y, 90, -180);
                
                g2.setStroke(new BasicStroke(1));
                
                width -= third;
                break;
            case INPUT_OUTPUT:
                p1 = new Point.Float(node.getX() + (int)((float)node.getWidth()/4), node.getY());
                p2 = new Point.Float(node.getX() + node.getWidth(), node.getY());
                p3 = new Point.Float(node.getX() + (int)((float)node.getWidth()*3/4), node.getY() + node.getHeight());
                p4 = new Point.Float(node.getX(), node.getY() + node.getHeight());
                
                drawPolygonFromPoints(g2, node, new Point.Float[]{p1, p2, p3, p4}, camera);           
                
                startEverythingX += (node.getWidth()/4) / camera.getZoom();
                width -= (node.getWidth()/2) / camera.getZoom();
                break;
            case DOCUMENT:
                p1 = new Point.Float(node.getX(), node.getY());
                p2 = new Point.Float(node.getX() + node.getWidth(), node.getY());
                p3 = new Point.Float(node.getX(), node.getY() + (int)( (float)node.getHeight()*7/8));
                p4 = new Point.Float(node.getX() + (node.getWidth()/2), node.getY() + node.getHeight());
                
                ip1 = camera.convertWorldToCanvas(p1);
                ip2 = camera.convertWorldToCanvas(p2);
                ip3 = camera.convertWorldToCanvas(p3);
                ip4 = camera.convertWorldToCanvas(p4);
                
                g.setColor(node.getStyle().getFillColor());
                g.fillRect(ip1.x, ip1.y, ip2.x-ip1.x, ip3.y-ip1.y);
                int eighthHeight = ip4.y-ip3.y;
                int halfWidth = ip4.x-ip1.x;
                g.fillArc(ip3.x, ip3.y-eighthHeight, halfWidth, eighthHeight*2, 180, 180);
                g.setColor(backgroundColor);
                g.fillArc(ip4.x, ip3.y - eighthHeight, halfWidth, eighthHeight*2, 0, 180);
                
                g.setColor(node.getStyle().getBorderColor());
                g2.setStroke(new BasicStroke(node.getStyle().getBorderSize()));
                g.drawLine(ip1.x, ip1.y, ip2.x, ip2.y);
                g.drawLine(ip2.x, ip2.y, ip2.x, ip3.y);
                g.drawLine(ip1.x, ip1.y, ip3.x, ip3.y);
                g.drawArc(ip3.x, ip3.y-eighthHeight, halfWidth, eighthHeight*2, 180, 180);
                g.drawArc(ip4.x, ip3.y - eighthHeight, halfWidth, eighthHeight*2, 0, 180);
                g2.setStroke(new BasicStroke(1));
                break;
        }

        // only draw text if there is text
        if (node.getInnerText().length() > 0) {
            g2.setColor(node.getStyle().getFontColor());
            
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

                if (g.getFontMetrics().getStringBounds(testResultRow, g).getWidth() < width) {
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

            int lineHeight = g.getFontMetrics().getHeight();

            int sumOfCharsPrevRows = 0;

            boolean drawnCursorYet = false;
            
            // draw according to how the row was organized
            for (int r = 0; r < text.size(); r++) {
                g.drawString(text.get(r), (int) startEverythingX,
                        (int) startEverythingY + (r * lineHeight));
                if (sumOfCharsPrevRows + text.get(r).length() >= cursorLocation) {
                   if (!drawnCursorYet && blinkCursor) {
                        // get the offset for where to draw the cursor
                        int thisRowOffset = (int)(g.getFontMetrics().getStringBounds(
                                (text.get(r).substring(0, cursorLocation - sumOfCharsPrevRows)), g).getWidth() 
                                / camera.getZoom());
                        //draw the cursor
                        g2.fillRect((int)startEverythingX + thisRowOffset, 
                            (int)startEverythingY + (lineHeight * (r - 1)) - 2, 
                            3, lineHeight + 4);
                        drawnCursorYet = true;
                    }
                } else {
                    sumOfCharsPrevRows += text.get(r).length();
                }
            }

        // only draw the cursor is there is no text to go through
        } else {
            int lineHeight = g.getFontMetrics().getHeight();
            // if the cursor is still not drawn yet and there is no text
            if (blinkCursor && node.getInnerText().length() == 0) {
                //draw the cursor at the start
                g2.fillRect((int)startEverythingX, 
                    (int)startEverythingY - lineHeight - 2, 
                    3, lineHeight + 4);
            }
        }
    }
}
