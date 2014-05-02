package GoogleAPI.Tasks;

import java.util.concurrent.ExecutionException;

import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

import Oauth2.Credential;

public class TasksService{

	public TaskLists getTasksList(Credential credential) {
		
		TaskLists taskLists = null;
		try {
			taskLists = new GetTasksList().execute(credential).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return taskLists;
	}
	
	public void showTasksList(TaskLists taskLists) {
		
		for (TaskList taskList : taskLists.getItems()) {
			  System.out.println(taskList.getTitle());
		}
	}
	
	public void addTasksList(Credential credential, TaskList taskList) {
		
	}
}
