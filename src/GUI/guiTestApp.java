package GUI;

/**
 * Class for testing the gui.
 */
public class guiTestApp {
	
	public GraphicalUserInterface gui;

	public guiTestApp(){
		gui = new GraphicalUserInterface(0,100);
	}
	
	public static void main(String[] args){
		guiTestApp gui = new guiTestApp();
		gui.setMessage("HI");
		gui.setProgress(10);
		gui.setMessage("");
		gui.setProgress(0);
		
	}
	
	public void setMessage(String mesg){
		gui.setMessage(mesg);
	}
	
	public void setProgress(int progress){
		gui.setProgress(progress);
	}
}
