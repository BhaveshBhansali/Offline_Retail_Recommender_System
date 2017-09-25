package de.dfki.irl.darby.prediction.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.JPanel;

import de.dfki.irl.darby.prediction.accumulation.Globals;
import de.dfki.irl.darby.prediction.grid.Grid;
import de.dfki.irl.darby.prediction.grid.GridCell;
import de.dfki.irl.darby.prediction.grid.ShelfScoreTable;
import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.helper.TracePoint;
import de.dfki.irl.darby.prediction.helper.cloud.BareCloud;
import de.dfki.irl.darby.prediction.helper.cloud.CloudUtils;
import de.dfki.irl.darby.prediction.helper.zones.CashDesk;
import de.dfki.irl.darby.prediction.helper.zones.CashDeskFinder;
import de.dfki.irl.darby.prediction.json.shelf.ShelfMeter;
import de.dfki.irl.darby.prediction.products.Product;

public class TraceVisualisation extends JPanel {

	private static final int fontSize = 8;
	private Trace trace;
	private final int ovalDiameter = 5;
	private final int specialOvalDiameter = 10;
	private ArrayList<BareCloud> clouds = new ArrayList<BareCloud>();
	private ArrayList<Product> products=new ArrayList<Product>();
	
	//kram fuer abdeckungsvisualisierung
	private VisualisationMode visuMode=VisualisationMode.OneTrace;
	public enum VisualisationMode{OneTrace,AllTraces}
	private ArrayList<TracePoint> tracePoints=new ArrayList<TracePoint>();

	@Override
	public void paint(Graphics arg0) {
		// TODO Auto-generated method stub
		super.paint(arg0);

		drawKasten(arg0);

		if (Globals.guiShowInventory) {
			drawInventory(arg0);
		}
		
		if (visuMode==VisualisationMode.AllTraces){
			drawAllTraces(arg0);
			return;
		}
		
		if (trace == null || trace.getPoints() == null)
			return;
		
		trace.setClouds(clouds);
		
		if (Globals.guiShowGridPossibilities)
			drawCellPossibilities(arg0);
		if (Globals.guiShowShelfPossibilities)
			drawPossibilities(arg0);

		drawCashDesks(arg0);
		if (Globals.guiShowInterpolated) {
			drawTrace(arg0, trace.getInterpolatedTrace(), 100);
		}

		if (Globals.guiShowTrace)
			drawTrace(arg0, trace, 0);

		if (Globals.guiShowProducts){
			drawProducts(arg0);
		}
		if (Globals.guiShowHighpoints)
			drawHighpoints(arg0);
		if (Globals.guiShowClouds)
			drawClouds(arg0);
		
		/*if (Globals.guiShowPurchases){
			drawPurchases(arg0);
		}*/
	}

	public ArrayList<TracePoint> getTracePoints() {
		return tracePoints;
	}

	public void setTracePoints(ArrayList<TracePoint> tracePoints) {
		this.tracePoints = tracePoints;
	}

	private void drawAllTraces(Graphics arg0) {
		for (TracePoint point:tracePoints){
			drawPoint(arg0, point, Color.black, "", 2);
		}
		
	}

	public void drawProducts(Graphics arg0){
		
		ArrayList<GridCell> productCells=new ArrayList<GridCell>();
		
		for (Product prod: products){
			ArrayList<GridCell> blockingCells = prod.getBlockingCells();
			if (blockingCells!=null) productCells.addAll(blockingCells);
		}
		
		System.out.println("got " + productCells.size() + " product cells");
		for (GridCell cell : productCells) {
			drawRect(
					arg0,
					cell.getDimensions(),
					Color.BLUE);
		}
	}
	public ArrayList<Product> getProducts() {
		return products;
	}

	public void setProducts(ArrayList<Product> products) {
		this.products = products;
	}

	private void drawPurchases(Graphics arg0) {
		for (BareCloud cloud : CloudUtils.filterCloudsByDistance(clouds, Globals.minDist)) {
			Grid grid=Grid.getInstance();
			
			ShelfMeter meter=grid.getNearestShelfMeter(cloud.getCenter().getX(), cloud.getCenter().getY());
			if (meter !=null){
				arg0.setColor(Color.ORANGE);
				
				//recalculate it to quuppa: first need to create the two bounding points, then convert, then create point/size measures again
				de.dfki.irl.darby.prediction.json.shelf.Rectangle bounds = meter.getBounds();
				de.dfki.irl.darby.prediction.json.shelf.Rectangle twopoints=new de.dfki.irl.darby.prediction.json.shelf.Rectangle(bounds.getX(),bounds.getY(),bounds.getX()+bounds.getWidth(),bounds.getY()+bounds.getHeight());
				twopoints=twopoints.getQuuppaMeasures();
				bounds.setX(twopoints.getX());
				bounds.setY(twopoints.getY()+(twopoints.getHeight()-twopoints.getY()));
				bounds.setWidth(twopoints.getWidth()-twopoints.getX());
				bounds.setHeight(-(twopoints.getHeight()-twopoints.getY()));
				
				drawRect(arg0, bounds, Color.orange);
			}
		}
		
	}
	
	

	private void drawHighpoints(Graphics arg0) {
		ShelfScoreTable scores = trace.getScores();
		scores.findHighPoints();
		for (GridCell cell : scores.getHighPoints().keySet()) {
			drawRect(
					arg0,
					cell.getDimensions(),
					new Color((float) (scores.getCellScore(cell) / scores
							.getMaxCellScore()), 0f, 0f));
		}

	}

	private void drawPossibilities(Graphics arg0) {

		ShelfScoreTable scores = trace.getScores();

		for (GridCell[] cella : Grid.getInstance().getCells()) {
			for (GridCell cell : cella) {
				if (cell.isBlocked()) {
					Double score = scores.getScore(cell.getBlocker());
					if (score == null)
						score = 0.0;

					double color = score / scores.getMaxScore();
					drawRect(arg0, cell.getDimensions(), new Color(0f,
							(float) color, 0f));
				}
			}
		}

	}

	private void drawCellPossibilities(Graphics arg0) {

		ShelfScoreTable scores = trace.getScores();

		for (GridCell[] cella : Grid.getInstance().getCells()) {
			for (GridCell cell : cella) {
				// if (!cell.isBlocked()){
				Double score = scores.getCellScore(cell);
				if (score == null)
					score = 0.0;

				double color = score / scores.getMaxCellScore();

				drawRect(arg0, cell.getDimensions(), new Color((float) color,
						0f, (float) (1 - color)));
				// }
			}
		}

	}

	private void drawTrace(Graphics arg0, Trace trace, int blueCol) {
		TracePoint lastPoint = null;

		// color gradient stuff
		int numOfPoints = trace.getPoints().size();
		double colorAddStep = 255.0 / numOfPoints;
		int i = 0;

		for (TracePoint p : trace.getPoints()) {
			if (lastPoint != null) {
				lineTo(arg0, lastPoint, p, new Color(
						(int) (255 - (i * colorAddStep)),
						(int) (i * colorAddStep), blueCol), i);
			} else {
				// startpunkt!
				drawSpecialPoint(arg0, p, Color.blue, "start");

			}
			lastPoint = p;
			i++;
		}

		// endpunkt zeichnen
		drawSpecialPoint(arg0, lastPoint, Color.red, "end");
	}

	private void drawInventory(Graphics arg0) {
		Collection<ShelfMeter> buyMeters=new HashSet<ShelfMeter>();
		
		
		if (Globals.guiShowPurchases){
			String statusText="--------Most likely bought products----------\n";
			for (BareCloud cloud:CloudUtils.filterCloudsByDistance(clouds, Globals.minDist)){
				ShelfMeter buyMeter=Grid.getInstance().getBuyMeter(cloud);
				if (buyMeter==null){
					statusText +="	C"+cloud.getCloudId()+": No Shelf nearby\n";// for pos. (" + cloud.getCenter().getX() + ";" + cloud.getCenter().getY() + ")\n"; 
				}
				else{
					statusText +="	C"+ cloud.getCloudId() +": Shelf '" + buyMeter.getParent().getName() + "' , ShelfNo. " + buyMeter.getParent().getParent().RegalNr +"-"+buyMeter.getParent().getPartID() + "-" + buyMeter.RegalFach + ", stop time="+ (int)cloud.getScore() +"s \n";
				}
			}
			statusText +="----------------------------";
			StatusConsole.getInstance().setText(statusText);

		}
		for (GridCell[] cella : Grid.getInstance().getCells()) {
			for (GridCell cell : cella) {
				if (cell.isBlocked()) {
					Color color=Color.lightGray;
					if (buyMeters.contains(cell.getBlocker())){
						color=Color.orange;
					}
					drawRect(arg0, cell.getDimensions(), color);
				}
			}
		}

	}

	private void setStatusText(Collection<ShelfMeter> buyMeters) {
		String text="----Most likely bought products----------\n";
		
		for (ShelfMeter meter:buyMeters){
			text +="	Shelf '" + meter.getParent().getName() + "' , ShelfNo. " + meter.getParent().getParent().RegalNr +"-"+meter.getParent().getPartID() + "-" + meter.RegalFach + "\n";  
		}
		text +="----------------------------";
		StatusConsole.getInstance().setText(text);
		
	}

	private void drawRect(Graphics arg0,
			de.dfki.irl.darby.prediction.json.shelf.Rectangle dimensions,
			Color lightgray) {
		TracePoint ol = new TracePoint(dimensions.getX(), dimensions.getY(), 1);
		TracePoint ur = new TracePoint(dimensions.getX()
				+ dimensions.getWidth(), dimensions.getY()
				+ dimensions.getHeight(), 1);

		ol = transformToGUI(ol);
		ur = transformToGUI(ur);

		arg0.setColor(lightgray);
		double width = ur.getX() - ol.getX();
		double height = ur.getY() - ol.getY();
		arg0.fillRect((int) ol.getX(), (int) (ol.getY() + height), (int) width,
				(int) -height);
		// System.out.println("fill:"+(int)ol.getX()+";" + (int)ol.getY()+ ";"
		 //+(int)width + ";" + (int)height);
	}

	private void drawClouds(Graphics arg0) {
		for (BareCloud cloud : CloudUtils.filterCloudsByDistance(clouds, Globals.minDist)) {
			
			drawSpecialPoint(arg0, cloud.getCenter(), Color.black,
					"C" + cloud.getCloudId());

			TracePoint upperLeft = new TracePoint(cloud.getCenter());
			upperLeft.shiftEven(-cloud.getDiameter() / 2);
			TracePoint lowerRight = new TracePoint(cloud.getCenter());
			lowerRight.shiftEven(cloud.getDiameter() / 2);

			upperLeft = transformToGUI(upperLeft);
			lowerRight = transformToGUI(lowerRight);
			arg0.setColor(Color.black);
			arg0.drawOval((int) upperLeft.getX(), (int) lowerRight.getY(),
					(int) (lowerRight.getX() - upperLeft.getX()),
					(int) (upperLeft.getY() - lowerRight.getY()));
		}
	}

	private void drawCashDesks(Graphics g) {
		CashDeskFinder finder = new CashDeskFinder();
		for (CashDesk desk : finder.getCashDesks()) {
			drawSpecialPoint(g, desk.getPosition(), Color.MAGENTA, "Desk "
					+ desk.getDeskNumber());
		}

	}

	public ArrayList<BareCloud> getClouds() {
		return clouds;
	}

	public void setClouds(ArrayList<BareCloud> clouds) {
		this.clouds.clear();
		this.clouds = clouds;
	}

	private TracePoint transformToGUI(TracePoint in) {
		Rectangle bounds = this.getBounds();
		double y = 100 - in.getY();
		double scaleX = bounds.getWidth() / 110;
		double scaleY = bounds.getHeight() / 100;

		int drawX1 = (int) (in.getX() * scaleX);
		int drawY1 = (int) (y * scaleY);

		TracePoint point = new TracePoint(drawX1, drawY1,
				in.getPositionTimeStamp());
		point.setId(in.getId());
		return point;
	}

	
	private void drawSpecialPoint(Graphics g, TracePoint p, Color color,
			String caption) {
		drawPoint(g, p, color, caption, specialOvalDiameter);

	}

	private void drawPoint(Graphics g, TracePoint p, Color color, String caption, int diameter) {
		if (p == null)
			return;
		Rectangle bounds = this.getBounds();
		double y = 100 - p.getY();
		double scaleX = bounds.getWidth() / 110;
		double scaleY = bounds.getHeight() / 100;

		int drawX1 = (int) (p.getX() * scaleX);
		int drawY1 = (int) (y * scaleY);

		g.setColor(color);
		g.fillOval(drawX1 - diameter / 2, drawY1
				- diameter / 2, diameter,
				diameter);

		// Caption
		Graphics2D g2d = (Graphics2D) g;
		Font font = new Font("Serif", Font.PLAIN, this.fontSize);
		g2d.setFont(font);
		// g2d.setColor(Color.black);
		g2d.drawString(caption + "", drawX1 + ovalDiameter, drawY1
				+ ovalDiameter + this.fontSize);
		g.setColor(Color.black);
	}

	public VisualisationMode getVisuMode() {
		return visuMode;
	}

	public void setVisuMode(VisualisationMode visuMode) {
		this.visuMode = visuMode;
	}

	private void drawKasten(Graphics g) {
		// TODO Auto-generated method stub
		Rectangle bounds = this.getBounds();
		bounds.height -= 2;
		bounds.width -= 2;
		g.drawLine(0, 0, bounds.width, 0);
		g.drawLine(bounds.width, bounds.height, bounds.width, 0);
		g.drawLine(bounds.width, bounds.height, 0, bounds.height);
		g.drawLine(0, 0, 0, bounds.height);
	}

	private void lineTo(Graphics g, TracePoint p1, TracePoint p2, Color color,
			int caption) {

		g.setColor(color);
		Rectangle bounds = this.getBounds();
		double y1 = 100 - p1.getY();
		double y2 = 100 - p2.getY();
		
		if (Globals.useUHopperHack){
			y1=(40000 - p1.getY())/Globals.uHopperScale;
			y2=(40000 - p2.getY())/Globals.uHopperScale;
		}
		
		double scaleX = bounds.getWidth() / 110;
		double scaleY = bounds.getHeight() / 100;

		double x1 = p1.getX();
		double x2 = p2.getX();
		
		if (Globals.useUHopperHack){
			x1/=Globals.uHopperScale;
			x2/=Globals.uHopperScale;
		}
		
		int drawX1 = (int) (x1 * scaleX);
		int drawY1 = (int) (y1 * scaleY);
		
		int drawX2 = (int) (x2 * scaleX);
		int drawY2 = (int) (y2 * scaleY);
		g.fillOval(drawX1 - ovalDiameter / 2, drawY1 - ovalDiameter / 2,
				ovalDiameter, ovalDiameter);

		// numbering of points
		Graphics2D g2d = (Graphics2D) g;
		Font font = new Font("Serif", Font.PLAIN, this.fontSize);
		g2d.setFont(font);
		// g2d.setColor(Color.black);
		g2d.drawString(caption + "", drawX1 + ovalDiameter, drawY1
				+ ovalDiameter + this.fontSize);
		// g2d.setColor(color);
		g.fillOval(drawX2 - ovalDiameter / 2, drawY2 - ovalDiameter / 2,
				ovalDiameter, ovalDiameter);
		g.drawLine(drawX1, drawY1, drawX2, drawY2);
		
		System.out.println("line: " + drawX1  +";" + drawY1 + " - " + drawX2 + ";"+ drawY2);
	}

	public Trace getTrace() {
		return trace;
	}

	public void setTrace(Trace trace) {
		this.trace = trace;
		repaint();
	}

	public TraceVisualisation(Trace trace) {
		super();
		this.trace = trace;
	}

	public void resetClouds() {
		clouds.clear();
	}

	

}
