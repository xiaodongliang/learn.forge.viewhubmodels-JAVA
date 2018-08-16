
import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;

import com.autodesk.client.auth.OAuth2TwoLegged;
import com.autodesk.client.auth.ThreeLeggedCredentials;

@WebServlet({ "/oauthtoken" })
public class oauthtoken extends HttpServlet {

    public oauthtoken() {
    }

    public void init() throws ServletException {

    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    	HttpSession session = req.getSession(); 
 		oauth credentials = new oauth(session);
 		
 		if(!credentials.isAuthorized()) {
 			resp.setStatus(401); 
 		
 		}else {
 			try {
 			String token  = 
 			credentials.getOAuthPublic();
 			 PrintWriter out = resp.getWriter();
 	        JSONObject obj = new JSONObject();
 	        obj.put("access_token", token);  
            out.print(obj);
 			
 			}catch (Exception e) {
 				resp.setStatus(500); 
 			}  
 		}
 		
 		

    }

    public void destroy() {
        super.destroy();
    }
}