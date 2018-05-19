package app;

import tomcat.http.HttpRequest;
import tomcat.http.HttpResponse;
import tomcat.servlet.HttpServlet;

/**
 * 请求展示页面
 */
public class ShowServlet extends HttpServlet{
	/**
	 * 处理请求的方法
	 * @param request
	 * @param response
	 */
	public void service(HttpRequest request,HttpResponse response){
		String page=request.getParam("page");
		response.sendRedirect(page+".html");
	}

}








