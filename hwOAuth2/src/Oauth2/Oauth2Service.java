package Oauth2;

import java.util.concurrent.ExecutionException;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;

public class Oauth2Service {
	
	public String getAuthorizationUrl() {
		
		String str = new GoogleAuthorizationCodeRequestUrl(Oauth2Info.CLIENT_ID, Oauth2Info.REDIRECT_URI, Oauth2Info.SCOPE).build();
		
		return str;
	}
	
	public GoogleTokenResponse accessTokenRequest(String code) {
		
		GoogleTokenResponse accessToken = null;
    	
		try {
			accessToken = new AccessTokenRequest().execute(code).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
		return accessToken;
	}
	
	public String accessProtectedResource(Credential credential) {

		String str = null;
		
		try {
			str = new AccessProtectedResource().execute(credential).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return str;
	}
}
