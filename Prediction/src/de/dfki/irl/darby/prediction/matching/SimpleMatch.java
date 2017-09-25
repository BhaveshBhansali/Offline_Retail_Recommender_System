package de.dfki.irl.darby.prediction.matching;

import java.util.ArrayList;

import de.dfki.irl.darby.prediction.helper.cloud.BareCloud;
import de.dfki.irl.darby.prediction.matching.bonmatching.UPurchase;

public class SimpleMatch implements LLMatchA {

	private int traceId;
	private ArrayList<BareCloud> clouds;
	private ArrayList<UPurchase> purchases;
	private int realPurchases=0, calculatedPurchases=0;
	private long duration;

	private final double purchasesPerMin=1.25;
	
	public SimpleMatch(ArrayList<BareCloud> clouds, ArrayList<UPurchase> purchases, int traceId, long duration) {
		super();
		for (int i=0; i<clouds.size();i++){
			//create fake clouds
		}
		this.purchases = purchases;
		this.traceId=traceId;
		this.duration=duration;
		this.realPurchases=purchases.size();
		this.calculatedPurchases=(int) ((duration/60000)*purchasesPerMin);

	}
	
	@Override
	public int getNumDelta() {
		// TODO Auto-generated method stub
		return calculatedPurchases-realPurchases;
	}

	@Override
	public int getCalculatedPurchases() {
		// TODO Auto-generated method stub
		return calculatedPurchases;
	}

	@Override
	public int getRealPurchases() {
		// TODO Auto-generated method stub
		return realPurchases;
	}

	@Override
	public double getAvgWayDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

}
