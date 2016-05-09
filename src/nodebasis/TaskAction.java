package nodebasis;

public enum TaskAction {
	HANDLE_REQUESTMESSAGE(0), HANDLE_AGENTMESSAGE(0), 
	CREATE_REQUESTMESSAGE(1), CREATE_AGENTMESSAGE(2);
	
	private final int importance;
	
	private TaskAction(int importance){
		this.importance = importance;
	}
	
	public int getTaskActionImportance(){
		return this.importance;
	}
}
