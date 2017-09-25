package de.dfki.irl.darby.prediction.accumulation;

import de.dfki.irl.darby.prediction.database.DBManager.DatabaseType;

public class Globals {

	public static final int minCashierSerie = 2;
	// public static final DatabaseType DBTYPE = DatabaseType.MYSQL;
	public static final DatabaseType DBTYPE = DatabaseType.POSTGRESQL;
	public static final double interpolation_maxPositionDiff = 10;
	public static final double interpolation_maxTimeDiff = 20000;
	public static final double interpolationTimestep = 2000;
	public static boolean useUHopperHack=true;
	
	public static double maxCloudSpeed = 1000;// 0.1;
	public static double minDist = 10;

	public static boolean guiShowClouds = true;
	public static boolean guiShowInventory = true;
	
	public static boolean guiShowShelfPossibilities = false;
	public static boolean guiShowInterpolated = false;
	public static boolean guiShowTrace = true;
	public static boolean guiShowGridPossibilities = false;
	public static boolean guiShowHighpoints = false;
	public static boolean guiShowPurchases = true;
	public static boolean guiShowConsole = false;

	public enum EvaluationMethod {
		InterpolatedTrace, CloudsOnly
	}

	public static final EvaluationMethod evaluationMethod = EvaluationMethod.CloudsOnly;

	// Zum finden des Shelfmeters zu einem Stopppunkt: Wie weit darf der
	// shelfmeter max. weg sein
	public static final Integer maxProductDistance = 15;

	// bender constants

	public static final double speedTimeWindow = 8.51;
	public static final double positionTimeWindow = 20.24;
	public static double speedThreshold = useUHopperHack?50:0.38;// 0.08;//0.38; //m/s
	public static boolean guiShowProducts=true;
	public static final double minimumStopTime = 4;
	//public static final long matching_maxTimediff = 600000;
	public static final long matching_maxTimediff = 600;  //10 minutes
	public static final double maxBonMatchingDist = 20;
	public static final double uHopperScale = 400;
	

}
