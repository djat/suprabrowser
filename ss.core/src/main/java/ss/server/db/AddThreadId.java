/*
 * Created on Mar 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.server.db;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.dom4j.Document;

/**
 * @author david
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class AddThreadId {
    
    public static void main(String[] args) {
        AddThreadId ati = new AddThreadId();
        ati.startForTable("4821776520500068363");
        //ati.listTables();
    }
    private static Random tableIdGenerator = new Random();
    @SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AddThreadId.class);
    
    XMLDB xmldb = new XMLDB();
    
    public AddThreadId() {
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
    
    
    public void startForTable(String table) {
        Vector all = this.xmldb.getAllMessages(table);
        
        for (int i=0;i<all.size();i++) {
            Document doc = (Document)all.get(i);
            if (doc.getRootElement().element("current_sphere")==null) {
                doc.getRootElement().addElement("current_sphere").addAttribute("value",table);
                this.xmldb.replaceDoc(doc,table);
                
            }
            
            //String threadId = new Long(getNextTableId()).toString();
            /*
            
            if (doc.getRootElement().element("response_id")==null) {
                
                Vector children = xmldb.getChildren(table,doc.getRootElement().element("message_id").attributeValue("value"));
                
                Hashtable thread = xmldb.getAllOfThread(doc,table);
                
                Vector responses = (Vector)thread.get("responses");
                
                if (doc.getRootElement().element("thread_id")==null) {
               
                doc.getRootElement().addElement("thread_id").addAttribute("value",threadId);
                xmldb.replaceDoc(doc,table);
                
                }
                
                
                
                for (int j=0;j<responses.size();j++) {
                    
                    Document response = (Document)responses.get(j);
                    
                    if (response.getRootElement().element("thread_id")!=null) {
                        
                         response.getRootElement().addElement("thread_id").addAttribute("value",threadId);
                         xmldb.replaceDoc(response,table);
                         
                         }
                    
                }
                
                //logger.info("doc: "+doc.getRootElement().element("subject").attributeValue("value")+ " has this many responses..."+responses.size());
                
                //logger.info("COMPARE: "+children.size()+" : "+responses.size());
                
              
                
                
                
            }
            else {
                
                if (doc.getRootElement().element("thread_id")==null) {
                    
                    doc.getRootElement().addElement("thread_id").addAttribute("value",threadId);
                    xmldb.replaceDoc(doc,table);
                    
                    }
            
                        
                
            }
            */
            
        }
        
        
        
        

    }
    
    
    
    
    

}
