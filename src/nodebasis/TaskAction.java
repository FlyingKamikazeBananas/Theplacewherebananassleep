package nodebasis;

/**
 * The enumeration TaskAction holds the various executable tasks a
 * node can manage.
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * */
public enum TaskAction {
	HANDLE_REQUESTMESSAGE(0), HANDLE_AGENTMESSAGE(1), 
	CREATE_REQUESTMESSAGE(2), CREATE_AGENTMESSAGE(3);
	
	private final int importance;
	
	private TaskAction(int importance){
		this.importance = importance;
	}
	
	/**
	 * <b>getTaskActionImportance</b>
	 * <pre>public int getTaskActionImportance()</pre>
	 * <p>
	 * Returns the value representation of the importance 
	 * of performing the specific action.
	 * </p>
	 * @return the importance of the action.
	 */
	public int getTaskActionImportance(){
		return this.importance;
	}
}