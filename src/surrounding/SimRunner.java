package surrounding;

public class SimRunner extends Thread{
	
	private volatile boolean isRunning = true;
	
	public SimRunner(){
	}
	
	@Override
	public void run(){
		while(isRunning){
			
		}
	}
	
	public void shutDown(){
		isRunning = false;
	}
}
