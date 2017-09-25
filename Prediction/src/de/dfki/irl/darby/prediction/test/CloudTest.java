package de.dfki.irl.darby.prediction.test;


import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.dfki.irl.darby.prediction.helper.TracePoint;
import de.dfki.irl.darby.prediction.helper.Tupel;
import de.dfki.irl.darby.prediction.helper.cloud.CloudFinderGrowCloud;

public class CloudTest {

	private ArrayList<TracePoint> points;
	@Before
	public void setUp(){
		points=new ArrayList<TracePoint>();
		points.add(new TracePoint(1, 1, 0));
		points.add(new TracePoint(1, 1.5, 0));
		points.add(new TracePoint(1.5, 1, 0));
		points.add(new TracePoint(3, 1, 0));
		points.add(new TracePoint(3, 1.1, 0));
		points.add(new TracePoint(3.1, 1, 0));
		points.add(new TracePoint(3, 1, 0));
		points.add(new TracePoint(5, 1, 0));
		points.add(new TracePoint(1, 10, 0));
		points.add(new TracePoint(100, 1, 0));
		//two clouds:1-3 &  4-6
	}
	@Test
	public void testTupel() {
		Tupel t=new Tupel(points.get(0),points.get(1));
		Tupel t2=new Tupel(points.get(1),points.get(0));
		
		Assert.assertEquals(t, t2);
	}
	@Test
	public void testTupelHashMap() {
		Tupel t=new Tupel(points.get(0),points.get(1));
		Tupel t2=new Tupel(points.get(1),points.get(0));
		
		HashMap<Tupel<TracePoint>,Double> testMap=new HashMap<Tupel<TracePoint>,Double>();
		testMap.put(t, 66d);
		Assert.assertEquals(testMap.containsKey(t2),true);
		Assert.assertEquals((double)testMap.get(t2),(double)66d,0.0001d);
	}
	@Test
	public void testClustering(){
		CloudFinderGrowCloud finder=new CloudFinderGrowCloud();
		finder.addPoints(points);
		finder.findClouds(2, 2);
		System.out.println(finder);
	}
	
	

}
