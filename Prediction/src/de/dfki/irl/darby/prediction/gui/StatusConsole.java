package de.dfki.irl.darby.prediction.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class StatusConsole extends JFrame {
	private JTextArea txtConsole;

	private static StatusConsole instance;
	
	public static StatusConsole getInstance(){
		if (instance==null){
			instance=new StatusConsole();
		}
		return instance;
	}
	public StatusConsole(){
		buildGUI();
	}

	private void buildGUI() {
		setTitle("Status Console");
		JPanel mainPanel=new JPanel();
		mainPanel.setLayout(new BorderLayout());
		txtConsole = new JTextArea();
		txtConsole.setAlignmentX(JTextField.LEFT_ALIGNMENT);
		txtConsole.setAlignmentY(TOP_ALIGNMENT);

		mainPanel.add(txtConsole);
		getContentPane().add(mainPanel);
		setPreferredSize(new Dimension(500,400));
		pack();
	}
	
	public void setText(String arg0) {
		txtConsole.setText(arg0);
	}
}
