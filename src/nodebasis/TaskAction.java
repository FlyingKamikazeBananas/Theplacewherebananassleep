package nodebasis;

/**
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
	
	
	public int getTaskActionImportance(){
		return this.importance;
	}
}