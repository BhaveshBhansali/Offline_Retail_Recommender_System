package de.dfki.irl.darby.prediction.bender;

public class Pair<T1, T2> 
{
	  public T1 first;
	  public T2 second;

	  public Pair() 
	  {
	    this.first = null;
	    this.second = null;
	  }
	  
	  public Pair(T1 first, T2 second) 
	  {
	    this.first = first;
	    this.second = second;
	  }
}
