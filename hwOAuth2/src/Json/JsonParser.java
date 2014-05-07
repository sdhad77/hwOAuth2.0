package Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON 문자열을 파싱하는 클래스입니다.
 * @author 신동환
 */
public class JsonParser
{
	private JSONObject json;
	
	public Object[] parseUserInfo(String str)
	{
		Object[] jsonResult = new Object[3];
		
		try
		{
			//문자열에서 name, email, picture 데이터 뽑아냄.
			json = new JSONObject(str);
			jsonResult[0] = json.get(JsonInfo.JSON_USERINFO_NAME);
			jsonResult[1] = json.get(JsonInfo.JSON_USERINFO_EMAIL);
			jsonResult[2] = json.get(JsonInfo.JSON_USERINFO_PICTURE);
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		return jsonResult;
	}
	
	public Object[][] parseTaskLists(String str)
	{
		Object[][] parseredData = null;
		
		try 
		{
			json = new JSONObject(str);
			JSONArray jArr = json.getJSONArray(JsonInfo.JSON_TASKS_ITEMS);
			
			String[] jsonName = {JsonInfo.JSON_TASKS_TITLE, JsonInfo.JSON_TASKS_ID, JsonInfo.JSON_TASKS_SELFLINK};
			parseredData = new String[jArr.length()][jsonName.length];
			for(int i = 0; i < jArr.length(); i++)
			{
				json = jArr.getJSONObject(i);
				if(json != null)
				{
					for(int j = 0; j < jsonName.length; j++)
					{
						//TITLE, ID, SELFLINK 정보를 저장
						parseredData[i][j] = json.getString(jsonName[j]);
					}
				}
			}
		}
		catch (JSONException e1)
		{
			e1.printStackTrace();
		}
		
		return parseredData;
	}
}
