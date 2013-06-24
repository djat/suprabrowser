/*
 * Created on Jan 16, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.server.admin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import ss.refactor.supraspheredoc.old.Utils;
import ss.server.db.XMLDB;
import ss.server.domain.service.SupraSphereProvider;

public class ExportXMLDataForEditing {
    
    String fsep = System.getProperty("file.separator");
    String bdir = System.getProperty("user.dir");
   static  XMLDB xmldb = null;
   
   @SuppressWarnings("unused")
   private static final org.apache.log4j.Logger logger = ss.global.SSLogger
		.getLogger(ExportXMLDataForEditing.class);
    
    public ExportXMLDataForEditing() {
        xmldb = new XMLDB();
    }
    
    public static void main(String[] args) {
        
        new ExportXMLDataForEditing();
        //ed.exportXMLFromTable("4821776520500068363","2659990764481176788");
        //ed.exportSphereFromSphere();
        ExportXMLDataForEditing.replaceIntoSphereFromFile();
        
        
    }
    
    
    public static void exportXMLFromTable(String tableId, String messageId) {
        
    	logger.info("Table: "+tableId+ " : "+messageId);
        Document doc = xmldb.getSpecificID(tableId,messageId);
        
        logger.info("Here was the doc: "+doc.asXML());
        
        
    }
    
    public static void replaceIntoSphereFromFile() {
      try {
        String supraSphereName = xmldb.getDBSphere();
        Document sphereDoc = Utils.getUtils( xmldb ).getSupraSphereDocument();
        
        logger.warn("HERE YOU GO: "+sphereDoc.asXML());
        File f = new File (System.getProperty("user.dir")+System.getProperty("file.separator")+supraSphereName+".xml");
        
        SAXReader reader1 = new SAXReader();
        try {
            Document doc = reader1.read(f);
            
            xmldb.replaceDoc(doc,supraSphereName);
            
            
        } catch (Exception e) {
        	logger.error(e.getMessage(), e);
            
        }
      }
      catch(NullPointerException exc) {
        logger.error("Document Exception : "+exc.getMessage(), exc);
      }
        
    }
    
    public static void exportSphereFromSphere() {
      try {
      Document sphereDoc = Utils.getUtils( xmldb ).getSupraSphereDocument();

      // logger.warn("HERE YOU GO: "+sphereDoc.asXML());
      File f = new File(System.getProperty("user.dir")
          + System.getProperty("file.separator") + "dboutput"
          + System.getProperty("file.separator") + "ss" + ".xml");
      OutputFormat format = OutputFormat.createPrettyPrint();

      try {
        FileOutputStream fout = new FileOutputStream(f);
        XMLWriter writer = new XMLWriter(fout, format);
        writer.write(sphereDoc);
        writer.close();

        fout.close();
      }
      catch (FileNotFoundException e) {
    	  logger.error(e.getMessage(), e);
      }
      catch (UnsupportedEncodingException e) {
    	  logger.error(e.getMessage(), e);
      }
      catch (IOException e) {
    	  logger.error(e.getMessage(), e);
      }
    }
    catch (NullPointerException exc) {
      logger.error("Document Exception : "+ exc.getMessage(), exc);
    }
      
    }
    
    public void exportSphereIdFromSphere() {
        
        
        
        
    }
    

}
