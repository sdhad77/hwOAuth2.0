package Oauth2;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

public class Credential {

	private GoogleCredential credential;
	
	public Credential() {
		credential = new GoogleCredential();
	}

	public GoogleCredential getCredential() {
		return credential;
	}

	public void setCredential(GoogleCredential credential) {
		this.credential = credential;
	}
}
