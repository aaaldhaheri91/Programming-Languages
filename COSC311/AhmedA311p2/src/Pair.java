/**
 * A generic class that represents a pair of objects of the same type
 * 
 *
 */
public class Pair <T extends Comparable> implements Comparable{

	// instance variables
	private T ID;
	private T address;
	
	// constructors
	
	public Pair() {      
		ID = null;
		address = null;
	}
	
	public Pair (T ID, T address) {
		this.ID = ID;
		this.address = address;
	}
	
	// accessors and mutators 
	public void setData (T ID, T address) {
		this.ID = ID;
		this.address = address;
	}
	
	public void setID (T ID) {
		this.ID = ID;
	}
	
	public void setAddress (T address) {
		this.address = address;
	}
	
	public T getID() {
		return ID;
	}
	
	public T getAddress() {
		return address;
	}
	
	// return a string representation of a pair
	@Override
	public String toString() {
		return "(" + ID + " , " + address + ")";
	}
	
	// return true if two Pairs are the same, otherwise return false
	@Override
	public boolean equals(Object other){
		if (other == null)
			return false;
		else if (getClass() != other.getClass())
			return false;
		else {
			Pair<T> otherPair = (Pair<T>) other;
			return ID.equals(otherPair.ID) && address.equals(otherPair.address);
		}
			
	}
	
	// compare two Pair objects using their first elements. 
	@Override
	public int compareTo(Object other){
		return ID.compareTo(((Pair<T>)other).ID);
	}
}