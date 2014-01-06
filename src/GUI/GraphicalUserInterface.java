package GUI;

import java.lang.reflect.InvocationTargetException;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GraphicalUserInterface {
	private ChatContainer chatContainer;
	private ProgressBarContainer progressBarContainer;
	private SystemMessageContainer sysMsgContainer;
	private ButtonsContainer buttonsContainer;
	
	public GraphicalUserInterface(){
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

	public void createAndShowGUI(){
		JFrame frame = new JFrame("Snapchat v1.0");
		JPanel panel = new JPanel();
		
		chatContainer = new ChatContainer();
		progressBarContainer = new ProgressBarContainer();
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
	
	void setMessage(String strMsg){
		if(sysMsgContainer!=null)
			sysMsgContainer.setMessage(strMsg);
	}
	
	void clearMessage(){
		if(sysMsgContainer!=null)
			sysMsgContainer.clearMessage();
	}
}
