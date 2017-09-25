package de.dfki.irl.darby.prediction.helper.cloud;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.dfki.irl.darby.prediction.accumulation.Globals;
import de.dfki.irl.darby.prediction.database.DBManager;
import de.dfki.irl.darby.prediction.database.Database;
import de.dfki.irl.darby.prediction.database.ResultCallback;
import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.helper.TracePoint;
import de.dfki.irl.darby.prediction.products.Product;

public abstract class CloudFinderA implements ResultCallback {

	protected ArrayList<TracePoint> points = new ArrayList<TracePoint>();
	protected TreeMap<Double,Cloud> clouds = new TreeMap<Double,Cloud>();
	Database db = DBManager.getDatabase(Globals.DBTYPE);
	private int processedTraces;
	protected Trace trace;

	public Trace getTrace() {
		return trace;
	}

	public void setTrace(Trace trace) {
		this.trace = trace;
	}

	public CloudFinderA() {
		super();
	}

	public void addPoints(ArrayList<TracePoint> tracePoints) {
		points.addAll(tracePoints);
	}

	@Override
	public String toString() {
		String ret="Cloudfinder\n\n";
		for (Entry<Double,Cloud> entry:clouds.descendingMap().entrySet()){
			ret +=entry.getKey() + " - " + entry.getValue() + "\n";
		}
		return ret;
	}

	public double getDistance(TracePoint p1, TracePoint p2) {
		return Math.sqrt(Math.pow(p1.getX()-p2.getX(), 2)+Math.pow(p1.getY()-p2.getY(), 2));
	}

	public void receiveTrace(Trace trace) {
		this.trace=trace;
		points.clear();
		clouds.clear();
		points.addAll(trace.getPoints());
		Collection<Cloud> clouds=findClouds(5, 5);
		db.storeClouds(clouds);
		
		processedTraces++;
		if (processedTraces%100==0){
			System.out.println("Processed " + processedTraces + " traces");
		}
	}

	

	public abstract Collection<Cloud> findClouds(int minNumPoints, double maxDiameter);
	
	public void transactionFinished() {
		
		
	}

	public void receiveCloud(Cloud cloud) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void receiveProduct(Product product) {
		// TODO Auto-generated method stub
		
	}
}