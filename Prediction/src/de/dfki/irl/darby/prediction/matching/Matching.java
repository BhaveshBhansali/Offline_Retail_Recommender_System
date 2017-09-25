package de.dfki.irl.darby.prediction.matching;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import de.dfki.irl.darby.prediction.accumulation.Globals;
import de.dfki.irl.darby.prediction.accumulation.TraceAccumulator;
import de.dfki.irl.darby.prediction.bender.StopPointFinder;
import de.dfki.irl.darby.prediction.database.DBManager;
import de.dfki.irl.darby.prediction.database.Database;
import de.dfki.irl.darby.prediction.matching.bonmatching.BonMatcher;

public class Matching {
	public static TraceAccumulator acc=new TraceAccumulator();
	//public static StopPointFinder cf=new StopPointFinder();//new CloudFinderBender();//CloudFinderGrowCloud cf=new CloudFinderGrowCloud();
	
	public static void main(String[] args) {
		Database db=DBManager.getDatabase(Globals.DBTYPE);
		Long startTime;
		Long endTime;
		
		//TraceAccumulator acc=new TraceAccumulator();
		
		//startTime=Timestamp.valueOf(args1).getTime()/1000;
		//endTime=Timestamp.valueOf(args2).getTime()/1000;
		
		//LLShoppingMatcher matcher=new LLShoppingMatcher();
		//db.registerCallback(matcher);
		db.registerCallback(acc);
		db.getAllTraces();
		//db.registerCallback(cf);
		/*
		for (int year=2015;year<=2016;year++){
			for (int month=01;month<=12;month++){
				System.out.println("year: "+year+" month: "+month);		
				// Create a calendar object and set year and month
				Calendar mycal = new GregorianCalendar(year, month-1, 1);

				// Get the number of days in that month
				int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH); // 
	
				//System.out.println(Timestamp.valueOf(year+"-"+month+"-01 06:00:00"));
				//System.out.println(Timestamp.valueOf(year+"-"+month+"-01 06:00:00").getTime()/1000);
				
				//System.out.println(Timestamp.valueOf(year+"-"+month+"-"+daysInMonth+" 22:00:00"));
				//System.out.println(Timestamp.valueOf(year+"-"+month+"-"+daysInMonth+" 22:00:00").getTime()/1000);		
				
				startTime=Timestamp.valueOf(year+"-"+month+"-01 06:00:00").getTime()/1000;
				endTime=Timestamp.valueOf(year+"-"+month+"-"+daysInMonth+" 22:00:00").getTime()/1000;
				
				TraceAccumulator acc=new TraceAccumulator();
				
				db.registerCallback(acc);
				db.getAllTraces(startTime,endTime);
		

			}

		}*/
		

	}

	
}