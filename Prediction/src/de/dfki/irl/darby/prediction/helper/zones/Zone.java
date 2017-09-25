package de.dfki.irl.darby.prediction.helper.zones;

public class Zone {
	private double minX,maxX,minY,maxY;

	
	
	public Zone(double minX, double maxX, double minY, double maxY) {
		super();
		this.minX = minX;
		this.maxX = maxX;
		this.minY = minY;
		this.maxY = maxY;
	}

	public boolean isInside(double x, double y){
		return isInbetween(minX, x, maxX) && isInbetween(minY, y, maxY);
	}
	
	private static boolean isInbetween(double l, double m, double u) {
		
		return l<=m && m<=u;
	}
}
