/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy;

/**
 * A class to manage typing text
 * @author Kevin
 */
public class TextTypeManager {
 
    /**
     * Keep track of where on the line you're adding text (tail, head, or
     * center) 0 = center, 1 = tail, and 2 = head, It will rotate between these three
     * in the order 0 -> 1 -> 2 -> 0 ...
     */
    private int linePartTyping;
    public static final int TAIL = 1;
    public static final int CENTER = 0;
    public static final int HEAD = 2;
    
    /**
     * The location of the cursor on the string. The cursor is located behind the 
     * character whose index is this number. This number is 0 for the start of 
     * the string, and is equal to str.length() for a String str when it's at
     * the end of the word.
     * 
     * For a string "word" and the location of "1" the cursor is positioned as
     * "w|ord"
     */
    private int locationOfTypeCursor;
    /**
     * Whether or not to show the cursor when rendering
     */
    private boolean showingCursor;
    
    /**
     * Create a new text type manager with the cursor location at 0.
     */
    public TextTypeManager() {
        showingCursor = false;
        linePartTyping = 0;
        locationOfTypeCursor = CENTER;
    }

    public int getLinePartTyping() {
        return linePartTyping;
    }

    public void setLinePartTyping(int linePartTyping) {
        this.linePartTyping = linePartTyping;
    }

    public int getLocationOfTypeCursor() {
        return locationOfTypeCursor;
    }

    public void decrementLocationOfTypeCursor() {
        locationOfTypeCursor--;
    }
    
    public void incrementLocationOfTypeCursor() {
        locationOfTypeCursor++;
    }
    
    public void setLocationOfTypeCursor(int locationOfTypeCursor) {
        this.locationOfTypeCursor = locationOfTypeCursor;
    }

    public boolean isCursorShowing() {
        return showingCursor;
    }

    public void setCursorShowing(boolean showCursor) {
        this.showingCursor = showCursor;
    }
    
    
}
