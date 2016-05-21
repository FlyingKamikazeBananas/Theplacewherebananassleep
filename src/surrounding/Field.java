package surrounding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import coordination.Position;
import nodebasis.Event;
import nodebasis.Message;
import nodebasis.Node;

/**
 * The <code>Field</code> class holds the node network. It is its 
 * responsibility to make sure each node is updated, and made ready
 * for the next iteration, each iteration. It is the <code>Field</code> class
 * which initially determines which nodes are supposed to create messages and when.</br>
 * </br>
 * The class allows for some multi-threading. See each specific method for
 * further details.
 * 
 * @author  Alexander Beliaev, Nils Sundberg
 * @version 1.0
 * @since   2016-05-19
 * */
public class Field{
	
	private final int updateLimit;
	private final int eventChanceRange;
	private final int agentChanceRange;
	private final int requestIntervalRange;
	private final int numberOfRequestNodes;
	private final Random random;
	private final boolean enableAgentCreation;
	private final boolean enableEventCreation;
	private final boolean enableRequestCreation;
	
	private HashMap<Position, Node> nodeMap;
	private List<Node> requestNodesList;
	private boolean recentlyChangedNodeNetwork;
	private volatile int currentTime;
	private int eventId;
	private volatile boolean hasLoadedNodeNetwork;
	private volatile boolean simulationIsRunning;
	
	/**
	 * <p>
	 * Creates a <code>Field</code> object with the amount of updates the simulation hold,
	 * the event chance range (the chance it then calculated by 1/the input value), the agent
	 * chance range, the interval between request message creations, and the number of request
	 * nodes. The following are legal parameter values:
	 * <ul>
	 * 		<li>updateLimit: above 0</li>
	 * 		<li>requestIntervalRange: above 0</li>
	 * 		<li>eventChanceRange</li>
	 * 		<ol>
	 * 			<li>-1, to disable events and agents</li>
	 * 			<li>above 0 to enable event and specify value</li>
	 * 		</ol>
	 * 		<li>agentChanceRange</li>
	 * 		<ol>
	 * 			<li>-1, to disable agents</li>
	 * 			<li>above 0 to enable agents and specify value</li>
	 * 		</ol>
	 * 		<li>numberOfRequestNodes</li>
	 * 		<ol>
	 * 			<li>0, to disable requests</li>
	 * 			<li>above 0 to enable requests and specify value</li>
	 * 		</ol>
	 * </ul>
	 * </p>
	 * @param updateLimit the amount of updates the simulation should run.
	 * @param eventChanceRange the request chance range.
	 * @param agentChanceRange the agent chance range.
	 * @param requestIntervalRange the number of updates between request message creations.
	 * @param numberOfRequestNodes number of request nodes.
	 * @throws java.lang.IllegalArgumentException if either of the given arguments 
	 * don't follow the above mentioned rules.
	 * @see Message
	 * @see Node
	 */
	public Field(int updateLimit, int eventChanceRange, 
			int agentChanceRange, int requestIntervalRange,
			int numberOfRequestNodes) throws IllegalArgumentException{
		if(eventChanceRange == -1){
			if(agentChanceRange == -1){
				enableEventCreation = enableAgentCreation = false;
			}else{
				enableAgentCreation = true;
				enableEventCreation = false;
			}
		}else if(agentChanceRange == -1){
			enableAgentCreation = false;
			enableEventCreation = true;
		}else{
			enableEventCreation = enableAgentCreation = true;
		}
		if(numberOfRequestNodes == 0){
			enableRequestCreation = false;
		}else{
			enableRequestCreation = true;
		}
		
		if(updateLimit <= 0 || (eventChanceRange != -1 && eventChanceRange <= 0) ||
				(agentChanceRange != -1 &&  agentChanceRange <= 0) || 
				requestIntervalRange <= 0 ||
				numberOfRequestNodes < 0){
			throw new IllegalArgumentException("Illegal argument(s).");
		}else{
			this.updateLimit = updateLimit;
			this.eventChanceRange = eventChanceRange;
			this.agentChanceRange = agentChanceRange;
			this.requestIntervalRange = requestIntervalRange;
			this.numberOfRequestNodes = numberOfRequestNodes;
			
			random = new Random();
			nodeMap = new HashMap<Position, Node>();
			requestNodesList = new ArrayList<Node>(numberOfRequestNodes);
			setRecentlyChangedNodeNetwork(false);
			setCurrentTime(0);
			hasLoadedNodeNetwork = false;
			eventId = 0;
			simulationIsRunning = false;
		}
	}
	
	/**
	 * <p>
	 * Updates all nodes in the node network by calling all of their subsequent update methods.
	 * This method does this indiscriminately, meaning that it is up to each individual node
	 * to decide if it should update or not. This method also decides when and where events
	 * should spawn, and if an agent message should follow; and where and when the requests should
	 * be generated and sent.</br>
	 * </br>
	 * When each node has been updated, all the nodes will be iterated over again to reset these
	 * to "ready", meaning they are ready to be updated again.
	 * </p>
	 */
	public synchronized void update(){
		Iterator<Entry<Position, Node>> iterator = nodeMap.entrySet().iterator();
		Node tempNode;
		Event event;
		
		/*if(this.getCurrentTime() % 250 == 0){
			System.out.println(this.getCurrentTime() + ": running...");
		}*/
		
		if(updateLimit >= getCurrentTime() && simulationIsRunning){
			if(shouldGenerateNewRequests()){
				for(Node node : requestNodesList){
					node.generateNewTask(random.nextInt(eventId));
				}
			}
			while (iterator.hasNext()) {
				tempNode = iterator.next().getValue();
				if(shouldGenerateNewEvent()){
					event = tempNode.generateNewEvent(newEventId());
					if(shouldGenerateNewAgentMsg()){
						tempNode.generateNewTask(event);
					}
				}
				tempNode.update();
			}
			
			iterator = nodeMap.entrySet().iterator();
			while (iterator.hasNext()) {
				tempNode = iterator.next().getValue();
				tempNode.reset();
			}
			
			incrementCurrentTime();
		}else if(simulationIsRunning){
			simulationIsRunning = false;
			System.out.println("End of simulation");
		}
	}

	/*
	 * Helper method.
	 * 
	 * Generates the list over which nodes should generate requests.
	 * */
	private void createRequestNodesList(){
		List<Integer> list = new ArrayList<Integer>(numberOfRequestNodes);
		int temp;
		int index;
		for(int i=0; i<numberOfRequestNodes; i++){
			temp = random.nextInt(nodeMap.size());
			if(list.contains(temp)){
				i--;
			}else{
				((ArrayList<Integer>)list).add(temp);
			}
		}
        
		index = 0;
		for(Map.Entry<Position, Node> entry : nodeMap.entrySet()){
			if(list.contains(index)){
				((ArrayList<Node>)requestNodesList).add(entry.getValue());
			}
			index++;
		}
	}
	
	/*
	 * Helper method.
	 * 
	 * Generates and returns a new event id.
	 * */
	private int newEventId(){
		eventId++;
		return eventId;
	}
	
	/*
	 * Helper method.
	 * 
	 * Checks if a new event should be generated.
	 * */
	private boolean shouldGenerateNewEvent(){
		return enableEventCreation ? random.nextInt(eventChanceRange) == 0 : false;
	}
	
	/*
	 * Helper method.
	 * 
	 * Checks if a new agent message should be generated.
	 * */
	private boolean shouldGenerateNewAgentMsg(){
		return enableAgentCreation ? random.nextInt(agentChanceRange) == 0 : false;
	}
    
	/*
	 * Helper method.
	 * 
	 * Checks if a new request message should be generated.
	 * */
	private boolean shouldGenerateNewRequests(){
		return enableRequestCreation ? (getCurrentTime() > 0 &&
				getCurrentTime()%requestIntervalRange == 0) : false;
	}
	
	/**
	 * <p>
	 * This method must be called with an instantiated <code>HashMap</code> containing
	 * the node network, before the simulation can be started. If the method is called
	 * with a <code>null</code> <code>HashMap</code> nothing will happen.
	 * </br>
	 * </p>
	 * @param nodeMap the <code>HashMap</code> containing the node network.
	 */
	public void loadNodeNetwork(HashMap<Position, Node> nodeMap)
			throws IllegalArgumentException{
		if(!hasLoadedNodeNetwork && nodeMap != null){
			setRecentlyChangedNodeNetwork(true);
			
			for(Map.Entry<Position, Node> entry : nodeMap.entrySet()){
				this.nodeMap.put(entry.getKey(), entry.getValue());
			}
			for(Map.Entry<Position, Node> entry : this.nodeMap.entrySet()){
				requestNeighbourUpdate(entry.getValue());
			}
			
			if(numberOfRequestNodes > nodeMap.size()){
				this.nodeMap.clear();
				setRecentlyChangedNodeNetwork(false);
				throw new IllegalArgumentException("more request nodes specified "
						+ "than number of nodes in node network!");
			}else{
				createRequestNodesList();
				hasLoadedNodeNetwork = true;
				setRecentlyChangedNodeNetwork(false);
			}
		}
	}
	
	/**
	 * <p>
	 * This operation is somewhat expensive, and if possible should be used
	 * before the start of the simulation.</br>
	 * </br>
	 * By giving the method a node it will find all nodes within the range 
	 * of the specified node, and return an <code>ArrayList</code> containing
	 * all of the found nodes.
	 * </p>
	 * @param nodeAtCentrum the node whose signal strength is going to be
	 * tested and compared against other nodes.
	 * @return and <code>ArrayList</code> containing all found nodes.
	 */
	public ArrayList<Node> getNodesWithinRangeofNode(Node nodeAtCentrum){
		int signalStrength = nodeAtCentrum.getSignalStrength();
		int centrumX = nodeAtCentrum.getPosition().getX();
		int centrumY = nodeAtCentrum.getPosition().getY();
		int minYFromCentrum = (0 < centrumY-signalStrength ? centrumY-signalStrength : 0);
		int maxYFromCentrum = centrumY+signalStrength;
		int currentYOffset;
		int currentMinXFromCentrum;
		int currentMaxXFromCentrum;
		int boundsX;
		ArrayList<Node> listToReturn = new ArrayList<Node>();
		
		for(Map.Entry<Position, Node> entry : nodeMap.entrySet()){
			Position mapPos = entry.getKey();
			Node mapNode = entry.getValue();
			
			if(minYFromCentrum <= mapPos.getY() && mapPos.getY() <= maxYFromCentrum){
				currentYOffset = Math.abs(centrumY-mapPos.getY());
				boundsX = (int)Math.sqrt(Math.abs(Math.pow(signalStrength, 2)-
						Math.pow(currentYOffset, 2))); 
				currentMinXFromCentrum = (0 < centrumX-boundsX ? centrumX-boundsX : 0);
				currentMaxXFromCentrum = centrumX+boundsX;
				
				if((centrumX <= mapPos.getX() && mapPos.getX() <= currentMaxXFromCentrum) ||
						(currentMinXFromCentrum <= mapPos.getX() && mapPos.getX() < centrumX)){
					listToReturn.add(mapNode);
				}
			}
		}
		
		listToReturn.remove(nodeAtCentrum);
		
		return listToReturn;
	}
	
	/**
	 * <p>
	 * Returns the current time of the simulation.
	 * </p>
	 * @return the current amount of elapsed updates.
	 */
	public synchronized int getCurrentTime(){
		return currentTime;
	}
	
	/**
	 * <p>
	 * Returns whether or not a node network has been loaded by the field class.
	 * </p>
	 * @return <code>true</code> if a node network has been loaded, <code>false</code>
	 * otherwise.
	 */
	public synchronized boolean getHasLoadedNodeNetwork(){
		return hasLoadedNodeNetwork;
	}
	
	/**
	 * <p>
	 * Returns whether or not the node network recently has been modified.</br>
	 * No use as of this version.
	 * </p>
	 * @return <code>true</code> if the node network has been modified, <code>false</code>
	 * otherwise.
	 */
	public boolean getRecentlyChangedNodeNetwork(){
		return recentlyChangedNodeNetwork;
	}
	
	/**
	 * <p>
	 * Returns a string representation of the node network. Do not confuse this with
	 * a toString method. See the <code>getStringRepresentation</code> method in the
	 * <code>Node</code> class for further details.
	 * </p>
	 * @return the string representation of the node network.
	 */
	public String getStringRepresentation(){
		String representation = "";
		for(Map.Entry<Position, Node> entry : nodeMap.entrySet()){
			representation += entry.getValue().getStringRepresentation() + "\n";
		}
		return representation;
	}
	
	/**
	 * <p>
	 * Returns whether or not the simulation is currently running.
	 * </p>
	 * @return <code>true</code> if the simulation is running, <code>false</code>
	 * otherwise.
	 */
	public synchronized boolean getSimulationIsRunning(){
		return simulationIsRunning;
	}
	
	/**
	 * <p>
	 * Sets the simulation to run or stop. Only works as a means to start
	 * the simulation once.
	 * </p>
	 * @param simulationIsRunning <code>true</code> to start, <code>false</code> to stop.
	 */
	public synchronized void setSimulationIsRunning(boolean simulationIsRunning){
		this.simulationIsRunning = simulationIsRunning;
	}
	
	/*
	 * Helper method.
	 * 
	 * Sets if the node network has been modified. 
	 * */
	private void setRecentlyChangedNodeNetwork(boolean recentlyChangedNodeNetwork){
		this.recentlyChangedNodeNetwork = recentlyChangedNodeNetwork;
	}
	
	/*
	 * Helper method.
	 * 
	 * Sets the current time.
	 * */
	private void setCurrentTime(int currentTime){
		this.currentTime = currentTime;
	}
	
	/*
	 * Helper method.
	 * 
	 * Increments the current time.
	 * */
	private void incrementCurrentTime(){
		currentTime++;
	}
	
	/*
	 * Helper method.
	 * 
	 * Tells a specific node to update its list over neighbors.
	 * */
	private void requestNeighbourUpdate(Node node){
		node.updateNeighbours();
	}
	
}
