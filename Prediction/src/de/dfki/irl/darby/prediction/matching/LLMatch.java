package de.dfki.irl.darby.prediction.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.dfki.irl.darby.prediction.bender.Pair;
import de.dfki.irl.darby.prediction.helper.cloud.BareCloud;
import de.dfki.irl.darby.prediction.matching.bonmatching.UPurchase;

public class LLMatch implements LLMatchA {
	private int traceId;
	private ArrayList<BareCloud> clouds;
	private ArrayList<UPurchase> purchases;
	private int realPurchases=0, calculatedPurchases=0;
	private long duration;

	
	public LLMatch(ArrayList<BareCloud> clouds, ArrayList<UPurchase> purchases, int traceId, long duration) {
		super();
		this.clouds = clouds;
		this.purchases = purchases;
		this.traceId=traceId;
		this.duration=duration;
		this.realPurchases=clouds.size();
		this.calculatedPurchases=purchases.size();
		doMatching();
	}
	
	/* (non-Javadoc)
	 * @see de.dfki.irl.darby.prediction.matching.LLMatchA#getNumDelta()
	 */
	@Override
	public int getNumDelta(){
		return calculatedPurchases-realPurchases;
	}

	/* (non-Javadoc)
	 * @see de.dfki.irl.darby.prediction.matching.LLMatchA#getCalculatedPurchases()
	 */
	@Override
	public int getCalculatedPurchases() {
		return calculatedPurchases;
	}

	@Override
	public String toString() {
		return "LLMatch [traceId=" + traceId + ", realPurchases="
				+ realPurchases + ", calculatedPurchases="
				+ calculatedPurchases + ", getNumDelta()=" + getNumDelta()
				+ "]";
	}

	public int getTraceId() {
		return traceId;
	}
	
	
	/* (non-Javadoc)
	 * @see de.dfki.irl.darby.prediction.matching.LLMatchA#getRealPurchases()
	 */
	@Override
	public int getRealPurchases() {
		return realPurchases;
	}
	
	private HashMap<UPurchase,BareCloud> buyToCalc=new HashMap<UPurchase,BareCloud>();
	
	private void doMatching(){
		TreeMap<Double,Pair<UPurchase,BareCloud>> distanceMap = new TreeMap<Double,Pair<UPurchase,BareCloud>>();
		
		for (UPurchase pur:purchases){
			for (BareCloud cloud: clouds){
				Pair<UPurchase,BareCloud> paar=new Pair<UPurchase,BareCloud>(pur,cloud);
				
				double dist=getDist(pur,cloud);
				distanceMap.put(dist, paar);
			}
		}
		
		for (Entry<Double,Pair<UPurchase,BareCloud>> entry:distanceMap.entrySet()){
			UPurchase pur=entry.getValue().first;
			BareCloud cloud=entry.getValue().second;
			
			if (!buyToCalc.containsKey(pur) && !buyToCalc.containsValue(cloud)){
				//we can add this combi
				buyToCalc.put(pur, cloud);
			}
		}
	}

	private double getDist(UPurchase pur, BareCloud cloud) {
		return pur.getCenterTP().distanceTo(cloud.getCenter());
	}
	
	/* (non-Javadoc)
	 * @see de.dfki.irl.darby.prediction.matching.LLMatchA#getAvgWayDistance()
	 */
	@Override
	public double getAvgWayDistance(){
		double sum=0;
		for (Entry<UPurchase,BareCloud> entry:buyToCalc.entrySet()){
			sum +=getDist(entry.getKey(), entry.getValue());
		}
		return sum/buyToCalc.size();
	}
}
