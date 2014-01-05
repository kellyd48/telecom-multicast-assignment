package GUI;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Frame {

	static public void createAndShowGUI(){
		JFrame frame = new JFrame("Snapchat v1.0");
		JPanel panel = new JPanel();
		
		ChatContainer chatContainer = new ChatContainer();
		ProgressBarContainer progressBarContainer = new ProgressBarContainer();
		SystemMessageContainer sysMsgContainer = new SystemMessageContainer("Waiting...");
		ButtonsContainer buttonsContainer = new ButtonsContainer(chatContainer);

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
	
	static public void main(String[] args){
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
	}

}
