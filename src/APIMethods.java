import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class APIMethods {
	public static void sendCode(String number) throws IOException {
		String urlParameters  = "{\"phone_number\":\""+number+"\"}";
		byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
		String request        = "https://api.gotinder.com/v2/auth/sms/send?auth_type=sms";
		URL    url            = new URL( request );
		HttpURLConnection connection= (HttpURLConnection) url.openConnection();           
		connection.setDoOutput( true );
		connection.setInstanceFollowRedirects( false );
		connection.setRequestMethod( "POST" );
		connection.setRequestProperty("platform","android");
		connection.setRequestProperty("User-Agent","Tinder Android Version 11.0.1");
		connection.setRequestProperty("os-version","24");
		connection.setRequestProperty("app-version","3544");
		connection.setRequestProperty("Content-Type","application/json");
		
		connection.setRequestProperty("Accept-Language","en");
		connection.setUseCaches( false );
		try( DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
			   wr.write( postData );
		}
	    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    String inputLine;
	    StringBuffer response = new StringBuffer();
	    while ((inputLine = in.readLine()) != null) {
	          response.append(inputLine);
	    }
	    in.close();
	    JSONObject jobj = new JSONObject(response.toString());
	    try {
	    	jobj.getJSONObject("data").get("otp_length").toString().equals("6");
	    	System.out.println("SMS SEND PASSED!");
	    }catch(Exception e) {
	    	System.out.println("SMS SEND FAILED!");
	    }
	}
	public static String getRefreshToken(String number,String smsCode) throws IOException {
		String urlParameters  = "{\"phone_number\": \""+number+"\" , \"otp_code\": \""+smsCode+"\" , \"is_update\": \"false\"}";
		byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
		String request        = "https://api.gotinder.com/v2/auth/sms/validate?auth_type=sms";
		URL    url            = new URL( request );
		HttpURLConnection connection= (HttpURLConnection) url.openConnection();           
		connection.setDoOutput( true );
		connection.setInstanceFollowRedirects( false );
		connection.setRequestMethod( "POST" );
		connection.setRequestProperty("platform","android");
		connection.setRequestProperty("User-Agent","Tinder Android Version 11.0.1");
		connection.setRequestProperty("os-version","24");
		connection.setRequestProperty("app-version","3544");
		connection.setRequestProperty("Content-Type","application/json");
		
		connection.setRequestProperty("Accept-Language","en");
		connection.setUseCaches( false );
		try( DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
			   wr.write( postData );
		}
	    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    String inputLine;
	    StringBuffer response = new StringBuffer();
	    while ((inputLine = in.readLine()) != null) {
	          response.append(inputLine);
	    }
	    in.close();
	    JSONObject jobj = new JSONObject(response.toString());
	   
	    try {
	    	
	    	return jobj.getJSONObject("data").get("refresh_token").toString() ;
	    }catch(Exception e) {
	    	return "";
	    }
			
	}
	public static String getAPIAuthKey(String refreshToken) throws IOException {
		String urlParameters  = "{\"refresh_token\":\""+refreshToken+"\"}";
		byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
		String request        = "https://api.gotinder.com/v2/auth/login/sms";
		URL    url            = new URL( request );
		HttpURLConnection connection= (HttpURLConnection) url.openConnection();           
		connection.setDoOutput( true );
		connection.setInstanceFollowRedirects( false );
		connection.setRequestMethod( "POST" );
		connection.setRequestProperty("platform","android");
		connection.setRequestProperty("User-Agent","Tinder Android Version 11.0.1");
		connection.setRequestProperty("os-version","24");
		connection.setRequestProperty("app-version","3544");
		connection.setRequestProperty("Content-Type","application/json");
		connection.setRequestProperty("Accept-Language","en");
		connection.setRequestProperty("x-supported-image-formats","webp");
		connection.setUseCaches( false );
		try( DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
			   wr.write( postData );
		}
	    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	    String inputLine;
	    StringBuffer response = new StringBuffer();
	    while ((inputLine = in.readLine()) != null) {
	          response.append(inputLine);
	    }
	    in.close();
	    JSONObject jobj = new JSONObject(response.toString());
	   
	    try {
	    	
	    	return jobj.getJSONObject("data").get("api_token").toString() ;
	    }catch(Exception e) {
	    	return "";
	    }
	}
	//WORK IN PROGRESS
	public static ArrayList<userData> dowloadRecUsers(String api_token) throws IOException

	{
        URL obj = new URL("https://api.gotinder.com/v2/recs/core?locale=en");
        HttpURLConnection  connection = (HttpURLConnection) obj.openConnection();
        connection.setConnectTimeout(1000);
        connection.setRequestMethod( "GET" );
        //Request header
        connection.setRequestProperty("platform","android");
		connection.setRequestProperty("User-Agent","Tinder Android Version 11.0.1");
		connection.setRequestProperty("os-version","24");
		connection.setRequestProperty("app-version","3544");
		connection.setRequestProperty("Content-Type","application/json");
		connection.setRequestProperty("Accept-Language","en");
		connection.setRequestProperty("x-supported-image-formats","webp");
		connection.setRequestProperty("X-Auth-Token",api_token);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONObject jobj = new JSONObject(response.toString());
        JSONObject data = jobj.getJSONObject("data");
        JSONArray results = data.getJSONArray("results");
        ArrayList<userData> rtnList = new ArrayList<userData>();
        for (int i = 0; i < results.length(); i++) {
        	 JSONObject userOBJECT = results.getJSONObject(i);
        	 JSONObject user = userOBJECT.getJSONObject("user");
        	 userData newUser = new userData();
        	 newUser.ID = user.getString("_id");
        	 newUser.bio = user.getString("bio");
        	 newUser.birthDay = user.getString("birth_date");
        	 newUser.name = user.getString("name");
        	 JSONArray photos = user.getJSONArray("photos");
        	 for (int j = 0; j <  photos.length(); j++) {
        		 JSONObject photo =  photos.getJSONObject(j);
        		 newUser.photoUrls.add(photo.getString("url"));
        	 }
        	 try {
        		 newUser.gender = Integer.parseInt(user.getString("gender"));
        	 }catch(Exception h) {
        		 newUser.gender = -1;
        	 }
        	
        	 newUser.distance = "NULL";
        	 rtnList.add(newUser);
        }
		return rtnList;
	}
}
