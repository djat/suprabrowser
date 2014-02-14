package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

public class ReplaceDocHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ReplaceDocHandler.class);

	private final DialogsMainCli cli;
	
	public ReplaceDocHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.REPLACE_DOC;
	}

	@SuppressWarnings("unchecked")
	public void replaceDoc(final Hashtable session, final Document sendDoc) {
		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL, SSProtocolConstants.REPLACE_DOC);
		
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.DOCUMENT, sendDoc);
		this.cli.sendFromQueue(update);
	}

}
