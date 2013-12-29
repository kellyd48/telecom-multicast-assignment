package GUI;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ChatContainer extends JPanel{
	
	public ChatContainer(){
		this.setPreferredSize(new Dimension(600,600));
			ImageIcon image = createImageIcon("img/img.jpg","img.jpg");
			JLabel label = new JLabel("",image,JLabel.CENTER);
			this.add(label);
	}
	
	private ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: "+ getClass().getResource("").getPath() + path );
			return null;
		}
	}
	
	private ImageIcon resizeImage(ImageIcon image){
		return image;
	}
	
	
}
