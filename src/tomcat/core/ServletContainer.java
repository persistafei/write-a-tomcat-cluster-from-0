package tomcat.core;

import tomcat.context.ServerContext;
import tomcat.http.HttpSession;
import tomcat.servlet.HttpServlet;
import tomcat.core.ServletClassLoader;

import java.util.*;

public class ServletContainer {
	
	private static ServletClassLoader classLoader= ServerContext.classLoader;
	private static Map<String, HttpServlet> servletObjects= new HashMap<>();  //servlet OBJECTS
	private static Map<String , Long>lastrun= new HashMap<>();
	private static Map<String, HttpSession> sessionMap= new HashMap<>();
	static{

	    /*
	        定时器 ,实现servlet生命周期
	     */
		Timer timer= new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				long curtime= System.currentTimeMillis();
				Set<Map.Entry<String, Long>> es= lastrun.entrySet();
				//遍历
				for(Map.Entry<String,Long> e: es){

					long inter= (curtime-e.getValue())/1000/60;
					if(inter>= 1){
                        servletObjects.remove(e.getKey());
					}
				}
			}
		}, 1000*60);  //1分钟运行一次
	}

    /**
     * '获取servlet实例
     * @param uri
     * @return
     */
	public static  HttpServlet getServlet(String uri){
		HttpServlet servlet= servletObjects.get(uri);
		if(servlet==null){
			servlet= createServletInstance(uri);
		}
        long curtime= System.currentTimeMillis();
		lastrun.put(uri, curtime);  //将uri请求和其 请求的时刻 保存
		return servlet;
	}

    /**
     * 反射实例化
     * @param uri
     * @return
     */
	static HttpServlet createServletInstance(String uri){
			HttpServlet servlet = null;
			try {
			    String className= ServerContext.getClassName(uri);
				Class c= classLoader.findClass(className);
				servlet= (HttpServlet) c.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
            servletObjects.put(uri, servlet);
		
			return servlet;
	}

	public static HttpSession getSession(String sessionId){
		return sessionMap.get(sessionId);
	}
	public static void addSession(HttpSession session){
		sessionMap.put(session.getSessionId(), session);
	}
}
