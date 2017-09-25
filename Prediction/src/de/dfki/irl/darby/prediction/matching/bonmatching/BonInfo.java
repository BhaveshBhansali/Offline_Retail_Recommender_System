package de.dfki.irl.darby.prediction.matching.bonmatching;
import java.util.ArrayList;

public class BonInfo {

	/*
	 * bonId=transnumber timestamp=endtimestamp
	 */
	long traceId, bonID, timestamp, workstationID;
	String matchingType;
	ArrayList<Long> materials;
	ArrayList<Double> quantities;

	public long getTraceId() {
		return traceId;
	}

	public BonInfo(long traceId, long bonID, long timestamp,long workstationID, String matchingType, ArrayList<Long> materials,ArrayList<Double> quantities) {
		super();
		this.traceId = traceId;
		this.bonID = bonID;
		this.timestamp = timestamp;
		this.workstationID = workstationID;
		this.matchingType = matchingType;
		this.materials = materials;
		this.quantities = quantities;
	}

	public ArrayList<Double> getQuantities() {
		return quantities;
	}

	public void setQuantities(ArrayList<Double> quantities) {
		this.quantities = quantities;
	}

	/**
	 * warning: only cloning half of the data: traceId,bonID,timestamp and
	 * workstationID
	 * 
	 * @return
	 */
	@Override
	public BonInfo clone() {
		String newmatchingType = matchingType == null ? null : new String(
				matchingType);

		ArrayList<Long> newmaterials = new ArrayList<Long>();
		if (materials != null) {
			for (long material : materials) {
				newmaterials.add(material);
			}
		}
		ArrayList<Double> newquantities = new ArrayList<Double>();
		if (quantities != null) {
			for (double quant : quantities) {
				newquantities.add(quant);
			}
		}
		return new BonInfo(traceId, bonID, timestamp, workstationID,
				newmatchingType, newmaterials, newquantities);

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (bonID ^ (bonID >>> 32));
		result = prime * result + ((materials == null) ? 0 : materials.hashCode());
		result = prime * result
				+ ((matchingType == null) ? 0 : matchingType.hashCode());
		result = prime * result
				+ ((quantities == null) ? 0 : quantities.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		result = prime * result + (int) (traceId ^ (traceId >>> 32));
		result = prime * result
				+ (int) (workstationID ^ (workstationID >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BonInfo other = (BonInfo) obj;
		if (bonID != other.bonID)
			return false;
		if (materials == null) {
			if (other.materials != null)
				return false;
		} else if (!materials.equals(other.materials))
			return false;
		if (matchingType == null) {
			if (other.matchingType != null)
				return false;
		} else if (!matchingType.equals(other.matchingType))
			return false;
		if (quantities == null) {
			if (other.quantities != null)
				return false;
		} else if (!quantities.equals(other.quantities))
			return false;
		if (timestamp != other.timestamp)
			return false;
		if (traceId != other.traceId)
			return false;
		if (workstationID != other.workstationID)
			return false;
		return true;
	}

	public void setTraceId(long traceId) {
		this.traceId = traceId;
	}

	public long getBonID() {
		return bonID;
	}

	public void setBonID(long bonID) {
		this.bonID = bonID;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getWorkstationID() {
		return workstationID;
	}

	public void setWorkstationID(long workstationID) {
		this.workstationID = workstationID;
	}

	public String getMatchingType() {
		return matchingType;
	}

	public void setMatchingType(String matchingType) {
		this.matchingType = matchingType;
	}

	public ArrayList<Long> getMaterials() {
		return materials;
	}

	public void setMaterials(ArrayList<Long> materials) {
		this.materials = materials;
	}

}
