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


@WebServlet({ "/oAuthURL" })
public class oAuthURL extends HttpServlet {

	public oAuthURL() {
	}

	public void init() throws ServletException {

	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		// return the oAuth URL 
		String url =
			    "https://developer.api.autodesk.com" +
			    "/authentication/v1/authorize?response_type=code" +
			    "&client_id=" + config.credentials.client_id +
			    "&redirect_uri=" + config.credentials.callbackURL +
			    "&scope=" + config.credentials.oAuthScope; 
		PrintWriter out = resp.getWriter();
		out.print(url); 
	} 

	public void destroy() {
		super.destroy();
	}
}
