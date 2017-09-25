package de.dfki.irl.darby.prediction.matching.bonmatching;

import java.util.ArrayList;

import de.dfki.irl.darby.prediction.accumulation.Globals;
import de.dfki.irl.darby.prediction.database.DBManager;
import de.dfki.irl.darby.prediction.database.Database;

public class TracesList {
	private ArrayList<TraceInfo> traceInfos=new ArrayList<TraceInfo>();
	
	public void loadFromDB(long startTime, long EndTime){
		Database db=DBManager.getDatabase(Globals.DBTYPE);
		traceInfos=db.getTraceInfosByPaytime(startTime, EndTime);
	}

	public ArrayList<TraceInfo> getTraceInfos() {
		return traceInfos;
	}
}
