package GUI;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.sun.org.apache.xerces.internal.util.URI;

public class ButtonsContainer extends JPanel{
	private ButtonsContainer self;
	private GraphicalUserInterface gui;
	private ChatContainer chatContainer;
	private JButton saveButton, shareButton;
	private JFileChooser shareDialog, saveDialog;
	private String saveFilePath;
	private URL urlForSharedImage;
	private ImageIcon sharedImage;
	
	public ButtonsContainer( GraphicalUserInterface graphicalUserInterface,ChatContainer cc) {
		self = this;
		
		sharedImage = new ImageIcon();
		
		this.gui = graphicalUserInterface;
		this.chatContainer = cc;
		
		shareDialog = new JFileChooser();
		saveDialog = new JFileChooser();
		
		FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
		shareDialog.setFileFilter(imageFilter);
		saveDialog.setFileFilter(imageFilter);
		
		saveButton = new JButton("Save");
		saveButton.setToolTipText("Use this button to save the received image in a chosen directory.");
		saveButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int returnValue = saveDialog.showSaveDialog(self);
		
				if(returnValue==JFileChooser.APPROVE_OPTION){
					File file = saveDialog.getSelectedFile();
					saveFilePath = file.getAbsolutePath();
					System.out.println(saveFilePath);
					
					try {
					    // retrieve image
						ImageIcon ii = chatContainer.getImageIcon();
						BufferedImage bi = new BufferedImage(ii.getIconWidth(),ii.getIconHeight(),BufferedImage.TYPE_3BYTE_BGR);
						Graphics g = bi.getGraphics();
						g.drawImage(ii.getImage(), 0, 0, null);
					    File outputfile = new File(saveFilePath + ".jpg");
					    ImageIO.write(bi, "jpg", outputfile);
					} catch (IOException e){

					}
				}
			}
		});
		
		shareButton = new JButton("Share...");
		shareButton.setToolTipText("Use this button to select a picture that you want to share with your group.");
		shareButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int returnValue = shareDialog.showOpenDialog(self);
				
				if(returnValue==JFileChooser.APPROVE_OPTION){
					File file = shareDialog.getSelectedFile();
					try {
						urlForSharedImage = file.toURI().toURL();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("FilePath:"+urlForSharedImage.toString());
					sharedImage = new ImageIcon(urlForSharedImage);
					gui.setState(GraphicalUserInterface.IMAGE_SHARED);
				}
			}
		});
		this.add(saveButton);
		this.add(shareButton);
	}
	
	public ImageIcon getSharedImage(){ return sharedImage;}
}
