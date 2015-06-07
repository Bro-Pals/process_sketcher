/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 * The main class.
 * @author Jonathon
 */
public class Flowy {

    public static Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        FlowchartWindowManager fwm = new FlowchartWindowManager();
        
    }
    
}
