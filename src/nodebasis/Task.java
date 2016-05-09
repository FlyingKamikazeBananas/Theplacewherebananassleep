package nodebasis;

public class Task{

	private Object dataObject;
	private TaskAction action;
	
	public Task(Object dataObject, TaskAction action){
		this.dataObject = dataObject;
		this.action = action;
	}
	
	public Object getDataObject(){
		return dataObject;
	}
	
	public TaskAction getAction(){
		return action;
	}
	
	
	
}
