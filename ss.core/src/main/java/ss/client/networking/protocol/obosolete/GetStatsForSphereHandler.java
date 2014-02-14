/**
 * Jul 5, 2006 : 11:45:47 AM
 */
package ss.client.networking.protocol.obosolete;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.MessagesPane;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @deprecated
 * @author dankosedin
 * 
 */
public class GetStatsForSphereHandler implements ProtocolHandler {

	// TODO move
	private static final String SINCE_LAST_LAUNCHED = "since_last_launched";

	private static final String SINCE_MARK = "since_mark";

	private static final String REPLIES_TO_MINE = "replies_to_mine";

	private static final String TOTAL_IN_SPHERE = "total_in_sphere";

	private static final String SINCE_LOCAL_MARK = "since_local_mark";

	private DialogsMainCli cli;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GetStatsForSphereHandler.class);

	/**
	 * @deprecated
	 * @param cli
	 */
	public GetStatsForSphereHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handleGetStatsForSphere(final Hashtable update) {
		processUpdate(update);
	}

	public String getProtocol() {
		return SSProtocolConstants.GET_STATS_FOR_SPHERE;
	}

	public void handle(Hashtable update) {
		handleGetStatsForSphere(update);

	}

	private void processUpdate(final Hashtable update) {
		Hashtable updateSession = (Hashtable) update
				.get(SessionConstants.SESSION);
		final String sphereId = (String) updateSession
				.get(SessionConstants.SPHERE_ID);
		for (MessagesPane messagePane : this.cli.getSF()
				.getMessagePanesController().findMessagePanesBySphereId(
						sphereId)) {
			processMessagePane(update, messagePane);
		}
	}

	private void processMessagePane(Hashtable update, MessagesPane messagePane) {

		Document statsDoc = (Document) update.get(SessionConstants.STATS_DOC);
		String messageId = (String) update.get(SessionConstants.MESSAGE_ID);
		try {
			String newSubject = "("
					+ statsDoc.getRootElement().element(SINCE_LOCAL_MARK)
							.attributeValue(TOTAL_IN_SPHERE)
					+ ","
					+ statsDoc.getRootElement().element(SINCE_LOCAL_MARK)
							.attributeValue(REPLIES_TO_MINE)
					+ ","
					+ statsDoc.getRootElement().element(SINCE_LOCAL_MARK)
							.attributeValue(SINCE_MARK)
					+ ","
					+ statsDoc.getRootElement().element(SINCE_LOCAL_MARK)
							.attributeValue(SINCE_LAST_LAUNCHED) + ")";
			logger.info("new subject for that message id: " + newSubject);
			messagePane.updateStatsForSphere(newSubject, statsDoc, messageId);
		} catch (NullPointerException npe) {
		}
	}

}
