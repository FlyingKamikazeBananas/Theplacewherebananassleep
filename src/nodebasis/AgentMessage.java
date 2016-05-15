package nodebasis;

import java.util.HashMap;
import java.util.Map.Entry;

import coordination.Position;

public class AgentMessage extends Message{
	
	private HashMap<Integer, ImplicitEvent> routingMap;
	private HashMap<Position, Node> visitedNodes;
	
	public AgentMessage(Node node, int messageLife){
		super(messageLife);
		
		HashMap<Integer, ImplicitEvent> nodeRoutingMap = node.getRoutingMap();
		routingMap = new HashMap<Integer, ImplicitEvent>();
		visitedNodes = new HashMap<Position, Node>();
		
		for(Entry<Integer, ImplicitEvent> entry : nodeRoutingMap.entrySet()){
			routingMap.put(entry.getKey(), entry.getValue());
		}
	}
	
	protected void update(Node node){
		HashMap<Integer, ImplicitEvent> nodeRoutingMap = node.getRoutingMap();
		ImplicitEvent implicitEvent;
		int id;
		int distance;
		
		for(Entry<Integer, ImplicitEvent> entry : nodeRoutingMap.entrySet()){
			implicitEvent = routingMap.get(entry.getKey());
			distance = entry.getValue().getDistance();
			id = entry.getValue().getId();
			
			if(implicitEvent != null && implicitEvent.getDistance() > distance){
				routingMap.replace(entry.getKey(), new ImplicitEvent(id, distance+1, node));
			}else{
				routingMap.put(entry.getKey(), new ImplicitEvent(id, distance+1, node));
			}
		}
		
		visitedNodes.put(node.getPosition(), node);
	}
	
	protected HashMap<Integer, ImplicitEvent> getRoutingMap(){
		return routingMap;
	}
	
	protected boolean hasVisitedNode(Node node){
		if(node != null){
			return visitedNodes.containsValue(node);
		}
		return false;
	}
}
