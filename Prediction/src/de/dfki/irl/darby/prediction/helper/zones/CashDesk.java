package de.dfki.irl.darby.prediction.helper.zones;

import de.dfki.irl.darby.prediction.helper.TracePoint;

public class CashDesk {
	private TracePoint position;
	private int deskNumber;
	public CashDesk(TracePoint position, int deskNumber) {
		super();
		this.position = position;
		this.deskNumber = deskNumber;
	}
	public TracePoint getPosition() {
		return position;
	}
	public int getDeskNumber() {
		return deskNumber;
	}
	@Override
	public String toString() {
		return "CashDesk [deskNumber=" + deskNumber + "]";
	}
}
