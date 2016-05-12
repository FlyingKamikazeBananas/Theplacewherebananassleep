package nodebasis;

public abstract class Message {
	
	private final int createdAtTime;
	private final int expireByTime;
	
	public Message(int createdAtTime, int expireByTime){
		this.createdAtTime = createdAtTime;
		this.expireByTime = expireByTime;
	}
	
	protected abstract void update(Node node);
	
	protected int getExpirationTime(){
		return expireByTime;
	}
	
	protected int getBirthTime(){
		return createdAtTime;
	}
}
