/*
 * 아직 미완성 상태이며, 추가 개발 예정입니다.
 * com.google.api.services.tasks.model.Tasks
 * com.google.api.services.tasks.Tasks
 * 이름이 겹쳐서 아래의 것을 import하였고, 위의 Tasks는 그때그때 경로를 설정하는 방식으로 사용하였습니다.
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
 * 구글의 Tasks 서비스를 이용하기 위한 클래스입니다.
 * @author 신동환
 */
public class TasksService extends Activity
{	
	//리스너들이 tasksList의 ID값을 사용해야 하는 경우가 존재하여 변수로 저장하고 사용하기로 하였습니다.
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
	 * 초기화를 하기 위한 함수입니다.</br>
	 * taskList의 목록을 받아와서 Spinner를 만든 뒤</br>
	 * 할 일 추가 버튼, 새로고침 버튼을 초기화 합니다.
	 */
	private void init()
	{
		initTaskListsSpinner(getTaskLists());
		initAddTaskButton();
		initUpdateButton();		
	}
	
	/**
	 * 할 일을 추가하는 버튼입니다.
	 */
	private void initAddTaskButton()
	{
		Button addTaskButton = (Button)findViewById(R.id.button1);
		addTaskButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				Toast.makeText(getApplicationContext(), "할 일이 추가 되었습니다", Toast.LENGTH_SHORT).show();
				addTask(get_selectedTaskListID());
			}
		});
	}
	
	/**
	 * 새로고침을 하는 버튼입니다.
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
	 * 스피너의 taskLists 중에서 선택된 하나의 taskList의 task들을 리스트뷰로 보여주기 위한 함수입니다.
	 * @param tasks 현재 스피너에서 선택되어져있는 taskList
	 */
	private void initTasksListListView(com.google.api.services.tasks.model.Tasks tasks)
	{	
		ArrayList<String> arrayList = new ArrayList<String>();
		
		//task들의 이름만 따로 저장
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
       			//선택된 할 일의 제목을 토스트로 보여줌.
       			//새로운 액티비티를 만들어서 할 일을 수정할 수 있게 변경해야 합니다.
       			Toast.makeText(getBaseContext(), adapter.getItem(position).toString(), Toast.LENGTH_SHORT).show();
       		}
       	});
	}
	
	/**
	 * taskLists를 스피너로 만드는 함수입니다.
	 * @param taskLists 할 일 목록들입니다.
	 */
	private void initTaskListsSpinner(final TaskLists taskLists)
	{
		ArrayList<String> arrayList = new ArrayList<String>();
		
		//할 일 목록들에서 각 목록의 제목만 따로 저장합니다.
       	for (TaskList taskList : taskLists.getItems()) arrayList.add(taskList.getTitle());
       	
		final ArrayAdapter<String> adapterSpinner = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayList);
		adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        
		Spinner spinner = (Spinner) findViewById(R.id.spinner1);
		spinner.setAdapter(adapterSpinner);
		spinner.setVisibility(View.VISIBLE);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			//다른 할 일 목록을 보기 위해 스피너의 select를 변경할 때 입니다.
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				//선택된 할 일 목록이 어느것인지 찾습니다.
				for (TaskList taskList : taskLists.getItems())
				{
					//찾았으면
					if(taskList.getTitle() == adapterSpinner.getItem(position))
					{
						//클래스 내에서 사용하면 로컬 변수에 저장하고, 바뀐 할 일 목록을 보여줍니다.
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
	 * 할 일 목록들을 얻어오는 함수입니다.
	 * @return 할 일 목록들을 반환합니다.
	 */
	private TaskLists getTaskLists()
	{
		TaskLists taskLists = null;

		try 
		{
			//할 일 목록을 받아옵니다.
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
	 * 네트워크 처리이기 때문에 AsyncTask로 처리하였습니다.</br>
	 * 할 일 목록들을 받아오는 클래스 입니다.
	 * @author 신동환
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
				// 할 일 목록들을 받아옵니다.
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
	 * 할 일 목록을 추가하는 함수입니다.</br>
	 * 현재 할 일 목록의 할 일이 없을경우(empty) 널 포인트 예외처리가 발생하여 해결 방안을 찾고 있는 중입니다.</br>
	 * 해결 방안을 찾기 전까지는 이 함수는 사용되어서는 안됩니다.
	 */
	private void addTaskList()
	{
		//새로운 할 일 목록입니다.
		TaskList taskList = new TaskList();
		taskList.setTitle("테스트입니다.");

		//할 일 목록을 추가합니다.
		new AddTaskList().execute(taskList);
	}
	
	/**
	 * 네트워크 처리이기 때문에 AsyncTask로 처리하였습니다.</br>
	 * 할 일 목록을 추가하는 클래스입니다.
	 * @author 신동환
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
				//할 일 목록을 추가합니다.
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
	 * 할 일 목록을 가져오는 함수 입니다.
	 * @param tasksId 가져올 할 일 목록의 ID입니다.
	 * @return 가져온 할 일 목록을 반환합니다.
	 */
	private com.google.api.services.tasks.model.Tasks getTasksList(String tasksId)
	{
		com.google.api.services.tasks.model.Tasks tasksList = null;

		try 
		{
			//할 일 목록을 가져옵니다.
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
	 * 네트워크 처리이기 때문에 AsyncTask로 처리하였습니다.</br>
	 * 할 일 목록을 가져오는 클래스입니다.
	 * @author 신동환
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
				// 할 일 목록을 가져옵니다.
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
	 * 할 일을 추가하는 함수입니다.
	 * @param tasksId 할 일을 추가할, 할 일 목록의 ID입니다.
	 */
	private void addTask(String tasksId)
	{
		//새로 추가할 할 일입니다.
		Task task = new Task();
		task.setTitle("New Task");
		task.setNotes("Please complete me");
		task.setDue(new DateTime(System.currentTimeMillis() + 3600000));
		
		//할 일 목록(tasksID)에 할 일(task)을 추가합니다.
		new AddTask().execute(tasksId, task);
	}
	
	/**
	 * 네트워크 처리이기 때문에 AsyncTask로 처리하였습니다.</br>
	 * 할 일을 추가하는 클래스입니다.
	 * @author 신동환
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
				//할 일을 추가합니다.
				service.tasks().insert((String) params[0], (Task) params[1]).execute();
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	//get, set 함수입니다.
	public String get_selectedTaskListID() {return _selectedTaskListID;}
	
	public void set_selectedTaskListID(String _selectedTaskListID) {this._selectedTaskListID = _selectedTaskListID;}
}
