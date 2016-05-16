package surrounding;

public class FieldRunner extends Thread{
	
	private static final int DEFAULT_UPDATES_PER_SECOND = 1000;
	
	private volatile boolean isRunning = true;
	private final int updateMargin;
	private Field field;
	
	public FieldRunner(Field field){
		this(field, DEFAULT_UPDATES_PER_SECOND);
	}
	
	public FieldRunner(Field field, int updatesPerSecond)throws IllegalArgumentException{
		if(updatesPerSecond > 0){
			this.field = field;
			updateMargin = 1000000000 / updatesPerSecond;
		}else{
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public void run() throws IllegalStateException{
		long lastUpdateTime = System.nanoTime();
		
		if(!field.getHasLoadedNodeNetwork()){
			throw new IllegalStateException("Aborted attempt to run a simulation of"
					+ " Field which hasn't loaded a node network.");
		}else{
			while(isRunning){
				while(System.nanoTime() > lastUpdateTime){
					field.update(this);
					lastUpdateTime += updateMargin;
				}
			}
		}
	}
	
	public void shutDown(){
		isRunning = false;
	}
}
