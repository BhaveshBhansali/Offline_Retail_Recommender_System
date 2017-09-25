package de.dfki.irl.darby.prediction.products;

import java.util.ArrayList;

import de.dfki.irl.darby.prediction.grid.GridCell;
import de.dfki.irl.darby.prediction.json.shelf.JSONFactory;
import de.dfki.irl.darby.prediction.json.shelf.ShelfMeter;

public class Product {
	private int SAPMat;
	private String SAPWarengruppe;
	private String bezeichnung;
	private long ean;
	private int lieferantID;
	private String lieferant;
	private int RegalNr;
	private int Regalpart;
	private int regalMeter;
	
	public Product(int SAPMat, String SAPWarengruppe, String bezeichnung,
			long ean, int lieferantID, String lieferant, int regalplatz, int regal){
		super();
		this.SAPMat = SAPMat;
		this.SAPWarengruppe = SAPWarengruppe;
		this.bezeichnung = bezeichnung;
		this.ean = ean;
		this.lieferantID = lieferantID;
		this.lieferant = lieferant;
		
		if (Integer.toString(regalplatz).length()!=Integer.toString(regal).length()+4){
			//System.err.println("Could not parse regalplatz: " + regalplatz + " (shelf:" + regal + ")");
			return;
		}
		
		this.RegalNr=regal;
		this.Regalpart=(regalplatz/100)%100;
		this.regalMeter=regalplatz%100;
	}

	public ArrayList<GridCell> getBlockingCells(){
		ShelfMeter productMeter=JSONFactory.getInventory().getMeter(RegalNr, Regalpart, regalMeter);
		if (productMeter!=null) return productMeter.getBlockingCells();
		return null;
	}
	public int getSAPMat() {
		return SAPMat;
	}

	public String getSAPWarengruppe() {
		return SAPWarengruppe;
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public long getEan() {
		return ean;
	}

	public int getLieferantID() {
		return lieferantID;
	}

	public String getLieferant() {
		return lieferant;
	}

	public int getRegalNr() {
		return RegalNr;
	}

	public int getRegalpart() {
		return Regalpart;
	}

	public int getRegalMeter() {
		return regalMeter;
	}
	
	
	
}
