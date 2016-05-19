package nodebasis;

/**
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * */
public class Task{

	private Object dataObject;
	private TaskAction action;
	private int handleIndex;
	
	public Task(Object dataObject, TaskAction action){
		this.dataObject = dataObject;
		this.action = action;
		handleIndex = 0;
	}
	
	public Object getDataObject(){
		return dataObject;
	}
	
	public TaskAction getAction(){
		return action;
	}
	
	public void incrementIndex(){
		handleIndex++;
	}
	
	public int getHandleIntex(){
		return handleIndex;
	}
	
}
