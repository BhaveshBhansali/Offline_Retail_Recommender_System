package de.dfki.irl.darby.prediction.grid;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import com.stackframe.pathfinder.Node;

import de.dfki.irl.darby.prediction.json.shelf.Rectangle;
import de.dfki.irl.darby.prediction.json.shelf.ShelfMeter;



public class GridCell implements Node<GridCell>{
		Rectangle dimensions;
	    private boolean blocked;
	    private boolean traceOn=false;
	    private ShelfMeter blocker;
	    public ShelfMeter getBlocker() {
			return blocker;
		}




		public boolean isTraceOn() {
			return traceOn;
		}




		public void setTraceOn(boolean traceOn) {
			this.traceOn = traceOn;
		}




		public void setBlocker(ShelfMeter blocker) {
			this.blocker = blocker;
		}

		private transient List<GridCell> neighbors;

	    public GridCell(double x, double y,double width,double height) {
	        dimensions=new Rectangle(x, y, width, height);
	        neighbors = new ArrayList<GridCell>();
	    }

	  


		public Rectangle getDimensions() {
			return dimensions;
		}




		public void setBlocked(boolean blocked) {
	        this.blocked = blocked;
	    }

	    public boolean isBlocked() {
	        return blocked;
	    }

	    public Double getX() {
			return dimensions.getX();
		}




		public Double getY() {
			return dimensions.getY();
		}




		public Double getWidth() {
			return dimensions.getWidth();
		}




		public Double getHeight() {
			return dimensions.getHeight();
		}




		public double getDistance(GridCell dest) {
	        double a = dest.getX() - getX();
	        double b = dest.getY() - getY();
	        return Math.sqrt(a * a + b * b);
	    }

	    public double pathCostEstimate(GridCell goal) {
	        return getDistance(goal) * 0.99;
	    }

	    public double traverseCost(GridCell target) {
	        double distance = getDistance(target);
	        double diff = target.getHeight() - getHeight();
	        return Math.abs(diff) + distance;
	    }

	    public Iterable<GridCell> neighbors() {
	        List<GridCell> realNeighbors = new ArrayList<GridCell>();
	        if (!blocked) {
	            for (GridCell loc : neighbors) {
	                if (!loc.blocked) {
	                    realNeighbors.add(loc);
	                }
	            }
	        }

	        return realNeighbors;
	    }

	    public List<GridCell> getAllNeighbors() {
			return neighbors;
		}




		public void addNeighbor(GridCell l) {
	        neighbors.add(l);
	    }

	    @Override
	    public String toString() {
	        return "{x=" + getX() + ",y=" + getY() + "}";
	    }

	    private synchronized void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
	        s.defaultReadObject();
	        neighbors = new ArrayList<GridCell>();
	    }
}
