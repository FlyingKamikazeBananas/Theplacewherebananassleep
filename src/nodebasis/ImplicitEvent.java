package nodebasis;

public class ImplicitEvent{

	private final int id;
	private int distance;
	private Node node;
	
	public ImplicitEvent(Event event){
		this(event.getId(), 0, event.getNode());
	}
	
	public ImplicitEvent(int id, int distance, Node node){
		this.id = id;
		this.distance = distance;
		this.node = node;
	}
	
	public int getId(){
		return id;
	}
	
	public void setDistance(int distance){
		this.distance = distance;
	}
	
	public int getDistance(){
		return distance;
	}
	
	public void setNode(Node node){
		this.node = node;
	}
	
	public Node getNode(){
		return node;
	}
}
