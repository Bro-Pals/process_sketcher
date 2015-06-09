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
import bropals.flowy.TextTypeManager;
import bropals.flowy.data.Node;
import bropals.flowy.data.NodeLine;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 * Represents the different types of lines.
 * @author Kevin
 */
public enum LineType {
    /**
     * A simple solid line.
     */
    SOLID, 
    /**
     * A dashed line.
     */
    DASHED, 
    /**
     * A dotted line.
     */
    DOTTED;

    /**
     * Gets the byte representation of this line type.
     * @return the byte representation of this line type.
     */
    public byte toByte() {
        switch (this) {
            case SOLID:
                return 0;
            case DASHED:
                return 1;
            case DOTTED:
                return 2;
        }
        return -1;
    }
    
    /**
     * Gets the line type from its byte representation.
     * @param b the byte representation.
     * @return the corresponding line type.
     */
    public static LineType fromByte(byte b) {
        switch (b) {
            case 0:
                return SOLID;
            case 1:
                return DASHED;
            case 2:
                return DOTTED;
        }
        return null;
    }
    
    @Override
    public String toString() {
        switch (this) {
            case SOLID:
                return "Solid";
            case DASHED:
                return "Dashed";
            case DOTTED:
                return "Dotted";
        }
        return null;
    }

    /**
     * Interprets a string as a line type.
     * @param str the string to interpret.
     * @return the interpreted line type, or <code>null</code> if
     * none could be interpreted.
     */
    public static LineType fromString(String str) {
        switch (str) {
            case "Solid":
                return SOLID;
            case "Dashed":
                return DASHED;
            case "Dotted":
                return DOTTED;
        }
        return null;
    }

    /**
     * Scoots a point to the nearest edge of a node. Used for line rendering.
     * @param point the point to scoot.
     * @param node the node to scoot the point to the edge of.
     */
    private void scootPointToEdge(Point.Float point, Node node) {
        //Vector going from the center to the point in question
        Point.Float diffVec = new Point.Float(node.getX() + node.getWidth() / 2 - point.x, node.getY() + node.getHeight() / 2 - point.y);
        if (Math.abs(diffVec.x) > Math.abs(diffVec.y)) {
            if (diffVec.x < 0) {
                point.x = node.getX() + node.getWidth();
            } else {
                point.x = node.getX();
            }
        } else {
            if (diffVec.y < 0) {
                point.y = node.getY() + node.getHeight();
            } else {
                point.y = node.getY();
            }
        }
    }

    /**
     * Renders this line tpe, changing the color of the graphics to match the
     * style.
     *
     * @param n The node line to render
     * @param camera The camera in the flowchart
     * @param g The graphics to draw it to
     * @param blinkCursor Whether or not the text cursor is showing for this
     * render
     * @param cursorLocation The location of the cursor for text editing
     * @param partCursorDrawing What part of the line the cursor is being drawn
     * on
     * @return An array of two points of integers, representing the start and
     * end point of the line in canvas units.
     */
    public Point[] renderLine(NodeLine n, Camera camera, Graphics g,
            boolean blinkCursor, int cursorLocation, int partCursorDrawing) {
        Node par = n.getParent();
        Node chi = n.getChild();

        final int NOT_ASSIGNED = 1023901412;
        
        // fix the cursor location if it's less than 0
        if (cursorLocation < 0) {
            cursorLocation = 0;
        }

        // Render the Line
        Point.Float cp = new Point.Float(
                chi.getX() + chi.getWidth() / 2,
                chi.getY() + chi.getHeight() / 2
        );

        Point.Float pp = new Point.Float(
                par.getX() + par.getWidth() / 2,
                par.getY() + par.getHeight() / 2
        );

        // create a vector for the direction of the line
        float diffX = (float) (cp.getX() - pp.getX());
        float diffY = (float) (cp.getY() - pp.getY());
        float lineLength = (float) Math.sqrt((diffX * diffX) + (diffY * diffY));
        diffX = diffX / lineLength;
        diffY = diffY / lineLength; //Now they are normalized

        pp.x += (diffX * par.getWidth() / 2);
        pp.y += (diffY * par.getHeight() / 2);

        scootPointToEdge(pp, par);

        cp.x -= (diffX * chi.getWidth() / 2);
        cp.y -= (diffY * chi.getHeight() / 2);

        scootPointToEdge(cp, chi);

        float tailDist = 0.20f; // 20% of the length
        float centerDist = 0.5f; // 50% of the length
        float headDist = 0.8f; // 80% of the length

        //Redo the vector thing
        // create a vector for the direction of the line
        diffX = (float) (cp.getX() - pp.getX());
        diffY = (float) (cp.getY() - pp.getY());
        lineLength = (float) Math.sqrt((diffX * diffX) + (diffY * diffY));
        diffX = diffX / lineLength;
        diffY = diffY / lineLength; //Now they are normalized

        // Force a no shape figure to smash down to its local orgin
        if (chi.getStyle().getShape() == Shape.NONE) {
            cp.setLocation(chi.getX(), chi.getY());
        }

        Point int_p1 = camera.convertWorldToCanvas(pp);
        Point int_p2 = camera.convertWorldToCanvas(cp);

        g.setColor(n.getStyle().getLineColor());
        ((Graphics2D) g).setStroke(new BasicStroke(n.getStyle().getLineSize()));

        switch (this) {
            case SOLID:
                g.drawLine(int_p1.x, int_p1.y,
                        int_p2.x, int_p2.y);
                break;
            case DASHED:
                ((Graphics2D) g).setStroke(new BasicStroke(
                        n.getStyle().getLineSize(),
                        BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL,
                        1f,
                        new float[]{10},
                        0f
                ));
                g.drawLine(int_p1.x, int_p1.y,
                        int_p2.x, int_p2.y);
                ((Graphics2D) g).setStroke(new BasicStroke(n.getStyle().getLineSize()));
                break;
            case DOTTED:
                float canvasLineLength = (float) Math.sqrt(
                        (((int_p2.x - int_p1.x) * (int_p2.x - int_p1.x)))
                        + (((int_p2.y - int_p1.y) * (int_p2.y - int_p1.y)))
                );
                float prog = 0;
                while (prog < canvasLineLength) {
                    g.fillOval(int_p1.x + (int) (diffX * prog) - (n.getStyle().getLineSize()), int_p1.y + (int) (diffY * prog) - (n.getStyle().getLineSize()),
                            (n.getStyle().getLineSize()*2), (n.getStyle().getLineSize()*2));
                    prog += (n.getStyle().getLineSize()*4);
                }
                break;
        }
        
        if (chi.getStyle().getShape() != Shape.NONE) {
            //Render the arrow head
            float pdiffX = -diffY;
            float pdiffY = diffX;

            float fromX = diffX * (lineLength - 15);
            float fromY = diffY * (lineLength - 15);

            Point.Float arrowHead1 = new Point.Float(pp.x + fromX + pdiffX * 15, pp.y + fromY + pdiffY * 10);
            Point.Float arrowHead2 = new Point.Float(pp.x + fromX - pdiffX * 15, pp.y + fromY - pdiffY * 10);

            Point iarrow1 = camera.convertWorldToCanvas(arrowHead1);
            Point iarrow2 = camera.convertWorldToCanvas(arrowHead2);

            g.drawLine(iarrow1.x, iarrow1.y, int_p2.x, int_p2.y);
            g.drawLine(iarrow2.x, iarrow2.y, int_p2.x, int_p2.y);
        }

        ((Graphics2D) g).setStroke(new BasicStroke(1));
        // Render the Text

        // set the font for drawing the font
        g.setFont(n.getStyle().getFontType().deriveFont((float) (n.getStyle().getFontSize() / camera.getZoom())));
        g.setColor(n.getStyle().getFontColor());

        // Variables to track the location of the cursor
        float originalParentPointX = (float) pp.getX();
        float originalParentPointY = (float) pp.getY();
        int cursorRenderX = 0;
        int cursorRenderY = 0;

        // draw the tail, center, and head texts
        /// draw the Tail text
        int[] info = renderText(n.getTailText(), tailDist, originalParentPointX, originalParentPointY,
                TextTypeManager.TAIL, partCursorDrawing, lineLength, diffY, diffX, g, cursorLocation, 
                camera, blinkCursor);
        if (partCursorDrawing == TextTypeManager.TAIL) {
            cursorRenderX = info[0];
            cursorRenderY = info[1];
        }

        // Draw the Center text
        info = renderText(n.getCenterText(), centerDist, originalParentPointX, originalParentPointY,
                TextTypeManager.CENTER, partCursorDrawing, lineLength, diffY, diffX, g, cursorLocation, 
                camera, blinkCursor);
        if (partCursorDrawing == TextTypeManager.CENTER) {
            cursorRenderX = info[0];
            cursorRenderY = info[1];
        }

        /// draw the Head text
        info = renderText(n.getHeadText(), headDist, originalParentPointX, originalParentPointY,
                TextTypeManager.HEAD, partCursorDrawing, lineLength, diffY, diffX, g, cursorLocation, 
                camera, blinkCursor);
        if (partCursorDrawing == TextTypeManager.HEAD) {
            cursorRenderX = info[0];
            cursorRenderY = info[1];
        }

        // draw the blinking cursor
        if (blinkCursor) {
            g.setColor(Color.BLACK);
            g.fillRect(cursorRenderX, cursorRenderY,
                    3, g.getFontMetrics().getHeight() + 4);

        }
        return new Point[]{int_p1, int_p2};
    }
    
    /**
     * A method that takes away the copy pasting of rendering the text
     * @param text The text to draw
     * @param distanceRatio The ratio of where on the line the text is drawn. 
     *              It's a value from 0 to 1
     * @param originalParentPointX The x position where the line originates
     * @param originalParentPointY The y position where the line originates
     * @param cursorConditional The value to test the partCursorDrawing value to 
     *          to decide if the cursor will be drawn on this text
     * @param partCursorDrawing The value indicating what part of the line the cursor
     *              will be drawn on
     * @param lineLength The length of the line
     * @param diffY The s component of the normalized vector for the line
     * @param diffX The y component of the normalized vector for the line
     * @param g The graphics context
     * @param cursorLocation The location of the cursor
     * @param camera The camera context
     * @param blinkCursor Whether or not the cursor will be showing
     * @return 
     */
    private int[] renderText(String text, float distanceRatio, float originalParentPointX, 
            float originalParentPointY, int cursorConditional, int partCursorDrawing, float lineLength, 
            float diffY, float diffX, Graphics g, int cursorLocation, Camera camera, boolean blinkCursor) {
        int cursorRenderX = 0;
        int cursorRenderY = 0;
        float xPositionOffset = (int) g.getFontMetrics().getStringBounds(text, g).getWidth() / 2;
        Point.Float pp = new Point.Float(originalParentPointX + (distanceRatio * diffX * lineLength) - xPositionOffset,
                originalParentPointY + (distanceRatio * diffY * lineLength));
        if (blinkCursor && partCursorDrawing == cursorConditional) {
            if (text.length() > 0 && cursorLocation > text.length()) {
                cursorLocation = text.length();
            }
            cursorRenderX = camera.convertWorldToCanvasX((float) pp.getX())
                    + (int) (g.getFontMetrics().getStringBounds(text.substring(0, cursorLocation), g).getWidth());
            cursorRenderY = (int) (camera.convertWorldToCanvasY((int) pp.getY()) - g.getFontMetrics().getHeight() - 2);
        }
        g.drawString(text,
                (int) camera.convertWorldToCanvasX((float) pp.getX()),
                (int) camera.convertWorldToCanvasY((float) pp.getY()));
        
        return new int[]{cursorRenderX, cursorRenderY};
    }
}
