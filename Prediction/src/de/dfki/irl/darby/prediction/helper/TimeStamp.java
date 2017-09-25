package de.dfki.irl.darby.prediction.helper;

import java.util.Date;
import java.sql.Timestamp;

public class TimeStamp {

	public Timestamp TimeStamp(String dateString){
		return Timestamp.valueOf(dateString);
		
	}
	
	
	
}
