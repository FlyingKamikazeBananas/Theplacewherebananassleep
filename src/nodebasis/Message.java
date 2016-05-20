package nodebasis;

/**
 * A <code>Message</code> is a means for the a node to spread or fetch information between
 * other nodes. The class implements the Lifespan interface.
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * @see Lifespan
 * */
public abstract class Message implements Lifespan{
	
	private final int messageLifespan;
	private int currentMessageLifespan;
	
	/**
	 * <b>Message</b>
	 * <pre>public Message(int messageLife)</pre>
	 * <p>
	 * Creates a <code>Message</code> object with the 
	 * amount of lives this message should possess.
	 * </p>
	 * @param messageLife the amount of lives the message should possess.
	 * @throws java.lang.IllegalArgumentException if the given amount of lives are equal
	 * to or less than zero.
	 */
	public Message(int messageLife) throws IllegalArgumentException{
		if(messageLife > 0){
			this.messageLifespan = this.currentMessageLifespan = messageLife;
		}else{
			throw new IllegalArgumentException("message lifespan must be a natural number");
		}
	}
	
	protected abstract void update(Node node);
	
	/**
	 * <b>decrementLifespan</b>
	 * <pre>public void decrementLifespan()</pre>
	 * <p>
	 * Decrements the current lifespan of the message by one.
	 * </p>
	 */
	@Override
	public void decrementLifespan(){
		currentMessageLifespan--;
	}
	
	/**
	 * <b>isDead</b>
	 * <pre>public boolean isDead()</pre>
	 * <p>
	 * Returns if the message has expired.
	 * </p>
	 * @return <code>true</code> if the message has expired,
	 * <code>false</code> otherwise.
	 */
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
