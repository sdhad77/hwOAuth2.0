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
	
	@Override
	protected void onPreExecute() {
		
		httpTransport = new NetHttpTransport();
		jsonFactory = new JacksonFactory();
	}
	
	@Override
	protected Void doInBackground(Credential... credential) {
		
		Tasks service = new Tasks.Builder(httpTransport, jsonFactory, credential[0].getCredential()).setApplicationName("MYTask").build();
		
		TaskList taskList = new TaskList();
		taskList.setTitle("New Task List");

		try {
			service.tasklists().insert(taskList).execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
