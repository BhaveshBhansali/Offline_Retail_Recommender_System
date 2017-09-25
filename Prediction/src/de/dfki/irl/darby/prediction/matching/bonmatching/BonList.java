package de.dfki.irl.darby.prediction.matching.bonmatching;

import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Timestamp;

import de.dfki.irl.darby.prediction.accumulation.Globals;
import de.dfki.irl.darby.prediction.database.DBManager;
import de.dfki.irl.darby.prediction.database.Database;
import de.dfki.irl.darby.prediction.helper.SAPTimestamp;

public class BonList {

	private HashMap<Long, ArrayList<BonInfo>> bons = new HashMap<Long, ArrayList<BonInfo>>(); //map workstation-> bons

	private final HashMap<Long, Integer> indices = new HashMap<Long, Integer>();

	public void loadFromDB(Long startTime, Long EndTime) {
		Database db = DBManager.getDatabase(Globals.DBTYPE);
		bons = db.getRawBons(startTime, EndTime);  // get workstation_id and boninfo in HashMap
		for (Long l : bons.keySet()) {
			indices.put(l, 0);
		}
	
	
	}

	public void resetIndices() {
		for (Long l : indices.keySet()) {
			indices.put(l, 0);
		}
	}

	public HashMap<Long, ArrayList<BonInfo>> getBons() {
		return bons;
	}

	/**
	 * shifts right until we are over the given time epoch with all workstations
	 * 
	 * @param epoch
	 */
	public boolean shiftRTime(long epoch) {
		for (Long workstation : indices.keySet()) {

			while (workstationEpoch(workstation) <= epoch) {
				incIndex(workstation);
				if (indices.get(workstation) >= bons.get(workstation).size()) {
					// we're at the end
					decIndex(workstation);
					break;
				}
			}
			// System.out.println("Shift ws " + workstation + " -> "
			// + workstationEpoch(workstation));
		}
		return true;
	}

	/**
	 * returns BonInfos in the past (negative shift) or future (positive shift)
	 * 
	 * @param shift
	 * @return
	 */
	public BonInfo getOtherInfo(long workstation, int shift) {
		return bons.get(workstation).get(indices.get(workstation) + shift)
				.clone();
	}

	public boolean workstationExists(long workstationID) {
		return indices.containsKey(workstationID);
	}

	private void incIndex(long workstation) {
		indices.put(workstation, indices.get(workstation) + 1);
	}

	private void decIndex(long workstation) {
		indices.put(workstation, indices.get(workstation) - 1);
	}

	public long workstationEpoch(long workstation) {
		int workstationIndex = indices.get(workstation);
		//SAPTimestamp stamp = SAPTimestamp.fromLong(bons.get(workstation).get(workstationIndex).timestamp);
		//return stamp.toEpoch();
		
		return (bons.get(workstation).get(workstationIndex).timestamp);
	}

	public boolean timeExists(long cashDesk, int index) {
		if (!indices.containsKey(cashDesk))
			return false;

		index += indices.get(cashDesk);
		return index >= 0 && index < bons.get(cashDesk).size();
	}
}
