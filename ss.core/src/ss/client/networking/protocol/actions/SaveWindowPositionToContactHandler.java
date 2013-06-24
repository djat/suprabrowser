package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * TODO:#member-refactoring
 * 
 * Save window position info to all contact documents in login sphere. 
 * @see XMLDB.saveWindowPositionToContact
 * 
 */
public class SaveWindowPositionToContactHandler extends AbstractOldActionBuilder {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SaveWindowPositionToContactHandler.class);

	private final DialogsMainCli cli;
	
	public SaveWindowPositionToContactHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.SAVE_WINDOW_POSITION_TO_CONTACT;
	}

	@SuppressWarnings("unchecked")
	public void saveWindowPositionToContact(final Hashtable session,
			Document buildDoc) {
		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.SAVE_WINDOW_POSITION_TO_CONTACT);
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.DOCUMENT, buildDoc);
		this.cli.sendFromQueue(update);
	}

}
