package de.dfki.irl.darby.prediction.accumulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import de.dfki.irl.darby.prediction.accumulation.results.AccumulationResult;
import de.dfki.irl.darby.prediction.accumulation.results.CountingResult;
import de.dfki.irl.darby.prediction.accumulation.results.NumericResult;
import de.dfki.irl.darby.prediction.database.DBManager;
import de.dfki.irl.darby.prediction.database.ResultCallback;
import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.helper.TracePoint;
import de.dfki.irl.darby.prediction.helper.cloud.Cloud;
import de.dfki.irl.darby.prediction.helper.zones.CashDesk;
import de.dfki.irl.darby.prediction.helper.zones.CashDeskFinder;
import de.dfki.irl.darby.prediction.helper.zones.ZoneClassifier;
import de.dfki.irl.darby.prediction.products.Product;

public class TraceAccumulator implements ResultCallback{
/**
 * Diese Klasse nimmt geparste Punkte einer trajectory auf 
 * und speichert dazu akkumulierte daten, wie dauer des einkaufs, zurückgelegte streke etc.
 */
	
	//temporäre sachen -> trace-spezifisch
	private HashMap<CashDesk,Double> accumulatedCashDistances;
	private CashDeskFinder finder=new CashDeskFinder();
	private int numOfDistances=0; //wie oft wurde was in der CashDistance-Map addiert? How often was what added to the CashDistance map?
	
	//globale sachen
	private ArrayList<AccumulationResult> results=new ArrayList<AccumulationResult>();
	private NumericResult distance=new NumericResult();
	private NumericResult time=new NumericResult();
	private CountingResult<CashDesk> desks=new CountingResult<CashDesk>();
	
	//callback sachen
	int receivedCallbacks=0;
	private int unknownCash;
	
	
	public void accumulateTrace(Trace t){
		
		t.checkSanity();
		
		double distance=0;
		long atCashTimestamp=0;
		double biggestJump=0;
		
		TracePoint lastPoint=null;
		int numofPoints=t.getPoints().size();
		
		ZoneClassifier classifier=new ZoneClassifier();
		int atCashierSerie=0; // wie oft hintereinander war der Punkt in der cash area?  How often in succession was the point in the cash area?
		
		ArrayList<TracePoint> cashPoints=new ArrayList<TracePoint>();
		
		accumulatedCashDistances=new HashMap<CashDesk,Double>();
		numOfDistances=0;
		
		//debug vars
		int numOfAtcash=0;
		
		for (TracePoint point:t.getPoints()){
			
			//distance calculation
			if (lastPoint!=null){
				double stepDistance = lastPoint.distanceTo(point);
				distance+=stepDistance;
				if (stepDistance>biggestJump){
					biggestJump=stepDistance;
				}
			}
			lastPoint=point;
			
			
			//cashier stuff
			if (classifier.isAtCashier(point)){
				atCashierSerie++;
				
				numOfAtcash++;
				
				
				if (atCashierSerie>Globals.minCashierSerie){
					
					if (atCashTimestamp==0){
						//zum ersten mal sicher in der cashZone  For the first time safe in the cashZone
						atCashTimestamp=point.getPositionTimeStamp();  //epoch_time
					}
					
					//vorhergehende pkte reinnehmen  Previous pkte
					if (cashPoints.size()>0){
						for (TracePoint pt:cashPoints){
							findCashZone(point);
							numOfDistances++;
						}
						cashPoints.clear();
					}
					
					//distanzen berechnen und akkumulieren  Calculate and accumulate
					findCashZone(point);
					numOfDistances++;

				}
				else{
					cashPoints.add(point);
				}
			}
			else{
				
				//wieder außerhalb der kasse, bevor  minCashierSerie erreicht wurde-> alles resetten
				//Again outside the cash register, before minCashierSerie was reached-> everything resetten
				atCashierSerie=0;
				cashPoints.clear();
			}
		}
		normalizeCashDists();
		
		double time=t.getPoints().get(numofPoints-1).getPositionTimeStamp()-t.getPoints().get(0).getPositionTimeStamp();
		AccumulationResult res=new AccumulationResult(t.getId(), time/60000, distance, accumulatedCashDistances, atCashTimestamp);
		res.setEndTime(t.getPoints().get(numofPoints-1).getPositionTimeStamp());
		res.setStartTime(t.getPoints().get(0).getPositionTimeStamp());
		String positionTimeStampText = t.getPoints().get(0).getPositionTimeStampText();
		res.setStartTimeText(positionTimeStampText);
		//String positionTimeStampText1 = t.getPoints().get(numofPoints-1).getPositionTimeStampText();
		//res.setStartTimeText(positionTimeStampText1);
		res.setBiggestJump(biggestJump);
		res.setAvgJump(distance/t.getPoints().size());
		
		this.distance.newValue(distance);
		this.time.newValue(time/60000);
		this.desks.addValue(res.getUsedCashDesk());
		
		if (res.getUsedCashDesk()==null){
			unknownCash++;
		}
		
		this.results.add(res);
		
		//System.out.println("atCash:" + numOfAtcash + "/" + t.getPoints().size());
	}
	
	public String printCashDistances(){
		String ret="";
		for (int i=1;i<30;i++){
			ret +="CashDesk " + i + " : " + accumulatedCashDistances.get(finder.get(i))+ "\n";
		}
		return ret;
	}

	public ArrayList<AccumulationResult> getResults() {
		return results;
	}

	public NumericResult getDistance() {
		return distance;
	}

	public NumericResult getTime() {
		return time;
	}

	public CountingResult<CashDesk> getDesks() {
		return desks;
	}

	//wird nach abschluss der cashdesk-akkumulation aufgerufen: normalisiert die Summe der distanzen über die anzahl der punkte
	//Is called after completion of cashdesk accumulation: normalizes the sum of the distances over the number of points
	private void normalizeCashDists(){
		HashMap<CashDesk,Double> ret= new HashMap<CashDesk,Double>();
		for (Entry<CashDesk,Double> distance:accumulatedCashDistances.entrySet()){
			ret.put(distance.getKey(), distance.getValue()/numOfDistances);
		}
		accumulatedCashDistances=ret;
	}
private void findCashZone(TracePoint point) {
	HashMap<CashDesk, Double> actualDistances=finder.getDistanceMap(point);
	for (Entry<CashDesk,Double> distance:actualDistances.entrySet()){
		Double currentDist=accumulatedCashDistances.get(distance.getKey());
		
		//if entry does not exist, set it to zero
		if (currentDist==null) currentDist=0.0;
		
		accumulatedCashDistances.put(distance.getKey(), currentDist+distance.getValue());
	}
	numOfDistances++;
}

@Override
public String toString() {
	return "TraceAccumulator [distance=" + distance + ", time=" + time
			+ ", desks=" + desks + ", unknownCashs=" + unknownCash+ ", results=" + results + "]";
}

@Override
public void receiveTrace(Trace trace) {
	accumulateTrace(trace);
	receivedCallbacks++;
	if (receivedCallbacks%1000==0){
		System.out.println("Processed " + receivedCallbacks + " traces");
	}
}

@Override
public void transactionFinished() {
	//System.out.println(this);
	System.out.println(results);
	writeResultsToDB();
}

private void writeResultsToDB() {
	// TODO Auto-generated method stub
	DBManager.getDatabase(Globals.DBTYPE).storeAccumulationresults(results);
	System.out.println("Stored "+results.size()+" accumulation results in DB");
}

@Override
public void receiveCloud(Cloud cloud) {
	// TODO Auto-generated method stub
	
}

@Override
public void receiveProduct(Product product) {
	// TODO Auto-generated method stub
	
}


}
