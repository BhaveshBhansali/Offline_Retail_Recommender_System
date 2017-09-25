package de.dfki.irl.darby.prediction.helper.cloud;

import java.util.ArrayList;

public class CloudUtils {
	public static ArrayList<BareCloud> filterCloudsByDistance(ArrayList<BareCloud> clouds, double minDist){
		ArrayList<BareCloud> ret=new ArrayList<BareCloud>();
		
		for (BareCloud cloud:clouds){
			if (!hasConflict(ret,cloud, minDist)) {
				ret.add(cloud);
			}
		}
		
		return ret;
	}

	private static boolean hasConflict(ArrayList<BareCloud> ret,
			BareCloud cloud2, double dist) {
		for (BareCloud cloud:ret){
			if (cloud.getCenter().distanceTo(cloud2.getCenter())<dist) return true;
		}
		
		return false;
	}
}
