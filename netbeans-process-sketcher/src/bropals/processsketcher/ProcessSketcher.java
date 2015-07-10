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

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.SplashScreen;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.UIManager;

/**
 * The main class.
 * @author Jonathon
 */
public class ProcessSketcher {

    public static Font[] allFonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
    
    public static BufferedImage mainIconSmaller;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {
            //System.out.println("Could not apply system look and feel: " + e);
        }
        
        BufferedImage image = null;
        try {
            image = (BufferedImage)ImageIO.read(ProcessSketcher.class.getResourceAsStream("/splash.png"));
        } catch (IOException ex) {
            System.err.println("Could not load splash image");
        }
        SplashScreen splash = SplashScreen.getSplashScreen();
        if (splash != null) {
            Graphics2D g = splash.createGraphics();
            g.fillRect(4, 132, 392, 134);
            splash.update();
            if (image != null) {
                for (int i=0; i<49; i++) {
                    g.drawImage(image, 0, 0, null);
                    g.fillRect(4+(i*8), 132, 392-(i*8), 164);
                    try {
                        Thread.sleep(30);  
                    } catch(Exception e) {
                        return;
                    }
                    splash.update();
                }
            }
            g.dispose();
            try {
                Thread.sleep(50);  
            } catch(Exception e) {
                return;
            }
            splash.close();
        }
        try {
             mainIconSmaller = (BufferedImage)ImageIO.read(ProcessSketcher.class.getResourceAsStream("/bropals/processsketcher/icons/mainWindowIcon.png"));
        } catch (IOException ex) {
            System.err.println("Could not load main window icon image");
        }
        FlowchartWindowManager fwm = new FlowchartWindowManager();
    }
    
}
