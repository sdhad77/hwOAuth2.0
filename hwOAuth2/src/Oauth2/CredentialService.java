package Oauth2;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

public class CredentialService {
	
	private HttpTransport httpTransport;
	private JacksonFactory jsonFactory;
	
	public CredentialService() {
		
		httpTransport = new NetHttpTransport();
		jsonFactory = new JacksonFactory();
	}
	
	public GoogleCredential makeCredential(GoogleTokenResponse token) {
		
		GoogleCredential credential = new GoogleCredential.Builder()
										.setTransport(httpTransport)
										.setJsonFactory(jsonFactory)
										.setClientSecrets(Oauth2Info.CLIENT_ID, Oauth2Info.CLIENT_SECRET)
										.build();

		credential.setAccessToken(token.getAccessToken());
		credential.setRefreshToken(token.getRefreshToken());
		
		return credential;
	}
}
