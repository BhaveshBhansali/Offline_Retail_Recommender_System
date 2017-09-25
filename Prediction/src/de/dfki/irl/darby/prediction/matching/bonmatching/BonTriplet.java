package de.dfki.irl.darby.prediction.matching.bonmatching;

/**
 * Wil not be used, replaced by hashmap in bonlist
 */
@Deprecated
public class BonTriplet {
	private static final int tripletSize=5;
	private long workstationID;
	private long[] transnumbers=new long[tripletSize];
	private long[] timestamps=new long[tripletSize];
	
	/**
	 * timeline
	 *              0    1    2    3     4    
	 *        past <----------------------> present
	 */
	public BonTriplet clone(){
		BonTriplet newTriplet=new BonTriplet();
		newTriplet.workstationID=workstationID;
		
		for (int i=0;i<tripletSize;i++ ){
			newTriplet.transnumbers[i]=transnumbers[i];
			newTriplet.timestamps[i]=timestamps[i];
		}
		return newTriplet;
	}
	
	private void shiftL(){
		for (int i=1;i<tripletSize;i++ ){
			transnumbers[i]=transnumbers[i-1];
			timestamps[i]=timestamps[i-1];
		}
	}
	
	public void addNewRecord(long transnumber, long timestamp){
		shiftL();
		transnumbers[tripletSize-1]=transnumber;
		timestamps[tripletSize-1]=timestamp;
	}
	
}
