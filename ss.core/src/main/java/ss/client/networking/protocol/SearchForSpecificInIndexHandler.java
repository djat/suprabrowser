/**
 * Jul 5, 2006 : 12:54:49 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.messagedeliver.DeliverersManager;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class SearchForSpecificInIndexHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SearchForSpecificInIndexHandler.class);
	
	private final DialogsMainCli cli;

	public SearchForSpecificInIndexHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handleSearchForSpecificInIndex(final Hashtable update) {
		this.cli.session = (Hashtable) update.get(SessionConstants.SESSION);
		Vector resultsTable = (Vector) update
				.get(SessionConstants.RESULTS_TABLE);

		final String sphereId = (String) this.cli.session
				.get(SessionConstants.SPHERE_ID);
//		String uniqueId = (String) this.cli.session
//				.get(SessionConstants.UNIQUE_ID);
//		MessagesPane mp = this.cli.getSF().getMessagesPaneFromSphereId(
//				sphereId, uniqueId);

		for (int i = 0; i < resultsTable.size(); i++) {

			Document genericDoc = (Document) resultsTable.get(i);

			// genericDoc.getRootElement().addElement("response_id").addAttribute("value",messageId);

			Hashtable<String, Object> newUpdate = new Hashtable<String, Object>();
			newUpdate.put(SessionConstants.DOCUMENT, genericDoc);
			newUpdate.put(SessionConstants.SPHERE, sphereId);

			newUpdate.put(SessionConstants.IS_UPDATE, "true");

			//Statement st = Statement.wrap(genericDoc);
			//mp.addToAllMessages(st.getMessageId(), st);

			//mp.insertUpdate(genericDoc, true, true, false);
			
			DeliverersManager.INSTANCE.insert(
	    			DeliverersManager.FACTORY.createSimple(genericDoc, false, false, sphereId));

			// callInsert(newUpdate);

			/*
			 * if (i==resultsTable.size()-1) {
			 * 
			 * for (Enumeration enumereration =
			 * sF.messagePanes.elements();enumereration.hasMoreElements();) {
			 * 
			 * MessagesPane check = (MessagesPane)enumereration.nextElement();
			 * String checkId = (String)check.getSession().get("sphere_id"); if
			 * (checkId.equals(sphereId)) {
			 * check.openSpecificThread(genericDoc.getRootElement().element("message_id").attributeValue("value")); } } }
			 */

		}
	}

	public String getProtocol() {
		return SSProtocolConstants.SEARCH_FOR_SPECIFIC_IN_INDEX;
	}

	public void handle(Hashtable update) {
		handleSearchForSpecificInIndex(update);
	}

	@SuppressWarnings("unchecked")
	public void searchForSpecificInIndex(Hashtable session2,
			Document new_definition, Vector docsToMatch) {

		Hashtable toSend = (Hashtable) session2.clone();

		Hashtable test = new Hashtable();

		test.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.SEARCH_FOR_SPECIFIC_IN_INDEX);

		test.put(SessionConstants.SPHERE_DEFINITION2, new_definition);// RC

		test.put(SessionConstants.DOCS_TO_MATCH, docsToMatch);

		test.put(SessionConstants.SESSION, toSend);

		this.cli.sendFromQueue(test);

	}

}
