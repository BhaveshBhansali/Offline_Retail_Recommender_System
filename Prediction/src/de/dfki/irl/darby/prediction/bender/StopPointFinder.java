package de.dfki.irl.darby.prediction.bender;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import de.dfki.irl.darby.prediction.accumulation.Globals;
import de.dfki.irl.darby.prediction.bender.StopPointFinder.StopPoint;
import de.dfki.irl.darby.prediction.database.MySQLDatabase;
import de.dfki.irl.darby.prediction.database.PostGreSqlDatabase;
import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.helper.TracePoint;
import de.dfki.irl.darby.prediction.helper.cloud.Cloud;
import de.dfki.irl.darby.prediction.helper.cloud.CloudFinderA;
import de.dfki.irl.darby.prediction.matching.bonmatching.UPurchase;

public class StopPointFinder extends CloudFinderA implements IPlottable
{
	public class Point
	{
		public double time;			//in seconds
		public double deltaTimeBefore, deltaTimeNext; 
		
		public Complex position;    //in m
		public Complex deltaPositionBefore, deltaPositionNext;    //in m
		public double  speed;		//in m/s
		
		public Complex aPosition;    //in m
		public Complex aDeltaPositionBefore, aDeltaPositionNext;    //in m
		public double  aSpeed;		//in m/s

		
		@Override
		public String toString() 
		{
			String string = "";
			
			string += "Time: " + time + "\n" +
					  "Position: " + position + "\n" +
					  "Speed: " + speed + "\n" +
					  "APosition: " + aPosition + "\n" +
					  "ASpeed: " + aSpeed;
			
			return string;
		}
	}
	
	public class StopPoint
	{
		Complex position;
		double initialTime, finalTime, deltaTime;
	}
	
	public List<StopPoint> stopPoints = new LinkedList<StopPoint>();
	
	public Point[] points;
	public ArrayList<UPurchase> upurchases=new ArrayList<UPurchase>();
	
	//MySQLDatabase database;
	PostGreSqlDatabase database;
	
	public StopPointFinder()
	{
		points = new Point[0];
		
		try 
		{
			//database = new MySQLDatabase();
			database = new PostGreSqlDatabase();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.out.println("Could not load the database!");
		}
	}
	
	public void ComputeStopPoints(double speedThreshold, double minimumStopTime)
	{
		stopPoints = new LinkedList<StopPoint>();
		int limit = points.length - 1;
		for (int n = 1; n < limit; ++n)
		{
			while (n < limit && points[n].aSpeed > speedThreshold)
				++n;
			
			if (n < limit)
			{
				StopPoint stopPoint = new StopPoint();
				Complex A = new Complex(),
						B = new Complex();
				
				if (points[n-1].aSpeed  > speedThreshold)
				{
					stopPoint.initialTime = points[n].time - points[n].deltaTimeBefore*((speedThreshold - points[n].aSpeed)/(points[n-1].aSpeed - points[n].aSpeed));
				}
				else
				{
					stopPoint.initialTime = points[n-1].time;
				}
				
				++n;
				
				while (n < limit && points[n].aSpeed <= speedThreshold)
					++n;
				
				if (points[n].aSpeed  > speedThreshold)
				{
					stopPoint.finalTime = points[n - 1].time + points[n - 1].deltaTimeNext*((speedThreshold - points[n - 1].aSpeed)/(points[n].aSpeed - points[n-1].aSpeed));
				}
				else
				{
					stopPoint.initialTime = points[n].time;
				}
				
				
				stopPoint.deltaTime = stopPoint.finalTime - stopPoint.initialTime;
				
				if (stopPoint.deltaTime >= minimumStopTime)
				{
					ComputeForwardWeightedPosition(n - 1, stopPoint.finalTime - points[n - 1].time, A);
					ComputeBackwardWeightedPosition(n - 1, points[n - 1].time - stopPoint.initialTime, B);
					stopPoint.position = A.Add(B).Div(stopPoint.deltaTime);
					
					stopPoints.add(stopPoint);
				}
			}
		}
		
	}
	
	public double ComputeForwardWeightedPosition(int index, double deltaTime, Complex weightedPosition)
	{
		double usedDeltaTime = deltaTime;
		weightedPosition.Set(0, 0);
		int limit = points.length - 1;
		
		while (index < limit && deltaTime > points[index].deltaTimeNext)
		{
			weightedPosition.Add(Complex.Add(points[index].position, points[index + 1].position).Mul(points[index].deltaTimeNext/2));
			deltaTime -= points[index].deltaTimeNext;
			++index;
		}
		
		if (index == limit)
		{
			usedDeltaTime -= deltaTime;
		}
		else
		{
			Complex position = Complex.Add(points[index].position, Complex.Mul(points[index].deltaPositionNext, deltaTime/points[index].deltaTimeNext));
			weightedPosition.Add(Complex.Add(points[index].position, position).Mul(deltaTime/2));
		}
		
		return usedDeltaTime;
	}
	
	public double ComputeBackwardWeightedPosition(int index, double deltaTime, Complex weightedPosition)
	{
		double usedDeltaTime = deltaTime;
		weightedPosition.Set(0, 0);
		
		while (index != 0 && deltaTime > points[index].deltaTimeBefore)
		{
			weightedPosition.Add(Complex.Add(points[index].position, points[index - 1].position).Mul(points[index].deltaTimeBefore/2));
			deltaTime -= points[index].deltaTimeBefore;
			--index;
		}
		
		if (index == 0)
		{
			usedDeltaTime -= deltaTime;
		}
		else
		{
			Complex position = Complex.Add(points[index].position, Complex.Mul(points[index].deltaPositionBefore, deltaTime/points[index].deltaTimeBefore));
			weightedPosition.Add(Complex.Add(points[index].position, position).Mul(deltaTime/2));
		}
	
		return usedDeltaTime;
	}
	
	public void ComputeAveragePositions(double timeWindow)
	{
		double halfTimeWindow = timeWindow/2,
			   usedTime;
		
		Complex A = new Complex(),
				B = new Complex();
		
		int limit = points.length - 1;
		for (int n = 1; n < limit; ++n)
		{
			usedTime  = ComputeBackwardWeightedPosition(n, halfTimeWindow, A);
			usedTime += ComputeForwardWeightedPosition(n, halfTimeWindow, B);
			points[n].aPosition.Set(A.Add(B).Div(usedTime));
		}
		
		ComputeADeltaPosition();
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
		
		for (int n = 0; n < points.length; ++n)
		{
			usedTime  = ComputeBackwardPosition(n, halfTimeWindow, A);
			usedTime += ComputeForwardPosition(n, halfTimeWindow, B);
			points[n].speed = Complex.Sub(B, A).Magnitude() / usedTime;
		}
	}

	private void ComputeADeltaPosition()
	{
		points[0].aDeltaPositionBefore = new Complex();
		points[0].aDeltaPositionNext = Complex.Sub(points[1].aPosition, points[0].aPosition);

		int last = points.length - 1;
		for (int n = 1; n < last; ++n)
		{
			points[n].aDeltaPositionBefore = Complex.Neg(points[n - 1].aDeltaPositionNext);
			points[n].aDeltaPositionNext = Complex.Sub(points[n + 1].aPosition, points[n].aPosition);
		}
		
		points[last].aDeltaPositionBefore = Complex.Neg(points[last - 1].aDeltaPositionNext);
		points[last].aDeltaPositionNext = new Complex();
	}

	private double ComputeAForwardPosition(int index, double deltaTime, Complex position)
	{
		double usedDeltaTime = deltaTime;
		
		while (index != points.length && deltaTime > points[index].deltaTimeNext)
		{
			deltaTime -= points[index].deltaTimeNext;
			++index;
		}
		
		if (index == points.length)
		{
			position.Set(points[points.length - 1].aPosition);
			usedDeltaTime -= deltaTime;
		}
		else
		{
			position.Set(Complex.Add(points[index].aPosition, Complex.Mul(points[index].aDeltaPositionNext, deltaTime/points[index].deltaTimeNext)));
		}
		
		return usedDeltaTime;
	}
	
	private double ComputeABackwardPosition(int index, double deltaTime, Complex position)
	{
		double usedDeltaTime = deltaTime;
		
		while (index != -1 && deltaTime > points[index].deltaTimeBefore)
		{
			deltaTime -= points[index].deltaTimeBefore;
			--index;
		}
		
		if (index == -1)
		{
			position.Set(points[0].aPosition);
			usedDeltaTime -= deltaTime;
		}
		else
		{
			position.Set(Complex.Add(points[index].aPosition, Complex.Mul(points[index].aDeltaPositionBefore, deltaTime/points[index].deltaTimeBefore)));
		}
		
		return usedDeltaTime;
	}
	
	public void ComputeASpeed(double timeWindow)
	{
		double halfTimeWindow = timeWindow/2,
			   usedTime;
		
		Complex A = new Complex(),
				B = new Complex();
		
		for (int n = 0; n < points.length; ++n)
		{
			usedTime  = ComputeABackwardPosition(n, halfTimeWindow, A);
			usedTime += ComputeAForwardPosition(n, halfTimeWindow, B);
			points[n].aSpeed = Complex.Sub(B, A).Magnitude() / usedTime;
		}
	}

	/**
	 * load this if trace is already loaded / custom DB system or loading operation is used
	 * @param tracePoints
	 * @return
	 */
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
			points[n].aPosition = new Complex(tracePoint.getX(), tracePoint.getY());
		}
		
		ComputeDeltaTime();
		ComputeDeltaPosition();
		
		
		return true;
	}
	
	public ArrayList<UPurchase> getUpurchases() {
		return upurchases;
	}
	public void load(){
		Load(super.points);
	}

	public void setUpurchases(ArrayList<UPurchase> upurchases) {
		this.upurchases = upurchases;
	}

	/**
	 * use this if you want trace to be loaded automatically, using the db
	 * @param traceId
	 * @return
	 */
	public boolean Load(int traceId)
	{
		Trace trace = database.getTraceByTraceId(traceId);
		ArrayList<TracePoint> tracePoints = trace.getPoints();
		upurchases=database.getUPurForTrace(traceId);
		return Load(tracePoints);
	}

	private int findMaximumSpeedIndex(int first, int last)
	{
		int index = first;
		
		++first;
		while (first <= last)
		{
			if (this.points[index].speed < this.points[first].speed)
				index = first;
			
			++first;
		}
		
		return index;
	}
	private int findMinimumSpeedIndex(int first, int last)
	{
		int index = first;
		
		++first;
		while (first <= last)
		{
			if (this.points[index].speed > this.points[first].speed)
				index = first;
			
			++first;
		}
		
		return index;
	}
	
	private Pair<Integer, Integer> findMinimumSpeedBetween(int first, int last)
	{
		Pair<Integer, Integer> pair = new Pair<Integer, Integer>(first, last);
		
		return pair;
	}

	public Collection<StopPoint> findStopPoints() 
	{
		double timeWindow = Globals.speedTimeWindow;
		double speedThreshold = Globals.speedThreshold;

		Load(super.points);
		
		ComputeAveragePositions(Globals.positionTimeWindow);
		ComputeADeltaPosition();
		ComputeASpeed(Globals.speedTimeWindow);
		ComputeStopPoints(Globals.speedThreshold, Globals.minimumStopTime);

		return this.stopPoints;
	}
	
	@Override
	public Collection<Cloud> findClouds(int minNumPoints, double maxDiameter) 
	{
		load();
		ComputeAveragePositions(Globals.positionTimeWindow);
		ComputeADeltaPosition();
		ComputeASpeed(Globals.speedTimeWindow);
		
		double thr=findOptimumThreshold();
		Globals.speedThreshold=thr;
		ComputeStopPoints(Globals.speedThreshold, Globals.minimumStopTime);
		
		ArrayList<Cloud> ret=new ArrayList<Cloud>();
		
		for (StopPoint point:findStopPoints()){
			double time=point.deltaTime;
			Cloud cloud=new Cloud();
			cloud.setCloudSpeed(time);
			cloud.addPoint(new TracePoint(point.position.x,point.position.y,0));
			cloud.setTraceId(this.trace.getId());
			ret.add(cloud);
		}
		
		return ret;
	}

	public int FindClosestPointTo(Complex position)
	{
		int i = 0;
		double d = Double.MAX_VALUE, aux;
		
		for (int n = 0; n < points.length; ++n)
		{
			aux = Complex.Sub(points[n].position, position).SqrMagnitude();
			if (aux < d)
			{
				d = aux;
				i = n;
			}
		}
		
		return i;
	}
	
	public int FindClosestAPointTo(Complex position)
	{
		int i = 0;
		double d = Double.MAX_VALUE, aux;
		
		for (int n = 0; n < points.length; ++n)
		{
			aux = Complex.Sub(points[n].aPosition, position).SqrMagnitude();
			if (aux < d)
			{
				d = aux;
				i = n;
			}
		}
		
		return i;
	}
	
	Color colorLine = new Color(255, 128, 0);
	Color colorMoving = new Color(0, 0xff, 0);
	Color colorStopped = new Color(0xff, 0, 0);

	public void PlotTrajectory(Graphics2D g2, PlotWindow plotWindow) 
	{
		g2.setColor(colorLine);
		
		Complex A = plotWindow.MapWorldToScreen(points[0].position), B;
		Complexi Ai = new Complexi((int) A.x, (int)  A.y), 
				 Bi = new Complexi();
		
		List<Complexi> stoppedPoints = new LinkedList<Complexi>();
		List<Complexi>  movingPoints = new LinkedList<Complexi>();
		
		if (points[0].speed > Globals.speedThreshold)
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
			
			if (points[n].speed > Globals.speedThreshold)
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
	
	
	Color colorALine = new Color(0, 0, 255);
	Color colorAMoving = new Color(0, 0, 255);
	Color colorAStopped = new Color(0xff, 0, 0xff);

	
	public double findOptimumThreshold(){
		
		double optiThr=0;
		double maxNum=0;
		
		for (double d=0;d<500;d+=0.1){
			Globals.speedThreshold = d;
			ComputeStopPoints(d, Globals.minimumStopTime);
			double score=getScoring();
			//System.out.println("d=" + d + " - #="+stopPoints.size()+ " - score=" + score);
			if (score>maxNum){
				maxNum=score;
				optiThr=d;
			}
		}
		return optiThr;
	}
	/**
	 * wie gut ist das aktuelle ergebnis? je höher der Wert, umso besser
	 * @return
	 */
	private double getScoring() {
		double allDist=0;
		if (stopPoints.size()==1) return 0.1;
		for (StopPoint p:stopPoints){
			for (StopPoint q:stopPoints){
				if (p!=q){
					Complex len=new Complex(p.position);
					len.Sub(q.position);
					allDist+=len.len();
				}
				
			}
			
		}
		return allDist;
	}
	
	/**
	 * wie gut ist das aktuelle ergebnis? je höher der Wert, umso besser
	 * @return
	 * @deprecated
	 */
	private double getScoringUseMin() {
		double allDist=0;
		if (stopPoints.size()==1) return 0.1;
		for (StopPoint p:stopPoints){
			double minLen=Double.MAX_VALUE;
			for (StopPoint q:stopPoints){
				if (p!=q){
					Complex len=new Complex(p.position);
					len.Sub(q.position);
					if (len.len()<minLen){
						minLen=len.len();
					}
				}
				
			}
			System.out.println("minLen:" + minLen);
			allDist+=minLen;
		}
		return allDist;
		//return this.stopPoints.size();
	}

	public void PlotATrajectory(Graphics2D g2, PlotWindow plotWindow) 
	{
		g2.setColor(colorALine);
		
		Complex A = plotWindow.MapWorldToScreen(points[0].aPosition), B;
		Complexi Ai = new Complexi((int) A.x, (int)  A.y), 
				 Bi = new Complexi();
		
		List<Complexi> stoppedPoints = new LinkedList<Complexi>();
		List<Complexi>  movingPoints = new LinkedList<Complexi>();
		
		//find max speed
		
		double maxSpeed=0;
		for (Point p:points){
			if (p.aSpeed>maxSpeed){
				maxSpeed=p.aSpeed;
			}
		}
		
		
		if (points[0].aSpeed > Globals.speedThreshold)
		{
			movingPoints.add(new Complexi(Ai));
		}
		else
		{
			stoppedPoints.add(new Complexi(Ai));
		}
		
		Stroke lineStroke = new BasicStroke(3);
		g2.setStroke(lineStroke);
		for (int n = 1; n < points.length; ++n)
		{
			
			double colOffset=points[n].aSpeed/maxSpeed*255;
			

			
			
			g2.setColor(new Color((int)colOffset,((int)(255-colOffset)),0));
			
			B = plotWindow.MapWorldToScreen(points[n].aPosition);
			Bi.Set((int) B.x, (int)  B.y);
			g2.drawLine(Ai.x, Ai.y, Bi.x, Bi.y);
			
			if (points[n].aSpeed > Globals.speedThreshold)
			{
				movingPoints.add(new Complexi(Bi));
			}
			else
			{
				stoppedPoints.add(new Complexi(Bi));
			}
			
			Ai.Set(Bi);
		}
		
		g2.setColor(colorAMoving);
		for (Complexi point : movingPoints)
		{
			g2.fillOval(point.x - 3, point.y - 3, 6, 6);
		}
		
		g2.setColor(colorAStopped);
		for (Complexi point : stoppedPoints)
		{
			g2.fillOval(point.x - 3, point.y - 3, 6, 6);
		}
	}
	
	Color colorStopPoints1 = new Color(255, 0, 0);
	Color colorStopPoints2 = new Color(0, 0, 0);
	Stroke stroke = new BasicStroke(3);
	
	public void PlotStopPoints(Graphics2D g2, PlotWindow plotWindow) 
	{
		g2.setStroke(stroke);
		Complexi Ai = new Complexi();
		
		for(StopPoint stopPoint : stopPoints)
		{
			Complex A = plotWindow.MapWorldToScreen(stopPoint.position);
			Ai.Set((int) A.x, (int) A.y);
			g2.setColor(colorStopPoints1);
			
			g2.drawLine(Ai.x - 8, Ai.y - 8, Ai.x + 8, Ai.y + 8);
			g2.drawLine(Ai.x - 8, Ai.y + 8, Ai.x + 8, Ai.y - 8);
			
			g2.setColor(colorStopPoints2);
			g2.drawString(""+ ((int) stopPoint.deltaTime), Ai.x + 9, Ai.y + 9);
		}
	}
	
	@Override
	public void plot(Graphics2D g2, PlotWindow plotWindow) 
	{
		//PlotTrajectory(g2, plotWindow);
		PlotATrajectory(g2, plotWindow);
		PlotStopPoints(g2, plotWindow);
		paintUPurchases(g2,plotWindow);
	}
	public void drawCross(Graphics2D g2, Complex position, String caption, PlotWindow plotWindow) 
	{
		Color colorStopPoints1 = new Color(0, 255, 0);
		Color colorStopPoints2 = new Color(0, 0, 0);
		Stroke stroke = new BasicStroke(3);
		
		g2.setStroke(stroke);
		Complexi Ai = new Complexi();
		
		
			Complex A = plotWindow.MapWorldToScreen(position);
			Ai.Set((int) A.x, (int) A.y);
			g2.setColor(colorStopPoints1);
			
			g2.drawLine(Ai.x - 8, Ai.y - 8, Ai.x + 8, Ai.y + 8);
			g2.drawLine(Ai.x - 8, Ai.y + 8, Ai.x + 8, Ai.y - 8);
			
			g2.setColor(colorStopPoints2);
			g2.drawString(caption, Ai.x + 9, Ai.y + 9);
		
	}
	private void paintUPurchases(Graphics2D g2, PlotWindow plotWindow) {
		for (UPurchase pur:upurchases){
			drawCross(g2, new Complex(pur.getCenterX(), pur.getCenterY()),pur.getProdId()+"", plotWindow);
		}
	}
	
}
