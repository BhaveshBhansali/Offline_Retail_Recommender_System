package de.dfki.irl.darby.prediction.helper;

import de.dfki.irl.darby.prediction.database.DBManager;
import de.dfki.irl.darby.prediction.database.DBManager.DatabaseType;
import de.dfki.irl.darby.prediction.database.Database;

public class TraceModifier {
public static void main(String[] args){
	//Database db=DBManager.getDatabase(DatabaseType.MYSQL);
	Database db=DBManager.getDatabase(DatabaseType.POSTGRESQL);
	Trace orig=db.getTraceByTraceId(1);
	
	Trace t1=new Trace();
	t1.setId(-3);
	Trace t2=new Trace();
	t2.setId(-2);
	Trace t3=new Trace();
	t3.setId(-1);
	
	for (int i=0;i<orig.getPoints().size();i++){
		if (i<=234){
			t1.add(orig.getPoints().get(i));
		}
		if (i>234 && i<=524){
			t2.add(orig.getPoints().get(i));
		}
		if (i>524){
			t3.add(orig.getPoints().get(i));
		}
	}
	
	db.saveTrace(t1);
	db.saveTrace(t2);
	db.saveTrace(t3);
}
}
