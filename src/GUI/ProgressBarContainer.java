package GUI;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressBarContainer extends JPanel{
	private final int WIDTH = 600;
	private final int HEIGHT = 50;
	private final int PADDING = 30;
	
	JProgressBar progressBar;
	
	public ProgressBarContainer() {
		progressBar = new JProgressBar(0,100);
		progressBar.setValue(25);
		progressBar.setToolTipText("This shows you the progress of receiving or sending an image adequately.");
		this.add(progressBar);
	}
}
