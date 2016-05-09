package nodebasis;

import coordination.Position;

public class Event {
	
	private int id, time;
	private Position position;
	
	public Event(int id, int time, Position position){
		this.id = id;
		this.time = time;
		this.position = position;
	}

	public int getId() {
		return id;
	}

	public int getTime() {
		return time;
	}

	public Position getPosition() {
		return position;
	}
	
	
}
