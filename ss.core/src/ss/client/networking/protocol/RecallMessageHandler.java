/**
 * Jul 4, 2006 : 5:38:44 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.networking.PostponedUpdate;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class RecallMessageHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RecallMessageHandler.class);

	private final DialogsMainCli cli;
	
	public RecallMessageHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	@SuppressWarnings("unchecked")
	public void handleRecallMessage(final Hashtable update) {
		// System.out.println("got protocol recall message");
		update.put(SessionConstants.IS_UPDATE, SSProtocolConstants.RECALL);
		this.cli.callInsert( new PostponedUpdate( update ));
	}

	public String getProtocol() {
		return SSProtocolConstants.RECALL_MESSAGE;
	}

	public void handle(Hashtable update) {
		handleRecallMessage(update);
	}

	@SuppressWarnings("unchecked")
	public void recallMessage(Hashtable session, Document doc, String sphere) {
		logger.info("creating send session");
		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.RECALL_MESSAGE);
		update.put(SessionConstants.DOCUMENT, doc);
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.SPHERE_ID2, sphere);
		this.cli.sendFromQueue(update);
	}

}
