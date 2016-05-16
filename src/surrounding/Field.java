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
import nodebasis.Node;

public class Field {
	
	private final int updateLimit;
	private final int eventChanceRange;
	private final int agentChanceRange;
	private final int requestIntervalRange;
	private final int numberOfRequestNodes;
	private final Random random;
	
	private HashMap<Position, Node> nodeMap;
	private List<Node> requestNodesList;
	private boolean recentlyChangedNodeNetwork;
	private int currentTime, eventId;
	private boolean hasLoadedNodeNetwork;
	
	public Field(int updateLimit, int eventChanceRange, 
			int agentChanceRange, int requestIntervalRange,
			int numberOfRequestNodes){
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
	}
	
	protected void update(FieldRunner runner){
		Iterator<Entry<Position, Node>> iterator = nodeMap.entrySet().iterator();
		Node tempNode;
		Event event;
		//System.out.println("Time: " + getCurrentTime() + "; size: " + nodeMap.size());
		if(updateLimit >= getCurrentTime()) {
			while (iterator.hasNext()) {
				tempNode = iterator.next().getValue();
				if(shouldGenerateNewEvent()){
					event = tempNode.generateNewEvent(newEventId(),
							getCurrentTime());
					if(shouldGenerateNewAgentMsg()){
						tempNode.generateNewTask(event);
					}
				}
				tempNode.update();
			}
			if(shouldGenerateNewRequests()){
				for(Node node : requestNodesList){
					node.generateNewTask(random.nextInt(eventId));
				}
			}
			incrementCurrentTime();
		}else{
			runner.shutDown();
		}
		
	}

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
	
	private int newEventId(){
		eventId++;
		return eventId;
	}
	
	private boolean shouldGenerateNewEvent(){
		return random.nextInt(eventChanceRange) == 0;
	}
	
	private boolean shouldGenerateNewAgentMsg(){
		return random.nextInt(agentChanceRange) == 0;
	}
    
	private boolean shouldGenerateNewRequests(){
		
		return getCurrentTime()>0 && getCurrentTime()%requestIntervalRange==0;
	}
	
	public void loadNodeNetwork(HashMap<Position, Node> nodeMap){
		if(!hasLoadedNodeNetwork){
			setRecentlyChangedNodeNetwork(true);
			
			for(Map.Entry<Position, Node> entry : nodeMap.entrySet()){
				this.nodeMap.put(entry.getKey(), entry.getValue());
			}
			for(Map.Entry<Position, Node> entry : this.nodeMap.entrySet()){
				requestNeighbourUpdate(entry.getValue());
			}
			
			createRequestNodesList();
			
			hasLoadedNodeNetwork = true;
			setRecentlyChangedNodeNetwork(false);
		}
	}
	
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
		
		return listToReturn;
	}
	

	public int getCurrentTime(){
		return currentTime;
	}
	
	public boolean getHasLoadedNodeNetwork(){
		return hasLoadedNodeNetwork;
	}
	
	public boolean getRecentlyChangedNodeNetwork(){
		return recentlyChangedNodeNetwork;
	}
	
	private void setRecentlyChangedNodeNetwork(boolean recentlyChangedNodeNetwork){
		this.recentlyChangedNodeNetwork = recentlyChangedNodeNetwork;
	}
	
	private void setCurrentTime(int currentTime){
		this.currentTime = currentTime;
	}
	
	private void incrementCurrentTime(){
		currentTime++;
	}
	
	private void requestNeighbourUpdate(Node node){
		node.updateNeighbours();
	}
	
}
