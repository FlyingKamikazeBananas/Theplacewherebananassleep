package nodebasis;

/**
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
	
	@Override
	public void decrementLifespan(){
		if(currentRequestLifespan<0)
			System.out.println(currentRequestLifespan);
		currentRequestLifespan--;
	}

	@Override
	public boolean isDead(){
		return currentRequestLifespan <= 0;
	}
	
	public void reviveRequest(){
		this.currentRequestLifespan = this.requestLifespan;
		numberOfTimesRevived++;
	}
	
	public int getNumberOfTimesRevived(){
		return numberOfTimesRevived;
	}
	
	public int getLife(){
		return currentRequestLifespan;
	}
	
}
