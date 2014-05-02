package Oauth2;

import java.io.IOException;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import android.os.AsyncTask;

public class AccessProtectedResource extends AsyncTask<GoogleTokenResponse, Void, String>{
	
	private HttpTransport httpTransport;
	private JacksonFactory jsonFactory;
	
	@Override
	protected void onPreExecute() {

		httpTransport = new NetHttpTransport();
		jsonFactory = new JacksonFactory();
	}
	
	@Override
	protected String doInBackground(GoogleTokenResponse... accessToken) {

		String str = null;
		GoogleCredential credential = new GoogleCredential.Builder()
										.setTransport(httpTransport)
										.setJsonFactory(jsonFactory)
										.setClientSecrets(Oauth2Info.CLIENT_ID, Oauth2Info.CLIENT_SECRET)
										.build();
     	
		credential.setAccessToken(accessToken[0].getAccessToken());
		credential.setRefreshToken(accessToken[0].getRefreshToken());
     	
		HttpRequestFactory rf = httpTransport.createRequestFactory(credential);
		GenericUrl endPoint = new GenericUrl(Oauth2Info.ENDPOINT_URL);
 
		HttpResponse response;
     	
		try {
			response = rf.buildGetRequest(endPoint).execute();
			str = response.parseAsString();
		} catch(IOException e) {
			e.printStackTrace();
		}

		return str;
	}
}
