package tomcat.core;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ServletClassLoader extends ClassLoader{
	private String rootDir;
	public ServletClassLoader(String rootDir){
		this.rootDir= rootDir;
	}

	public byte[] getClassData(String fullClassName){

		String path= rootDir+ "/"+ fullClassName.replace('.', '/')+".class";
		System.out.println(path+"--------------");
		byte[] classBinary= null;
		try (
				FileInputStream fin= new FileInputStream(path);
				ByteArrayOutputStream baout= new ByteArrayOutputStream();
				){
			byte[] data= new byte[1024];
			int len=-1;
			while((len= fin.read(data))!= -1){
				baout.write(data,0,len);
			}
			classBinary= baout.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return classBinary;
	}

	/*public void loadJar() {
		URL url = null;
		try {
			File classes_path = new File(rootDir);
			File lib = new File(classes_path.getParent() + "/lib");
			File[] jars = lib.listFiles();
			URL[] urls = new URL[jars.length];
			for (int i = 0; i < jars.length; i++) {
				urls[i] = new URL(jars[i].getAbsolutePath());
			}
			URLClassLoader urlClassLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());

		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}*/

	@Override
	public Class<?> findClass(String fullClassName) throws ClassNotFoundException {

		Class<?>c= findLoadedClass(fullClassName);
		if(c== null){
			//c= getSystemClassLoader().loadClass(fullClassName);

			if(c== null){
				byte[] data= getClassData(fullClassName);
				if(data==null){
					throw new ClassNotFoundException();
				}
				c= defineClass(fullClassName,data , 0, data.length);
			}
		}
		return c;
	}
}
