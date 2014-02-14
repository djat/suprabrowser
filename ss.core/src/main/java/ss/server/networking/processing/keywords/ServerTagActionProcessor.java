/**
 * 
 */
package ss.server.networking.processing.keywords;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import ss.common.GenericXMLDocument;
import ss.common.ListUtils;
import ss.common.SSProtocolConstants;
import ss.common.StringUtils;
import ss.common.VerifyAuth;
import ss.domainmodel.Statement;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.util.SessionConstants;
import ss.util.VariousUtils;

/**
 * @author roman
 *
 */
public class ServerTagActionProcessor {
	
	private static final Logger logger = SSLogger.getLogger(ServerTagActionProcessor.class);
	
	private final Hashtable sendSession;
	
	private Vector<String> spheresToPublish = new Vector<String>();

	private Vector<String> spheresToReplace = new Vector<String>();
	
	private final Document doc;
	
	private final String sphereId;
	
	private final String keywordText;

	private StringBackground stringBackGround;
	
	private final DialogsMainPeer peer;

	private BoolBackground boolBackground;

	private String personalSphereId;

	private Document existingPersonalTag;

	private ProcessingStatus status = new ProcessingStatus();

	private Document existingTag;

	private Document tagDoc;

	@SuppressWarnings("unchecked")
	public ServerTagActionProcessor(final DialogsMainPeer peer, Hashtable sendSession, final Document doc, final String sphereId, final String keywordText) {
		this.sphereId = sphereId;
		this.doc = doc;
		this.sendSession = sendSession;
		this.sendSession.put(SessionConstants.SPHERE_ID2, sphereId);
		this.keywordText = keywordText;
		this.peer = peer;
		this.stringBackGround = new StringBackground(sendSession,
				this.doc, this.doc, this.peer.getVerifyAuth());
		this.boolBackground = new BoolBackground(this.peer.getVerifyAuth(),
				this.stringBackGround, isCrossTagging(this.doc));
		this.personalSphereId = this.peer.getVerifyAuth().getPersonalSphereFromLogin((String)sendSession.get(SessionConstants.USERNAME));
	}
	
	/**
	 * @param doc2
	 * @return
	 */
	private boolean isCrossTagging(Document doc) {
		String parentSphere = doc.getRootElement().element("current_sphere").attributeValue("value");
		return !parentSphere.equals(this.sphereId);
	}

	public void doTagAction() {
		findExistingPersonalTag( );

		findExistingTag( );
		
		String unique = identifyUnique( );
		
		final Element finalElem = processSearchElem(unique);

		processViews(finalElem);
		publish();
		replace();
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void replace() {
		for (String sphereId : this.spheresToReplace) {
			Hashtable sendSession = (Hashtable) this.sendSession.clone();
			sendSession.put("sphere_id", sphereId);
			
			if (this.boolBackground.isCrossTagging()
					|| !this.boolBackground.isPersonalSphere()) {
				sendSession.put("multi_loc_sphere", this.sendSession.get("sphere_id"));
			}
			if (this.existingPersonalTag != null) {
				replaceDoc(sendSession, this.existingPersonalTag);
			}
			if (this.tagDoc != null) {
				replaceDoc(sendSession, this.tagDoc);
			}
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void publish() {
		final String personalSphere = this.peer.getVerifyAuth().getPersonalSphereFromLogin( (String)this.sendSession.get(SessionConstants.USERNAME) );
		if (StringUtils.isBlank( personalSphere )){
			logger.error("Personal sphere is null, cannot publish keyword");
			return;
		}
		
		if (this.existingPersonalTag == null) {
			Hashtable sendSession = (Hashtable) this.sendSession.clone();
			sendSession.put("sphere_id", personalSphere);
			if (this.tagDoc != null) {
				if (this.tagDoc.getRootElement().element("current_sphere") != null) {
					this.tagDoc.getRootElement().element("current_sphere")
							.detach();
				}
				publishTerse(sendSession, this.tagDoc);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void publishTerse(final Hashtable sendSession, final Document tagDoc) {
		Hashtable toSend = (Hashtable) sendSession.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL, SSProtocolConstants.PUBLISH);
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.DOCUMENT, tagDoc);
		this.peer.getHandlers().getProtocolHandler(SSProtocolConstants.PUBLISH).handle(update);
	}

	@SuppressWarnings("unchecked")
	private void processViews(final Element finalElem) {
		if (!this.boolBackground.isCrossTagging()) {
			Hashtable sendSession = (Hashtable) this.sendSession.clone();
			sendSession.put("sphere_id", this.sendSession.get("sphere_id"));
			if (!this.boolBackground.isKeyword()) {
				saveQueryView(sendSession, this.doc,
						finalElem);
			}
			addQueryToContact(sendSession, finalElem);
		} else {
			Hashtable sendSession = (Hashtable) this.sendSession.clone();
			sendSession.put("sphere_id", this.stringBackGround
					.getTaggedDocSphere());
			sendSession.put("multi_loc_sphere", this.sendSession.get("sphere_id"));

			addQueryToContact(sendSession, finalElem);

			if (!this.boolBackground.isKeyword()) {
				saveQueryView(sendSession, this.doc, finalElem);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void saveQueryView(final Hashtable session, final Document doc, final Element keywordElement) {
		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.SAVE_QUERY_VIEW);

		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.DOCUMENT, doc);
		update.put(SessionConstants.KEYWORD_ELEMENT, keywordElement);

		this.peer.getHandlers().getProtocolHandler(SSProtocolConstants.SAVE_QUERY_VIEW).handle(update);
	}

	/**
	 * @param unique
	 * @return
	 */
	private Element processSearchElem(String unique) {
		Element searchElem = this.doc.getRootElement().element("search");
		Element keywordsElem = getKeywordsElement(this.keywordText, unique);
		if (searchElem == null) {
			addKeyword(unique);
		} else {
			insertKeyword(unique, keywordsElem);
		}
		return keywordsElem;
	}
	
	@SuppressWarnings("unchecked")
	public void tagAnother(Document parentDoc, Element existingKeyword,
			Document genericDoc, String unique, boolean toAdd) {
		boolean isPersonalSphere = this.peer.getVerifyAuth().isPersonal(
				(String) this.sendSession.get("sphere_id"),
				(String) this.sendSession.get("username"),
				(String) this.sendSession.get("real_name"));
		Hashtable sendSession = (Hashtable) this.sendSession.clone();
		String uniqueId = existingKeyword.attributeValue("unique_id");
		String currentSphere = null;
		if (!isCrossTagging(parentDoc) && !isPersonalSphere) {
			if (genericDoc.getRootElement().element("current_sphere") != null) {
				currentSphere = genericDoc.getRootElement().element(
						"current_sphere").attributeValue("value");
			} else {
				currentSphere = (String) sendSession.get("sphere_id");
			}
			if (existingKeyword.attributeValue("current_location") != null) {
				currentSphere = existingKeyword
						.attributeValue("current_location");
			}
		} else {
			Document lastDoc = this.doc;
			if (lastDoc == null) {
				lastDoc = parentDoc;
			}
			if (lastDoc.getRootElement().element("current_sphere") != null) {
				currentSphere = lastDoc.getRootElement().element(
						"current_sphere").attributeValue("value");
				sendSession.put("sphere_id", currentSphere);
			} else {
				currentSphere = (String) sendSession.get("sphere_id");
			}
			if (this.existingPersonalTag != null) {
				currentSphere = this.peer.getVerifyAuth().getSystemName(
						(String) this.sendSession.get("real_name"));
				sendSession.put("sphere_id", currentSphere);
			}
			if (isPersonalSphere) {
				String personalSphereOfAuthor = this.peer.getVerifyAuth()
						.getSystemName(
								(String) this.sendSession.get("real_name"));
				sendSession.put("sphere_id", personalSphereOfAuthor);
			}
		}
		String uniqeDocSphere = currentSphere;
		Document uniqueDoc = this.peer.getXmldb().getKeywordsWithUnique(uniqeDocSphere, uniqueId);
		if (uniqueDoc == null) {
			uniqeDocSphere = parentDoc.getRootElement().element(
			"multi_loc_sphere").attributeValue("value");
			uniqueDoc = this.peer.getXmldb().getKeywordsWithUnique(uniqeDocSphere, uniqueId);
		}
		if ( uniqueDoc == null ) {
			logger.error("Could not find unique keyword doc for unique: " + uniqueId + " no either : " + uniqeDocSphere + " or in " + currentSphere);
		}
		Element add = new DefaultElement("keywords").addAttribute("unique_id",
				unique).addAttribute(
				"value",
				genericDoc.getRootElement().element("subject").attributeValue(
						"value")).addAttribute(
				"moment",
				genericDoc.getRootElement().element("moment").attributeValue(
						"value"));

		if (genericDoc.getRootElement().element("current_sphere") != null) {
			currentSphere = genericDoc.getRootElement().element("current_sphere").attributeValue("value");
		} else {
			currentSphere = this.peer.getVerifyAuth().getSystemName(
					(String) this.sendSession.get("real_name"));
		}
		
		add.addAttribute("current_location", currentSphere);
		uniqueDoc.getRootElement().add(add);
		Vector list = new Vector(uniqueDoc.getRootElement().elements(
				"multi_loc_sphere"));
		for (int i = 0; i < list.size(); i++) {
			Element one = (Element) list.get(i);
			String id = one.attributeValue("value");
			if (!id.equals(uniqeDocSphere)) {
				Hashtable sendSession2 = (Hashtable) sendSession.clone();
				sendSession2.put("sphere_id", id);
				replaceDoc(sendSession2, uniqueDoc);
			}
		}
		final Hashtable uniqueDocSession = (Hashtable)sendSession.clone();
		uniqueDocSession.put("sphere_id", uniqeDocSphere);
		replaceDoc(uniqueDocSession, uniqueDoc);
	}
	
	@SuppressWarnings("unchecked")
	private void replaceDoc(final Hashtable session, final Document uniqueDoc) {
		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL, SSProtocolConstants.REPLACE_DOC);
		
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.DOCUMENT, uniqueDoc);
		
		this.peer.getHandlers().getProtocolHandler(SSProtocolConstants.REPLACE_DOC).handle(update);
	}
	
	@SuppressWarnings("unchecked")
	private void insertKeyword(String unique, final Element finalElem) {
		Vector list = new Vector((Collection) this.doc.getRootElement().element("search").element("interest").elements());

		logger.info( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! This was the list: " + ListUtils.valuesToString( list ) );

		// Iterate through the existing keywords on the document being
		// tagged. If any of the existing keywords matches tag being
		// performed, it needs to create an
		// association between the tag being performed and the keyword
		// that
		// already exists through the method "tagAnother()". However, if
		// the
		// tag being performed also
		// exists at the personal sphere of the tagger, it needs to
		// create
		// an association between the existing keywords and the tag in
		// the
		// personal sphere. Because of
		// the "multi_loc" element, it should only be necessary to
		// create
		// the relationship in one place

		boolean taggedOnce = false;

		for (int i = 0; i < list.size(); i++) {

			Element keyword = (Element) list.get(i);

			String value = keyword.attributeValue("value");
			
			boolean found = false;

			if (this.tagDoc != null) {
				Vector docList = new Vector((Collection) this.tagDoc
						.getRootElement().elements("keywords"));
				for (int j = 0; j < docList.size(); j++) {
					Element test = (Element) docList.get(j);
					if (test.attributeValue("value").equals(value)) {
						found = true;
					}
				}

				if ( !found ) {
					Element key = (Element) keyword.clone();
					key.addAttribute("multiple", "1");
					this.tagDoc.getRootElement().add(key);
					if (!this.boolBackground.isKeyword()) {
						tagAnother(this.doc, key, this.tagDoc, unique,
								true);
						taggedOnce = true;
					}
				} else {
					String apath = "//keywords[@value=\"" + value + "\"]";
					Element addTo = (Element) this.tagDoc.selectObject(apath);
					String oldMultiple = null;
					try {
						oldMultiple = addTo.attributeValue("multiple");
					} catch (NullPointerException npe) {
						oldMultiple = "0";
					}
					if(oldMultiple==null) {
						oldMultiple = "0";
					}
					Integer one = new Integer(oldMultiple);
					addTo.addAttribute("multiple", new Integer(
							one.intValue() + 1).toString());

					if (!this.boolBackground.isKeyword()) {
						tagAnother(this.doc, addTo, this.tagDoc, unique,
								true);
						taggedOnce = true;
					}
				}

				if (this.tagDoc.getRootElement().element("unique_id") == null) {
					this.tagDoc.getRootElement().addElement("unique_id")
							.addAttribute("value", unique);
				}

				if (this.status.isNeedsToReplaceInPersonal()) {
					addMultiLocSphere(this.tagDoc, this.stringBackGround
							.getTagAuthorSphere());
				}

				Hashtable sendSession3 = (Hashtable) this.sendSession.clone();
				sendSession3.put("multi_loc_sphere", (String) this.sendSession.get("sphere_id"));
			}

			if (this.existingPersonalTag != null) {
				Vector docList = new Vector(
						(Collection) this.existingPersonalTag.getRootElement()
								.elements("keywords"));

				for (int j = 0; j < docList.size(); j++) {
					Element test = (Element) docList.get(j);
					if (test.attributeValue("value").equals(value)) {
						found = true;
					}
				}

				if ( !found ) {
					Element key = (Element) keyword.clone();
					key.addAttribute("multiple", "1");
					this.existingPersonalTag.getRootElement().add(key);
					
					if (!this.boolBackground.isKeyword()) {
						if (!taggedOnce) {
							if (logger.isDebugEnabled()) {
								logger.debug("Not taggedOnce");
							}
							tagAnother(this.doc, key,
									this.existingPersonalTag, unique, false);
						}
					} else {
						if (!taggedOnce) {
							Document keywordParent = null;
							Statement statement = Statement.wrap(this.doc);
							if(statement.getResponseId()!=null) {
								keywordParent = this.peer.getXmldb().getDocByMessageId((String)this.sendSession.get(SessionConstants.SPHERE_ID2), statement.getResponseId());
							}
							if(keywordParent!=null) {
								tagAnother(keywordParent, key, this.tagDoc, unique,
										false);
							}
						}
					}
				} else {
					String apath = "//keywords[@value=\'" + value + "\']";
					Element addTo = (Element) this.existingPersonalTag
							.selectObject(apath);
					String oldMultiple = null;

					try {
						oldMultiple = addTo.attributeValue("multiple");

					} catch (NullPointerException npe) {
						oldMultiple = "0";
					}

					Integer one = new Integer(oldMultiple);
					addTo.addAttribute("multiple", new Integer(
							one.intValue() + 1).toString());

					if (!this.boolBackground.isKeyword()) {
						if (!taggedOnce) {
							tagAnother(this.doc, addTo,
									this.existingPersonalTag, unique, false);
						}
					}
				}

				if (this.existingPersonalTag.getRootElement().element(
						"unique_id") == null) {
					this.existingPersonalTag.getRootElement().addElement(
							"unique_id").addAttribute("value", unique);
				}

				if (this.status.isNeedsToReplaceInPersonal()) {
					addMultiLocSphere(this.existingPersonalTag,
							this.stringBackGround.getTagAuthorSphere());
				}
				Hashtable sendSession3 = (Hashtable) this.sendSession.clone();
				sendSession3.put("multi_loc_sphere", (String) this.sendSession.get(SessionConstants.SPHERE_ID2));
			}
		}
		
		finalElem.addAttribute("unique_id", unique);
		if (this.existingPersonalTag == null) {
			addQueryToContact(this.sendSession, finalElem);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addQueryToContact(final Hashtable session, final Element keywordElement) {
		Hashtable toSend = (Hashtable) session.clone();
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.ADD_QUERY_TO_CONTACT);
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.KEYWORD_ELEMENT, keywordElement);
		this.peer.getHandlers().getProtocolHandler(SSProtocolConstants.ADD_QUERY_TO_CONTACT).handle(update);
	}
	
	private Element getKeywordsElement(String tagText, String unique) {
		Element keywordsElem = new DefaultElement("keywords").addAttribute(
				"value", tagText);
		if (keywordsElem.element("unique_id") == null) {
			keywordsElem.addAttribute("unique_id", unique);
		}
		
		if (this.existingPersonalTag == null) {
			keywordsElem.addAttribute("current_location",
					this.personalSphereId);
		} else {
			keywordsElem.addAttribute("current_location", this.stringBackGround
					.getTagAuthorSphere());
		}
		return keywordsElem;
	}
	
	private void addKeyword(String unique) {
		// Doesn't have any, needs to be added
		if (this.status.isReplaceInCurrent() == false) {
			if (this.status.isNeedsToReplaceInPersonal() == true) {
				addMultiLocSphere(this.tagDoc, this.stringBackGround
						.getTagAuthorSphere());
			}
			if (this.tagDoc.getRootElement().element("unique_id") == null) {
				this.tagDoc.getRootElement().addElement("unique_id")
						.addAttribute("value", unique);
			}
			try {
				String dupe = "//email/multi_loc_sphere[@value=\""
						+ this.stringBackGround.getTagAuthorSphere() + "\"]";
				Element already = (Element) this.tagDoc.selectObject(dupe);
				already.detach();
			} catch (Exception e) {
				logger.error("", e);
			}
			if (!this.stringBackGround.getTaggedDocSphere().equals(
					this.stringBackGround.getTagAuthorSphere())) {
				addMultiLocSphere(this.tagDoc, this.stringBackGround
						.getTaggedDocSphere());
			}
		} else {
			if (this.status.isNeedsToReplaceInPersonal() == true) {
				addMultiLocSphere(this.tagDoc, this.stringBackGround
						.getTagAuthorSphere());
			}
			this.spheresToReplace.add((String) this.sendSession
					.get("sphere_id"));
		}
	}

	/**
	 * @return
	 */
	private String identifyUnique() {
		String unique = new Long(VariousUtils.getNextUniqueId()).toString();
		if (isNewTag()) {
			unique = identifyUniqueOfNewTag(this.keywordText, unique);
		} else if (isCurrentTag()) {
			unique = identifyUniqueOfCurrentTag(unique);
		} else if (isPrivateTag()) {
			unique = identifyUniqueOfPrivateTag(unique);
		}
		return unique;
	}
	
	@SuppressWarnings("unchecked")
	private String identifyUniqueOfNewTag(String tagText, String unique) {
		// Both the existing query in the sphere being "browsed" are
		// null.
		// This means that it needs to send a copy to both that sphere
		// itself and the
		// personal sphere of the person who tagged

		this.tagDoc = GenericXMLDocument.createKeywordsDoc(tagText, this.sendSession, unique);

		if (this.boolBackground.isCrossTagging()) {
			this.sendSession.put("multi_loc_sphere", this.stringBackGround
					.getOriginalSphere());
			this.sendSession.put("sphere_id", this.stringBackGround
					.getTaggedDocSphere());
			addMultiLocSphere(this.tagDoc, this.stringBackGround
					.getTagAuthorSphere());
			addMultiLocSphere(this.tagDoc, this.stringBackGround
					.getTaggedDocSphere());

			addToListOfSpheres(this.spheresToPublish, this.stringBackGround
					.getTagAuthorSphere());
			addToListOfSpheres(this.spheresToPublish, this.stringBackGround
					.getTaggedDocSphere());

		} else {
			addToListOfSpheres(this.spheresToPublish, this.stringBackGround
					.getOriginalSphere());
		}
		return unique;
	}
	
	private String identifyUniqueOfPrivateTag(String unique) {
		if (!this.boolBackground.isPersonalSphere()) {
			// Genericdoc will now be the existingPersonalQuery...
			this.tagDoc = this.existingPersonalTag;
		}

		if (this.boolBackground.isCrossTagging()
				|| !this.boolBackground.isPersonalSphere()) {
			this.status.setNeedsToReplaceInPersonal(true);

			if (this.existingPersonalTag.getRootElement().element(
					"current_sphere") != null) {
				this.existingPersonalTag.getRootElement().element(
						"current_sphere").detach();
			}
			if (this.existingPersonalTag.getRootElement().element("unique_id") == null) {
				this.existingPersonalTag.getRootElement().addElement(
						"unique_id").attributeValue("value", unique);
			} else {
				unique = this.existingPersonalTag.getRootElement().element(
						"unique_id").attributeValue("value");
			}

			addMultiLocSphere(this.existingPersonalTag, this.stringBackGround
					.getTagAuthorSphere());

			if (this.boolBackground.isCrossTagging()) {
				addToListOfSpheres(this.spheresToPublish, this.stringBackGround
						.getTaggedDocSphere());
			}

			addMultiLocSphere(this.existingPersonalTag, this.stringBackGround
					.getTaggedDocSphere());

			this.spheresToReplace.add(this.stringBackGround
					.getTagAuthorSphere());

		}
		return unique;
	}
	
	private String identifyUniqueOfCurrentTag(String unique) {
		this.tagDoc = this.existingTag;
		this.status.setReplaceInCurrent(true);
		try {
			unique = this.existingTag.getRootElement().element("unique_id")
					.attributeValue("value");
		} catch (Exception e) {
			logger.error("Cannot get unique of existing", e);
		}
		if (this.boolBackground.isCrossTagging()) {
			addToListOfSpheres(this.spheresToPublish, this.stringBackGround
					.getTagAuthorSphere());
		} else {
			addToListOfSpheres(this.spheresToReplace, this.stringBackGround
					.getOriginalSphere());
		}
		return unique;
	}
	
	private void addMultiLocSphere(Document genericDoc, String multiLocSphere) {
		if (!VariousUtils.checkElementAttributeValueExists(genericDoc,
				"multi_loc_sphere", multiLocSphere)) {
			genericDoc.getRootElement().addElement("multi_loc_sphere")
					.addAttribute("value", multiLocSphere);
		}
	}

	private void addToListOfSpheres(Vector<String> spheresVector, String sphere) {
		if (!VariousUtils.vectorContains(sphere, spheresVector)) {
			spheresVector.add(sphere);
		}
	}
	
	private boolean isPrivateTag() {
		return this.existingPersonalTag != null && this.existingTag == null;
	}

	private boolean isCurrentTag() {
		return this.existingPersonalTag == null && this.existingTag != null;
	}

	private boolean isNewTag() {
		return this.existingTag == null && this.existingPersonalTag == null;
	}

	/**
	 * 
	 */
	private void findExistingTag() {
		if (this.doc == null) {
			return;
		}
		if (!this.boolBackground.isCrossTagging()) {
			this.existingTag = this.peer.getXmldb().getExistingQuery("4932287373218570630", this.keywordText);
		}
	}

	/**
	 * 
	 */
	private void findExistingPersonalTag() {
		if (this.boolBackground.isCrossTagging()
				|| !this.boolBackground.isPersonalSphere()) {
			this.existingPersonalTag = this.peer.getXmldb().getExistingQuery(this.stringBackGround
							.getTagAuthorSphere(), this.keywordText);

			if (this.existingPersonalTag != null) {
				this.status.setNeedsToReplaceInPersonal(true);
			}
		}
	}

	class ProcessingStatus {
		private boolean needsToReplaceInPersonal = false;

		private boolean replaceInCurrent = false;

		void setNeedsToReplaceInPersonal(boolean needsToReplaceInPersonal) {
			this.needsToReplaceInPersonal = needsToReplaceInPersonal;
		}
		boolean isNeedsToReplaceInPersonal() {
			return this.needsToReplaceInPersonal;
		}
		void setReplaceInCurrent(boolean replaceInCurrent) {
			this.replaceInCurrent = replaceInCurrent;
		}
		boolean isReplaceInCurrent() {
			return this.replaceInCurrent;
		}
	}

	class BoolBackground {
		private boolean personalSphere;

		private boolean keyword;

		private boolean crossTagging;

		private BoolBackground(boolean isPersonalSphere, boolean isKeyword,
				boolean crossTagging) {
			this.personalSphere = isPersonalSphere;
			this.keyword = isKeyword;
			this.crossTagging = crossTagging;
		}
		
		public BoolBackground(VerifyAuth verifyAuth,
				StringBackground tagStrings, boolean crossTagging) {
			this(
					verifyAuth.isPersonal(tagStrings.getOriginalSphere(),
							tagStrings.getUserName(), tagStrings.getRealName()),
					(tagStrings.getDocType().equals("keywords")) ? true : false,
					crossTagging);
		}
		boolean isPersonalSphere() {
			return this.personalSphere;
		}
		boolean isKeyword() {
			return this.keyword;
		}
		boolean isCrossTagging() {
			return this.crossTagging;
		}
	}

	class StringBackground {
		private String originalSphere;

		private String userName;

		private String realName;

		private String docType;

		private String tagAuthorSphere;

		private String taggedDocSphere;

		private String taggedDocAuthorSphere;

		@SuppressWarnings("unchecked")
		public StringBackground(Hashtable sendSession, Document parentDoc,
				Document lastSelectedDoc, VerifyAuth verifyAuth) {
			this.originalSphere = (String) sendSession.get("sphere_id");
			this.userName = (String) sendSession.get("username");
			this.realName = (String) sendSession.get("real_name");
			try {
				this.docType = parentDoc.getRootElement().element("type")
				.attributeValue("value");
			} catch(RuntimeException ex) {
				this.docType = "terse";
			}
			
			this.tagAuthorSphere = verifyAuth.getSystemName(this.realName);
			this.taggedDocSphere = parentDoc.getRootElement().element("current_sphere")
					.attributeValue("value");
			this.taggedDocAuthorSphere = verifyAuth
					.getPrivateForSomeoneElse(parentDoc.getRootElement()
							.element("giver").attributeValue("value"));
		}
		String getOriginalSphere() {
			return this.originalSphere;
		}
		String getUserName() {
			return this.userName;
		}
		String getRealName() {
			return this.realName;
		}
		String getDocType() {
			return this.docType;
		}
		String getTagAuthorSphere() {
			return this.tagAuthorSphere;
		}
		String getTaggedDocSphere() {
			return this.taggedDocSphere;
		}
		String getTaggedDocAuthorSphere() {
			return this.taggedDocAuthorSphere;
		}
	}
}
