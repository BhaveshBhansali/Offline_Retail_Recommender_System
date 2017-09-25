package de.dfki.irl.darby.prediction.grid;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import de.dfki.irl.darby.prediction.helper.Trace;
import de.dfki.irl.darby.prediction.helper.TracePoint;
import de.dfki.irl.darby.prediction.helper.cloud.BareCloud;

public class GridScorer {
/**
 * berechnet die scores (wahrscheinlichkeiten) 
 * für das Grid für einen gegebenen punkt und akkumuliert sie in der ShelfScoreTable
 */
	
	private ShelfScoreTable scoreTable=new ShelfScoreTable();
	private Grid grid;
	private HashSet<GridCell> visitedCells=new HashSet<GridCell>();
	private LinkedList<Entry<GridCell,Double>> todoStack=new LinkedList<Entry<GridCell,Double>>();
	private final double distancehandicap=0.01;
	private final double initialPossibility=0.8;
	
	public GridScorer(Grid grid) {
		super();
		this.grid = grid;
	}
	public ShelfScoreTable getScoreTable() {
		return scoreTable;
	}
	public void evaluateTrace (Trace trace){
		for (TracePoint point:trace.getInterpolatedTrace().getPoints()){
		//TracePoint point=trace.getInterpolatedTrace().getPoints().get(49);
		GridCell cell = grid.getCell(point.getX(),point.getY());//point.getX(), point.getY());
			if (cell!=null)evaluatePosition(cell);
		}
	}
	public void evaluateClouds(ArrayList<BareCloud> clouds){
		for (BareCloud cloud:clouds){
			TracePoint point=cloud.getCenter();
			GridCell cell = grid.getCell(point.getX(),point.getY());
			if (cell!=null)evaluatePosition(cell);
		}
		
	}
	public void evaluatePosition(GridCell cell){
		todoStack.clear();
		todoStack.push(new SimpleEntry(cell, initialPossibility));
		while(!todoStack.isEmpty()){
			Entry<GridCell,Double> now=todoStack.pop();
			evaluatePosition(now.getKey(),now.getValue());
		}
		
		scoreTable.resetRound();
		visitedCells.clear();
	}
	public void evaluatePosition(GridCell cell, double possibility){
		//System.out.println("cells in cache:" + visitedCells.size() + " poss:" + possibility);
		
		//we were already here or have no score left-> return
		if (visitedCells.contains(cell)||possibility<=0) {
			return;
		}
		else{
			visitedCells.add(cell);
		}
		
		scoreTable.increaseCellScore(cell, possibility);
		
		//its a wall-> dont look at neighbours
		if (cell.isBlocked()){
			//note: the function only increases if we had no increase action in this round yet
			scoreTable.increaseScore(cell.getBlocker(), possibility);
		}
		else{
			
			for (GridCell childCell:cell.getAllNeighbors()){
				todoStack.addLast(new SimpleEntry(childCell,possibility-distancehandicap));
			}
		}
		
	}
	
}
