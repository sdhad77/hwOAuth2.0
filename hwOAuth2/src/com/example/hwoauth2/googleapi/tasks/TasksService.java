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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.hwoauth2.R;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

public class TasksService extends Activity
{
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
		
		initTaskListsSpinner(getTaskLists());
	}
	
	private void initTasksListListView(com.google.api.services.tasks.model.Tasks tasks)
	{
		ArrayList<String> arrayList = new ArrayList<String>();
		
		for (Task task : tasks.getItems()) arrayList.add(task.getTitle());
		
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
       	
       	ListView listView = (ListView)findViewById(R.id.listView1);
       	listView.setAdapter(adapter);
       	listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
       	listView.setVisibility(View.VISIBLE);
       	
       	listView.setOnItemClickListener(new OnItemClickListener()
       	{
       		@Override
       		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
       		{
       			String str = (String)adapter.getItem(position);
       			Toast.makeText(getBaseContext(), str, Toast.LENGTH_SHORT).show();
       		}
       	});
	}
	
	private void initTaskListsSpinner(final TaskLists taskLists)
	{
		ArrayList<String> arrayList = new ArrayList<String>();
		
       	for (TaskList taskList : taskLists.getItems()) arrayList.add(taskList.getTitle());
       	
		final ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
		adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setPrompt("리스트를 선택해주세요");
        spinner.setAdapter(adapterSpinner);
        spinner.setVisibility(View.VISIBLE);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				for (TaskList taskList : taskLists.getItems())
				{
					if(taskList.getTitle() == adapterSpinner.getItem(position)) initTasksListListView(getTasksList(taskList.getId()));
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
	}
	
	private TaskLists getTaskLists()
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
	
	private void addTaskList(TaskList taskList)
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
	
	private com.google.api.services.tasks.model.Tasks getTasksList(String tasksId)
	{
		com.google.api.services.tasks.model.Tasks tasksList = null;

		try 
		{
			tasksList = new GetTasksList().execute(tasksId).get();
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}
		    	
		return tasksList;
	}
	
	private class GetTasksList extends AsyncTask<String, Void, com.google.api.services.tasks.model.Tasks>
	{
		@Override
		protected com.google.api.services.tasks.model.Tasks doInBackground(String... params)
		{
			Tasks service = new Tasks.Builder(
								new NetHttpTransport(), 
								new JacksonFactory(), 
								Oauth2Service.getInstance().get_credential())
								.setApplicationName("MYTask")
								.build();

			com.google.api.services.tasks.model.Tasks tasksList = null;
			try
			{
				tasksList = service.tasks().list(params[0]).execute();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			return tasksList;
		}
	}
}
