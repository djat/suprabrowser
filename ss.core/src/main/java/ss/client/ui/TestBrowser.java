package ss.client.ui;

import java.io.IOException;

import org.apache.log4j.Logger;

import ss.global.SSLogger;

public class TestBrowser {
	
	private static final Logger logger = SSLogger.getLogger(TestBrowser.class);
	
	public static void main(String[] args) {
		TestBrowser tb = new TestBrowser();
		tb.exec();
		
	}
	
	public void exec() {
		
		String link ="http://www.suprasphere.com";
		String exec = "/home/dankosedin/firefox/firefox -remote \"\"openurl("+link +",new-tab)\"\"";
 	   
 	   logger.info("heres the command: "+exec);
 	   		
			try {
				
				Runtime.getRuntime().exec(exec);
				
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		
	}
	
	

}
