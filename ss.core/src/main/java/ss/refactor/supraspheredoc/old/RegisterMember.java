/**
 * 
 */
package ss.refactor.supraspheredoc.old;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import ss.client.event.createevents.CreateEmailAction;
import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.DmpFilter;
import ss.common.SSProtocolConstants;
import ss.common.TimeLogWriter;
import ss.common.VerifyAuth;
import ss.common.domain.service.ISupraSphereFacade;
import ss.common.email.EmailAliasesCreator;
import ss.domainmodel.SphereEmail;
import ss.server.db.XMLDB;
import ss.server.domain.service.IRegisterMember;
import ss.server.domain.service.ISupraSphereEditFacade;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.protocol.RegisterMemberHandler;
import ss.server.networking.util.WorkflowConfigurationSetup;
import ss.smtp.reciever.EmailProcessor;
import ss.util.EmailUtils;
import ss.util.SupraXMLConstants;
import ss.util.VariousUtils;

/**
 *
 */
public class RegisterMember extends AbstractSsDocFeature implements IRegisterMember {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RegisterMember.class);

	private static final String THREAD_ID = "thread_id";

	private static final String RESPONSE_ID = "response_id";

	private static final String MESSAGE_ID = "message_id";

	private static final String EX_MESSAGE = "ex_message";

	private static final String EX_DISPLAY = "ex_display";

	private static final String EX_SYSTEM = "ex_system";

	private static final String URL = "URL";

	private static final String LOCATIONS = "locations";

	private static final String VOTE_MOMENT = "vote_moment";

	private static final String MEMBER = "member";

	private static final String TALLY = "tally";

	private static final String VOTING_MODEL = "voting_model";

	private static final String LAST_UPDATED = "last_updated";

	private static final String MOMENT = "moment";

	private static final String VALUE = "value";

	private static final String SYSTEM_NAME = "system_name";
	
	/**
	 * @param update
	 * @param session
	 * @param inviteUsername
	 * @param inviteContact
	 * @param sphereName
	 * @param sphereId
	 * @param realName
	 * @param username
	 * @param inviteSphereType
	 * @param contactDoc
	 * @param supraSphere
	 */
	public void registerMember(final Hashtable update, Hashtable session,
			String inviteUsername, String inviteContact, String sphereName,
			String sphereId, String realName, String username,
			String inviteSphereType, Document contactDoc, String supraSphere) {
		try {

			boolean isUserExists = this.peer.getVerifyAuth().isUserExist(
					username);
			if (!isUserExists) {
				final Iterable<DialogsMainPeer> handlers = DialogsMainPeerManager.INSTANCE.getHandlers();
				final TimeLogWriter timeLogWriter = new TimeLogWriter(
						RegisterMemberHandler.class, "Start registering");
				ISupraSphereEditFacade supraSphereFacade = this.peer.getXmldb().getEditableSupraSphere();
				supraSphereFacade.registerMember(supraSphere, contactDoc, inviteContact, 
					inviteUsername, sphereName, sphereId,  
					realName, username, inviteSphereType, session );
				
				createEmailSphere(update, sphereId);
				createEmailAliasesForP2PSpheres(sphereId, realName, username, handlers);
				timeLogWriter.logTime("Finish registering");
			} else {
				logger.warn("User with such username: " + username
						+ " already exists");
			}
		} catch (Exception exc) {
			logger.error("Can't register user", exc);
		}
	}

	/**
	 * 
	 */
	private void createEmailAliasesForP2PSpheres(final String loginSphere,
			final String contact_name, final String login_name, final Iterable<DialogsMainPeer> handlers) {

		try {
			final XMLDB xmldb = this.peer.getXmldb();
			xmldb.initVerifyAuth();
			final VerifyAuth auth = xmldb.getVerifyAuth();
			final ISupraSphereFacade supraStatement = auth.getSupraSphere();
			final String supraSphere = supraStatement.getSystemName();

			if (logger.isDebugEnabled()) {
				logger.debug("SupraSphere: " + supraSphere + ", loginSphere: "
						+ loginSphere + ", login_name: " + login_name
						+ ", contact_name: " + contact_name);
			}

			final Vector<String> privateMembers = xmldb
					.selectMembers(loginSphere);

			final Vector<String> pairs = xmldb.selectMembers(supraSphere);

			if (!loginSphere.equals(supraSphere)) {
				pairs.addAll(privateMembers);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("privateMembers size: " + privateMembers.size()
						+ ", pairs size: " + pairs.size());
			}

			for (String coll_name : pairs) {
				if (logger.isDebugEnabled()) {
					logger.debug("Next in pairs: " + coll_name);
				}
				if (!coll_name.equals(contact_name)
						&& !coll_name.equals(supraSphere)) {
					if (logger.isDebugEnabled()) {
						logger.debug("coll_name not equals contact_name");
					}
					String sphere_id = auth
							.getSphereSystemNameByContactAndDisplayName(
									coll_name, contact_name);
					String login = auth.getLoginForContact(coll_name);
					if (logger.isDebugEnabled()) {
						logger.debug("login for coll_name is " + login
								+ ", sphere_id is: " + sphere_id);
					}
					SphereEmail sphereEmail = new SphereEmail();
					sphereEmail.setSphereId(sphere_id);
					List<String> aliases = EmailAliasesCreator
							.createAddressStringOfPersonToPersonShpere(login,
									coll_name, login_name, contact_name,
									sphere_id, auth);
					if ((aliases == null) || (aliases.isEmpty())) {
						logger
								.error("Cannot put email aliases for P2P sphere between "
										+ coll_name + " and " + contact_name);
					} else {
						if (logger.isDebugEnabled()) {
							for (String s : aliases) {
								logger.debug("next alias: " + s);
							}
						}
						SpherePossibleEmailsSet set = new SpherePossibleEmailsSet(
								aliases);
						sphereEmail.setEmailNames(set);
						sphereEmail.setEnabled(true);
						sphereEmail.setIsMessageIdAdd(true);
						supraStatement.getSpheresEmails().put(sphereEmail);
					}
				}
			}
			Document supraSphereDocumentNew = xmldb.replaceDoc(supraStatement.getBindedDocumentForSaveToDb(), supraSphere);
			for (DialogsMainPeer handler : handlers) {
				// this.logger.info("SENDING AUTH");
				handler.getVerifyAuth().setSphereDocument(
						(Document) supraSphereDocumentNew.clone());
				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setStringValue("protocol", "updateVerify");
				dmpResponse.setVerifyAuthValue("verifyAuth", handler
						.getVerifyAuth());
				handler.sendFromQueue(dmpResponse);
			}
		} catch (Throwable ex) {
			logger.error(
					"Cannot create email aliases for P2P spheres for user: "
							+ contact_name, ex);
		}
	}

	@SuppressWarnings("unchecked")
	private void createEmailSphere(final Hashtable update,
			final String loginSphere) {
		// Hashtable session = (Hashtable) update.get(SC.SESSION);
		// Vector membersDocs = (Vector) update.get(SC.MEMBERS);

		String login = (String) update.get(SC.USERNAME);
		String real_name = (String) update.get(SC.REAL_NAME2);

		String system_name = VariousUtils.createMessageId();
		String display_name = EmailUtils.getEmailSphereOnLogin(login);

		Document sphereDoc = XMLDoc(real_name, display_name, (String) update
				.get(SC.INVITE_CONTACT), system_name);

		/*
		 * String username = (String) session.get(SC.USERNAME); String real_name =
		 * (String) session.get(SC.REAL_NAME); String sphereId = (String)
		 * session.get(SC.SPHERE_ID);
		 */
		String sphereURL = "";
		try {

			Vector members = new Vector();
			members.add(real_name);

			Vector memberLogins = new Vector();
			memberLogins.add(login);

			getUtils().registerSphereWithMembers(members,
					system_name, display_name);

			// String loginSphere =
			// this.peer.getXmldb().getUtils().getLoginSphereSystemName(login);

			final String domain = EmailAliasesCreator
					.getDefaultParentDomainName(loginSphere, this.peer
							.getVerifyAuth().getSupraSphere());

			// sendAuthToAll(returndoc);

			Element email = sphereDoc.getRootElement();
			String moment = DialogsMainPeer.getCurrentMoment();

			email.addElement(MOMENT).addAttribute(VALUE, moment);
			email.addElement(LAST_UPDATED).addAttribute(VALUE, moment);

			email.element(VOTING_MODEL).element(TALLY).addElement(MEMBER)
					.addAttribute(VALUE, real_name).addAttribute(VOTE_MOMENT,
							moment);

			/*
			 * String newSphereId = sphereDoc.getRootElement().attributeValue(
			 * SYSTEM_NAME);
			 * 
			 * String newSphereName = sphereDoc.getRootElement().attributeValue(
			 * DISPLAY_NAME);
			 */

			this.peer.getXmldb().insertDoc(sphereDoc, loginSphere);
			WorkflowConfigurationSetup
					.setupWorkflowConfigurationForSphere(sphereDoc);

			processmembersDoc(login, loginSphere, sphereURL, system_name,
					display_name, loginSphere);

			processResponceId(sphereDoc, loginSphere, system_name);

			this.peer.getXmldb().insertDoc(sphereDoc, system_name);

			SphereEmail sphereEmail = new SphereEmail();
			sphereEmail.setSphereId(system_name);
			SpherePossibleEmailsSet set = new SpherePossibleEmailsSet(
					SpherePossibleEmailsSet.createAddressString(real_name,
							login, domain));
			sphereEmail.setEmailNames(set);
			sphereEmail.setEnabled(true);
			sphereEmail.setIsMessageIdAdd(false);
			this.peer.getXmldb().getUtils().addEmailSphereNode(sphereEmail);
			// Document sphere_definition = this.peer.getXmldb()
			// .getSphereDefinition(loginSphere, system_name);

			// Element inherit = sphere_definition.getRootElement().element(
			// INHERIT);

			// sendSphereToAllMembers(sphereDoc, memberLogins, sphereId,
			// newSphereId);

		} catch (DocumentException exc) {
			logger.error("Document Exception", exc);
		}
	}

	private Document XMLDoc(String contact, String dysplayName, String giver,
			String system_id) {

		Document createDoc = DocumentHelper.createDocument();

		Element root = createDoc.addElement("sphere");

		// String default_delivery = "normal";

		// long longnum = System.currentTimeMillis();

		// String system_id = (Long.toString(getNextTableId()));

		root.addElement("voting_model").addAttribute("type", "absolute")
				.addAttribute("desc", "Absolute without qualification");
		root.element("voting_model").addElement("specific");

		// String decisive_member = (String)decs.getSelectedItem();

		/*
		 * if (!decisive_member.equals("None")) {
		 * root.element("voting_model").element("specific").addElement("member").addAttribute("contact_name",decisive_member); }
		 * else {
		 */
		root.element("voting_model").element("specific").addElement("member")
				.addAttribute("contact_name", "__NOBODY__");
		// }

		// root.addElement("status").addAttribute("value","ratified");

		root.element("voting_model").addElement("tally").addAttribute("number",
				"0.0").addAttribute("value", "0.0");

		root.addElement("thread_type").addAttribute("value", "sphere");
		root.addElement("type").addAttribute("value", "sphere");

		DefaultElement body = new DefaultElement("body");

		// ListModel m_model = mem_list.getModel();

		root.addElement("member").addAttribute("contact_name", contact);

		body.addElement("version").addAttribute("value", "3000");

		body.addElement("orig_body");

		root.add(body);

		root.addAttribute("display_name", dysplayName).addAttribute(
				"system_name", system_id).addAttribute("sphere_type", "group");

		root.addElement("subject").addAttribute("value", dysplayName);

		root.addElement("giver").addAttribute("value", giver);

		root.addElement("default_delivery").addAttribute("value", "normal");
		root.addElement("default_type").addAttribute("value",
				CreateEmailAction.EMAIL_TITLE);

		root.addElement("thread_types");

		// root.element("thread_types").addElement("terse").addAttribute("modify",
		// "own").addAttribute("enabled", "true");
		root.element("thread_types").addElement("message").addAttribute(
				"modify", "own").addAttribute("enabled", "false");
		root.element("thread_types").addElement(
				SupraXMLConstants.TYPE_VALUE_EXTERNALEMAIL).addAttribute(
				"modify", "own").addAttribute("enabled", "true");
		root.element("thread_types").addElement("bookmark").addAttribute(
				"modify", "own").addAttribute("enabled", "true");
		root.element("thread_types").addElement("terse").addAttribute("modify",
				"own").addAttribute("enabled", "true");
		root.element("thread_types").addElement("rss").addAttribute("modify",
				"own").addAttribute("enabled", "true");
		root.element("thread_types").addElement("keywords").addAttribute(
				"modify", "own").addAttribute("enabled", "true");
		root.element("thread_types").addElement("contact").addAttribute(
				"modify", "own").addAttribute("enabled", "false");
		root.element("thread_types").addElement("file").addAttribute("modify",
				"own").addAttribute("enabled", "true");
		root.element("thread_types").addElement("sphere").addAttribute(
				"modify", "own").addAttribute("enabled", "true");

		root.addElement("expiration").addAttribute("value", "All");

		return createDoc;
	}

	@SuppressWarnings("unchecked")
	private void processmembersDoc(String login, String sphereId,
			String sphereURL, String newSphereId, String newSphereName,
			String loginSphereForContact) throws DocumentException {

		// String loginSphereForContact =
		// this.peer.getXmldb().getUtils().getLoginSphereSystemName(login);

		Document contactDoc = this.peer.getXmldb().getContactDoc(
				loginSphereForContact, login);

		if (contactDoc == null) {

			contactDoc = this.peer.getXmldb().getContactDoc(sphereId, login);

		}

		if (contactDoc != null) {

			if (contactDoc.getRootElement().element(LOCATIONS) == null) {

				contactDoc.getRootElement().addElement(LOCATIONS).addElement(
						SC.SPHERE).addAttribute(URL, sphereURL).addAttribute(
						EX_SYSTEM, sphereId).addAttribute(EX_DISPLAY,
						this.peer.getVerifyAuth().getDisplayName(sphereId))
						.addAttribute(
								EX_MESSAGE,
								contactDoc.getRootElement().element(MESSAGE_ID)
										.attributeValue(VALUE));
				contactDoc.getRootElement().element(LOCATIONS).addElement(
						SC.SPHERE).addAttribute(URL, sphereURL).addAttribute(
						EX_SYSTEM, newSphereId).addAttribute(EX_DISPLAY,
						newSphereName).addAttribute(
						EX_MESSAGE,
						contactDoc.getRootElement().element(MESSAGE_ID)
								.attributeValue(VALUE));

			} else {

				Vector list = new Vector(contactDoc.getRootElement().element(
						LOCATIONS).elements());

				contactDoc.getRootElement().element(LOCATIONS).addElement(
						SC.SPHERE).addAttribute(URL, sphereURL).addAttribute(
						EX_SYSTEM, newSphereId).addAttribute(EX_DISPLAY,
						newSphereName).addAttribute(
						EX_MESSAGE,
						contactDoc.getRootElement().element(MESSAGE_ID)
								.attributeValue(VALUE));

				for (int i = 0; i < list.size(); i++) {

					Element one = (Element) list.get(i);

					String systemName = one.attributeValue(EX_SYSTEM);

					this.peer.getXmldb().replaceDoc(contactDoc, systemName);

				}

			}

			Document document = (Document) contactDoc.clone();
			Vector locations = null;
			if (document.getRootElement().element(LOCATIONS) != null) {
				locations = new Vector(document.getRootElement().element(
						LOCATIONS).elements());
			}
			for (int k = 0; k < locations.size(); k++) {

				Element loc = (Element) locations.get(k);
				String locSphereId = loc.attributeValue(EX_SYSTEM);
				// String locMessageId =
				// loc.attributeValue("ex_message");

				logger.info("Sending to this loc sphere id: " + locSphereId);

				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setDocumentValue(SC.DOCUMENT, document);
				dmpResponse.setStringValue(SC.PROTOCOL,
						SSProtocolConstants.UPDATE_DOCUMENT);
				for (DialogsMainPeer handler : DmpFilter.filter(locSphereId)) {
					handler.sendFromQueue(dmpResponse);
				}

			}

			this.peer.getXmldb().insertDoc(contactDoc, newSphereId);
		}
	}

	private void processResponceId(Document sphereDoc, String sphereId,
			String newSphereId) {
		if (sphereDoc.getRootElement().element(RESPONSE_ID) != null) {

			// this.logger.info("it is a response");
			// Hashtable entireThread =
			// xmldb.getEntireThread((String)session.get("sphere_id"),sphereDoc.getRootElement().element("response_id").attributeValue("value"));

			// Hashtable allDocs =
			// (Hashtable)entireThread.get("all");
			Vector allDocs = this.peer.getXmldb().getOnlyThread(
					sphereId,
					sphereDoc.getRootElement().element(THREAD_ID)
							.attributeValue(VALUE), null);

			// this.logger.info("alldocss: "+allDocs.size());

			// for (Enumeration enumer =
			// allDocs.keys();enumer.hasMoreElements();) {

			// String key = (String)enumer.nextElement();
			for (int j = 0; j < allDocs.size(); j++) {
				Document one = (Document) allDocs.get(j);

				// this.logger.info("spheredocasxml:
				// "+one.asXML());
				String sphereParentId = one.getRootElement().attributeValue(
						SYSTEM_NAME);
				// this.logger.info("system name:
				// "+sphereParentId);
				this.peer.getXmldb().insertDoc(sphereDoc, sphereParentId);
				this.peer.getXmldb().insertDoc(one, newSphereId);

			}
		}
	}

}
