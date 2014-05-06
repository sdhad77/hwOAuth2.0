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
		SharedPreferences pref = _mContext.getSharedPreferences("pref", Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.clear();
		editor.commit();
	}
	
	public Context get_mContext() {return _mContext;}

	public void set_mContext(Context _mContext) {this._mContext = _mContext;}
}
