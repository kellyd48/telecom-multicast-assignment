package GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

public class ButtonsContainer extends JPanel{
	ButtonsContainer self;
	JButton saveButton, shareButton;
	JFileChooser shareDialog, saveDialog;

	public ButtonsContainer() {
		self = this;
		
		shareDialog = new JFileChooser();
		saveDialog = new JFileChooser();
		
		saveButton = new JButton("Save");
		saveButton.setToolTipText("Use this button to save the received image in a chosen directory.");
		saveButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveDialog.showSaveDialog(self);
			}
		});
		
		shareButton = new JButton("Share...");
		shareButton.setToolTipText("Use this button to select a picture that you want to share with your group.");
		shareButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				shareDialog.showOpenDialog(self);
			}
		});
		this.add(saveButton);
		this.add(shareButton);
	}
	
	
}
