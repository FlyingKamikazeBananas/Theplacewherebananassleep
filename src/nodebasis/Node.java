package nodebasis;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Map.Entry;

import surrounding.Field;
import coordination.Position;

//behöver snyggas upp

public class Node{
	
	private final int agentLife;
	private final int requestLife;
	
	private PriorityQueue<Task> taskQueue;
	private HashMap<Integer, ImplicitEvent> routingMap;
	private HashMap<Integer, Request> requestMap;
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
		requestMap = new HashMap<Integer, Request>();
	}
	
	/**
	 * 
	 */
	public void update(){
		Task currentTask;
		Request tempRequest;
		Message message;
		ArrayList<Task> failedTasks;
		Iterator<Map.Entry<Integer, Request>> iterator;
		boolean successfulTask = false;
		
		for(Map.Entry<Integer, Request> entry : requestMap.entrySet()){
			entry.getValue().decrementLifespan();
			//System.out.println(entry.getValue().getLife());
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
						//System.out.println("Skapa agent, time: " + field.getCurrentTime());
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
						//System.out.println("Skapa request, time: " + field.getCurrentTime());
						message = new RequestMessage((Integer)currentTask.getDataObject(),
								requestLife, field.getCurrentTime(), this);
						requestMap.put(((RequestMessage)message).getRequestId(),
								new Request(requestLife*8));
						taskQueue.remove();
						if(sendMessage((RequestMessage)message)){
							successfulTask = true;
						}else{
							failedTasks.add(new Task((RequestMessage)message,
									TaskAction.HANDLE_REQUESTMESSAGE));
						}
						break;
					case HANDLE_AGENTMESSAGE:
						//System.out.println(this.toString() + "; Hantera agent, time: " + field.getCurrentTime());
						message = (AgentMessage)currentTask.getDataObject();
						
						//if hasn't yet updated the message
						//i.e, first time taken out of taskQueue.
						if(currentTask.getHandleIntex() == 0){
							((AgentMessage)message).update(this);
							update((AgentMessage)message);
							((AgentMessage)message).decrementLifespan();
						}
						
						//attempt to send message or add the task to a list
						if(((AgentMessage)message).isDead()){
							taskQueue.remove();
						}else if(sendMessage((AgentMessage)message)){
							taskQueue.remove();
							successfulTask = true;
						}else{
							failedTasks.add(taskQueue.remove());
						}
						break;
					case HANDLE_REQUESTMESSAGE:
						//System.out.println("Hantera request, time: " + field.getCurrentTime());
						message = (RequestMessage)currentTask.getDataObject();
						
						if(currentTask.getHandleIntex() == 0){
							((RequestMessage)message).update(this);
							((RequestMessage)message).decrementLifespan();
						}
						
						//request message is returned
						if(((RequestMessage)message).getIsReturned()){
							taskQueue.remove();
							//if it returned in time
							//i.e. Node is still holding the request:
							
							/*for(Map.Entry<Integer, Request> entry : requestMap.entrySet()){
								System.out.println(entry.getKey() + " == " + ((RequestMessage)message).getRequestId());
							}*/
							
							if(requestMap.containsKey(((RequestMessage)message)
									.getRequestId())){
								printEvent(((RequestMessage)message).getEvent(),
										(RequestMessage)message);
								requestMap.remove(((RequestMessage)message)
									.getRequestId());
							}
						}else if(((RequestMessage)message).isDead()){
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
				
				iterator = requestMap.entrySet().iterator();
				while(iterator.hasNext()){
					tempRequest = iterator.next().getValue();
					if(tempRequest.isDead()){
						if(tempRequest.getNumberOfTimesRevived() == 0){
							tempRequest.reviveRequest();
						}else{
							iterator.remove();
							//System.out.println("DIED");
						}
					}
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
			task.incrementIndex();
			taskQueue.add(task);
		}
	}
	
	private boolean sendMessage(RequestMessage message){
		Node legitNode;
		boolean visitedAll = true;
		
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
		
		//first check any of the adjacent nodes
		//which hasn't yet been visited by the message.
		for(Node node : neighboursList){
			if(!message.hasVisitedNode(node)){
				visitedAll = false;
				if(sendMessage(node, message)){
					return true;
				}
			}
		}
		
		//if ALL of the adjacent nodes already have been
		//visited, then try to send back to one of them.
		if(visitedAll){
			for(Node node : neighboursList){
				if(sendMessage(node, message)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	private boolean sendMessage(AgentMessage message){
		boolean visitedAll = true;
		
		//first check any of the adjacent nodes
		//which hasn't yet been visited by the message.
		for(Node node : neighboursList){
			if(!message.hasVisitedNode(node)){
				visitedAll = false;
				if(sendMessage(node, message)){
					return true;
				}
			}
		}
		
		//if ALL of the adjacent nodes already have been
		//visited, then try to send back to one of them.
		if(visitedAll){
			for(Node node : neighboursList){
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
			message.resetCurrentMessageLife();
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
	
	private void generateNewTask(AgentMessage message){
		taskQueue.add(new Task(message, TaskAction.HANDLE_AGENTMESSAGE));
		setNodeState(NodeState.BUSY);
		setTimeOfMostRecentUpdate(field.getCurrentTime());
	}
	
	private void generateNewTask(RequestMessage message){
		Iterator<Task> iterator;
		Task currentTask;
		boolean foundAlike = false;
		iterator = taskQueue.iterator();
		
		//manage if message of same type already exist;
		//look at equals method in RequestMessage
		while(iterator.hasNext()){
			currentTask = iterator.next();
			
			//obviously have to check if any of the tasks actually
			//consists of an instance of RequestMessage
			if(message.equals(currentTask.getDataObject())){
				
				//if newly received message is on its way back and the currently
				//held isn't, then replace.
				if(message.getReturnToSender() && 
							!((RequestMessage)currentTask.getDataObject()).getReturnToSender()){
					iterator.remove();
					taskQueue.add(new Task(message, TaskAction.HANDLE_REQUESTMESSAGE));
					setNodeState(NodeState.BUSY);
					setTimeOfMostRecentUpdate(field.getCurrentTime());
					
				//if both are not, or if both are on their way back, then compare 
				//current lives. the one with most lives is the one who gets to stay.
				}else if((!message.getReturnToSender() && 
						!((RequestMessage)currentTask.getDataObject()).getReturnToSender()) ||
						(message.getReturnToSender() && 
						((RequestMessage)currentTask.getDataObject()).getReturnToSender())){
					if(message.compareTo(((RequestMessage)currentTask.getDataObject())) > 0){
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
	
	public void updateNeighbours(){
		if(field.getRecentlyChangedNodeNetwork()){
			neighboursList = field.getNodesWithinRangeofNode(this);
		}
	}
	
	private void setNodeState(NodeState nodeState){
		this.nodeState = nodeState;
	}
	
	protected boolean hasUpdatedThisTimeTick(){
		return timeOfMostRecentUpdate == field.getCurrentTime();
	}
	
	private void setTimeOfMostRecentUpdate(int time){
		timeOfMostRecentUpdate = time;
	}
	
	private void printEvent(Event event, RequestMessage message){
		System.out.println(field.getCurrentTime() 
				+ ": " + this.toString()
				+ " received event details from request sent at "
				+ message.getTimeOfCreation() + " --"
				+ " x:" + event.getPosition().getX() 
				+ "; y:" + event.getPosition().getY()
				+ ", time: " + event.getTime()
				+ ", id: " + event.getId()
				+ "\n");
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
