package de.dfki.irl.darby.prediction.accumulation.results;

import java.util.HashMap;
import java.util.Map.Entry;

import de.dfki.irl.darby.prediction.helper.zones.CashDesk;

public class AccumulationResult {
	private int traceID;
	private double shoppingTime;
	private double shoppingDistance;
	
	private HashMap<CashDesk, Double> cashDeskDistances;
	private CashDesk usedCashDesk=null;
	private long startPayTime;
	private long startTime;
	private long endTime;
	private String startTimeText;
	private double biggestJump;
	private double avgJump;
	
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
	public String getStartTimeText() {
		return startTimeText;
	}
	public void setStartTimeText(String startTimeText) {
		this.startTimeText = startTimeText;
	}
	public AccumulationResult(int traceID, double shoppingTime,
			double shoppingDistance, HashMap<CashDesk, Double> accumulatedCashDistances, long startPayTime) {
		super();
		this.traceID = traceID;
		this.shoppingTime = shoppingTime;
		this.shoppingDistance = shoppingDistance;
		this.cashDeskDistances = accumulatedCashDistances;
		this.startPayTime = startPayTime;
		if (cashDeskDistances.size()>0){
			usedCashDesk=getMinEntry(cashDeskDistances);
		}
	}
	public double getBiggestJump() {
		return biggestJump;
	}
	public void setBiggestJump(double biggestJump) {
		this.biggestJump = biggestJump;
	}
	public double getAvgJump() {
		return avgJump;
	}
	public void setAvgJump(double avgJump) {
		this.avgJump = avgJump;
	}
	private CashDesk getMinEntry(HashMap<CashDesk, Double> cashDeskDistances2) {
		Entry<CashDesk, Double> minEntry=null;
		
		for (Entry<CashDesk, Double> entry:cashDeskDistances2.entrySet()){
			if (minEntry==null ||minEntry.getValue()>entry.getValue()){
				minEntry=entry;
			}
		}
		
		return minEntry.getKey();
	}
	public int getTraceID() {
		return traceID;
	}
	public void setTraceID(int traceID) {
		this.traceID = traceID;
	}
	public double getShoppingTime() {
		return shoppingTime;
	}
	public void setShoppingTime(double shoppingTime) {
		this.shoppingTime = shoppingTime;
	}
	public double getShoppingDistance() {
		return shoppingDistance;
	}
	public void setShoppingDistance(double shoppingDistance) {
		this.shoppingDistance = shoppingDistance;
	}
	
	public long getStartPayTime() {
		return startPayTime;
	}
	public void setStartPayTime(long startPayTime) {
		this.startPayTime = startPayTime;
	}
	@Override
	public String toString() {
		return "AccumulationResult [traceID=" + traceID + ", \nshoppingTime="
				+ shoppingTime + ", \nshoppingDistance=" + shoppingDistance
				
				+ ", \nusedCashDesk=" + usedCashDesk + ", \nstartPayTime="
				+ startPayTime + "]";
	}
	public CashDesk getUsedCashDesk() {
		return usedCashDesk;
	}
	
	
}
