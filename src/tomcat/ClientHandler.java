package tomcat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import tomcat.context.HttpContext;
import tomcat.context.ServerContext;
import tomcat.core.ServletClassLoader;
import tomcat.core.ServletContainer;
import tomcat.http.EmptyRequestException;
import tomcat.http.HttpRequest;
import tomcat.http.HttpResponse;
import tomcat.servlet.HttpServlet;

public class ClientHandler implements Runnable{
	Socket client;
	public ClientHandler(Socket s) {
		client=s;
	}
	
	@Override
	public void run() {
		try(
				InputStream in= client.getInputStream();
				OutputStream out = client.getOutputStream();
				){
			HttpRequest request= new HttpRequest(in);
			HttpResponse response= new HttpResponse(out);
			String uri= request.getUri();
			
			System.out.println("请求解析完毕");
			if(uri.contains(".")){
				String extension= uri.substring(uri.lastIndexOf(".")+1);
				if (HttpContext.getMimeType(extension)!=null) {
					System.out.println("请求资源");
					forward(extension, uri, request, response);
				}
			}else{
				System.out.println("请求功能");
				//容器实例化 servlet对象
				HttpServlet servlet= ServletContainer.getServlet(uri);
				servlet.service(request,response );
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (EmptyRequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void forward(String extension,String uri, HttpRequest request
						,HttpResponse response){
		File page= new File(ServerContext.rootDir+ uri);
		//System.out.println(page.getAbsolutePath());
		System.out.println(page.exists());
		if(page.exists()){
			response.setStatusCode(200);
			response.setEntity(page);
			response.setContentLen(page.length());
			response.setContentType(extension);
			response.setCookie(request.getCookie());
			response.flush();
		}else{
			response.setStatusCode(404);
		}
	}
	
	

}
