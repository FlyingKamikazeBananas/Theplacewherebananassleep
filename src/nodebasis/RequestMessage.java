package nodebasis;

import java.util.Stack;

public class RequestMessage extends Message{
	
	private final int addressedTo;
	private Stack<Node> routingStack;
	private Event event; //has to add some way for the
						//originating node to retrieve the Event
	private boolean returnToSender;
	
	public RequestMessage(int addressedTo, int createdAtTime, int expireByTime){
		super(createdAtTime, expireByTime);
		this.addressedTo = addressedTo;
		
		routingStack = new Stack<Node>();
		setReturnToSender(false);
	}
	
	protected void update(Node node){
		Event event;
		if(!getReturnToSender()){
			try{
				event = node.getEventById(addressedTo);
				this.event = event;
				setReturnToSender(true);
			}catch(IllegalArgumentException e){
				routingStack.push(node);
			}
		}
	}
	
	protected boolean getReturnToSender(){
		return returnToSender;
	}
	
	private void setReturnToSender(boolean returnToSender){
		this.returnToSender = returnToSender;
	}
	
	protected Node getReturnAddress(){
		return routingStack.pop(); //maybe change to peek()
								   //and increase supervision
	}
	
}
