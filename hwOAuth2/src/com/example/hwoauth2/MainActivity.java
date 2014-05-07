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

/**
 * 메인 액티비티
 * @author 신동환
 */
public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        
		//초기화
		init();
	}
	
	/**
	 * 각종 기능들 초기화</br>
	 * 토큰 저장서비스인 PrefService,</br>
	 * 어떤 구글 서비스를 이용할 것인지 선택하는 ServiceSelectButton,</br>
	 * 선택한 기능을 사용하기 위해 인증 기능을 사용하기 위한 LoginWebView,</br>
	 * 저장된 토큰을 제거하는 DeleteCacheDataButton
	 */
	private void init()
	{
		PrefService.getInstance().set_mContext(getApplicationContext());
		initServiceSelectButton();
		initLoginWebView();
		initDeleteCacheDataButton();
	}

	/**
	 * 어떤 구글 서비스를 이용할 것인지 선택하는 버튼입니다.</br>
	 * 라디오버튼으로 선택가능한 리스트를 보여주고, 그 중 하나를 선택하여 확인버튼을 누르면 서비스를 이용할 수 있습니다.</br>
	 */
	private void initServiceSelectButton()
	{
		Button serviceSelectButton = (Button)findViewById(R.id.button1);
		serviceSelectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				RadioGroup serviceRadioGroup = (RadioGroup)findViewById(R.id.radioGroup1);
				
				//서비스가 선택되었는지
				if(serviceRadioGroup.getCheckedRadioButtonId() > 0)
				{
					//선택한 서비스가 무엇인지..
					switch(serviceRadioGroup.getCheckedRadioButtonId())
					{
						//사용자 정보 서비스 이용
						case R.id.radioButton1:
							Oauth2Service.getInstance().setSelectService(Oauth2Info.Service.USERINFO);
							break;
							
						//Tasks 서비스 이용
						case R.id.radioButton2:
							Oauth2Service.getInstance().setSelectService(Oauth2Info.Service.TASKS);
							break;
							
						default :
							Toast.makeText(getApplicationContext(), "존재하지 않는 ID 입니다. 사용자 정보로 이동합니다.", Toast.LENGTH_LONG).show();
							Oauth2Service.getInstance().setSelectService(Oauth2Info.Service.USERINFO);
							break;
					}
					
					//access토큰이 존재하지 않을 경우, 즉 첫 접속일 경우
					if(Oauth2Service.getInstance().get_accessToken() == PrefService.PREF_TOKEN_IS_NOT_EXIST)
					{
						//로그인 및 권한 획득을 위해 웹뷰를 사용합니다.
						WebView loginWebView = (WebView) findViewById(R.id.webView1);
						loginWebView.setVisibility(View.VISIBLE);
						loginWebView.loadUrl(Oauth2Service.getInstance().getAuthorizationUrl());
						
						//현재 보여지고 있는 레이아웃을 안보이게 합니다.
						RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.layout1);
						relativeLayout.setVisibility(View.INVISIBLE);
					}
					//access토큰이 존재하는 경우
					else
					{
						//access토큰을 읽어와서 바로 서비스로 이동을 합니다.
						Oauth2Service.getInstance().makeCredential();
						Intent intent = new Intent(getApplicationContext(), Oauth2Service.getInstance().getServiceClass());			
						startActivity(intent);
						finish();
					}
				}
				//서비스가 선택되지 않았을 경우
				else
				{
					Toast.makeText(getApplicationContext(), "서비스를 선택해주세요", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	/**
	 * 선택한 기능을 사용하기 위해 권한 획득을 하는 웹뷰 입니다.
	 */
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
						//url에서 필요한 code 부분만 잘라냅니다.
						String code = url.substring(Oauth2Info.getInstance().getREDIRECT_URI().length() + 7, url.length());
						
						//잘라낸 code를 이용하여 Credential을 만듭니다.
						Oauth2Service.getInstance().codeToCredential(code);
	
						//사용이 끝난 웹뷰를 안보이게 하고,
						loginWebView.setVisibility(View.INVISIBLE);
						
						//서비스 사용화면으로 넘어갑니다.
						Intent intent = new Intent(getApplicationContext(), Oauth2Service.getInstance().getServiceClass());			
						startActivity(intent);
						finish();
					}
					//얻어낸 문자열이 error로 시작되는 경우, 즉 code값에 문제가 있을경우
					else if (url.indexOf("error=") != -1)
					{
						Toast.makeText(getApplicationContext(), "code error", Toast.LENGTH_LONG).show();
						loginWebView.setVisibility(View.INVISIBLE);
					}
				}
			}
		});
	}
	
	/**
	 * 저장된 토큰을 제거하는 버튼입니다.
	 */
	private void initDeleteCacheDataButton()
	{
		Button deleteCacheDataButton = (Button)findViewById(R.id.button2);
		deleteCacheDataButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//저장된 데이터 삭제
				PrefService.getInstance().removeAllPreferences();
				Toast.makeText(getApplicationContext(), "데이터가 삭제 되었습니다", Toast.LENGTH_LONG).show();
			}
		});
	}
}
