package de.dfki.irl.darby.prediction.json.shelf;

import java.awt.Graphics2D;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map.Entry;

import de.dfki.irl.darby.prediction.bender.Complex;
import de.dfki.irl.darby.prediction.bender.IPlottable;
import de.dfki.irl.darby.prediction.bender.Pair;
import de.dfki.irl.darby.prediction.bender.PlotWindow;

public class ShelfPart implements IPlottable
{
	public ArrayList<ShelfMeter> ShelfMeters;
	public boolean NameSichtbar;
	public int ID;
	private int PartID; 
	public String Name;
	private Shelf parent;
	private Double OrientationX,OrientationY;
	
	public Shelf getParent() {
		return parent;
	}
	public int getPartID() {
		return PartID;
	}
	public void setParent(Shelf parent) {
		this.parent = parent;
	}
	public Double getOrientationX() {
		return OrientationX;
	}
	public Double getOrientationY() {
		return OrientationY;
	}
	public boolean intersectsWith(Rectangle other) {
		for (ShelfMeter meter:ShelfMeters){
			if (meter.intersectsWith(other)) return true;
		}
		return false;
	}
	public String getName() {
		if (Name !=null||Name== "") return Name;
		return "(noname)";
	}
	public ArrayList<Entry<ShelfMeter,Rectangle>> getCoveredRegions() {
		ArrayList<Entry<ShelfMeter,Rectangle>> ret=new ArrayList<Entry<ShelfMeter,Rectangle>>();
		for (ShelfMeter meter:ShelfMeters){
			ret.add(new SimpleEntry(meter, meter.getBounds()));
		}
		return ret;
	}

	public Pair<ShelfMeter, Complex> GetClosestShelfMeterTo(Complex point)
	{
		Pair<ShelfMeter, Complex> pair = new Pair<ShelfMeter, Complex>();
		Complex candidateClosetPoint;
		double closestdistance = Double.MAX_VALUE;
		double candidateDistance;
		
		
		for (ShelfMeter shelfMeter : ShelfMeters)
		{
			candidateClosetPoint = shelfMeter.GetClosestPointTo(point);
			candidateDistance = Complex.Sub(candidateClosetPoint, point).SqrMagnitude();
			if (candidateDistance < closestdistance)
			{
				closestdistance = candidateDistance;
				pair.first = shelfMeter;
				pair.second = candidateClosetPoint;
			}
		}
		
		return pair;
	}
	
	@Override
	public void plot(Graphics2D g2, PlotWindow plotWindow) 
	{
		for (ShelfMeter shelfMeter : ShelfMeters)
		{
			shelfMeter.plot(g2, plotWindow);
		}
	}
	public ShelfMeter getMeter(int meterNo) {
		for (ShelfMeter meter: ShelfMeters){
			if (meter.RegalFach==meterNo) return meter;
		}
		
		return null;
	}
}
