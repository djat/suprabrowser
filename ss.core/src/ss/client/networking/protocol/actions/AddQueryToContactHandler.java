package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Element;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * TODO:#member-refactoring
 * 
 * TBC:Check usage
 * 
 */
public class AddQueryToContactHandler extends AbstractOldActionBuilder {

	private final DialogsMainCli cli;

	public AddQueryToContactHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.ADD_QUERY_TO_CONTACT;
	}

	@SuppressWarnings("unchecked")
	public void addQueryToContact(final Hashtable session,
			Element keywordElement) {

		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.ADD_QUERY_TO_CONTACT);
		
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.KEYWORD_ELEMENT, keywordElement);

		this.cli.sendFromQueue(update);

	}

}
