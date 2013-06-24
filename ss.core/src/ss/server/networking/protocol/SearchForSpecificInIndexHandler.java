package ss.server.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import ss.common.DmpFilter;
import ss.common.GenericXMLDocument;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.search.SearchEngine;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.FilteredHandlers;

public class SearchForSpecificInIndexHandler implements ProtocolHandler {

	private static final String LAST_UPDATED = "last_updated";

	private static final String MOMENT = "moment";

	private static final String RESPONSE_ID = "response_id";

	private static final String CURRENT_SPHERE = "current_sphere";

	private static final String CONFIRMED = "confirmed";

	private static final String STATUS = "status";

	private static final String MESSAGE_ID = "message_id";

	private static final String THREAD_TYPE = "thread_type";

	private static final String TYPE = "type";

	private static final String UNIQUE_ID = "unique_id";

	private static final String CONTACT_NAME = "contact_name";

	private static final String VALUE = "value";

	private static final String KEYWORDS = "keywords";

	private static final String SEARCH = "search";

	@SuppressWarnings("unused")
	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public SearchForSpecificInIndexHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.SEARCH_FOR_SPECIFIC_IN_INDEX;
	}

	public void handle(Hashtable update) {
		handleSearchForSpecificInIndex(update);
	}

	@SuppressWarnings("unchecked")
	public void handleSearchForSpecificInIndex(final Hashtable update) {
		this.logger.info("Start match against history");

		Hashtable session = (Hashtable) update.get(SC.SESSION);
		Document sphereDefinition = (Document) update
				.get(SC.SPHERE_DEFINITION2);//RC
		Vector docsToMatch = (Vector) update.get(SC.DOCS_TO_MATCH);
		
		String sphereId = (String) session.get(SC.SPHERE_ID);

		Element keywordQuery = sphereDefinition.getRootElement()
				.element(SEARCH).element(KEYWORDS);
		this.logger.info("KEYWORDS : " + keywordQuery.asXML());

		Vector recentQueries = new Vector();
		recentQueries.add(keywordQuery);

		Vector resultsTable = new Vector();

		for (int i = 0; i < recentQueries.size(); i++) {
			Element one = (Element) recentQueries.get(i);
			this.logger.info("ONE: " + one.asXML());

			this.logger.info("NUMBER OF FREAKING DOCS TO MATCH: "
					+ docsToMatch.size());
			for (int k = 0; k < docsToMatch.size(); k++) {

				Document matchDoc = (Document) docsToMatch.get(k);
				this.logger.info("CHecking this one: " + matchDoc.asXML());

				SearchEngine se = new SearchEngine();
				boolean satisfiesSearch = se.searchForKeywordsInThreadId(
						matchDoc, one.attributeValue(VALUE), false);

				if (satisfiesSearch) {

					Element elem = one;
					String subject = elem.attributeValue(VALUE);
					String contact = elem.attributeValue(CONTACT_NAME);
					String moment = DialogsMainPeer.getCurrentMoment();

					String unique = new Long(GenericXMLDocument.getNextTableId())
							.toString();

					Document genericDoc = GenericXMLDocument.XMLDoc(subject, "", contact);
					genericDoc.getRootElement().addElement(UNIQUE_ID)
							.addAttribute(VALUE, unique);
					genericDoc.getRootElement().addElement(TYPE).addAttribute(
							VALUE, KEYWORDS);
					genericDoc.getRootElement().addElement(THREAD_TYPE)
							.addAttribute(VALUE, KEYWORDS);
					genericDoc.getRootElement().addElement(MESSAGE_ID)
							.addAttribute(
									VALUE,
									new Long(GenericXMLDocument.getNextTableId())
											.toString());

					genericDoc.getRootElement().addElement(STATUS)
							.addAttribute(VALUE, CONFIRMED);
					genericDoc.getRootElement().addElement(CONFIRMED)
							.addAttribute(VALUE, "true");
					genericDoc.getRootElement().addElement(CURRENT_SPHERE)
							.addAttribute(VALUE, sphereId);
					genericDoc.getRootElement().addElement(RESPONSE_ID)
							.addAttribute(
									VALUE,
									matchDoc.getRootElement().element(
											MESSAGE_ID).attributeValue(VALUE));
					genericDoc.getRootElement().addElement(MOMENT)
							.addAttribute(VALUE, moment);
					genericDoc.getRootElement().addElement(LAST_UPDATED)
							.addAttribute(VALUE, moment);

					resultsTable.add(genericDoc);

					this.logger.info("FOUND ONE: ....will add it to this: "
							+ matchDoc.getRootElement().element(MESSAGE_ID)
									.attributeValue(VALUE));
					this.logger.info("AND THE NEW KEYWORD: "
							+ genericDoc.asXML());
				}
			}
		}

		// Now send the results back
		this.logger.info("NOW send the results back: " + resultsTable.size());
		FilteredHandlers filteredHandlers = FilteredHandlers
				.getExactHandlersFromSession(session);
		for (DialogsMainPeer handler : DmpFilter.filter(filteredHandlers, session)) {
			this.logger.info("verifyAuth checked out");
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setMapValue(SC.SESSION, session);
			dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.SEARCH_FOR_SPECIFIC_IN_INDEX);
			dmpResponse.setStringValue(SC.SHOW_PROGRESS, "true");
			dmpResponse.setVectorValue(SC.RESULTS_TABLE, resultsTable);
			handler.sendFromQueue(dmpResponse);
		}
	}

}
