package ss.server.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.search.SearchEngine;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.FilteredHandlers;
import ss.util.XMLSchemaTransform;

public class MatchAgainstOtherHistoryHandler implements ProtocolHandler {

	private static final String MULTI_LOC_SPHERE = "multi_loc_sphere";

	private static final String CURRENT_SPHERE = "current_sphere";

	private static final String MESSAGE_ID = "message_id";

	private static final String SUBJECT = "subject";

	private static final String VALUE = "value";

	private static final String RESPONSE_ID = "response_id";

	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public MatchAgainstOtherHistoryHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.MATCH_AGAINST_OTHER_HISTORY;
	}

	public void handle(Hashtable update) {
		handleMatchAgainstOtherHistory(update);

	}

	@SuppressWarnings("unchecked")
	public void handleMatchAgainstOtherHistory(final Hashtable update) {
		Hashtable session = (Hashtable) update.get(SC.SESSION);
		Vector docsToMatch = (Vector) update.get(SC.DOCS_TO_MATCH);
		
		String sphereId = (String) session.get(SC.SPHERE_ID);
		String username = (String) session.get(SC.USERNAME);		
		try {

			this.logger.info("Got request for match against other history");

			this.logger.info("sphere id : " + sphereId);

			Vector memberHomeSpheres = this.peer.getXmldb().getUtils()
					.getHomeSphereForAllMembers(sphereId);
			this.logger.warn("memberHomeSPheres: " + memberHomeSpheres.size());

			String myHomeSphere = this.peer.getXmldb().getUtils().getHomeSphereFromLogin(
					username);

			for (int d = 0; d < memberHomeSpheres.size(); d++) {

				String oneHomeSphere = (String) memberHomeSpheres.get(d);

				this.logger
						.warn("Trying one member sphere in match against other");

				if (!oneHomeSphere.equals(myHomeSphere)) {

					this.logger
							.warn("Checking all the keywords in another sphere: "
									+ oneHomeSphere);

					// String apath = "//contact/keywords";
					Vector keywords = this.peer.getXmldb().getKeywords(
							oneHomeSphere);
					Hashtable resultsTable = new Hashtable();

					for (int k = 0; k < docsToMatch.size(); k++) {

						Document matchDoc = (Document) docsToMatch.get(k);

						for (int i = 0; i < keywords.size(); i++) {
							Document one = (Document) keywords.get(i);

							SearchEngine se = new SearchEngine();
							boolean satisfiesSearch = se
									.searchForKeywordsInThreadId(matchDoc, one
											.getRootElement().element(SUBJECT)
											.attributeValue(VALUE), false);

							if (satisfiesSearch) {

								this.logger
										.warn("FOUND ONE!, now send back somehow!!!");

								one.getRootElement().addElement(RESPONSE_ID)
										.addAttribute(
												VALUE,
												matchDoc.getRootElement()
														.element(MESSAGE_ID)
														.attributeValue(VALUE));
								if (one.getRootElement()
										.element(CURRENT_SPHERE) == null) {
									// one.getRootElement().addElement("current_sphere").addAttribute("value",verifyAuth.getSystemName((String)session.get("real_name")));
									// one.getRootElement().addElement("current_sphere")
									// .addAttribute("value", oneHomeSphere);
									one.getRootElement().addElement(
											CURRENT_SPHERE).addAttribute(VALUE,
											sphereId);
									one.getRootElement().addElement(
											MULTI_LOC_SPHERE, oneHomeSphere);

								} else {
									XMLSchemaTransform
											.removeAllElementsWithName(one,
													CURRENT_SPHERE);

									one.getRootElement().addElement(
											CURRENT_SPHERE).addAttribute(VALUE,
											sphereId);
									one.getRootElement().addElement(
											MULTI_LOC_SPHERE).addAttribute(
											VALUE, oneHomeSphere);

								}

								String messageId = one.getRootElement()
										.element(MESSAGE_ID).attributeValue(
												VALUE);

								resultsTable.put(messageId, one);

							}

						}
					}
					// Now send the results back
					FilteredHandlers filteredHandlers = FilteredHandlers
							.getExactHandlersFromSession(session);
					for (DialogsMainPeer handler : filteredHandlers) {
						//TODO ???checked?  where? 
						this.logger.info("verifyAuth checked out");
						final DmpResponse dmpResponse = new DmpResponse();
						dmpResponse.setMapValue(SC.SESSION, session);
						dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.MATCH_AGAINST_HISTORY);
						dmpResponse.setStringValue(SC.SHOW_PROGRESS, "true");
						dmpResponse.setMapValue(SC.RESULTS_TABLE, resultsTable);
						handler.sendFromQueue(dmpResponse);
						break;
					}
				}
			}
		} catch (DocumentException exc) {
			this.logger.error("Document Exception", exc);
		}
	}

}
