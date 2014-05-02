package Oauth2;

import java.io.IOException;

import android.os.AsyncTask;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

public class AccessTokenRequest extends AsyncTask<String, Void, GoogleTokenResponse>{
	
	private HttpTransport httpTransport;
	private JacksonFactory jsonFactory;
	
	@Override
	protected void onPreExecute() {
		
		httpTransport = new NetHttpTransport();
		jsonFactory = new JacksonFactory();
	}
	
	@Override
	protected GoogleTokenResponse doInBackground(String... code) {

		GoogleTokenResponse accessToken = null;
    	
		try {
			accessToken = new GoogleAuthorizationCodeTokenRequest(
							httpTransport, 
							jsonFactory, 
							Oauth2Info.CLIENT_ID, 
							Oauth2Info.CLIENT_SECRET, 
							code[0],
							Oauth2Info.REDIRECT_URI).execute();
		} catch(IOException e) {
			e.printStackTrace();
		}
         
		return accessToken;
	}
}
