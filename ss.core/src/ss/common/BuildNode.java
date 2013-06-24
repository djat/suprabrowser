/*
 * Created on Mar 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.common;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.server.db.XMLDB;

/**
 * @author david
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class BuildNode {
	
    XMLDB xmldb = new XMLDB();
    
    @SuppressWarnings("unused")    
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(BuildNode.class);
    
    public BuildNode() {
        
        
    }
       
    
    @SuppressWarnings("unchecked")
	public Vector getEntireThreadFromResponses(String sphereId,Hashtable responses) {
        Hashtable rootThreads = new Hashtable();
        Vector reallyAll = new Vector();
        
        for (Enumeration num=responses.keys();num.hasMoreElements();) {
            
            String key = (String)num.nextElement();
            Document doc = (Document)responses.get(key);
            String threadId = null;
        try {
            threadId = doc.getRootElement().element("thread_id").attributeValue("value");
        } catch (Exception e) {
            //logger.info("EXCEPTION ON THIS DOC: "+doc.asXML());
            
            
        }
            
            if (!rootThreads.containsKey(threadId)) {
                
            Vector thread = this.xmldb.getOnlyThread(sphereId,threadId,responses);
            //logger.info("THREad: "+thread.size());
            reallyAll.addAll(thread);
            
            
            //rootThreads.put(threadId,thread);
            
            }
        }
        
        //logger.info("THIS MANY THREads: "+reallyAll.size());
        
        //MessagesMutableTreeNode top = createNodeFromThreads(reallyAll);
        return reallyAll;
        //return top;
    }

    /*
     * 
     * Currently not used.
     */
//    public MessagesMutableTreeNode createNodeFromThreads(Vector all) {
//        Vector threads = new Vector();
//        
//        Document createDoc = DocumentHelper.createDocument();
//    Element messages = createDoc.addElement("Messages").addElement("i").addText("Messages");
//
//        MessagesMutableTreeNode top = new MessagesMutableTreeNode(null, "messages", "none", null, null);
//        
//        
//        
//    for (int i=all.size()-1;i>=0;i--) { // for reverse order
//      //for (int i=0;i<all.size();i++) {
//    
//      //  logger.info("trying one");
//        Document doc = null;
//        
//                try {
//                    doc = (Document)all.get(i);
//                    //logger.info("doc: "+doc.asXML());
//                } catch (Exception e) {
//                	logger.error(e.getMessage(), e);
//                }
//              
//                    
//                  Element viewDoc = doc.getRootElement();
//        
//        Element response = viewDoc.element("response_id");
//        String responseId = null;
//        if (response==null) {
//        //    logger.info("Response null");
//            
//            MessagesMutableTreeNode rootLevel = null;//new MessagesMutableTreeNode(doc.getRootElement().element("subject").attributeValue("value"),doc.getRootElement().element("message_id").attributeValue("value"),null,doc.getRootElement().element("type").attributeValue("value"));
//            top.add(rootLevel);
//            //all.remove(i);
//            //i--;
//            
//        }
//        else {
//      //      logger.info("no, it has a response...");
//            
//            responseId = response.attributeValue("value");
//            
//        
//        //Enumeration enumer = top.breadthFirstEnumeration();
//
//     for (Enumeration enumer = top.preorderEnumeration();enumer.hasMoreElements();) {
//
//                    // Get the next element in the enumereration, add it to a
//                    // temporary node
//                    // this enumer actually represents the usenode asset, is
//                    // not a
//                    // copy
//                    MessagesMutableTreeNode tempnode = (MessagesMutableTreeNode) enumer
//                    
//                            .nextElement();
//                    if (!tempnode.isRoot()) {
//
//                    // Get the filename associated with the node
//                    String message_test = tempnode.getMessageId();
//
//                    /////System.out.println("testing id:
//                    // 1068516532887"+message_test);
//
//                    // Now loop through all of the responses to see if the
//                    // response_id from any of them equal the message_id
//                    // from the temporary node
//
//                    if (message_test.equals(responseId)) {
//
//                        //     //System.out.println("found the one:
//                        // "+response_id);
//
//                      MessagesMutableTreeNode tmp = null;// new MessagesMutableTreeNode(doc.getRootElement().element("subject").attributeValue("value"),doc.getRootElement().element("message_id").attributeValue("value"),null,doc.getRootElement().element("type").attributeValue("value"));
//
//                        tempnode.add(tmp);
//                  //      all.remove(i);
//                   //     i--;
//
//                        
//                    }
//                    }
//        
//      }
//      
//    
//    }
//    
//
//    }
//    //logger.info("RETURNING TOP: "+top.getChildCount());
//    return top;       
//}
}