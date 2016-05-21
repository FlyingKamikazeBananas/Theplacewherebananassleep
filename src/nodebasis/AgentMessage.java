package nodebasis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import coordination.Position;

/**
 * The <code>AgentMessage</code> class extends the <code>Message</code> class, and is
 * defined by the sole purpose of spreading knowledge regarding the directions to various events
 * which has occurred within the node network.</br>
 * </br>
 * It is important to note that whenever 
 * a node receives an instance to this class, that the node foremost fetches data from
 * the message before proceeding with updating the message itself. 
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * 
 * */
public class AgentMessage extends Message{
	
	private final String agentId;
	
	private Map<Integer, ImplicitEvent> routingMap;
	private Map<Position, Node> visitedNodes;
	
	/**
	 * <p>
	 * Creates an <code>AgentMessage</code> object with the
	 * the amount of lives it should have.
	 * </p>
	 * @param node the originating Node, where the message was created.
	 * @param messageLife the amount of lives the message should possess.
	 * @throws java.lang.IllegalArgumentException if the given amount of lives are equal
	 * to or less than zero.
	 * @see Node
	 */
	public AgentMessage(Node node, int messageLife, int time) throws IllegalArgumentException, 
			NullPointerException{
		super(messageLife);
		if(node != null){
			routingMap = new HashMap<Integer, ImplicitEvent>();
			visitedNodes = new HashMap<Position, Node>();
			visitedNodes.put(node.getPosition(), node);
			
			for(Entry<Integer, ImplicitEvent> entry : node.getRoutingMap().entrySet()){
				routingMap.put(entry.getKey(), entry.getValue());
			}
			agentId = node.toString() + "." + time;
		}else{
			throw new NullPointerException("null node given");
		}
	}
	
	/**
	 * <p>
	 * Fetch data from the routing map (by calling <code>getRoutingMap()</code>) of this instance before calling this 
	 * method. When this method is called the internal data is cleared, to
	 * then be redefined with data from the given <code>Node</code>.</br>
	 * </br>
	 * This method must be called before passing on this message to another <code>Node</code>.
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
	
	public String getAgentId(){
		return agentId;
	}
}
