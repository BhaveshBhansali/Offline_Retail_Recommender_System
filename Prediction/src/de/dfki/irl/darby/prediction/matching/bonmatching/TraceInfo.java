package de.dfki.irl.darby.prediction.matching.bonmatching;

public class TraceInfo {
	private int traceID,cashDesk;
	private long payTime,startTime,endTime;
	private double time,distance,biggestJump,averageJump;
	public TraceInfo(int traceID, int cashDesk, long payTime, long startTime,
			long endTime, double time, double distance, double biggestJump,
			double averageJump) {
		super();
		this.traceID = traceID;
		this.cashDesk = cashDesk;
		this.payTime = payTime;
		this.startTime = startTime;
		this.endTime = endTime;
		this.time = time;
		this.distance = distance;
		this.biggestJump = biggestJump;
		this.averageJump = averageJump;
	}
	public int getTraceID() {
		return traceID;
	}
	public void setTraceID(int traceID) {
		this.traceID = traceID;
	}
	public int getCashDesk() {
		return cashDesk;
	}
	public void setCashDesk(int cashDesk) {
		this.cashDesk = cashDesk;
	}
	public long getPayTime() {
		return payTime;
	}
	public void setPayTime(long payTime) {
		this.payTime = payTime;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public double getTime() {
		return time;
	}
	public void setTime(double time) {
		this.time = time;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public double getBiggestJump() {
		return biggestJump;
	}
	public void setBiggestJump(double biggestJump) {
		this.biggestJump = biggestJump;
	}
	public double getAverageJump() {
		return averageJump;
	}
	public void setAverageJump(double averageJump) {
		this.averageJump = averageJump;
	}
	
	
}
