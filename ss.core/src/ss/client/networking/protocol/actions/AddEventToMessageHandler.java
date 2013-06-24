package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Element;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

public class AddEventToMessageHandler extends AbstractOldActionBuilder {

	private final DialogsMainCli cli;

	public AddEventToMessageHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.ADD_EVENT_TO_MESSAGE;
	}

	@SuppressWarnings("unchecked")
	public void addEventToMessage(final Hashtable session, String messageId,
			Element event) {

		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.ADD_EVENT_TO_MESSAGE);
		update.put(SessionConstants.MESSAGE_ID, messageId);
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.ELEMENT, event);
		this.cli.sendFromQueue(update);

	}

}
