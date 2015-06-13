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
package bropals.processsketcher.data;

import bropals.processsketcher.style.LineStyle;
import java.awt.Graphics;
import bropals.processsketcher.Camera;
import bropals.processsketcher.FlowchartWindow;
import bropals.processsketcher.style.FontStyle;

/**
 * An object to represent a process flow between two nodes.
 * The arrow points on the head side of the line.
 * @author Jonathon
 */
public class NodeLine implements Selectable, BinaryData {
    
    /**
     * The parent node of this line.
     */
    private Node parent;
    /**
     * The child node of this line.
     */
    private Node child;
    /**
     * The style used by this line.
     */
    private LineStyle style;
    /**
     * THe text that will render near the tail of this line.
     */
    private String tailText;
    /**
     * The text that will render near the center of this line.
     */
    private String centerText;
    /**
     * The text that will render near the head of this line.
     */
    private String headText;
    /**
     * The style that this node line is linked with.
     */
    private String linkedStyle;

    /**
     * Creates a new NodeLine to relate two nodes together. 
     * @param parent the node that is on the tail side of the node line.
     * @param child the node that is on the head side of the node line.
     */
    public NodeLine(Node parent, Node child) {
        this.parent = parent;
        this.child = child;
        style = new LineStyle();
        tailText = "";
        centerText = "";
        headText = "";
    }
    
    /**
     * Gets the parent node (the node on the tail side).
     * @return the parent node.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Sets the parent node (the node on the tail side).
     * @param parent the new parent node.
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Gets the child node (the node on the head side).
     * @return the child node.
     */
    public Node getChild() {
        return child;
    }

    /**
     * Sets the child node (the node on the head side).
     * @param child the new child node.
     */
    public void setChild(Node child) {
        this.child = child;
    }

    /**
     * Gets the line style that is controlling the look of this line.
     * @return the line style that is controlling the look of this style.
     */
    public LineStyle getStyle() {
        return style;
    }

    /**
     * Sets the line style to control the look of this line.
     * @param style the line style to control the look of this line.
     */
    public void setStyle(LineStyle style) {
        this.style = style;
    }

    /**
     * Gets the text that is on the tail side of the line.
     * @return the text that is on the tail side of the line.
     */
    public String getTailText() {
        return tailText;
    }

    /**
     * Sets the text that is on the tail side of the line.
     * @param tailText the text that is on the tail side of the line.
     */
    public void setTailText(String tailText) {
        this.tailText = tailText;
    }

    /**
     * Gets the text that is on the center of the line.
     * @return the text that is on the center of the line.
     */
    public String getCenterText() {
        return centerText;
    }

    /**
     * Sets the text that is in the center of the line.
     * @param centerText the text on the center of the line.
     */
    public void setCenterText(String centerText) {
        this.centerText = centerText;
    }

    /**
     * Gets the text that is on the head side of the line.
     * @return the text that is on the head side of the line.
     */
    public String getHeadText() {
        return headText;
    }

    /**
     * Sets the text that is on the head side of the line.
     * @param headText the text that is on the head side of the line.
     */
    public void setHeadText(String headText) {
        this.headText = headText;
    }
    
    /**
     * Get the style that this node line is linked to, or <code>null</code>
     * if it is not linked to any.
     * @return the linked style name.
     */
    public String getLinkedStyle() {
        return linkedStyle;
    }
    
    @Override
    public boolean isLinked() {
        return getLinkedStyle() != null;
    }
    
    @Override
    public void assignStyle(String styleName) {
        linkedStyle = styleName;
    }
    
    @Override
    public void unlink() {
        linkedStyle = null;
    }
    
    @Override
    public Object clone() {
        NodeLine other = new NodeLine(parent, child);
        other.setHeadText(headText);
        other.setTailText(tailText);
        other.setCenterText(centerText);
        other.setStyle((LineStyle)(style.clone()));
        other.assignStyle(other.getLinkedStyle());
        return other;
    }
   
    @Override
    public FontStyle getFontStyle() {
        return style;
    }

    @Override
    public int bytes() {
        /*
            Node line is special due to the fact that the ID numbers of the
            child and parent node is before the actual reading of the node
            line. The 8 is here to represent the two integers before the
            chunk of data read by the "fromBinary" function.
        */
        if (isLinked()) {
            return 9 + BinaryUtil.bytesForString(tailText) + BinaryUtil.bytesForString(centerText)
                   + BinaryUtil.bytesForString(headText) + BinaryUtil.bytesForString(linkedStyle);
        } else {
            return 9 + BinaryUtil.bytesForString(tailText) + BinaryUtil.bytesForString(centerText)
                   + BinaryUtil.bytesForString(headText) + style.bytes();
        }
    }

    @Override
    public void toBinary(byte[] arr, int pos) {
        int tailBytes = BinaryUtil.bytesForString(tailText);
        int centerBytes = BinaryUtil.bytesForString(centerText);
        int headBytes = BinaryUtil.bytesForString(headText);
        BinaryUtil.stringToBytes(tailText, arr, pos);
        BinaryUtil.stringToBytes(centerText, arr, pos+tailBytes);
        BinaryUtil.stringToBytes(headText, arr, pos+tailBytes+centerBytes);
        if (isLinked()) {
            arr[pos+tailBytes+centerBytes+headBytes] = 1;
            BinaryUtil.stringToBytes(linkedStyle, arr, pos+tailBytes+centerBytes+headBytes+1);
        } else {
            arr[pos+tailBytes+centerBytes+headBytes] = 0;
            style.toBinary(arr, pos+tailBytes+centerBytes+headBytes+1);
        }
    }

    @Override
    public void fromBinary(byte[] arr, int pos, FlowchartWindow window) {
        tailText = BinaryUtil.bytesToString(arr, pos);
        int tailBytes = BinaryUtil.bytesForString(tailText);
        centerText = BinaryUtil.bytesToString(arr, pos+tailBytes);
        int centerBytes = BinaryUtil.bytesForString(centerText);
        headText = BinaryUtil.bytesToString(arr, pos+tailBytes+centerBytes);
        int headBytes = BinaryUtil.bytesForString(headText);
        style = new LineStyle();
        if (arr[pos+tailBytes+centerBytes+headBytes] == 1) {
            linkedStyle = BinaryUtil.bytesToString(arr, pos+tailBytes+centerBytes+headBytes+1);
            style.setTo(window.getStyleManager().getLineStyle(linkedStyle));
        } else {
            style.fromBinary(arr, pos+tailBytes+centerBytes+headBytes+1, window);
        }
    }
}
