import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.xml.bind.DatatypeConverter;

import org.codehaus.jettison.json.JSONObject;
import org.json.*;

import com.autodesk.client.auth.Credentials;
import com.autodesk.client.auth.OAuth2ThreeLegged;
import com.autodesk.client.auth.OAuth2TwoLegged;
import com.autodesk.client.auth.ThreeLeggedCredentials;
import com.autodesk.client.ApiException;
import com.autodesk.client.ApiResponse;
import com.autodesk.client.api.*;
import com.autodesk.client.model.*;
import com.sun.xml.bind.v2.schemagen.xmlschema.List;


@WebServlet({ "/datamanagement" })
public class datamanagement extends HttpServlet {

	public datamanagement() {
	}

	public void init() throws ServletException {

	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession session = req.getSession(); 
 		oauth oauthClient = new oauth(session);
 		
 		if(!oauthClient.isAuthorized()) {
 			resp.setStatus(401);  
 		}else {
 			try {
 			
 			String id = req.getParameter("id"); 
 			String href = URLDecoder.decode(id, "UTF-8"); 

 			if(href.isEmpty()) {
 	 			resp.setStatus(500);   
 				return;
 			}
 			
 			
	         
  			if(href.equals("#")) {
 	            getHubs(oauthClient, resp); 
 			}else {
 				
 				String[] params = href.split("/");
 				String resourceName = params[params.length - 2];
 				String resourceId = params[params.length - 1];
 				
 				switch(resourceName) {
 				case "hubs":
 					getProjects(resourceId,oauthClient,resp);
 					break;
 				case "projects":
 					break;
 				case "folders":
 					break;
 				case "items":
 					break;
 				} 
 				
 			} 
 			
 			}catch (Exception e) {
 				resp.setStatus(500); 
 			}  
 		}
 		
	} 

	public void destroy() {
		super.destroy();
	}
	
	void getHubs(oauth oauthClient, HttpServletResponse resp) {
		
		try {
			 
			
	        
			HubsApi hubs = new HubsApi();
	 		ThreeLeggedCredentials threeCre = oauthClient.getCredentialsInternal(); 
	
	 		ApiResponse<Hubs> hubsResponse = 
	 				hubs.getHubs(new ArrayList<String>(){},
	 				new ArrayList<String>(){},
	 				oauthClient.OAuthClient(null), threeCre);
	 		
	 		ArrayList<Hub> hubsArray = (ArrayList<Hub>) hubsResponse.getData().getData();
	 		
	 		PrintWriter out = resp.getWriter();
	 		JSONArray jsonArray = new JSONArray();
	        
		 	for (int i = 0; i < hubsArray.size(); i++) {
		 		Hub eachHub = hubsArray.get(i); 
		 		String hubType = eachHub.getAttributes().getExtension().getType();
		 		
		 		String hubTypeForTree = "";
		 		if(hubType.equals("hubs:autodesk.core:Hub")) {
		 			hubTypeForTree = "hubs";
		 		}else if(hubType.equals("hubs:autodesk.a360:PersonalHub")) {
		 			hubTypeForTree = "personalHub";
		 		}else if(hubType.equals("hubs:autodesk.bim360:Account")) {
		 			hubTypeForTree = "bim360Hubs";
		 		}
		 		else {
					//resp.setStatus(500);  
		 		}
		 		
		 		String href = eachHub.getLinks().getSelf().getHref(); 
		 		String name = eachHub.getAttributes().getName();
 		 		 
		        JSONObject obj = new JSONObject();

		        obj.put("id", href);  
		        obj.put("text", name);  
		        obj.put("type", hubTypeForTree);  
		        obj.put("children", true);   
		         
		        jsonArray.put(obj);
 			} 		
		 	resp.setContentType("application/json");

		 	out.print(jsonArray);
		}
		catch (Exception e) {
				resp.setStatus(500); 
		}   
 		 
	}
	
	void getProjects(String hubId,oauth oauthClient, HttpServletResponse resp) {
		
		try {
			 
			ProjectsApi projects = new ProjectsApi();
	 		ThreeLeggedCredentials threeCre = oauthClient.getCredentialsInternal(); 
	
	 		ApiResponse<Projects> projectsResponse = 
	 				projects.getHubProjects(hubId,new ArrayList<String>(){},
	 				new ArrayList<String>(){},
	 				oauthClient.OAuthClient(null), threeCre);
	 		
	 		ArrayList<Project> projectsArray = (ArrayList<Project>) projectsResponse.getData().getData();
	 		
	 		PrintWriter out = resp.getWriter(); 
	 		JSONArray jsonArray = new JSONArray();

		 	for (int i = 0; i < projectsArray.size(); i++) {
		 		Project eachProject = projectsArray.get(i); 
		 		String projectType = eachProject.getAttributes().getExtension().getType();
		 		
		 		String projectTypeForTree = "";
		 		if(projectType.equals("projects:autodesk.core:Project")) {
		 			projectTypeForTree = "a360projects";
		 		}else if(projectType.equals("projects:autodesk.bim360:Project")) {
		 			projectTypeForTree = "personalHub";
		 		} 
		 		else {
					//resp.setStatus(500);  
		 		}
		 		
		 		String href = eachProject.getLinks().getSelf().getHref(); 
		 		String name = eachProject.getAttributes().getName();
 		 		
 		        JSONObject obj = new JSONObject();
		        
 
		        obj.put("id", href);  
		        obj.put("text", name);  
		        obj.put("type", projectTypeForTree);  
		        obj.put("children", true);   
		         
		        jsonArray.put(obj);
 			} 		
		 	out.print(jsonArray);
		}
		catch (Exception e) {
				resp.setStatus(500); 
		}   
 		 
	}
}
