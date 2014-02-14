/**
 * 
 */
package ss.client.ui.messagedeliver.popup;

import org.dom4j.Document;

/**
 * @author zobo
 *
 */
class PopUpElement {
	private final Document doc;
	
	private final String messageId;
	
	private final boolean popupAnyway;

	PopUpElement(final Document doc, final String messageId, final boolean popupAnyway) {
		super();
		this.doc = doc;
		this.messageId = messageId;
		this.popupAnyway = popupAnyway;
	}

	Document getDoc() {
		return this.doc;
	}

	String getMessageId() {
		return this.messageId;
	}

	boolean isPopupAnyway() {
		return this.popupAnyway;
	}
}
