package com.example.hwoauth2;

import com.example.hwoauth2.googleapi.tasks.TasksService;
import com.example.hwoauth2.googleapi.userinfo.UserInfoService;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import Oauth2.Oauth2Info;
import Oauth2.Oauth2Service;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private WebView _loginWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        
		init();
        
		_loginWebView.loadUrl(Oauth2Service.getInstance().getAuthorizationUrl());
	}
	
	private void init()
	{
		_loginWebView = (WebView) findViewById(R.id.webView1);
		_loginWebView.getSettings().setJavaScriptEnabled(true);
		
		_loginWebView.setWebViewClient(new WebViewClient()
		{			
			public void onPageFinished(WebView view, String url)
			{
				super.onPageFinished(view, url);
				if(url.startsWith(Oauth2Info.REDIRECT_URI))
				{
					if (url.indexOf("code=") != -1)
					{
						String code = url.substring(Oauth2Info.REDIRECT_URI.length() + 7, url.length());
							
						Oauth2Service.getInstance().codeToCredential(code);
	
						_loginWebView.setVisibility(View.INVISIBLE);
						
				       	showUserInfo();
				       	
		//		       	showTask();
					}
					else if (url.indexOf("error=") != -1)
					{
						Toast.makeText(getApplicationContext(), "code error", Toast.LENGTH_LONG).show();
						_loginWebView.setVisibility(View.INVISIBLE);
					}
				}
			}
		});
	}
   
	private void showUserInfo()
	{
		Intent intent = new Intent(getApplicationContext(), UserInfoService.class);
		intent.putExtra("jsonStr", UserInfoService.getInstance().getUserInfo());
		startActivity(intent);
		finish();
	}
	
	private void showTask()
	{
       	Intent intent = new Intent(getApplicationContext(), TasksService.class);
		startActivity(intent);
		finish();
	}
}
