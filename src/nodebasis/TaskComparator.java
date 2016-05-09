package nodebasis;

import java.util.Comparator;

public class TaskComparator implements Comparator<Task>{

	@Override
	public int compare(Task task_one, Task task_two) {
		return task_one.getAction().getTaskActionImportance()-
				task_two.getAction().getTaskActionImportance();
	}
}
