import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.json.simple.JSONObject;

import com.autodesk.client.auth.Credentials;
import com.autodesk.client.auth.OAuth2ThreeLegged;
import com.autodesk.client.auth.OAuth2TwoLegged;
import com.autodesk.client.auth.ThreeLeggedCredentials;

public class oauth {

	private Credentials threeLeggedCredentials = null;
	private HttpSession _session = null;
 
	
	public oauth(HttpSession v) {
		_session = v;
	} 
	
	public  OAuth2ThreeLegged OAuthClient(ArrayList<String> scopes) throws Exception {

		String client_id = config.credentials.client_id;
		String client_secret = config.credentials.client_secret;
		String callback = config.credentials.callbackURL;
		if (scopes == null)
			scopes = config.scopeInternal; 
		
		return new OAuth2ThreeLegged(client_id,
									 client_secret,
									 callback,
									 scopes,
									 Boolean.valueOf(true));

	}
	
	public void setCode(String code) {
		try {
			OAuth2ThreeLegged forgeOAuthInternal =  OAuthClient(config.scopeInternal);
			OAuth2ThreeLegged forgeOAuthPublic = OAuthClient(config.scopePublic);
			
			
			ThreeLeggedCredentials credentialsInternal = forgeOAuthInternal.getAccessToken(code);
			System.out.print("get credentialsInternal succeeded: " );
			
			ThreeLeggedCredentials credentialsPublic = forgeOAuthPublic.refreshAccessToken(credentialsInternal.getRefreshToken());

 			System.out.print("get credentialsPublic succeeded: " );

			_session.setAttribute("tokenInternal", credentialsInternal.getAccessToken());
			_session.setAttribute("tokenPublic", credentialsPublic.getAccessToken());
			_session.setAttribute("refreshToken", credentialsPublic.getRefreshToken());
 
			long nowtime = DateTime.now().toDate().getTime(); 
			_session.setAttribute("expiresAt", nowtime + credentialsPublic.getExpiresAt());  
			
		}
		catch (Exception e) {
 			e.printStackTrace();
		} 
	}
	
	public long getExpiresIn () {
		
		long nowtime = DateTime.now().toDate().getTime(); 
		Date expiresAt = new Date((long)_session.getAttribute("expiresAt"));
		 
	    return Math.round((expiresAt.getTime() - nowtime) / 1000);
	};

	public boolean isExpired () {
		
		long nowtime = DateTime.now().toDate().getTime(); 

		Date expiresAt = new Date((long)_session.getAttribute("expiresAt"));

	    return nowtime - expiresAt.getTime() > 0 ;
	};

	public boolean isAuthorized  () {
	    // !! converts value into boolean
		String publicToken =  (String)_session.getAttribute("tokenPublic"); 
		boolean v = false;
		if(publicToken!=null && !publicToken.isEmpty())
			v=true;
	    return v;
	};

	//build the oAuth object with public scope
	public String getOAuthPublic() throws Exception {
		return (String)_session.getAttribute("tokenPublic"); 
	}

	//build the oAuth object with internal scope 
	public  String getOAuthInternal() throws Exception {
		return (String)_session.getAttribute("tokenInternal"); 
	}   
	
	//build the ThreeLeggedCredentials internal
	public ThreeLeggedCredentials getCredentialsInternal() throws Exception {
		
		String refreshToken = (String)_session.getAttribute("refreshToken"); 
		long expiresAt = (long)_session.getAttribute("expiresAt");  
		String tokenInternal = (String)_session.getAttribute("tokenInternal"); 
		
		if(refreshToken.isEmpty() || expiresAt == 0 ||tokenInternal.isEmpty() )
			return null;
		
		return new ThreeLeggedCredentials(tokenInternal,expiresAt,refreshToken); 
 	}
	
   
		
	
}