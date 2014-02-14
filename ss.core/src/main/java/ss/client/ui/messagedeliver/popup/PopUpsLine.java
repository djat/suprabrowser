/**
 * 
 */
package ss.client.ui.messagedeliver.popup;

import java.util.LinkedList;
import java.util.Queue;

import org.dom4j.Document;

/**
 * @author zobo
 *
 */
class PopUpsLine {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PopUpsLine.class);
	
	private final Queue<PopUpElement> line;
	
	PopUpsLine(){
		this.line = new LinkedList<PopUpElement>();
	}

	PopUpElement take(){
		synchronized (this.line) {
			return this.line.poll();
		}
	}
	
	void offer( final Document doc, final String messageId, final boolean popupAnyway ){
		if (messageId == null){
			throw new NullPointerException("messageId is null");
		}
		if (doc == null){
			throw new NullPointerException("document is null");
		}
		synchronized (this.line) {
			if (!isExisted(messageId)) {
				if (logger.isDebugEnabled()) {
					logger.debug("Document putted to queue");
					logger.debug("messageId: " + messageId + ", popupAnyway: " + popupAnyway + ", doc: " + doc.asXML());
				}
				this.line.offer(new PopUpElement(doc, messageId, popupAnyway));
			} else {
				logger.error("Such element already in popups line, not adding");
			}
		}
	}
	
	private boolean isExisted( final String messageId ){
		if (messageId == null){
			throw new NullPointerException("messageId is null");
		}
		for (PopUpElement element : this.line){
			if (element.getMessageId().equals(messageId)){
				return true;
			}
		}
		return false;
	}
	
	void remove(final String messageId){
		if (messageId == null){
			throw new NullPointerException("messageId is null");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("removing element with messageId: " + messageId);
		}
		synchronized (this.line) {
			if (this.line.isEmpty()){
				return;
			}
			PopUpElement toRemove = null;
			for (PopUpElement element : this.line){
				if (element.getMessageId().equals(messageId)){
					toRemove = element;
					break;
				}
			}
			if (toRemove != null) {
				this.line.remove( toRemove );
				if (logger.isDebugEnabled()) {
					logger.debug("Element with messageId " + messageId + " removed from queue");
				}
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("No such element with messageId " + messageId + " in queue");
				}
			}
		}
	}
}
