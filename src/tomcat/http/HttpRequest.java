package tomcat.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import tomcat.context.HttpContext;
import tomcat.context.ServerContext;
import tomcat.core.ServletContainer;


public class HttpRequest{
	
	private InputStream in;
	
	private String requestLine;
	private String requestUrl;
	private String uri;
	private String queryString;
	private String method;
	private HttpCookie cookie;
	private HttpSession session;

	private Map<String, String> parameters= new HashMap<>();
	/*
	 * 消息头相关信息
	 */
	private Map<String,String> headers = new HashMap<String,String>();
	
	public HttpRequest( InputStream in) throws EmptyRequestException {
		this.in= in;
		
		parseRequestLine();
		parseHeaders();
		testCookie();
		parseContent();
		parseQueryString();
	}

	/**
	 * 解析请求行
	 * @throws EmptyRequestException 
	 */
	public void parseRequestLine() throws EmptyRequestException{
		System.out.println("解析请求行");
		
		requestLine= readLine();
		if(requestLine.length()==0){
			throw new EmptyRequestException("请求头为空");
		}
		String ss[]= requestLine.split("\\s");
		requestUrl= ss[1];
		method=ss[0];
		
		System.out.println(requestUrl);

		//处理get请求
		if(method.equals("GET")){
			System.out.println("是get请求");
			if(requestUrl.contains("?")){
				String p[]= requestUrl.split("\\?");
				uri=p[0];
				queryString=p[1];
			}else{
				uri= requestUrl;
			}
		}else{
			uri= requestUrl;
		}
		System.out.println(uri+"--------------");
	}

	/**
	 * 解析/设置cookie
	 */
	public void testCookie(){
		
		String cookieStrings=headers.get("Cookie");
		
		if(cookieStrings==null || cookieStrings.equals("")){  //cookie为空
			session= new HttpSession();  //new 一个session
			ServletContainer.addSession(session);   //将session保存到容器中
			cookie= new HttpCookie("sessionid",session.getSessionId());  //设置cookie

		}else{
			//	cookie不为空, 获取sessionid, 从而得到session
			String[] cString= cookieStrings.split(";");
			for(String s1 : cString){
				if(s1.trim().startsWith("sessionid")){
					String sessionid= s1.substring(s1.indexOf("=")+1);
					session= ServletContainer.getSession(sessionid);
					if(session== null){
						session= new HttpSession();  //new 一个session
						ServletContainer.addSession(session);   //将session保存到容器中
						cookie= new HttpCookie("sessionid",session.getSessionId());  //设置cookie
					}else {
						cookie= new HttpCookie("sessionid",session.getSessionId());
					}
				}
			}
		}
	}

	/**
	 * 解析 请求参数
	 */
	public void parseQueryString(){
		if(queryString==null){
			return;
		}
		System.out.println("解析queryStrig  "+queryString);
		URLDecoder decoder= new URLDecoder();
		try {
			queryString= decoder.decode(queryString, ServerContext.URIEncoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String[] ss= queryString.split("&");
		for(String s: ss){
			int index= s.indexOf("=");
			String key= s.substring(0, index).trim();
			String value= s.substring( index+1).trim();
			parameters.put(key, value);
		}
	}
	
	/**
	 * 读一行
	 */
	public String readLine(){
		int i=-1;
		StringBuilder sb= new StringBuilder();
		char c1='a', c2='a';
		try {
			while((i= in.read())!= -1){
				c1=(char) i;
				sb.append(c1);
				if(c2==13&& c1==10){
					break;
				}
				c2=c1;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString().trim();
	}
	
	//解析消息头
		private  void parseHeaders(){
			//读取每一行，若读取某行返回了“\\s”, 说明读到了CRLF, 则消息头读完了。
			System.out.println("开始解析消息头");
			String s= null;
			
			while(!(s= readLine()).equals("")){
				System.out.println(s);
				int index= s.indexOf(":");
				headers.put(s.substring(0, index).trim(), s.substring(index+1).trim());
			}
		}
	
	/**
	 * 解析消息正文
	 */
	private void parseContent(){
		/*
		 * 查看消息头中是否有Content-Length.若没有
		 * 则表示没有消息正文部分.
		 */
		if(headers.containsKey(HttpContext.CONTEN_LENGTH)){
			try{
				System.out.println("开始解析消息正文");
				/*
				 * 判断是否为form表单数据
				 */
				String contentType = headers.get(
					HttpContext.CONTEN_TYPE);
				if("application/x-www-form-urlencoded".equals(contentType)){
					//开始处理form表单数据
					System.out.println("开始处理表单数据");
					//读取正文中的所有字节
					int length = Integer.parseInt(
						headers.get(HttpContext.CONTEN_LENGTH));
					byte[] data = new byte[length];
					in.read(data);
					queryString= new String(data);
				}						
				
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public String getParam(String param){
		return parameters.get(param);
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public String getUri() {
		return uri;
	}

	public HttpCookie getCookie() {
		return cookie;
	}
}
