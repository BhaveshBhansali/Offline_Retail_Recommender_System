package de.dfki.irl.darby.prediction.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import de.dfki.irl.darby.prediction.accumulation.Globals;
import de.dfki.irl.darby.prediction.database.DBManager;
import de.dfki.irl.darby.prediction.database.Database;
import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.helper.cloud.BareCloud;
import de.dfki.irl.darby.prediction.json.shelf.JSONFactory;
import de.dfki.irl.darby.prediction.matching.bonmatching.BonInfo;
import de.dfki.irl.darby.prediction.products.Inventory;
import de.dfki.irl.darby.prediction.products.Product;

public class TraceGUIPanel {

	private TraceVisualisation visu;
	Database db;
	private JTextField txtTraceId;
	private JPanel mainPanel;
	private SettingsPanel panSettings;
	
	public TraceGUIPanel() {
		db=DBManager.getDatabase(Globals.DBTYPE);
		buildGUI();
		panSettings = new SettingsPanel();
		panSettings.setParentForm(this);
	}

	private void buildGUI() {
		JFrame mainFrame=new JFrame("TraceGUI");
		
		mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		JPanel ctrlPanel=new JPanel();
		ctrlPanel.setLayout(new BorderLayout());
		txtTraceId = new JTextField();
		JButton cmdGo=new JButton("Go");
		ctrlPanel.add(txtTraceId,BorderLayout.CENTER);
		
		JPanel btnPanel=new JPanel();
		btnPanel.add(cmdGo);
		ctrlPanel.add(btnPanel,BorderLayout.EAST);
		
		cmdGo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cmdGoClicked();			
			}
		});
		JButton cmdForward=new JButton(">");
		JButton cmdBackward=new JButton("<");
		JButton cmdBigger=new JButton("+");
		JButton cmdSmaller=new JButton("-");
		JButton cmdSettings=new JButton("...");
		btnPanel.add(new JSeparator(JSeparator.VERTICAL));
		btnPanel.add(cmdBackward);
		btnPanel.add(cmdForward);
		btnPanel.add(new JSeparator(JSeparator.VERTICAL));
		btnPanel.add(cmdBigger);
		btnPanel.add(cmdSmaller);
		btnPanel.add(new JSeparator(JSeparator.VERTICAL));
		btnPanel.add(cmdSettings);
		
		cmdSettings.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cmdSettingsClicked();
				
			}
		});
		cmdBackward.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cmdBackwardClicked();
				
			}
		});
		cmdForward.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cmdForwardClicked();
				
			}
		});
		
		cmdBigger.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cmdBiggerClicked();
				
			}
		});
		cmdSmaller.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cmdSmallerClicked();
				
			}
		});
		
	mainPanel.add(ctrlPanel, BorderLayout.NORTH);
	visu = new TraceVisualisation(null);
	JScrollPane scrollPane = new JScrollPane(visu);
	mainPanel.add(scrollPane, BorderLayout.CENTER);
	
	
	
	mainFrame.getContentPane().add(mainPanel);
	mainFrame.setPreferredSize(new Dimension(1000, 800));
	mainFrame.pack();
	mainFrame.setVisible(true);
	mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	protected void cmdSettingsClicked() {
		panSettings.setVisible(!panSettings.isVisible());
		panSettings.setBounds((int) (mainPanel.getBounds().x+mainPanel.getBounds().getWidth()), mainPanel.getBounds().y, panSettings.getPreferredSize().width, panSettings.getPreferredSize().height);
	}

	protected void cmdBiggerClicked() {
		Dimension size=visu.getSize();
		size.setSize(size.width*2, size.height*2);
		visu.setPreferredSize(size);
		visu.invalidate();
		visu.doLayout();
		mainPanel.doLayout();
		mainPanel.validate();
		visu.repaint();
	}
	protected void cmdSmallerClicked() {
		Dimension size=visu.getSize();
		size.setSize(size.width/2, size.height/2);
		visu.setPreferredSize(size);
		visu.invalidate();
		visu.doLayout();
		mainPanel.doLayout();
		mainPanel.validate();
		visu.repaint();
	}

	protected void cmdForwardClicked() {
		int traceId=-1;
		try{
			traceId=Integer.parseInt(txtTraceId.getText());
		}
		catch(NumberFormatException nf){
			
			return;
		}
		traceId++;
		txtTraceId.setText(traceId+"");

		Trace t=db.getTraceByTraceId(traceId);
		if (t==null|| t.getPoints().size()==0){
			JOptionPane.showMessageDialog(null,"Trace not found or empty");
			return;
		}
		visu.setTrace(t);
		BonInfo info=db.getBonForTrace(traceId);
		ArrayList<Product> products=new ArrayList<Product>();
		if (info !=null){
			ArrayList<Long> eans=info.getMaterials();
			if (eans!=null){
				
				Inventory inv=new Inventory();
				
				for (Long ean:eans){
					Product prod=inv.getProductByEan(ean);
					if (prod!=null) products.add(prod);
				}
			}
		}
		visu.setProducts(products);
		ArrayList<BareCloud> clouds=db.getCloudsByTrace(t.getId(), Globals.maxCloudSpeed);
		if (clouds!=null){
			
			visu.setClouds(clouds);
		}
		else{
			visu.resetClouds();
		}
	}

	protected void cmdBackwardClicked() {
		int traceId=-1;
		try{
			traceId=Integer.parseInt(txtTraceId.getText());
		}
		catch(NumberFormatException nf){
			
			return;
		}
		traceId--;
		txtTraceId.setText(traceId+"");

		Trace t=db.getTraceByTraceId(traceId);
		if (t==null|| t.getPoints().size()==0){
			JOptionPane.showMessageDialog(null,"Trace not found or empty");
			return;
		}
		visu.setTrace(t);
		BonInfo info=db.getBonForTrace(traceId);
		ArrayList<Product> products=new ArrayList<Product>();
		if (info !=null){
			ArrayList<Long> eans=info.getMaterials();
			if (eans!=null){
				
				Inventory inv=new Inventory();
				
				for (Long ean:eans){
					Product prod=inv.getProductByEan(ean);
					if (prod!=null) products.add(prod);
				}
			}
		}
		visu.setProducts(products);
		ArrayList<BareCloud> clouds=db.getCloudsByTrace(t.getId(), Globals.maxCloudSpeed);
		if (clouds!=null){
			visu.setClouds(clouds);
		}
		else{
			visu.resetClouds();
		}
	}

	protected void cmdGoClicked() {
		int traceId=-1;
		try{
			traceId=Integer.parseInt(txtTraceId.getText());
		}
		catch(NumberFormatException nf){
			JOptionPane.showMessageDialog(null,"Could not parse traceID");
			return;
		}
		Trace t=db.getTraceByTraceId(traceId);
		if (t==null|| t.getPoints().size()==0){
			JOptionPane.showMessageDialog(null,"Trace not found or empty");
			return;
		}
		ArrayList<BareCloud> clouds=db.getCloudsByTrace(t.getId(), Globals.maxCloudSpeed);
		
		BonInfo info=db.getBonForTrace(traceId);
		ArrayList<Product> products=new ArrayList<Product>();
		if (info !=null){
			ArrayList<Long> eans=info.getMaterials();
			if (eans!=null){
				
				Inventory inv=new Inventory();
				
				for (Long ean:eans){
					Product prod=inv.getProductByEan(ean);
					if (prod!=null) products.add(prod);
				}
			}
		}
		visu.setProducts(products);
				
				
		visu.setTrace(t);
		if (clouds!=null){
			visu.setClouds(clouds);
		}
		else{
			visu.resetClouds();
		}
		
	}

	public void settingsChanged() {
		visu.invalidate();
		visu.doLayout();
		mainPanel.doLayout();
		mainPanel.validate();
		visu.repaint();
		
	}

	public void reloadDBData() {
		cmdGoClicked();
		
	}

}
