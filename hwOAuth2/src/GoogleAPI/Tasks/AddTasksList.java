package GoogleAPI.Tasks;

import java.io.IOException;

import Oauth2.Credential;
import android.os.AsyncTask;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.TaskList;

public class AddTasksList extends AsyncTask<Credential, Void, Void>{

	private HttpTransport httpTransport;
	private JacksonFactory jsonFactory;
	private TaskList newTaskList;
	
	public TaskList getNewTaskList() {
		return newTaskList;
	}

	public void setNewTaskList(TaskList newTaskList) {
		this.newTaskList = newTaskList;
	}

	@Override
	protected void onPreExecute() {
		
		httpTransport = new NetHttpTransport();
		jsonFactory = new JacksonFactory();
	}
	
	@Override
	protected Void doInBackground(Credential... credential) {
		
		Tasks service = new Tasks.Builder(httpTransport, jsonFactory, credential[0].getCredential()).setApplicationName("MYTask").build();
		
		try {
			service.tasklists().insert(newTaskList).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
