package de.dfki.irl.darby.prediction.json.shelf;

import de.dfki.irl.darby.prediction.bender.Complex;
import de.dfki.irl.darby.prediction.helper.TracePoint;
import de.dfki.irl.darby.prediction.helper.Util;


public class Rectangle{
	private double x,y,width,height;
	
	private double scaleX,scaleY;

	public Rectangle(double x, double y, double width, double height) {
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rectangle(Rectangle other){
		x=other.x;
		y=other.y;
		width=other.width;
		height=other.height;
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public Rectangle getQuuppaMeasures(){
		TracePoint xy=new TracePoint(x, y, 1);
		TracePoint widthheight=new TracePoint(x+width, y+height, 1);

		
		xy=Util.FPToQuuppa(xy);
		widthheight=Util.FPToQuuppa(widthheight);

		widthheight.setX(widthheight.getX()-xy.getX());
		widthheight.setY(widthheight.getY()-xy.getY());
		
		return new Rectangle(xy.getX(), xy.getY(), widthheight.getX(), widthheight.getY());
	}
	public Rectangle getFPMeasures(){
		TracePoint xy=new TracePoint(x, y, 1);
		TracePoint widthheight=new TracePoint(x+width, y+height, 1);
		
		xy=Util.QuuppaToFP(xy);
		widthheight=Util.QuuppaToFP(widthheight);
		widthheight.setX(widthheight.getX()-xy.getX());
		widthheight.setY(widthheight.getY()-xy.getY());
		
		return new Rectangle(xy.getX(), xy.getY(), widthheight.getX(), widthheight.getY());
	}
	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Rectangle [x=" + x + ", y=" + y + ", width=" + width
				+ ", height=" + height + "]";
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}
	
	public boolean intersects(Rectangle other){
		if (isInbetween(x, other.getX(), other.getX()+other.getWidth())||isInbetween(x+getWidth(), other.getX(), other.getX()+other.getWidth())){
			if (isInbetween(y, other.getY(), other.getY()+other.getHeight())||isInbetween(y+getHeight(), other.getY(), other.getY()+other.getHeight())){
				return true;
			}
		}
		return false;
	}
	public boolean isInbetween(double number, double lBound, double uBound){
		return (number >=lBound && number <= uBound);
	}
	
	public Complex GetClosestPointTo(Complex point)
	{
		int xAreaCode = (point.x < x) ? -1 : ((point.x < x + width) ? 0 : 1), 
			yAreaCode = (point.y < y) ? ((point.y < y - height) ? -1 : 0) : 1;
		
		switch (xAreaCode)
		{
			case -1:
				switch (yAreaCode)
				{
					case -1: return new Complex(x, y - height);
					case  0: return new Complex(x, point.y);
					case  1: return new Complex(x, y);
				}
				
			case 0:
				switch (yAreaCode)
				{
					case -1: return new Complex(point.x, y - height);
					case  0: return new Complex(point);
					case  1: return new Complex(point.x, y);
				}
				
			case 1:
				switch (yAreaCode)
				{
					case -1: return new Complex(x + width, y - height);
					case  0: return new Complex(x + width, point.y);
					case  1: return new Complex(x + width, y);
				}
				
			default:
				return point;
		}
	}

	
}
