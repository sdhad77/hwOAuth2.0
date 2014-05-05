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

public class UserInfoService extends Activity
{
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
		
		androidUniversalImageLoaderInit();
		
		Object[] jsonTempObject = new Object[3];
	   	JsonParser jsonParser = new JsonParser();
	    jsonTempObject = jsonParser.parse(getUserInfo());
	  
		ImageView imgView = (ImageView)findViewById(R.id.imageView);
		
		ImageLoader.getInstance().displayImage(jsonTempObject[2].toString(), imgView);
			
		TextView textView = (TextView) findViewById(R.id.textView1);
		textView.setText(jsonTempObject[0].toString());
		
		textView = (TextView) findViewById(R.id.textView2);
		textView.setText(jsonTempObject[1].toString());
	}
	
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
	
	public String getUserInfo()
	{
		return Oauth2Service.getInstance().accessProtectedResource(Oauth2Info.ENDPOINT_URL);
	}
}