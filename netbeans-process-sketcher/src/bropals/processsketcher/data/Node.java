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

import bropals.processsketcher.EventManager;
import bropals.processsketcher.FlowchartWindow;
import bropals.processsketcher.style.FontStyle;
import bropals.processsketcher.style.NodeStyle;
import java.util.ArrayList;

/**
 * An object to represent a node in a flowchart.
 *
 * @author Jonathon
 */
public class Node implements Selectable, BinaryData {

    /**
     * The smallest value for the width / height of a node.
     */
    public final static float MINIMUM_SIZE = (EventManager.DRAG_RESIZE_DISTANCE * 2) + 4;

    /**
     * If the style is null, revert to a default style.
     */
    private NodeStyle style;
    /**
     * The x position of the Node in world units.
     */
    private float x;
    /**
     * The y position of the Node in world units.
     */
    private float y;
    /**
     * The width of the Node in world units
     */
    private float width;
    /**
     * The height of the Node in world units.
     */
    private float height;
    /**
     * A list of all the lines connected to this node.
     */
    private ArrayList<NodeLine> linesConnected;
    /**
     * The text that is inside of this Node.
     */
    private String innerText;
    /**
     * The style that this node is linked with.
     */
    private String linkedStyle;

    /**
     * Creates the default node in the specified world coordinate position.
     *
     * @param x the X position of the node in world coordinates.
     * @param y the Y position of the node in world coordinates.
     */
    public Node(float x, float y) {
        style = new NodeStyle(); // ???
        this.x = x;
        this.y = y;
        width = 100;
        height = 80;
        linesConnected = new ArrayList<>();
        innerText = "";
    }

    /**
     * Gets the node style that is controlling the look of this Node.
     *
     * @return the style controlling the look of this Node.
     */
    public NodeStyle getStyle() {
        return style;
    }

    /**
     * Sets the node style that is to control the look of this Node.
     *
     * @param style the style of this Node.
     */
    public void setStyle(NodeStyle style) {
        this.style = style;
    }

    /**
     * Gets the X position of this node, in world coordinates.
     *
     * @return the X position of the node, in world coordinates.
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the X position of this node, in world coordinates.
     *
     * @param x the new X position of this node, in world coordinates.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Gets the Y position of this node, in world coordinates.
     *
     * @return the Y position of the node, in world coordinates.
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the Y position of this node, in world coordinates.
     *
     * @param y the new Y position of this node, in world coordinates.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Gets the width of this node, in world coordinates.
     *
     * @return the width of this node, in world coordinates.
     */
    public float getWidth() {
        return width;
    }

    /**
     * Sets the width of this node, in world coordinates.
     *
     * @param width the new width of this node, in world coordinates.
     */
    public void setWidth(float width) {
        if (width >= MINIMUM_SIZE) {
            this.width = width;
        }
    }

    /**
     * Gets the height of this node, in world coordinates.
     *
     * @return the height of this node, in world coordinates.
     */
    public float getHeight() {
        return height;
    }

    /**
     * Sets the height of this node, in world coordinates.
     *
     * @param height the new height of this node, in world coordinates.
     */
    public void setHeight(float height) {
        if (height >= MINIMUM_SIZE) {
            this.height = height;
        }
    }

    /**
     * Gets the list of node lines that are connected to this node.
     *
     * @return the list of node lines connected to this node.
     */
    public ArrayList<NodeLine> getLinesConnected() {
        return linesConnected;
    }

    /**
     * Gets the text that this node contains.
     *
     * @return the text that this node contains.
     */
    public String getInnerText() {
        return innerText;
    }

    /**
     * Sets the text that this node contains.
     *
     * @param innerText the new body of text for this node to contain.
     */
    public void setInnerText(String innerText) {
        this.innerText = innerText;
    }

    /**
     * Get the style that this node is linked to, or <code>null</code> if it is
     * not linked to any.
     *
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
        Node other = new Node(getX(), getY());
        other.setWidth(getWidth());
        other.setHeight(getHeight());
        other.setStyle((NodeStyle) style.clone()); // same style
        other.setInnerText(getInnerText()); // initially no text
        other.assignStyle(getLinkedStyle());
        return other;
    }

    @Override
    public FontStyle getFontStyle() {
        return style;
    }

    @Override
    public int bytes() {
        if (isLinked()) {
            return 18 + 
                    BinaryUtil.bytesForString(innerText) +
                    BinaryUtil.bytesForString(linkedStyle);
        } else {
            return 18 + BinaryUtil.bytesForString(innerText) + style.bytes();
        }
    }

    @Override
    public void toBinary(byte[] arr, int pos) {
        BinaryUtil.floatToBytes(x, arr, pos);
        BinaryUtil.floatToBytes(y, arr, pos + 4);
        BinaryUtil.floatToBytes(width, arr, pos + 8);
        BinaryUtil.floatToBytes(height, arr, pos + 12);
        BinaryUtil.stringToBytes(innerText, arr, pos + 16);
        if (isLinked()) {
            arr[pos + 17] = 1;
            BinaryUtil.stringToBytes(linkedStyle, arr, pos + 18 + BinaryUtil.bytesForString(innerText));
        } else {
            arr[pos + 17] = 0;
            style.toBinary(arr, pos + 18 + BinaryUtil.bytesForString(innerText));
        }
    }

    @Override
    public void fromBinary(byte[] arr, int pos, FlowchartWindow window) {
        x = BinaryUtil.bytesToFloat(arr, pos);
        y = BinaryUtil.bytesToFloat(arr, pos + 4);
        width = BinaryUtil.bytesToFloat(arr, pos + 8);
        height = BinaryUtil.bytesToFloat(arr, pos + 12);
        innerText = BinaryUtil.bytesToString(arr, pos + 16);
        style = new NodeStyle();
        if (arr[pos + 17] == 1) { //Linked
            linkedStyle = BinaryUtil.bytesToString(arr, pos+18+BinaryUtil.bytesForString(innerText));
            style.setTo(window.getStyleManager().getNodeStyle(linkedStyle));
        } else {
            style.fromBinary(arr, pos + 18 + BinaryUtil.bytesForString(innerText), window);
        }
    }
    
    /**
     * Gets the index of the first node that is a parent of this node
     * in the given list. This returns <code>-1</code> if there is no
     * node in the list that is also a parent of this node.
     * @param list the list of nodes to search
     * @return the index of the first node that is also a parent of this node.
     */
    public int getFirstOccuranceOfParent(ArrayList<Node> list) {
        ArrayList<Node> parents = getParentNodes();
        for (int i=0; i<list.size(); i++) {
            for (Node parent : parents) {
                if (list.get(i).equals(parent)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    /**
     * Gets the index of the first node that is a child of this node
     * in the given list. This returns <code>-1</code> if there is no
     * node in the list that is also a child of this node.
     * @param list the list of nodes to search
     * @return the index of the first node that is also a child of this node.
     */
    public int getFirstOccuranceOfChild(ArrayList<Node> list) {
        ArrayList<Node> children = getChildNodes();
        for (int i=0; i<list.size(); i++) {
            for (Node child : children) {
                if (list.get(i).equals(child)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Gets the list of nodes that are being pointed to
     * by this node.
     * @return the list of nodes that are being pointed to
     * by this node.
     */
    public ArrayList<Node> getChildNodes() {
        ArrayList<Node> nodes = new ArrayList<>();
        for (NodeLine nl : getLinesConnected()) {
            if (!nl.getChild().equals(this)) {
                nodes.add(nl.getChild());
            }
        }
        return nodes;
    }
    
    /**
     * Gets the list of nodes that are pointing to
     * this node.
     * @return the list of nodes that are pointing to
     * this node.
     */
    public ArrayList<Node> getParentNodes() {
        ArrayList<Node> nodes = new ArrayList<>();
        for (NodeLine nl : getLinesConnected()) {
            if (!nl.getParent().equals(this)) {
                nodes.add(nl.getParent());
            }
        }
        return nodes;
    }
}
