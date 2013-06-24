/**
 * 
 */
package ss.smtp.sender;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zobo
 *
 */
public class SendingLine {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SendingLine.class);

	private final LinkedBlockingQueue<SendingElement> line = new LinkedBlockingQueue<SendingElement>();

	public void put(SendingElement element) {
		try {
			this.line.put(element);
		} catch (InterruptedException ex) {
			logger.error(ex);
		}
	}
	
	public SendingElement take() {
		try {
			return this.line.take();
		} catch (InterruptedException ex) {
			logger.error(ex);
			return null;
		}
	}
}
