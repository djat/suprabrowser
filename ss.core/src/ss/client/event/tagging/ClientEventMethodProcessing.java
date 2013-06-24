/*
 * Created on Apr 19, 2005
 */
package ss.client.event.tagging;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.ui.MessagesPane;
import ss.client.ui.messagedeliver.DeliverersManager;
import ss.client.ui.tree.NodeDocumentsBundle;
import ss.common.UiUtils;
import ss.common.XmlDocumentUtils;
import ss.common.debug.DebugUtils;
import ss.domainmodel.Statement;
import ss.util.VariousUtils;

/**
 * @author david
 * 
 */
public class ClientEventMethodProcessing {
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ClientEventMethodProcessing.class);

	@SuppressWarnings("unchecked")
	public static void openTagsForItems(final Hashtable session, final MessagesPane mp) {
				Vector<String> messageIdsToExclude = UiUtils.swtEvaluate(new Callable<Vector<String>>(){
					public Vector<String> call() throws Exception {
						return mp.getMessagesTree().getAllMessagesIdForSelectedThread();
					}
				});
				List<NodeDocumentsBundle> bundles = UiUtils.swtEvaluate(new Callable<List<NodeDocumentsBundle>>(){
					public List<NodeDocumentsBundle> call() throws Exception {
						return mp.getMessagesTree().getSelectedDocumentsWithParents();
					}
				});
				
				String otherSphere = null;

				for (NodeDocumentsBundle documentBunle : bundles) {
					Document doc = documentBunle.getNodeStatement()
							.getBindedDocument();
					final Statement statement = Statement.wrap(doc);

					if (logger.isDebugEnabled()) {
						logger.debug("try show tags for " + statement.getType()
								+ " " + statement.getSubject());
					}

					UiUtils.swtBeginInvoke(new Runnable() {
						public void run() {
							mp.getMessagesTree().resetNodeStatus(
									statement.getMessageId());
						}
					});
					

					Document parentDoc = documentBunle.getParentStatement() == null ? doc
							: documentBunle.getParentStatement()
									.getBindedDocument();

					if (statement.isKeywords()) {

						String docUnique = statement.getUniqueId();
						String currentSphere = statement.getCurrentSphere();

						if (currentSphere == null) {
							currentSphere = (String) session.get("sphere_id");
						}

						otherSphere = currentSphere;

						if (doc.getRootElement().element("multi_loc_sphere") != null) {
							otherSphere = doc.getRootElement().element(
									"multi_loc_sphere").attributeValue("value");
						}

						Document uniqueDoc = mp.client.getKeywordsWithUnique(docUnique, currentSphere);

						if (uniqueDoc == null) {

							if (parentDoc.getRootElement().element("type")
									.attributeValue("value").equals("keywords")) {
								if (parentDoc.getRootElement().element(
										"multi_loc_sphere") != null) {

									Vector list = new Vector(parentDoc
											.getRootElement().elements(
													"multi_loc_sphere"));
									for (int k = 0; k < list.size(); k++) {
										Element one = (Element) list.get(k);
										String value = one
												.attributeValue("value");
										Hashtable sendSession = (Hashtable) mp
												.getRawSession().clone();
										sendSession.put("sphere_id", value);
										logger
												.warn("It probably exists here: !!!!!!"
														+ value);
										uniqueDoc = mp.client
												.getKeywordsWithUnique(docUnique, value);
										if (uniqueDoc != null) {
											break;
										}

									}
								}

							} else {
								logger
										.warn("Try this because its parent is not keywords");
								if (parentDoc.getRootElement().element(
										"current_sphere") != null) {
									String parentCurrent = parentDoc
											.getRootElement().element(
													"current_sphere")
											.attributeValue("value");
									uniqueDoc = mp.client
											.getKeywordsWithUnique(docUnique, parentCurrent);
								}

							}

						}

						if (uniqueDoc.getRootElement()
								.element("current_sphere") == null) {
							uniqueDoc.getRootElement().addElement(
									"current_sphere").addAttribute("value",
									currentSphere);
						}
						logger.warn("new unique: " + uniqueDoc.asXML());

						DeliverersManager.INSTANCE
								.insert(DeliverersManager.FACTORY
										.createReplace(uniqueDoc, doc,
												currentSphere, false));
						// mp.replaceDocWith(doc, uniqueDoc);
						doc = uniqueDoc;

					}

					try {

						Element root = doc.getRootElement();

						Element interest = null;

						interest = root.element("search").element("interest");

						Vector keywords = new Vector(interest.elements());

						logger.warn("There are this many keywords on it: "
								+ keywords.size());

						for (int i = 0; i < keywords.size(); i++) {
							Element elem = (Element) keywords.get(i);

							String subject = elem.attributeValue("value");

							Document genericDoc = null;

							if (!mp.isSupraQuery(doc)) {
								logger.error("trying to get new generic: "
										+ subject);
								Hashtable sendSession = (Hashtable) mp
										.getRawSession().clone();
								if (elem.attributeValue("current_location") != null) {
									otherSphere = elem
											.attributeValue("current_location");

								}
								logger.error("otherSphere: " + otherSphere);
								sendSession.put("sphere_id", otherSphere);
								genericDoc = mp.client.getExistingQuery(
										sendSession, subject, otherSphere);

							} else {

								Hashtable sendSession = (Hashtable) mp
										.getRawSession().clone();
								String sphereId = (String) sendSession
										.get("sphere_id");
								String parentSphere = mp.getLastSelectedDoc()
										.getRootElement().element(
												"current_sphere")
										.attributeValue("value");
								logger.error("parent sphere..." + parentSphere);
								logger.error("sphereId: " + sphereId);
								sendSession.put("sphere_id", parentSphere);

								genericDoc = mp.client.getExistingQuery(
										sendSession, subject, sphereId);

							}

							genericDoc.getRootElement().addElement(
									"response_id").addAttribute(
									"value",
									doc.getRootElement().element("message_id")
											.attributeValue("value"));

							String messageId = genericDoc.getRootElement()
									.element("message_id").attributeValue(
											"value");
							Statement genericSt = Statement.wrap(genericDoc);
							if (!VariousUtils.vectorContains(messageId,
									messageIdsToExclude)) {
								/*
								 * mp
								 * .addToAllMessages(genericSt.getMessageId(),
								 * genericSt);
								 */

								logger.warn("WILL INSERT UPDATE!: "
										+ genericDoc.asXML());

								// mp.insertUpdate(genericDoc, false, false,
								// true,
								// true);

								String currentSphere = checkMultiLocSphere(
										genericSt, (String) mp.getRawSession()
												.get("sphere_id"));
								DeliverersManager.INSTANCE
										.insert(DeliverersManager.FACTORY
												.createSimple(genericDoc, true,
														true, currentSphere));

								/*
								 * if (i == keywords.size() - 1) {
								 * 
								 * mp.openSpecificThread(genericDoc.getRootElement()
								 * .element("message_id").attributeValue(
								 * "value")); }
								 */
							}

							if (logger.isDebugEnabled()) {
								logger.debug(genericSt);
							}

							if (i == keywords.size() - 1) {
								mp.getMessagesTree().openSpecificThread(
										genericDoc.getRootElement().element(
												"message_id").attributeValue(
												"value"));
							}
						}

					} catch (Exception npe) {
						findRelatedConceptsForOne(session, mp, doc);
					}
				}
	}

	/**
	 * @param currentSphere
	 * @param genericSt
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String checkMultiLocSphere(Statement genericSt,
			String mpSphere) {
		String currentSphere = genericSt.getCurrentSphere();
		Document doc = genericSt.getBindedDocument();
		if (logger.isDebugEnabled()) {
			logger.debug("mpSphere : " + mpSphere);
			logger.debug("generic : " + genericSt);
		}

		if (currentSphere == null || !currentSphere.equals(mpSphere)) {
			List<Element> multi_loc = (List<Element>) doc.getRootElement()
					.elements("multi_loc_sphere");
			for (Element e : multi_loc) {
				String value = e.attributeValue("value");
				if (value.equals(mpSphere)) {
					return mpSphere;
				}
			}
			return currentSphere;
		} else {
			return currentSphere;
		}
	}

	@SuppressWarnings("unchecked")
	public static void findRelatedConceptsForOne(final Hashtable session,
			final MessagesPane mp, final Document doc) {
				logger.warn("Trying find related concepts for one!");

				Vector vector = UiUtils.swtEvaluate(new Callable<Vector>() {
					public Vector call() throws Exception {
						return mp.getMessagesTree().getAllUniqueIdsForKeywordsInThread();
					}
				}); 

				String type = doc.getRootElement().element("type")
						.attributeValue("value");
				logger.info("doc now: " + XmlDocumentUtils.toPrettyString(doc));

				if (type.equals("keywords")) {

					Element root = doc.getRootElement();
					String currentSphere = null;

					if (doc.getRootElement().element("current_sphere") != null) {
						currentSphere = doc.getRootElement().element(
								"current_sphere").attributeValue("value");

					} else {
						currentSphere = (String) session.get("sphere_id");
					}
					logger.info("currentSphere: " + currentSphere);

					String unique = root.element("unique_id").attributeValue(
							"value");

					Document uniqueDoc = mp.client.getKeywordsWithUnique(unique, currentSphere);

					logger.info("UNIQUE: " + uniqueDoc.asXML());

					String apath = "//email/keywords";

					Vector keywords = null;

					try {
						keywords = new Vector((List) uniqueDoc
								.selectObject(apath));
					} catch (ClassCastException cce) {

						keywords = new Vector();
						Element cast = (Element) uniqueDoc.selectObject(apath);
						keywords.add(cast);

					}

					for (int i = 0; i < keywords.size(); i++) {

						Element elem = (Element) keywords.get(i);

						String subject = elem.attributeValue("value");
						String uniqueId = elem.attributeValue("unique_id");
						logger.warn("subject trying: " + subject);
						Hashtable sendSession = (Hashtable) mp.getRawSession()
								.clone();

						if (elem.attributeValue("current_location") != null) {
							currentSphere = elem
									.attributeValue("current_location");
						}

						if (!VariousUtils.vectorContains(uniqueId,
								vector)) {
							logger.warn("getting unique from each: "
									+ uniqueId
									+ " : "
									+ currentSphere
									+ " : "
									+ (String) (mp.getRawSession()
											.get("sphere_id")));

							sendSession.put("sphere_id", currentSphere);
							final Document genericDoc = mp.client
									.getKeywordsWithUnique(uniqueId, currentSphere);

							if (genericDoc == null) {
								logger.error("Generic doc is null "
										+ DebugUtils.getCurrentStackTrace());
								continue;
							}
							if (genericDoc.getRootElement().element(
									"response_id") != null) {
								genericDoc.getRootElement().element(
										"response_id").detach();
								genericDoc.getRootElement().addElement(
										"response_id").addAttribute(
										"value",
										doc.getRootElement().element(
												"message_id").attributeValue(
												"value"));

							} else {
								genericDoc.getRootElement().addElement(
										"response_id").addAttribute(
										"value",
										doc.getRootElement().element(
												"message_id").attributeValue(
												"value"));

							}

							logger.warn("got it.....what is the response id..?"
									+ genericDoc.asXML());

							// mp.addToAllMessages(genericSt.getMessageId(), genericSt);

							// mp.insertUpdate(genericDoc, false, false, true, true);
							DeliverersManager.INSTANCE
									.insert(DeliverersManager.FACTORY
											.createSimple(genericDoc, true,
													true, (String) mp
															.getRawSession()
															.get("sphere_id")));
							if (i == keywords.size() - 1) {

								UiUtils.swtBeginInvoke(new Runnable() {
									public void run() {
										mp.getMessagesTree().openSpecificThread(
												genericDoc.getRootElement().element(
														"message_id").attributeValue(
														"value"));
									}
								});
							}
						}
					}
				}
	}
}