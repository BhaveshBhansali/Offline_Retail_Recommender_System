package de.dfki.irl.darby.prediction.products;

import java.util.ArrayList;

import de.dfki.irl.darby.prediction.json.shelf.ShelfMeter;

public class Bon {
	public ArrayList<Product> getBoughtProducts() {
		return boughtProducts;
	}

	ArrayList<Product> boughtProducts=new ArrayList<Product>();

	public boolean add(Product e) {
		return boughtProducts.add(e);
	}

	public boolean remove(Object o) {
		return boughtProducts.remove(o);
	}
}
