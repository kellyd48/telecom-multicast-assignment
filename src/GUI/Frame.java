package GUI;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Frame {
	static final int WIDTH = 640;
	static final int HEIGHT =640;

	static public void createAndShowGUI(){
		JFrame frame = new JFrame("Snapchat v1.0");
		ChatContainer chatContainer = new ChatContainer();
		ProgressBarContainer progressBarContainer = new ProgressBarContainer();
		SystemMessageContainer sysMsgContainer = new SystemMessageContainer("Waiting...");

		frame.setLayout(new BorderLayout());
		
		frame.add(chatContainer, BorderLayout.PAGE_START);
		frame.add(progressBarContainer, BorderLayout.CENTER);
		frame.add(sysMsgContainer, BorderLayout.PAGE_END);
		frame.pack();
		
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	static public void main(String[] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}

}
