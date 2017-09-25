package de.dfki.irl.darby.prediction.helper.cloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

import de.dfki.irl.darby.prediction.database.ResultCallback;
import de.dfki.irl.darby.prediction.helper.TracePoint;
import de.dfki.irl.darby.prediction.products.Product;

public class CloudFinderGrowCloud extends CloudFinderA implements ResultCallback{
	
	public Collection<Cloud> findClouds(int minNumPoints, double maxDiameter){
		
		for (int i=0;i<points.size();i++){
			Cloud cloud=new Cloud();
			if (trace !=null){
				cloud.setTraceId(trace.getId());
			}
			cloud.addPoint(points.get(i));
			cloud=growCloud(cloud,i,i,maxDiameter);
			if (cloud.getNumberOfPoints()>=minNumPoints){
				clouds.put(cloud.getScore(), cloud);				
			}

		}
		
		//spuren rausfiltern, die punkte beinhalten, die eine bessere spur schon verwendet hat
		ArrayList<TracePoint> usedPoints= new ArrayList<TracePoint>();
		ArrayList<Double> toRemove=new ArrayList<Double>();
		
		for (Entry<Double,Cloud> entry:clouds.descendingMap().entrySet()){
			for (TracePoint point:entry.getValue().getPoints()){
				if (usedPoints.contains(point)){
					//point already use-> discard this cloud
					toRemove.add(entry.getKey());
					break;
				}
			}
			//cloud is ok-> add used points to list
			usedPoints.addAll(entry.getValue().getPoints());
		}
		for (Double d:toRemove){
			clouds.remove(d);
		}
		return clouds.values();
		
	}
	private Cloud growCloud(Cloud cloud, int lowerIndex, int upperIndex, double maxDiameter) {
		// TODO Auto-generated method stub
		TracePoint nextPoint;
		
		double prevDist=(double) (lowerIndex>0 ? points.get(lowerIndex-1).distanceTo(cloud.getCenter()) : Double.MAX_VALUE);
		double nextDist=(double) (upperIndex<points.size()-1 ? points.get(upperIndex+1).distanceTo(cloud.getCenter()) : Double.MAX_VALUE);
		
		if (prevDist==Double.MAX_VALUE && nextDist==Double.MAX_VALUE){
			//list is empty->something's wrong->return
			return cloud;
		}
		
		if (prevDist>nextDist){
			nextPoint=points.get(upperIndex+1);
			upperIndex++;
		}
		else{
			nextPoint=points.get(lowerIndex-1);
			lowerIndex--;
		}
		
		cloud.addPoint(nextPoint);
		if (cloud.getDiameter()>maxDiameter){
			cloud.removePoint(nextPoint);
			return cloud;
		}
		
		//note: parameters (lower/upperindex) have already be changed before
		return growCloud(cloud, lowerIndex, upperIndex, maxDiameter);
		
	}
	@Override
	public void receiveProduct(Product product) {
		// TODO Auto-generated method stub
		
	}
}
