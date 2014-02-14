package ss.common;

import java.io.File;
import java.util.Arrays;

import org.dom4j.Document;

public class RecursiveList {

	      
	    
	    
	    public void listDirContents(Document rootDoc, File someDirectory, FileMonitor monitor) {
	        
	        String[] fileOrDirName = someDirectory.list(); 
	        Arrays.sort(fileOrDirName);
	        for (int i = 0; i < fileOrDirName.length; i++) {
	           
	            File f = new File(someDirectory, fileOrDirName[i]);
	            
	            //if (f.getAbsolutePath().endsWith("txt")||(f.getAbsolutePath().endsWith("java"))) {
	            	
	              
	                
	            //}
	            
	            if (f.isDirectory()) {
	            	
	            	monitor.addFile (f);
	                listDirContents(rootDoc,f, monitor);
	                
	            }
	            else {
	                
	                monitor.addFile (f);
	            }
	        }
	    }
	    
	}

