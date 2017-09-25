package de.dfki.irl.darby.prediction.matching.bonmatching;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map.Entry;

import de.dfki.irl.darby.prediction.helper.TimeStamp;
import de.dfki.irl.darby.prediction.accumulation.Globals;
import de.dfki.irl.darby.prediction.database.DBManager;
import de.dfki.irl.darby.prediction.database.Database;
import de.dfki.irl.darby.prediction.grid.Grid;
import de.dfki.irl.darby.prediction.grid.GridCell;
import de.dfki.irl.darby.prediction.helper.SAPTimestamp;
import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.products.Inventory;
import de.dfki.irl.darby.prediction.products.Product;

import java.util.Calendar;


public class BonMatcher {
	BonList bons;
	TracesList traces;

	private static Database db = DBManager.getDatabase(Globals.DBTYPE);
	//private SAPTimestamp begin;
	//private SAPTimestamp end;
	private Long begin;
	private Long end;
	
	Inventory inv;

	public static void main(String[] args) {
		/*for (int month = 9; month <= 10; month++) {
			for (int day = 1; day <= 31; day++) {
				System.out.println("Current date:" + day + "." + month);
				BonMatcher matcher = new BonMatcher(new SAPTimestamp(2014,
						month, day, 18, 30, 44));
				matcher.filterRawBons();
			}
		}*/
		
		
		//BonMatcher matcher = new BonMatcher();
		
		
		//matcher.getFinalMatchingDist();
		
		//db.writeBonMatchingsDistance(finaldistance);
		
		//HashMap<Long,String> finalMatching=matcher.getFinalMatching();
		
		//db.writeBonMatchings(finalMatching);
		
		
		
		 //BonMatcher matcher = new BonMatcher(new SAPTimestamp(2015, 01, 21, 12,33, 20)); 
		 //matcher.filterRawBons();
		
		
		 
		/*
		for (int year=2015;year<=2015;year++){
			for (int month=02;month<=03;month++){
				System.out.println("year: "+year+" month: "+month);		
				// Create a calendar object and set year and month
				Calendar mycal = new GregorianCalendar(year, month-1, 1);

				// Get the number of days in that month
				int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH); // 
	
				
				//System.out.println(Timestamp.valueOf(year+"-"+month+"-01 06:00:00"));
				//System.out.println(Timestamp.valueOf(year+"-"+month+"-01 06:00:00").getTime());
				
				//System.out.println(Timestamp.valueOf(year+"-"+month+"-"+daysInMonth+" 22:00:00"));
				//System.out.println(Timestamp.valueOf(year+"-"+month+"-"+daysInMonth+" 22:00:00").getTime());
								
				
				BonMatcher matcher = new BonMatcher(Timestamp.valueOf(year+"-"+month+"-01 06:00:00"),Timestamp.valueOf(year+"-"+month+"-"+daysInMonth+" 22:00:00"));
				matcher.filterRawBons();
				
			}
		}
		*/
		
		BonMatcher matcher = new BonMatcher(Timestamp.valueOf("2016-07-01 05:00:00"),Timestamp.valueOf("2016-07-31 23:00:00"));
		matcher.filterRawBons(); 
		
		
	}

	public BonMatcher() {
		inv=new Inventory();
	}
	
	/*
	public BonMatcher(SAPTimestamp date) {
		//inv=new Inventory();
		System.out.println(date);
		begin = date.clone();
		begin.setHours(7);
		begin.setMinutes(0);
		end = date.clone();
		end.setHours(21);
		end.setMinutes(0);
		
		System.out.println(begin);
		System.out.println(end);
		String a="2015-01-01 12:23:33";
		System.out.println(a.toString());
		String b="2015-01-02 00:00:00";
		java.util.Date today = new java.util.Date();
	    System.out.println(new Timestamp(today.getTime()));
	    System.out.println(Timestamp.valueOf(a).getTime());
	
		
		System.out.println(begin.toSAPString());
		System.out.println(end.toSAPString());
		
		System.out.println(begin.toEpoch()/1000);
		System.out.println(end.toEpoch());
	
		System.out.println(SAPTimestamp.fromEpoch(begin.toEpoch()));

		//bons = new BonList();
		//bons.loadFromDB(begin, end);

		//traces = new TracesList();
		//traces.loadFromDB(begin.toEpoch(), end.toEpoch());
	}
	*/
	
	
	public BonMatcher(Timestamp Begindate,Timestamp Enddate){
		
		inv=new Inventory();
		
		begin=Begindate.getTime()/1000;
		end=Enddate.getTime()/1000;
		
		System.out.println(begin);
		Timestamp ts= new Timestamp(begin*1000);
		
		System.out.println(ts);
		
		
		bons = new BonList();
		bons.loadFromDB(begin,end);   //receipt_id, epoch_time, wid from receipts
		
		traces = new TracesList();
		traces.loadFromDB(begin,end); //trace_id, paytime,cashesk,... from traceinfo (accumtable)

		
	}
	
	
	
	public void filterRawBons() {
		System.out.println("Raw bon filtering done");
		ArrayList<BonInfo> store = prefilter();
		System.out.println("Prefiltering succesful. Got " + store.size()
				+ " bons. Populating...");
		store = populateBons(store);
		System.out.println("Populating done. Got " + store.size()
				+ " bons. Storing in new DB... ");
		db.writeBonInfos(store);
		System.out.println("Writing done..exiting");
	}

	private ArrayList<BonInfo> prefilter() {
		ArrayList<BonInfo> ret = new ArrayList<BonInfo>();

		for (TraceInfo info : traces.getTraceInfos()) {
			long payTime = info.getPayTime();
			SAPTimestamp stamp = SAPTimestamp.fromEpoch(payTime);
			/*
			 * System.out.println("processing trace " + info.getTraceID() +
			 * " - time " + payTime);
			 */
			if (payTime > 0) {
				// go further in the bon list (which is sorted by time) until we
				// are at the time when the trace is at cashzone
				if (!bons.shiftRTime(payTime)) {
					// no more bons loaded-> return
					return ret;
				}
				ArrayList<BonInfo> bonBlock = getBonBlock(info, payTime); //get bons for the neighbouring workstations and times nearby
				ret.addAll(bonBlock);
			}
		}

		return ret;  //
	}

	private ArrayList<BonInfo> populateBons(ArrayList<BonInfo> bons) {
		ArrayList<BonInfo> ret = new ArrayList<BonInfo>();

		// build a map, which says for each workstationID and transactionnumber,
		// which BonInfos
		// are containing it
		HashMap<Entry<Long, Long>, ArrayList<BonInfo>> transactionToBon = new HashMap<Entry<Long, Long>, ArrayList<BonInfo>>();
		ArrayList<Long> transactionsToExtract = new ArrayList<Long>();

		for (BonInfo info : bons) {

			SimpleEntry<Long, Long> entry = new SimpleEntry<Long, Long>(
					info.getWorkstationID(), info.getBonID());

			ArrayList<BonInfo> infolist = transactionToBon.get(entry);

			if (infolist == null) {
				infolist = new ArrayList<BonInfo>();

				transactionToBon.put(entry, infolist);

			}
			transactionsToExtract.add(info.getBonID());
			infolist.add(info);
		}

		// then, take the complete bon from the db and copy the data from the
		// enriched BonInfos (containing matched traceID, matchingtype etc.)

		Iterable<BonInfo> rawBons = db.getRawBons(begin, end,transactionsToExtract); // get bonid, epochtime, wid, eans, quantity

		System.out.println("Got raw bons from db");

		for (BonInfo info : rawBons) {
			SimpleEntry<Long, Long> entry = new SimpleEntry<Long, Long>(
					info.getWorkstationID(), info.getBonID());

			ArrayList<BonInfo> bonsUsingIt = transactionToBon.get(entry); // get boninfo for wid and bonid
			if (bonsUsingIt != null) {
				// at least one bon uses this workstation/TA number -
				ArrayList<BonInfo> populatedinfos = populateBonInfos(info,bonsUsingIt);

				for (BonInfo popinfo : populatedinfos) { 
					if (!ret.contains(popinfo)) { // popinfo has to be unique to get to be added by ret(arraylist)
						ret.add(popinfo);
					}
				}

			}
		}

		// last, output this set of complete BonInfos, containing matched trace,
		// matching type and product eans

		return ret;
	}

	private ArrayList<BonInfo> populateBonInfos(BonInfo info,
			ArrayList<BonInfo> enhInfos) {
		ArrayList<BonInfo> ret = new ArrayList<BonInfo>();

		for (BonInfo enhInfo : enhInfos) {
			BonInfo newInfo = info.clone();
			newInfo.setTraceId(enhInfo.getTraceId());
			newInfo.setMatchingType(enhInfo.getMatchingType());
			ret.add(newInfo);
		}

		return ret;
	}

	/**
	 * returns the bons for the neighbouring workstations and times nearby
	 * 
	 * @param info
	 * @param payTime
	 * @return
	 */
	private ArrayList<BonInfo> getBonBlock(TraceInfo info, long payTime) {

		ArrayList<BonInfo> infos = new ArrayList<BonInfo>();

		if (bons.workstationExists(info.getCashDesk())
				&& Math.abs(payTime - bons.workstationEpoch(info.getCashDesk())) < Globals.matching_maxTimediff)
			infos.addAll(getTimeLine(info.getCashDesk(), ".0",
					info.getTraceID()));
		if (bons.workstationExists(info.getCashDesk() - 1)
				&& Math.abs(payTime
						- bons.workstationEpoch(info.getCashDesk() - 1)) < Globals.matching_maxTimediff)
			infos.addAll(getTimeLine(info.getCashDesk() - 1, "U1",
					info.getTraceID()));
		if (bons.workstationExists(info.getCashDesk() - 2)
				&& Math.abs(payTime
						- bons.workstationEpoch(info.getCashDesk() - 2)) < Globals.matching_maxTimediff)
			infos.addAll(getTimeLine(info.getCashDesk() - 2, "U2",
					info.getTraceID()));
		if (bons.workstationExists(info.getCashDesk() + 1)
				&& Math.abs(payTime
						- bons.workstationEpoch(info.getCashDesk() + 1)) < Globals.matching_maxTimediff)
			infos.addAll(getTimeLine(info.getCashDesk() + 1, "L1",
					info.getTraceID()));
		if (bons.workstationExists(info.getCashDesk() + 2)
				&& Math.abs(payTime
						- bons.workstationEpoch(info.getCashDesk() + 2)) < Globals.matching_maxTimediff)
			infos.addAll(getTimeLine(info.getCashDesk() + 2, "L2",
					info.getTraceID()));

		return infos;
	}

	private ArrayList<BonInfo> getTimeLine(int cashDesk, String typeCode,
			int traceID) {
		ArrayList<BonInfo> ret = new ArrayList<BonInfo>();

		if (bons.timeExists(cashDesk, 0))
			ret.add(brandInfo(bons.getOtherInfo(cashDesk, 0), typeCode + ".0",
					traceID));
		if (bons.timeExists(cashDesk, -1))
			ret.add(brandInfo(bons.getOtherInfo(cashDesk, -1), typeCode + "B1",
					traceID));
		if (bons.timeExists(cashDesk, -2))
			ret.add(brandInfo(bons.getOtherInfo(cashDesk, -2), typeCode + "B2",
					traceID));
		if (bons.timeExists(cashDesk, +1))
			ret.add(brandInfo(bons.getOtherInfo(cashDesk, +1), typeCode + "A1",
					traceID));
		if (bons.timeExists(cashDesk, +2))
			ret.add(brandInfo(bons.getOtherInfo(cashDesk, +2), typeCode + "A2",
					traceID));
		return ret;
	}

	private BonInfo brandInfo(BonInfo info, String matchingType, int traceID) {
		info.setTraceId(traceID);
		info.setMatchingType(matchingType);
		return info;
	}
	
	
	
	private void getFinalMatchingDist(){
			
		
		
		EnhancedBonList bons=db.getEnhancedBons();
		Grid grid=Grid.getInstance();
		for (Long traceId:bons.getTraceIDs()){
			
				
			if (traceId==null) continue;
			Trace trace=db.getTraceByTraceId((int)((long)traceId));
			grid.resetTraces();
			grid.addTrace(trace);
			for (BonInfo info:bons.getIdealBonList(traceId)){
				double maxDist=getMaxDist(info,grid);
				System.out.println(traceId+" : "+maxDist+" : "+info.getMatchingType());
				
				//ret.put(traceId, maxDist);
					
				
			}
			//no matching found-> take closest one maybe
		}
		
		//return ret;
	}
		
	
	private HashMap<Long,String> getFinalMatching(){
		
		HashMap<Long,String> ret=new HashMap<Long,String>();
		
		EnhancedBonList bons=db.getEnhancedBons();
		Grid grid=Grid.getInstance();
		for (Long traceId:bons.getTraceIDs()){
			
				
			if (traceId==null) continue;
			Trace trace=db.getTraceByTraceId((int)((long)traceId));
			grid.resetTraces();
			grid.addTrace(trace);
			for (BonInfo info:bons.getIdealBonList(traceId)){
				double maxDist=getMaxDist(info,grid);
				if (maxDist<Globals.maxBonMatchingDist){
					System.out.println(traceId+" : "+info.getMatchingType());
					ret.put(traceId, info.getMatchingType());
					break; //found matching -> goto next trace
				}
			}
			//no matching found-> take closest one maybe
		}
		
		return ret;
	}

	private double getMaxDist(BonInfo info, Grid grid) {
		double ret=0;
		for (Long ean:info.getMaterials()){
			System.out.println("EAN: "+ean);
			Product prod=inv.getProductByEan(ean);
			System.out.println("Product: "+prod);
			if (prod!=null){
				//product found-> calculate distance
				System.out.println("test1");
				
				ArrayList<GridCell> blockingCells = prod.getBlockingCells();
				if (blockingCells!=null){
					System.out.println("test2");
					double dist=grid.getDistanceToTrace(blockingCells);
					if (dist>ret) ret=dist;
				}
			}
		}
		return ret;
	}
}
