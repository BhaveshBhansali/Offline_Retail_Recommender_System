package de.dfki.irl.darby.prediction.json.test;

import junit.framework.Assert;

import org.junit.Test;

import de.dfki.irl.darby.prediction.grid.Grid;
import de.dfki.irl.darby.prediction.helper.SAPTimestamp;
import de.dfki.irl.darby.prediction.helper.TracePoint;
import de.dfki.irl.darby.prediction.helper.Util;
import de.dfki.irl.darby.prediction.json.shelf.Inventory;
import de.dfki.irl.darby.prediction.json.shelf.JSONFactory;

public class TestJSON {

	@Test
	public void test() {
		Inventory inventory = JSONFactory.getInventory();
		Assert.assertNotNull(inventory);
		//System.out.println(inventory);
	}
	
	@Test
	public void testGrid(){
		Grid grid=new Grid(300, 300);
		System.out.println(grid);
	}
	/*@Test
	public void testToQuuppaUmrechnung(){
		TracePoint fp=new TracePoint(177.37, 47.25, 1);
		TracePoint fp2=new TracePoint(894.89, 965.03, 1);
		
		TracePoint qp=Util.FPToQuuppa(fp);
		TracePoint qp2=Util.FPToQuuppa(fp2);
		
		Assert.assertEquals(qp.getX(),15.96,0.001);
		Assert.assertEquals(qp.getY(),95.19,0.001);
		
		Assert.assertEquals(qp2.getX(),84.45,0.001);
		Assert.assertEquals(qp2.getY(),4.64,0.001);
	}
	
	@Test
	public void testToFPUmrechnung(){
		TracePoint fp=new TracePoint(15.96,95.19,1);//177.37, 47.25, 1);
		TracePoint fp2=new TracePoint(84.45,4.64,1);//894.89, 965.03, 1);
		
		TracePoint qp=Util.QuuppaToFP(fp);
		TracePoint qp2=Util.QuuppaToFP(fp2);
		
		Assert.assertEquals(177.37,qp.getX(),0.001);
		Assert.assertEquals(47.25,qp.getY(),0.001);
		
		Assert.assertEquals(894.89,qp2.getX(),0.001);
		Assert.assertEquals(965.03,qp2.getY(),0.001);
	}*/
	
	@Test
	public void testToQuuppaUmrechnung(){
		TracePoint fp=new TracePoint(301.59, 245.35, 1);
		TracePoint fp2=new TracePoint(694.88, 752.4, 1);
		
		TracePoint qp=Util.FPToQuuppa(fp);
		TracePoint qp2=Util.FPToQuuppa(fp2);
		
		Assert.assertEquals(qp.getX(),15.96,0.01);
		Assert.assertEquals(qp.getY(),95.19,0.01);
		
		Assert.assertEquals(qp2.getX(),84.45,0.01);
		Assert.assertEquals(qp2.getY(),4.64,0.01);
	}
	
	@Test
	public void testToFPUmrechnung(){
		TracePoint fp=new TracePoint(15.96,95.19,1);//177.37, 47.25, 1);
		TracePoint fp2=new TracePoint(84.45,4.64,1);//894.89, 965.03, 1);
		
		TracePoint qp=Util.QuuppaToFP(fp);
		TracePoint qp2=Util.QuuppaToFP(fp2);
		
		Assert.assertEquals(301.59,qp.getX(),0.001);
		Assert.assertEquals(245.35,qp.getY(),0.001);
		
		Assert.assertEquals(694.88,qp2.getX(),0.001);
		Assert.assertEquals(752.4,qp2.getY(),0.001);
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testSAPTimestamp(){
		SAPTimestamp stamp=SAPTimestamp.fromLong(20141029170918l);
		Assert.assertEquals(1414598958l,stamp.toEpoch()/1000 );
		stamp.setSeconds(7);
		Assert.assertEquals(1414598947l,stamp.toEpoch()/1000 );
	}
}
