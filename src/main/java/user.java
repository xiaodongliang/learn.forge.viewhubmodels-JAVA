import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.xml.bind.DatatypeConverter;

import org.json.*;

import com.autodesk.client.auth.OAuth2TwoLegged; 
import com.autodesk.client.ApiException;
import com.autodesk.client.ApiResponse;
import com.autodesk.client.api.*;
import com.autodesk.client.model.*;


@WebServlet({ "/user" })
public class user extends HttpServlet {

	public user() {
	}

	public void init() throws ServletException {

	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(); 
 		oauth oauthClient = new oauth(session);
 		
 		//UserProfileApi user = new UserProfileApi();
 		//working... UserProfileApi has not been implemented. 
		 
	} 

	public void destroy() {
		super.destroy();
	}
}
