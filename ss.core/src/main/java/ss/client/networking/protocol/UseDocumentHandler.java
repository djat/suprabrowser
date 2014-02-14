/**
 * Jul 4, 2006 : 6:27:36 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class UseDocumentHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(UseDocumentHandler.class);

	private final DialogsMainCli cli;

	public UseDocumentHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handleUseDocument(Hashtable update) {
	}

	public String getProtocol() {
		return SSProtocolConstants.USE_DOCUMENT;
	}

	public void handle(Hashtable update) {
		handleUseDocument(update);

	}

	@SuppressWarnings("unchecked")
	public void useDocument(final Hashtable session,
			final org.dom4j.Document sendDoc, String increment) {

		logger.info("Calling useDocument!!");
		Hashtable toSend = (Hashtable) session.clone();

		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL, SSProtocolConstants.USE_DOCUMENT);
		update.put(SessionConstants.SESSION, toSend);

		update.put(SessionConstants.DOCUMENT, sendDoc);
		update.put(SessionConstants.INCREMENT, increment);

		this.cli.sendFromQueue(update);

		logger.info("Sent usedocument");

	}

}
