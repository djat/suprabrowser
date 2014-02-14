/**
 * 
 */
package ss.smtp.defaultforwarding;

import java.util.concurrent.LinkedBlockingQueue;

import org.dom4j.Document;

import ss.domainmodel.Statement;

/**
 * @author zobo
 *
 */
public class ForwardingLine {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ForwardingLine.class);

	private final LinkedBlockingQueue<ForwardingElement> line = new LinkedBlockingQueue<ForwardingElement>();

	public void put(ForwardingElement element) {
		if (check(element.getDoc())){
			try {
				this.line.put(element);
			} catch (InterruptedException ex) {
				logger.error(ex);
			}
		}
	}
	
	private boolean check(Document doc){
		if (doc == null){
			return false;
		}
		Statement statement = Statement.wrap(doc);
		if (statement.isTerse() || statement.isMessage() || statement.isEmail()){
			return true;
		}
		return false;
	}

	/**
	 * @return
	 */
	public ForwardingElement take() {
		try {
			return this.line.take();
		} catch (InterruptedException ex) {
			logger.error(ex);
			return null;
		}
	}
}
