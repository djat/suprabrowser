package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;


/**
 * TODO:#member-refactoring
 * 
 * Saves tab order to all contacts documents  
 * 
 */
public class SaveTabOrderToContactHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SaveTabOrderToContactHandler.class);

	private final DialogsMainCli cli;
	
	public SaveTabOrderToContactHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.SAVE_TAB_ORDER_TO_CONTACT;
	}

	@SuppressWarnings("unchecked")
	public void saveTabOrderToContact(final Hashtable session, Document buildDoc) {
		Hashtable toSend = (Hashtable) session.clone();

		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.SAVE_TAB_ORDER_TO_CONTACT);
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.DOCUMENT, buildDoc);
		this.cli.sendFromQueue(update);
	}

}
