package nodebasis;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import coordination.Position;

//Behöver snyggas upp
//säkrare användning av routingStack

public class RequestMessage extends Message{
	
	private final int addressedTo;
	private final int lifeSpan;
	private final int requestId;
	private final int timeOfCreation;
	private final Node originatingNode;
	
	private Stack<Node> routingStack;
	private Map<Position, Node> visitedNodes;
	private Event event;
	private boolean returnToSender;
	private int currentMessageLife;
	private boolean isReturned;
	
	public RequestMessage(int addressedTo, int messageLife,
			int timeOfCreation, Node originatingNode) throws IllegalArgumentException{
		super(messageLife);
		this.addressedTo = addressedTo;
		this.lifeSpan = this.currentMessageLife = messageLife;
		this.timeOfCreation = this.requestId = timeOfCreation;
		this.originatingNode = originatingNode;
		
		routingStack = new Stack<Node>();
		visitedNodes = new HashMap<Position, Node>();
		setReturnToSender(false);
		setIsReturned(false);
		
		routingStack.push(originatingNode);
	}
	
	protected void update(Node node){
		Event event;
		if(!getReturnToSender()){
			try{
				event = node.getEventById(addressedTo);
				this.event = event;
				setReturnToSender(true);
			}catch(IllegalArgumentException e){
				visitedNodes.put(node.getPosition(), node);
				routingStack.push(node);
			}
		}else{
			routingStack.pop();
			setIsReturned(getReturnToSender() &&
					node.equals(originatingNode));
		}
	}
	
	protected boolean hasVisitedNode(Node node){
		return visitedNodes.containsValue(node);
	}
	
	protected int getAddressedTo(){
		return addressedTo;
	}
	
	protected boolean getReturnToSender(){
		return returnToSender;
	}
	
	private void setReturnToSender(boolean returnToSender){
		this.returnToSender = returnToSender;
	}
	
	private void setIsReturned(boolean isReturned){
		this.isReturned = isReturned;
	}
	
	protected boolean getIsReturned(){
		return isReturned;
	}
	
	protected Node getReturnAddress(){
		return routingStack.peek();
	}
	
	protected void resetCurrentMessageLife(){
		currentMessageLife = lifeSpan;
	}
	
	protected int getTimeOfCreation(){
		return timeOfCreation;
	}
	
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
	
	private int getCurrentLifespan(){
		return currentMessageLife;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + addressedTo;
		result = prime * result + currentMessageLife;
		result = prime * result + (returnToSender ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestMessage other = (RequestMessage) obj;
		if (addressedTo != other.addressedTo)
			return false;
		if (getOriginNode() != other.getOriginNode())
			return false;
		if (getTimeOfCreation() != other.getTimeOfCreation()){
			return false;
		}
		return true;
	}
	
	/**
	 * >0: this is greater than the other
	 * 0: equals
	 * <0: this is lower than the other
	 * */
	public int compareTo(RequestMessage message){
		return this.getCurrentLifespan() - message.getCurrentLifespan();
	}
	
	public int getRequestId(){
		return requestId;
	}
	
	private Node getOriginNode(){
		return routingStack.lastElement();
	}
	
}
