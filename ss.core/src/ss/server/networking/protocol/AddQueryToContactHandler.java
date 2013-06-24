package ss.server.networking.protocol;

import java.util.Hashtable;

import org.dom4j.Element;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class AddQueryToContactHandler implements ProtocolHandler {

	private static final String GIVER = "giver";

	private static final String MOMENT = "moment";

	private DialogsMainPeer peer;

	public AddQueryToContactHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.ADD_QUERY_TO_CONTACT;
	}

	public void handle(Hashtable update) {
		handleAddQueryToContact(update);
	}

	public void handleAddQueryToContact(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		Element keywordElement = (Element) update.get(SC.KEYWORD_ELEMENT);

		String userName = (String) session.get(SC.USERNAME);
		String realName = (String) session.get(SC.REAL_NAME);

		String moment = DialogsMainPeer.getCurrentMoment();

		keywordElement.addAttribute(MOMENT, moment);
		keywordElement.addAttribute(GIVER, realName);

		String sphereCore = this.peer.getXmldb().getUtils().getSphereCore(
				session);

		this.peer.getXmldb().addQueryToContact(sphereCore, userName,
				keywordElement);
	}

}
