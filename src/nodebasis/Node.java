package nodebasis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Map.Entry;

import surrounding.Field;
import coordination.Position;

public class Node {

	private PriorityQueue<Task> taskQueue;
	private HashMap<Integer, ImplicitEvent> routingMap;
	private List<Event> eventList;
	private List<Node> neighboursList;
	private Position position;
	private int timeOfMostRecentUpdate;
	private NodeState nodeState;
	private int signalStrength;
	private Field field;
	
	public Node(Field field, Position position, int signalStrength){
		Comparator<Task> taskComparator = new TaskComparator();
		
		this.field = field;
		this.position = position;
		this.signalStrength = signalStrength;
		
		nodeState = NodeState.READY;
		timeOfMostRecentUpdate = 0;
		
		taskQueue = new PriorityQueue<Task>(10, taskComparator);
		eventList = new ArrayList<Event>();
		neighboursList = new ArrayList<Node>();
		routingMap = new HashMap<Integer, ImplicitEvent>();
	}
	
	public void update(){
		Task currentTask;
		Message message;
		ArrayList<Task> failedTasks;
		boolean successfulTask = false;
		
		if(field.getRecentlyChangedNodeNetwork()){
			findNeighbours();
		}
		
		if(nodeState == NodeState.BUSY && !hasUpdatedThisTimeTick()){
			if(taskQueue.isEmpty()){
				setNodeState(NodeState.READY);
			}else{
				failedTasks = new ArrayList<Task>(taskQueue.size());
				while(!successfulTask && !taskQueue.isEmpty()){
					currentTask = taskQueue.peek();
					
					switch(currentTask.getAction()){
					case CREATE_AGENTMESSAGE:
						message = new AgentMessage(this, 0, 0); //fix placeholder
						//do more stuff
						break;
					case CREATE_REQUESTMESSAGE:
						message = new RequestMessage(0, 0, 0);//fix placeholder
						//do more stuff
						break;
					case HANDLE_AGENTMESSAGE:
						message = (AgentMessage)currentTask.getDataObject();
						if(currentTask.getHandleIntex() == 0){
							((AgentMessage)message).update(this);
							update((AgentMessage)message);
						}
						if(sendMessage((AgentMessage)message)){
							taskQueue.remove();
							successfulTask = true;
							returnToTaskQueue(failedTasks);
						}else{
							failedTasks.add(taskQueue.remove());
						}
						break;
					case HANDLE_REQUESTMESSAGE:
						message = (RequestMessage)currentTask.getDataObject();
						((RequestMessage)message).update(this);
						
						if(((RequestMessage)message).getReturnToSender()){
							if(sendMessage(((RequestMessage)message).getReturnAddress(),
									((RequestMessage)message))){
								taskQueue.remove();
								successfulTask = true;
								returnToTaskQueue(failedTasks);
							}else{
								failedTasks.add(taskQueue.remove());
							}
						}else{
							if(sendMessage((RequestMessage)message)){
								taskQueue.remove();
								successfulTask = true;
								returnToTaskQueue(failedTasks);
							}else{
								failedTasks.add(taskQueue.remove());
							}
						}
						break;
					}
				}
				
				if(successfulTask){
					setTimeOfMostRecentUpdate(field.getCurrentTime());
				}
			}
		}
	}
	
	private void update(AgentMessage agentMessage){
		HashMap<Integer, ImplicitEvent> agentRoutingMap = agentMessage.getRoutingMap();
		ImplicitEvent implicitEvent;
		int distance;
		
		for(Entry<Integer, ImplicitEvent> entry : agentRoutingMap.entrySet()){
			implicitEvent = routingMap.get(entry.getKey());
			distance = entry.getValue().getDistance();
			
			if(implicitEvent != null && implicitEvent.getDistance() > distance){
				routingMap.replace(entry.getKey(), entry.getValue());
			}else{
				routingMap.put(entry.getKey(), entry.getValue());
			}
		}
	}
	
	private void returnToTaskQueue(ArrayList<Task> taskList){
		for(Task task : taskList){
			taskQueue.add(task);
		}
	}
	
	private boolean sendMessage(RequestMessage message){
		for(Node node : neighboursList){
			if(sendMessage(node, message)){
				return true;
			}
		}
		return false;
	}
	
	private boolean sendMessage(AgentMessage message){
		for(Node node : neighboursList){
			if(sendMessage(node, message)){
				return true;
			}
		}
		return false;
	}
	
	private boolean sendMessage(Node node, RequestMessage message){
		if(node.getNodeState() == NodeState.READY &&
				!node.hasUpdatedThisTimeTick()){
			node.generateNewTask(message);
			return true;
		}
		return false;
	}
	
	private boolean sendMessage(Node node, AgentMessage message){
		if(node.getNodeState() == NodeState.READY &&
				!node.hasUpdatedThisTimeTick() && 
				!message.hasVisitedNode(node)){
			node.generateNewTask(message);
			return true;
		}
		return false;
	}
	
	public Event generateNewEvent(int id, int time){
		Event e = new Event(id, time, this);
		eventList.add(e);
		return e;
	}
	
	public void generateNewTask(Event event){
		taskQueue.add(new Task(event, TaskAction.CREATE_AGENTMESSAGE));
		setNodeState(NodeState.BUSY);
	}
	
	public void generateNewTask(Integer i){
		taskQueue.add(new Task(i, TaskAction.CREATE_REQUESTMESSAGE));
		setNodeState(NodeState.BUSY);
	}
	
	protected void generateNewTask(Message message){
		if(message.getClass() == AgentMessage.class){
			taskQueue.add(new Task(message, TaskAction.HANDLE_AGENTMESSAGE));
		}else if(message.getClass() == RequestMessage.class){
			taskQueue.add(new Task(message, TaskAction.HANDLE_REQUESTMESSAGE));
		}
		setNodeState(NodeState.BUSY);
		setTimeOfMostRecentUpdate(field.getCurrentTime());
	}
	
	protected NodeState getNodeState(){
		return nodeState;
	}
	
	protected HashMap<Integer, ImplicitEvent> getRoutingMap(){
		return routingMap;
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
		neighboursList = field.getNodesWithinRangeofNode(this);
	}
	
	private void setNodeState(NodeState nodeState){
		this.nodeState = nodeState;
	}
	
	public boolean hasUpdatedThisTimeTick(){
		return timeOfMostRecentUpdate == field.getCurrentTime();
	}
	
	private void setTimeOfMostRecentUpdate(int time){
		timeOfMostRecentUpdate = time;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((position == null) ? 0 : position.hashCode());
		result = prime * result + signalStrength;
		result = prime * result + timeOfMostRecentUpdate;
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
		Node other = (Node) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		if (signalStrength != other.signalStrength)
			return false;
		if (timeOfMostRecentUpdate != other.timeOfMostRecentUpdate)
			return false;
		return true;
	}
	
	
	
}
