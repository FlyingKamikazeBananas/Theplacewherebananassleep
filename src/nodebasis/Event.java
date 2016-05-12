package nodebasis;

import coordination.Position;

public class Event {
	
	private int id, time;
	private Position position;
	private Node node;
	
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
