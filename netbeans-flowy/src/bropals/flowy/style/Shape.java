/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.style;

import bropals.flowy.Camera;
import bropals.flowy.data.Node;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.util.ArrayList;

/**
 *
 * @author Jonathon
 */
public enum Shape {
    SQUARE, TRIANGLE, OVAL, DIAMOND, THING;
    
    /**
     * Render a node according to what the style is.
     * @param node The node that is being rendered
     * @param camera The camera used to transform it
     * @param g The graphics that it will be drawn to
     * @param blinkCursor Whether or not the cursor will be showing for editing text
     * @param cursorLocation The lcoation of the cursor for editing text
     */
    public void renderShape(Node node, Camera camera, Graphics g, boolean blinkCursor, int cursorLocation) {
        Graphics2D g2 = (Graphics2D) g;
        
        if (cursorLocation > node.getInnerText().length()) {
            cursorLocation = node.getInnerText().length();
        }
        
        switch(this) {
            case SQUARE:
                g.setColor(node.getStyle().getBorderColor());
                Point.Float p1 = new Point.Float(node.getX(), node.getY());
                Point.Float p2 = new Point.Float(node.getX() + node.getWidth(), node.getY());
                Point.Float p3 = new Point.Float(node.getX() + node.getWidth(), node.getY() + node.getHeight());
                Point.Float p4 = new Point.Float(node.getX(), node.getY() + node.getHeight());
                
                // use canvas units to draw the node
                Point _p1 = camera.convertWorldToCanvas(p1);
                Point _p2 = camera.convertWorldToCanvas(p2);
                Point _p3 = camera.convertWorldToCanvas(p3);
                Point _p4 = camera.convertWorldToCanvas(p4);
                
                g.drawLine((int)_p1.getX(), (int)_p1.getY(),(int)_p2.getX(), (int)_p2.getY());
                g.drawLine((int)_p2.getX(), (int)_p2.getY(),(int)_p3.getX(), (int)_p3.getY());
                g.drawLine((int)_p3.getX(), (int)_p3.getY(),(int)_p4.getX(), (int)_p4.getY());
                g.drawLine((int)_p4.getX(), (int)_p4.getY(),(int)_p1.getX(), (int)_p1.getY());
                break;
        }
        
        if (node.getInnerText().length() > 0) {
            g2.setColor(node.getStyle().getFontColor());
            Font transformedFont = node.getStyle().getFontType().deriveFont(node.getStyle().getFontSize() / camera.getZoom());
            g2.setFont(transformedFont);
            
            float padding = 5;
            float startEverythingX = (float)camera.convertWorldToCanvasX(node.getX()) + padding; // canvas units
            float startEverythingY =(float)camera.convertWorldToCanvasX(node.getY()) + g2.getFontMetrics().getHeight() + padding; // canvas units
            // the width each row can't get longer than (in canvas units)
            float width = node.getWidth() - (padding * 2);
            
            // seperate the text into rows according to their lengths, not extending canvas units
            
            // the array for the text. Every element of the array represents one row of text
            ArrayList<String> text = new ArrayList<>();
            String[] words = node.getInnerText().split(" ");
            text.add(words[0]); // first word goes into the first row
            
            // what row is currently being filled
            int rowOn = 0;
            for (int i=1; i<words.length; i++) {
                String testResultRow = text.get(rowOn) + " " + words[i]; // add a space after each word
                // if the word is NOT a new line character and the new word will fit
                if (!words[i].equals("\n") && 
                        g.getFontMetrics().getStringBounds(testResultRow, g).getWidth() < width) {
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

            int lineHeight = g.getFontMetrics().getHeight() ;
            
            int sumOfCharsPrevRows = 0;
            int rowCursorInside = 0;
            
            // draw according to how the row was organized
            for (int r=0; r<text.size(); r++) {
                g.drawString(text.get(r), (int)startEverythingX, 
                        (int)startEverythingY + (r * lineHeight));
                if (sumOfCharsPrevRows + text.get(r).length() >= cursorLocation) {
                    rowCursorInside = r;
                } else {
                    sumOfCharsPrevRows += text.get(r).length();
                }
            }
            
            if (blinkCursor) {
                // get the offset for where to draw the cursor
                int thisRowOffset = (int)g.getFontMetrics().getStringBounds(
                        (entireString.substring(sumOfCharsPrevRows, cursorLocation)), g).getWidth();
                //draw the cursor
                g2.fillRect((int)startEverythingX + thisRowOffset, 
                    (int)startEverythingY + (lineHeight * (rowCursorInside - 1)) - 2, 
                    3, lineHeight + 4);
            }
        }
    }
}
