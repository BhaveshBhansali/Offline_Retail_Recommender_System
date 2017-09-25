package de.dfki.irl.darby.prediction.bender;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import de.dfki.irl.darby.prediction.accumulation.Globals;
import de.dfki.irl.darby.prediction.database.*;
import de.dfki.irl.darby.prediction.helper.*;
import de.dfki.irl.darby.prediction.json.shelf.Inventory;
import de.dfki.irl.darby.prediction.json.shelf.JSONFactory;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.gson.JsonParser;

class ControlFrame extends JFrame implements KeyListener
{
	Inventory inventory;
	StopPointFinder trajectory;
	JTextArea  textArea;
	List<StopPointFinder> trajectories = new LinkedList<StopPointFinder>();
	
	Plotter plotter;
	Plotter functionPlotter;
	
	public class ControlPanel extends JPanel implements ChangeListener, KeyListener
	{
		JSlider sliderTimeWindowPosition;
		JSlider sliderTimeWindowSpeed;
		JSlider sliderSpeedThreshold;
		
		TextField textFieldTimeWindowPosition;
		TextField textFieldTimeWindowSpeed;
		TextField textFieldSpeedThreshold;
		
		Random random = new Random();
		
		double timeWindowPosition = Globals.positionTimeWindow;
		double timeWindowSpeed = Globals.speedTimeWindow;
		double speedThreshold = Globals.speedThreshold;
		
		
		public ControlPanel()
		{
			sliderTimeWindowSpeed = new JSlider(0, 100000, 0);
			sliderSpeedThreshold = new JSlider(0, 100000, 0);
			sliderTimeWindowPosition = new JSlider(10, 100000, 10);
			
			sliderTimeWindowSpeed.addChangeListener(this);	
			sliderSpeedThreshold.addChangeListener(this);
			sliderTimeWindowPosition.addChangeListener(this);
			
			textFieldTimeWindowSpeed = new TextField();
			textFieldSpeedThreshold = new TextField();
			textFieldTimeWindowPosition = new TextField();
			
			textFieldTimeWindowSpeed.setMaximumSize(new Dimension(2000, 20));
			textFieldSpeedThreshold.setMaximumSize(new Dimension(2000, 20));
			textFieldTimeWindowPosition.setMaximumSize(new Dimension(2000, 20));

			this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
			this.add(sliderTimeWindowPosition);
			this.add(textFieldTimeWindowPosition);
			this.add(sliderTimeWindowSpeed);
			this.add(textFieldTimeWindowSpeed);
			this.add(sliderSpeedThreshold);
			this.add(textFieldSpeedThreshold);

			this.addKeyListener(this);
			sliderTimeWindowPosition.addKeyListener(this);
			sliderTimeWindowSpeed.addKeyListener(this);
			sliderSpeedThreshold.addKeyListener(this);
			textFieldTimeWindowSpeed.addKeyListener(this);
			textFieldSpeedThreshold.addKeyListener(this);
			textFieldTimeWindowPosition.addKeyListener(this);
			
			this.setFocusable(true);
		}

		@Override
		public void stateChanged(ChangeEvent arg0) 
		{
			Object source = arg0.getSource();
			
			if (source == sliderTimeWindowSpeed)
			{
				timeWindowSpeed = 25.0*((double)sliderTimeWindowSpeed.getValue()/sliderTimeWindowSpeed.getMaximum());
				textFieldTimeWindowSpeed.setText(Double.toString(timeWindowSpeed));
				trajectory.ComputeSpeed(timeWindowSpeed);
				trajectory.ComputeASpeed(timeWindowSpeed);
				trajectory.ComputeStopPoints(speedThreshold, Globals.minimumStopTime);
				plotter.repaint();
				functionPlotter.repaint();
			}
			else if (source == sliderSpeedThreshold)
			{
				if (Globals.useUHopperHack){
					speedThreshold = 500.0*((double)sliderSpeedThreshold.getValue()/sliderSpeedThreshold.getMaximum());
				}
				else{
					speedThreshold = 3.0*((double)sliderSpeedThreshold.getValue()/sliderSpeedThreshold.getMaximum());					
				}

				updateGraphThresholdChange();
			}
			else if (source == sliderTimeWindowPosition)
			{
				timeWindowPosition = 500.0*sliderTimeWindowPosition.getValue()/sliderTimeWindowPosition.getMaximum();
				textFieldTimeWindowPosition.setText(Double.toString(timeWindowPosition));
				trajectory.ComputeAveragePositions(timeWindowPosition);
				trajectory.ComputeASpeed(timeWindowSpeed);
				trajectory.ComputeStopPoints(speedThreshold, Globals.minimumStopTime);
				plotter.repaint();
				functionPlotter.repaint();
			}
		}

		private void updateGraphThresholdChange() {
			Globals.speedThreshold = speedThreshold;
			textFieldSpeedThreshold.setText(Double.toString(speedThreshold));
			trajectory.ComputeStopPoints(speedThreshold, Globals.minimumStopTime);
			plotter.repaint();
			functionPlotter.repaint();
		}

		String typedNumber = "";
		@Override
		public void keyPressed(KeyEvent arg0) 
		{
			switch (arg0.getKeyCode())
			{
				case KeyEvent.VK_I:
					trajectory.Load(random.nextInt(750));
					trajectory.ComputeSpeed(timeWindowSpeed);
					trajectory.ComputeAveragePositions(timeWindowPosition);
					trajectory.ComputeASpeed(timeWindowSpeed);
					trajectory.ComputeStopPoints(speedThreshold, Globals.minimumStopTime);
					plotter.repaint();
					functionPlotter.repaint();
					
					break;
					
				case KeyEvent.VK_L:
					System.out.println("Load " + typedNumber);
					if (!typedNumber.isEmpty())
					{

						int id = Integer.parseInt(typedNumber);
						typedNumber="";
						trajectory.Load(id);
						
						trajectory.ComputeSpeed(timeWindowSpeed);
						trajectory.ComputeAveragePositions(timeWindowPosition);
						trajectory.ComputeASpeed(timeWindowSpeed);
						trajectory.ComputeStopPoints(speedThreshold, Globals.minimumStopTime);
						plotter.repaint();
						functionPlotter.repaint();
						typedNumber = "";
					}
					break;
				case KeyEvent.VK_A:
					//auto adjust threshold
					double opti=trajectory.findOptimumThreshold();
					
					if (Globals.useUHopperHack){
						int sliderval = (int) ((opti/500)*((double)sliderSpeedThreshold.getMaximum()-sliderSpeedThreshold.getMinimum()));
						sliderSpeedThreshold.setValue(sliderval);
						
					}
					else{
						sliderSpeedThreshold.setValue((int) ((opti/3.0)*((double)sliderSpeedThreshold.getValue()/sliderSpeedThreshold.getMaximum())));
									
					}
					updateGraphThresholdChange();
					break;
				case KeyEvent.VK_0: typedNumber += "0"; break;
				case KeyEvent.VK_MINUS: typedNumber += "-"; break;
				case KeyEvent.VK_1: typedNumber += "1"; break;
				case KeyEvent.VK_2: typedNumber += "2"; break;
				case KeyEvent.VK_3: typedNumber += "3"; break;
				case KeyEvent.VK_4: typedNumber += "4"; break;
				case KeyEvent.VK_5: typedNumber += "5"; break;
				case KeyEvent.VK_6: typedNumber += "6"; break;
				case KeyEvent.VK_7: typedNumber += "7"; break;
				case KeyEvent.VK_8: typedNumber += "8"; break;
				case KeyEvent.VK_9: typedNumber += "9"; break;
				
		
			}
		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub
			
		}
	}
	
	public ControlFrame()
	{
		this.setLayout(new BorderLayout());
		setTitle("Trace Analyzer Studio PRO");
		inventory = JSONFactory.getInventory();
		plotter = new Plotter();

		trajectory = new StopPointFinder();
		if (Globals.useUHopperHack){
			trajectory.Load(688354);
		}
		else{
			trajectory.Load(0);			
		}

		trajectory.ComputeSpeed(1);

		plotter.Add(inventory);
		plotter.Add(trajectory);

		functionPlotter = new Plotter();
		functionPlotter.Add(new TrajectoryData(trajectory));
		functionPlotter.setPreferredSize(new Dimension(-1, 300));
		
		functionPlotter.plotWindow.SetWindowY(0, 3.0, PlotWindow.Space.World);
		functionPlotter.plotWindow.SetWindowX(0, 300.0, PlotWindow.Space.World);
		
		plotter.addKeyListener(this);

		textArea = new JTextArea();
		textArea.setPreferredSize(new Dimension(400, -1));
		textArea.setLineWrap(true);
		textArea.setText("");
		this.add(new ControlPanel(), BorderLayout.PAGE_START);
		this.add(plotter, BorderLayout.CENTER);
		this.add(functionPlotter, BorderLayout.PAGE_END);
		this.add(textArea, BorderLayout.EAST);
		this.pack();
	}

	@Override
	public void keyPressed(KeyEvent arg0) 
	{
		if (arg0.getSource().equals(plotter) && KeyEvent.VK_D == arg0.getKeyCode())
		{
			Point p = plotter.getMousePosition();
			if (p != null)
			{
				Complex sposition = new Complex(p.getX(), p.getY());
				Complex wposition = plotter.plotWindow.MapScreenToWorld(sposition);
				
				int i = trajectory.FindClosestPointTo(wposition);
				String text = "Point: " + i + "\n" + trajectory.points[i].toString() + "\n\n";
				textArea.setText(text);
				
				i = trajectory.FindClosestAPointTo(wposition);
				text = "APoint: " + i + "\n" + trajectory.points[i].toString() + "\n\n";
				textArea.append(text);
				textArea.repaint();
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}


public class TestSolution 
{
	public static void main	(String... args)
	{
		ControlFrame controlFrame = new ControlFrame();
		controlFrame.setVisible(true);
	}
}
