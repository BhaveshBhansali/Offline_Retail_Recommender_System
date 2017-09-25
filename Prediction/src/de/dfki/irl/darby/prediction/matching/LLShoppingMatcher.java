package de.dfki.irl.darby.prediction.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import de.dfki.irl.darby.prediction.accumulation.Globals;
import de.dfki.irl.darby.prediction.bender.Pair;
import de.dfki.irl.darby.prediction.bender.StopPointFinder;
import de.dfki.irl.darby.prediction.database.DBManager;
import de.dfki.irl.darby.prediction.database.DBManager.DatabaseType;
import de.dfki.irl.darby.prediction.database.Database;
import de.dfki.irl.darby.prediction.database.ResultCallback;
import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.helper.cloud.BareCloud;
import de.dfki.irl.darby.prediction.helper.cloud.Cloud;
import de.dfki.irl.darby.prediction.matching.bonmatching.UPurchase;
import de.dfki.irl.darby.prediction.products.Product;

public class LLShoppingMatcher implements ResultCallback {

	
	ArrayList<LLMatch> matches=new ArrayList<LLMatch>();
	//Database db=DBManager.getDatabase(DatabaseType.MYSQL);
	Database db=DBManager.getDatabase(DatabaseType.POSTGRESQL);
	long sumDuration=0;
	int sumBoughts=0;
	
	private int limit=20; //empfang auf n traces limitieren (zum debugging)

	@Override
	public void receiveTrace(Trace trace) {
		if (trace.getPoints().size()==0){
			System.err.println("empty trace:" + trace.getId());
		}
		
		limit--;
		if (limit<=0) return;
		
		processMatching(trace);
			
			

	}

	private void processMatching(Trace trace) {
		

		ArrayList<BareCloud> clouds=db.getCloudsByTrace(trace.getId(), 100000);
		ArrayList<UPurchase> purchases=db.getUPurForTrace(trace.getId());
		
		matches.add(new LLMatch(clouds, purchases, trace.getId(), trace.getDuration()));
		
		sumDuration+=trace.getDuration();
		sumBoughts+=purchases.size();
	}

	@Override
	public void receiveProduct(Product product) {
		// TODO Auto-generated method stub

	}

	public double getPurchasesPerMin(){
		sumDuration/=60000; //convert duration ms->min
		
		return sumBoughts/((double)sumDuration); 
	}
	@Override
	public void transactionFinished() {

			System.out.println(this);

	}
	
	public double getAvgDifference(){
		double sum=0;
		for (LLMatchA match: matches){
			sum +=Math.abs(match.getNumDelta());
		}
		return sum/matches.size();
	}
	
	public double getAvgReal(){
		double sum=0;
		for (LLMatchA match: matches){
			sum +=match.getRealPurchases();
		}
		return sum/matches.size();
	}
	public double getAvgWayDist(){
		double sum=0;
		for (LLMatchA match: matches){
			sum +=match.getAvgWayDistance();
		}
		return sum/matches.size();
	}
	public double getAvgCalculated(){
		double sum=0;
		for (LLMatchA match: matches){
			sum +=match.getCalculatedPurchases();
		}
		return sum/matches.size();
	}

	@Override
	public String toString() {
		return "LLShoppingMatcher [getPurchasesPerMin()="
				+ getPurchasesPerMin() + ", getAvgDifference()="
				+ getAvgDifference() + ", getAvgReal()=" + getAvgReal()
				+ ", getAvgWayDist()=" + getAvgWayDist()
				+ ", getAvgCalculated()=" + getAvgCalculated()
				+ ", getNumMatches()=" + getNumMatches() + ", matches="
				+ matches + "]";
	}

	@Override
	public void receiveCloud(Cloud cloud) {
		// TODO Auto-generated method stub
		
	}

	public int getNumMatches() {
		return matches.size();
	}

	
}
