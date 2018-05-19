package tomcat.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import tomcat.context.HttpContext;
import tomcat.context.ServerContext;

public class HttpResponse {
	
	private OutputStream out;
	private int statusCode;
	private File entity;

	public static Map<String, String> headers= new HashMap<>();

	public HttpResponse(OutputStream out) {
		this.out = out;
	}
	
	public void flush(){
		sendStatusLine();
		sendHeaders();
		forward("");
		sendContent();
	}
	
	public void sendStatusLine(){
		
		System.out.println("发送响应头");
		String statusLine= ServerContext.protocol+" "+statusCode+" "+HttpContext.getStatusReasonByCode(statusCode);
		System.out.println(statusLine+"  状态行");
		forward(statusLine);
	}
	
	public void sendHeaders(){

		for(Entry<String, String>e: headers.entrySet()){
			String line= e.getKey()+": "+e.getValue();
			forward(line);
		}
		System.out.println(headers+"  响应headers");
	}
	
	public void sendContent(){
		if(entity== null){
			System.out.println("无响应正文====");
			return;
		}
		try(		BufferedInputStream bin= new BufferedInputStream(new FileInputStream(entity));
				BufferedOutputStream bou= new BufferedOutputStream(out);
				){
			int len= -1;
			byte[] data= new byte[10*1024];
			while((len=bin.read(data))!= -1){
				bou.write(data,0,len);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void forward(String line){
		try {
			byte[] data= line.getBytes("iso8859-1");
			
			out.write(data);
			out.write(HttpContext.CR);
			out.write(HttpContext.LF);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 让客户端重定向到指定地址
	 * @param url
	 */
	public void sendRedirect(String url){
		//设置状态代码为重定向
		this.setStatusCode(302);
		//设置响应头:Location
		headers.put(HttpContext.LOCATION, url);
		//响应客户端
		this.flush();
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public void setContentType(String extension){
		String type= HttpContext.getMimeType(extension);
		headers.put(HttpContext.CONTEN_TYPE, type);
	}
	
	public void setContentLen(long len){
		headers.put(HttpContext.CONTEN_LENGTH, len+"");
	}
	
	public void setEntity(File file){
		entity= file;
	}
	
	public void setCookie(HttpCookie cookie){
		headers.put("Set-Cookie", cookie.getName()+"="+cookie.getValue());
	}
	
}
