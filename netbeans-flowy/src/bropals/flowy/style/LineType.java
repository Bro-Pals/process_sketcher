/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.style;

import bropals.flowy.Camera;
import bropals.flowy.data.Node;
import bropals.flowy.data.NodeLine;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author Kevin
 */
public enum LineType {

    SOLID, DASHED, DOTTED;

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
                    g.fillOval(int_p1.x + (int) (diffX * prog) - 2, int_p1.y + (int) (diffY * prog) - 2,
                            4, 4);
                    prog += 12;
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
        float xPositionOffset = 0;
        float originalParentPointX = (float) pp.getX();
        float originalParentPointY = (float) pp.getY();
        int cursorRenderX = 0;
        int cursorRenderY = 0;

        // draw the tail, center, and head texts
        /// draw the Tail text
        int[] info = renderText(n.getTailText(), headDist, originalParentPointX, originalParentPointY,
                1, partCursorDrawing, lineLength, diffY, diffX, g, cursorLocation, 
                camera, blinkCursor, NOT_ASSIGNED);
        if (info[0] != NOT_ASSIGNED && info[1] != NOT_ASSIGNED) {
            cursorRenderX = info[0];
            cursorRenderY = info[1];
        }

        // Draw the Center text
        info = renderText(n.getCenterText(), centerDist, originalParentPointX, originalParentPointY,
                0, partCursorDrawing, lineLength, diffY, diffX, g, cursorLocation, 
                camera, blinkCursor, NOT_ASSIGNED);
        if (info[0] != NOT_ASSIGNED && info[1] != NOT_ASSIGNED) {
            cursorRenderX = info[0];
            cursorRenderY = info[1];
        }

        /// draw the Head text
        info = renderText(n.getHeadText(), headDist, originalParentPointX, originalParentPointY,
                2, partCursorDrawing, lineLength, diffY, diffX, g, cursorLocation, 
                camera, blinkCursor, NOT_ASSIGNED);
        if (info[0] != NOT_ASSIGNED && info[1] != NOT_ASSIGNED) {
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
    
    /// a method to reduce the amount of copy pasting
    public int[] renderText(String text, float distanceRatio, float originalParentPointX, 
            float originalParentPointY, int cursorConditional, int partCursorDrawing, float lineLength, 
            float diffY, float diffX, Graphics g, int cursorLocation, Camera camera, boolean blinkCursor, int NOT_ASSIGNED) {
        int cursorRenderX = NOT_ASSIGNED;
        int cursorRenderY = NOT_ASSIGNED;
        float xPositionOffset = (int) g.getFontMetrics().getStringBounds(text, g).getWidth() / 2;
        Point.Float pp = new Point.Float(originalParentPointX + (distanceRatio * diffX * lineLength) - xPositionOffset,
                originalParentPointY + (distanceRatio * diffY * lineLength));
        if (blinkCursor && partCursorDrawing == 2) {
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
