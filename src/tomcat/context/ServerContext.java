package tomcat.context;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import tomcat.core.ServletClassLoader;

public class ServerContext {
	
	public static  String protocol;
	public static  String URIEncoding;
	public static int port;
	public static int pool;

	public static String rootDir;
	public static ServletClassLoader classLoader;
	
	public static Map<String, String> servletMapping= new HashMap<>();

	static{
		init();
	}
	
	public static void init(){
		parseServerConfig();
		initWebappPath();

		parseWebXml();
		classLoader= new ServletClassLoader(rootDir+"/WEB-INF/classes");
	}

	/*
	解析服务器的基本属性
	 */
	public static void parseServerConfig(){
		SAXReader reader= new SAXReader();
		try {
			Document doc= reader.read("conf/serverConfig.xml");
			Element root= doc.getRootElement();
			
			protocol= root.attributeValue("protocol");
			URIEncoding= root.attributeValue("URIEncoding");
			port= Integer.parseInt(root.attributeValue("port"));
			pool= Integer.parseInt(root.attributeValue("pool"));
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	/*
		解析web应用的 路径
	 */
	static void initWebappPath() {
		try(
				BufferedReader breader= new BufferedReader(new InputStreamReader(new FileInputStream("conf/startup.bat")));
		){
			String s= null;
			while((s= breader.readLine())!= null){
				String regex= "set\\s+tomcat_home\\s*=.*";
				s= s.toLowerCase();
				if(s.matches(regex)){
					rootDir= s.substring(s.indexOf("=")+1).toString();
				}
			}
			if(rootDir== null){
				throw new FileNotFoundException();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	   解析web应用的 web.xml
	 */
	public static void parseWebXml() {
		try {
			File web_inf= new File(rootDir, "WEB-INF");
			File web_xml= new File(web_inf, "web.xml");

			SAXReader reader= new SAXReader();
			Document document = reader.read(web_xml);
			Element root= document.getRootElement();
			List<Element > servEles= root.elements("servlet");
			List<Element > urlEles= root.elements("servlet-mapping");
			Map<String, String > servMap = new HashMap<>();
			Map<String, String > urlMap = new HashMap<>();
			//servletMapping = new HashMap<>();   // url--> class
			for(Element e: servEles){
				servMap.put(e.elementText("servlet-name"), e.elementText("servlet-class"));
			}
			for(Element e: urlEles){
				urlMap.put(e.elementText("servlet-name"), e.elementText("url-pattern"));
			}
			for(Map.Entry<String, String> entry: urlMap.entrySet()){
				servletMapping.put(entry.getValue(), servMap.get(entry.getKey()));
			}
			System.out.println(servletMapping);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getClassName(String uri){
		return servletMapping.get(uri);
	}

	public static void main(String[] args) {

	}
}
