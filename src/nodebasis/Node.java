package nodebasis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Map.Entry;

import surrounding.Field;
import coordination.Position;

//left to implement:
//counter to keep track on time elapsed after
//generated and send a request message

public class Node {

	private final int agentLife;
	private final int requestLife;
	
	private PriorityQueue<Task> taskQueue;
	private HashMap<Integer, ImplicitEvent> routingMap;
	private List<Event> eventList;
	private List<Node> neighboursList;
	private Position position;
	private int timeOfMostRecentUpdate;
	private NodeState nodeState;
	private int signalStrength;
	private Field field;
	
	public Node(Field field, Position position, int signalStrength,
			int agentLife, int requestLife){
		Comparator<Task> taskComparator = new TaskComparator();
		
		this.field = field;
		this.position = position;
		this.signalStrength = signalStrength;
		this.agentLife = agentLife;
		this.requestLife = requestLife;
		
		nodeState = NodeState.READY;
		timeOfMostRecentUpdate = 0;
		
		taskQueue = new PriorityQueue<Task>(10, taskComparator);
		eventList = new ArrayList<Event>();
		neighboursList = new ArrayList<Node>();
		routingMap = new HashMap<Integer, ImplicitEvent>();
	}
	
	/**
	 * 
	 */
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
						message = new AgentMessage(this, agentLife);
						taskQueue.remove();
						if(sendMessage((AgentMessage)message)){
							successfulTask = true;
						}else{
							failedTasks.add(new Task((AgentMessage)message,
									TaskAction.HANDLE_AGENTMESSAGE));
						}
						break;
					case CREATE_REQUESTMESSAGE:
						message = new RequestMessage((Integer)currentTask.getDataObject(),
								requestLife);
						taskQueue.remove();
						if(sendMessage((RequestMessage)message)){
							successfulTask = true;
						}else{
							failedTasks.add(new Task((RequestMessage)message,
									TaskAction.HANDLE_REQUESTMESSAGE));
						}
						break;
					case HANDLE_AGENTMESSAGE:
						message = (AgentMessage)currentTask.getDataObject();
						
						//if hasn't yet updated the message
						//i.e, first time taken out of taskQueue.
						if(currentTask.getHandleIntex() == 0){
							((AgentMessage)message).update(this);
							update((AgentMessage)message);
						}
						
						//attempt to send message or add the task to a list
						if(sendMessage((AgentMessage)message)){
							taskQueue.remove();
							successfulTask = true;
						}else{
							failedTasks.add(taskQueue.remove());
						}
						break;
					case HANDLE_REQUESTMESSAGE:
						message = (RequestMessage)currentTask.getDataObject();
						((RequestMessage)message).update(this);
						((RequestMessage)message).decrementMessageLife();
						
						if(((RequestMessage)message).getIsReturned()){
							
							//the event is returned
							//was it on time?
							//check and print
							//((RequestMessage)message).getEvent();
							
						}else if(((RequestMessage)message).getCurrentMessageLife() <= 0){
							//message is past its life time
							taskQueue.remove();
						
						//attempt to return to the sender (if needed),
						//or add task to list if failed.
						}else if(((RequestMessage)message).getReturnToSender()){
							
							if(sendMessage(((RequestMessage)message).getReturnAddress(),
									((RequestMessage)message))){
								((RequestMessage)message).resetCurrentMessageLife();
								taskQueue.remove();
								successfulTask = true;
							}else{
								failedTasks.add(taskQueue.remove());
							}
						//send message or add the task to a list
						}else{
							if(sendMessage((RequestMessage)message)){
								taskQueue.remove();
								successfulTask = true;
							}else{
								failedTasks.add(taskQueue.remove());
							}
						}
						break;
					}
				}
				
				returnToTaskQueue(failedTasks);
				
				if(successfulTask){
					setTimeOfMostRecentUpdate(field.getCurrentTime());
				}
			}
		}
	}
	
	/*
	 * Checks if the message possesses any valuable information,
	 * i.e. if the message knows a shorter path to an event, and if
	 * the message knows of an event which this node doesn't.
	 * */
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
		Node legitNode;
		
		//first check routingMap for path.
		if(routingMap.containsKey(message.getAddressedTo())){
			legitNode = routingMap.get(message.getAddressedTo()).getNode();
			if(sendMessage(legitNode, message)){
				message.resetCurrentMessageLife();
				return true;
			}else{
				return false;
			}
		}
		
		//if didn't find in routingMap, then try random.
		for(Node node : neighboursList){
			if(sendMessage(node, message)){
				return true;
			}
			
		}
		return false;
	}
	
	private boolean sendMessage(AgentMessage message){
		ArrayList<Node> failedNodes = new ArrayList<Node>();
		boolean visitedAll = true;
		
		//first check any of the adjacent nodes
		//which hasn't yet been visited by the message.
		for(Node node : neighboursList){
			if(!message.hasVisitedNode(node)){
				visitedAll = false;
				if(sendMessage(node, message)){
					return true;
				}
			}else{
				failedNodes.add(node);
			}
		}
		
		//if ALL of the adjacent nodes already have been
		//visited, then try to send back to one of them.
		if(visitedAll){
			for(Node node : failedNodes){
				if(sendMessage(node, message)){
					return true;
				}
			}
		}
		
		//the life of a message isn't all sweet 'u know.
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
				!node.hasUpdatedThisTimeTick()){
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
		Iterator<Task> iterator;
		Task currentTask;
		boolean foundAlike = false;
		
		if(message.getClass() == AgentMessage.class){
			taskQueue.add(new Task(message, TaskAction.HANDLE_AGENTMESSAGE));
			setNodeState(NodeState.BUSY);
			setTimeOfMostRecentUpdate(field.getCurrentTime());
		}else if(message.getClass() == RequestMessage.class){
			
			iterator = taskQueue.iterator();
			
			//manage if message of same type already exist;
			//look at equals method in RequestMessage
			while(iterator.hasNext()){
				currentTask = (Task)iterator.next();
				if(((RequestMessage)message).equals(currentTask.getDataObject())){
					
					//if newly received message is on its way back and the currently
					//held isn't, then replace.
					if(((RequestMessage)message).getReturnToSender() && 
								!((RequestMessage)currentTask.getDataObject()).getReturnToSender()){
						iterator.remove();
						taskQueue.add(new Task(message, TaskAction.HANDLE_REQUESTMESSAGE));
						setNodeState(NodeState.BUSY);
						setTimeOfMostRecentUpdate(field.getCurrentTime());
						
					//if both are not, or if both are on their way back, then compare 
					//current lives. the one with most lives is the one who gets to stay.
					}else if((!((RequestMessage)message).getReturnToSender() && 
							!((RequestMessage)currentTask.getDataObject()).getReturnToSender()) ||
							(((RequestMessage)message).getReturnToSender() && 
							((RequestMessage)currentTask.getDataObject()).getReturnToSender())){
						if(((RequestMessage)currentTask.getDataObject()).getCurrentMessageLife() <
								((RequestMessage)message).getCurrentMessageLife()){
							iterator.remove();
							taskQueue.add(new Task(message, TaskAction.HANDLE_REQUESTMESSAGE));
							setNodeState(NodeState.BUSY);
							setTimeOfMostRecentUpdate(field.getCurrentTime());
						}
					}
					foundAlike = true;
					break;
				}
			}
			
			if(!foundAlike){
				taskQueue.add(new Task(message, TaskAction.HANDLE_REQUESTMESSAGE));
				setNodeState(NodeState.BUSY);
				setTimeOfMostRecentUpdate(field.getCurrentTime());
			}
		}
		
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
	
	protected boolean knowPathToEventById(Integer id){
		return routingMap.containsKey(id);
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
