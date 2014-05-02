package Oauth2;

import java.io.IOException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;

import android.os.AsyncTask;

public class AccessProtectedResource extends AsyncTask<Credential, Void, String>{
	
	private HttpTransport httpTransport;
	
	@Override
	protected void onPreExecute() {

		httpTransport = new NetHttpTransport();
	}
	
	@Override
	protected String doInBackground(Credential... credential) {

		String str = null;
    	
		HttpRequestFactory rf = httpTransport.createRequestFactory(credential[0].getCredential());
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
