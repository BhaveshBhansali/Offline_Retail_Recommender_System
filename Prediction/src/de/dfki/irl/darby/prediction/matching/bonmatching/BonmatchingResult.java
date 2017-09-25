package de.dfki.irl.darby.prediction.matching.bonmatching;

public class BonmatchingResult {
	
	//result contains the transactions for calculated workstation, as well as two workstations above and below
	//bontriplet stores transaction for a workstation on the calculated time, as well as two transactions before and afterwards
	private BonTriplet[] triplets=new BonTriplet[5];
}
