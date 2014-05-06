package com.example.hwoauth2;

import Oauth2.Oauth2Info;
import Oauth2.Oauth2Service;
import SharedPreferences.PrefService;
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
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        
		init();
	}
	
	private void init()
	{
		PrefService.getInstance().set_mContext(getApplicationContext());
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
						Oauth2Service.getInstance().setSelectService(Oauth2Info.Service.USERINFO);
						break;
						
					case R.id.radioButton2:
						Oauth2Service.getInstance().setSelectService(Oauth2Info.Service.TASKS);
						break;
						
					default :
						Toast.makeText(getApplicationContext(), "존재하지 않는 ID 입니다", Toast.LENGTH_LONG).show();
						break;
				}
				
				WebView loginWebView = (WebView) findViewById(R.id.webView1);
				loginWebView.setVisibility(View.VISIBLE);
				loginWebView.loadUrl(Oauth2Service.getInstance().getAuthorizationUrl());
				
				RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.layout1);
				relativeLayout.setVisibility(View.INVISIBLE);
			}
		});
	}
	
	private void initLoginWebView()
	{
		final WebView loginWebView = (WebView) findViewById(R.id.webView1);
		loginWebView.getSettings().setJavaScriptEnabled(true);
		
		loginWebView.setWebViewClient(new WebViewClient()
		{			
			public void onPageFinished(WebView view, String url)
			{
				super.onPageFinished(view, url);
				if(url.startsWith(Oauth2Info.getInstance().getREDIRECT_URI()))
				{
					if (url.indexOf("code=") != -1)
					{
						String code = url.substring(Oauth2Info.getInstance().getREDIRECT_URI().length() + 7, url.length());
							
						Oauth2Service.getInstance().codeToCredential(code);
	
						loginWebView.setVisibility(View.INVISIBLE);
						
						Intent intent = new Intent(getApplicationContext(), Oauth2Service.getInstance().getServiceClass());			
						startActivity(intent);
						finish();
					}
					else if (url.indexOf("error=") != -1)
					{
						Toast.makeText(getApplicationContext(), "code error", Toast.LENGTH_LONG).show();
						loginWebView.setVisibility(View.INVISIBLE);
					}
				}
			}
		});
	}
}
