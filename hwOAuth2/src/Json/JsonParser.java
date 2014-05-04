package Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser
{
	private JSONObject json;
	
	public Object[] parse(String str)
	{
		
		Object[] jsonResult = new Object[3];
		
		try
		{
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
	
	public Object[][] parseArray(String str)
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
