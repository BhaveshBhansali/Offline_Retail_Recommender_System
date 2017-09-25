package de.dfki.irl.darby.prediction.helper.cloud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.dfki.irl.darby.prediction.helper.TracePoint;
import de.dfki.irl.darby.prediction.helper.Tupel;

public class Cloudfinderold {

	private HashMap<Tupel<TracePoint>,Double> distanceMap= new HashMap<Tupel<TracePoint>,Double>();
	
	private ArrayList<TracePoint> points=new ArrayList<TracePoint>();
		
	public void addPoints(ArrayList<TracePoint> trace){
		double distance;
		
		points.addAll(trace);
		for (TracePoint point1:trace){
			for (TracePoint point2:trace){
				distance=getDistance(point1, point2);
				distanceMap.put (new Tupel(point1,point2),distance);
			}
		}
	}
	
	public double getDistance(TracePoint p1, TracePoint p2){
		return Math.sqrt(Math.pow(p1.getX()-p2.getX(), 2)+Math.pow(p1.getY()-p2.getY(), 2));
	}
	
	public ArrayList<Cloud> findClouds(int minNumPoints, int maxDiameter){
		ArrayList<Cloud> possibleClouds=new ArrayList<Cloud>();
		
		/*HashMap<Tupel<TracePoint>,Double> newDistanceMap= new HashMap<Tupel<TracePoint>,Double>();
		//filter out too big distances
		for (Entry<Tupel<TracePoint>,Double> entry:distanceMap.entrySet()){
			if (entry.getValue()<=maxDiameter){
				newDistanceMap.put(entry.getKey(),entry.getValue());
			}
		}
		distanceMap=newDistanceMap;
		
		for (Entry<Tupel<TracePoint>,Double> entry:distanceMap.entrySet()){
			Cloud thisCloud=growCloud(entry.getKey());
			if (thisCloud.getDiameter()<=maxDiameter && thisCloud.getNumberOfPoints()>=minNumPoints) {
				possibleClouds.add(thisCloud);
			}
		}
		
		return possibleClouds;*/
		
		for (TracePoint point: points){
			TreeMap<Double, TracePoint> pointDistances=buildDistanceMap(point);
			Cloud newCloud=new Cloud();
			for (Entry<Double,TracePoint> entry:pointDistances.entrySet()){
				if (entry.getKey()<=maxDiameter){
					newCloud.addPoint(entry.getValue());
				}
				else{
					break;
				}
			}
			possibleClouds.add(newCloud);
		}
		possibleClouds=filterClouds(possibleClouds);
		return possibleClouds;
	}

	/**
	 * wenn punkte in mehreren clustern sind, nur die größten cluster erhalten
	 * @param possibleClouds
	 * @return
	 */
	private ArrayList<Cloud> filterClouds(ArrayList<Cloud> possibleClouds) {
		ArrayList<Cloud> ret=new ArrayList<Cloud>();
		
		//TODO:implement
		return ret;
	}

	private TreeMap<Double, TracePoint> buildDistanceMap(TracePoint reference) {
		TreeMap<Double, TracePoint> ret=new TreeMap<Double, TracePoint>();
		for (TracePoint point:points){
			ret.put(getDistance(reference, point), point);
		}
		return ret;
	}

	private Cloud growCloud(Tupel<TracePoint> key) {
		Cloud cloud=new Cloud();
		cloud.addPoint(key.getT1());
		cloud.addPoint(key.getT2());
		for (Entry<Tupel<TracePoint>,Double> entry:distanceMap.entrySet()){
			if (entry.getKey()!=key){
				
			}
		}
		
		//TODO:implement
		return null;
	}
}
