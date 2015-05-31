/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bropals.flowy.icons;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author Jonathon
 */
public class IconManager {
    
    private static HashMap<String, BufferedImage> loadedIcons = new HashMap<>();
    
    public static ImageIcon getIcon(String iconFileName) {
        BufferedImage icon = loadedIcons.get(iconFileName);
        if (icon == null) {
            try {
                icon = (BufferedImage)ImageIO.read(IconManager.class.getResourceAsStream("/bropals/flowy/icons/" + iconFileName));
            } catch (IOException ex) {
                System.err.println("Unable to load icon " + iconFileName );
            }
            loadedIcons.put(iconFileName, icon);
        }
        return new ImageIcon(icon);
    }
}
