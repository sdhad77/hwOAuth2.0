package com.example.hwoauth2.googleapi.userinfo;

import com.example.hwoauth2.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import Json.JsonParser;
import Oauth2.Oauth2Info;
import Oauth2.Oauth2Service;

/**
 * ����� ���� ���񽺸� �̿��ϱ� ���� Ŭ����</br>
 * �̹��� �δ� ���̺귯�� �ּ��Դϴ�.</br>
 * https://github.com/nostra13/Android-Universal-Image-Loader
 * @author �ŵ�ȯ
 */
public class UserInfoService extends Activity
{
	final private int USERINFO_ARRAY_SIZE = 3;
	final private int USERINFO_ARRAY_INDEX_NAME = 0;
	final private int USERINFO_ARRAY_INDEX_EMAIL = 1;
	final private int USERINFO_ARRAY_INDEX_PICTURE = 2;
	
	private static UserInfoService _instance;
	
	public static UserInfoService getInstance()
	{
		if( _instance == null )
		{
			_instance = new UserInfoService();
		}

		return _instance;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userinfo_activity);
		
		//androidUniversalImageLoader ���̺귯���� ����ϱ� ���� �ʱ�ȭ����.
		androidUniversalImageLoaderInit();
		
		//����� ������ ���ͼ� Object �迭�� ������.
		Object[] jsonTempObject = new Object[USERINFO_ARRAY_SIZE];
	   	JsonParser jsonParser = new JsonParser();
	    jsonTempObject = jsonParser.parseUserInfo(getUserInfo());
	  
		ImageView imgView = (ImageView)findViewById(R.id.imageView);
		
		//�̹��� �δ��� �̿��Ͽ� ȭ�鿡 �̹����� �׷���.
		ImageLoader.getInstance().displayImage(jsonTempObject[USERINFO_ARRAY_INDEX_PICTURE].toString(), imgView);
			
		//����� �̸� ���
		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setText(jsonTempObject[USERINFO_ARRAY_INDEX_NAME].toString());
		
		//����� �̸��� ���
		textView = (TextView) findViewById(R.id.textView2);
		textView.setText(jsonTempObject[USERINFO_ARRAY_INDEX_EMAIL].toString());
	}
	
	/**
	 * �̹��� ���̺귯���� �̿��ϱ� ���� �ʱ�ȭ ���ִ� �Լ�
	 */
	private void androidUniversalImageLoaderInit()
	{
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
												.threadPriority(Thread.NORM_PRIORITY - 2)
												.denyCacheImageMultipleSizesInMemory()
												.discCacheFileNameGenerator(new Md5FileNameGenerator())
												.tasksProcessingOrder(QueueProcessingType.LIFO)
												.writeDebugLogs()
												.build();
	
		ImageLoader.getInstance().init(config);
	}
	
	/**
	 * Oauth2ServiceŬ������ ����Ͽ� ����� ������ ��� ���� �Լ�
	 * @return json���� �� ����� ����
	 */
	public String getUserInfo()
	{
		return Oauth2Service.getInstance().accessProtectedResource(Oauth2Info.getInstance().getENDPOINT_URL());
	}
}