package Client;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ImageDisplay extends JPanel{

    private BufferedImage image;

    /**
     * Constructor
     */
    public ImageDisplay(String filePath) {
       try {                
          image = ImageIO.read(new File(filePath));
       } catch (IOException e) {
       		e.printStackTrace();
       }
    } // end Constructor

    /**
     * Paint image to screen
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, 0, 0, null);        
    } // end paintComponent
} // end ImageDisplay class