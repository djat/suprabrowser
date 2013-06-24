/**
 * 
 */
package ss.refactor.supraspheredoc.old;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import ss.client.ui.email.SpherePossibleEmailsSet;
import ss.common.DmpFilter;
import ss.common.SSProtocolConstants;
import ss.common.StringUtils;
import ss.common.VerifyAuth;
import ss.common.domainmodel2.SsDomain;
import ss.common.email.EmailAliasesCreator;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.SphereEmail;
import ss.domainmodel.SphereEmailCollection;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.configuration.ConfigurationValue;
import ss.domainmodel.configuration.ModerateAccessMember;
import ss.domainmodel.configuration.ModerationAccessModel;
import ss.domainmodel.configuration.ModerationAccessModelList;
import ss.server.db.XMLDB;
import ss.server.db.XMLDBOld;
import ss.server.domain.service.ICreateSphere;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.FilteredHandlers;
import ss.server.networking.util.WorkflowConfigurationSetup;

/**
 * 
 */
public class CreateSphere extends AbstractSsDocFeature implements ICreateSphere {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CreateSphere.class);

	private static final String NORMAL = "normal";

	private static final String CONFIRMED = "confirmed";

	private static final String CURRENT_SPHERE = "current_sphere";

	private static final String DATA = "data";

	private static final String INHERIT = "inherit";

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

	private static final String LOGIN_NAME = "login_name";

	private static final String VALUE = "value";

	private static final String CONTACT_NAME = "contact_name";

	private static final String DISPLAY_NAME = "display_name";

	private static final String SYSTEM_NAME = "system_name";

	/**
	 * @param membersDocs
	 * @param system_name
	 * @param display_name
	 * @param sphereDoc
	 * @param username
	 * @param real_name
	 * @param sphereId
	 * @param sphereURL
	 */
	public void createSphere(Vector membersDocs, String system_name,
			String display_name, Document sphereDoc, String username,
			String real_name, String sphereId, String sphereURL, String prefEmailAlias) {
		try {

			Vector<String> members = new Vector<String>();
			Vector<String> memberLogins = new Vector<String>();
			for (int i = 0; i < membersDocs.size(); i++) {

				Document oneMember = (Document) membersDocs.get(i);
				String contactName = oneMember.getRootElement().element(
					CONTACT_NAME).attributeValue(VALUE);

				members.add(contactName);
				String loginName = oneMember.getRootElement().element(
					LOGIN_NAME).attributeValue(VALUE);
				memberLogins.add(loginName);

			}

			SphereStatement sphere = SphereStatement.wrap(sphereDoc);
			WorkflowConfigurationSetup.setupWorkflowConfigurationForSphere(sphere);
			
			if(sphere.isClubDeal()) {
				try {
					setupClubdealConfiguration(sphere);
				} catch (Exception ex) {
					logger.error("Error in setupClubdealConfiguration", ex);
				}
			}

			final Document returndoc = getUtils().registerSphereWithMembers(members, system_name, display_name);

			this.peer.getXmldb().getUtils().getLoginSphereSystemName(username);

			final Element email = sphereDoc.getRootElement();
			final String moment = DialogsMainPeer.getCurrentMoment();

			email.addElement(MOMENT).addAttribute(VALUE, moment);
			email.addElement(LAST_UPDATED).addAttribute(VALUE, moment);

			email.element(VOTING_MODEL)
				.element(TALLY)
				.addElement(MEMBER)
				.addAttribute(VALUE, real_name)
				.addAttribute(VOTE_MOMENT, moment);

			String newSphereId = sphereDoc.getRootElement().attributeValue(
				SYSTEM_NAME);

			String newSphereName = sphereDoc.getRootElement().attributeValue(
				DISPLAY_NAME);

			SphereStatement stSphere = SphereStatement.wrap(sphereDoc);
			stSphere.setCurrentSphere(sphereId);
			sphereDoc = stSphere.getBindedDocument();
			this.peer.getXmldb().insertDoc(sphereDoc, sphereId);

			processmembersDoc(membersDocs, sphereId, sphereURL, newSphereId,
				newSphereName);

			// REMOVE PROCESS RESPONCE ID?
			processResponceId(sphereDoc, sphereId, newSphereId);

			stSphere.setCurrentSphere(newSphereId);
			sphereDoc = stSphere.getBindedDocument();
			this.peer.getXmldb().insertDoc(sphereDoc, newSphereId);

			fillEmailAliasesForSphere(newSphereId, newSphereName, sphereId, prefEmailAlias);

			Document sphere_definition = this.peer.getXmldb()
				.getSphereDefinition(sphereId, newSphereId);

			Element inherit = sphere_definition.getRootElement().element(
				INHERIT);

			if (inherit != null) {

				newSphereId = sphere_definition.getRootElement().element(
					INHERIT).element(DATA).attributeValue(VALUE);
			}

			sendAuthToAll(returndoc);

			sendSphereToAllMembers(sphereDoc, memberLogins, sphereId,
				newSphereId);

		} catch (DocumentException exc) {
			logger.error("Document Exception", exc);
		}
	}

	/**
	 * @param sphere
	 */
	private void setupClubdealConfiguration(SphereStatement sphere) {
		final ConfigurationValue config = SsDomain.CONFIGURATION.getMainConfigurationValue();
		
		final ModerationAccessModelList list = config.getClubdealModerateAccesses();

		final ModerationAccessModel cdAccess = new ModerationAccessModel();

		cdAccess.setDisplayName(sphere.getDisplayName());
		cdAccess.setSystemName(sphere.getSystemName());

		final Vector<Document> contactDocs = this.peer.getXmldb().getAllContacts();
		if ( contactDocs != null ) {
			final List<SupraSphereMember> members = this.peer.getVerifyAuth().getAllMembers();
			final List<String> memberContactNames = new ArrayList<String>();
			if ( members != null ) {
				for ( SupraSphereMember member : members ) {
					memberContactNames.add( StringUtils.getNotNullString( member.getContactName() ) );
				}
			}
			for (Document doc : contactDocs) {
				if ( doc == null ) {
					continue;
				}
				final ContactStatement contact = ContactStatement.wrap(doc);
				final String contactName = contact.getContactNameByFirstAndLastNames();
				if (cdAccess.getMemberList().getMemberByContactName(contactName) != null) {
					continue;
				}
				try {
					ModerateAccessMember member = new ModerateAccessMember();
					
					if ( memberContactNames.contains( contactName ) ) {
						
					}
					boolean isModerator = ( memberContactNames.contains( contactName ) ) ? this.peer.getVerifyAuth().isAdmin(
							contactName, contact.getLogin()) : false;
					member.setModerator( isModerator );
					member.setContactName( contactName );
					member.setLoginName( contact.getLogin() );

					cdAccess.getMemberList().addMember( member );
				} catch (Exception ex) {
					logger.error( "Error in determing moderation access for contact : " + contact.getContactNameByFirstAndLastNames() ,ex );
				}
			}
		}
		list.addClubdealAccess(cdAccess);
		SsDomain.CONFIGURATION.setMainConfigurationValue(config);
	}

	/**
	 * @param newSphereId
	 * @param newSphereName
	 */
	private void fillEmailAliasesForSphere(final String newSphereId,
			final String newSphereName, final String parentSphereId, final String prefEmailAlias) {
		try {
			String domain = EmailAliasesCreator.getDefaultParentDomainName(
				parentSphereId, this.peer.getVerifyAuth().getSupraSphere());
			SphereEmail sphereEmail = new SphereEmail();
			sphereEmail.setSphereId(newSphereId);
			final String descriptionInAlias = SpherePossibleEmailsSet.convertToNamingConventionDescription(newSphereName);
			final String aliasWithSystemName = SpherePossibleEmailsSet.createAddressString(
				descriptionInAlias, newSphereId, domain);
			
			final String aliasWithDysplayName = SpherePossibleEmailsSet.createAddressString(
					descriptionInAlias,
					checkAliasAllowed(SpherePossibleEmailsSet.convertToNamingConvention(newSphereName)),
					domain);
			
			final String prefAlias;
			if(StringUtils.isNotBlank(prefEmailAlias)) {
				prefAlias = SpherePossibleEmailsSet.createAddressString(
						descriptionInAlias,
						checkAliasAllowed(SpherePossibleEmailsSet.convertToNamingConvention(prefEmailAlias)),
						domain);
			} else {
				prefAlias = aliasWithDysplayName;
			}
			SpherePossibleEmailsSet set = new SpherePossibleEmailsSet(
				prefAlias);
			set.addAddresses(aliasWithSystemName);
			if(StringUtils.isNotBlank(prefEmailAlias)) {
				set.addAddresses(aliasWithDysplayName);
			}
			sphereEmail.setEmailNames(set);
			sphereEmail.setEnabled(true);
			sphereEmail.setIsMessageIdAdd(true);
			this.peer.getXmldb().getUtils().addEmailSphereNode(sphereEmail);
			if (logger.isDebugEnabled()) {
				logger.debug("Email aliases for sphere " + newSphereName
						+ " is: " + set.getSingleStringEmails());
			}
		} catch (Throwable ex) {
			logger.error("Cannot create email aliases for sphere: "
					+ newSphereName, ex);
		}
	}

	/**
	 * @param name
	 * @return
	 */
	private String checkAliasAllowed(String aliasesname) {

		String name = new String(aliasesname);
		VerifyAuth verify = this.peer.getXmldb().getVerifyAuth();
		SphereEmailCollection sphereEmails = verify.getSpheresEmails();
		SpherePossibleEmailsSet ret = new SpherePossibleEmailsSet();

		boolean emptyEmailAliases = true;
		for (SphereEmail sphereEmail : sphereEmails) {
			SpherePossibleEmailsSet set = sphereEmail.getEmailNames();
			if (set != null) {
				String str = sphereEmail.getEmailNames()
					.getSingleStringEmails();
				if (StringUtils.isNotBlank(str)) {
					ret.addAddresses(str);
					emptyEmailAliases = false;
				}
			}
		}
		if (emptyEmailAliases) {
			return aliasesname;
		}

		boolean isReturn;
		int i = 0;
		while (true) {
			isReturn = true;
			for (String s : ret.getParsedEmailNames()) {
				if (name.equals(s)) {
					isReturn = false;
				}
			}
			if (isReturn) {
				return name;
			} else {
				i++;
				name = aliasesname.concat("_" + i);
			}
		}
	}

	private void sendSphereToAllMembers(Document sphereDoc,
			Vector memberLogins, String sphereId, String newSphereId) {

		Document supraSphereDocument = null;
		try {
			supraSphereDocument = XMLDBOld.get(new XMLDB())
				.getSupraSphereDocument();
		} catch (DocumentException e) {
			logger.error("Could not get SupraSphere Document", e);
		}

		for (int p = 0; p < memberLogins.size(); p++) {

			String member = (String) memberLogins.get(p);

			logger.info("Sending sphere to member: " + member);
			FilteredHandlers filteredHandlers = FilteredHandlers.getUserAllHandlers(member);
			for (DialogsMainPeer handler : filteredHandlers) {
				logger.info("this handler was the handle for this user: "
						+ member);

				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setStringValue(SC.PROTOCOL,
					SSProtocolConstants.UPDATE);
				dmpResponse.setStringValue(SC.SORT, "true");
				sphereDoc.getRootElement()
					.addElement(CURRENT_SPHERE)
					.addAttribute(VALUE, newSphereId);
				sphereDoc.getRootElement().addElement(CONFIRMED).addAttribute(
					VALUE, "true");
				dmpResponse.setDocumentValue(SC.DOCUMENT, sphereDoc);
				// TDOD SPHERE_ID?
				dmpResponse.setStringValue(SC.SPHERE, sphereId);
				dmpResponse.setStringValue(SC.DELIVERY_TYPE, NORMAL);
				handler.sendFromQueue(dmpResponse);

				if (supraSphereDocument != null) {
					sendUpdateSupraSphere(handler, supraSphereDocument);
				}
			}
		}
	}

	private void sendUpdateSupraSphere(DialogsMainPeer handler,
			Document supraSphereDocument) {
		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setStringValue(SC.PROTOCOL,
			SSProtocolConstants.UPDATE_VERIFY_SPHERE_DOCUMENT);
		dmpResponse.setDocumentValue(SC.SUPRA_SPHERE_DOCUMENT,
			supraSphereDocument);
		handler.getVerifyAuth().setSphereDocument(supraSphereDocument);
		handler.sendFromQueue(dmpResponse);

	}

	private void processResponceId(Document sphereDoc, String sphereId,
			String newSphereId) {
		if (sphereDoc.getRootElement().element(RESPONSE_ID) != null) {

			// logger.info("it is a response");
			// Hashtable entireThread =
			// xmldb.getEntireThread((String)session.get("sphere_id"),sphereDoc.getRootElement().element("response_id").attributeValue("value"));

			// Hashtable allDocs =
			// (Hashtable)entireThread.get("all");
			final Vector allDocs = this.peer.getXmldb().getOnlyThread(
				sphereId,
				sphereDoc.getRootElement().element(THREAD_ID).attributeValue(
					VALUE), null);

			// logger.info("alldocss: "+allDocs.size());

			// for (Enumeration enumer =
			// allDocs.keys();enumer.hasMoreElements();) {

			// String key = (String)enumer.nextElement();
			for (int j = 0; j < allDocs.size(); j++) {
				Document one = (Document) allDocs.get(j);

				// logger.info("spheredocasxml:
				// "+one.asXML());
				String sphereParentId = one.getRootElement().attributeValue(
					SYSTEM_NAME);
				// logger.info("system name:
				// "+sphereParentId);
				this.peer.getXmldb().insertDoc(sphereDoc, sphereParentId);
				this.peer.getXmldb().insertDoc(one, newSphereId);

			}
		}
	}

	@SuppressWarnings("unchecked")
	private void processmembersDoc(Vector membersDocs, String sphereId,
			String sphereURL, String newSphereId, String newSphereName)
			throws DocumentException {
		for (int j = 0; j < membersDocs.size(); j++) {

			String login = ((Document) membersDocs.get(j)).getRootElement()
				.element(LOGIN_NAME)
				.attributeValue(VALUE);

			logger.info("Login..." + login);

			String loginSphereForContact = this.peer.getXmldb()
				.getUtils()
				.getLoginSphereSystemName(login);
			if (loginSphereForContact == null) {
				logger.info("crap...");
			}

			Document contactDoc = this.peer.getXmldb().getContactDoc(
				loginSphereForContact, login);

			if (contactDoc == null) {

				contactDoc = this.peer.getXmldb()
					.getContactDoc(sphereId, login);

			}

			if (contactDoc != null) {

				if (contactDoc.getRootElement().element(LOCATIONS) == null) {

					contactDoc.getRootElement()
						.addElement(LOCATIONS)
						.addElement(SC.SPHERE)
						.addAttribute(URL, sphereURL)
						.addAttribute(EX_SYSTEM, sphereId)
						.addAttribute(EX_DISPLAY,
							this.peer.getVerifyAuth().getDisplayName(sphereId))
						.addAttribute(
							EX_MESSAGE,
							contactDoc.getRootElement()
								.element(MESSAGE_ID)
								.attributeValue(VALUE));
					contactDoc.getRootElement().element(LOCATIONS).addElement(
						SC.SPHERE).addAttribute(URL, sphereURL).addAttribute(
						EX_SYSTEM, newSphereId).addAttribute(EX_DISPLAY,
						newSphereName).addAttribute(
						EX_MESSAGE,
						contactDoc.getRootElement()
							.element(MESSAGE_ID)
							.attributeValue(VALUE));

				} else {

					Vector list = new Vector(contactDoc.getRootElement()
						.element(LOCATIONS)
						.elements());

					contactDoc.getRootElement().element(LOCATIONS).addElement(
						SC.SPHERE).addAttribute(URL, sphereURL).addAttribute(
						EX_SYSTEM, newSphereId).addAttribute(EX_DISPLAY,
						newSphereName).addAttribute(
						EX_MESSAGE,
						contactDoc.getRootElement()
							.element(MESSAGE_ID)
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
	}

	private void sendAuthToAll(Document returndoc) {
		DialogsMainPeer.updateVerifyAuthForAll(returndoc);
	}
}
