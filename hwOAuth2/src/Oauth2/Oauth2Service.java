package Oauth2;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import android.os.AsyncTask;

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
	
	public GoogleCredential get_credential()
	{
		return _credential;
	}

	public void set_credential(GoogleCredential _credential)
	{
		this._credential = _credential;
	}
	
	public String getAuthorizationUrl()
	{
		String str = new GoogleAuthorizationCodeRequestUrl(
						Oauth2Info.CLIENT_ID, 
						Oauth2Info.REDIRECT_URI, 
						Oauth2Info.SCOPE)
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
										.setClientSecrets(Oauth2Info.CLIENT_ID, Oauth2Info.CLIENT_SECRET)
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
								Oauth2Info.CLIENT_ID, 
								Oauth2Info.CLIENT_SECRET, 
								code[0],
								Oauth2Info.REDIRECT_URI).execute();
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
}
