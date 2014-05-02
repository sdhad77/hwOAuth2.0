package GoogleAPI.Tasks;

import java.io.IOException;

import Oauth2.Credential;
import android.os.AsyncTask;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.TaskLists;

public class GetTasksList extends AsyncTask<Credential, Void, TaskLists>{

	private HttpTransport httpTransport;
	private JacksonFactory jsonFactory;
	
	@Override
	protected void onPreExecute() {
		
		httpTransport = new NetHttpTransport();
		jsonFactory = new JacksonFactory();
	}
	
	@Override
	protected TaskLists doInBackground(Credential... credential) {
		
		Tasks service = new Tasks.Builder(httpTransport, jsonFactory, credential[0].getCredential()).setApplicationName("MYTask").build();

		TaskLists taskLists = null;
		try {
			taskLists = service.tasklists().list().execute();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return taskLists;
	}
}
