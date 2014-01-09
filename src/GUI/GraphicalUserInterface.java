package GUI;

import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GraphicalUserInterface {
	/*STATES*/
	public static final int DEFAULT_STATE = 0;
	public static final int IMAGE_SHARED_STATE = 1;
	/*------*/
	
	public static final String WINDOW_TITLE = "Snapchat v1.0";
	public static final int DEFAULT_MIN_PROGRESS_VALUE = 0;
	public static final int DEFAULT_MAX_PROGRESS_VALUE = 100;
	
	private ChatContainer chatContainer;
	private ProgressBarContainer progressBarContainer;
	private SystemMessageContainer sysMsgContainer;
	private ButtonsContainer buttonsContainer;
	private int progressMinValue, progressMaxValue;
	private int state;
	private String clientID;
	
	/**
	 * GUI constructor.
	 * Pass in the unique identifier of the client to be displayed in the window title.
	 * @param clientID
	 */
	public GraphicalUserInterface(String clientID){
		this(DEFAULT_MIN_PROGRESS_VALUE, DEFAULT_MAX_PROGRESS_VALUE, clientID);
	}
	
	/**
	 * Constructor creates the JFrame with 4 JLabels in it. One is used for displaying an image(ChatContainer),
	 * one contains progress bar, another one contains system message and the last one contains save and share buttons.
	 * It also sets the state of the GUI to its default state.
	 * @param	progressMinValue	this is the minimum progress value of the JProgressBar that gets created here.
	 * @param	progressMaxValue	this is the maximum progress value of the JProgressBar that gets created here.
	 */
	public GraphicalUserInterface(int progressMinValue, int progressMaxValue, String clientID){
		//default value
		state = 0;
		this.clientID = clientID;
		this.progressMinValue=progressMinValue;
		this.progressMaxValue=progressMaxValue;
		
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
		JFrame frame = new JFrame(WINDOW_TITLE +" - "+ clientID);
		JPanel panel = new JPanel();
		
		chatContainer = new ChatContainer();
		progressBarContainer = new ProgressBarContainer(this.progressMinValue, this.progressMaxValue);
		sysMsgContainer = new SystemMessageContainer("Waiting...");
		buttonsContainer = new ButtonsContainer(this, chatContainer);

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
		
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/**
	 * Sets the system message to be displayed one at a time to update the user with what is
	 * happening backstage at any given moment.
	 * @param	strMsg	Message to be displayed.
	 * */
	public void setMessage(String strMsg){
		if(sysMsgContainer!=null)
			sysMsgContainer.setMessage(strMsg);
	}
	
	/**
	 * Clears the system message to display nothing. It is not necessary to clear the message before setting a new message,
	 * you can just use the setMessage on its own to do this. 
	 * */
	public void clearMessage(){
		if(sysMsgContainer!=null)
			sysMsgContainer.clearMessage();
	}
	
	/**
	 * Displays the image in the GUI when provided a valid BufferedImage in the parameter list.
	 * @param	srcImg	the source image is the image of type BufferedImage that will be displayed when the method is ran.
	 * */
	public void displayImage(BufferedImage srcImg){ 
		chatContainer.displayImage(srcImg);
	}
	
	/**
	 * Displays the image in the GUI when provided a valid ImageIcon in the parameter list.
	 * @param	srcImg	the source image is the image of type ImageIcon that will be displayed when the method is ran.
	 * */
	public void displayImage(ImageIcon srcImg){ 
		System.out.println(srcImg.getIconWidth()+"x"+srcImg.getIconHeight());
		chatContainer.displayImage(srcImg);
	}
	
	/** Clears the image from the GUI so an empty JLabel is displayed.
	 * 
	 * */
	public void clearImage(){}
	
	/**
	 * Creates ImageIcon provided a valid path and some kind of description of the image.
	 * @param	path	absolute or relative file path needs to be provided.
	 * @param	description	brief description of an image, can be left blank by providing an empty string(eg.:"")
	 * @return	ImageIcon	returns an ImageIcon type image from a path provided as a parameter and containing specified the descripition.
	 * */
	public ImageIcon createImageIcon(String path, String description){ 
		File file = new File(path);
		try {
			return chatContainer.createImageIcon(file.toURI().toURL(), description);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Possible states include:
	 * <ul>
	 * <li>DEFAULT_STATE, </li>
	 * <li>IMAGE_SHARED</li>
	 * </ul>
	 * @return	the current state of the GUI. 
	 * */
	public int getState(){ 
		return state;
	}
	
	/**
	 * When there is no action from user the state is set to DEFAULT_STATE and after the user takes some action the state
	 * is set to particular state and after the task initiated by the user is performed the state must be reset to DEFAULT_STATE.
	 * <p>
	 * Example: when user shares some picture the state is set to IMAGE_SHARED and so an adequate check statement
	 * should be implemented in the logic to check for the current state and then the image should be sent to the
	 * multicast-group and the state should be reset to DEFAULT_STATE.
	 * */
	public void setState(int state){ 
		this.state = state;
	}
	
	/**
	 * Sets progress of the progress bar to a given value. 
	 * @param	progress	The progress value has to be in the range: minValue <= progress <= maxValue .
	 * */
	public void setProgress(int progress){ 
		progressBarContainer.setProgress(progress);
	}
	
	/**
	 * @return int	Returns the maximum progress value of the progress bar that was originally set in the constructor.
	 * */
	public int getMaxProgress(){ 
		return progressBarContainer.getMaxValue();
	}
	
	/**
	 * @return int	Returns the minimum progress value of the progress bar that was originally set in the constructor.
	 * */
	public int getMinProgress(){ 
		return progressBarContainer.getMinValue();
	}
	
	/**
	 * Returns the image that the user wishes to share. After image has been used to send it over the state should be reset
	 * to DEFAULT_STATE.
	 * @return	ImageIcon	returns the shared image.
	 * */
	public ImageIcon getSharedImage(){ 
		return buttonsContainer.getSharedImage();
	}

	/**
	 * Returns a file that has been shared by the user using the share button.
	 * Returns null if no file has been shared.
	 * @return
	 */
	public File getSharedImageFile(){
		return buttonsContainer.getSharedFile();
	}
}
