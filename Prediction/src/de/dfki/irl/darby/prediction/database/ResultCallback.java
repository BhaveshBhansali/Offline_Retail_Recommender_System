package de.dfki.irl.darby.prediction.database;

import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.helper.cloud.Cloud;
import de.dfki.irl.darby.prediction.products.Product;

public interface ResultCallback {
	public void receiveTrace(Trace trace);
	public void receiveCloud(Cloud cloud);
	public void receiveProduct(Product product);
	public void transactionFinished();
}
