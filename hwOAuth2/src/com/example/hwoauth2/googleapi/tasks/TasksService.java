package com.example.hwoauth2.googleapi.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import Oauth2.Oauth2Service;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.hwoauth2.R;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

public class TasksService extends Activity
{
	private ArrayAdapter<String> _adapter;
	
	private static TasksService _instance;
	
	public static TasksService getInstance()
	{
		if( _instance == null )
		{
			_instance = new TasksService();
		}

		return _instance;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tasks_activity);
		
		TaskLists _taskLists = getTaskLists();
		
		ArrayList<String> _arrayList = new ArrayList<String>();
       	for (TaskList taskList : _taskLists.getItems()) 
       	{
       		_arrayList.add(taskList.getTitle());
       	}
       	
       	_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, _arrayList);
       	
       	ListView _listView = (ListView)findViewById(R.id.listView1);
       	_listView.setAdapter(_adapter);
       	_listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
       	_listView.setVisibility(View.VISIBLE);
       	
       	_listView.setOnItemClickListener(new OnItemClickListener()
       	{
       		@Override
       		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
       		{
       			String str = (String)_adapter.getItem(position);
       			Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
       		}
       	});
	}
	
	public TaskLists getTaskLists()
	{
		TaskLists taskLists = null;

		try 
		{
			taskLists = new GetTaskLists().execute().get();
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		
		return taskLists;
	}
	
	private class GetTaskLists extends AsyncTask<Void, Void, TaskLists>
	{
		@Override
		protected TaskLists doInBackground(Void... params)
		{
			Tasks service = new Tasks.Builder(
								new NetHttpTransport(), 
								new JacksonFactory(), 
								Oauth2Service.getInstance().get_credential())
								.setApplicationName("MYTask")
								.build();

			TaskLists taskLists = null;
			try
			{
				taskLists = service.tasklists().list().execute();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			return taskLists;
		}
	}
	
	public void addTaskList(TaskList taskList)
	{
		try
		{
			new AddTaskList().execute(taskList).get();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
	}
	
	private class AddTaskList extends AsyncTask<TaskList, Void, Void>
	{
		@Override
		protected Void doInBackground(TaskList... taskList)
		{
			Tasks service = new Tasks.Builder(
								new NetHttpTransport(), 
								new JacksonFactory(), 
								Oauth2Service.getInstance().get_credential())
								.setApplicationName("MYTask")
								.build();
			
			try
			{
				service.tasklists().insert(taskList[0]).execute();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			return null;
		}
	}
}
