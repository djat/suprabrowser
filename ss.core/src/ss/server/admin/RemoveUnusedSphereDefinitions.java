package ss.server.admin;

import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import ss.refactor.supraspheredoc.old.Utils;
import ss.server.db.AcrossTableUtils;
import ss.server.db.XMLDB;
import ss.server.domain.service.SupraSphereProvider;

public class RemoveUnusedSphereDefinitions {

  XMLDB xmldb = null;
  @SuppressWarnings("unused")
private static final org.apache.log4j.Logger logger = ss.global.SSLogger
		.getLogger(RemoveUnusedSphereDefinitions.class);

  public RemoveUnusedSphereDefinitions() {
    this.xmldb = new XMLDB();

  }

  public static void main(String[] args) {
    RemoveUnusedSphereDefinitions ru = new RemoveUnusedSphereDefinitions();
    ru.removeSphereDefinitions();

  }

  @SuppressWarnings("unchecked")
public void removeSphereDefinitions() {

    try {
      AcrossTableUtils across = new AcrossTableUtils(this.xmldb);
    
      Hashtable spheres = across.getAllSpheresWithoutDuplicatesTableKeys();
  
      logger.info("SIZE: " + spheres.size());
  
      Document supraSphereDoc = Utils.getUtils( this.xmldb ).getSupraSphereDocument();
  
      for (Enumeration enumerate = spheres.keys(); enumerate.hasMoreElements();) {
        String key = (String) enumerate.nextElement();
  
        Document doc = (Document) spheres.get(key);
  
        // System.out.println("DISPLAY NAME:
        // "+doc.getRootElement().attributeValue("display_name"));
  
        String systemName = doc.getRootElement().attributeValue("system_name");
        String displayName = doc.getRootElement().attributeValue("display_name");
  
        String apath = "//suprasphere/member/sphere[@system_name=\"" + systemName
          + "\"]";
        // System.out.println("APAth : "+apath);
  
        try {
          // TODO handle case where there is one document.
          
		Element sphereElem = (Element) supraSphereDoc.selectObject(apath);
  
        }
        catch (ClassCastException e) {
  
          try {
            Vector vec = new Vector((List) supraSphereDoc.selectObject(apath));
  
            if (vec.size() == 0) {
              if (doc.getRootElement().element("current_table") != null) {
            	  logger.info("REMOVE this doc: " + systemName + " : "
                  + displayName);
                try {
                  String curr = doc.getRootElement().element("current_table")
                    .getText();
  
                  this.xmldb.removeSphereDocWithSystem(doc, curr, systemName);
                  this.xmldb.removeDoc(doc, curr);
                }
                catch (Exception ex) {
                	logger.error(ex.getMessage(), ex);
                }
  
                this.xmldb.removeDoc(doc, systemName);
                this.xmldb.removeDoc(doc, key);
                this.xmldb.removeDoc(doc, "Punya");
                logger.info("REMOVE FROM Punya with : ");
  
                this.xmldb.removeSphereDocWithSystem(doc, "Punya", systemName);
              }
            }
  
            for (int i = 0; i < vec.size(); i++) {
              
			Element one = (Element) vec.get(i);
            }
          }
          catch (SQLException exec) {
            logger.error("Caught SQLException in removeSphereDefinitions",exec);
  
          }
        }
      }
    }
    catch(NullPointerException exc) {
      logger.error("Document Exception", exc);
    }    
  }
}


