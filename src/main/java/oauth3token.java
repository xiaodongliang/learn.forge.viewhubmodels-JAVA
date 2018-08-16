import java.io.*;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.xml.bind.DatatypeConverter;

import org.json.*;

import com.autodesk.client.auth.Credentials;
import com.autodesk.client.auth.OAuth2ThreeLegged;
import com.autodesk.client.auth.OAuth2TwoLegged;
import com.autodesk.client.auth.ThreeLeggedCredentials;
import com.autodesk.client.ApiException;
import com.autodesk.client.ApiResponse;
import com.autodesk.client.api.*;
import com.autodesk.client.model.*;


@WebServlet({ "/oauth3token" })
public class oauth3token extends HttpServlet {

	public oauth3token() {
	}

	public void init() throws ServletException {

	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(); 
		String code = req.getParameter("code"); 
		oauth credentials = new oauth(session);
		credentials.setCode(code);
		resp.sendRedirect("/"); 
	} 

	public void destroy() {
		super.destroy();
	}
}
