/**
 * 
 */
package ss.common.converter;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zobo
 * 
 */
class ConvertingLine {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ConvertingLine.class);

	private final LinkedBlockingQueue<ConvertingElement> line = new LinkedBlockingQueue<ConvertingElement>();

	public void put(ConvertingElement element) {
		try {
			this.line.put(element);
		} catch (InterruptedException ex) {
			logger.error(ex);
		}
	}

	public ConvertingElement take() {
		try {
			return this.line.take();
		} catch (InterruptedException ex) {
			logger.error(ex);
			return null;
		}
	}
}
