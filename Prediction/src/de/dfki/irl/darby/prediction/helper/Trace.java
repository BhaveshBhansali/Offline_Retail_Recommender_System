package de.dfki.irl.darby.prediction.helper;

import java.util.ArrayList;

import de.dfki.irl.darby.prediction.accumulation.Globals;
import de.dfki.irl.darby.prediction.grid.Grid;
import de.dfki.irl.darby.prediction.grid.GridScorer;
import de.dfki.irl.darby.prediction.grid.ShelfScoreTable;
import de.dfki.irl.darby.prediction.helper.cloud.BareCloud;

public class Trace {
	
	private static int idCounter=0;
	private int id=idCounter++;
	private int maxPointDist=11;
	private long startTime;
	private long endTime=0;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private ArrayList<TracePoint> points=new ArrayList<TracePoint>();
	private ArrayList<BareCloud> clouds=new ArrayList<BareCloud>();
	private Trace interpolatedTrace;
	private ShelfScoreTable scoreTable=null;
	
	public TracePoint getInterpolatedPoint(double milliseconds){
		TracePoint before = null,after = null;
		for (int i=0;i<points.size()-1;i++){
			if (points.get(i).getPositionTimeStamp()<startTime+milliseconds && points.get(i+1).getPositionTimeStamp()>startTime+milliseconds){
				//these are our 2 reference points
				before=points.get(i);
				after=points.get(i+1);
				break;
			}
		}
		TracePoint interpolated = calculateInterpolation(milliseconds, before,
				after);
		return interpolated;
		
	}

	public ArrayList<BareCloud> getClouds() {
		return clouds;
	}

	public void setClouds(ArrayList<BareCloud> clouds) {
		this.clouds = clouds;
	}

	private TracePoint calculateInterpolation(double milliseconds,
			TracePoint before, TracePoint after) {
		if (before==null||after==null) return null;
		double timeDifference=after.getPositionTimeStamp()-before.getPositionTimeStamp();
		
		double xDiff=after.getX()-before.getX();
		double yDiff=after.getY()-before.getY();
		double positionDiff=Math.sqrt(xDiff*xDiff+yDiff*yDiff);
		
		if (positionDiff>Globals.interpolation_maxPositionDiff || timeDifference>Globals.interpolation_maxTimeDiff){
			return null;
		}
		
		double wishedDifference = (startTime+milliseconds)-before.getPositionTimeStamp();
		double scale=wishedDifference/timeDifference;
		
		//translation entsprechend anteil skalieren
		xDiff *=scale;
		yDiff *=scale;
		
		TracePoint interpolated = new TracePoint(before.getX()+xDiff, before.getY()+yDiff, (long) (startTime+milliseconds));
		return interpolated;
	}
	
	/**
	 * generiert eine trace mit zeitlich gleichmäßigen stützpunkten, gegebenden gewünschten zeitschritten in ms.
	 * @param timestep in ms
	 * @return
	 */
	public Trace getInterpolatedTrace(){
		
		
		if (interpolatedTrace!=null) return interpolatedTrace;
				
		double timestep=Globals.interpolationTimestep;
		long endtime=points.get(points.size()-1).getPositionTimeStamp();
		long startTime=points.get(0).getPositionTimeStamp();
		
		long currentTime=0;
		
		Trace newTrace=new Trace();
		newTrace.setId(getId());
		
		int currentIndex=0;
		TracePoint before=points.get(currentIndex);
		TracePoint after=points.get(currentIndex+1);
		
		while(currentTime+startTime<=endtime){
			//System.out.println("interpolated:" + (endtime-currentTime));
			TracePoint interpolatedPoint = calculateInterpolation(currentTime, before, after);
			//System.out.println("added " + interpolatedPoint + " between " + before + " and " + after);
			if (interpolatedPoint!=null) {
				newTrace.add(interpolatedPoint);
			}
			
			currentTime+=timestep;
			
			//go further until we have the two neighbouring points
			while(currentIndex+2<points.size() && after.getPositionTimeStamp()<startTime+currentTime){
				currentIndex++;
				before=points.get(currentIndex);
				after=points.get(currentIndex+1);
			}
		}
		interpolatedTrace=newTrace;
		
		return interpolatedTrace;
	}
	public boolean add(TracePoint e) {
		if (points.size()==0){
			startTime=e.getPositionTimeStamp();	
		}
		endTime=e.getPositionTimeStamp();
		return points.add(e);
	}

	public ArrayList<TracePoint> getPoints() {
		return points;
	}
	
	public boolean checkSanity(){
		return checkMaxDist();
	}

	private boolean checkMaxDist() {
		TracePoint lastPoint = null;
		

		for (int i=0;i<points.size();i++){
			TracePoint point=points.get(i);
			double nextPointDist=points.size() >= i+1 ? 0 : points.get(i+1).distanceTo(point);
			if (lastPoint!=null){
				if (lastPoint.distanceTo(point)>maxPointDist && nextPointDist>maxPointDist){
					System.out.println("maxDist violated at " + id + " pt " + i + "  :   " + lastPoint.distanceTo(point) );
					return false;
				}
			}
			lastPoint=points.get(i);
		}
		
		return true;
	}
	
	/**
	 * returns the duration of the trace in *milliseconds* (ms)
	 * @return
	 */
	public long getDuration(){
		return endTime-startTime;
	}
	public ShelfScoreTable getScores(){
		if (scoreTable==null){
			switch(Globals.evaluationMethod){
			case InterpolatedTrace:
				GridScorer scorer=new GridScorer(Grid.getInstance());
				scorer.evaluateTrace(getInterpolatedTrace());
				scoreTable=scorer.getScoreTable();
				break;
				
			case CloudsOnly:
				GridScorer cscorer=new GridScorer(Grid.getInstance());
				cscorer.evaluateClouds(getClouds());
				scoreTable=cscorer.getScoreTable();
				break;
			}
		}
		
		
		
		return scoreTable;
	}
}
