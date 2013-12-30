package GUI;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressBarContainer extends JPanel{
	private final int WIDTH = Constants.PROGRESS_BAR_CONTAINER_WIDTH;
	private final int HEIGHT = Constants.PROGRESS_BAR_CONTAINER_HEIGHT;
	private final int PADDING = Constants.PROGRESS_BAR_CONTAINER_PADDING;
	
	JProgressBar progressBar;
	
	public ProgressBarContainer() {
		progressBar = new JProgressBar(0,100);
		progressBar.setValue(25);
		progressBar.setToolTipText("This shows you the progress of receiving or sending an image adequately.");
		this.add(progressBar);
	}
}
