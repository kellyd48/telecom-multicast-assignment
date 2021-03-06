package GUI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ChatContainer extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int WIDTH = 500;
	public static final int HEIGHT = 500;
	public static final int PADDING = 20;
	private JLabel label;
	private ImageIcon image;
	
	public ChatContainer(){
		this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		image = new ImageIcon();
		label = new JLabel();
		this.add(label);
	}
	
	public ImageIcon createImageIcon(URL urlForSharedImage, String description) {
		if (urlForSharedImage != null) {
			return new ImageIcon(urlForSharedImage, description);
		} else {
			System.err.println("Couldn't find file: " + urlForSharedImage.toString());
			return null;
		}
	}
	
	private boolean isImageTooBig(ImageIcon srcImage){
		if(srcImage.getIconHeight()>HEIGHT || srcImage.getIconWidth()>WIDTH)
			return true;
		return false;
	}
	
	private ImageIcon resizeImage(ImageIcon srcImage){
		ImageIcon iImage = null;
		
        if(srcImage.getIconHeight()>HEIGHT){
        	double h = HEIGHT-(2*PADDING);
        	double w = h/srcImage.getIconHeight()*srcImage.getIconWidth();
        	iImage = resizeImage(srcImage, (int)w, (int)h);
        }
        else if(srcImage.getIconWidth()>WIDTH){
        	double w = WIDTH-(2*PADDING);
        	double h = w/srcImage.getIconWidth()*srcImage.getIconHeight();
        	iImage = resizeImage(srcImage, (int)w, (int)h);
        }
        else{
        	double h = HEIGHT-(2*PADDING);
        	double w = WIDTH-(2*PADDING);
        	iImage = resizeImage(srcImage, (int)w, (int)h);
        }

        return iImage;
	}
	
	private ImageIcon resizeImage(ImageIcon srcImage, int w, int h){
		ImageIcon iImage = null;

        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImage.getImage(), 0, 0, w, h, null);
        g2.dispose();
        iImage =new ImageIcon(resizedImg);
	        
        return iImage;
	}
	
	public void displayImage(BufferedImage srcImg){
		assert(srcImg != null);
		image.setImage(srcImg);
		if(isImageTooBig(image)){
			image = resizeImage(image);
		}
		label.setIcon(image);
		super.repaint();
	}
	
	public void displayImage(ImageIcon srcImg){
		image.setImage(srcImg.getImage());
		if(isImageTooBig(image)){
			image = resizeImage(image);
		}
		label.setIcon(image);
		super.repaint();
	}
	
	public ImageIcon getImageIcon(){
		return image;
	}
}
