package de.dfki.irl.darby.prediction.accumulation.results;

public class NumericResult {
	private double min=Double.MAX_VALUE,acc,max=Double.MIN_VALUE;
	private int numOfVals=0;
	
	public void newValue(double value){
		if (value<min) min=value;
		if (value>max) max=value;
		
		acc+=value;
		numOfVals++;
	}

	public double getMin() {
		return min;
	}

	public double getAvg() {
		return acc/numOfVals;
	}

	public double getMax() {
		return max;
	}

	@Override
	public String toString() {
		return "[getAvg()=" + getAvg() + ", min=" + min
				+ ", max=" + max + ", numOfVals=" + numOfVals + "]";
	}
	
	
}
