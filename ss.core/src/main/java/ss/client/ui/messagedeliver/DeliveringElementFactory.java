/**
 * 
 */
package ss.client.ui.messagedeliver;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.client.ui.messagedeliver.AbstractDeliveringElement.DeliveringElementType;
import ss.domainmodel.Statement;
import ss.global.SSLogger;

/**
 * @author zobo
 *
 */
class DeliveringElementFactory implements IDeliveringElementsFactory {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(DeliveringElementFactory.class);
	
	/* (non-Javadoc)
	 * @see ss.client.ui.messagedeliver.IDeliveringElementsFactory#createList(java.util.Hashtable, java.util.Vector)
	 */
	public AbstractDeliveringElement createList(
			Hashtable allDocs, String highligth, String sphereId) {
		return new ListDeliveringElement(allDocs, highligth, sphereId);
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.messagedeliver.IDeliveringElementsFactory#createReplace(org.dom4j.Document, org.dom4j.Document)
	 */
	public AbstractDeliveringElement createReplace(Document newDoc,
			Document oldDoc, String sphereId, boolean onlyIfExists) {
		return new ReplaceDeliveringElement(newDoc, oldDoc, sphereId, onlyIfExists);
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.messagedeliver.IDeliveringElementsFactory#createSimple(org.dom4j.Document, boolean, boolean)
	 */
	public AbstractDeliveringElement createSimple(Document doc,
			boolean openTreeToMessageId, boolean insertToSelectedOnly, String sphereId) {
		return createSimple(doc, null, openTreeToMessageId, insertToSelectedOnly, sphereId);
	}
	
	/* (non-Javadoc)
	 * @see ss.client.ui.messagedeliver.IDeliveringElementsFactory#createSimple(org.dom4j.Document, java.lang.String, boolean, boolean, java.lang.String)
	 */
	public AbstractDeliveringElement createSimple(Document doc, String typeOfUpdate, boolean openTreeToMessageId, boolean insertToSelectedOnly, String sphereId) {
		return new SimpleDeliveringElement(doc, typeOfUpdate, openTreeToMessageId, insertToSelectedOnly, sphereId);
	}

	public String getLogInfo(AbstractDeliveringElement element){
		try {
		if (element == null){
			return "Null AbstractDeliveringElement";
		}
		if (element.getType() == DeliveringElementType.SIMPLE){
			Statement st = Statement.wrap(SimpleDeliveringElement.class.cast(element).getDoc());
			return st.getSubject();
		} else if (element.getType() == DeliveringElementType.LIST){
			return "ListDeliveringElement with size " + ListDeliveringElement.class.cast(element).getAllMessages().size();
		} else if (element.getType() == DeliveringElementType.REPLACE){
			Document doc = ReplaceDeliveringElement.class.cast(element).getNewDoc();
			if (doc == null){
				return "Nul Doc in ReplaceDeliveringElement";
			}
			Statement st = Statement.wrap(doc);
			return st.getSubject();
		} else {
			return "Undentified AbstractDeliveringElement";
		}
		} catch (Exception ex){
			return " exception in getting logging information from AbstractDeliveringElement";
		}
	}
}
