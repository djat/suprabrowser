/**
 * 
 */
package ss.smtp.sender;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zobo
 *
 */
public class SendingResultStatusLine {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SendingResultStatusLine.class);
	
	class SendingElementSendStatus {
		
		private final SendingElement element;
		
		private final boolean sent;

		public SendingElement getElement() {
			return this.element;
		}

		public boolean isSent() {
			return this.sent;
		}

		SendingElementSendStatus(SendingElement element, boolean sent) {
			super();
			this.element = element;
			this.sent = sent;
		}
	}

	private final LinkedBlockingQueue<SendingElementSendStatus> line = new LinkedBlockingQueue<SendingElementSendStatus>();

	public void put(SendingElement element, boolean sent) {
		try {
			this.line.put( new SendingElementSendStatus(element, sent) );
		} catch (InterruptedException ex) {
			logger.error(ex);
		}
	}
	
	public SendingElementSendStatus take() {
		try {
			return this.line.take();
		} catch (InterruptedException ex) {
			logger.error(ex);
			return null;
		}
	}
}
