/**
 * Jul 5, 2006 : 12:02:47 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.VerbosedSession;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class GetInfoForHandler implements ProtocolHandler {

	// TODO move
	private static final String VALUE = "value";

	// TODO move
	public static final String PHYSICAL_LOCATION = "physical_location";

	private final DialogsMainCli cli;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetInfoForHandler.class);

	public GetInfoForHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handleGetInfoFor(final Hashtable update) {
		logger
				.info("got request for get info for in this client...probably for me");

		Document doc = (Document) update.get(SessionConstants.DOC);

		Hashtable updateSession = (Hashtable) update
				.get(SessionConstants.SESSION);

		logger
				.info("The request to start should have the same session information twice: "
						+ (String) updateSession.get(SessionConstants.SESSION));

		Element physicalLocation = doc.getRootElement().element(
				PHYSICAL_LOCATION);

		String locationName = null;

		if (physicalLocation != null) {
			locationName = physicalLocation.attributeValue(VALUE);
		}

		logger.info("Got request to start!: " + doc.asXML());

		final VerbosedSession mainVerbosedSession = this.cli.getSF().getMainVerbosedSession();
		final String currentProfileId = mainVerbosedSession.getProfileId();

		logger.info("currentProfileId: " + currentProfileId);

		if (locationName.equals(currentProfileId)) {

			final Hashtable sendSession = mainVerbosedSession.rawClone();

			// TODO Sounds like a bag. Where should sendSession go?

		}
	}

	public String getProtocol() {
		return SSProtocolConstants.GET_INFO_FOR;
	}

	public void handle(Hashtable update) {
		handleGetInfoFor(update);
	}

	@SuppressWarnings("unchecked")
	public void getInfoFor(Hashtable session2, Document doc) {
		Hashtable toSend = (Hashtable) session2.clone();
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL, SSProtocolConstants.GET_INFO_FOR);
		update.put(SessionConstants.DOC, doc);
		update.put(SessionConstants.SESSION, toSend);
		this.cli.sendFromQueue(update);
	}

}
