package SharedPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * ��ū�� �����ϱ� ���� ���� Ŭ�����Դϴ�.</br>
 * ������ ����Ʈ�� �����Ͽ� ��������ϴ�.</br>
 * http://muzesong.tistory.com/79</br>
 * http://arabiannight.tistory.com/198</br>
 * @author �ŵ�ȯ
 */
public class PrefService
{
	private final String PREF_NAME = "token";
	
	public final static String PREF_TOKEN_IS_NOT_EXIST     = "token_is_not_exist";
 
	public final static String PREF_ACCESS_TOKEN_USERINFO  = "access_token_userinfo";
	public final static String PREF_REFRESH_TOKEN_USERINFO = "refresh_token_userinfo";
	
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
	 * �����͸� ������
	 * @param key ������ �������� key��
	 * @param value ������ ������
	 */
	public void put(String key, String value)
	{
		SharedPreferences pref = _mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
 
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * �����͸� �о��
	 * @param key �о�� �������� key��
	 * @param dftValue Ű�� �������� ������ ����� ����Ʈ ������
	 * @return �о�� ������ or dftValue
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
	 * ��� ������ ����
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
