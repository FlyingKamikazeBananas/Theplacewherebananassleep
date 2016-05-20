package surrounding;

/**
 * The <code>FieldRunner</code> class implements the <code>Runnable</code> interface.
 * The class itself is the cogwheel of the simulation; it makes sure that the simulation
 * doesn't progress too fast.
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * */
public class FieldRunner implements Runnable{
	
	public static final int DEFAULT_UPDATES_PER_SECOND = 1000;
	
	private volatile boolean isRunning = true;
	private final int updateMargin;
	private Field field;
	
	/**
	 * <b>FieldRunner</b>
	 * <pre>public FieldRunner(Field field)</pre>
	 * <p>
	 * Creates a <code>FieldRunner</code> object for the specified <code>Field</code>
	 * object, with an upper limit of 1000 updates per second.
	 * </p>
	 * @param field the <code>Field</code> object which the <code>FieldRunner</code> is
	 * supposed to run.
	 * @see Field
	 */
	public FieldRunner(Field field){
		this(field, DEFAULT_UPDATES_PER_SECOND);
	}
	
	/**
	 * <b>FieldRunner</b>
	 * <pre>public FieldRunner(Field field)</pre>
	 * <p>
	 * Creates a <code>FieldRunner</code> object for the specified <code>Field</code>,
	 *  and the upper bound on the amount of updates which are allowed per second.
	 * The actual amount of updates can be lower than this amount.
	 * </p>
	 * @param field the <code>Field</code> object which the <code>FieldRunner</code> is
	 * supposed to run.
	 * @param updatesPerSecond upper bound on allowed updates per second.
	 * @throws java.lang.IllegalArgumentException if the amount of specified updates are equal to
	 * or less than zero.
	 * @see Field
	 */
	public FieldRunner(Field field, int updatesPerSecond)throws IllegalArgumentException{
		if(updatesPerSecond > 0){
			this.field = field;
			updateMargin = 1000000000 / updatesPerSecond;
		}else{
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * <b>run</b>
	 * <pre>public void run()</pre>
	 * <p>
	 * Starts a simulation on a <code>Field</code>.
	 * </p>
	 * @throws java.lang.IllegalStateException if the <code>Field</code> doesn't contain
	 * a node network.
	 */
	@Override
	public void run() throws IllegalStateException{
		long lastUpdateTime = System.nanoTime();
		
		if(!field.getHasLoadedNodeNetwork()){
			throw new IllegalStateException("Aborted attempt to run a simulation of"
					+ " Field which hasn't loaded a node network.");
		}else{
			field.setSimulationIsRunning(true);
			while(isRunning){
				while(System.nanoTime() > lastUpdateTime){
					if(!field.getSimulationIsRunning()){
						shutDown();
						break;
					}
					field.update();
					lastUpdateTime += updateMargin;
				}
			}
		}
	}
	
	/**
	 * <b>shutDown</b>
	 * <pre>public synchronized void shutDown()</pre>
	 * <p>
	 * Tells the thread where the simulation is running on, to stop.
	 * </p>
	 */
	public synchronized void shutDown(){
		isRunning = false;
	}
}
