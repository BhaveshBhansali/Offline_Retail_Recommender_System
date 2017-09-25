package de.dfki.irl.darby.prediction.json.shelf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import de.dfki.irl.darby.prediction.bender.Complex;
import de.dfki.irl.darby.prediction.bender.Complexi;
import de.dfki.irl.darby.prediction.bender.IPlottable;
import de.dfki.irl.darby.prediction.bender.PlotWindow;
import de.dfki.irl.darby.prediction.grid.GridCell;

public class ShelfMeter implements IPlottable
{
	public int RegalFach;
	public int ID;
	public ArrayList<ArrayList<ArrayList<Double>>> Paths;
	private Rectangle bounds=null;
	private Rectangle boundsNormalized =null;
	private ShelfPart parent;
	private ArrayList<GridCell> blockingCells=new ArrayList<GridCell>();
	
	public Rectangle getBounds(){
		
		if (bounds !=null) return new Rectangle(bounds);

		if (Paths.size()!=1 || Paths.get(0).size()==0 || Paths.get(0).get(0).size()!=2){
			System.err.println("JSON:ParseShelf:Invalid ShelfMeter polygon (s1: " + Paths.size() + " - s2:" + Paths.get(0).size() + " - s3:" + (Paths.get(0).get(0).size()));
			return null;
		}
		double minX=Double.MAX_VALUE;
		double minY=Double.MAX_VALUE;
		double maxX=Double.MIN_VALUE;
		double maxY=Double.MIN_VALUE;
		
		for (ArrayList<Double> pointList:Paths.get(0)){
			if (pointList.size()!=2){
				System.err.println("JSON:ParseShelf:invalid number of points");
				return null;
			}
			double x = pointList.get(0);
			double y = pointList.get(1);
			
			if (x<minX) minX=x;
			if (y<minY) minY=y;
			if (y>maxY) maxY=y;
			if (x>maxX) maxX=x;
			
		}
	
		bounds = new Rectangle(minX, minY, (maxX-minX), (maxY-minY));
		return bounds;
	}

	public ArrayList<GridCell> getBlockingCells() {
		return blockingCells;
	}

	public boolean addBlockingCell(GridCell e) {
		return blockingCells.add(e);
	}

	public ShelfPart getParent() {
		return parent;
	}

	public void setParent(ShelfPart parent) {
		this.parent = parent;
	}

	public Rectangle getBoundsNormalized(){
		
		if (boundsNormalized !=null) return boundsNormalized;

		if (Paths.size()!=1 || Paths.get(0).size()==0 || Paths.get(0).get(0).size()!=2){
			System.err.println("JSON:ParseShelf:Invalid ShelfMeter polygon (s1: " + Paths.size() + " - s2:" + Paths.get(0).size() + " - s3:" + (Paths.get(0).get(0).size()));
			return null;
		}
		double minX=Double.MAX_VALUE;
		double minY=Double.MAX_VALUE;
		double maxX=Double.MIN_VALUE;
		double maxY=Double.MIN_VALUE;
		
		for (ArrayList<Double> pointList:Paths.get(0)){
			if (pointList.size()!=2){
				System.err.println("JSON:ParseShelf:invalid number of points");
				return null;
			}
			Double x = pointList.get(0);
			Double y = pointList.get(1);
			
			if (x<minX) minX=x;
			if (y<minY) minY=y;
			if (y>maxY) maxY=y;
			if (x>maxX) maxX=x;
			
		}
		
		/*
		public static TracePoint FPToQuuppa(TracePoint point){
	         TracePoint ret=new TracePoint(point);
	         double x=ret.getX()- 177.37;
	         x*=0.09545378;
	         x+=15.96;
	         double y=ret.getY()- 47.25;
	         y*=-0.098661988;
	         y+=95.19;
	         ret.setX(x);
	         ret.setY(y);
	         return ret;
	     }
		*/
		boundsNormalized = new Rectangle((minX - 301.59)*0.17414 + 15.96, 
										 (minY - 245.35)*(-0.17858) + 95.19, 
										 (maxX-minX)*0.17414, (maxY-minY)*0.17858);
		return boundsNormalized;
	}

	public boolean intersectsWith(Rectangle other) {
		if ((getBounds().intersects(other))){
			return true;
		}
		else{
			return other.intersects(getBounds());
		}
		
	}

	public Complex GetClosestPointTo(Complex point)
	{
		Rectangle rectangle = this.getBoundsNormalized();
		return rectangle.GetClosestPointTo(point);
	}

	static Color colorInside = new Color(200, 200, 200);
	static Color colorBorder = new Color(0, 0, 0);
	@Override
	public void plot(Graphics2D g2, PlotWindow plotWindow) 
	{
		Rectangle rectangle = this.getBoundsNormalized();
		
		Complex A = new Complex(rectangle.getX(), rectangle.getY()),
				B = new Complex(rectangle.getX() + rectangle.getWidth(), rectangle.getY() + rectangle.getHeight());;
		
		A = plotWindow.MapWorldToScreen(A);
		B = plotWindow.MapWorldToScreen(B);
		
		Complexi Ai = new Complexi((int) A.x, (int) A.y),
				 Bi = new Complexi((int) B.x, (int) B.y);
		
		if (Ai.x > Bi.x)
		{
			int aux = Ai.x;
			Ai.x = Bi.x;
			Bi.x = aux;
		}
		
		if (Ai.y > Bi.y)
		{
			int aux = Ai.y;
			Ai.y = Bi.y;
			Bi.y = aux;
		}
		
		g2.setColor(colorInside);
		g2.fillRect(Ai.x, Ai.y, Bi.x - Ai.x + 1, Bi.y - Ai.y + 1);
		g2.setColor(colorBorder);
		g2.drawRect(Ai.x, Ai.y, Bi.x - Ai.x + 1, Bi.y - Ai.y + 1);
	}

	
}
