package nodebasis;

import coordination.Position;

public class Event {
	
	private final int id, time;
	private final Position position;
	private final Node node;
	
	public Event(int id, int time, Node node){
		this.id = id;
		this.time = time;
		this.position = node.getPosition();
		this.node = node;
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
	
	public Node getNode(){
		return node;
	}
	
	
}
