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
	private Map<Integer, ImplicitEvent> routingMap;
	private Map<Integer, Request> requestMap;
	private Map<Integer, Event> eventMap;
	private List<Node> neighboursList;
	private Position position;
	private int timeOfMostRecentUpdate;
	private NodeState nodeState;
	private int signalStrength;
	private Field field;
	
	public Node(Field field, Position position, int signalStrength,
			int agentLife, int requestLife) throws IllegalArgumentException, 
			NullPointerException{
		Comparator<Task> taskComparator = new TaskComparator();
		if(field == null){
			throw new NullPointerException("field can not be null");
		}else if(position == null){
			throw new NullPointerException("position can not be null");
		}else if(signalStrength < 0){
			throw new IllegalArgumentException("signal strength must be positive");
		}else if(agentLife <= 0 || requestLife <= 0){
			throw new IllegalArgumentException("messages must have a minimum lifespan"
					+ "of 1");
		}else{
			this.field = field;
			this.position = position;
			this.signalStrength = signalStrength;
			this.agentLife = agentLife;
			this.requestLife = requestLife;
		
			nodeState = NodeState.READY;
			timeOfMostRecentUpdate = 0;
			
			taskQueue = new PriorityQueue<Task>(10, taskComparator);
			eventMap = new HashMap<Integer, Event>();
			neighboursList = new ArrayList<Node>();
			routingMap = new HashMap<Integer, ImplicitEvent>();
			requestMap = new HashMap<Integer, Request>();
		}
	}
	
	/**
	 * <b><i>update</i></b>
	 * <p>
	 * <pre>public void update()</pre>
	 * 
	 * The update method completes up to one task
	 * specified by earlier generated <code>Task</code>s, every
	 * time the method is called. If the <code>Node</code> fails
	 * to complete the task with the highest priority it will try
	 * the next of equal or lower priority until there are no more
	 * tasks or if the <code>Node</code> manages to complete one.
	 * <p>
	 * The <code>Node</code> will not attempt to complete any tasks
	 * if it recently has been updated either by being called by this 
	 * method, or if it recently has received a message.
	 * <p>
	 * Any active requests will lose one life upon calling this method.
	 * <p>
	 * @see AgentMessage
	 * @see RequestMessage
	 * @see Task
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
						}
					}
				}
			}
		}
	}
	
	/*
	 * Helper method.
	 * 
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
	
	/*
	 * Helper method.
	 * 
	 * Returns all tasks which couldn't be completed during the update, to
	 * the priority queue holding the tasks.
	 * */
	private void returnToTaskQueue(ArrayList<Task> taskList){
		for(Task task : taskList){
			task.incrementIndex();
			taskQueue.add(task);
		}
	}
	
	/*
	 * Helper method.
	 * 
	 * Attempts to send a request message (without specified receiving node).
	 * It does it in this order, if any of the previous fails:
	 * 		- first checking in the routing table if there is a known
	 * 			path to the event,
	 * 		- then checking any of the adjacent nodes which hasn't
	 * 			yet passed along this request message,
	 * 		- and lastly checking any adjacent nodes if they are ready
	 * 			to receive.
	 * 
	 * If all of the above fail, then the node will abandon the associated task
	 * until next update.
	 * 
	 * Notes: if the node has a neighboring node which knows the way
	 * to a specific event, it would be folly to send it to a node
	 * which doesn't know the way. Hence the method can return true
	 * or false in the cluster contained in the first if-statement.
	 * 
	 * The method either returns true or false, depending on if
	 * the message was successfully sent or not.
	 * */
	private boolean sendMessage(RequestMessage message){
		Node legitNode;
		boolean visitedAll = true;
		
		if(routingMap.containsKey(message.getAddressedTo())){
			legitNode = routingMap.get(message.getAddressedTo()).getNode();
			if(sendMessage(legitNode, message)){
				message.resetCurrentMessageLife();
				return true;
			}else{
				return false;
			}
		}
		
		for(Node node : neighboursList){
			if(!message.hasVisitedNode(node)){
				visitedAll = false;
				if(sendMessage(node, message)){
					return true;
				}
			}
		}
		
		if(visitedAll){
			for(Node node : neighboursList){
				if(sendMessage(node, message)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	/*
	 * Helper method.
	 * 
	 * Attempts to send an agent message (without specified receiving node).
	 * It does it in this order, if any of the previous fails:
	 * 		- first checking any of the adjacent nodes which hasn't
	 * 			yet passed along this agent message,
	 * 		- and lastly checking any adjacent nodes if they are ready
	 * 			to receive.
	 * 
	 * If all of the above fail, then the node will abandon the associated task
	 * until next update.
	 * 
	 * The method either returns true or false, depending on if
	 * the message was successfully sent or not.
	 * */
	private boolean sendMessage(AgentMessage message){
		boolean visitedAll = true;
		
		for(Node node : neighboursList){
			if(!message.hasVisitedNode(node)){
				visitedAll = false;
				if(sendMessage(node, message)){
					return true;
				}
			}
		}
		
		if(visitedAll){
			for(Node node : neighboursList){
				if(sendMessage(node, message)){
					return true;
				}
			}
		}
		
		return false;
	}
	
	/*
	 * Helper method.
	 * 
	 * Attempts to send a request message to a specific node.
	 * 
	 * The method either returns true or false, depending on if
	 * the message was successfully sent or not.
	 * */
	private boolean sendMessage(Node node, RequestMessage message){
		if(node.getNodeState() == NodeState.READY &&
				!node.hasUpdatedThisTimeTick()){
			node.generateNewTask(message);
			message.resetCurrentMessageLife();
			return true;
		}
		return false;
	}
	
	/*
	 * Helper method.
	 * 
	 * Attempts to send an agent message to a specific node.
	 * 
	 * The method either returns true or false, depending on if
	 * the message was successfully sent or not.
	 * */
	private boolean sendMessage(Node node, AgentMessage message){
		if(node.getNodeState() == NodeState.READY &&
				!node.hasUpdatedThisTimeTick()){
			node.generateNewTask(message);
			return true;
		}
		return false;
	}
	
	/**
	 * <b><i>generateNewEvent</i></b>
	 * <p>
	 * <pre>public Event generateNewEvent(int id)</pre>
	 * 
	 * Generates a new <code>Event</code> with the specified id.
	 * The <code>Event</code> is then stored by the <code>Node</code>
	 * which was used to call this method. If an event with the specified
	 * id already is contained by the <code>Node</code>, the <code>Event</code>
	 * is then replaced by the newly generated one. 
	 * <p>
	 * @param id - the id of the <code>Event</code>.
	 * @return the <code>Event</code> which was generated.
	 * @see Event
	 * @see Task
	 */
	public Event generateNewEvent(int id){
		Event e = new Event(id, field.getCurrentTime(), this);
		eventMap.put(id, e);
		return e;
	}
	
	/**
	 * <b><i>generateNewTask</i></b>
	 * <p>
	 * <pre>public void generateNewTask(Event event)</pre>
	 * 
	 * Generates a new internal <code>Task</code> for the <code>Node</code>
	 * telling it to create a new <code>AgentMessage</code> with the
	 * given <code>Event</code> as the most recent occurrence.
	 * <p>
	 * @param event - the most recent <code>Event</code>.
	 * @throws java.io.NullPointerException if the given <code>Event</code> is null.
	 * @see AgentMessage
	 * @see Event
	 * @see Task
	 */
	public void generateNewTask(Event event) throws NullPointerException{
		if(event != null){
			taskQueue.add(new Task(event, TaskAction.CREATE_AGENTMESSAGE));
			setNodeState(NodeState.BUSY);
		}else{
			throw new NullPointerException("can not generate new task with"
					+ " null event");
		}
	}
	
	/**
	 * <b><i>generateNewTask</i></b>
	 * <p>
	 * <pre>public void generateNewTask(Integer id)</pre>
	 * 
	 * Generates a new internal <code>Task</code> for the <code>Node</code>
	 * telling it to create a new <code>RequestMessage</code> with the
	 * given id specifying which <code>Event</code> to fetch data from.
	 * <p>
	 * @param id - the id of the <code>Event</code> which the <code>Node</code>
	 * will pass to the <code>RequestMessage</code>.
	 * @throws java.io.NullPointerException if the given <code>Integer</code> is null.
	 * @see Event
	 * @see RequestMessage
	 * @see Task
	 */
	public void generateNewTask(Integer id) throws NullPointerException{
		if(id != null){
			taskQueue.add(new Task(id, TaskAction.CREATE_REQUESTMESSAGE));
			setNodeState(NodeState.BUSY);
		}else{
			throw new NullPointerException("can not generate new task with"
					+ " null id");
		}
	}
	
	/*
	 * Helper method.
	 * 
	 * 
	 * */
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
		return new HashMap<Integer, ImplicitEvent>(routingMap);
	}
	
	protected Event getEventById(int id){
		return eventMap.get(id);
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
	
	/**
	 * <b><i>getStringRepresentation</i></b>
	 * <p>
	 * <pre>public String getStringRepresentation()</pre>
	 * 
	 * Returns a <code>String</code> representation of the <code>Node</code> on 
	 * the form:
	 * <p>
	 * <pre>position x; position y; signal strength; agent life; request life</pre>
	 * <p>
	 * @return the <code>String</code> representation of the <code>Node</code>.
	 */
	public String getStringRepresentation(){
		return getPosition().getX()
				+ ";" + getPosition().getY()
				+ ";" + getSignalStrength()
				+ ";" + agentLife
				+ ";" + requestLife;
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
		Node other;
		if(this == obj){
			return true;
		}
		if(obj == null){
			return false;
		}
		if(getClass() != obj.getClass()){
			return false;
		}
		other = (Node) obj;
		if(position == null){
			if(other.position != null){
				return false;
			}
		}else if(!position.equals(other.position)){
			return false;
		}
		return true;
	}
}
