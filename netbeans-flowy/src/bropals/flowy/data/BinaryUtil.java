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

import java.awt.Color;
import java.awt.Font;

/**
 * Class containing convenience methods for binary stuff.
 *
 * @author Jonathon
 */
public class BinaryUtil {

    /**
     * Writes an integer to 4 bytes in big-endian notation to the byte array.
     *
     * @param num the integer to write.
     * @param arr the byte array to place the int in.
     * @param pos the index of the first byte in the 4 byte sequence.
     */
    public static void intToBytes(int num, byte[] arr, int pos) {
        arr[pos] = (byte) ((num & 0xFF000000) >> 24);
        arr[pos + 1] = (byte) ((num & 0x00FF0000) >> 16);
        arr[pos + 2] = (byte) ((num & 0x0000FF00) >> 8);
        arr[pos + 3] = (byte) ((num & 0x000000FF));
    }

    /**
     * Writes a float to 4 bytes in big-endian notation to the byte array.
     *
     * @param num the float to write.
     * @param arr the byte array to place the int in.
     * @param pos the index of the first byte in the 4 byte sequence.
     */
    public static void floatToBytes(float num, byte[] arr, int pos) {
        intToBytes(Float.floatToIntBits(num), arr, pos);
    }

    /**
     * Writes a string to a byte array. The format is: first an int to say how
     * many characters the string is, then the characters themselves, with 2
     * bytes per character. The number of bytes a string takes up is 4 +
     * (str.length() * 2).
     *
     * @param str the string to write.
     * @param arr the array to write the string to.
     * @param pos the position of the first byte of the string block.
     */
    public static void stringToBytes(String str, byte[] arr, int pos) {
        char[] carr = str.toCharArray();
        intToBytes(carr.length, arr, pos);
        for (int i = 0; i < carr.length; i++) {
            charToBytes(carr[i], arr, pos + 4 + (i * 2));
        }
    }

    /**
     * Writes a character to a byte array, taking up 2 bytes.
     *
     * @param c the character to write
     * @param arr the array to write the character to.
     * @param pos the position of the character's first byte.
     */
    public static void charToBytes(char c, byte[] arr, int pos) {
        arr[pos] = (byte) ((c & 0xFF00) >> 8);
        arr[pos + 1] = (byte) (c & 0x00FF);
    }

    /**
     * Writes the given color to the byte array in RGB format.
     *
     * @param color the color to write.
     * @param arr the byte array to place it in.
     * @param pos the index of the first byte, the red component.
     */
    public static void colorToBytes(Color color, byte[] arr, int pos) {
        int rgb = color.getRGB();
        arr[pos] = (byte) ((rgb & 0x00FF0000) >> 16);
        arr[pos + 1] = (byte) ((rgb & 0x0000FF00) >> 8);
        arr[pos + 2] = (byte) (rgb & 0x000000FF);
    }

    /**
     * Writes the given font object to the byte array.
     * @param font the font to write.
     * @param arr the byte array to place it in.
     * @param pos 
     */
    public static void fontToBytes(Font font, byte[] arr, int pos) {
        stringToBytes(font.getFontName(), arr, pos);
    }
    
    /**
     * Interprets an int from 4 bytes from the given byte array.
     *
     * @param arr the byte array.
     * @param pos the position of the first byte of the int.
     * @return the interpreted int.
     */
    public static int bytesToInt(byte[] arr, int pos) {
        return (((int) arr[pos] << 24) & 0xFF000000)
                | (((int) arr[pos + 1] << 16) & 0x00FF0000)
                | (((int) arr[pos + 2] << 8) & 0x0000FF00)
                | (((int) arr[pos + 3]) & 0x000000FF);
    }

    /**
     * Interprets a float from 4 bytes from the given byte array.
     *
     * @param arr the byte array.
     * @param pos the position of the first byte of the int.
     * @return the interpreted float.
     */
    public static float bytesToFloat(byte[] arr, int pos) {
        return Float.intBitsToFloat(bytesToInt(arr, pos));
    }

    /**
     * Reads a string from a byte array.
     * @param arr the byte array to read the string from.
     * @param pos the position of the string's first byte.
     * @return the read string.
     */
    public static String bytesToString(byte[] arr, int pos) {
        int charCount = bytesToInt(arr, pos);
        String str = "";
        for (int i=0; i<charCount; i++) {
            str += bytesToChar(arr, pos + 4 + (i*2));
        }
        return str;
    }
    
    /**
     * Reads a character from a byte array.
     *
     * @param arr the array to read the character from.
     * @param pos the position of the character's first byte.
     * @return the read character.
     */
    public static char bytesToChar(byte[] arr, int pos) {
        return (char) ((((short) arr[pos] << 8) & 0xFF00)
                | ((short) arr[pos + 1] & 0x00FF));
    }

    /**
     * Reads a color from the byte array as a 24-bit depth RGB color.
     *
     * @param arr the byte array to place it in.
     * @param pos the index of the first byte, the red component.
     * @return the read color.
     */
    public static Color bytesToColor(byte[] arr, int pos) {
        return new Color(
                arr[pos] & 0x000000FF,
                arr[pos + 1] & 0x000000FF,
                arr[pos + 2] & 0x000000FF);
    }
    
    /**
     * Interprets some bytes as a font.
     * @param arr the byte array.
     * @param pos the position of the first byte of the font.
     * @return the interpreted font.
     */
    public static Font bytesToFont(byte[] arr, int pos) {
        String fontName = bytesToString(arr, pos);
        Font font = Font.decode(fontName);
        if (font == null) {
            System.err.println("This system does not have the \"" + fontName + "\" font, using a default font.");
            font = new Font(Font.SERIF, Font.PLAIN, 12);
        }
        return font;
    }
    
    /**
     * Get the number of bytes needed by the given string.
     * @param str the string to measure.
     * @return the number of bytes to store the string.
     */
    public static int bytesForString(String str) {
        return 4 + (str.length()*2);
    }
    
    /**
     * Get the number of bytes needed by the given font.
     * @param font the font to measure.
     * @return the number of bytes to store the font.
     */
    public static int bytesForFont(Font font) {
        return 4 + (font.getFontName().length()*2);
    }
}
