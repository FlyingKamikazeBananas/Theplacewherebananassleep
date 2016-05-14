package nodebasis;

public abstract class Message{
	
	private final int messageLife;
	private int currentMessageLife;
	
	public Message(int messageLife) throws IllegalArgumentException{
		if(messageLife > 0){
			this.messageLife = this.currentMessageLife = messageLife;
		}else{
			throw new IllegalArgumentException("message lifespan must be a natural number");
		}
	}
	
	protected abstract void update(Node node);
	
	protected int getInitialMessageLife(){
		return messageLife;
	}
	
	protected int getCurrentMessageLife(){
		return currentMessageLife;
	}
	
	protected void decrementMessageLife(){
		currentMessageLife--;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + currentMessageLife;
		result = prime * result + messageLife;
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
		if (currentMessageLife != other.currentMessageLife)
			return false;
		if (messageLife != other.messageLife)
			return false;
		return true;
	}
	
	
}
