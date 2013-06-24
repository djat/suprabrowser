/**
 * 
 */
package ss.smtp.responcetosphere;

import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;

import ss.smtp.sender.SendingElement;

/**
 * @author zobo
 * 
 */
public class ResponceLine {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ResponceLine.class);

	private final LinkedBlockingQueue<ResponceElement> line = new LinkedBlockingQueue<ResponceElement>();

	private final Hashtable<String, ResponceElement> waitingLine = new Hashtable<String, ResponceElement>();

	private Object sync = new Object();

	public void put(ResponceElement element) {
		try {
			this.line.put(element);
		} catch (InterruptedException ex) {
			logger.error(ex);
		}
	}

	public ResponceElement take() {
		try {
			return this.line.take();
		} catch (InterruptedException ex) {
			logger.error(ex);
			return null;
		}
	}

	/**
	 * @param sendingElement
	 */
	public void addSuccessfull(SendingElement sendingElement) {
		synchronized (this.sync) {
			String messageId = sendingElement.getMessageId();
			ResponceElement responceElement = this.waitingLine.get(messageId);
			if (responceElement != null){
				responceElement.add(ResponceElementFactory.createSuccessfull(sendingElement));
				check(responceElement);
			}
		}
	}

	/**
	 * @param sendingElement
	 */
	public void addFailed(SendingElement sendingElement) {
		synchronized (this.sync) {
			String messageId = sendingElement.getMessageId();
			ResponceElement responceElement = this.waitingLine.get(messageId);
			if (responceElement != null){
				responceElement.add(ResponceElementFactory.createFailed(sendingElement));
				check(responceElement);
			}
		}
	}
	
	private void check(ResponceElement responceElement){
		if (responceElement.isFull()){
			this.waitingLine.remove(responceElement);
			try {
				this.line.put(responceElement);
			} catch (InterruptedException ex) {
				logger.error(ex);
			}
		}
	}

	/**
	 * @param messageId
	 */
	public void initiate(String messageId, String sphereId, int count) {
		synchronized (this.sync) {
			if (count < 1){
				return;
			}
			if (this.waitingLine.get(messageId) == null) {
				this.waitingLine.put(messageId, ResponceElementFactory
						.createBlank(messageId, sphereId, count));
			}
		}
	}
	
	
}
