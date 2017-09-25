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
import javax.swing.JTextField;

import de.dfki.irl.darby.prediction.accumulation.Globals;
import de.dfki.irl.darby.prediction.database.DBManager;
import de.dfki.irl.darby.prediction.database.Database;
import de.dfki.irl.darby.prediction.database.ResultCallback;
import de.dfki.irl.darby.prediction.gui.TraceVisualisation.VisualisationMode;
import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.helper.TracePoint;
import de.dfki.irl.darby.prediction.helper.cloud.Cloud;
import de.dfki.irl.darby.prediction.products.Product;

public class AllTraceGUI implements ResultCallback {

	private TraceVisualisation visu;
	Database db;
	private JTextField txtTraceId;
	private JPanel mainPanel;
	private SettingsPanel panSettings;
	private ArrayList<TracePoint> allPoints=new ArrayList<TracePoint>();
	private int numTraces;
	
	
	public static void main(String[] args){
		AllTraceGUI gui=new AllTraceGUI();

	}
	public AllTraceGUI() {
		db=DBManager.getDatabase(Globals.DBTYPE);
		db.registerCallback(this);
		buildGUI();
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
		/*JButton cmdForward=new JButton(">");
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
		});*/
		
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

	
	protected void cmdGoClicked() {
		numTraces = 0;
		try{
			numTraces=Integer.parseInt(txtTraceId.getText());
		}
		catch(NumberFormatException nf){
			JOptionPane.showMessageDialog(null,"Could not parse traceID");
			return;
		}
		

		
		visu.setVisuMode(VisualisationMode.AllTraces);
		//db.getAllTraces(Long startTime, Long endTime);
		
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

	@Override
	public void receiveTrace(Trace trace) {
		if (trace.getId()<=this.numTraces){
			allPoints.addAll(trace.getPoints());
			if (trace.getId()%100==0){
				visu.setTracePoints(allPoints);
				visu.repaint();
				System.out.println("Received " + trace.getId() + " traces");
			}
		}
		
	}

	@Override
	public void receiveCloud(Cloud cloud) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveProduct(Product product) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void transactionFinished() {
		visu.setTracePoints(allPoints);
		visu.repaint();
		System.out.println("All traces drawn");
		
	}

}
