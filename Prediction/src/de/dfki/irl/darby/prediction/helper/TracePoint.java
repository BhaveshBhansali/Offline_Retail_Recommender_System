/*
 * Copyright (c) 2014 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement.
 *  
 */

package de.dfki.irl.darby.prediction.helper;

public class TracePoint {

	private double x, y, accuracy;
	long positionTimeStamp;
	private String id;
	private int subId;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getSubId() {
		return subId;
	}

	public void setSubId(int subId) {
		this.subId = subId;
	}

	public void setPositionTimeStamp(long positionTimeStamp) {
		this.positionTimeStamp = positionTimeStamp;
	}

	/**
	 * verschiebt den punkt gleichmaessig ueber beide achsen
	 * @param scalar
	 */
	public void shiftEven(double scalar){
		x+=scalar;
		y+=scalar;
	}
	String positionTimeStampText = "-";

	public TracePoint() {
		//
	}
	public TracePoint(TracePoint vorlage) {
		x=vorlage.x;
		y=vorlage.y;
		positionTimeStamp=vorlage.positionTimeStamp;
		accuracy=vorlage.accuracy;
		id=vorlage.id;
		positionTimeStampText=vorlage.positionTimeStampText;
	}

	public double distanceTo(TracePoint other){
		return Math.sqrt(Math.pow((other.x-x),2)+Math.pow((other.y-y),2));
	}
	public TracePoint(double x, double y, long t) {
		this.x = x;
		this.y = y;
		this.positionTimeStamp = t;
	}

	

	public void setPositionTimeStampText(String positionTimeStampText) {
		this.positionTimeStampText = positionTimeStampText;
	}

	public long getPositionTimeStamp() {
		return positionTimeStamp;
	}

	public String getPositionTimeStampText() {
		return positionTimeStampText;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getAccuracy() {
		return accuracy;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

	public void setY(double y) {
		this.y = y;
	}

	

	@Override
	public String toString() {
		return "TracePoint [x=" + x + ", y=" + y + ", accuracy=" + accuracy
				+ ", responseTimeStamp=" + positionTimeStamp
				+ ", responseTimeStampText=" + positionTimeStampText
				+ "]";
	}

	
	

	
}
