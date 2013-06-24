/**
 * Jul 5, 2006 : 12:15:33 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.networking.DialogsMainCli;
import ss.client.networking.PostponedUpdate;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class FindAssetsInSameConceptSetHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static Logger logger = SSLogger
			.getLogger(FindAssetsInSameConceptSetHandler.class);

	private final DialogsMainCli cli;

	public FindAssetsInSameConceptSetHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handleFindAssetsInSameConceptSet(final Hashtable update) {
		this.cli.session = (Hashtable) update.get(SessionConstants.SESSION);
		String messageId = (String) update.get(SessionConstants.MESSAGE_ID);
		String queryId = (String) update.get(SessionConstants.QUERY_ID);
		Vector results = (Vector) update.get(SessionConstants.RESULTS);

		String sphereId = (String) this.cli.session
				.get(SessionConstants.SPHERE_ID2);

		for (int i = 0; i < results.size(); i++) {

			Document genericDoc = (Document) results.get(i);
			processDocument(genericDoc, messageId, sphereId, queryId);

			/*
			 * if (i == results.size() - 1) {
			 * 
			 * 
			 * 
			 * for (Enumeration enumer = sF.messagePanes .elements();
			 * enumer.hasMoreElements();) {
			 * 
			 * MessagesPane check = (MessagesPane) enumer .nextElement(); String
			 * checkId = (String) check .getSession().get("sphere_id");
			 * 
			 * String checkQuery = null; try { checkQuery = (String) check
			 * .getSession().get("query_id"); } catch (Exception e) { } boolean
			 * rightQueryId = false; if (checkQuery != null) {
			 * 
			 * if (queryId != null) { if (checkQuery.equals(queryId)) {
			 * rightQueryId = true; } } } else { rightQueryId = true; }
			 * 
			 * 
			 * if (checkId.equals(sphereId) && rightQueryId) {
			 * 
			 * logger.warn("Opening the specificThread!!!!! "); //
			 * check.openSpecificThread(genericDoc.getRootElement() //
			 * .element("message_id").attributeValue("value")); } } }
			 */

		}
	}

	@SuppressWarnings("unchecked")
	public void processDocument(Document genericDoc, String messageId,
			String sphereId, String queryId) {	
		genericDoc.getRootElement().addElement("response_id").addAttribute(
				"value", messageId);
		Element docSphere = genericDoc.getRootElement().element(
				"current_sphere");
		if (docSphere == null) {
			genericDoc.getRootElement().addElement("current_sphere")
					.addAttribute("value", sphereId);
		} else {
			if (!docSphere.attributeValue("value").equals(sphereId)) {
				List<Element> multi = (List<Element>) genericDoc
						.getRootElement().elements("multi_loc_sphere");
				boolean finded = false;
				for (Element ms : multi) {
					if (ms.attributeValue("value").equals(sphereId)) {
						finded = true;
					}
				}
				if (!finded) {
					genericDoc.getRootElement().addElement("multi_loc_sphere")
							.addAttribute("value", sphereId);
				}
			}
		}

		genericDoc.getRootElement().addElement("from_find_assets");
		
		Hashtable newUpdate = new Hashtable();
		newUpdate.put(SessionConstants.DOCUMENT, genericDoc);
		newUpdate.put(SessionConstants.SPHERE, sphereId);

		newUpdate.put(SessionConstants.IS_UPDATE, "true");
		genericDoc.getRootElement().addElement("expand");

		if (queryId != null) {
			newUpdate.put(SessionConstants.QUERY_ID, queryId);
		}
		if (!this.cli.isOnlyOpenSphere(this.cli.session)) {
			newUpdate.put(SessionConstants.QUERY_ONLY, "true");
		}

		this.cli.callInsert(new PostponedUpdate(newUpdate));
	}

	public String getProtocol() {
		return SSProtocolConstants.FIND_ASSETS_IN_SAME_CONCEPT_SET;
	}

	public void handle(Hashtable update) {
		handleFindAssetsInSameConceptSet(update);

	}

	@SuppressWarnings("unchecked")
	public void findAssetsInSameConceptSet(Hashtable session2, String uniqueId,
			String messageId, String keywordSphereId, Vector messageIdsToExclude) {
		Object queryId = session2.get(SessionConstants.QUERY_ID);

		Hashtable toSend = (Hashtable) session2.clone();

		Hashtable test = new Hashtable();

		test.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.FIND_ASSETS_IN_SAME_CONCEPT_SET);
		test.put(SessionConstants.SESSION, toSend);
		test.put(SessionConstants.UNIQUE_ID, uniqueId);
		test.put(SessionConstants.MESSAGE_ID, messageId);
		test.put(SessionConstants.SPHERE_ID, keywordSphereId);
		if (queryId != null) {
			test.put(SessionConstants.QUERY_ID, queryId);
		}
		test.put(SessionConstants.MESSAGE_IDS_TO_EXCLUDE, messageIdsToExclude);

		this.cli.sendFromQueue(test);

	}

}
