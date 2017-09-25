package de.dfki.irl.darby.prediction.bender;

import java.awt.Color;
import java.awt.Graphics2D;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.dfki.irl.darby.prediction.database.MySQLDatabase;
import de.dfki.irl.darby.prediction.database.ResultCallback;
import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.helper.TracePoint;
import de.dfki.irl.darby.prediction.helper.cloud.Cloud;
import de.dfki.irl.darby.prediction.helper.cloud.CloudFinderA;
import de.dfki.irl.darby.prediction.products.Product;

public class CloudFinderBender extends CloudFinderA implements IPlottable,ResultCallback
{
	class Point
	{
		public Complex position;    //in m
		public double time;			//in seconds
		public double deltaTimeBefore, deltaTimeNext; 
		public Complex deltaPositionBefore, deltaPositionNext;    //in m
		public double speed;		//in m/s
	}
	
	public Point[] points;
	public double speedThreshold = 0;
	MySQLDatabase database;
	
	public CloudFinderBender()
	{
		points = new Point[0];
		
		try 
		{
			database = new MySQLDatabase();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.out.println("Could not load the database!");
		}
	}
	
	private void ComputeDeltaTime()
	{
		points[0].deltaTimeBefore = 0;
		points[0].deltaTimeNext = points[1].time;
		
		int last = points.length - 1;
		for (int n = 1; n < last; ++n)
		{
			points[n].deltaTimeBefore = points[n - 1].deltaTimeNext;
			points[n].deltaTimeNext = points[n + 1].time - points[n].time;
		}
		
		points[last].deltaTimeBefore = points[last - 1].deltaTimeNext;
		points[last].deltaTimeNext = 0;
	}
	
	private void ComputeDeltaPosition()
	{
		points[0].deltaPositionBefore = new Complex();
		points[0].deltaPositionNext = Complex.Sub(points[1].position, points[0].position);

		int last = points.length - 1;
		for (int n = 1; n < last; ++n)
		{
			points[n].deltaPositionBefore = Complex.Neg(points[n - 1].deltaPositionNext);
			points[n].deltaPositionNext = Complex.Sub(points[n + 1].position, points[n].position);
		}
		
		points[last].deltaPositionBefore = Complex.Neg(points[last - 1].deltaPositionNext);
		points[last].deltaPositionNext = new Complex();
	}

	private double ComputeForwardPosition(int index, double deltaTime, Complex position)
	{
		double usedDeltaTime = deltaTime;
		
		while (index != points.length && deltaTime > points[index].deltaTimeNext)
		{
			deltaTime -= points[index].deltaTimeNext;
			++index;
		}
		
		if (index == points.length)
		{
			position.Set(points[points.length - 1].position);
			usedDeltaTime -= deltaTime;
		}
		else
		{
			position.Set(Complex.Add(points[index].position, Complex.Mul(points[index].deltaPositionNext, deltaTime/points[index].deltaTimeNext)));
		}
		
		return usedDeltaTime;
	}
	
	private double ComputeBackwardPosition(int index, double deltaTime, Complex position)
	{
		double usedDeltaTime = deltaTime;
		
		while (index != -1 && deltaTime > points[index].deltaTimeBefore)
		{
			deltaTime -= points[index].deltaTimeBefore;
			--index;
		}
		
		if (index == -1)
		{
			position.Set(points[0].position);
			usedDeltaTime -= deltaTime;
		}
		else
		{
			position.Set(Complex.Add(points[index].position, Complex.Mul(points[index].deltaPositionBefore, deltaTime/points[index].deltaTimeBefore)));
		}
		
		return usedDeltaTime;
	}
	
	public void ComputeSpeed(double timeWindow)
	{
		double halfTimeWindow = timeWindow/2,
			   usedTime;
		
		Complex A = new Complex(),
				B = new Complex();
		
		ComputeDeltaTime();
		ComputeDeltaPosition();
		
		for (int n = 0; n < points.length; ++n)
		{
			usedTime  = ComputeBackwardPosition(n, halfTimeWindow, A);
			usedTime += ComputeForwardPosition(n, halfTimeWindow, B);
			points[n].speed = Complex.Sub(B, A).Magnitude() / usedTime;
		}
	}

	public boolean Load(ArrayList<TracePoint> tracePoints)
	{
		TracePoint tracePoint;
		double initialTimeStamp = tracePoints.get(0).getPositionTimeStamp();
		points = new Point[tracePoints.size()];
		
		if (tracePoints.isEmpty())
			return false;
		
		for (int n = 0; n < points.length; ++n)
		{
			tracePoint = tracePoints.get(n);
			points[n] = new Point();
			points[n].position = new Complex(tracePoint.getX(), tracePoint.getY());
			points[n].time = (tracePoint.getPositionTimeStamp() - initialTimeStamp) / 1000.0;
		}
		
		return true;
	}
	
	public boolean Load(int traceId)
	{
		Trace trace = database.getTraceByTraceId(traceId);
		ArrayList<TracePoint> tracePoints = trace.getPoints();
		return Load(tracePoints);
	}

	
	Color colorLine = new Color(255, 128, 0);
	Color colorMoving = new Color(0, 0xff, 0);
	Color colorStopped = new Color(0xff, 0, 0);
	@Override
	public void plot(Graphics2D g2, PlotWindow plotWindow) 
	{
		g2.setColor(colorLine);
		
		Complex A = plotWindow.MapWorldToScreen(points[0].position), B;
		Complexi Ai = new Complexi((int) A.x, (int)  A.y), 
				 Bi = new Complexi();
		
		List<Complexi> stoppedPoints = new LinkedList<Complexi>();
		List<Complexi>  movingPoints = new LinkedList<Complexi>();
		
		if (points[0].speed > speedThreshold)
		{
			movingPoints.add(new Complexi(Ai));
		}
		else
		{
			stoppedPoints.add(new Complexi(Ai));
		}
		
		for (int n = 1; n < points.length; ++n)
		{
			B = plotWindow.MapWorldToScreen(points[n].position);
			Bi.Set((int) B.x, (int)  B.y);
			g2.drawLine(Ai.x, Ai.y, Bi.x, Bi.y);
			
			if (points[n].speed > speedThreshold)
			{
				movingPoints.add(new Complexi(Bi));
			}
			else
			{
				stoppedPoints.add(new Complexi(Bi));
			}
			
			Ai.Set(Bi);
		}
		
		g2.setColor(colorMoving);
		for (Complexi point : movingPoints)
		{
			g2.fillOval(point.x - 3, point.y - 3, 6, 6);
		}
		
		g2.setColor(colorStopped);
		for (Complexi point : stoppedPoints)
		{
			g2.fillOval(point.x - 3, point.y - 3, 6, 6);
		}
	}

	
	@Override
	public Collection<Cloud> findClouds(int minNumPoints, double maxDiameter) 
	{
		double timeWindow = 8.8;
		double speedThreshold = 0.3;
		double halfTimeWindow = timeWindow/2.0;
		double usedTime, totalTime;
		Complex A = new Complex(),
				B = new Complex();
		
		int firstPointIndex, 
			lastPointIndex;

		Load(super.points);
		this.ComputeSpeed(timeWindow);
		
		Collection<Cloud> clouds = new LinkedList<Cloud>();
		Cloud newCloud;
		
		for (int n = 0; n < this.points.length; ++n)
		{
			while (n < this.points.length && 
				   this.points[n].speed > speedThreshold) ++n;
			
			if (n < this.points.length)
			{
				newCloud = new Cloud();
				newCloud.setTraceId(trace.getId());
				while (n < this.points.length && this.points[n].speed <= speedThreshold)
				{
					newCloud.addPoint(super.points.get(n));
					++n;
				}
				//Cloud Speed Computation
				firstPointIndex = n - newCloud.getNumberOfPoints();
				lastPointIndex  = n - 1;

				usedTime =  ComputeForwardPosition(lastPointIndex, halfTimeWindow, B);
				usedTime += ComputeBackwardPosition(firstPointIndex, halfTimeWindow, A);
				usedTime += this.points[lastPointIndex].time - this.points[firstPointIndex].time;
				newCloud.setCloudSpeed(B.Sub(A).Magnitude()/usedTime);
				//...

				
				clouds.add(newCloud);
			}
		}
		
		return clouds;
	}

	
}
