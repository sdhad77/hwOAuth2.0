/*
 * ���� �̿ϼ� �����̸�, �߰� ���� �����Դϴ�.
 * com.google.api.services.tasks.model.Tasks
 * com.google.api.services.tasks.Tasks
 * �̸��� ���ļ� �Ʒ��� ���� import�Ͽ���, ���� Tasks�� �׶��׶� ��θ� �����ϴ� ������� ����Ͽ����ϴ�.
*/

package com.example.hwoauth2.googleapi.tasks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import Oauth2.Oauth2Service;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.hwoauth2.R;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;

/**
 * ������ Tasks ���񽺸� �̿��ϱ� ���� Ŭ�����Դϴ�.
 * @author �ŵ�ȯ
 */
public class TasksService extends Activity
{	
	//�����ʵ��� tasksList�� ID���� ����ؾ� �ϴ� ��찡 �����Ͽ� ������ �����ϰ� ����ϱ�� �Ͽ����ϴ�.
	private String _selectedTaskListID;

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
		
		init();
	}
	
	/**
	 * �ʱ�ȭ�� �ϱ� ���� �Լ��Դϴ�.</br>
	 * taskList�� ����� �޾ƿͼ� Spinner�� ���� ��</br>
	 * �� �� �߰� ��ư, ���ΰ�ħ ��ư�� �ʱ�ȭ �մϴ�.
	 */
	private void init()
	{
		initTaskListsSpinner(getTaskLists());
		initAddTaskButton();
		initUpdateButton();		
	}
	
	/**
	 * �� ���� �߰��ϴ� ��ư�Դϴ�.
	 */
	private void initAddTaskButton()
	{
		Button addTaskButton = (Button)findViewById(R.id.button1);
		addTaskButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getApplicationContext(), "�� ���� �߰� �Ǿ����ϴ�", Toast.LENGTH_SHORT).show();
				addTask(get_selectedTaskListID());
			}
		});
	}
	
	/**
	 * ���ΰ�ħ�� �ϴ� ��ư�Դϴ�.
	 */
	private void initUpdateButton()
	{
		Button updateButton = (Button)findViewById(R.id.button2);
		updateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				initTasksListListView(getTasksList(get_selectedTaskListID()));
			}
		});
	}
	
	/**
	 * ���ǳ��� taskLists �߿��� ���õ� �ϳ��� taskList�� task���� ����Ʈ��� �����ֱ� ���� �Լ��Դϴ�.
	 * @param tasks ���� ���ǳʿ��� ���õǾ����ִ� taskList
	 */
	private void initTasksListListView(com.google.api.services.tasks.model.Tasks tasks)
	{	
		ArrayList<String> arrayList = new ArrayList<String>();
		
		//task���� �̸��� ���� ����
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
       			//���õ� �� ���� ������ �佺Ʈ�� ������.
       			//���ο� ��Ƽ��Ƽ�� ���� �� ���� ������ �� �ְ� �����ؾ� �մϴ�.
       			Toast.makeText(getBaseContext(), adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
       		}
       	});
	}
	
	/**
	 * taskLists�� ���ǳʷ� ����� �Լ��Դϴ�.
	 * @param taskLists �� �� ��ϵ��Դϴ�.
	 */
	private void initTaskListsSpinner(final TaskLists taskLists)
	{
		ArrayList<String> arrayList = new ArrayList<String>();
		
		//�� �� ��ϵ鿡�� �� ����� ���� ���� �����մϴ�.
       	for (TaskList taskList : taskLists.getItems()) arrayList.add(taskList.getTitle());
       	
		final ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
		adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		spinner.setAdapter(adapterSpinner);
		spinner.setVisibility(View.VISIBLE);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			//�ٸ� �� �� ����� ���� ���� ���ǳ��� select�� ������ �� �Դϴ�.
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				//���õ� �� �� ����� ��������� ã���ϴ�.
				for (TaskList taskList : taskLists.getItems())
				{
					//ã������
					if(taskList.getTitle() == adapterSpinner.getItem(position))
					{
						//Ŭ���� ������ ����ϸ� ���� ������ �����ϰ�, �ٲ� �� �� ����� �����ݴϴ�.
						set_selectedTaskListID(taskList.getId());
						initTasksListListView(getTasksList(get_selectedTaskListID()));
					}
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
	}
	
	/**
	 * �� �� ��ϵ��� ������ �Լ��Դϴ�.
	 * @return �� �� ��ϵ��� ��ȯ�մϴ�.
	 */
	private TaskLists getTaskLists()
	{
		TaskLists taskLists = null;

		try 
		{
			//�� �� ����� �޾ƿɴϴ�.
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
	
	/**
	 * ��Ʈ��ũ ó���̱� ������ AsyncTask�� ó���Ͽ����ϴ�.</br>
	 * �� �� ��ϵ��� �޾ƿ��� Ŭ���� �Դϴ�.
	 * @author �ŵ�ȯ
	 */
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
				// �� �� ��ϵ��� �޾ƿɴϴ�.
				taskLists = service.tasklists().list().execute();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			return taskLists;
		}
	}
	
	/**
	 * �� �� ����� �߰��ϴ� �Լ��Դϴ�.</br>
	 * ���� �� �� ����� �� ���� �������(empty) �� ����Ʈ ����ó���� �߻��Ͽ� �ذ� ����� ã�� �ִ� ���Դϴ�.</br>
	 * �ذ� ����� ã�� �������� �� �Լ��� ���Ǿ�� �ȵ˴ϴ�.
	 */
	private void addTaskList()
	{
		//���ο� �� �� ����Դϴ�.
		TaskList taskList = new TaskList();
		taskList.setTitle("�׽�Ʈ�Դϴ�.");

		//�� �� ����� �߰��մϴ�.
		new AddTaskList().execute(taskList);
	}
	
	/**
	 * ��Ʈ��ũ ó���̱� ������ AsyncTask�� ó���Ͽ����ϴ�.</br>
	 * �� �� ����� �߰��ϴ� Ŭ�����Դϴ�.
	 * @author �ŵ�ȯ
	 */
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
				//�� �� ����� �߰��մϴ�.
				service.tasklists().insert(taskList[0]).execute();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	/**
	 * �� �� ����� �������� �Լ� �Դϴ�.
	 * @param tasksId ������ �� �� ����� ID�Դϴ�.
	 * @return ������ �� �� ����� ��ȯ�մϴ�.
	 */
	private com.google.api.services.tasks.model.Tasks getTasksList(String tasksId)
	{
		com.google.api.services.tasks.model.Tasks tasksList = null;

		try 
		{
			//�� �� ����� �����ɴϴ�.
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
	
	/**
	 * ��Ʈ��ũ ó���̱� ������ AsyncTask�� ó���Ͽ����ϴ�.</br>
	 * �� �� ����� �������� Ŭ�����Դϴ�.
	 * @author �ŵ�ȯ
	 */
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
				// �� �� ����� �����ɴϴ�.
				tasksList = service.tasks().list(params[0]).execute();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			return tasksList;
		}
	}
	
	/**
	 * �� ���� �߰��ϴ� �Լ��Դϴ�.
	 * @param tasksId �� ���� �߰���, �� �� ����� ID�Դϴ�.
	 */
	private void addTask(String tasksId)
	{
		//���� �߰��� �� ���Դϴ�.
		Task task = new Task();
		task.setTitle("New Task");
		task.setNotes("Please complete me");
		task.setDue(new DateTime(System.currentTimeMillis() + 3600000));
		
		//�� �� ���(tasksID)�� �� ��(task)�� �߰��մϴ�.
		new AddTask().execute(tasksId, task);
	}
	
	/**
	 * ��Ʈ��ũ ó���̱� ������ AsyncTask�� ó���Ͽ����ϴ�.</br>
	 * �� ���� �߰��ϴ� Ŭ�����Դϴ�.
	 * @author �ŵ�ȯ
	 */
	private class AddTask extends AsyncTask<Object, Void, Void>
	{
		@Override
		protected Void doInBackground(Object... params)
		{
			Tasks service = new Tasks.Builder(
								new NetHttpTransport(), 
								new JacksonFactory(), 
								Oauth2Service.getInstance().get_credential())
								.setApplicationName("MYTask")
								.build();
			
			try
			{
				//�� ���� �߰��մϴ�.
				service.tasks().insert((String) params[0], (Task) params[1]).execute();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	//get, set �Լ��Դϴ�.
	public String get_selectedTaskListID() {return _selectedTaskListID;}
	
	public void set_selectedTaskListID(String _selectedTaskListID) {this._selectedTaskListID = _selectedTaskListID;}
}
