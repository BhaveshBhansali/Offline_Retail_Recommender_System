package de.dfki.irl.darby.prediction.helper.cloud;

import de.dfki.irl.darby.prediction.helper.TracePoint;

public class BareCloud {
/**
 * aehnlich der cloud-Klasse, aber ohne inhalt (trajectorypoints). Entsteht wenn eine cloud aus der db geladen wird.
 */
	
	private int traceId,cloudId,numPoints;
	private double score,diameter;
	private TracePoint center;
	public int getTraceId() {
		return traceId;
	}
	public void setTraceId(int traceId) {
		this.traceId = traceId;
	}
	public int getCloudId() {
		return cloudId;
	}
	public void setCloudId(int cloudId) {
		this.cloudId = cloudId;
	}
	public int getNumPoints() {
		return numPoints;
	}
	public void setNumPoints(int numPoints) {
		this.numPoints = numPoints;
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public double getDiameter() {
		return diameter;
	}
	public void setDiameter(double diameter) {
		this.diameter = diameter;
	}
	public TracePoint getCenter() {
		return center;
	}
	public void setCenter(TracePoint center) {
		this.center = center;
	}
	public BareCloud(int traceId, int cloudId, int numPoints, double score,
			double diameter, TracePoint center) {
		super();
		this.traceId = traceId;
		this.cloudId = cloudId;
		this.numPoints = numPoints;
		this.score = score;
		this.diameter = diameter;
		this.center = center;
	}
	
}
