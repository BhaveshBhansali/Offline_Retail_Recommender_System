package de.dfki.irl.darby.prediction.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.dfki.irl.darby.prediction.accumulation.Globals;

public class SettingsPanel extends JFrame {
private JCheckBox chkClouds;
private JCheckBox chkInventory;
private JCheckBox chkPossibilities;
private TraceGUIPanel parentForm;
private JTextField txtminScore;
private JCheckBox chkInterpolated;
private JCheckBox chkTrace;
private JCheckBox chkGridPossibilities;
private JCheckBox chkHighpoints;
private JCheckBox chkProducts;
private JTextField txtminDist;
private JCheckBox chkPurchase;
private AbstractButton chkConsole;
public static void main(String[] args){
	SettingsPanel pan=new SettingsPanel();
	//pan.buildGui();
}



public TraceGUIPanel getParentForm() {
	return parentForm;
}

public SettingsPanel(){
	buildGui();
}

public void setParentForm(TraceGUIPanel parentForm) {
	this.parentForm = parentForm;
}



public void buildGui() {
	
	//setLayout(new GridLayout(2, 1));
	
	this.setTitle("Settings");
	JPanel mainpanel=new JPanel();
	GroupLayout mainLayout = new GroupLayout(mainpanel);
	mainpanel.setLayout(mainLayout);
	
	JPanel panCheckboxes=new JPanel();
	panCheckboxes.setLayout(new BoxLayout(panCheckboxes,BoxLayout.Y_AXIS));
	
	panCheckboxes.setBorder(BorderFactory.createTitledBorder("Options"));
	chkClouds = new JCheckBox("Show Clouds");
	chkClouds.setSelected(Globals.guiShowClouds);
	chkInventory = new JCheckBox("Show Inventory");
	chkInventory.setSelected(Globals.guiShowInventory);
	chkTrace = new JCheckBox("Show orig. trace");
	chkTrace.setSelected(Globals.guiShowTrace);
	chkInterpolated = new JCheckBox("Show interp. trace");
	chkInterpolated.setSelected(Globals.guiShowInterpolated);
	chkPossibilities = new JCheckBox("Show Shelf prob.");
	chkPossibilities.setSelected(Globals.guiShowShelfPossibilities);
	chkGridPossibilities = new JCheckBox("Show Grid prob.");
	chkGridPossibilities.setSelected(Globals.guiShowGridPossibilities);
	chkHighpoints = new JCheckBox("Show high points");
	chkHighpoints.setSelected(Globals.guiShowHighpoints);
	chkPurchase = new JCheckBox("Show guessed Purchases");
	chkPurchase.setSelected(Globals.guiShowPurchases);
	chkProducts = new JCheckBox("Show actual Purchases");
	chkProducts.setSelected(Globals.guiShowProducts);
	chkConsole = new JCheckBox("Show status console");
	chkConsole.setSelected(Globals.guiShowConsole);
	
	//text fields
	JPanel panNumerics=new JPanel();
	JPanel panMinScore=new JPanel();
	panMinScore.setBorder(BorderFactory.createTitledBorder("minCloudScore"));
	txtminScore = new JTextField();
	txtminScore.setText(Globals.maxCloudSpeed+"");
	txtminScore.setHorizontalAlignment(JTextField.RIGHT);
	panMinScore.setLayout(new BorderLayout());
	panMinScore.add(txtminScore,BorderLayout.CENTER);
	
	JPanel panMinDist=new JPanel();
	panMinDist.setBorder(BorderFactory.createTitledBorder("Filter Distance"));
	txtminDist = new JTextField();
	txtminDist.setText(Globals.minDist+"");
	txtminDist.setHorizontalAlignment(JTextField.RIGHT);
	panMinDist.setLayout(new BorderLayout());
	panMinDist.add(txtminDist,BorderLayout.CENTER);
	
	panNumerics.add(panMinScore);
	panNumerics.add(panMinDist);
	panNumerics.setLayout(new BoxLayout(panNumerics,BoxLayout.Y_AXIS));
	panCheckboxes.add(chkClouds);
	
	panCheckboxes.add(chkInventory);
	panCheckboxes.add(chkTrace);
	panCheckboxes.add(chkInterpolated);
	panCheckboxes.add(chkPossibilities);
	panCheckboxes.add(chkGridPossibilities);
	panCheckboxes.add(chkHighpoints);
	panCheckboxes.add(chkPurchase);
	panCheckboxes.add(chkProducts);
	panCheckboxes.add(chkConsole);
	mainpanel.add(panCheckboxes);
	mainpanel.add(panNumerics);
	
	JButton btnApply=new JButton("Apply");
	btnApply.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			btnApplyClicked();
		}
	});
	
	JPanel btnPanel=new JPanel();
	btnPanel.setLayout(new BorderLayout());
	btnPanel.add(btnApply,BorderLayout.CENTER);
	mainpanel.add(btnPanel);
	
	//group layouting
	mainLayout.setHorizontalGroup(mainLayout.createParallelGroup().addComponent(panCheckboxes).addComponent(panNumerics).addComponent(btnPanel));
	mainLayout.setVerticalGroup(mainLayout.createSequentialGroup().addComponent(panCheckboxes).addComponent(panNumerics).addComponent(btnPanel));
	
	getContentPane().add(mainpanel);
	pack();
}

protected void btnApplyClicked() {
	Globals.guiShowClouds=chkClouds.isSelected();
	Globals.guiShowInventory=chkInventory.isSelected();
	Globals.guiShowInterpolated=chkInterpolated.isSelected();
	Globals.guiShowTrace=chkTrace.isSelected();
	Globals.guiShowShelfPossibilities=chkPossibilities.isSelected();
	Globals.guiShowGridPossibilities=chkGridPossibilities.isSelected();
	Globals.guiShowHighpoints=chkHighpoints.isSelected();
	Globals.guiShowPurchases=chkPurchase.isSelected();
	Globals.guiShowProducts=chkProducts.isSelected();
	Globals.guiShowConsole=chkConsole.isSelected();
	

	StatusConsole console = StatusConsole.getInstance();
	console.setVisible(Globals.guiShowConsole);
	console.setBounds((int) (this.getBounds().x), this.getBounds().y+this.getBounds().height, console.getPreferredSize().width,console.getPreferredSize().height);
	double minCloudScore,minDist;
	
	try{
		minCloudScore=Double.parseDouble(txtminScore.getText());
		Globals.maxCloudSpeed=minCloudScore;
		minDist=Double.parseDouble(txtminDist.getText());
		Globals.minDist=minDist;
		parentForm.reloadDBData();
	}
	catch(NumberFormatException nf){
		//do nothing
		JOptionPane.showMessageDialog(null,"Invalid number in text Field 'minCloudScore'");
	}
	if (parentForm!=null){
		parentForm.settingsChanged();
	}
}
}
