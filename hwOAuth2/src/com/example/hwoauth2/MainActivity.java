package com.example.hwoauth2;

import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import Oauth2.Oauth2Info;
import Oauth2.Oauth2Service;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {
	
	private Oauth2Service _oauth2;
	private WebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, new PlaceholderFragment())
				.commit();
		}
        
		init();
        
		String oauth2Url = _oauth2.getAuthorizationUrl();
		webView.loadUrl(oauth2Url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);

			return rootView;
		}
	}
	
	private void init() {
		
		_oauth2 = new Oauth2Service();
		
		androidUniversalImageLoaderInit();
		
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		
		webView.setWebViewClient(new WebViewClient() {			
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if(url.startsWith(Oauth2Info.REDIRECT_URI)) {
					if (url.indexOf("code=") != -1) {
							
						String code = url.substring(Oauth2Info.REDIRECT_URI.length() + 7, url.length());
							
						GoogleTokenResponse accessToken = _oauth2.accessTokenRequest(code);
						String result = _oauth2.accessProtectedResource(accessToken);
							
						Object[] jsonTempObject = new Object[3];
				       	JsonParser jsonParser = new JsonParser();
				       	jsonTempObject = jsonParser.parse(result);
	
				       	drawView(jsonTempObject);
					}
					else if (url.indexOf("error=") != -1) {
						Toast.makeText(getApplicationContext(), "code error", Toast.LENGTH_LONG).show();
						webView.setVisibility(View.INVISIBLE);
					}
				}
			}
		});
	}
	
	private void androidUniversalImageLoaderInit() {
		   
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
												.threadPriority(Thread.NORM_PRIORITY - 2)
												.denyCacheImageMultipleSizesInMemory()
												.discCacheFileNameGenerator(new Md5FileNameGenerator())
												.tasksProcessingOrder(QueueProcessingType.LIFO)
												.writeDebugLogs()
												.build();

		ImageLoader.getInstance().init(config);
	}
   
	private void drawView(Object[] obj) {
	   
		WebView webView = (WebView) findViewById(R.id.webView1);
		webView.setVisibility(View.INVISIBLE);
		
		ImageView imgView = (ImageView)findViewById(R.id.imageView1);
		
		imgView.setVisibility(View.VISIBLE);
		ImageLoader.getInstance().displayImage(obj[2].toString(), imgView);
			
		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setVisibility(View.VISIBLE);
		textView.setText(obj[0].toString() + "\n" + obj[1].toString());
	}
}
