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

/**
 * <p>
 * The <code>Node</code> class ...
 * </p>
 * <p>
 * Depending on what tasks the node hold, upon calling the update method the node
 * will either create a new <code>AgentMessage</code> or <code>RequestMessage</code>,
 * or handle a <code>AgentMessage</code> or <code>RequestMessage</code> it has received earlier.</br>
 * The tasks are held in a priority queue with the following priority (top = high, bottom = lower):
 * <ul>
 * 		<li>creating an agent message</li>
 * 		<li>creating a request message</li>
 * 		<li>handling an agent message, or a request message.</li>
 * </ul>
 * Handling an agent message refer to (if not already) comparing and synchronizing each routing map, then
 * passing the message along to an adequate neighboring node.</br>
 * Handling a request message refer to (if not already) checking where it is heading;
 * if heading back, then checking if it has reached its goal and process the fetched event (if
 * the request still is active);
 * or passing it along accordingly.</br>
 * If either message has reached or is past its expiration date, the node will disregard it
 * by removing it from the priority queue, and then try the next task instead.</br> 
 * </br>
 * If a newly created agent message could not be sent it will be put in a new
 * task as "handling an agent message" and ultimately returned to the priority queue.</br>
 * Similarly if a newly request message could not be send it will be put in a new task
 * and treated as "handling a request message".</br>
 * If the node could not process a message with the task of handling it, it will ultimately
 * return it to the priority queue.
 * </p>
 * */
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
	
	/**
	 * <b>Node</b>
	 * <pre>public Node(Field field, Position position, int signalStrength,
			int agentLife, int requestLife)</pre>
	 * <p>
	 * Creates a <code>Node</code> object with the specified properties.
	 * </p>
	 * @param field the <code>Field</code> instance holding this <code>Node</code>.
	 * @param position the <code>Position</code> this node should presume.
	 * @param signalStrength the signal strength of this node, this determines which
	 * other nodes this node can communicate with.
	 * @param agentLife the amount of lives this node should instantiate its agent messages
	 * with.
	 * @param requestLife the amount of live this node should instantiate its request messages
	 * with.
	 * @throws java.lang.IllegalArgumentException if the signal strength is less
	 * than zero, or if the specified agent or request lives are equal to or is less
	 * than zero.
	 * @throws java.lang.NullPointerException if either the given <code>Field</code> or the 
	 * given <code>Position</code> is null.
	 */
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
	 * <pre>public void update()</pre>
	 * <p>
	 * The update method completes up to one task
	 * specified by earlier generated <code>Task</code>s, every
	 * time the method is called. If the <code>Node</code> fails
	 * to complete the task with the highest priority it will try
	 * the next of equal or lower priority until there are no more
	 * tasks or if the <code>Node</code> manages to complete one.
	 * </p>
	 * <p>
	 * The <code>Node</code> will not attempt to complete any tasks
	 * if it recently has been updated either by being called by this 
	 * method, or if it recently has received a message.
	 * </p>
	 * <p>
	 * Any active requests will lose one life upon calling this method.
	 * </p>
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
						
						if(currentTask.getHandleIntex() == 0){
							((AgentMessage)message).update(this);
							update((AgentMessage)message);
							((AgentMessage)message).decrementLifespan();
						}
						
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
						
						if(((RequestMessage)message).getIsReturned()){
							taskQueue.remove();
							
							if(requestMap.containsKey(((RequestMessage)message)
									.getRequestId())){
								printEvent(((RequestMessage)message).getEvent(),
										(RequestMessage)message);
								requestMap.remove(((RequestMessage)message)
									.getRequestId());
							}
						}else if(((RequestMessage)message).isDead()){
							taskQueue.remove();
						}else if(((RequestMessage)message).getReturnToSender()){
							if(sendMessage(((RequestMessage)message).getReturnAddress(),
									((RequestMessage)message))){
								((RequestMessage)message).resetCurrentMessageLife();
								taskQueue.remove();
								successfulTask = true;
							}else{
								failedTasks.add(taskQueue.remove());
							}
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
	 * <pre>public Event generateNewEvent(int id)</pre>
	 * <p>
	 * Generates a new <code>Event</code> with the specified id.
	 * The <code>Event</code> is then stored by the <code>Node</code>
	 * which was used to call this method. If an event with the specified
	 * id already is contained by the <code>Node</code>, the <code>Event</code>
	 * is then replaced by the newly generated one. 
	 * </p>
	 * @param id the id of the <code>Event</code>.
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
	 * <pre>public void generateNewTask(Event event)</pre>
	 * <p>
	 * Generates a new internal <code>Task</code> for the <code>Node</code>
	 * telling it to create a new <code>AgentMessage</code> with the
	 * given <code>Event</code> as the most recent occurrence.
	 * </p>
	 * @param event the most recent <code>Event</code>.
	 * @throws java.lang.NullPointerException if the given <code>Event</code> is null.
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
	 * <pre>public void generateNewTask(Integer id)</pre>
	 * <p>
	 * Generates a new internal <code>Task</code> for the <code>Node</code>
	 * telling it to create a new <code>RequestMessage</code> with the
	 * given id specifying which <code>Event</code> to fetch data from.
	 * </p>
	 * @param id the id of the <code>Event</code> which the <code>Node</code>
	 * will pass to the <code>RequestMessage</code>.
	 * @throws java.lang.NullPointerException if the given <code>Integer</code> is null.
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
	 * See the two methods directly below this one.
	 * */
	private void addMessageTask(Message message, TaskAction action){
		taskQueue.add(new Task(message, action));
		setNodeState(NodeState.BUSY);
		setTimeOfMostRecentUpdate(field.getCurrentTime());
	}
	
	/*
	 * Helper method.
	 * 
	 * This method is used between nodes. It generates a new Task with
	 * the specified agent message, and then adds it to the priority queue
	 * which the node hold. The Node is marked as updated when this method is called.
	 * */
	private void generateNewTask(AgentMessage message){
		addMessageTask(message, TaskAction.HANDLE_AGENTMESSAGE);
	}
	
	/*
	 * Helper method.
	 * 
	 * This method is used between nodes. It generates a new Task with
	 * the specified request message, and then adds it to the priority queue
	 * which the node hold. The Node is marked as updated when this method is called.
	 * 
	 * More specifically if an equal request message is found within the priority queue of 
	 * the node the following is checked and performed:
	 * 		- if either message is on its way back to the originating node where it was
	 * 			created, the one on the way back is stored and the other disregarded.
	 * 		- if both are on their way back to the originating node, or if both aren't
	 * 			then they are compared with the compareTo method in the RequestMessage class.
	 * 			The input message is only used if the compareTo method return a value greater
	 * 			than zero.
	 * */
	private void generateNewTask(RequestMessage message){
		Iterator<Task> iterator;
		Task currentTask;
		boolean foundAlike = false;
		iterator = taskQueue.iterator();
		
		while(iterator.hasNext()){
			currentTask = iterator.next();
			
			if(message.equals(currentTask.getDataObject())){
				if(message.getReturnToSender() && 
							!((RequestMessage)currentTask.getDataObject()).getReturnToSender()){
					iterator.remove();
					addMessageTask(message, TaskAction.HANDLE_REQUESTMESSAGE);
				}else if((!message.getReturnToSender() && 
						!((RequestMessage)currentTask.getDataObject()).getReturnToSender()) ||
						(message.getReturnToSender() && 
						((RequestMessage)currentTask.getDataObject()).getReturnToSender())){
					if(message.compareTo(((RequestMessage)currentTask.getDataObject())) > 0){
						iterator.remove();
						addMessageTask(message, TaskAction.HANDLE_REQUESTMESSAGE);
					}
				}
				foundAlike = true;
				break;
			}
		}
		
		if(!foundAlike){
			addMessageTask(message, TaskAction.HANDLE_REQUESTMESSAGE);
		}
	}
	
	
	/**
	 * <b><i>getNodeState</i></b>
	 * <pre>protected NodeState getNodeState()</pre>
	 * <p>
	 * Returns the current state of the node.
	 * </p>
	 * @return the state of the node.
	 */
	protected NodeState getNodeState(){
		return nodeState;
	}
	
	/**
	 * <b><i>getRoutingMap</i></b>
	 * <pre>protected HashMap<Integer, ImplicitEvent> getRoutingMap()</pre>
	 * <p>
	 * Returns the current routing map which this node holds. The
	 * routing map tells from which adjacent nodes it received information
	 * regarding specific events.
	 * </p>
	 * @return a copy of the <code>HashMap</code> containing directions to
	 * events which the node know about.
	 */
	protected HashMap<Integer, ImplicitEvent> getRoutingMap(){
		return new HashMap<Integer, ImplicitEvent>(routingMap);
	}
	
	/**
	 * <b><i>getEventById</i></b>
	 * <pre>protected Event getEventById(int id)</pre>
	 * <p>
	 * Returns the <code>Event</code> corresponding to the specified
	 * event id, or <code>null</code> if no event with such id 
	 * is stored within the node.
	 * </p>
	 * @param id - the id of the sought <code>Event</code>
	 * @return the <code>Event</code> with the specified id, or null
	 * if no such event exist within the node.
	 */
	protected Event getEventById(int id){
		return eventMap.get(id);
	}
	
	/**
	 * <b><i>getSignalStrength</i></b>
	 * <pre>public int getSignalStrength()</pre>
	 * <p>
	 * Returns the int representation of the signal strength for this <code>Node</code>.
	 * </p>
	 * @return the signal strength of this <code>Node</code>.
	 */
	public int getSignalStrength(){
		return signalStrength;
	}
	
	/**
	 * <b><i>getPosition</i></b>
	 * <pre>public Position getPosition()</pre>
	 * <p>
	 * Returns the position object of this <code>Node</code>.
	 * </p>
	 * @return the position object of this <code>Node</code>.
	 * @see Position
	 */
	public Position getPosition(){
		return position;
	}
	
	/**
	 * <b><i>updateNeighbours</i></b>
	 * <pre>public void updateNeighbours()</pre>
	 * <p>
	 * Signals this <code>Node</code> to revise its list over neighboring nodes.
	 * The call has no effect if the <code>Field</code> in which the <code>Node</code>
	 * is held, hasn't recently made changes to the node network.
	 * </p>
	 * @see Field
	 */
	public void updateNeighbours(){
		if(field.getRecentlyChangedNodeNetwork()){
			neighboursList = field.getNodesWithinRangeofNode(this);
		}
	}
	
	/*
	 * Only the node class will and should be able to change its internal state. 
	 * */
	private void setNodeState(NodeState nodeState){
		this.nodeState = nodeState;
	}
	
	protected boolean hasUpdatedThisTimeTick(){
		return timeOfMostRecentUpdate == field.getCurrentTime();
	}
	
	/*
	 * Helper method.
	 * 
	 * Updates the holder with the time of the most recent update.
	 * */
	private void setTimeOfMostRecentUpdate(int time){
		timeOfMostRecentUpdate = time;
	}
	
	/*
	 * Helper method.
	 * 
	 * This method is used in direct correlation to when a request message has
	 * returned to the node (in time). It prints some interesting information
	 * surrounding the event which the node sent for.
	 * */
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
	 * <pre>public String getStringRepresentation()</pre>
	 * <p>
	 * Returns a <code>String</code> representation of the <code>Node</code> on 
	 * the form:
	 * </p>
	 * <pre>position x; position y; signal strength; agent life; request life</pre>
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

	/**
	 * <b><i>equals</i></b>
	 * <pre>public boolean equals(Object obj)</pre>
	 * <p>
	 * Returns <code>true</code> if and only if this <code>Node</code> and 
	 * the compared object refer to the same (<code>this == other is true</code>), <b>or</b> if the <code>Position</code> of this <code>Node</code> is equal to
	 * the <code>Position</code> of the compared <code>Node</code>.
	 * </p>
	 * @param obj - the <code>Object</code> to compare to this.
	 * @return <code>true</code> if this <code>Node</code> and the compared object refer to the same, or if their positions correspond.
	 * @see Position
	 */
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
