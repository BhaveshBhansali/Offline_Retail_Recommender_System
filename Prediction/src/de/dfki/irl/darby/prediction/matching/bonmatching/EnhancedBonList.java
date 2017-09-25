package de.dfki.irl.darby.prediction.matching.bonmatching;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class EnhancedBonList {
	HashMap<Long, ArrayList<BonInfo>> bons = new HashMap<Long, ArrayList<BonInfo>>(); //map traceID-> bons
	
	public Set<Long> getTraceIDs(){
		return bons.keySet();
	}
	public void addBon(long traceId, BonInfo bon){
		ArrayList<BonInfo> thisbons=this.bons.get(traceId);
		
		if (thisbons==null){
			thisbons=new ArrayList<BonInfo>();
			this.bons.put(traceId,thisbons);
		}
		thisbons.add(bon);
	}
	
	public ArrayList<BonInfo> getIdealBonList(Long traceID){
		ArrayList<BonInfo> ret=new ArrayList<BonInfo>();
		ret.addAll(getIdealTimeline(traceID,".0"));
		ret.addAll(getIdealTimeline(traceID,"U1"));
		ret.addAll(getIdealTimeline(traceID,"L1"));
		ret.addAll(getIdealTimeline(traceID,"U2"));
		ret.addAll(getIdealTimeline(traceID,"L2"));
		
		return ret;
	}

	private Collection<? extends BonInfo> getIdealTimeline(long traceID,String place) {
		ArrayList<BonInfo> ret=new ArrayList<BonInfo>();
		
		BonInfo matchingtypeBon = getMatchintypeBon(traceID, place + ".0");
		if (matchingtypeBon!=null) ret.add(matchingtypeBon);
		
		BonInfo matchingtypeBona1 = getMatchintypeBon(traceID, place + "A1");
		if (matchingtypeBona1!=null) ret.add(matchingtypeBona1);
		
		BonInfo matchingtypeBonb1 = getMatchintypeBon(traceID, place + "B1");
		if (matchingtypeBonb1!=null) ret.add(matchingtypeBonb1);
		
		BonInfo matchingtypeBona2 = getMatchintypeBon(traceID, place + "A2");
		if (matchingtypeBona2!=null) ret.add(matchingtypeBona2);
		
		BonInfo matchingtypeBonb2 = getMatchintypeBon(traceID, place + "B2");
		if (matchingtypeBonb2!=null) ret.add(matchingtypeBonb2);

		return ret;
	}
	
	private BonInfo getMatchintypeBon(long traceID, String matchingType){
		for (BonInfo info:bons.get(traceID)){
			if (info.getMatchingType().equals(matchingType)) return info;
		}
		return null;
	}
	
}
