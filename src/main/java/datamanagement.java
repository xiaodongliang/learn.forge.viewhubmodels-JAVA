import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.xml.bind.DatatypeConverter;

import org.codehaus.jettison.json.JSONObject;
import org.joda.time.DateTime;
import org.json.*;

import com.autodesk.client.auth.Credentials;
import com.autodesk.client.auth.OAuth2ThreeLegged;
import com.autodesk.client.auth.OAuth2TwoLegged;
import com.autodesk.client.auth.ThreeLeggedCredentials;
import com.autodesk.client.ApiException;
import com.autodesk.client.ApiResponse;
import com.autodesk.client.api.*;
import com.autodesk.client.model.*;
import com.owlike.genson.annotation.JsonConverter;
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
 					String hubId = params[params.length - 3]; 
 					getFolders(hubId,resourceId,oauthClient,resp); 
 					break;
 				case "folders":
 					 String projectId = params[params.length - 3];
                     getFolderContents(projectId, resourceId/*folder_id*/, oauthClient,resp); 
 					break;
 				case "items":
 					 projectId = params[params.length - 3];
                     getVersions(projectId, resourceId/*item_id*/,  oauthClient,resp); 
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
	 		
	 		//PrintWriter out = resp.getWriter();
	 		//JSONArray jsonArray = new JSONArray();
	        
		 	resp.setContentType("application/json;charset=UTF-8");
	        ServletOutputStream out = resp.getOutputStream();

	        //JSONArray does not work, the response will be a string, instead of a Json array
	        //workaround by send a stringfied json
	        
		 	String finalJsonStr = "[";
		 			
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
		        
		        finalJsonStr+= obj.toString();
		        if(i<hubsArray.size() - 1)
		        	finalJsonStr += ",";
		         
		        //jsonArray.put(obj);
  			} 		
		 	finalJsonStr += "]";

		 	//out.print(jsonArray); 
		 	out.print(finalJsonStr);
	        
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
	 		
	 		//PrintWriter out = resp.getWriter(); 
	 		//JSONArray jsonArray = new JSONArray();
	 		

		 	resp.setContentType("application/json;charset=UTF-8");
	        ServletOutputStream out = resp.getOutputStream();
		 	String finalJsonStr = "[";

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
		         
		        finalJsonStr+= obj.toString();
		        if(i<projectsArray.size() - 1)
		        	finalJsonStr += ",";
		        //jsonArray.put(obj); 
 			} 		
 
		 	finalJsonStr += "]";

		 	//out.print(jsonArray); 
		 	out.print(finalJsonStr);
		}
		catch (Exception e) {
				resp.setStatus(500); 
		}   
 		 
	}
	
void getFolders(String hubId,String projectId,oauth oauthClient, HttpServletResponse resp) {
		
		try {
			 
			ProjectsApi projects = new ProjectsApi();
	 		ThreeLeggedCredentials threeCre = oauthClient.getCredentialsInternal(); 
	
	 		ApiResponse<Folders> topfoldersResponse = 
	 				projects.getProjectTopFolders(hubId,projectId,
	 				oauthClient.OAuthClient(null), threeCre);
	 		
 	 		ArrayList<Folder> foldersArray = (ArrayList<Folder>) topfoldersResponse.getData().getData();
	 		
	 		//PrintWriter out = resp.getWriter(); 
	 		//JSONArray jsonArray = new JSONArray();
	 		

		 	resp.setContentType("application/json;charset=UTF-8");
	        ServletOutputStream out = resp.getOutputStream();
		 	String finalJsonStr = "[";

		 	for (int i = 0; i < foldersArray.size(); i++) {
		 		Folder eachFolder = foldersArray.get(i); 
		 		 
		 		
		 		String href = eachFolder.getLinks().getSelf().getHref(); 
		 		String name = eachFolder.getAttributes().getDisplayName()==null?
		 				eachFolder.getAttributes().getName():eachFolder.getAttributes().getDisplayName();
				//String folderType = eachFolder.getAttributes().getExtension().getType();
		 		String folderType = "folders";
 		 		
 		        JSONObject obj = new JSONObject();
		        
 
		        obj.put("id", href);  
		        obj.put("text", name);  
		        obj.put("type", folderType);  
		        obj.put("children", true);   
		         
		        finalJsonStr+= obj.toString();
		        if(i<foldersArray.size() - 1)
		        	finalJsonStr += ",";
		        //jsonArray.put(obj); 
 			} 		
 
		 	finalJsonStr += "]";

		 	//out.print(jsonArray); 
		 	out.print(finalJsonStr);
		}
		catch (Exception e) {
				resp.setStatus(500); 
		}   
 		 
	}
	
	void getFolderContents(String projectId,String folderId,oauth oauthClient, HttpServletResponse resp) {
		
		try {
			 
			FoldersApi folderApi = new FoldersApi();
	 		ThreeLeggedCredentials threeCre = oauthClient.getCredentialsInternal(); 
	
	 		ApiResponse<JsonApiCollection> topfoldersResponse = 
	 				folderApi.getFolderContents(projectId, folderId,
	 						new ArrayList<String>(){},new ArrayList<String>(){},new ArrayList<String>(){}, 
	 				null, null, oauthClient.OAuthClient(null), threeCre);
	 		
 	 		ArrayList<JsonApiResource> contentsArray = (ArrayList<JsonApiResource>) topfoldersResponse.getData().getData();
	 		
	 		//PrintWriter out = resp.getWriter(); 
	 		//JSONArray jsonArray = new JSONArray();
	 		

		 	resp.setContentType("application/json;charset=UTF-8");
	        ServletOutputStream out = resp.getOutputStream();
		 	String finalJsonStr = "[";

		 	for (int i = 0; i < contentsArray.size(); i++) {
		 		JsonApiResource eachContent = contentsArray.get(i); 
		 		 
		 		
		 		String href = eachContent.getLinks().getSelf().getHref(); 
		 		String name = eachContent.getAttributes().getDisplayName()==null?
		 				eachContent.getAttributes().getName():eachContent.getAttributes().getDisplayName();
				//String folderType = eachFolder.getAttributes().getExtension().getType();
		 		String contentType = "items";
 		 		
 		        JSONObject obj = new JSONObject();
		        
 
		        obj.put("id", href);  
		        obj.put("text", name);  
		        obj.put("type", contentType);  
		        obj.put("children", true);   
		         
		        finalJsonStr+= obj.toString();
		        if(i<contentsArray.size() - 1)
		        	finalJsonStr += ",";
		        //jsonArray.put(obj); 
 			} 		
 
		 	finalJsonStr += "]";

		 	//out.print(jsonArray); 
		 	out.print(finalJsonStr);
		}
		catch (Exception e) {
				resp.setStatus(500); 
		}   
 		 
	}
	
	void getVersions(String projectId,String itemId,oauth oauthClient, HttpServletResponse resp) {
		
		try {
			 
			ItemsApi itemApi = new ItemsApi();
	 		ThreeLeggedCredentials threeCre = oauthClient.getCredentialsInternal(); 
	
	 		ApiResponse<Versions> versionsResponse = 
	 				itemApi.getItemVersions(projectId, itemId, null, null, null, null, null, null,
	 						oauthClient.OAuthClient(null), threeCre );  
	 		
 	 		ArrayList<Version> versionsArray = (ArrayList<Version>) versionsResponse.getData().getData();
	 		
	 		//PrintWriter out = resp.getWriter(); 
	 		//JSONArray jsonArray = new JSONArray();
	 		

		 	resp.setContentType("application/json;charset=UTF-8");
	        ServletOutputStream out = resp.getOutputStream();
		 	String finalJsonStr = "[";

		 	for (int i = 0; i < versionsArray.size(); i++) {
		 		
		 		Version version = versionsArray.get(i); 
		 		 
		 		
		 		String dateFormated = new DateTime(version.getAttributes().getLastModifiedTime()).toLocalDateTime().toString();
 	            String[] params = version.getId().split("\\?");
	            String versionst = params[params.length - 1];
 				
	            String viewerUrn = (version.getRelationships() != null && 
	            		version.getRelationships().getDerivatives() != null ?
	            				version.getRelationships().getDerivatives().getData().getId() : null);
	            
	 			String name = URLDecoder.decode("v" + versionst + ":" 
	 			+ dateFormated + " by " + version.getAttributes().getLastModifiedUserName(), "UTF-8"); 

 		 		
 		        JSONObject obj = new JSONObject(); 
 
		        obj.put("id", viewerUrn);  
		        obj.put("text", name);  
		        obj.put("type",  (viewerUrn != null ? "versions" : "unsupported"));  
		        obj.put("children", false);   
		         
		        finalJsonStr+= obj.toString();
		        if(i<versionsArray.size() - 1)
		        	finalJsonStr += ",";
		        //jsonArray.put(obj); 
 			} 		
 
		 	finalJsonStr += "]";

		 	//out.print(jsonArray); 
		 	out.print(finalJsonStr);
		}
		catch (Exception e) {
				resp.setStatus(500); 
		}   
 		 
	}
	
	
}
