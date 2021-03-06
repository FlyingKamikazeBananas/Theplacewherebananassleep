package nodebasis;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import coordination.Position;

/**
 * The <code>RequestMessage</code> class extends the <code>Message</code> class, and is
 * defined by the sole purpose of fetching information regarding a specific event,
 * on the request of a node. Various complementary methods such as navigators <code>getReturnToSender()</code>
 * and hasVisitedNode(Node) are provided to make it easier for a node to decide where the message 
 * is supposed to be sent thenceforth.
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * */
public class RequestMessage extends Message{
	
	private final int addressedTo;
	private final int lifeSpan;
	private final String requestId;
	private final int timeOfCreation;
	private final Node originatingNode;
	
	private Stack<Node> routingStack;
	private Map<Position, Node> visitedNodes;
	private Event event;
	private boolean returnToSender;
	private int currentMessageLife;
	private boolean isReturned;
	
	/**
	 * Creates a <code>RequestMessage</code> object with the id to the sought event, the
	 * amount of lives this message should possess, the time of its creation, and the
	 * origin node as a reference.
	 * </p>
	 * @param addressedTo the id of the sought event.
	 * @param messageLife the amount of lives the message should possess.
	 * @param timeOfCreation the time this message is created.
	 * @param originatingNode the <code>Node</code> instantiated this message.
	 * @throws java.lang.IllegalArgumentException if the given amount of lives are equal
	 * to or less than zero.
	 * @see Message
	 * @see Node
	 */
	public RequestMessage(int addressedTo, int messageLife,
			int timeOfCreation, Node originatingNode) throws IllegalArgumentException, 
			NullPointerException{
		super(messageLife);
		if(originatingNode != null){
			this.addressedTo = addressedTo;
			this.lifeSpan = this.currentMessageLife = messageLife;
			this.timeOfCreation = timeOfCreation;
			this.requestId = "" + originatingNode.toString() + "." + timeOfCreation;
			this.originatingNode = originatingNode;
			
			routingStack = new Stack<Node>();
			visitedNodes = new HashMap<Position, Node>();
			setReturnToSender(false);
			setIsReturned(false);
			
			routingStack.push(originatingNode);
			visitedNodes.put(originatingNode.getPosition(), originatingNode);
		}else{
			throw new NullPointerException("null node given");
		}
	}
	
	/**
	 * <p>
	 * Updates the message by adding or removing nodes to an internal stack
	 * specifying the path from its origin, and depending where on its journey
	 * it is, either: checking if the given node has the sought event, or
	 * if the given node is equal to the originating node, whereupon it will
	 * define itself as 'returned'.
	 * </p>
	 * @param node currently visited <code>Node</code>.
	 */
	protected void update(Node node){
		Event event;
		if(!getReturnToSender()){
			event = node.getEventById(addressedTo);
			if(event != null){
				this.event = event;
				setReturnToSender(true);
			}else{
				visitedNodes.put(node.getPosition(), node);
				routingStack.push(node);
			}
		}else{
			routingStack.pop();
			setIsReturned(getReturnToSender() &&
					node.equals(originatingNode));
		}
	}
	
	/**
	 * <p>
	 * Checks if the given node has passed along this message any time during the lifetime of this message.
	 * </p>
	 * @param node the <code>Node</code> to check.
	 * @return <code>true</code> if this message has been passed along by the given <code>Node</code> during its lifetime,
	 * <code>false</code> otherwise.
	 * 
	 */
	protected boolean hasVisitedNode(Node node){
		return visitedNodes.containsValue(node);
	}
	
	/**
	 * <b>getAddressedTo</b>
	 * <pre>protected int getAddressedTo()</pre>
	 * <p>
	 * Returns the id of the event this message is supposed to fetch data about.
	 * </p>
	 * @return the id of the event.
	 * 
	 */
	protected int getAddressedTo(){
		return addressedTo;
	}
	
	/**
	 * <p>
	 * Returns if this message is to be returned to its origin sender.
	 * </p>
	 * @return <code>true</code> if it is supposed to be return to the original sender,
	 * <code>false</code> otherwise.
	 * 
	 */
	protected boolean getReturnToSender(){
		return returnToSender;
	}
	
	/*
	 * Helper method.
	 * */
	private void setReturnToSender(boolean returnToSender){
		this.returnToSender = returnToSender;
	}
	
	/*
	 * Helper method.
	 * */
	private void setIsReturned(boolean isReturned){
		this.isReturned = isReturned;
	}
	
	/**
	 * <p>
	 * Returns if this message is returned to the original sender.
	 * </p>
	 * @return <code>true</code> if it is returned,
	 * <code>false</code> otherwise.
	 * 
	 */
	protected boolean getIsReturned(){
		return isReturned;
	}
	
	/**
	 * <p>
	 * Returns the most recent node on the path back to where 
	 * the message came from.
	 * </p>
	 * @return the most recently visited node.
	 * 
	 */
	protected Node getReturnAddress(){
		return routingStack.peek();
	}
	
	/**
	 * <p>
	 * Resets the current life of this message to the amount 
	 * it was given when instantiated.
	 * </p>
	 * 
	 */
	protected void resetCurrentMessageLife(){
		currentMessageLife = lifeSpan;
	}
	
	/**
	 * <p>
	 * Returns the time this message was instantiated.
	 * </p>
	 * @return the supposed time when this message was created.
	 */
	protected int getTimeOfCreation(){
		return timeOfCreation;
	}
	
	/**
	 * <p>
	 * Returns the data fetched from the sought event.
	 * </p>
	 * @return the event.
	 * @throws java.lang.IllegalStateException if the message 
	 * has not yet been returned.
	 */
	protected Event getEvent() throws IllegalStateException{
		if(getIsReturned()){
			return event;
		}else{
			throw new IllegalStateException("the message has not yet been returned");
		}
	}
	
	@Override
	public boolean isDead(){
		return currentMessageLife <= 0;
	}
	
	@Override
	public void decrementLifespan(){
		currentMessageLife--;
	}
	
	/*
	 * Helper method.
	 * */
	public int getCurrentLifespan(){
		return currentMessageLife;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + addressedTo;
		result = prime * result + (returnToSender ? 1231 : 1237);
		return result;
	}

	/**
	 * <p>
	 * Returns <code>true</code> if and only if this <code>RequestMessage</code> and 
	 * the compared object refer to the same (<code>this == other is true</code>), or if the both <code>RequestMessages</code>
	 * share the same originating <code>Node</code> and are addressed to the same <code>Event</code>.
	 * </p>
	 * @param obj the <code>Object</code> to compare to this.
	 * @return <code>true</code> if this <code>RequestMessage</code> and the compared object refer to the same, or if their values correspond.
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(!super.equals(obj))
			return false;
		if(getClass() != obj.getClass())
			return false;
		RequestMessage other = (RequestMessage)obj;
		if(addressedTo != other.addressedTo)
			return false;
		if(getOriginNode() != other.getOriginNode())
			return false;
		return true;
	}
	
	/**
	 * <p>
	 * Compares this <code>RequestMessage</code> with another.
	 * </p>
	 * @param message the <code>RequestMessage</code> to compare to this.
	 * @return a value >0 if this message has a greater current lifespan than the other,
	 * a value =0 if both have the same current lifespan, and <0 if the lifespan of this message
	 * is lower than the other.
	 */
	public int compareTo(RequestMessage message){
		return this.getCurrentLifespan() - message.getCurrentLifespan();
	}
	
	/**
	 * <p>
	 * Returns the id correlated to a <code>Request</code>.
	 * </p>
	 * @return the id of a <code>Request</code>
	 * @see Request
	 */
	public String getRequestId(){
		return requestId;
	}
	
	/*
	 * Helper method.
	 * */
	private Node getOriginNode(){
		return originatingNode;
	}
	
}
