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
    /**
     * A constant for the tail of a node line.
     */
    public static final int TAIL = 1;
    /**
     * A constant for the center of a node line.
     */
    public static final int CENTER = 0;
    /**
     * A constant for the head of a node line.
     */
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
