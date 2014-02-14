/**
 * Jul 5, 2006 : 12:34:09 PM
 */
package ss.client.networking.protocol;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.networking.PostponedUpdate;
import ss.client.ui.MessagesPane;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.domainmodel.Statement;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class MatchAgainstHistoryHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(MatchAgainstHistoryHandler.class);
	
	// TODO move
	private static final String EXPAND = "expand";

	private static final String VALUE = "value";

	private static final String MESSAGE_ID = "message_id";

	private final DialogsMainCli cli;

	public MatchAgainstHistoryHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	@SuppressWarnings("unchecked")
	public void handleMatchAgainstHistory(final Hashtable update) {
		logger.info("Recieve back      ");
		this.cli.session = (Hashtable) update.get(SessionConstants.SESSION);
		Hashtable resultsTable = (Hashtable) update
				.get(SessionConstants.RESULTS_TABLE);
		String sphereId = (String) this.cli.session
				.get(SessionConstants.SPHERE_ID2);
		String uniqueId = (String) this.cli.session
				.get(SessionConstants.UNIQUE_ID);
		logger.info("sphereId=" + sphereId + " uniqueId=" + uniqueId);

		logger.info("GOT RESULTS " + resultsTable.size());

		for (Enumeration enumer = resultsTable.keys(); enumer.hasMoreElements();) {
			String messageId = (String) enumer.nextElement();
			Document genericDoc = (Document) resultsTable.get(messageId);

			// genericDoc.getRootElement().addElement("response_id").addAttribute("value",messageId);
			logger.info("HERE IS THE RESULT: " + genericDoc.asXML());

			Hashtable newUpdate = new Hashtable();
			newUpdate.put(SessionConstants.DOCUMENT, genericDoc);
			newUpdate.put(SessionConstants.SPHERE, sphereId);

			if (!this.cli.isOnlyOpenSphere(this.cli.session)) {
				logger.info("Its the only open one");
				newUpdate.put(SessionConstants.QUERY_ONLY, "true");

			}

			String queryId = (String) this.cli.session
					.get(SessionConstants.QUERY_ID);
			if (queryId != null) {
				newUpdate.put(SessionConstants.QUERY_ID, queryId);
			}
			newUpdate.put(SessionConstants.IS_UPDATE, "true");

			MessagesPane mp = this.cli.getSF().getMessagesPaneFromSphereId(
					sphereId, uniqueId);

			Statement st = Statement.wrap(genericDoc);
			mp.addToAllMessages(st.getMessageId(), st);

			genericDoc.getRootElement().addElement(EXPAND);
			this.cli.callInsert( new PostponedUpdate( newUpdate ) );

			if (!enumer.hasMoreElements()) {
				for (MessagesPane messagesPane : this.cli.getSF()
						.getMessagePanesController().findMessagePanesBySphereId(sphereId)) {
					messagesPane.getMessagesTree().openSpecificThread(genericDoc.getRootElement()
								.element(MESSAGE_ID).attributeValue(VALUE));
				}
			}

		}
	}

	public String getProtocol() {
		return SSProtocolConstants.MATCH_AGAINST_HISTORY;
	}

	public void handle(Hashtable update) {
		handleMatchAgainstHistory(update);
	}

	@SuppressWarnings("unchecked")
	public void matchAgainstHistory(Hashtable session2, Vector docsToMatch) {
		logger.info("      ");
		logger.info("calling match against in cli: " + docsToMatch.size());

		Hashtable toSend = (Hashtable) session2.clone();

		Hashtable test = new Hashtable();

		test.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.MATCH_AGAINST_HISTORY);

		test.put(SessionConstants.SESSION, toSend);
		test.put(SessionConstants.DOCS_TO_MATCH, docsToMatch);

		this.cli.sendFromQueue(test);

	}

}
