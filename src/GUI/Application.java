package GUI;

public class Application {

	public static void main(String[] args) {
		GraphicalUserInterface gui = new GraphicalUserInterface();
		int i =0;
		while(i<Integer.MAX_VALUE){
			gui.setMessage(Integer.toString(i));
			i++;
		}
	}

}
