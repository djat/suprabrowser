package ss.server.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;

import ss.common.DmpFilter;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.search.SearchEngine;
import ss.server.db.XMLDB;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.FilteredHandlers;
import ss.util.XMLSchemaTransform;

public class MatchAgainstHistoryHandler implements ProtocolHandler {
	
	private static final String MULTI_LOC_SPHERE = "multi_loc_sphere";

	private static final String CURRENT_SPHERE = "current_sphere";

	private static final String MESSAGE_ID = "message_id";

	private static final String RESPONSE_ID = "response_id";

	private static final String VALUE = "value";

	private static final String SUBJECT = "subject";

	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public MatchAgainstHistoryHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.MATCH_AGAINST_HISTORY;
	}

	public void handle(Hashtable update) {
		handleMatchAgainstHistory(update);
	}

	@SuppressWarnings("unchecked")
	public void handleMatchAgainstHistory(final Hashtable update) {
		// try {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		this.logger.info("" + session);
		Vector docsToMatch = (Vector) update.get(SC.DOCS_TO_MATCH);

		String systemName = (String) session.get(SC.SPHERE_ID);
		// this.peer.getVerifyAuth().getSystemName(realName);

		XMLDB xmldb = this.peer.getXmldb();
		String sphereCore = this.peer.getVerifyAuth()
				.getPersonalSphereFromLogin((String) session.get(SC.USERNAME));

		// Document contactDoc = xmldb.getContactDoc(sphereCore, username);

		// String apath = "//contact/keywords";

		Vector keywords = xmldb.getKeywords(systemName);
		Vector keywords2 = xmldb.getKeywords(sphereCore);
		keywords.addAll(keywords2);
		this.logger.info("keywordsize=" + keywords.size());
		Hashtable resultsTable = new Hashtable();

		for (int k = 0; k < docsToMatch.size(); k++) {

			Document matchDoc = (Document) docsToMatch.get(k);

			for (int i = 0; i < keywords.size(); i++) {
				Document one = (Document) keywords.get(i);
				this.logger.info(" Try to search");
				this.logger.info("keyword=" + one.asXML());
				this.logger.info("doc=" + matchDoc.asXML());
				this.logger.info(" ");
				SearchEngine se = new SearchEngine();
				boolean satisfiesSearch = se.searchForKeywordsInThreadId(
						matchDoc, one.getRootElement().element(SUBJECT)
								.attributeValue(VALUE), false);
				if (satisfiesSearch) {

					this.logger.info("FOUND ONE!, now send back somehow!!!");

					one.getRootElement().addElement(RESPONSE_ID).addAttribute(
							VALUE,
							matchDoc.getRootElement().element(MESSAGE_ID)
									.attributeValue(VALUE));
					if (one.getRootElement().element(CURRENT_SPHERE) == null) {
						one.getRootElement().addElement(CURRENT_SPHERE)
								.addAttribute(VALUE, systemName);
					}
					if (!one.getRootElement().element(CURRENT_SPHERE)
							.attributeValue(VALUE).equals(systemName)) {
						XMLSchemaTransform.removeAllElementsWithName(one,
								CURRENT_SPHERE);

						one.getRootElement().addElement(CURRENT_SPHERE)
								.addAttribute(VALUE, systemName);
						one.getRootElement().addElement(MULTI_LOC_SPHERE)
								.addAttribute(VALUE, sphereCore);

					}

					resultsTable.put(one.getRootElement().element(MESSAGE_ID)
							.attributeValue(VALUE), one);
				}
			}
		}
		// Now send the results back
		this.logger.info("NOW send the results back: " + resultsTable.size());
		FilteredHandlers filteredHandlers = FilteredHandlers
				.getExactHandlersFromSession(session);
		for (DialogsMainPeer handler : DmpFilter.filter(filteredHandlers,
				session)) {
			this.logger.info("verifyAuth checked out");
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setMapValue(SC.SESSION, session);
			dmpResponse.setStringValue(SC.PROTOCOL,
					SSProtocolConstants.MATCH_AGAINST_HISTORY);
			dmpResponse.setStringValue(SC.SHOW_PROGRESS, "true");
			dmpResponse.setMapValue(SC.RESULTS_TABLE, resultsTable);
			handler.sendFromQueue(dmpResponse);
		}
		// } catch (DocumentException exc) {
		// logger.error("Document Exception", exc);
		// }
	}

	// private void someLogging(Vector docsToMatch, String realName, String
	// systemName) {
	// this.logger.info(" ");
	// this.logger.info("realName=" + realName);
	// this.logger.info("systemName=" + systemName);
	// for (Object o : docsToMatch) {
	// this.logger.info("doc=" + ((Document) o).asXML());
	// }
	// this.logger.info(" ");
	// }

	// private void getAllKeyWords() {
	// XMLDB xmldb = this.peer.getXmldb();
	// Vector keywords = xmldb.getAllKeywords();
	// this.logger.info("Allkeywordsize=" + keywords.size());
	// for (int i = 0; i < keywords.size(); i++) {
	// Document one = (Document) keywords.get(i);
	// this.logger.info(" ");
	// this.logger.info("keyword=" + one.asXML());
	// this.logger.info(" ");
	// }

	// }

}
