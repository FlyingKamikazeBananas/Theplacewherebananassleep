package surrounding;

public class SimRunner extends Thread{
	
	private static final int DEFAULT_UPDATES_PER_SECOND = 1000;
	
	private volatile boolean isRunning = true;
	private final int updateMargin;
	private Field field;
	
	public SimRunner(Field field){
		this(field, DEFAULT_UPDATES_PER_SECOND);
	}
	
	public SimRunner(Field field, int updatesPerSecond)throws IllegalArgumentException{
		if(updatesPerSecond > 0){
			this.field = field;
			updateMargin = 1000000000 / updatesPerSecond;
		}else{
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public void run(){
		long lastUpdateTime = System.nanoTime();
		
		while(isRunning){
			while(System.nanoTime() > lastUpdateTime){
				field.update();
				lastUpdateTime += updateMargin;
			}
		}
	}
	
	public void shutDown(){
		isRunning = false;
	}
}
