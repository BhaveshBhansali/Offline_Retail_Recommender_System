package de.dfki.irl.darby.prediction.database;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.sql.Timestamp;

import de.dfki.irl.darby.prediction.accumulation.results.AccumulationResult;
import de.dfki.irl.darby.prediction.helper.SAPTimestamp;
import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.helper.cloud.BareCloud;
import de.dfki.irl.darby.prediction.helper.cloud.Cloud;
import de.dfki.irl.darby.prediction.matching.bonmatching.BonInfo;
import de.dfki.irl.darby.prediction.matching.bonmatching.EnhancedBonList;
import de.dfki.irl.darby.prediction.matching.bonmatching.TraceInfo;
import de.dfki.irl.darby.prediction.matching.bonmatching.UPurchase;

public interface Database {

	public abstract ArrayList<Integer> getTraceIds();

	public abstract Trace getTraceByTraceId(int id);

	public abstract ArrayList<BareCloud> getCloudsByTrace(int traceID,
			double minScore);

	/*
	 * Asynchronous methods, results sent by a call back
	 */
	public abstract void registerCallback(ResultCallback callback);

	public abstract void unregisterCallback(ResultCallback callback);

	public abstract void getAllTraces();

	public abstract void getAllClouds();

	public abstract void getAllProducts();

	public abstract void storeAccumulationresults(
			ArrayList<AccumulationResult> res);

	public abstract void storeClouds(Collection<Cloud> clouds);

	public abstract void saveTrace(Trace t);

	public ArrayList<TraceInfo> getTraceInfos(long startTimestamp,
			long endTimestamp);

	public HashMap<Long, ArrayList<BonInfo>> getRawBons(Long startTime,
			Long endTime);

	//public HashMap<Long, ArrayList<BonInfo>> getRawBons(SAPTimestamp startTime,
			//SAPTimestamp endTime);

	public Iterable<BonInfo> getRawBons(Long startTime,
			Long endTime, Iterable<Long> transnumbers);

	
	//public Iterable<BonInfo> getRawBons(SAPTimestamp startTime,
			//SAPTimestamp endTime, Iterable<Long> transnumbers);

	public void writeBonInfos(ArrayList<BonInfo> infos);

	public abstract ArrayList<TraceInfo> getTraceInfosByPaytime(long startTime,
			long endTime);

	public EnhancedBonList getEnhancedBons();

	public BonInfo getBonForTrace(long traceId);

	public ArrayList<UPurchase> getUPurForTrace(long traceId);
	
	public void writeBonMatchingsDistance(HashMap<Long, Double> matchingsDistance); // Dist Distribution
	
	public void writeBonMatchings(HashMap<Long, String> matchings);
}