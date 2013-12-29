package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SystemMessageContainer extends JPanel{
	private final int WIDTH = Constants.SYSTEM_MSG_CONTAINER_WIDTH;
	private final int HEIGHT = Constants.SYSTEN_MSG_CONTAINER_HEIGHT;
	private final int PADDING = Constants.SYSTEN_MSG_CONTAINER_PADDING;
	private JLabel labelMsg;
	private String strMsg;

	public SystemMessageContainer(String strMsg){
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		this.strMsg = strMsg;
		labelMsg = new JLabel(strMsg, JLabel.CENTER);
		this.add(labelMsg, BorderLayout.NORTH);
	}
	
	public void setMessage(String strMsg){
		this.strMsg = strMsg;
		labelMsg.setText(this.strMsg);
	}
	
	public void clearMessage(){
		this.strMsg = "";
		labelMsg.setText(this.strMsg);
	}
}
