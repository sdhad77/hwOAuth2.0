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
//		initTasksListListView(getTaskLists());
		
	}
	
	private void initTasksListListView(ArrayList<String> arrayList)
	{
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
	
	private void initTaskListsSpinner(ArrayList<String> arrayList)
	{
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
				Toast.makeText(getApplicationContext(), adapterSpinner.getItem(position) + "을/를 선택 했습니다.", Toast.LENGTH_LONG).show();
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
	}
	
	public ArrayList<String> getTaskLists()
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
		
		ArrayList<String> _arrayList = new ArrayList<String>();
       	for (TaskList taskList : taskLists.getItems()) 
       	{
       		_arrayList.add(taskList.getTitle());
       	}
		
		return _arrayList;
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
