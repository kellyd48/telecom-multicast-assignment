package GUI;

import javax.swing.ImageIcon;

/**
 * Class for testing the gui.
 */
public class guiTestApp {
	
	public GraphicalUserInterface gui;

	public guiTestApp(){
		gui = new GraphicalUserInterface("");
	}
	
	public static void main(String[] args){
		guiTestApp gui = new guiTestApp();
		gui.setMessage("HI");
		gui.setProgress(10);
		gui.setMessage("Waiting to share an image...");
		gui.setProgress(0);
		while(true){
			gui.getImage();
		}
	}
	
	public void setMessage(String mesg){
		gui.setMessage(mesg);
	}
	
	public void setProgress(int progress){
		gui.setProgress(progress);
	}
	
	public void getImage(){
		if(gui.getState()==gui.IMAGE_SHARED){
			ImageIcon image = gui.getSharedImage();
			System.out.println(image.getIconWidth()+"x"+image.getIconHeight());
			gui.displayImage(image);
		}else{
			System.out.println("No valid image....");
		}
	}
}
