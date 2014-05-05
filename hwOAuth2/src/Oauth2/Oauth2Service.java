package Oauth2;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import Oauth2.Oauth2Info.Service;
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

public class Oauth2Service 
{
	private GoogleCredential _credential;
	
	private static Oauth2Service _instance;

	private Oauth2Service()
	{
		_credential = null;
	}
	
	public static Oauth2Service getInstance()
	{
		if( _instance == null )
		{
			_instance = new Oauth2Service();
		}

		return _instance;
	}
	
	public String getAuthorizationUrl()
	{
		String str = new GoogleAuthorizationCodeRequestUrl(
							Oauth2Info.getInstance().getCLIENT_ID(), 
							Oauth2Info.getInstance().getREDIRECT_URI(), 
							Oauth2Info.getInstance().getSCOPE())
							.build();
		
		return str;
	}
	
	public GoogleCredential codeToCredential(String code)
	{
		_credential = makeCredential(accessTokenRequest(code));
		return _credential;
	}
	
	public GoogleCredential makeCredential(GoogleTokenResponse token)
	{
		GoogleCredential credential = new GoogleCredential.Builder()
										.setTransport(new NetHttpTransport())
										.setJsonFactory(new JacksonFactory())
										.setClientSecrets(Oauth2Info.getInstance().getCLIENT_ID(), 
												          Oauth2Info.getInstance().getCLIENT_SECRET())
										.build();

		credential.setAccessToken(token.getAccessToken());
		credential.setRefreshToken(token.getRefreshToken());
		
		return credential;
	}
	
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
	         
			return response;
		}
	}
	
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
	
	private class AccessProtectedResource extends AsyncTask<Object, Void, String>
	{
		private HttpTransport httpTransport;
		private GoogleCredential _credential;

		@Override
		protected void onPreExecute() 
		{
			httpTransport = new NetHttpTransport();
		}

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
	
	public Class<?> getServiceClass()
	{
		if     (Oauth2Info.getInstance().getSelectService() == Oauth2Info.Service.USERINFO) return UserInfoService.class;
		else if(Oauth2Info.getInstance().getSelectService() == Oauth2Info.Service.TASKS)    return TasksService.class;
		
		return null;
	}
	
	public void setSelectService(Service service)
	{
		if(service == Oauth2Info.Service.USERINFO)
		{
			Oauth2Info.getInstance().setSelectService(Oauth2Info.Service.USERINFO);
			Oauth2Info.getInstance().setENDPOINT_URL(Oauth2Info.getInstance().getENDPOINT_UserInfo());
			Oauth2Info.getInstance().setSCOPE(Oauth2Info.getInstance().getSCOPE_UserInfo());
		}
		else if(service == Oauth2Info.Service.TASKS)
		{
			Oauth2Info.getInstance().setSelectService(Oauth2Info.Service.TASKS);
			Oauth2Info.getInstance().setENDPOINT_URL(Oauth2Info.getInstance().getENDPOINT_Tasks());
			Oauth2Info.getInstance().setSCOPE(Oauth2Info.getInstance().getSCOPE_Tasks());
		}
	}
	
	public GoogleCredential get_credential() {return _credential;}

	public void set_credential(GoogleCredential _credential) {this._credential = _credential;}
}
