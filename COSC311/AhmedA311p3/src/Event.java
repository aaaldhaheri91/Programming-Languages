/*
 * An event class to store event info
 */
public class Event {

	//data fields
	private int id;
	private int time;
	private String type;
	
	//constructor to initialize data members
	public Event(int id, int time, String type){
		this.id = id;
		this.time = time;
		this.type = type;
	}
	
	//setters
	public void setId(int id){
		this.id = id;
	}
	
	public void setTime(int time){
		this.time = time;
	}
	
	public void setType(String type){
		this.type = type;
	}
	
	//accessors
	public int getId(){
		return id;
	}
	
	public int getTime(){
		return time;
	}
	
	public String getType(){
		return type;
	}
	
	//returns a string representation of the event info
	@Override
	public String toString(){
		return "ID: " + id + ", Time: " + time + ", Type: " + type;
	}
	
}//end class
