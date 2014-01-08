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
		gui.setMessage("");
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
		ImageIcon image = gui.getSharedImage();
		if(image != null){
			gui.displayImage(image);
		}else
			System.out.println("No valid image");
	}
}
