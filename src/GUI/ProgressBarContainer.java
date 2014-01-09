package GUI;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProgressBarContainer extends JPanel{
	private final int WIDTH = 600;
	private final int HEIGHT = 50;
	private final int PADDING = 30;
	
	private JProgressBar progressBar;
	private int minValue, maxValue;
	
	public ProgressBarContainer(int minValue, int maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		
		progressBar = new JProgressBar(minValue,maxValue);
		progressBar.setValue(minValue);
		progressBar.setToolTipText("This shows you the progress of receiving or sending an image adequately.");
		
		this.add(progressBar);
	}
	
	public void setProgress(int n){
		assert(n <= maxValue && n >= minValue);
		progressBar.setValue(n);
		progressBar.updateUI();
	}
	
	public int getMaxValue(){
		return maxValue;
	}
	
	public int getMinValue(){
		return minValue;
	}
}
