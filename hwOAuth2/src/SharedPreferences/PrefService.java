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
public class PrefService
{
	private final String PREF_NAME = "token";
	
	//토큰이 존재하지 않을 때 사용 할 문자열
	public final static String PREF_TOKEN_IS_NOT_EXIST     = "token_is_not_exist";
 
	//사용자 정보 서비스 관련 토큰을 이용할 때 사용할 문자열
	public final static String PREF_ACCESS_TOKEN_USERINFO  = "access_token_userinfo";
	public final static String PREF_REFRESH_TOKEN_USERINFO = "refresh_token_userinfo";
	
	//Tasks 서비스 관련 토큰을 이용할 때 사용할 문자열
	public final static String PREF_ACCESS_TOKEN_TASKS     = "access_token_tasks";
	public final static String PREF_REFRESH_TOKEN_TASKS    = "refresh_token_tasks";
     
	private Context _mContext;
 
	private static PrefService _instance;

	private PrefService()
	{
		_mContext = null;
	}
	
	public static PrefService getInstance()
	{
		if( _instance == null )
		{
			_instance = new PrefService();
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
		SharedPreferences pref = _mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
	
	public Context get_mContext() {return _mContext;}

	public void set_mContext(Context _mContext) {this._mContext = _mContext;}
}
