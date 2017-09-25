package de.dfki.irl.darby.prediction.json.shelf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;

public class JSONFactory {
	private static Inventory inv=null;
public static Inventory getInventory(){
	
	if (inv!=null) return inv;
	
	Gson gson = new Gson();
	 
	try {
 
		BufferedReader br = new BufferedReader(
			new FileReader("res/regale.json"));
 
		//convert the json string back to object
		Inventory obj = gson.fromJson(br, Inventory.class);
		
		inv=obj;
		
		for (Shelf shelf:inv.Shelfs){
			for (ShelfPart part:shelf.ShelfParts){
				part.setParent(shelf);
				for (ShelfMeter meter:part.ShelfMeters){
					meter.setParent(part);
				}
			}
		}
		return obj;
 
	} catch (IOException e) {
		e.printStackTrace();
		return null;
	}
}
}
