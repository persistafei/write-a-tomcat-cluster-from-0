package tomcat.core;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JarLoader {
	
	public static void init() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		/*URL url= new URL("file:///c:/Users/freedom/Downloads/dom4j-1.6.1.jar");
		URLClassLoader urlClassLoader= new URLClassLoader(new URL[]{url});
		
		Class class1=urlClassLoader.loadClass("org.dom4j.io.SAXReader");*/
		
		//class1.newInstance();
		
		/*ServletClassLoader servletClassLoader=new ServletClassLoader("c:/Users/freedom/Downloads/dom4j-1.6.1.jar");
	
		Class o=servletClassLoader.loadClass("org.dom4j.io.SAXReader.class");
	
		Object obn=o.newInstance();
		System.out.println(obn.getClass());
		System.out.println(";;;;;;");*/
		
		/*
		URL url= new URL("file:///c:/Users/freedom/Downloads/dom4j-1.6.1.jar");
		URLClassLoader urlClassLoader= new URLClassLoader(new URL[]{url});
		
		Class class1=urlClassLoader.loadClass("org.dom4j.io.SAXReader");*/
		
		//class1.newInstance();
		
		String s="file://F:/win10/tomcat1/wtpwebapps/EasyMall/WEB-INF/classes/";
		URL url= new URL(s);
		URLClassLoader urlClassLoader= new URLClassLoader(new URL[]{url});
		
		System.out.println("---");
		Class class1=urlClassLoader.loadClass("cn.tedu.dao.impl.UserDaoImpl");
		System.out.println(class1.newInstance());
	}
	
	public static void main(String[] args) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		init();
	}
}
