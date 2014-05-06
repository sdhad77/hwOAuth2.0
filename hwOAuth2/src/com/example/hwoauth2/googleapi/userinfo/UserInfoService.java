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
 * 사용자 정보 서비스를 이용하기 위한 클래스</br>
 * 이미지 로더 라이브러리 주소입니다.</br>
 * https://github.com/nostra13/Android-Universal-Image-Loader
 * @author 신동환
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
		
		//androidUniversalImageLoader 라이브러리를 사용하기 위해 초기화해줌.
		androidUniversalImageLoaderInit();
		
		//사용자 정보를 얻어와서 Object 배열에 저장함.
		Object[] jsonTempObject = new Object[USERINFO_ARRAY_SIZE];
	   	JsonParser jsonParser = new JsonParser();
	    jsonTempObject = jsonParser.parseUserInfo(getUserInfo());
	  
		ImageView imgView = (ImageView)findViewById(R.id.imageView);
		
		//이미지 로더를 이용하여 화면에 이미지를 그려줌.
		ImageLoader.getInstance().displayImage(jsonTempObject[USERINFO_ARRAY_INDEX_PICTURE].toString(), imgView);
			
		//사용자 이름 출력
		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setText(jsonTempObject[USERINFO_ARRAY_INDEX_NAME].toString());
		
		//사용자 이메일 출력
		textView = (TextView) findViewById(R.id.textView2);
		textView.setText(jsonTempObject[USERINFO_ARRAY_INDEX_EMAIL].toString());
	}
	
	/**
	 * 이미지 라이브러리를 이용하기 위해 초기화 해주는 함수
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
	 * Oauth2Service클래스를 사용하여 사용자 정보를 얻어 내는 함수
	 * @return json으로 된 사용자 정보
	 */
	public String getUserInfo()
	{
		return Oauth2Service.getInstance().accessProtectedResource(Oauth2Info.getInstance().getENDPOINT_URL());
	}
}