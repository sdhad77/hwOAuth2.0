package com.example.hwoauth2;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonParser {
	
	private JSONObject json;
	
	public Object[] parse(String str) {
		
		Object[] jsonResult = new Object[3];
		
		try {
			json = new JSONObject(str);
			jsonResult[0] = json.get(JsonInfo.JSON_NAME);
			jsonResult[1] = json.get(JsonInfo.JSON_EMAIL);
			jsonResult[2] = json.get(JsonInfo.JSON_PICTURE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jsonResult;
	}
}
