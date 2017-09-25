package de.dfki.irl.darby.prediction.helper;

public class Tupel<T> {
	private T t1,t2;

	public T getT1() {
		return t1;
	}

	public void setT1(T t1) {
		this.t1 = t1;
	}

	public T getT2() {
		return t2;
	}

	public void setT2(T t2) {
		this.t2 = t2;
	}

	public Tupel(T t1, T t2) {
		super();
		this.t1 = t1;
		this.t2 = t2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((t1 == null) ? 0 : t1.hashCode());
		result = prime * result + ((t2 == null) ? 0 : t2.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tupel other = (Tupel) obj;
		if (t1 == null) {
			if (other.t1 != null)
				return false;
			//Achtung: permutierendes Tupel, d.h. zwei tupel sind auch dann equal, wenn das zweite element aus t1 dem ersten element aus t1 entspricht und umgekehrt
		} else if (!t1.equals(other.t1)){
			if (t1.equals(other.t2) && t2.equals(other.t1)){
				return true;
			}
			return false;
		}
			
		if (t2 == null) {
			if (other.t2 != null)
				return false;
		} else if (!t2.equals(other.t2))
			return false;
		
		
		return true;
	}

}
