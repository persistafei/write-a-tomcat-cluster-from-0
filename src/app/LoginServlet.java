package app;

import tomcat.http.HttpRequest;
import tomcat.http.HttpResponse;
import tomcat.servlet.HttpServlet;

/**
 * 完成登录操作
 * @author adminitartor
 *
 */
public class LoginServlet extends HttpServlet{
	public void service(HttpRequest request,HttpResponse response){
		try {
			
			//获取用户登录信息
			String username = request.getParam("username");
			String password = request.getParam("password");		
			System.out.println("用户名和密码"+username+password);
			//若查到了该用户,并且密码匹配则登录成功
			response.sendRedirect("login_suc.html");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}







