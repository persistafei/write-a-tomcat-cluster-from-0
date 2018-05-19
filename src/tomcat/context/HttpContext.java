package tomcat.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class HttpContext {
	
	public static final int CR= 13;
	public static final int LF= 10;
	
	public static String CONTEN_LENGTH="Content-Length";
	public static String CONTEN_TYPE="Content-Type";

	public static String LOCATION="location";
	
	
	public static Map<String , String> mimeMappin= new HashMap<>();
	public static Map<Integer , String> statusReson= new HashMap<>();
	
	
	
	static{
		parseMimeMapping();
		parseStatusReason();
	}
	
	public static void parseMimeMapping(){
		SAXReader reader= new SAXReader();
		try {
			Document doc= reader.read("conf/web.xml");
			Element root= doc.getRootElement();
			
			List<Element> elements= root.elements("mime-mapping");
			
			for(Element e: elements){
				String key= e.elementText("extension");
				String value= e.elementText("mime-type");
				mimeMappin.put(key, value);
			}
			
			
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
	}
	
	public static  void parseStatusReason(){
		statusReson.put(200, "OK");
		statusReson.put(302, "MOVED PERMANENTLY");
		statusReson.put(404, "NOT FOUND");
		statusReson.put(500, "SERVER INTERNAL ERROR");
	}
	
	public static String getStatusReasonByCode(int code) {
		return statusReson.get(code);
	}
	
	public static String getMimeType(String extension){
		return mimeMappin.get(extension);
	}
	

}
