package nodebasis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import surrounding.Field;
import coordination.Position;

public class Node {

	private PriorityQueue<Task> taskQueue;
	private RoutingTable routingTable;
	private List<Event> eventList;
	private List<Node> neighbours;
	private Position position;
	private boolean recentlyUpdated;
	private NodeState nodeState;
	private int signalStrength;
	private Field field;
	
	public Node(Field field, Position position, int signalStrength){
		Comparator<Task> taskComparator = new TaskComparator();
		
		this.field = field;
		this.position = position;
		this.signalStrength = signalStrength;
		
		nodeState = NodeState.READY;
		recentlyUpdated = false;
		
		taskQueue = new PriorityQueue<Task>(10, taskComparator);
		eventList = new ArrayList<Event>();
		neighbours = new ArrayList<Node>();
		routingTable = new RoutingTable();
	}
	
	public void update(){
		if(nodeState == NodeState.BUSY && !recentlyUpdated){
			//do stuff
		}
	}
	
	public Event generateNewEvent(int id, int time){
		Event e = new Event(id, time, this.position);
		eventList.add(e);
		return e;
	}
	
	public void generateNewTask(Event event){
		taskQueue.add(new Task(event, TaskAction.CREATE_AGENTMESSAGE));
	}
	
	public void generateNewTask(Integer i){
		taskQueue.add(new Task(i, TaskAction.CREATE_REQUESTMESSAGE));
	}
	
	protected void generateNewTask(Message message){
		if(message.getClass() == AgentMessage.class){
			taskQueue.add(new Task(message, TaskAction.HANDLE_AGENTMESSAGE));
		}else{
			taskQueue.add(new Task(message, TaskAction.HANDLE_REQUESTMESSAGE));
		}
	}
	
	protected NodeState getNodeState(){
		return nodeState;
	}
	
	protected RoutingTable getRoutingTable(){
		return routingTable;
	}
	
	protected Event getEventById(int id) throws IllegalArgumentException{
		for(Event e : eventList){
			if(e.getId() == id){
				return e;
			}
		}
		throw new IllegalArgumentException("event with id not found");
	}
	
	public int getSignalStrength(){
		return signalStrength;
	}
	
	public Position getPosition(){
		return position;
	}
	
	private void findNeighbours(){
		
	}
	
	
}
