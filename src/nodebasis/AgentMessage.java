package nodebasis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import coordination.Position;

/**
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * 
 * */
public class AgentMessage extends Message{
	
	private Map<Integer, ImplicitEvent> routingMap;
	private Map<Integer, ImplicitEvent> routingMapDeepCopy;
	private List<Integer> routingMapReference;
	private Map<Position, Node> visitedNodes;
	private Node previousNode;
	
	/**
	 * <b>AgentMessage</b>
	 * <pre>public AgentMessage(Node node, int messageLife)</pre>
	 * <p>
	 * Creates an <code>AgentMessage</code> object with the origin <code>Node</code> as a reference, and
	 * the amount of lives it should have.
	 * </p>
	 * @param node the originating Node, where the message was created.
	 * @param messageLife the amount of lives the message should possess.
	 * @throws java.lang.IllegalArgumentException if the given amount of lives are equal
	 * to or less than zero.
	 * @see Node
	 */
	public AgentMessage(Node node, int messageLife) throws IllegalArgumentException{
		super(messageLife);
		
		routingMap = new HashMap<Integer, ImplicitEvent>();
		routingMapDeepCopy = new HashMap<Integer, ImplicitEvent>();
		routingMapReference = new ArrayList<Integer>();
		visitedNodes = new HashMap<Position, Node>();
		previousNode = node;
		
		for(Entry<Integer, ImplicitEvent> entry : node.getRoutingMap().entrySet()){
			routingMap.put(entry.getKey(), entry.getValue());
			routingMapDeepCopy.put(entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * <b>update</b>
	 * <pre>protected void update(Node node)</pre>
	 * <p>
	 * This method must be called after data has been fetched from this message.</br>
	 * </br>
	 * </p>
	 * @param node currently visited <code>Node</code>.
	 */
	protected void update(Node node){
		routingMap.clear();
		
		for(Entry<Integer, ImplicitEvent> entry : node.getRoutingMap().entrySet()){
			routingMap.put(entry.getKey(), new ImplicitEvent(entry.getValue().getId(),
					entry.getValue().getDistance()+1, node));
		}
		visitedNodes.put(node.getPosition(), node);
	}
	
	/**
	 * <b>getRoutingMap</b>
	 * <pre>public HashMap<Integer, ImplicitEvent> getRoutingMap()</pre>
	 * <p>
	 * Returns the routing map which the agent message holds. It contains
	 * directions to various events.
	 * </p>
	 * @return a HashMap<Integer, ImplicitEvent> with directions to various events.
	 */
	protected HashMap<Integer, ImplicitEvent> getRoutingMap(){
		return new HashMap<Integer, ImplicitEvent>(routingMap);
	}
	
	/**
	 * <b>hasVisitedNode</b>
	 * <pre>public boolean hasVisitedNode(Node node)</pre>
	 * <p>
	 * Returns <code>true</code> or <code>false</code> depending on if this message has been to
	 * a specific node. If the given node is null, this returns <code>false</code>.
	 * </p>
	 * @param node the node to check.
	 * @return <code>true</code> if this message has been handled by the given node,
	 * <code>false</code> otherwise.
	 */
	protected boolean hasVisitedNode(Node node){
		if(node != null){
			return visitedNodes.containsValue(node);
		}
		return false;
	}
}
