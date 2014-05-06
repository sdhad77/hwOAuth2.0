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
 * Oauth2Info 클래스의 정보들을 이용하여 각종 Oauth2 서비스들을 제공합니다.
 * @author 신동환
 */
public class Oauth2Service 
{
	//AccessToken과 Credential을 클래스 내에서 자유롭게 쓸수 있도록 하였습니다.
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
	 * 접근 코드를 얻어내기 위한 Url 주소를 만듭니다.
	 * @return 접근 code
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
	 * code를 이용해 Credential을 만들어냅니다.
	 * @param code accessToken을 받기 위해 필요한 code
	 * @return 토큰 정보가 저장된 Credential
	 */
	public GoogleCredential codeToCredential(String code)
	{
		_credential = makeCredential(accessTokenRequest(code));
		storeToken();
		return _credential;
	}
	
	/**
	 * accessToken이 이미 존재할 때 Credential을 만드는 함수입니다.
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
	 * Google에서 얻어낸 Token을 이용하여 Credential을 만듭니다.
	 * @param response 접근 코드를 이용해 구글로 부터 얻은 데이터. 토큰 정보가 저장되 있음.
	 * @return 토큰 정보가 저장된 Credential
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
	 * 접근 코드를 이용하여 토큰을 얻어내는 함수입니다.
	 * @param code 접근 코드
	 * @return 코드를 이용해 얻어낸 토큰정보들
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
	 * 네트워크 관련 작업은 메인 스레드에서 진행하면 안되기 때문에 AsyncTask를 이용합니다.</br>
	 * 코드를 이용해 토큰 정보들을 얻어내는 클래스입니다.
	 * @author 신동환
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
	 * 토큰(Credential)을 이용해 구글 서비스를 이용하는 함수입니다.</br>
	 * 클래스 내에 있는 Credential을 이용하는 함수입니다.
	 * @param endPointUrl 접근할 url 주소
	 * @return 구글로 부터 전송 받은 문자열
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
	 * 토큰(Credential)을 이용해 구글 서비스를 이용하는 함수입니다.</br>
	 * Credential을 매개변수로 받아서 이용하는 함수입니다.
	 * @param credential 토큰 정보들이 저장되있는 Credential
	 * @param endPointUrl 접근할 url 주소
	 * @return 구글로 부터 전송받은 문자열 정보
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
	 * 네트워크 관련 작업은 메인 스레드에서 진행하면 안되기 때문에 AsyncTask를 이용합니다.</br>
	 * endPoint url 주소를 이용하여 구글로 부터 데이터를 얻어내는 클래스입니다.
	 * @author 신동환
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

		//Object의 첫번째 데이터는 Credential, 두번째 데이터는 EndPointUrl 입니다.
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
	 * 현재 사용하고 있는 서비스의 클래스를 반환합니다.</br>
	 * 반환된 클래스로 새로운 액티비티를 생성합니다.
	 * @return 현재 사용하고 있는 서비스의 클래스
	 */
	public Class<?> getServiceClass()
	{
		if     (Oauth2Info.getInstance().getSelectService() == Oauth2Info.Service.USERINFO) return UserInfoService.class;
		else if(Oauth2Info.getInstance().getSelectService() == Oauth2Info.Service.TASKS)    return TasksService.class;
		
		return null;
	}
	
	/**
	 * 선택된 서비스로 세팅하는 작업을 하는 함수입니다.</br>
	 * 서비스 별로 나뉘어져 있으며, 저장된 accessToken을 읽어옵니다.
	 * @param service 선택된 서비스
	 */
	public void setSelectService(Service service)
	{
		//사용자 정보 서비스를 선택하였을 경우
		if(service == Oauth2Info.Service.USERINFO)
		{
			//현재 사용중인 서비스를 사용자 정보로 세팅.
			//EndPointUrl, Scope 정보를 사용자 정보 서비스를 이용할 수 있게끔 세팅함.
			Oauth2Info.getInstance().setSelectService(Oauth2Info.Service.USERINFO);			
			Oauth2Info.getInstance().setENDPOINT_URL(Oauth2Info.getInstance().getENDPOINT_UserInfo());
			Oauth2Info.getInstance().setSCOPE(Oauth2Info.getInstance().getSCOPE_UserInfo());
		}
		//Tasks 서비스를 선택하였을 경우
		else if(service == Oauth2Info.Service.TASKS)
		{
			Oauth2Info.getInstance().setSelectService(Oauth2Info.Service.TASKS);
			Oauth2Info.getInstance().setENDPOINT_URL(Oauth2Info.getInstance().getENDPOINT_Tasks());
			Oauth2Info.getInstance().setSCOPE(Oauth2Info.getInstance().getSCOPE_Tasks());
		}
		//저장되어있는 accessToken을 읽어와서 클래스내의 _accessToken변수에 저장함.
		//저장되어있는 토큰이 없을 경우 "token_is_not_exist" 문자열이 저장됨.
		set_accessToken(loadAccessToken());
	}
	
	/**
	 * 토큰을 저장하는 함수. 서비스 별로 토큰을 따로 저장합니다.
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
	 * 토큰을 읽어오는 함수. 서비스 별로 토큰을 따로 읽어옵니다.
	 * @return 문자열을 반환하는데, 토큰이 존재할 경우 토큰을 반환하고, 토큰이 존재하지 않을경우 "token_is_not_exist" 문자열을 반환합니다.
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
	
	//get, set 함수들 입니다.
	public GoogleCredential get_credential() {return _credential;}
	public String get_accessToken()          {return _accessToken;}

	public void set_credential(GoogleCredential _credential) {this._credential = _credential;}
	public void set_accessToken(String _accessToken)         {this._accessToken = _accessToken;}
}
