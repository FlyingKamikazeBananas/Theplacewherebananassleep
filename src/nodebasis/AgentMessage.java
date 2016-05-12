package nodebasis;

import java.util.HashMap;
import java.util.Map.Entry;

public class AgentMessage extends Message{
	
	private HashMap<Integer, ImplicitEvent> routingMap;
	
	public AgentMessage(Node node, int createdAtTime, int expireByTime){
		super(createdAtTime, expireByTime);
		
		HashMap<Integer, ImplicitEvent> nodeRoutingMap = node.getRoutingMap();
		routingMap = new HashMap<Integer, ImplicitEvent>();
		
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
	}
	
	protected HashMap<Integer, ImplicitEvent> getRoutingMap(){
		return routingMap;
	}
}
