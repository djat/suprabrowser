/**
 * 
 */
package ss.client.ui.messagedeliver;

import java.util.Hashtable;

import org.dom4j.Document;

/**
 * @author zobo
 *
 */
public interface IDeliveringElementsFactory {
	AbstractDeliveringElement createSimple(final Document doc, String typeOfUpdate, final boolean openTreeToMessageId, final boolean insertToSelectedOnly, String sphereId);
	
	AbstractDeliveringElement createSimple(final Document doc, final boolean openTreeToMessageId, final boolean insertToSelectedOnly, String sphereId);
	
	AbstractDeliveringElement createList(final Hashtable allDocs, String highligth, String sphereId);
	
	AbstractDeliveringElement createReplace(final Document newDoc, final Document oldDoc, String sphereId, boolean onlyIfExists);
	
	String getLogInfo(AbstractDeliveringElement element);
}
