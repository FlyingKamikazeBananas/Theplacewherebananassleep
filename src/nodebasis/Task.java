package nodebasis;

/**
 * The task class is used to associate an object with a specific (for a node) manageable
 * action.
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * @see TaskAction
 * */
public class Task{

	private Object dataObject;
	private TaskAction action;
	private int numberOfTries;
	
	/**
	 * <p>
	 * Creates a <code>Task</code> object with the data associated with a specific task which
	 * a node is to perform. This could be an <code>AgentMessage</code> along with the task of handling
	 * that message (TaskAction.HANDLE_AGENTMESSAGE).
	 * </p>
	 * @param dataObject the data associated with a task.
	 * @param action the task to perform.
	 * @see TaskAction
	 * @see Node
	 */
	public Task(Object dataObject, TaskAction action){
		this.dataObject = dataObject;
		this.action = action;
		numberOfTries = 0;
	}
	
	/**
	 * <p>
	 * Returns the data associated with the task.
	 * </p>
	 * @return the data associated with the task.
	 */
	public Object getDataObject(){
		return dataObject;
	}
	
	/**
	 * <p>
	 * Returns the task to perform.
	 * </p>
	 * @return the task to perform.
	 */
	public TaskAction getAction(){
		return action;
	}
	
	/**
	 * <p>
	 * Increments the amount of times a <code>Node</code> has attempted to
	 * execute the task by 1.
	 * </p>
	 */
	public void incrementTries(){
		numberOfTries++;
	}
	
	/**
	 * <p>
	 * Returns the amount of times a <code>Node</code> has attempted to
	 * execute the task.
	 * </p>
	 * @return the amount of times a <code>Node</code> has attempted to
	 * execute the task.
	 */
	public int getNumberOfTries(){
		return numberOfTries;
	}
	
}
