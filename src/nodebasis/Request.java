package nodebasis;

public class Request implements Lifespan{

	private final int requestLifespan;
	
	private int numberOfTimesRevived;
	private int currentRequestLifespan;
	
	public Request(int requestLifespan){
		this.requestLifespan = this.currentRequestLifespan = 
				requestLifespan;
		this.numberOfTimesRevived = 0;
	}
	
	@Override
	public void decrementLifespan() {
		currentRequestLifespan--;
	}

	@Override
	public boolean isDead() {
		return currentRequestLifespan <= 0;
	}
	
	public void reviveRequest(){
		this.currentRequestLifespan = this.requestLifespan;
		numberOfTimesRevived++;
	}
	
	public int getNumberOfTimesRevived(){
		return numberOfTimesRevived;
	}
	
}
