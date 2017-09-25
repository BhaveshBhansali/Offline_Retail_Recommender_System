package de.dfki.irl.darby.prediction.helper.zones;

import java.util.ArrayList;
import java.util.HashMap;

import de.dfki.irl.darby.prediction.helper.TracePoint;

public class CashDeskFinder {
/**
 * this class determines the nearest cash desk
 * 
 */
	
	private ArrayList<CashDesk> cashDesks = new ArrayList<CashDesk>();
	
	public CashDeskFinder(){
		
		//upper area
		cashDesks.add(new CashDesk(new TracePoint(91.11, 78.2, 0),1));
		cashDesks.add(new CashDesk(new TracePoint(91.11, 75.12, 0),2));
		cashDesks.add(new CashDesk(new TracePoint(91.11, 73.52, 0),3));
		cashDesks.add(new CashDesk(new TracePoint(91.11, 70.44, 0),4));
		cashDesks.add(new CashDesk(new TracePoint(91.11, 69.01, 0),5));
		cashDesks.add(new CashDesk(new TracePoint(91.11, 66.01, 0),6));
		cashDesks.add(new CashDesk(new TracePoint(91.11, 64.52, 0),7));
		cashDesks.add(new CashDesk(new TracePoint(91.11, 61.18, 0),8));
		cashDesks.add(new CashDesk(new TracePoint(91.11, 59.83, 0),9));
		cashDesks.add(new CashDesk(new TracePoint(91.11, 56.63, 0),10));
		
		//crap area
		cashDesks.add(new CashDesk(new TracePoint(93.11, 54.76, 0),11));
		cashDesks.add(new CashDesk(new TracePoint(89.57, 53.51, 0),12));
		cashDesks.add(new CashDesk(new TracePoint(95.33, 51.69, 0),13));
		cashDesks.add(new CashDesk(new TracePoint(91.59, 50.55, 0),14));
		cashDesks.add(new CashDesk(new TracePoint(95.33, 48.76, 0),15));
		cashDesks.add(new CashDesk(new TracePoint(91.59, 47.38, 0),16));
		
		//lower area
		cashDesks.add(new CashDesk(new TracePoint(95.33, 45.69, 0),17));
		cashDesks.add(new CashDesk(new TracePoint(95.33, 42.73, 0),18));
		cashDesks.add(new CashDesk(new TracePoint(95.33, 41.17, 0),19));
		cashDesks.add(new CashDesk(new TracePoint(95.33, 38.2, 0),20));
		cashDesks.add(new CashDesk(new TracePoint(95.33, 36.23, 0),21));
		cashDesks.add(new CashDesk(new TracePoint(95.33, 33.3, 0),22));
		cashDesks.add(new CashDesk(new TracePoint(95.33, 31.78, 0),23));
		cashDesks.add(new CashDesk(new TracePoint(95.33, 28.66, 0),24));
		cashDesks.add(new CashDesk(new TracePoint(95.33, 26.92, 0),25));
		cashDesks.add(new CashDesk(new TracePoint(95.33, 23.89, 0),26));
		cashDesks.add(new CashDesk(new TracePoint(95.33, 22.37, 0),27));
		cashDesks.add(new CashDesk(new TracePoint(95.33, 19.25, 0),28));
		cashDesks.add(new CashDesk(new TracePoint(95.33, 17.64, 0),29));
		cashDesks.add(new CashDesk(new TracePoint(95.33, 14.64, 0),30));
	}
	
	public ArrayList<CashDesk> getCashDesks() {
		return cashDesks;
	}

	public CashDesk get(int index) {
		return cashDesks.get(index);
	}

	public HashMap<CashDesk,Double> getDistanceMap(TracePoint point){
		HashMap<CashDesk, Double> ret=new HashMap<CashDesk,Double>();
		
		for (CashDesk desk: cashDesks){
			ret.put(desk, desk.getPosition().distanceTo(point));
		}
		return ret;
	}
	
}
