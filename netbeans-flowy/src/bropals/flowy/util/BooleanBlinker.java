/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
                System.out.println(e);
            }
            
            value = !value;
            for (BooleanBlinkListener bbl : listeners) {
                bbl.booleanSwitch(value);
            }
        }
    }
}
