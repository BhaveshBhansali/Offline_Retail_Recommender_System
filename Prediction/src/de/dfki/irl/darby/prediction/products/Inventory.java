package de.dfki.irl.darby.prediction.products;

import java.util.HashMap;

import de.dfki.irl.darby.prediction.accumulation.Globals;
import de.dfki.irl.darby.prediction.database.DBManager;
import de.dfki.irl.darby.prediction.database.Database;
import de.dfki.irl.darby.prediction.database.ResultCallback;
import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.helper.cloud.Cloud;

public class Inventory implements ResultCallback{

	HashMap<Long,Product> products=new HashMap<Long,Product>(); //ean to product object
	
	public Inventory(){
		Database db=DBManager.getDatabase(Globals.DBTYPE);
		db.registerCallback(this);
		db.getAllProducts();
	}
	@Override
	public void receiveTrace(Trace trace) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveCloud(Cloud cloud) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveProduct(Product product) {
		long ean = product.getEan();
		products.put(ean, product);
		
	}

	public Product getProductByEan(long ean){
		return products.get(ean);
	}
	@Override
	public void transactionFinished() {
		// TODO Auto-generated method stub
		
	}

}
