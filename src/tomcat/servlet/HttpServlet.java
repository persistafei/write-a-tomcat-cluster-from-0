package tomcat.servlet;

import tomcat.context.HttpContext;
import tomcat.http.HttpRequest;
import tomcat.http.HttpResponse;

import java.io.File;

public class HttpServlet {
	public void init(){}
	public void destroy(){};

	public void service(HttpRequest request, HttpResponse  response){

	}
	
	public void forward(String url, HttpRequest request,HttpResponse response){
		
		File file= new File("webapps/"+url);
		System.out.println("文件存在"+file.exists());
		if (file.exists()) {

			String extension = url.substring(url.lastIndexOf(".") + 1);
			response.setContentType(HttpContext.getMimeType(extension));
			response.setContentLen(file.length());
			response.setEntity(file);
			response.flush();
		}
	}

}
