package Oauth2;

import java.util.Arrays;
import java.util.List;

public class Oauth2Info
{
	final public static String CLIENT_ID = "438691373590-52ct0cdjpic99f80orje850gutg1elg6.apps.googleusercontent.com";
	final public static String CLIENT_SECRET = "";
	final public static String API_KEY = "AIzaSyCz-ZbhCQwM5mEJEGWkPI8HhoUwklHoGIc";
	
	final public static String ENDPOINT_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
	final public static String REDIRECT_URI = "http://localhost";
	
	final public static List<String> SCOPE = Arrays.asList("https://www.googleapis.com/auth/userinfo.profile",
														   "https://www.googleapis.com/auth/userinfo.email",
														   "https://www.googleapis.com/auth/tasks"
															);

}
