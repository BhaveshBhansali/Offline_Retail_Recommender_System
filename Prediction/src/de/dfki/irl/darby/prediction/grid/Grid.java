package de.dfki.irl.darby.prediction.grid;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import de.dfki.irl.darby.prediction.accumulation.Globals;
import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.helper.TracePoint;
import de.dfki.irl.darby.prediction.helper.cloud.BareCloud;
import de.dfki.irl.darby.prediction.helper.cloud.CloudUtils;
import de.dfki.irl.darby.prediction.json.shelf.Inventory;
import de.dfki.irl.darby.prediction.json.shelf.JSONFactory;
import de.dfki.irl.darby.prediction.json.shelf.Rectangle;
import de.dfki.irl.darby.prediction.json.shelf.ShelfMeter;

public class Grid {
	GridCell[][] cells;
	ArrayList<GridCell> cellCollection=new ArrayList<GridCell>();
	
	static Grid instance=null; 
	
	public GridCell[][] getCells() {
		return cells;
	}

	Rectangle cellSize;
	private final Rectangle marketSize=new Rectangle(0d,0d,95d, 98d);
	public Grid(int resolutionX, int resolutionY){
		
		Inventory inventory=JSONFactory.getInventory();
		
		cellSize=new Rectangle(0,0,marketSize.getWidth()/resolutionX, marketSize.getHeight()/resolutionY);
		cells=new GridCell[(int) ((marketSize.getWidth()/cellSize.getWidth())+1)][(int) ((marketSize.getHeight()/cellSize.getHeight())+1)];
		
		//first run: create cell object & determine whether it's blocked
		for (int i=0;i<cells.length;i++){
			for (int j=0;j<cells[0].length;j++){
				GridCell cell=new GridCell(i*cellSize.getWidth(), j*cellSize.getHeight(), cellSize.getWidth(), cellSize.getHeight());
				
				ShelfMeter blocker=inventory.intersectsWith(cell.getDimensions().getFPMeasures());
				
				cell.setBlocked(blocker!=null);
				cell.setBlocker(blocker);
				if (blocker !=null) blocker.addBlockingCell(cell);

				//hack* achsen waren vertauscht :-/
				cells[j][i]=cell;	
				
				//Commented out as A* wont be used!
				//cellCollection.add(cell);
			}
		}
		
		//second run: set neighbours
		
		for (int i=0;i<cells.length;i++){
			for (int j=0;j<cells[0].length;j++){
				GridCell cell=cells[i][j];
				if (i>0) cell.addNeighbor(cells[i-1][j]);
				if (i<cells.length-1) cell.addNeighbor(cells[i+1][j]);
				if (j<cells[0].length-1) cell.addNeighbor(cells[i][j+1]);
				if (j>0) cell.addNeighbor(cells[i][j-1]);
			}
		}
		

	}
	public ShelfMeter getNearestShelfMeter(double x, double y){
		GridCell thisCell=getCell(x,y);
		
		LinkedList<Entry<GridCell,Integer>> cellStack=new LinkedList<Entry<GridCell,Integer>>();
		HashSet<GridCell> alreadyVisited=new HashSet<GridCell>();
		
		cellStack.push(new SimpleEntry<GridCell,Integer>(thisCell,0));
		
		while (!cellStack.isEmpty()){
			Entry<GridCell,Integer> current=cellStack.removeLast();
			if (alreadyVisited.contains(current.getKey())) continue;
			
			if (current.getValue()>Globals.maxProductDistance) {
				
				return null;
			}
			
			
			if (current.getKey().getBlocker()!=null) {
				
				return current.getKey().getBlocker();
			}
			
			alreadyVisited.add(current.getKey());
			
			for (GridCell cell: current.getKey().getAllNeighbors()){
				cellStack.addFirst(new SimpleEntry<GridCell,Integer>(cell,current.getValue()+1));
			}
		
		}
		return null;
		
	}
	
	public void resetTraces(){
		for (GridCell cell:cellCollection){
			cell.setTraceOn(false);
		}
	}
	public void addTrace(Trace trace){
		for (TracePoint point:trace.getPoints()){
			GridCell cell=getCell(point.getX(), point.getY());
			if (cell!=null) {
				cell.setTraceOn(true);
			}
			
		}
	}
	
	public double getDistanceToTrace(double x, double y){
		GridCell startCell=getCell(x,y);
		
		
		if (startCell==null) return Integer.MAX_VALUE;
		
		LinkedList<Entry<GridCell,Integer>> cellStack=new LinkedList<Entry<GridCell,Integer>>();
		HashSet<GridCell> alreadyVisited=new HashSet<GridCell>();
		
		cellStack.push(new SimpleEntry<GridCell,Integer>(startCell,0));
		
		while (!cellStack.isEmpty()){
			Entry<GridCell,Integer> current=cellStack.removeLast();
			if (alreadyVisited.contains(current.getKey())) continue;
			
			
			
			if (current.getKey().isTraceOn()) {

				return current.getValue();
			}
			
			alreadyVisited.add(current.getKey());
			
			for (GridCell cell: current.getKey().getAllNeighbors()){
				if (!alreadyVisited.contains(cell)) cellStack.addFirst(new SimpleEntry<GridCell,Integer>(cell,current.getValue()+1));
			}
		
		}
		return Integer.MAX_VALUE;
	}
	
	public double getDistanceToTrace(ArrayList<GridCell> cells){
		
		
		if (cells==null||cells.size()==0) return Integer.MAX_VALUE;
		
		LinkedList<Entry<GridCell,Integer>> cellStack=new LinkedList<Entry<GridCell,Integer>>();
		HashSet<GridCell> alreadyVisited=new HashSet<GridCell>();
		
		for (GridCell cell:cells){
			cellStack.push(new SimpleEntry<GridCell,Integer>(cell,0));
		}
		
		while (!cellStack.isEmpty()){
			Entry<GridCell,Integer> current=cellStack.removeLast();
			if (alreadyVisited.contains(current.getKey())) continue;
			
			
			
			if (current.getKey().isTraceOn()) {

				return current.getValue();
			}
			
			alreadyVisited.add(current.getKey());
			
			for (GridCell cell: current.getKey().getAllNeighbors()){
				if (!alreadyVisited.contains(cell)) cellStack.addFirst(new SimpleEntry<GridCell,Integer>(cell,current.getValue()+1));
			}
		
		}
		return Integer.MAX_VALUE;
	}
	public int getWalkingDistance(double x, double y, double x2,double y2){
		GridCell startCell=getCell(x,y);
		GridCell targetCell=getCell(x2,y2);
		
		if (startCell==null||targetCell==null) return Integer.MAX_VALUE;
		
		LinkedList<Entry<GridCell,Integer>> cellStack=new LinkedList<Entry<GridCell,Integer>>();
		HashSet<GridCell> alreadyVisited=new HashSet<GridCell>();
		
		cellStack.push(new SimpleEntry<GridCell,Integer>(startCell,0));
		
		while (!cellStack.isEmpty()){
			Entry<GridCell,Integer> current=cellStack.removeLast();
			if (alreadyVisited.contains(current.getKey())) continue;
			
			
			
			if (current.getKey()==targetCell) {

				return current.getValue();
			}
			
			alreadyVisited.add(current.getKey());
			
			for (GridCell cell: current.getKey().getAllNeighbors()){
				if (!alreadyVisited.contains(cell)) cellStack.addFirst(new SimpleEntry<GridCell,Integer>(cell,current.getValue()+1));
			}
		
		}
		return Integer.MAX_VALUE;
		
	}
	
	public Collection<ShelfMeter> getBuymeters(ArrayList<BareCloud> clouds){
		HashSet<ShelfMeter> ret=new HashSet<ShelfMeter>();
		
		for (BareCloud cloud : CloudUtils.filterCloudsByDistance(clouds, Globals.minDist)) {
			
			
			ShelfMeter meter=getNearestShelfMeter(cloud.getCenter().getX(), cloud.getCenter().getY());
			if (meter !=null){
				ret.add(meter);
			}
		}
		
		return ret;
	}
	
	public ShelfMeter getBuyMeter(BareCloud cloud){
		return getNearestShelfMeter(cloud.getCenter().getX(), cloud.getCenter().getY());
	}
	public GridCell getCell(double xPos, double yPos){
		
		int xi = (int) (xPos/cellSize.getWidth());
		int yi = (int) (yPos/cellSize.getHeight());
		
		if (xi>=cells.length||yi>cells[0].length) return null;
		
		return cells[yi][xi];
	}
	public ArrayList<GridCell> getCellCollection() {
		return cellCollection;
	}

	@Override
	public String toString(){
		String ret="";
		for (int i=cells.length-1;i>=0;i--){ //koordinatensystem beginnt LU-> umdrehen
			for (int j=0;j<cells[0].length;j++){
				GridCell cell=cells[i][j];
				if (cell.isBlocked()){
					ret +="X"+cell.getBlocker().ID + "*";
				}
				else{
					ret +="_";
				}
			}
			ret +="\n";
		}
		return ret;
	}
	
	public static Grid getInstance(){
		//TODO: deserialization
		if (instance==null){
			instance=new Grid(300,300);
		}
		return instance; 
	}
}
