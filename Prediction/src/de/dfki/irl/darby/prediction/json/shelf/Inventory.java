package de.dfki.irl.darby.prediction.json.shelf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import de.dfki.irl.darby.prediction.bender.IPlottable;
import de.dfki.irl.darby.prediction.bender.PlotWindow;
import de.dfki.irl.darby.prediction.helper.TracePoint;

public class Inventory implements IPlottable
{
	public ArrayList<Shelf> Shelfs;
	public double[] Resolution;
	
	private HashMap<ShelfMeter,Rectangle> coveredRegions=null;
	
	@Override
	public String toString() {
		return "Inventory [shelves=" + Shelfs + ", Resolution="
				+ Arrays.toString(Resolution) + "]";
	}
	
	public ShelfMeter getMeter(int shelfNo, int partNo, int meterNo){
		Shelf shelf=getShelf(shelfNo);
		
		if (shelf!=null){
			ShelfPart part=shelf.getPart(partNo);
			if (part!=null){
				return part.getMeter(meterNo);
			}
		}
		
		return null;
	}
	private Shelf getShelf(int shelfNo) {
		for (Shelf shelf:Shelfs){
			if (shelf.RegalNr==shelfNo) return shelf;
		}
		return null;
	}

	public ShelfMeter intersectsWith(Rectangle other){
		if (coveredRegions==null){
			coveredRegions=new HashMap<ShelfMeter,Rectangle>();
			if (Shelfs==null) return null;
			for (Shelf shelf: Shelfs){
				for (Entry<ShelfMeter,Rectangle> entry:shelf.getCoveredRegions()){
					coveredRegions.put(entry.getKey(),entry.getValue());
				}
				
			}
		}

		for (Entry<ShelfMeter,Rectangle> rect:coveredRegions.entrySet()){
			if ( other.intersects(rect.getValue())) return rect.getKey();
		}
		return null;
	}

	public TracePoint getMax(){
		if (coveredRegions==null){
			//just to build coveredRegions map
			intersectsWith(new Rectangle(1, 1, 1, 1));
		}
		
		double maxX=0,maxY=0;
		
		for (Rectangle rect:coveredRegions.values()){
			double thisX=rect.getX()+rect.getWidth();
			double thisY=rect.getY()+rect.getHeight();
			
			if (thisX>maxX && thisY>maxX){
				maxX=thisX;
				maxY=thisY;		
			}
			
		}
		return new TracePoint(maxX, maxY, 0);
		
	}
	Color colorLine = new Color(0, 0, 0); //new Color(63, 72, 204);
	@Override
	public void plot(Graphics2D g2, PlotWindow plotWindow) 
	{
		g2.setColor(colorLine);
		for (Shelf shelf : Shelfs)
		{
			shelf.plot(g2, plotWindow);
		}
	}
}
