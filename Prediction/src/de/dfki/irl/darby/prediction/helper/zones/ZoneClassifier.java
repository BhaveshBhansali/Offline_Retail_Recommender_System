package de.dfki.irl.darby.prediction.helper.zones;

import de.dfki.irl.darby.prediction.helper.TracePoint;

public class ZoneClassifier {
	private ZonePolygon cashierArea;
	private ZonePolygon afterCashierArea;
	
	public boolean isAtCashier(TracePoint point){
		return cashierArea.isInside(point.getX(), point.getY());
	}
	public boolean isAfterCashier(TracePoint point){
		return afterCashierArea.isInside(point.getX(), point.getY());
	}

	
	public ZoneClassifier(){
		Zone uppercash=new Zone(89.36,92.35,55.7,79);
		Zone lowercash=new Zone(93.58,96.25,13.5,46.6);
		
		cashierArea=new ZonePolygon();
		cashierArea.add(lowercash);
		cashierArea.add(uppercash);
		Zone upperaftercash=new Zone(92.35,100,50,79);
		Zone loweraftercash=new Zone(96.25,110,13.5,50);
		
		cashierArea=new ZonePolygon();
		cashierArea.add(lowercash);
		cashierArea.add(uppercash);
		
		afterCashierArea=new ZonePolygon();
		afterCashierArea.add(loweraftercash);
		afterCashierArea.add(upperaftercash);
	}
}
