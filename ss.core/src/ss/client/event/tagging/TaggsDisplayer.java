/**
 * 
 */
package ss.client.event.tagging;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.ui.MessagesPane;
import ss.client.ui.messagedeliver.DeliverersManager;
import ss.common.UiUtils;
import ss.common.XmlDocumentUtils;
import ss.common.debug.DebugUtils;
import ss.domainmodel.Statement;
import ss.util.VariousUtils;

/**
 * @author zobo
 * 
 */
public class TaggsDisplayer {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(TaggsDisplayer.class);

	private final MessagesPane mp;

	private KeywordsLoader loader;

	public TaggsDisplayer(final MessagesPane mp) {
		if (mp == null) {
			throw new NullPointerException("Messages pane cannot be null");
		}
		this.mp = mp;
		this.loader = new KeywordsLoader(this.mp);
	}

	private List<Element> getTagsElements(final Document doc) {
		final List<Element> elements = new ArrayList<Element>();
		final Element root = doc.getRootElement();
		if (root == null) {
			logger.error("Root element is null");
			return elements;
		}

		final Element search = root.element("search");
		if (search == null) {
			return elements;
		}

		final Element interest = search.element("interest");
		if (interest == null) {
			logger.error("Interest element is null");
			return elements;
		}

		final List storedElements = interest.elements();
		if (storedElements == null) {
			logger.error("storedElements is null");
			return elements;
		}
		elements.addAll(storedElements);
		return elements;
	}

	public void showTagsForSingleItem( final Document outerDoc ) {

		if (outerDoc == null) {
			logger.error("Document is null");
			return;
		}
		
		Document doc = outerDoc;
		final Statement st =  Statement.wrap( doc );
		String otherSphere = null;
		
		if (st.isKeywords()) {
			try {
				AtomicReference<String> otherSphereContainer = new AtomicReference<String>();
				doc = displayForKeyword(st, this.mp.getRawSession(), otherSphereContainer );
				otherSphere = otherSphereContainer.get();
			} catch (Exception ex) {
				findRelatedConceptsForOne(this.mp.getRawSession(), this.mp, doc);
				return;
			}
		}

		final List<Element> tagsElements = getTagsElements(doc);
		if ((tagsElements == null) || (tagsElements.isEmpty())) {
			return;
		}

		final String originalDocMessagesId = doc.getRootElement().element(
				"message_id").attributeValue("value");

		Document genericDoc = null;

		for (Element elem : tagsElements) {

			genericDoc = this.loader.getTagDocument(doc, elem, otherSphere);

			if (genericDoc == null) {
				logger.error("Can not obtain genericDoc");
				continue;
			}
			genericDoc = (Document) genericDoc.clone();

			genericDoc.getRootElement().addElement("response_id").addAttribute(
					"value", originalDocMessagesId);

			String messageId = genericDoc.getRootElement()
					.element("message_id").attributeValue("value");
			Statement genericSt = Statement.wrap(genericDoc);
			if (!VariousUtils.vectorContains(messageId,
					getMessageIdToExclude(originalDocMessagesId))) {

				if (logger.isDebugEnabled()) {
					logger.debug("WILL INSERT UPDATE!: " + genericDoc.asXML());
				}

				String currentSphere = ClientEventMethodProcessing
						.checkMultiLocSphere(genericSt, (String) this.mp
								.getRawSession().get("sphere_id"));

				DeliverersManager.INSTANCE.insert(DeliverersManager.FACTORY
						.createSimple(genericDoc, true, true, (String) this.mp
								.getRawSession().get("sphere_id")));
			}

			if (logger.isDebugEnabled()) {
				logger.debug(genericSt);
			}
		}
	}

	private Vector<String> getMessageIdToExclude( final String messageId ) {
		final List<Statement> statementsToExclude = UiUtils
				.swtEvaluate(new Callable<List<Statement>>() {
					public List<Statement> call() throws Exception {
						return TaggsDisplayer.this.mp.getMessagesTree()
								.getChildrenStatementsFor( messageId );
					}
				});
		final Vector<String> messageIdsToExclude = new Vector<String>();
		if ( statementsToExclude != null ) {
			for (Statement st : statementsToExclude) {
				messageIdsToExclude.add(st.getMessageId());
			}
		}
		return messageIdsToExclude;
	}

	/**
	 * @param docs
	 */
	public void showTagsForList(List<Document> docs) {
		if (docs == null) {
			docs = getAllCurrentDocs();
		}
		for (Document doc : docs) {
			showTagsForSingleItem( doc );
		}
	}

	/**
	 * @return
	 */
	private List<Document> getAllCurrentDocs() {
		final List<Document> docs = new ArrayList<Document>();
		for (Statement st : this.mp.getAllMessages().values()) {
			if (!st.isKeywords()) {
				docs.add(st.getBindedDocument());
			}
		}
		return docs;
	}

	private Document displayForKeyword(final Statement statement,
			final Hashtable session, final AtomicReference<String> otherSphereContainer) {
		if (statement == null) {
			logger.error("Statement is null");
			return null;
		}
		String otherSphere = null;
		final Document doc = statement.getBindedDocument();
		final Document parentDoc = this.mp.getMessagesTree().getParentDocForKeyword(doc);

		String docUnique = statement.getUniqueId();
		String currentSphere = statement.getCurrentSphere();

		if (currentSphere == null) {
			currentSphere = (String) session.get("sphere_id");
		}

		otherSphere = currentSphere;

		if (doc.getRootElement().element("multi_loc_sphere") != null) {
			otherSphere = doc.getRootElement().element("multi_loc_sphere")
					.attributeValue("value");
		}

		Document uniqueDoc = this.mp.client.getKeywordsWithUnique(docUnique, currentSphere);

		if (uniqueDoc == null) {

			if (parentDoc.getRootElement().element("type").attributeValue(
					"value").equals("keywords")) {
				if (parentDoc.getRootElement().element("multi_loc_sphere") != null) {

					Vector list = new Vector(parentDoc.getRootElement()
							.elements("multi_loc_sphere"));
					for (int k = 0; k < list.size(); k++) {
						Element one = (Element) list.get(k);
						String value = one.attributeValue("value");
						Hashtable sendSession = (Hashtable) this.mp
								.getRawSession().clone();
						sendSession.put("sphere_id", value);
						logger.warn("It probably exists here: !!!!!!" + value);
						uniqueDoc = this.mp.client.getKeywordsWithUnique(docUnique, value);
						if (uniqueDoc != null) {
							break;
						}

					}
				}

			} else {
				logger.warn("Try this because its parent is not keywords");
				if (parentDoc.getRootElement().element("current_sphere") != null) {
					String parentCurrent = parentDoc.getRootElement().element(
							"current_sphere").attributeValue("value");
					uniqueDoc = this.mp.client.getKeywordsWithUnique(docUnique, parentCurrent);
				}

			}

		}

		if (uniqueDoc.getRootElement().element("current_sphere") == null) {
			uniqueDoc.getRootElement().addElement("current_sphere")
					.addAttribute("value", currentSphere);
		}
		logger.warn("new unique: " + uniqueDoc.asXML());

		DeliverersManager.INSTANCE.insert(DeliverersManager.FACTORY
				.createReplace(uniqueDoc, doc, currentSphere, false));
		// mp.replaceDocWith(doc, uniqueDoc);
		otherSphereContainer.set( otherSphere );
		return uniqueDoc;
	}
	
	private void findRelatedConceptsForOne(final Hashtable session,
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
