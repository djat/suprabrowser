package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

public class RemoveSphereHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RemoveSphereHandler.class);

	private final DialogsMainCli cli;
	
	public RemoveSphereHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.REMOVE_SPHERE;
	}

	@SuppressWarnings("unchecked")
	public void removeSphere(final Hashtable session, Document doc) {

		Hashtable toSend = (Hashtable) session.clone();

		Hashtable update = new Hashtable();
		update
				.put(SessionConstants.PROTOCOL,
						SSProtocolConstants.REMOVE_SPHERE);

		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.DOCUMENT, doc);
		this.cli.sendFromQueue(update);

	}

}
