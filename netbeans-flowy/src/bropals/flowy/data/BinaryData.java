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
package bropals.flowy.data;

import bropals.flowy.FlowchartWindow;

/**
 * An interface that allows an object to be saveable in a binary form.
 * @author Jonathon
 */
public interface BinaryData {
    /**
     * Get the number of bytes needed to save this object.
     * @return the number of bytes this object occupies.
     */
    public int bytes();
    /**
     * Converts an object to binary form.
     * @param arr the byte array to store this object in.
     * @param pos the position of the first byte of this object.
     */
    public void toBinary(byte[] arr, int pos);
    /**
     * Sets this object based on its binary form.
     * @param arr the byte array to interpret the object from.
     * @param pos the position of the first byte of this object's byte
     * representation.
     * @param window the flowchart window.
     */
    public void fromBinary(byte[] arr, int pos, FlowchartWindow window);
}
