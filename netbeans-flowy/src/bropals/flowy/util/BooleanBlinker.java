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
package bropals.flowy.util;

import java.util.ArrayList;

/**
 * A thread that switches a boolean value true or false
 * @author Kevin
 */
public class BooleanBlinker implements Runnable {
    
    private ArrayList<BooleanBlinkListener> listeners;
    private int millisecondsBetween;
    private boolean value;
    
    public BooleanBlinker(int millis) {
        this.millisecondsBetween = millis;
        value = false;
        listeners = new ArrayList<>();
    }

    public void addListener(BooleanBlinkListener bbl) {
        listeners.add(bbl);
    }
    
    public void clearListeners() {
        listeners.clear();
    }
    
    public boolean getValue() {
        return value;
    }
    
    @Override
    public void run() {
        boolean blinking = true;
        while (blinking) {
            try {
                Thread.sleep(millisecondsBetween);
            } catch(Exception e) {
                System.err.println(e);
            }
            
            value = !value;
            for (BooleanBlinkListener bbl : listeners) {
                bbl.booleanSwitch(value);
            }
        }
    }
}
