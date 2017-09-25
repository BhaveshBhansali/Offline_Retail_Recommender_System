package de.dfki.irl.darby.prediction.matching;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.dfki.irl.darby.prediction.grid.Grid;
import de.dfki.irl.darby.prediction.json.shelf.Inventory;
import de.dfki.irl.darby.prediction.json.shelf.JSONFactory;
import de.dfki.irl.darby.prediction.json.shelf.ShelfMeter;
import de.dfki.irl.darby.prediction.products.Bon;
import de.dfki.irl.darby.prediction.products.Product;

public class ShoppingMatcher {
	private Bon bon;
	ArrayList<ShelfMeter> buyMeters=new ArrayList<ShelfMeter>();
	TreeMap<Double,Entry<Product,ShelfMeter>> matchingMap=new TreeMap<Double,Entry<Product,ShelfMeter>>();
	
	private int sameShelfMeter;
	private int sameShelfPart;
	private int sameShelf;
	
	
	public ShoppingMatcher(Bon bon, ArrayList<ShelfMeter> buyMeters) {
		super();
		this.bon = bon;
		this.buyMeters = buyMeters;
	}
	
	public void calculate(){
		calculateMatching();
	}

	private void calculateMatching() {
		
		Grid grid=Grid.getInstance();
		Inventory inv=JSONFactory.getInventory();
		
		ArrayList<Product> alreadyMatched=new ArrayList<Product>();
		
		TreeMap<Double,Entry<Product,ShelfMeter>> distanceMap=new TreeMap<Double,Entry<Product,ShelfMeter>>();
		
		
		for (Product p:bon.getBoughtProducts()){
			for (ShelfMeter thoughtMeter:buyMeters){
				ShelfMeter productsPos=inv.getMeter(p.getRegalNr(), p.getRegalpart(), p.getRegalMeter());
				
				Entry<Product,ShelfMeter> entry=new SimpleEntry<Product, ShelfMeter>(p,thoughtMeter);
				double distance=getRegalDistance(thoughtMeter, productsPos);
				distanceMap.put(distance, entry);
			}
		}
		
		for (Entry<Double,Entry<Product,ShelfMeter>> entry:distanceMap.entrySet()){
			if (!alreadyMatched.contains(entry.getValue().getKey())){
				alreadyMatched.add(entry.getValue().getKey());
				matchingMap.put(entry.getKey(),entry.getValue());
			}
		}
		
	}
	
	public int getSameShelfMeter() {
		return sameShelfMeter;
	}

	public int getSameShelfPart() {
		return sameShelfPart;
	}

	public int getSameShelf() {
		return sameShelf;
	}

	public double getAverageDistance(){
		double ret=0.0;
		
		for (Entry<Double,Entry<Product,ShelfMeter>> entry:matchingMap.entrySet()){
			ret +=entry.getKey();
		}
		
		ret /=matchingMap.size();
		
		return ret;
		
	}
	
	private void calculateMeasures(){
		
		Grid grid=Grid.getInstance();
		Inventory inv=JSONFactory.getInventory();
		
		for (Entry<Double,Entry<Product,ShelfMeter>> entry:matchingMap.entrySet()){
			ShelfMeter thoughtMeter=entry.getValue().getValue();
			Product p=entry.getValue().getKey();
			ShelfMeter actualMeter=inv.getMeter(p.getRegalNr(), p.getRegalpart(), p.getRegalMeter());
			
			if (thoughtMeter==actualMeter) this.sameShelfMeter++;
			if (thoughtMeter.getParent()==actualMeter.getParent()) this.sameShelfPart++;
			if (thoughtMeter.getParent().getParent()==actualMeter.getParent().getParent()) this.sameShelf++;
			
			//TODO: same department?
		}
	}
	
	private double getRegalDistance(ShelfMeter m1, ShelfMeter m2){
		return Math.sqrt(Math.pow(m1.getBounds().getX()-m2.getBounds().getX(),2)+Math.pow(m1.getBounds().getY()-m2.getBounds().getY(),2));
	}
	
}
