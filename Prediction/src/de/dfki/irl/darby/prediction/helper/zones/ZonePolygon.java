package de.dfki.irl.darby.prediction.helper.zones;

import java.util.ArrayList;

public class ZonePolygon {
/**
 * stellt eine verschmelzung mehrerer rechteckiger zones dar // 
Represents a merging of several rectangular zones
 */
	
	private ArrayList<Zone> zones=new ArrayList<Zone>();

public boolean add(Zone e) {
	return zones.add(e);
}

public boolean remove(Zone o) {
	return zones.remove(o);
}
public boolean isInside(double x, double y){
	for (Zone z:zones){
		if (z.isInside(x, y)) return true;
	}
	return false;
}
	
}
