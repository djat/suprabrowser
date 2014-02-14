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
public class ListDeliveringElement extends AbstractDeliveringElement {
	
	private final String highligth;
	
	private final Hashtable allDocs;
	
	ListDeliveringElement(final Hashtable allDocs, final String highligth, final String sphereId) {
		super(DeliveringElementType.LIST, sphereId);
		this.highligth = highligth;
		this.allDocs = allDocs;
	}

	String getHighligth() {
		return this.highligth;
	} 
	
	public Hashtable getAllMessages() {
		return this.allDocs;
	}
}
