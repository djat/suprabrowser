package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * TODO:#member-refactoring
 * Check usage
 * 
 */
public class AddLocationsToDocHandler extends AbstractOldActionBuilder {

	private final DialogsMainCli cli;

	public AddLocationsToDocHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.ADD_LOCATIONS_TO_DOC;
	}

	@SuppressWarnings("unchecked")
	public void addLocationsToDoc(Hashtable session, Document document,
			String newSphereId, String newSphereName, String newMessageId) {
		Hashtable toSend = (Hashtable) session.clone();

		Hashtable test = new Hashtable();

		test.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.ADD_LOCATIONS_TO_DOC);

		test.put(SessionConstants.DOCUMENT, document);
		test.put(SessionConstants.NEW_SPHERE_ID, newSphereId);
		test.put(SessionConstants.NEW_SPHERE_NAME, newSphereName);
		// TODO sever never read NEW_MESSAGE_ID
		test.put(SessionConstants.NEW_MESSAGE_ID, newMessageId);
		test.put(SessionConstants.SESSION, toSend);

		this.cli.sendFromQueue(test);

	}

}
