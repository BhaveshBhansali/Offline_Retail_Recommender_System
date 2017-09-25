package de.dfki.irl.darby.prediction.helper;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Util {
	public static int compare(final Number x, final Number y) {
	    if(isSpecial(x) || isSpecial(y))
	        return Double.compare(x.doubleValue(), y.doubleValue());
	    else
	        return toBigDecimal(x).compareTo(toBigDecimal(y));
	}

	private static boolean isSpecial(final Number x) {
	    boolean specialDouble = x instanceof Double
	            && (Double.isNaN((Double) x) || Double.isInfinite((Double) x));
	    boolean specialFloat = x instanceof Float
	            && (Float.isNaN((Float) x) || Float.isInfinite((Float) x));
	    return specialDouble || specialFloat;
	}

	private static BigDecimal toBigDecimal(final Number number) {
	    if(number instanceof BigDecimal)
	        return (BigDecimal) number;
	    if(number instanceof BigInteger)
	        return new BigDecimal((BigInteger) number);
	    if(number instanceof Byte || number instanceof Short
	            || number instanceof Integer || number instanceof Long)
	        return new BigDecimal(number.longValue());
	    if(number instanceof Float || number instanceof Double)
	        return new BigDecimal(number.doubleValue());

	    try {
	        return new BigDecimal(number.toString());
	    } catch(final NumberFormatException e) {
	        throw new RuntimeException("The given number (\"" + number
	                + "\" of class " + number.getClass().getName()
	                + ") does not have a parsable string representation", e);
	    }
	}
	

	public static TracePoint FPToQuuppa(TracePoint point){
		TracePoint ret=new TracePoint(point);
		
		double x=ret.getX()- 301.59;
		x*=0.17414;
		x+=15.96;
		
		double y=ret.getY()- 245.35;
		y*=-0.17858;
		y+=95.19;
		
		ret.setX(x);
		ret.setY(y);
		return ret;
	}
	public static TracePoint QuuppaToFP(TracePoint point){
		TracePoint ret=new TracePoint(point);
		
		double x=ret.getX();
		x-=15.96;
		x*= 5.742298;
		x +=301.59;
		
		double y=ret.getY(); 
		y-=95.19;
		y*=-5.5996687;
		y +=245.35;
		
		ret.setX(x);
		ret.setY(y);
		return ret;
	}
	public static TracePoint FPToQuuppaold(TracePoint point){
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
	public static TracePoint QuuppaToFPold(TracePoint point){
		TracePoint ret=new TracePoint(point);
		
		double x=ret.getX();
		x/=0.09545378;
		x+= 177.37;
		x -=167.201340796;
		
		double y=ret.getY(); 
		y/=-0.098661988;
		y+= 47.25;
		y +=95.19/0.098661988;
		
		ret.setX(x);
		ret.setY(y);
		return ret;
	}
}
