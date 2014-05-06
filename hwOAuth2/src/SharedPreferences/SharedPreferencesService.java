package SharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 토큰을 저장하기 위해 만든 클래스입니다.</br>
 * 다음의 사이트를 참고하여 만들었습니다.</br>
 * http://muzesong.tistory.com/79</br>
 * http://arabiannight.tistory.com/198</br>
 * @author 신동환
 */
public class SharedPreferencesService
{
	private final String PREF_NAME = "token";
 
	public final static String PREF_ACCESS_TOKEN = "access_token";
	public final static String PREF_REFRESH_TOKEN = "refresh_token";
     
	private Context _mContext;
 
	private static SharedPreferencesService _instance;

	private SharedPreferencesService()
	{
		_mContext = null;
	}
	
	public static SharedPreferencesService getInstance()
	{
		if( _instance == null )
		{
			_instance = new SharedPreferencesService();
		}

		return _instance;
	}
 
	/**
	 * 데이터를 저장함
	 * @param key 저장할 데이터의 key값
	 * @param value 저장할 데이터
	 */
	public void put(String key, String value)
	{
		SharedPreferences pref = _mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
 
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 데이터를 읽어옴
	 * @param key 읽어올 데이터의 key값
	 * @param dftValue 키가 존재하지 않을때 사용할 디폴트 데이터
	 * @return 읽어온 데이터 or dftValue
	 */
	public String getValue(String key, String defValue)
	{
		SharedPreferences pref = _mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
 
		try
		{
			return pref.getString(key, defValue);
		}
		catch (Exception e)
		{
			return defValue;
		} 
	}
	
	/**
	 * 모든 데이터 제거
	 */
	public void removeAllPreferences()
	{
		SharedPreferences pref = _mContext.getSharedPreferences("pref", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
	
	public Context get_mContext() {return _mContext;}

	public void set_mContext(Context _mContext) {this._mContext = _mContext;}
}
