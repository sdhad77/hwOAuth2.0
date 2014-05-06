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
 * ���� ��Ƽ��Ƽ
 * @author �ŵ�ȯ
 */
public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        
		//�ʱ�ȭ
		init();
	}
	
	/**
	 * ���� ��ɵ� �ʱ�ȭ</br>
	 * ��ū ���弭���� PrefService,</br>
	 * � ���� ���񽺸� �̿��� ������ �����ϴ� ServiceSelectButton,</br>
	 * ������ ����� ����ϱ� ���� ���� ����� ����ϱ� ���� LoginWebView,</br>
	 * ����� ��ū�� �����ϴ� DeleteCacheDataButton
	 */
	private void init()
	{
		PrefService.getInstance().set_mContext(getApplicationContext());
		initServiceSelectButton();
		initLoginWebView();
		initDeleteCacheDataButton();
	}

	/**
	 * � ���� ���񽺸� �̿��� ������ �����ϴ� ��ư�Դϴ�.</br>
	 * ������ư���� ���ð����� ����Ʈ�� �����ְ�, �� �� �ϳ��� �����Ͽ� Ȯ�ι�ư�� ������ ���񽺸� �̿��� �� �ֽ��ϴ�.</br>
	 */
	private void initServiceSelectButton()
	{
		Button serviceSelectButton = (Button)findViewById(R.id.button1);
		serviceSelectButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				RadioGroup serviceRadioGroup = (RadioGroup)findViewById(R.id.radioGroup1);
				
				//���񽺰� ���õǾ�����
				if(serviceRadioGroup.getCheckedRadioButtonId() > 0)
				{
					//������ ���񽺰� ��������..
					switch(serviceRadioGroup.getCheckedRadioButtonId())
					{
						//����� ���� ���� �̿�
						case R.id.radioButton1:
							Oauth2Service.getInstance().setSelectService(Oauth2Info.Service.USERINFO);
							break;
							
						//Tasks ���� �̿�
						case R.id.radioButton2:
							Oauth2Service.getInstance().setSelectService(Oauth2Info.Service.TASKS);
							break;
							
						default :
							Toast.makeText(getApplicationContext(), "�������� �ʴ� ID �Դϴ�. ����� ������ �̵��մϴ�.", Toast.LENGTH_LONG).show();
							Oauth2Service.getInstance().setSelectService(Oauth2Info.Service.USERINFO);
							break;
					}
					
					//access��ū�� �������� ���� ���, �� ù ������ ���
					if(Oauth2Service.getInstance().get_accessToken() == PrefService.PREF_TOKEN_IS_NOT_EXIST)
					{
						//�α��� �� ���� ȹ���� ���� ���並 ����մϴ�.
						WebView loginWebView = (WebView) findViewById(R.id.webView1);
						loginWebView.setVisibility(View.VISIBLE);
						loginWebView.loadUrl(Oauth2Service.getInstance().getAuthorizationUrl());
						
						//���� �������� �ִ� ���̾ƿ��� �Ⱥ��̰� �մϴ�.
						RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.layout1);
						relativeLayout.setVisibility(View.INVISIBLE);
					}
					//access��ū�� �����ϴ� ���
					else
					{
						//access��ū�� �о�ͼ� �ٷ� ���񽺷� �̵��� �մϴ�.
						Oauth2Service.getInstance().makeCredential();
						Intent intent = new Intent(getApplicationContext(), Oauth2Service.getInstance().getServiceClass());			
						startActivity(intent);
						finish();
					}
				}
				//���񽺰� ���õ��� �ʾ��� ���
				else
				{
					Toast.makeText(getApplicationContext(), "���񽺸� �������ּ���", Toast.LENGTH_LONG).show();
				}
			}
		});
	}
	
	/**
	 * ������ ����� ����ϱ� ���� ���� ȹ���� �ϴ� ���� �Դϴ�.
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
						//url���� �ʿ��� code �κи� �߶���ϴ�.
						String code = url.substring(Oauth2Info.getInstance().getREDIRECT_URI().length() + 7, url.length());
						
						//�߶� code�� �̿��Ͽ� Credential�� ����ϴ�.
						Oauth2Service.getInstance().codeToCredential(code);
	
						//����� ���� ���並 �Ⱥ��̰� �ϰ�,
						loginWebView.setVisibility(View.INVISIBLE);
						
						//���� ���ȭ������ �Ѿ�ϴ�.
						Intent intent = new Intent(getApplicationContext(), Oauth2Service.getInstance().getServiceClass());			
						startActivity(intent);
						finish();
					}
					//�� ���ڿ��� error�� ���۵Ǵ� ���, �� code���� ������ �������
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
	 * ����� ��ū�� �����ϴ� ��ư�Դϴ�.
	 */
	private void initDeleteCacheDataButton()
	{
		Button deleteCacheDataButton = (Button)findViewById(R.id.button2);
		deleteCacheDataButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//����� ������ ����
				PrefService.getInstance().removeAllPreferences();
				Toast.makeText(getApplicationContext(), "�����Ͱ� ���� �Ǿ����ϴ�", Toast.LENGTH_LONG).show();
			}
		});
	}
}
