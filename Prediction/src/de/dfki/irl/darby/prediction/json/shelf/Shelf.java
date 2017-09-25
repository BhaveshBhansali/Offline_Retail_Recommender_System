package de.dfki.irl.darby.prediction.json.shelf;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;

import de.dfki.irl.darby.prediction.bender.Complex;
import de.dfki.irl.darby.prediction.bender.IPlottable;
import de.dfki.irl.darby.prediction.bender.Pair;
import de.dfki.irl.darby.prediction.bender.PlotWindow;

public class Shelf implements IPlottable
{
	public ArrayList<ShelfPart> ShelfParts;
	public int RegalNr;
	public boolean NummerSichtbar;
	@Override
	public String toString() {
		return "Shelf [ShelfParts=" + ShelfParts + ", RegalNr=" + RegalNr
				+ ", NummerSichtbar=" + NummerSichtbar + ", ID=" + ID + "]";
	}
	public int ID;
	public boolean intersectsWith(Rectangle other) {
		for (ShelfPart part: ShelfParts){
			if (part.intersectsWith(other)) return true;
		}
		return false;
	}
	public ArrayList<Entry<ShelfMeter,Rectangle>> getCoveredRegions() {
		
		ArrayList<Entry<ShelfMeter,Rectangle>> ret=new ArrayList<Entry<ShelfMeter,Rectangle>>();
		
		for (ShelfPart part: ShelfParts){
			ret.addAll(part.getCoveredRegions());
		}
		return ret;
	}
	
	public Pair<ShelfMeter, Complex> GetClosestShelfMeterTo(Complex point)
	{
		Pair<ShelfMeter, Complex> pair = new Pair<ShelfMeter, Complex>();
		Pair<ShelfMeter, Complex> candidate;
		double closestdistance = Double.MAX_VALUE;
		double candidateDistance;
		
		
		for (ShelfPart shelfPart : ShelfParts)
		{
			candidate = shelfPart.GetClosestShelfMeterTo(point);
			candidateDistance = Complex.Sub(candidate.second, point).SqrMagnitude();
			if (candidateDistance < closestdistance)
			{
				closestdistance = candidateDistance;
				pair = candidate;
			}
		}
		
		return pair;
	}
	
	@Override
	public void plot(Graphics2D g2, PlotWindow plotWindow) 
	{
		for (ShelfPart shelfPart : ShelfParts)
		{
			shelfPart.plot(g2, plotWindow);
		}
	}
	public ShelfPart getPart(int partNo) {
		for (ShelfPart part: ShelfParts){
			if (part.getPartID()==partNo) return part;
		}
		
		return null;
	}
	
}
