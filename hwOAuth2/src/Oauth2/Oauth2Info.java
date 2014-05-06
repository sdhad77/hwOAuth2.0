package Oauth2;

import java.util.Arrays;
import java.util.List;

/**
 * 인증 과정에서 필요한 데이터들을 관리하는 클래스입니다.
 * @author 신동환
 */
public class Oauth2Info
{
	final private String CLIENT_ID = "438691373590-52ct0cdjpic99f80orje850gutg1elg6.apps.googleusercontent.com";
	final private String CLIENT_SECRET = "";
	final private String API_KEY = "AIzaSyCz-ZbhCQwM5mEJEGWkPI8HhoUwklHoGIc";
	final private String REDIRECT_URI = "http://localhost";
	
	//SCOPE, ENDPOINT는 서비스에 따라 값이 변하기 때문에 변수로 사용하기 위해 final 선언을 하지 않았습니다.
	private String ENDPOINT_URL;
	private List<String> SCOPE;
	
	//사용가능한 서비스 목록입니다. 현재 사용자 정보, Tasks 서비스 이용가능합니다.
	public enum Service {NONE, USERINFO, TASKS};
	private Service selectService = Service.NONE;
	
	private String ENDPOINT_UserInfo = "https://www.googleapis.com/oauth2/v3/userinfo";
	private String ENDPOINT_Tasks    = "https://www.googleapis.com/oauth2/v3/userinfo";
	
	private List<String> SCOPE_UserInfo = Arrays.asList("https://www.googleapis.com/auth/userinfo.profile",
														"https://www.googleapis.com/auth/userinfo.email");
	private List<String> SCOPE_Tasks    = Arrays.asList("https://www.googleapis.com/auth/tasks");
	
	private static Oauth2Info _instance;

	private Oauth2Info() {};
	
	public static Oauth2Info getInstance()
	{
		if( _instance == null )
		{
			_instance = new Oauth2Info();
		}

		return _instance;
	}

	//get, set 함수들입니다.
	public Service getSelectService()       {return selectService;}
	public String getCLIENT_ID()            {return CLIENT_ID;}
	public String getCLIENT_SECRET()        {return CLIENT_SECRET;}
	public String getAPI_KEY()              {return API_KEY;}
	public String getREDIRECT_URI()         {return REDIRECT_URI;}
	public String getENDPOINT_URL()         {return ENDPOINT_URL;}
	public List<String> getSCOPE()          {return SCOPE;}
	public String getENDPOINT_UserInfo()    {return ENDPOINT_UserInfo;}
	public String getENDPOINT_Tasks()       {return ENDPOINT_Tasks;}
	public List<String> getSCOPE_UserInfo() {return SCOPE_UserInfo;}
	public List<String> getSCOPE_Tasks()    {return SCOPE_Tasks;}
	
	public void setSelectService(Service selectService)        {this.selectService = selectService;}
	public void setENDPOINT_URL(String eNDPOINT_URL)           {ENDPOINT_URL = eNDPOINT_URL;}
	public void setSCOPE(List<String> sCOPE)                   {SCOPE = sCOPE;}
	public void setENDPOINT_UserInfo(String eNDPOINT_UserInfo) {ENDPOINT_UserInfo = eNDPOINT_UserInfo;}
	public void setENDPOINT_Tasks(String eNDPOINT_Tasks)       {ENDPOINT_Tasks = eNDPOINT_Tasks;}
	public void setSCOPE_UserInfo(List<String> sCOPE_UserInfo) {SCOPE_UserInfo = sCOPE_UserInfo;}
	public void setSCOPE_Tasks(List<String> sCOPE_Tasks)       {SCOPE_Tasks = sCOPE_Tasks;}
}
