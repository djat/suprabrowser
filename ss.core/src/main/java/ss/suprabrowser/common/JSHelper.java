package ss.suprabrowser.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.apache.log4j.Logger;

import ss.global.SSLogger;

public class JSHelper {
    private static Logger logger = SSLogger.getLogger(JSHelper.class);    
    
    private static final Hashtable<String, String> cachedJs = new Hashtable<String, String>(); 

	public static String readJSFromFile(String fileName){
	    if(cachedJs.get(fileName)==null)
	    {
		try{
			// Change this back to load from the jar at time of deployment --DJ@
			//InputStream is = JSHelper.class.getClassLoader().getResourceAsStream(fileName);
			File f = new File(fileName);
			InputStream is = new FileInputStream(f);
			byte[] data = new byte[is.available()];
			is.read(data);
			String js = new String(data);
			cachedJs.put(fileName, js);
			return js;
		}catch(IOException fnfe){
			logger.error("FILENAME: "+fileName,fnfe);			
		}
		return null;
	    } else {
		return cachedJs.get(fileName);
	    }
	}
	
	public static void main(String[] args){
		logger.info(JSHelper.readJSFromFile("html/popup.js"));
	}
	
	
}
