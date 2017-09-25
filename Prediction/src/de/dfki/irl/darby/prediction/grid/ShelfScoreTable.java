package de.dfki.irl.darby.prediction.grid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import de.dfki.irl.darby.prediction.json.shelf.ShelfMeter;

public class ShelfScoreTable {
	private HashMap<ShelfMeter,Double> scores=new HashMap<ShelfMeter,Double>();
	private HashMap<GridCell,Double> cellScores=new HashMap<GridCell,Double>();
	private HashSet<ShelfMeter> thisRound=new HashSet<ShelfMeter>();
	private HashMap<GridCell,Double> highPoints=new HashMap<GridCell,Double>();
	
	private int numberOfRounds=1;
	private double maxScore=0;
	private double maxCellScore=0;
	
	public void increaseScore(ShelfMeter meter, double score){
		//only store it, if shelf meter was not evaluated yet
		if (meter !=null && !usedThisRound(meter)){
			Double oldScore=scores.get(meter);
			if (oldScore==null) oldScore=0.0;
			
			oldScore+=score;
			if (oldScore>maxScore) maxScore=oldScore;
			scores.put(meter, oldScore);
			thisRound.add(meter);
			}
	}
	
	public HashMap<GridCell, Double> getHighPoints() {
		return highPoints;
	}

	public void increaseCellScore(GridCell cell, double score){
		//only store it, if shelf meter was not evaluated yet
		if (cell !=null){
			Double oldScore=cellScores.get(cell);
			if (oldScore==null) oldScore=0.0;
			
			oldScore+=score;
			if (oldScore>maxCellScore) maxCellScore=oldScore;
			cellScores.put(cell, oldScore);
			}
	}
	
	public double getMaxCellScore() {
		return maxCellScore;
	}

	public void resetRound(){
		thisRound.clear();
		numberOfRounds++;
	}
	
	public int getNumberOfRounds() {
		return numberOfRounds;
	}

	public double getMaxScore() {
		return maxScore;
	}

	private boolean usedThisRound(ShelfMeter meter){
		return thisRound.contains(meter);
	}
	
	public Double getScore(ShelfMeter meter){
		return scores.get(meter);
	}
	
	public Double getCellScore(GridCell cell){
		return cellScores.get(cell);
	}
	
	public void findHighPoints(){
		highPoints.clear();
		
		for (Entry<GridCell,Double> entry:cellScores.entrySet()){
			if ( higherThanNeighbours(entry.getKey(),entry.getValue())){
				highPoints.put(entry.getKey(), entry.getValue());
			}
		}
		
	}

	private boolean higherThanNeighbours(GridCell key, Double value) {
		for (GridCell cell: key.getAllNeighbors()){
			Double neighborScore = cellScores.get(cell);
			if (neighborScore==null) neighborScore=0.0;
			
			if (neighborScore>=value) return false;
		}
		return true;
	}
}
