package nodebasis;

public abstract class Message implements Lifespan{
	
	private final int messageLifespan;
	private int currentMessageLifespan;
	
	public Message(int messageLife) throws IllegalArgumentException{
		if(messageLife > 0){
			this.messageLifespan = this.currentMessageLifespan = messageLife;
		}else{
			throw new IllegalArgumentException("message lifespan must be a natural number");
		}
	}
	
	protected abstract void update(Node node);
	
	@Override
	public void decrementLifespan(){
		currentMessageLifespan--;
	}
	
	@Override
	public boolean isDead(){
		return currentMessageLifespan <= 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + currentMessageLifespan;
		result = prime * result + messageLifespan;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (currentMessageLifespan != other.currentMessageLifespan)
			return false;
		if (messageLifespan != other.messageLifespan)
			return false;
		return true;
	}
	
	
}
