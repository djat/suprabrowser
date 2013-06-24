/**
 * 
 */
package ss.client.ui.messagedeliver;

import org.dom4j.Document;


/**
 * @author zobo
 *
 */
public class SimpleDeliveringElement extends AbstractDeliveringElement{

	final private Document doc;
	final private String typeOfUpdate;
    final private boolean openTreeToMessageId;
    final private boolean insertToSelectedOnly;
    
	public SimpleDeliveringElement(final Document doc, String typeOfUpdate, final boolean openTreeToMessageId, final boolean insertToSelectedOnly, final String sphereId) {
		super(DeliveringElementType.SIMPLE, sphereId);
		this.doc = doc;
		this.typeOfUpdate = typeOfUpdate;
		this.openTreeToMessageId = openTreeToMessageId;
		this.insertToSelectedOnly = insertToSelectedOnly;
	}

	Document getDoc() {
		return this.doc;
	}

	boolean isInsertToSelectedOnly() {
		return this.insertToSelectedOnly;
	}

	boolean isOpenTreeToMessageId() {
		return this.openTreeToMessageId;
	}

	public String getTypeOfUpdate() {
		return this.typeOfUpdate;
	}
}
