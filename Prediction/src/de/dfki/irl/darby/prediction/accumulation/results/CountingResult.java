package de.dfki.irl.darby.prediction.accumulation.results;

import java.util.HashMap;
import java.util.Map.Entry;

public class CountingResult<T> {
	private HashMap <T,Integer> counts=new HashMap<T,Integer>();
	
	public void addValue(T value){
		if (value !=null){
			Integer actualValue=counts.get(value);
			if (actualValue==null)actualValue=0;
			counts.put(value,actualValue+1);
		}
	}
	
	@Override
	public String toString(){
		String ret="Counting:\n";
		for (Entry<T,Integer> entry:counts.entrySet()){
			ret +="* " + entry.getKey() + " -> " + entry.getValue() + "\n"; 
		}
		
		return ret;
	}
}
