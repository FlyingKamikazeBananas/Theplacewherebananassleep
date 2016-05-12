package nodebasis;

public class RequestMessage extends Message{
	
	public RequestMessage(int addressedTo, int createdAtTime, int expireByTime){
		super(createdAtTime, expireByTime);
	}
	
	protected void update(Node node){
		
	}
}
