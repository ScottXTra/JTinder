import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
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
	    	jobj.getJSONObject("data").get("sms_sent").toString().equals("true");
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
		connection =connectionPropertys(connection);
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
	//GETS list of rec users that tinder thinks you will match with
	public static ArrayList<userData> getRecUsers(String api_token) throws IOException

	{
        URL obj = new URL("https://api.gotinder.com/v2/recs/core?locale=en");
        HttpURLConnection  connection = (HttpURLConnection) obj.openConnection();
        connection.setConnectTimeout(1000);
        connection.setRequestMethod( "GET" );
        //Request header
        connection =connectionPropertys(connection);
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
        	
        	 newUser.gender = Integer.parseInt(user.get("gender").toString());

        	 newUser.distance = userOBJECT.get("distance_mi").toString();
        	 System.out.println(newUser.gender);
        	 rtnList.add(newUser);
        }
		return rtnList;
	}
	//Swipes left on a given user ID
	public static boolean swipeLeft(String userID,String api_token) throws IOException {
		 URL obj = new URL("https://api.gotinder.com/pass/" + userID);
	        HttpURLConnection  connection = (HttpURLConnection) obj.openConnection();
	        connection.setConnectTimeout(1000);
	        connection.setRequestMethod( "GET" );
	        //Request header
	        connection =connectionPropertys(connection);
			connection.setRequestProperty("X-Auth-Token",api_token);
	        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        in.close();
	        //NOT A VERY GOOD CHECK WILL FIX LATER
	        if(response.toString().contains("\"status\":200")) {
	        	return true;
	        }else {
	        	return false;
	        }
	}

	//Swipes right on a given user ID
	public static boolean swipeRight(String userID,String api_token) throws IOException {
		 URL obj = new URL("https://api.gotinder.com/like/" + userID);
	        HttpURLConnection  connection = (HttpURLConnection) obj.openConnection();
	        connection.setConnectTimeout(1000);
	        connection.setRequestMethod( "GET" );
	        //Request header
	        connection =connectionPropertys(connection);
			connection.setRequestProperty("X-Auth-Token",api_token);
	        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();
	        while ((inputLine = in.readLine()) != null) {
	            response.append(inputLine);
	        }
	        in.close();
	        //NOT A VERY GOOD CHECK WILL FIX LATER
	        if(response.toString().contains("\"status\":200")) {
	        	return true;
	        }else {
	        	return false;
	        }
	}
	//Grabs data from user ID
	public static userData getUserData(String userID,String api_token) throws IOException {
		userData user = new userData();
		URL obj = new URL("https://api.gotinder.com/user/"+userID );
        HttpURLConnection  connection = (HttpURLConnection) obj.openConnection();
        connection.setConnectTimeout(1000);
        connection.setRequestMethod( "GET" );
        //Request header
        connection =connectionPropertys(connection);
		connection.setRequestProperty("X-Auth-Token",api_token);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        JSONObject jobj = new JSONObject(response.toString());
        JSONObject results = jobj.getJSONObject("results");
        user.ID = results.getString("_id");
        user.bio = results.getString("bio");
        user.birthDay = results.getString("birth_date");
        user.name = results.getString("name");
        JSONArray photos = results.getJSONArray("photos");
        for (int j = 0; j <  photos.length(); j++) {
        	JSONObject photo =  photos.getJSONObject(j);
        	user.photoUrls.add(photo.getString("url"));
   	 	}
        for (int j = 0; j <  photos.length(); j++) {
   		 	JSONObject photo =  photos.getJSONObject(j);
   		 	user.photoUrls.add(photo.getString("url"));
   	 	}
        user.gender = Integer.parseInt(results.get("gender").toString());
        user.distance = results.get("distance_mi").toString();
   	 	
		return user;
		
	}

	//Updates tinder location (what is used to find local matches)
	public static void updateLocation(String api_token,float lat,float lon) throws IOException {
		String urlParameters  = "{\"lat\":\""+lat+"\",\"lon\": \""+lon+"\", \"force_fetch_resources\":\"true\"}";
		byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
		String request        = "https://api.gotinder.com/v2/meta";
		URL    url            = new URL( request );
		HttpURLConnection connection= (HttpURLConnection) url.openConnection();           
		connection.setDoOutput( true );
		connection.setInstanceFollowRedirects( false );
		connection.setRequestMethod( "POST" );
		connection =connectionPropertys(connection);
		connection.setRequestProperty("X-Auth-Token",api_token);
		connection.setUseCaches( false );
		try( DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
			   wr.write( postData );
		}
	}
	//Downloads all unblurred likes that are displayed on (likes you) screen in app
	public static ArrayList<String> getUnbluredLikes(String api_token, boolean extraPhotos) throws IOException{
		ArrayList<String> photoUrls = new ArrayList<String>();
		URL obj = new URL("https://api.gotinder.com/v2/fast-match/teasers");
        HttpURLConnection  connection = (HttpURLConnection) obj.openConnection();
        connection.setConnectTimeout(1000);
        connection.setRequestMethod( "GET" );
        //Request header
        connection =connectionPropertys(connection);
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
        
        for (int i = 0; i < results.length(); i++) {
       	 	JSONObject userOBJECT = results.getJSONObject(i);
       	 	JSONObject user = userOBJECT.getJSONObject("user");
       	 	JSONArray photos = user.getJSONArray("photos");
       	 	photoUrls.add(photos.getJSONObject(0).getString("url"));
       	 	if(extraPhotos) {
       	 		for (int j = 0; j < photos.length(); j++) {
       	 			photoUrls.add(photos.getJSONObject(j).getString("url"));
       	 		}
       	 	}
        }
		return photoUrls;
	}
	public static int getNumberOfLikes(String api_token) throws IOException {
		URL obj = new URL("https://api.gotinder.com/v2/fast-match/count");
        HttpURLConnection  connection = (HttpURLConnection) obj.openConnection();
        connection.setConnectTimeout(1000);
        connection.setRequestMethod( "GET" );
        //Request header
        connection =connectionPropertys(connection);
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
        try {        	
        	return Integer.parseInt(data.get("count").toString());
        }catch (Exception e){
        	return 0;
        }
		
	}
	public static void sendMessage(String api_token, String userID,String yourUserID, String message) throws IOException {
		String urlParameters  = "{\"message\":\""+message+"\"}";
		byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
		String request        = "https://api.gotinder.com/user/matches/" + userID+yourUserID;
		URL    url            = new URL( request );
		HttpURLConnection connection= (HttpURLConnection) url.openConnection();           
		connection.setDoOutput( true );
		connection.setInstanceFollowRedirects( false );
		connection.setRequestMethod( "POST" );
		connection =connectionPropertys(connection);
		connection.setRequestProperty("X-Auth-Token",api_token);
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
	}
	//NOT FINISHED
	public static void downloadData(String api_token, String timeStamp,String userID) throws IOException {
		
		String urlParameters  = "{\"last_activity_date\":\""+timeStamp+"\"}";
		byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
		String request        = "https://api.gotinder.com/updates";
		URL    url            = new URL( request );
		HttpURLConnection connection= (HttpURLConnection) url.openConnection();           
		connection.setDoOutput( true );
		connection.setInstanceFollowRedirects( false );
		connection.setRequestMethod( "POST" );
		connection =connectionPropertys(connection);
		connection.setRequestProperty("X-Auth-Token",api_token);
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
	    JSONArray matches = jobj.getJSONArray("matches");
	    for (int i = 0; i < matches.length(); i++) {
	    	JSONObject convoObj = matches.getJSONObject(i);
	    	if(convoObj.getString("_id").contains(userID)) {
	    		JSONArray messageArray = convoObj.getJSONArray("messages");
	    		 for (int j = 0; j < messageArray.length(); j++) {
	    			 JSONObject messageObj = messageArray.getJSONObject(j);
	    			 System.out.println(messageObj.getString("message"));
	    		 }
	    	}
	    }
	    
	}
	//NOT FINISHED
	public static void sendGifMessage(String api_token, String userID,String yourUserID, String message) throws IOException {
		String urlParameters  = "{\"message\": \"https://media2.giphy.com/media/HCTfYH2Xk5yw/giphy.gif?cid=&rid=gdiphy.gif\" , \"type\": \"gif\" , \"gif_id\": \"HCTfYH2Xk5yw\"}";
		byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
		String request        = "https://api.gotinder.com/user/matches/" + userID+yourUserID;
		URL    url            = new URL( request );
		HttpURLConnection connection= (HttpURLConnection) url.openConnection();           
		connection.setDoOutput( true );
		connection.setInstanceFollowRedirects( false );
		connection.setRequestMethod( "POST" );
		connection =connectionPropertys(connection);
		connection.setRequestProperty("X-Auth-Token",api_token);
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
	}
	
	public static ArrayList<userData> getTopPicks(String api_token) throws IOException {
		URL obj = new URL("https://api.gotinder.com/v2/top-picks/preview");
        HttpURLConnection  connection = (HttpURLConnection) obj.openConnection();
        connection.setConnectTimeout(1000);
        connection.setRequestMethod( "GET" );
        //Request header
        connection =connectionPropertys(connection);
		connection.setRequestProperty("X-Auth-Token",api_token);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //System.out.println(response.toString());
        JSONObject jobj = new JSONObject(response.toString());
        JSONObject data = jobj.getJSONObject("data");
        JSONArray results = data.getJSONArray("results");
        ArrayList<userData> rtn = new ArrayList<userData>();
        for (int j = 0; j <  results.length(); j++) {
        	userData tmpUser = new userData();
        	JSONObject userElement =  results.getJSONObject(j);
        	JSONObject userObject = userElement.getJSONObject("user");
        	tmpUser.longId = userObject.getString("_id");
        	tmpUser.bio = userObject.getString("bio");
        	tmpUser.birthDay = userObject.getString("birth_date");
        	tmpUser.name = userObject.getString("name");
        	JSONArray photos = userObject.getJSONArray("photos");
            for (int i = 0; i <  photos.length(); i++) {
            	JSONObject photo =  photos.getJSONObject(i);
            	tmpUser.photoUrls.add(photo.getString("url"));
       	 	}
            tmpUser.gender = Integer.parseInt(userObject.get("gender").toString());
            tmpUser.distance = userElement.get("distance_mi").toString();
            rtn.add(tmpUser);
   	 	}
		return rtn;
	}
	public static HttpURLConnection connectionPropertys(HttpURLConnection connection) {
		connection.setRequestProperty("platform","android");
		connection.setRequestProperty("User-Agent","Tinder Android Version 11.0.1");
		connection.setRequestProperty("os-version","24");
		connection.setRequestProperty("app-version","3544");
		connection.setRequestProperty("Content-Type","application/json");
		connection.setRequestProperty("Accept-Language","en");
		connection.setRequestProperty("x-supported-image-formats","webp");
		return connection;
	}
}
