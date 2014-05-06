package Oauth2;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import Oauth2.Oauth2Info.Service;
import SharedPreferences.PrefService;
import android.os.AsyncTask;

import com.example.hwoauth2.googleapi.tasks.TasksService;
import com.example.hwoauth2.googleapi.userinfo.UserInfoService;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * Oauth2Info Ŭ������ �������� �̿��Ͽ� ���� Oauth2 ���񽺵��� �����մϴ�.
 * @author �ŵ�ȯ
 */
public class Oauth2Service 
{
	//AccessToken�� Credential�� Ŭ���� ������ �����Ӱ� ���� �ֵ��� �Ͽ����ϴ�.
	private GoogleCredential _credential;
	private String _accessToken;
	
	private static Oauth2Service _instance;

	private Oauth2Service()
	{
		_credential = null;
		_accessToken = null;
	}
	
	public static Oauth2Service getInstance()
	{
		if( _instance == null )
		{
			_instance = new Oauth2Service();
		}

		return _instance;
	}
	
	/**
	 * ���� �ڵ带 ���� ���� Url �ּҸ� ����ϴ�.
	 * @return ���� code
	 */
	public String getAuthorizationUrl()
	{
		String str = new GoogleAuthorizationCodeRequestUrl(
							Oauth2Info.getInstance().getCLIENT_ID(), 
							Oauth2Info.getInstance().getREDIRECT_URI(), 
							Oauth2Info.getInstance().getSCOPE())
							.build();
		
		return str;
	}
	
	/**
	 * code�� �̿��� Credential�� �������ϴ�.
	 * @param code accessToken�� �ޱ� ���� �ʿ��� code
	 * @return ��ū ������ ����� Credential
	 */
	public GoogleCredential codeToCredential(String code)
	{
		_credential = makeCredential(accessTokenRequest(code));
		storeToken();
		return _credential;
	}
	
	/**
	 * accessToken�� �̹� ������ �� Credential�� ����� �Լ��Դϴ�.
	 */
	public void makeCredential()
	{
		GoogleCredential credential = new GoogleCredential.Builder()
										.setTransport(new NetHttpTransport())
										.setJsonFactory(new JacksonFactory())
										.setClientSecrets(Oauth2Info.getInstance().getCLIENT_ID(), 
												          Oauth2Info.getInstance().getCLIENT_SECRET())
										.build();

		credential.setAccessToken(get_accessToken());
		
		_credential = credential;
	}
	
	/**
	 * Google���� �� Token�� �̿��Ͽ� Credential�� ����ϴ�.
	 * @param response ���� �ڵ带 �̿��� ���۷� ���� ���� ������. ��ū ������ ����� ����.
	 * @return ��ū ������ ����� Credential
	 */
	public GoogleCredential makeCredential(GoogleTokenResponse response)
	{
		GoogleCredential credential = new GoogleCredential.Builder()
										.setTransport(new NetHttpTransport())
										.setJsonFactory(new JacksonFactory())
										.setClientSecrets(Oauth2Info.getInstance().getCLIENT_ID(), 
												          Oauth2Info.getInstance().getCLIENT_SECRET())
										.build();

		credential.setAccessToken(response.getAccessToken());
		credential.setRefreshToken(response.getRefreshToken());
		
		return credential;
	}
	
	/**
	 * ���� �ڵ带 �̿��Ͽ� ��ū�� ���� �Լ��Դϴ�.
	 * @param code ���� �ڵ�
	 * @return �ڵ带 �̿��� �� ��ū������
	 */
	public GoogleTokenResponse accessTokenRequest(String code)
	{
		GoogleTokenResponse response = null;
    	
		try 
		{
			response = new AccessTokenRequest().execute(code).get();
		} 
		catch (InterruptedException e)
		{
			e.printStackTrace();
		} 
		catch (ExecutionException e) 
		{
			e.printStackTrace();
		}
         
		return response;
	}
	
	/**
	 * ��Ʈ��ũ ���� �۾��� ���� �����忡�� �����ϸ� �ȵǱ� ������ AsyncTask�� �̿��մϴ�.</br>
	 * �ڵ带 �̿��� ��ū �������� ���� Ŭ�����Դϴ�.
	 * @author �ŵ�ȯ
	 */
	private class AccessTokenRequest extends AsyncTask<String, Void, GoogleTokenResponse>
	{
		private HttpTransport httpTransport;
		private JacksonFactory jsonFactory;
		
		@Override
		protected void onPreExecute()
		{
			httpTransport = new NetHttpTransport();
			jsonFactory = new JacksonFactory();
		}
		
		@Override
		protected GoogleTokenResponse doInBackground(String... code)
		{
			GoogleTokenResponse response = null;
	    	
			try 
			{
				response = new GoogleAuthorizationCodeTokenRequest(
								httpTransport, 
								jsonFactory, 
								Oauth2Info.getInstance().getCLIENT_ID(), 
								Oauth2Info.getInstance().getCLIENT_SECRET(), 
								code[0],
								Oauth2Info.getInstance().getREDIRECT_URI()).execute();
			} 
			catch(IOException e) 
			{
				e.printStackTrace();
			}
			
			httpTransport = null;
			jsonFactory = null;
	         
			return response;
		}
	}
	
	/**
	 * ��ū(Credential)�� �̿��� ���� ���񽺸� �̿��ϴ� �Լ��Դϴ�.</br>
	 * Ŭ���� ���� �ִ� Credential�� �̿��ϴ� �Լ��Դϴ�.
	 * @param endPointUrl ������ url �ּ�
	 * @return ���۷� ���� ���� ���� ���ڿ�
	 */
	public String accessProtectedResource(String endPointUrl)
	{
		String str = null;

		try
		{
			str = new AccessProtectedResource().execute(_credential, endPointUrl).get();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}

		return str;
	}
	
	/**
	 * ��ū(Credential)�� �̿��� ���� ���񽺸� �̿��ϴ� �Լ��Դϴ�.</br>
	 * Credential�� �Ű������� �޾Ƽ� �̿��ϴ� �Լ��Դϴ�.
	 * @param credential ��ū �������� ������ִ� Credential
	 * @param endPointUrl ������ url �ּ�
	 * @return ���۷� ���� ���۹��� ���ڿ� ����
	 */
	public String accessProtectedResource(GoogleCredential credential, String endPointUrl)
	{
		String str = null;

		try
		{
			str = new AccessProtectedResource().execute(credential, endPointUrl).get();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		catch (ExecutionException e)
		{
			e.printStackTrace();
		}

		return str;
	}
	
	/**
	 * ��Ʈ��ũ ���� �۾��� ���� �����忡�� �����ϸ� �ȵǱ� ������ AsyncTask�� �̿��մϴ�.</br>
	 * endPoint url �ּҸ� �̿��Ͽ� ���۷� ���� �����͸� ���� Ŭ�����Դϴ�.
	 * @author �ŵ�ȯ
	 */
	private class AccessProtectedResource extends AsyncTask<Object, Void, String>
	{
		private HttpTransport httpTransport;
		private GoogleCredential _credential;

		@Override
		protected void onPreExecute() 
		{
			httpTransport = new NetHttpTransport();
		}

		//Object�� ù��° �����ʹ� Credential, �ι�° �����ʹ� EndPointUrl �Դϴ�.
		@Override
		protected String doInBackground(Object... params) 
		{
			String str = null;
			_credential = (GoogleCredential) params[0];
	    	
			HttpRequestFactory rf = httpTransport.createRequestFactory(_credential);
			GenericUrl endPoint = new GenericUrl((String) params[1]);
	 
			HttpResponse response;
	     	
			try
			{
				response = rf.buildGetRequest(endPoint).execute();
				str = response.parseAsString();
			} 
			catch(IOException e)
			{
				e.printStackTrace();
			}

			return str;
		}
	}
	
	/**
	 * ���� ����ϰ� �ִ� ������ Ŭ������ ��ȯ�մϴ�.</br>
	 * ��ȯ�� Ŭ������ ���ο� ��Ƽ��Ƽ�� �����մϴ�.
	 * @return ���� ����ϰ� �ִ� ������ Ŭ����
	 */
	public Class<?> getServiceClass()
	{
		if     (Oauth2Info.getInstance().getSelectService() == Oauth2Info.Service.USERINFO) return UserInfoService.class;
		else if(Oauth2Info.getInstance().getSelectService() == Oauth2Info.Service.TASKS)    return TasksService.class;
		
		return null;
	}
	
	/**
	 * ���õ� ���񽺷� �����ϴ� �۾��� �ϴ� �Լ��Դϴ�.</br>
	 * ���� ���� �������� ������, ����� accessToken�� �о�ɴϴ�.
	 * @param service ���õ� ����
	 */
	public void setSelectService(Service service)
	{
		//����� ���� ���񽺸� �����Ͽ��� ���
		if(service == Oauth2Info.Service.USERINFO)
		{
			//���� ������� ���񽺸� ����� ������ ����.
			//EndPointUrl, Scope ������ ����� ���� ���񽺸� �̿��� �� �ְԲ� ������.
			Oauth2Info.getInstance().setSelectService(Oauth2Info.Service.USERINFO);			
			Oauth2Info.getInstance().setENDPOINT_URL(Oauth2Info.getInstance().getENDPOINT_UserInfo());
			Oauth2Info.getInstance().setSCOPE(Oauth2Info.getInstance().getSCOPE_UserInfo());
		}
		//Tasks ���񽺸� �����Ͽ��� ���
		else if(service == Oauth2Info.Service.TASKS)
		{
			Oauth2Info.getInstance().setSelectService(Oauth2Info.Service.TASKS);
			Oauth2Info.getInstance().setENDPOINT_URL(Oauth2Info.getInstance().getENDPOINT_Tasks());
			Oauth2Info.getInstance().setSCOPE(Oauth2Info.getInstance().getSCOPE_Tasks());
		}
		//����Ǿ��ִ� accessToken�� �о�ͼ� Ŭ�������� _accessToken������ ������.
		//����Ǿ��ִ� ��ū�� ���� ��� "token_is_not_exist" ���ڿ��� �����.
		set_accessToken(loadAccessToken());
	}
	
	/**
	 * ��ū�� �����ϴ� �Լ�. ���� ���� ��ū�� ���� �����մϴ�.
	 */
	public void storeToken()
	{
		if(Oauth2Info.getInstance().getSelectService() == Oauth2Info.Service.USERINFO)
		{
			PrefService.getInstance().put(PrefService.PREF_ACCESS_TOKEN_USERINFO, _credential.getAccessToken());
			PrefService.getInstance().put(PrefService.PREF_REFRESH_TOKEN_USERINFO, _credential.getRefreshToken());
		}
		else if(Oauth2Info.getInstance().getSelectService() == Oauth2Info.Service.TASKS)
		{
			PrefService.getInstance().put(PrefService.PREF_ACCESS_TOKEN_TASKS, _credential.getAccessToken());
			PrefService.getInstance().put(PrefService.PREF_REFRESH_TOKEN_TASKS, _credential.getRefreshToken());
		}
	}

	/**
	 * ��ū�� �о���� �Լ�. ���� ���� ��ū�� ���� �о�ɴϴ�.
	 * @return ���ڿ��� ��ȯ�ϴµ�, ��ū�� ������ ��� ��ū�� ��ȯ�ϰ�, ��ū�� �������� ������� "token_is_not_exist" ���ڿ��� ��ȯ�մϴ�.
	 */
	public String loadAccessToken()
	{
		if(Oauth2Info.getInstance().getSelectService() == Oauth2Info.Service.USERINFO)
		{
			return PrefService.getInstance().getValue(PrefService.PREF_ACCESS_TOKEN_USERINFO, PrefService.PREF_TOKEN_IS_NOT_EXIST);
		}
		else if(Oauth2Info.getInstance().getSelectService() == Oauth2Info.Service.TASKS)
		{
			return PrefService.getInstance().getValue(PrefService.PREF_ACCESS_TOKEN_TASKS, PrefService.PREF_TOKEN_IS_NOT_EXIST);
		}
		
		return PrefService.PREF_TOKEN_IS_NOT_EXIST;
	}
	
	//get, set �Լ��� �Դϴ�.
	public GoogleCredential get_credential() {return _credential;}
	public String get_accessToken()          {return _accessToken;}

	public void set_credential(GoogleCredential _credential) {this._credential = _credential;}
	public void set_accessToken(String _accessToken)         {this._accessToken = _accessToken;}
}
