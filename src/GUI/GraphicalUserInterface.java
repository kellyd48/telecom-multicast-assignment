package GUI;

import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GraphicalUserInterface {
	public static final int PROGRESS_MIN_VALUE = 0;
	public static final int PROGRESS_MAX_VALUE = 100;
	public static final String WINDOW_TITLE = "Snapchat V1.0";
	
	private ChatContainer chatContainer;
	private ProgressBarContainer progressBarContainer;
	private SystemMessageContainer sysMsgContainer;
	private ButtonsContainer buttonsContainer;
	private int progressMinValue, progressMaxValue;
	private String clientID = "";

	public GraphicalUserInterface(String clientID){
		this(PROGRESS_MIN_VALUE, PROGRESS_MAX_VALUE, clientID);
	}
	
	public GraphicalUserInterface(int progressMinValue, int progressMaxValue, String clientID){
		this.progressMinValue=progressMinValue;
		this.progressMaxValue=progressMaxValue;
		this.clientID = clientID;
		try {
			javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					createAndShowGUI();
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createAndShowGUI(){
		JFrame frame = new JFrame(WINDOW_TITLE + " - Client ID:" + clientID);
		JPanel panel = new JPanel();

		chatContainer = new ChatContainer();
		progressBarContainer = new ProgressBarContainer(this.progressMinValue, this.progressMaxValue);
		sysMsgContainer = new SystemMessageContainer("Waiting...");
		buttonsContainer = new ButtonsContainer(chatContainer);

		GroupLayout layout = new GroupLayout(panel);

		GroupLayout.SequentialGroup horizontal = layout.createSequentialGroup();
		horizontal.addGroup(layout.createParallelGroup()
				.addComponent(chatContainer)
				.addComponent(progressBarContainer)
				.addComponent(sysMsgContainer)
				.addComponent(buttonsContainer));

		GroupLayout.SequentialGroup vertical = layout.createSequentialGroup();
		vertical.addComponent(chatContainer);
		vertical.addComponent(progressBarContainer);
		vertical.addComponent(sysMsgContainer);
		vertical.addComponent(buttonsContainer);

		layout.setHorizontalGroup(horizontal);
		layout.setVerticalGroup(vertical);

		panel.setLayout(layout);

		frame.add(panel);
		frame.pack();

		frame.setResizable(true);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void setMessage(String strMsg){
		if(sysMsgContainer!=null)
			sysMsgContainer.setMessage(strMsg);
	}
	
	public void clearMessage(){
		if(sysMsgContainer!=null)
			sysMsgContainer.clearMessage();
	}

	public void displayImage(BufferedImage srcImg){ 
		chatContainer.displayImage(srcImg);
	}

	public void displayImage(ImageIcon srcImg){ 
		chatContainer.displayImage(srcImg);
	}

	public ImageIcon createImageIcon(String path, String description){ 
		return chatContainer.createImageIcon(path, description);
	}

	/**
	 * Progress should be passed as an integer percentage of the progress completed ie (0 >= progress <= 100)
	 * @param progress
	 */
	public void setProgress(int progress){ 
		if(progressBarContainer != null)
			progressBarContainer.setProgress(progress);
	}
	
	public int getMaxProgress(){ 
		return progressBarContainer.getMaxValue();
	}
	
	public int getMinProgress(){ 
		return progressBarContainer.getMinValue();
	}
}
