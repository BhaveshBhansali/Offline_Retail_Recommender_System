package de.dfki.irl.darby.prediction.matching.bonmatching;

import de.dfki.irl.darby.prediction.helper.TracePoint;

public class UPurchase {
	private long traceId,prodId;
	private int centerX,centerY;
	public UPurchase(long traceId, long prodId, int centerX, int centerY) {
		super();
		this.traceId = traceId;
		this.prodId = prodId;
		this.centerX = centerX;
		this.centerY = centerY;
	}
	public long getTraceId() {
		return traceId;
	}
	public void setTraceId(long traceId) {
		this.traceId = traceId;
	}
	public long getProdId() {
		return prodId;
	}
	public void setProdId(long prodId) {
		this.prodId = prodId;
	}
	public int getCenterX() {
		return centerX;
	}
	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}
	public int getCenterY() {
		return centerY;
	}
	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}
	public TracePoint getCenterTP(){
		return new TracePoint(centerX, centerY, -1);
	}
}
