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
 * The <code>Node</code> class resembles a wireless point. I has the ability to communicate
 * with nodes within its signal strength. The node creates tasks and stores them in
 * a priority queue in order to prioritize more important happenings, and to keep things tidy.
 * </p>
 * <p>
 * Depending on what tasks the node hold, upon calling the update method the node
 * will either create a new <code>AgentMessage</code> or <code>RequestMessage</code>,
 * or handle a <code>AgentMessage</code> or <code>RequestMessage</code> it has received earlier.</br>
 * The tasks are held in a priority queue with the following priority (top = high, bottom = lower):
 * <ul>
 * 		<li>creating an agent message</li>
 * 		<li>creating a request message</li>
 * 		<li>handling an agent message</li>
 * 		<li>handling a request message.</li>
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
 * If a newly created agent message could not be sent, it will be put in a new
 * task as "handling an agent message" and ultimately returned to the priority queue.</br>
 * Similarly if a newly created request message could not be sent, it will be put in a new task
 * and treated as "handling a request message".</br>
 * If the node could not process a message with the task of handling it, it will ultimately
 * return it to the priority queue.</br>
 * </br>
 * An instance of <code>Node</code> allows for external monitoring if it has received
 * a returning request message, and/or if an agent or request message has been discarded
 * by it.
 * </p>
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * */
public class Node{
	
	private final int agentLife;
	private final int requestLife;
	
	private PriorityQueue<Task> taskQueue;
	private Map<Integer, ImplicitEvent> routingMap;
	private Map<String, Request> requestMap;
	private Map<Integer, Event> eventMap;
	private List<Node> neighboursList;
	private Position position;
	private NodeState nodeState;
	private int signalStrength;
	private Field field;
	private ExpirationReader expirationReader;
	private RequestReader requestReader;
	
	/**
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
			
			taskQueue = new PriorityQueue<Task>(10, taskComparator);
			eventMap = new HashMap<Integer, Event>();
			neighboursList = new ArrayList<Node>();
			routingMap = new HashMap<Integer, ImplicitEvent>();
			requestMap = new HashMap<String, Request>();
		}
	}
	
	/**
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
		Iterator<Map.Entry<String, Request>> iterator;
		boolean successfulTask = false;
		
		for(Map.Entry<String, Request> entry : requestMap.entrySet()){
			entry.getValue().decrementLifespan();
		}
		
		if(this.getNodeState() == NodeState.READY && !taskQueue.isEmpty()){
			failedTasks = new ArrayList<Task>(taskQueue.size());
			while(!successfulTask && !taskQueue.isEmpty()){
				currentTask = taskQueue.peek();
				
				switch(currentTask.getAction()){
				case CREATE_AGENTMESSAGE:
					Task newTaskA;
					message = new AgentMessage(this, agentLife, field.getCurrentTime());
					taskQueue.poll();
					if(sendMessage((AgentMessage)message)){
						successfulTask = true;
					}else{
						newTaskA = new Task((AgentMessage)message,
								TaskAction.HANDLE_AGENTMESSAGE);
						newTaskA.incrementTries();
						failedTasks.add(newTaskA);
					}
					break;
				case CREATE_REQUESTMESSAGE:
					Task newTaskR;
					message = new RequestMessage((Integer)currentTask.getDataObject(),
							requestLife, field.getCurrentTime(), this);
					requestMap.put(((RequestMessage)message).getRequestId(),
							new Request(requestLife));
					taskQueue.poll();
					if(sendMessage((RequestMessage)message)){
						successfulTask = true;
					}else{
						newTaskR = new Task((RequestMessage)message,
								TaskAction.HANDLE_REQUESTMESSAGE);
						newTaskR.incrementTries();
						failedTasks.add(newTaskR);
					}
					break;
				case HANDLE_AGENTMESSAGE:
					message = (AgentMessage)currentTask.getDataObject();
					
					if(currentTask.getNumberOfTries() == 0){
						update((AgentMessage)message);
						((AgentMessage)message).update(this);
						((AgentMessage)message).decrementLifespan();
					}
					
					if(((AgentMessage)message).isDead()){
						if(expirationReader != null && (expirationReader.getReaderMode() == 
								ExpirationReader.ReaderMode.ALL || 
								expirationReader.getReaderMode() == 
										ExpirationReader.ReaderMode.EXPIRED_AGENTS)){
							expirationReader.readIdOfExpiredObject(((AgentMessage)message).getAgentId());
						}
						taskQueue.poll();
					}else if(sendMessage((AgentMessage)message)){
						taskQueue.poll();
						successfulTask = true;
					}else{
						failedTasks.add(taskQueue.poll());
					}
					break;
				case HANDLE_REQUESTMESSAGE:
					message = (RequestMessage)currentTask.getDataObject();
					
					if(currentTask.getNumberOfTries() == 0){
						((RequestMessage)message).update(this);
						((RequestMessage)message).decrementLifespan();
					}
					
					if(((RequestMessage)message).getIsReturned()){
						taskQueue.poll();
						if(requestMap.containsKey(((RequestMessage)message)
								.getRequestId())){
							printEvent(((RequestMessage)message).getEvent(),
									(RequestMessage)message);
							if(requestReader != null){
								requestReader.readSuccessfulRequestId(((RequestMessage)message).getRequestId());
							}
							requestMap.remove(((RequestMessage)message)
								.getRequestId());
						}else{
							printExpiredRequestOnRetrievedEvent(((RequestMessage)message).getEvent().getTime());
						}
					}else if(((RequestMessage)message).getReturnToSender()){
						if(sendMessage(((RequestMessage)message).getReturnAddress(),
								((RequestMessage)message))){
							((RequestMessage)message).resetCurrentMessageLife();
							taskQueue.poll();
							successfulTask = true;
						}else{
							failedTasks.add(taskQueue.poll());
						}
					}else if(((RequestMessage)message).isDead()){
						//printFoundExpiredRequestMessage((RequestMessage)message);
						if(expirationReader != null && (expirationReader.getReaderMode() == 
								ExpirationReader.ReaderMode.ALL || 
								expirationReader.getReaderMode() == 
										ExpirationReader.ReaderMode.EXPIRED_REQUESTS)){
							expirationReader.readIdOfExpiredObject(((RequestMessage)message).getRequestId());
						}
						taskQueue.poll();
					}else{
						if(sendMessage((RequestMessage)message)){
							taskQueue.poll();
							successfulTask = true;
						}else{
							failedTasks.add(taskQueue.poll());
						}
					}
					break;
				}
			}
			
			returnToTaskQueue(failedTasks);
			
			if(successfulTask){
				setNodeState(NodeState.BUSY);
			}
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
	
	/*
	 * Helper method.
	 * 
	 * Checks if the message possesses any valuable information,
	 * i.e. if the message knows a shorter path to an event, and if
	 * the message knows of an event which this node doesn't.
	 * */
	private void update(AgentMessage agentMessage){
		ImplicitEvent implicitEvent;
		int distance;
		
		for(Entry<Integer, ImplicitEvent> entry : agentMessage.getRoutingMap().entrySet()){
			implicitEvent = routingMap.get(entry.getKey());
			distance = entry.getValue().getDistance();
			
			if(implicitEvent != null && implicitEvent.getDistance() > distance){
				routingMap.replace(entry.getKey(), entry.getValue());
			}else if(implicitEvent == null){
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
			task.incrementTries();
			taskQueue.add(task);
		}
	}
	
	/*
	 * Notes: if the node has a neighboring node which knows the way
	 * to a specific event, it would be folly to send it to a node
	 * which doesn't know the way. Hence the method can return true
	 * or false in the cluster contained in the first if-statement.  
	 * */
	/**
	 * <p>
	 * Attempts to send a request message (without specified receiving node).
	 * It does it in this order, if any of the previous fails:
	 * <ul>
	 * 		<li>first checking in the routing table if there is a known
	 * 			path to the event,</li>
	 * 		<li>then checking any of the adjacent nodes which hasn't
	 * 			yet passed along this request message,</li>
	 * 		<li>and lastly checking any adjacent nodes if they are ready
	 * 			to receive.</li>
	 * </ul></br>
	 * If all of the above fail, then the node will abandon the associated task
	 * until next update.</br>
	 * </br>
	 * The method either returns true or false, depending on if
	 * the message was successfully sent or not.
	 * </p>
	 * @param message the message to send.
	 * @return <code>true</code> if the message was sent, <code>false</code> otherwise.
	 * @see RequestMessage
	 */
	protected boolean sendMessage(RequestMessage message){
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
	
	/**
	 * <p>
	 * Attempts to send an agent message (without specified receiving node).
	 * It does it in this order, if any of the previous fails:
	 * <ul>
	 * 		<li>first checking any of the adjacent nodes which hasn't
	 * 			yet passed along this agent message,</li>
	 * 		<li>and lastly checking any adjacent nodes if they are ready
	 * 			to receive.</li>
	 * </ul></br>
	 * If all of the above fail, then the node will abandon the associated task
	 * until next update.</br>
	 * </br>
	 * The method either returns true or false, depending on if
	 * the message was successfully sent or not.
	 * </p>
	 * @param message the message to send.
	 * @return <code>true</code> if the message was sent, <code>false</code> otherwise.
	 * @see AgentMessage
	 */
	protected boolean sendMessage(AgentMessage message){
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
	
	/**
	 * <p>
	 * Attempts to send a request message to a specific node. The attempt
	 * will only be successful if the receiving node is ready.</br>
	 * </br>
	 * The method either returns true or false, depending on if
	 * the message was successfully sent or not.
	 * </p>
	 * @param message the message to send.
	 * @param node the node to receive the message.
	 * @return <code>true</code> if the message was sent, <code>false</code> otherwise.
	 * @see RequestMessage
	 */
	protected boolean sendMessage(Node node, RequestMessage message){
		if(node.getNodeState() == NodeState.READY){
			node.generateNewTask(message);
			return true;
		}
		return false;
	}
	
	/**
	 * <p>
	 * Attempts to send a agent message to a specific node. The attempt
	 * will only be successful if the receiving node is ready.</br>
	 * </br>
	 * The method either returns true or false, depending on if
	 * the message was successfully sent or not.
	 * </p>
	 * @param message the message to send.
	 * @param node the node to receive the message.
	 * @return <code>true</code> if the message was sent, <code>false</code> otherwise.
	 * @see AgentMessage
	 */
	protected boolean sendMessage(Node node, AgentMessage message){
		if(node.getNodeState() == NodeState.READY){
			node.generateNewTask(message);
			return true;
		}
		return false;
	}
	
	/**
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
		routingMap.put(id, new ImplicitEvent(e));
		return e;
	}
	
	/**
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
		}else{
			throw new NullPointerException("can not generate new task with"
					+ " null event");
		}
	}
	
	/**
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
	}
	
	/**
	 * <p>
	 * Generates a new internal <code>Task</code> for the <code>Node</code>
	 * telling it to handle an <code>AgentMessage</code>. When called
	 * the node is marked as busy.
	 * </p>
	 * @param message the message to handle.
	 * @see AgentMessage
	 */
	protected void generateNewTask(AgentMessage message){
		addMessageTask(message, TaskAction.HANDLE_AGENTMESSAGE);
	}
	
	/**
	 * <p>
	 * Generates a new internal <code>Task</code> for the <code>Node</code>
	 * telling it to handle a <code>RequestMessage</code>. When called
	 * the node is marked as busy.</br>
	 * </br>
	 * More specifically if an equal request message is found being handled by 
	 * the node the following is checked and performed:
	 * <ul>
	 * 		<li>if either message is on its way back to the originating node where it was
	 * 			created, the one on the way back is stored and the other discarded.</li>
	 * 		<li>if both are on their way back to the originating node, or if both aren't
	 * 			then they are compared with the compareTo method in the RequestMessage class.
	 * 			The input message is only used if the compareTo method return a value greater
	 * 			than zero.</li>
	 * </ul>
	 * </p>
	 * @param message the message to handle.
	 * @see RequestMessage
	 */
	protected void generateNewTask(RequestMessage message){
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
	 * <p>
	 * Returns the current state of the node.
	 * </p>
	 * @return the state of the node.
	 */
	protected NodeState getNodeState(){
		return nodeState;
	}
	
	/**
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
	 * <p>
	 * Returns the int representation of the signal strength for this <code>Node</code>.
	 * </p>
	 * @return the signal strength of this <code>Node</code>.
	 */
	public int getSignalStrength(){
		return signalStrength;
	}
	
	/**
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
	 * Helper method.
	 * */
	private void setNodeState(NodeState nodeState){
		this.nodeState = nodeState;
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
				+ " (" + getStringRepresentation() + ")"
				+ " received event details from request sent at "
				+ message.getTimeOfCreation() + " --"
				+ " x:" + event.getPosition().getX() 
				+ "; y:" + event.getPosition().getY()
				+ ", time: " + event.getTime()
				+ ", id: " + event.getId());
	}
	
	/*
	 * Helper method.
	 * 
	 * This method is used in direct correlation to when a request message has
	 * returned to the node (expired).
	 * */
	private void printExpiredRequestOnRetrievedEvent(int timeSent){
		System.out.println(field.getCurrentTime() 
				+ ": " + this.toString()
				+ " (" + getStringRepresentation() + ")"
				+ " received expired event request sent at "
				+ timeSent
				+ ". Discarded.");
	}
	
	/*
	private void printFoundExpiredRequestMessage(RequestMessage message){
		System.out.println(field.getCurrentTime()
				+ ": request message with request id: "
				+ message.getRequestId()
				+ ", sent at time "
				+ message.getTimeOfCreation()
				+ " has expired, and been discarded by a node.");
	}*/
	
	/**
	 * <p>
	 * Sets an <code>ExpirationReader</code> to this specific node.
	 * </p>
	 * @param expirationReader the reader.
	 * @see ExpirationReader
	 */
	public void setExpirationReader(ExpirationReader expirationReader){
		this.expirationReader = expirationReader;
	}
	
	/**
	 * <p>
	 * Sets a <code>RequestReader</code> to this specific node.
	 * </p>
	 * @param requestReader the reader.
	 * @see RequestReader
	 */
	public void setRequestReader(RequestReader requestReader){
		this.requestReader = requestReader;
	}
	
	/**
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
		return result;
	}

	/**
	 * <p>
	 * Returns <code>true</code> if and only if this <code>Node</code> and 
	 * the compared object refer to the same (<code>this == other is true</code>), <b>or</b> if the <code>Position</code> of this <code>Node</code> is equal to
	 * the <code>Position</code> of the compared <code>Node</code>.
	 * </p>
	 * @param obj the <code>Object</code> to compare to this.
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
	
	/**
	 * <p>
	 * Sets the internal node state as ready.
	 * </p>
	 * @see NodeState
	 */
	public void reset(){
		setNodeState(NodeState.READY);
	}
}
