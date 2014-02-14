/**
 * 
 */
package ss.client.ui.balloons;

import org.dom4j.Document;

import ss.client.ui.MessagesPane;

/**
 * @author zobo
 *
 */
public class BalloonElement {
	
	static enum BalloonTypes {
		DRAGANDDROP, REPLY, SIMPLE 
	}
	
	final private String messageId;
	
	final private Document doc;
	
	final private Document replyToThisDoc;
	
	final private boolean author;
	
	final private MessagesPane pane;
	
	final private BalloonTypes type;
	
	BalloonElement(final String messageId, final Document doc, final Document replyToThisDoc, final boolean author, final MessagesPane pane, final BalloonTypes type) {
		super();
		this.messageId = messageId;
		this.doc = doc;
		this.replyToThisDoc = replyToThisDoc;
		this.author = author;
		this.pane = pane;
		this.type = type;
	}

	Document getDoc() {
		return this.doc;
	}

	String getMessageId() {
		return this.messageId;
	}

	boolean isAuthor() {
		return this.author;
	}

	Document getReplyToThisDoc() {
		return this.replyToThisDoc;
	}

	MessagesPane getPane() {
		return this.pane;
	}

	BalloonTypes getType() {
		return this.type;
	}
}
