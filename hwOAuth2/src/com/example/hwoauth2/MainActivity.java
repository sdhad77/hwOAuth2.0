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
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private WebView _loginWebView;
	private int selectService;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        
		init();
	}
	
	private void init()
	{
		initServiceSelectButton();
		initLoginWebView();
	}
	
	private void initServiceSelectButton()
	{
		Button serviceSelectButton = (Button)findViewById(R.id.button1);
		serviceSelectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				RadioGroup serviceRadioGroup = (RadioGroup)findViewById(R.id.radioGroup1);
				
				switch(serviceRadioGroup.getCheckedRadioButtonId())
				{
					case R.id.radioButton1:
						selectService = 1;
						break;
						
					case R.id.radioButton2:
						selectService = 2;
						break;
						
					default :
						Toast.makeText(getApplicationContext(), "존재하지 않는 ID 입니다", Toast.LENGTH_LONG).show();
						break;
				}
				
				_loginWebView.setVisibility(View.VISIBLE);
				
				RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.layout1);
				relativeLayout.setVisibility(View.INVISIBLE);
			}
		});
	}
	
	private void initLoginWebView()
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
						
						Intent intent = null;
						
						if(selectService == 1) intent = new Intent(getApplicationContext(), UserInfoService.class);
						else if(selectService == 2) intent = new Intent(getApplicationContext(), TasksService.class);
						
						startActivity(intent);
						finish();
					}
					else if (url.indexOf("error=") != -1)
					{
						Toast.makeText(getApplicationContext(), "code error", Toast.LENGTH_LONG).show();
						_loginWebView.setVisibility(View.INVISIBLE);
					}
				}
			}
		});
		
		_loginWebView.loadUrl(Oauth2Service.getInstance().getAuthorizationUrl());
	}
}
