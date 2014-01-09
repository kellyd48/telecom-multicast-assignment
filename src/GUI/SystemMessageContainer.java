package GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SystemMessageContainer extends JPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final int WIDTH = 600;
	private final int HEIGHT = 30;
	private final int PADDING = 10;
	private JLabel labelMsg;

	public SystemMessageContainer(String strMsg){
		this.setLayout(new BorderLayout());
		this.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		labelMsg = new JLabel(strMsg, JLabel.CENTER);
		this.add(labelMsg, BorderLayout.NORTH);
	}
	
	public void setMessage(String strMsg){
		labelMsg.setText(strMsg);
	}
	
	public void clearMessage(){
		labelMsg.setText("");
	}
}
