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

public class MatchAgainstRecentHistoryHandler implements ProtocolHandler {

	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public MatchAgainstRecentHistoryHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.MATCH_AGAINST_RECENT_HISTORY;
	}

	public void handle(Hashtable update) {
		handleMatchAgainstRecentHistory(update);
	}

	@SuppressWarnings("unchecked")
	public void handleMatchAgainstRecentHistory(final Hashtable update) {
		this.logger.warn("Got request for match against other history");

		final Hashtable finalSession = (Hashtable) update.get(SC.SESSION);
		Vector docsToMatch = (Vector) update.get(SC.DOCS_TO_MATCH);

		String sphereID = (String) finalSession.get(SC.SPHERE_ID);
		String sUsername = (String) finalSession.get(SC.USERNAME);

		this.logger.warn("sphere id : " + sphereID);

		XMLDB xmldb = this.peer.getXmldb();
		String myHomeSphere = xmldb.getUtils()
				.getHomeSphereFromLogin(sUsername);
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("my homeSphere=" + myHomeSphere);
		}
		Vector recentContacts = xmldb.getLastFiveContacts(sphereID);

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("contact size=" + recentContacts.size());
		}
		for (int d = 0; d < recentContacts.size(); d++) {

			Document oneDoc = (Document) recentContacts.get(d);

			String username = oneDoc.getRootElement().element("login")
					.attributeValue("value");
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("contact useename=" + username);
			}

			if (username != null) {

				String oneHomeSphere = xmldb.getUtils().getHomeSphereFromLogin(
						username);
				if (this.logger.isDebugEnabled()) {
					this.logger.debug("contact homesphere=" + oneHomeSphere);
				}
				if (!oneHomeSphere.equals(myHomeSphere)) {

					// String apath = "//contact/keywords";
					Vector keywords = xmldb.getKeywords(oneHomeSphere);
					Hashtable resultsTable = new Hashtable();
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("kywords size=" + keywords.size());
					}
					for (int k = 0; k < docsToMatch.size(); k++) {

						Document matchDoc = (Document) docsToMatch.get(k);
						if (this.logger.isDebugEnabled()) {
							this.logger.debug("matchDoc=" + matchDoc.asXML());
						}
						for (int i = 0; i < keywords.size(); i++) {
							Document one = (Document) keywords.get(i);

							SearchEngine se = new SearchEngine();
							String keyword = one.getRootElement().element(
									"subject").attributeValue("value");
							if (this.logger.isDebugEnabled()) {
								this.logger.debug("keyword=" + keyword);
							}
							boolean satisfiesSearch = se
									.searchForKeywordsInThreadId(matchDoc,
											keyword, false);
							if (this.logger.isDebugEnabled()) {
								this.logger.debug("search result="
										+ satisfiesSearch);
							}
							if (satisfiesSearch) {

								this.logger
										.warn("FOUND ONE!, now send back somehow!!!");

								one
										.getRootElement()
										.addElement("response_id")
										.addAttribute(
												"value",
												matchDoc
														.getRootElement()
														.element("message_id")
														.attributeValue("value"));
								if (one.getRootElement().element(
										"current_sphere") == null) {
									// one.getRootElement().addElement("current_sphere").addAttribute("value",verifyAuth.getSystemName((String)session.get("real_name")));
									one.getRootElement().addElement(
											"current_sphere").addAttribute(
											"value", sphereID);

								} else {
									one = XMLSchemaTransform
											.removeAllElementsWithName(one,
													"current_sphere");

									one.getRootElement().addElement(
											"current_sphere").addAttribute(
											"value", sphereID);

								}

								resultsTable.put(one.getRootElement().element(
										"message_id").attributeValue("value"),
										one);

							}

						}
					}
					// Now send the results back
					FilteredHandlers filteredHandlers = FilteredHandlers
							.getExactHandlersFromSession(finalSession);
					for (DialogsMainPeer handler : DmpFilter.filter(
							filteredHandlers, finalSession)) {
						this.logger.info("verifyAuth checked out");
						final DmpResponse dmpResponse = new DmpResponse();
						dmpResponse.setMapValue(SC.SESSION, finalSession);
						dmpResponse.setStringValue(SC.PROTOCOL,
								SSProtocolConstants.MATCH_AGAINST_HISTORY);
						dmpResponse.setStringValue(SC.SHOW_PROGRESS, "true");
						dmpResponse.setMapValue(SC.RESULTS_TABLE, resultsTable);
						handler.sendFromQueue(dmpResponse);
					}
				}
			}
		}
	}

}
