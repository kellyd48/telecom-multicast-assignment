package GUI;

public class Application{
	
	public static void main(String[] args){
		GraphicalUserInterface gui = new GraphicalUserInterface(0,100, "");
		try {
			Thread.sleep(3000);
			gui.displayImage(gui.createImageIcon("imgs/jacob.jpg", "jacob"));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}