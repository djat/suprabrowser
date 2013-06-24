package ss.server.admin;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import ss.refactor.supraspheredoc.old.Utils;
import ss.server.db.XMLDB;
import ss.server.domain.service.SupraSphereProvider;
import ss.util.XMLSchemaTransform;


public class SaveSphereConvertUser {
	
	/*
	 * Created on Mar 18, 2005
	 *
	 * TODO To change the template for this generated file go to
	 * Window - Preferences - Java - Code Generation - Code and Comments
	 */


	/**
	 * @author david
	 *
	 * TODO To change the template for this generated type comment go to
	 * Window - Preferences - Java - Code Generation - Code and Comments
	 */
	
	    
	    public static void main(String[] args) {
	        
	        
	    	SaveSphereConvertUser sscu = new SaveSphereConvertUser();
	    	
	    	//sscu.startForTable("4821776520500068363");
	        sscu.listTables();
	    }
	    
	    private static Random tableIdGenerator = new Random();
	    
	    @SuppressWarnings("unused")
		private static final org.apache.log4j.Logger logger = ss.global.SSLogger
				.getLogger(SaveSphereConvertUser.class);
	    
	    XMLDB xmldb = new XMLDB();
	    public SaveSphereConvertUser() {
	    }
	    
	    public synchronized long getNextTableId() {
	        return Math.abs(tableIdGenerator.nextLong());
	    }
	    
	    public void listTables() {
	      try {
	        List sphereIds = this.xmldb.getAllSphereIds();
	        for (int i=0;i<sphereIds.size();i++) {
            String one = (String)sphereIds.get(i);
            logger.info("ONE ALTER: "+one);
                
	     // This has been refactored. This database conversion no longer applies.
            
            /*xmldb.alterTableAddSpecificColumns(one,"thread_id","varchar(100)");
            xmldb.alterTableAddSpecificColumns(one,"message_id","varchar(100)");
	            
            xmldb.alterTableAddSpecificColumns(one,"isResponse","int");
	          xmldb.alterTableAddSpecificColumns(one,"used","datetime");
	          xmldb.alterTableAddSpecificColumns(one,"modified","datetime");*/
	        }
	        for (int i=0;i<sphereIds.size();i++) {
	          String one = (String)sphereIds.get(i);
	          startForTable(one);
	        }
        }
        catch(SQLException exc) {
          logger.error("SQL Exception occured while getting table list", exc);
        }
	    }
	    
	    
	    @SuppressWarnings("unchecked")
		public void startForTable(String table) {
	      
        Document supraSphereDoc = null;
        String apath = null;
	      try {
	        Document sphereDoc = this.xmldb.getSphereDefinition("K Capital",table);
	        
	        if (sphereDoc!=null) {
	          Document sphereDef = this.xmldb.getSphereDefinition(table,table);
	          if (sphereDef==null) {
	            //logger.warn("could not find this one: "+sphereDoc.getRootElement().attributeValue("display_name"));
	            
	            
	            XMLSchemaTransform.setThreadAndOriginalAsMessage(sphereDoc);
	            Document newSphereDoc = sphereDoc;
	            if (newSphereDoc.getRootElement().element("response_id")==null) {
	              this.xmldb.insertDoc(newSphereDoc,table);
	            }
	          }
	        }
	        
	        supraSphereDoc = Utils.getUtils(this.xmldb).getSupraSphereDocument();
	        if (supraSphereDoc==null) {
	          logger.warn("IT was null");
	        }
	        
	        apath = "//suprasphere/member/sphere[@system_name='"
	          + table
	          + "' and @enabled='true']";
	        
	        Element elem = (Element)supraSphereDoc.selectObject(apath);
	        //logger.warn("elem :"+elem.asXML());
	        
	        Element parent = elem.getParent();
	        logger.warn("USERNAME: "+parent.attributeValue("login_name"));
	        String username = parent.attributeValue("login_name");
	        String loginSphere = this.xmldb.getUtils().getLoginSphereSystemName(parent.attributeValue("login_name"));
	        logger.warn("login sphere: "+loginSphere);
	        Document contact = this.xmldb.getContactDoc(loginSphere,username);
	        
	        
	        if (contact==null) {
	          logger.warn("contact null: "+username);
	          
	          Document contact2 = this.xmldb.getContactDoc("K Capital",username);
	          if (contact2!=null) {
	            logger.warn("not null there");
	            if (contact2.getRootElement().element("current_sphere")!=null) {
	              contact2.getRootElement().element("current_sphere").detach();
	            }
	            this.xmldb.insertDoc(contact2,loginSphere);
	            contact = contact2;
	          }
	        }
	        
	        Document newContact = XMLSchemaTransform.addLocationToDoc(contact,contact,"sphere::141.154.89.75:3074,K Capital",loginSphere,"K Capital",table,parent.attributeValue("display_name"));
	        this.xmldb.replaceDoc(newContact,loginSphere);
	        this.xmldb.insertDoc(newContact,table);
	        
	        
	      }
	      catch (ClassCastException cce) {
	        logger.error("catchclass cast exception", cce);
	      }
	      catch(NullPointerException exc) {
	        logger.error("Document Exception", exc);
	      }
	    }
	}





