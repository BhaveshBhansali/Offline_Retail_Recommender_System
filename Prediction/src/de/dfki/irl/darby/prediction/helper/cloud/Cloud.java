package de.dfki.irl.darby.prediction.helper.cloud;

import java.util.ArrayList;

import de.dfki.irl.darby.prediction.helper.TracePoint;

public class Cloud {
	ArrayList<TracePoint> points=new ArrayList<TracePoint>();
	int traceId=0;
	double cloudSpeed = Double.MAX_VALUE;

	public void addPoint(TracePoint point){
		points.add(point);
	}
	public int getTraceId() {
		return traceId;
	}
	public void setTraceId(int traceId) {
		this.traceId = traceId;
	}
	public void removePoint(TracePoint point){
		points.remove(point);
	}
	
	//1 means global speed in the cloud is ZERO
		public double getCloudSpeed()
		{
			return cloudSpeed;
		}
		
		public void setCloudSpeed(double speed)
		{
			cloudSpeed = speed;
		}
		

		
	/**
	 * bewertet, wie "gut" der cluster ist: je mehr punkte umso besser, je weniger radius um so besser
	 * @return
	 */
	public double getScore(){
		return getNumberOfPoints()/Math.max(1, getDiameter());
	}
	public ArrayList<TracePoint> getPoints() {
		return points;
	}
	public double getDiameter(){
		double radius=0;
		TracePoint center=getCenter();
		for (TracePoint point:points){
			double thisradius=getDistance(point, center);
			if (thisradius>radius) {
				radius=thisradius;
			}
		}
		return radius*2;
	}
	@Override
	public String toString() {
		return "Cloud [getScore()=" + getScore() + ", getDiameter()="
				+ getDiameter() + ", getNumberOfPoints()="
				+ getNumberOfPoints() + ", getCenter()=" + getCenter()
				+ ", points=" + points + "]";
	}
	public int getNumberOfPoints(){
		return points.size();
	}
	public TracePoint getCenter(){
		double centerx = 0,centery = 0;
		
		for (TracePoint point:points){
			centerx +=point.getX();
			centery +=point.getY();
		}
		return new TracePoint(centerx/points.size(),centery/points.size(),0);
	}
	public double getDistance(TracePoint p1, TracePoint p2){
		return Math.sqrt(Math.pow(p1.getX()-p2.getX(), 2)+Math.pow(p1.getY()-p2.getY(), 2));
	}
}
