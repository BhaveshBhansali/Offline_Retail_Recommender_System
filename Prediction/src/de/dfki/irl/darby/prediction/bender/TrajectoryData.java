package de.dfki.irl.darby.prediction.bender;

import java.awt.Color;
import java.awt.Graphics2D;

import de.dfki.irl.darby.prediction.accumulation.Globals;

public class TrajectoryData implements IPlottable
{
	public StopPointFinder trajectory;
	public Color colorLine = new Color(0xff, 0, 0);
	public Color colorALine = new Color(0, 0, 0xff);
	public Color colorTLine = new Color(0, 0, 0);
	
	public TrajectoryData(StopPointFinder trajectory)
	{
		this.trajectory = trajectory;
	}
	
	public void PlotSpeed(Graphics2D g2, PlotWindow plotWindow) 
	{
		if (trajectory.points.length > 1)
		{
			g2.setColor(colorLine);
			
			Complex A = plotWindow.MapWorldToScreen(trajectory.points[0].time, trajectory.points[0].speed), B;
			Complexi Ai = new Complexi((int) A.x, (int) A.y), 
					 Bi = new Complexi();
			
			for (int n = 0; n < trajectory.points.length; ++n)
			{	
				double time = trajectory.points[n].time;
				if (Globals.useUHopperHack){
					time *=3000;
				}
				B = plotWindow.MapWorldToScreen(time, trajectory.points[n].speed);
				Bi.Set((int) B.x, (int) B.y);
				g2.drawLine(Ai.x, Ai.y, Bi.x, Bi.y);
				Ai.Set(Bi);
			}
		}
	}
	
	public void PlotASpeed(Graphics2D g2, PlotWindow plotWindow) 
	{
		if (trajectory.points.length > 1)
		{
			g2.setColor(colorALine);
			
			Complex A = plotWindow.MapWorldToScreen(trajectory.points[0].time, trajectory.points[0].aSpeed), B;
			Complexi Ai = new Complexi((int) A.x, (int) A.y), 
					 Bi = new Complexi();
			
			for (int n = 0; n < trajectory.points.length; ++n)
			{	
				double time = trajectory.points[n].time;
				double aSpeed = trajectory.points[n].aSpeed;
				if (Globals.useUHopperHack){
					time *=3000;
				}
				B = plotWindow.MapWorldToScreen(time, aSpeed);
				Bi.Set((int) B.x, (int) B.y);
				
				g2.drawLine(Ai.x, Ai.y, Bi.x, Bi.y);
				g2.fillRect(Ai.x - 1, Ai.y - 1, 3, 3);
				
				Ai.Set(Bi);
			}
		}
	}
	
	public void PlotThreshold(Graphics2D g2, PlotWindow plotWindow) 
	{
		if (trajectory.points.length > 1)
		{
			g2.setColor(colorTLine);
			
			double lasttime = trajectory.points[trajectory.points.length - 1].time;
			if (Globals.useUHopperHack){
				lasttime*=3000;
			}
			
			Complex A = plotWindow.MapWorldToScreen(trajectory.points[0].time, Globals.speedThreshold), 
					B = plotWindow.MapWorldToScreen(lasttime, Globals.speedThreshold);
			
			Complexi Ai = new Complexi((int) A.x, (int) A.y), 
					 Bi = new Complexi((int) B.x, (int) B.y);
			
			
			g2.drawLine(Ai.x, Ai.y, Bi.x, Bi.y);
		}
	}
	
	@Override
	public void plot(Graphics2D g2, PlotWindow plotWindow) 
	{
		PlotSpeed(g2, plotWindow);
		PlotASpeed(g2, plotWindow);
		PlotThreshold(g2, plotWindow);
	}

}
