/**
 * 
 */
package ss.client.ui.messagedeliver;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zobo
 *
 */
public class MessagesLine {
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(MessagesLine.class);
	
	private final LinkedBlockingQueue<AbstractDeliveringElement> line = new LinkedBlockingQueue<AbstractDeliveringElement>();
	
	void put(AbstractDeliveringElement element){
		try {
			if (logger.isDebugEnabled()){
				logger.debug("recieved Document to queue: " + DeliverersManager.FACTORY.getLogInfo(element));
			}
			this.line.put(element);
		} catch (InterruptedException ex) {
			logger.error("Cannot put Document in queue",ex);
		}
	}
	
	AbstractDeliveringElement take(){
		try {
			AbstractDeliveringElement element = this.line.take();
			if (logger.isDebugEnabled()){
				logger.debug("Document taken from queue: " + DeliverersManager.FACTORY.getLogInfo(element));
			}
			return element;
		} catch (InterruptedException ex) {
			logger.error("Cannot take Document from queue",ex);
			return null;
		}
	}
}
