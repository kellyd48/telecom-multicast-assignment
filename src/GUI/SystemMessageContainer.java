package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SystemMessageContainer extends JPanel{
	private final int WIDTH = 600;
	private final int HEIGHT = 30;
	private final int PADDING = 10;
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
