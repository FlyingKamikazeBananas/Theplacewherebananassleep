package nodebasis;


import java.util.Comparator;

/**
 * The <code>TaskComparator class</code> implements the <code>java.util.Comparator</code>
 * interface. The class is used to order tasks by priority in the priority queue held by
 * the <code>Node class</code>.
 * 
 * @author  Alexander Beliaev
 * @version 1.0
 * @since   2016-05-19
 * */
public class TaskComparator implements Comparator<Task>{

	/**
	 * <b>compare</b>
	 * <pre>public int compare(Task task_one, Task task_two)</pre>
	 * <p>
	 * The compare method compares two tasks and returns:
	 * <ul>
	 * 		<li>>0 if the first task is of higher priority</li>
	 * 		<li>=0 if both tasks are of the same priority</li>
	 * 		<li><0 if the first task is of lower priority</li>
	 * </ul>
	 * </p>
	 * @param task_one the first task.
	 * @param task_two the second task.
	 * @return the difference in priority.
	 */
	@Override
	public int compare(Task task_one, Task task_two) {
		return task_one.getAction().getTaskActionImportance()-
				task_two.getAction().getTaskActionImportance();
	}
}
