package nodebasis;

/**
 * The Request class is used to keep track on the time elapsed since
 * a request message was created. The node can therefore determine if 
 * it should discard the request or continue the wait. The class implements 
 * the Lifespan interface.
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * */
public class Request implements Lifespan{

	private final int requestLifespan;
	
	private int numberOfTimesRevived;
	private int currentRequestLifespan;
	
	/**
	 * <b>Request</b>
	 * <pre>public Request(int requestLifespan)</pre>
	 * <p>
	 * Creates a <code>Request</code> object with the 
	 * amount of lives the request message initially had.
	 * </p>
	 * @param messageLife the amount of lives the message should possess.
	 * @throws java.lang.IllegalArgumentException if the given amount of lives are equal
	 * to or less than zero.
	 */
	public Request(int requestLifespan){
		this.requestLifespan = this.currentRequestLifespan = 
				(requestLifespan * 8);
		this.numberOfTimesRevived = 0;
	}
	
	/**
	 * <b>decrementLifespan</b>
	 * <pre>public void decrementLifespan()</pre>
	 * <p>
	 * Decrements the current lifespan of the request by one.
	 * </p>
	 */
	@Override
	public void decrementLifespan(){
		currentRequestLifespan--;
	}

	/**
	 * <b>isDead</b>
	 * <pre>public boolean isDead()</pre>
	 * <p>
	 * Returns if the request has expired.
	 * </p>
	 * @return <code>true</code> if the request has expired,
	 * <code>false</code> otherwise.
	 */
	@Override
	public boolean isDead(){
		return currentRequestLifespan <= 0;
	}
	
	/**
	 * <b>reviveRequest</b>
	 * <pre>public void reviveRequest()</pre>
	 * <p>
	 * Revives the request, returning it to full health.
	 * </p>
	 */
	public void reviveRequest(){
		this.currentRequestLifespan = this.requestLifespan;
		numberOfTimesRevived++;
	}
	
	/**
	 * <b>getNumberOfTimesRevived</b>
	 * <pre>public int getNumberOfTimesRevived()</pre>
	 * <p>
	 * Returns how many times the request has been revived.
	 * </p>
	 * @return the amount of times the request has been revived.
	 */
	public int getNumberOfTimesRevived(){
		return numberOfTimesRevived;
	}
	
	/**
	 * <b>getLife</b>
	 * <pre>public int getLife()</pre>
	 * <p>
	 * Returns how many lives the request currently possesses.
	 * </p>
	 * @return the current health of the request.
	 */
	public int getLife(){
		return currentRequestLifespan;
	}
	
}
