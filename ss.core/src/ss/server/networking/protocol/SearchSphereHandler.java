package ss.server.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.common.SphereDefinitionCreator;
import ss.common.VerifyAuth;
import ss.server.db.XMLDB;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.util.SessionConstants;
import ss.util.VariousUtils;

public class SearchSphereHandler implements ProtocolHandler {

	private static final String EMAIL_FORWARDING = "email_forwarding";

	private DialogsMainPeer peer;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SearchSphereHandler.class);

	public SearchSphereHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.SEARCH_SPHERE;
	}

	public void handle(Hashtable update) {
		handleSearchSphere(update);
	}

	public void handleSearchSphere(final Hashtable update) {
		if (update.get(SC.SUPRA_SPHERE_SEARCH) != null) {
			hadleSearchSphereFromSupraSearch(update);
		} else {
			logger.info("----- search sphere handler");
			final Hashtable session = (Hashtable) update.get(SC.SESSION);
			final String openBackground = (String) update.get(SC.OPEN_BACKGROUND);
			logger.warn("open back : "+openBackground);
			Document sphereDefinition = (Document) update
					.get(SC.SPHERE_DEFINITION);
			
			String sphere_id = (String) session.get(SC.SPHERE_ID);
			String localSphereId = (String) session.get(SC.LOCAL_SPHERE_ID);
			String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
			
			// sphere_definition - defines search criteria and sphere itself

			logger.info("SPHERE ID IN GET ANOTHER: " + sphere_id);
			logger
					.warn("localsphere id had better be freaking the right one"
							+ localSphereId);

			sphereDefinition = setUpSphereDefinition(sphereDefinition,
					sphere_id, supraSphere);


			// // system_name = sphere_id = table name
			// String systemSphereId =
			// sphereDefinition.getRootElement().attributeValue("system_name");

			// String currentMoment = DialogsMainPeer.getCurrentMoment();

			// // from users real name (ie. Joe Smith) get
			// // their sphereId aka table name
			// String realName = (String) aSession.get(SC.REAL_NAME);
			// String personalSphere =
			// this.peer.getVerifyAuth().getSystemName(realName);
			// // processSphereStats(session, sphereDefinition, sphereId,
			// // currentMoment, personalSphere);

//			if(isSphereForMemberEnabled(sphereDefinition, session)) {
				this.peer.sendDefinitionMessages(session, sphereDefinition,
						this.peer.getVerifyAuth(), openBackground);
//			}
		}
	}

	/**
	 * @param sphereDefinition
	 * @param sphere_id
	 * @param supraSphere
	 * @return
	 */
	private Document setUpSphereDefinition(Document sphereDefinition,
			String sphere_id, String supraSphere) {
		if (sphereDefinition == null) {
			sphereDefinition = this.peer.getXmldb().getSphereDefinition(
					sphere_id, sphere_id);

			if (sphereDefinition == null) {
				sphereDefinition = this.peer.getXmldb().getSphereDefinition(
						supraSphere, sphere_id);
			}

		} else {

			Document existingDefinition = this.peer.getXmldb()
					.getSphereDefinition(sphere_id, sphere_id);

			if (existingDefinition != null) {
				Element forward = existingDefinition.getRootElement().element(
						EMAIL_FORWARDING);
				if (forward != null) {
					forward.detach();

					sphereDefinition.getRootElement().add(forward);
				}

			}
		}

		if (sphereDefinition == null) {

			SphereDefinitionCreator sdc = new SphereDefinitionCreator();
			String displayName = this.peer.getVerifyAuth().getDisplayName(sphere_id);
			sphereDefinition = sdc.createDefinition(displayName, sphere_id);

		}
		return sphereDefinition;
	}

	@SuppressWarnings("unchecked")
	private void hadleSearchSphereFromSupraSearch(Hashtable update) {
		final String openBackground = (String) update.get(SC.OPEN_BACKGROUND);
		String sphere_id = (String) update.get(SC.SPHERE_ID);
		String message_id = (String) update.get(SC.MESSAGE_ID);
		String keywords  = (String)update.get(SC.KEYWORD_ELEMENT);

		VerifyAuth verifyAuth = this.peer.getVerifyAuth();
		Hashtable session = verifyAuth.getSession();
		
		final String memberLogin = (String) session.get(SessionConstants.USERNAME);
		if (!verifyAuth.isSphereEnabledForMember(sphere_id, memberLogin)) {
			logger.error("Sphere with ID: " + sphere_id + " is not allowed to open for user : " + memberLogin);
			return;
		}
		session.put(SC.SPHERE_ID, sphere_id);

		XMLDB xmldb = this.peer.getXmldb();
		Document[] docsInOrder = xmldb.getSupraSearchView(sphere_id, message_id);
		
		String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
		Document sphereDefinition = setUpSphereDefinition(null, sphere_id,
				supraSphere);
		
		sphereDefinition.getRootElement().addElement("query").addAttribute(
			"value", "true").addAttribute("query_id",
			VariousUtils.getNextRandomLong());
		
		try {

			sphereDefinition.getRootElement().addElement("search").addElement(
					"keywords").addAttribute("value", keywords);

		} catch (NullPointerException npe) {
		}



		final DmpResponse dmpResponse = new DmpResponse();

		Vector contactsOnly = xmldb.getContactsFromEmail(sphere_id, null);
		dmpResponse.setVectorValue(SC.CONTACTS_ONLY, contactsOnly);
		String sphereType = verifyAuth.getSphereType(sphere_id);
		Vector presenceInfo = (sphereType.equals("group") ? setUpPresenceInfoForGroups(session)
				: setUpPresenceInfoForNonGroups(session, contactsOnly));
		dmpResponse.setVectorValue(SC.PRESENCE_INFO, presenceInfo);

		dmpResponse.setVerifyAuthValue(SC.VERIFY_AUTH, verifyAuth);
		dmpResponse.setMapValue(SC.SESSION, session);
		dmpResponse.setDocumentValue(SC.SPHERE_DEFINITION, sphereDefinition);
		Element create_spheres = sphereDefinition.getRootElement().element(
				"create_spheres");

		if (create_spheres != null) {

			String create_id = create_spheres.element("sphere").attributeValue(
					"system_name");
			Document create = xmldb.getSphereDefinition(sphere_id, create_id);
			
			dmpResponse.setDocumentValue(SC.CREATE_DEFINITION, create);

		}

		Hashtable noReallyAll = new Hashtable();
		Vector allOrder = new Vector();
		for (Document message : docsInOrder) {
			String messageId = message.getRootElement().element("message_id").attributeValue("value");
			noReallyAll.put(messageId, message);
			allOrder.add(messageId);
		}
		noReallyAll.put("docs_in_order", docsInOrder);
		dmpResponse.setMapValue(SC.ALL, noReallyAll);
		//dmpResponse.setVectorValue(SC.ORDER, allOrder);
		dmpResponse.setStringValue(SC.SPHERE, sphere_id);
		dmpResponse.setStringValue(SC.HIGHLIGTH, message_id);
		dmpResponse.setStringValue(SC.PROTOCOL,
				SSProtocolConstants.RECEIVE_RESULTS_FROM_XMLSEARCH);
		dmpResponse.setStringValue(SC.SHOW_PROGRESS, openBackground
				.equals("false") ? "true" : "false");
		this.peer.sendFromQueue(dmpResponse);
	}

	private Vector setUpPresenceInfoForGroups(Hashtable session) {
		String sphere_id = (String) session.get(SC.SPHERE_ID);
		Vector presenceInfo = this.peer.getXmldb().getSubPresence(sphere_id,
				sphere_id);
		presenceInfo = this.peer.getOnlineForVectorOfStrings(presenceInfo,
				session);
		return presenceInfo;
	}

	@SuppressWarnings("unchecked")
	private Vector setUpPresenceInfoForNonGroups(Hashtable session,
			Vector contactsOnly) {
		String sphere_id = (String) session.get(SC.SPHERE_ID);
		String realName = (String) session.get(SC.REAL_NAME);
		String display = this.peer.getVerifyAuth().getDisplayName(sphere_id);

		Vector available = new Vector();
		if (display.equals(realName)) {
			this.logger.info("CONTACTS ONLY.SIZE: " + contactsOnly.size());
			available = this.peer.createMemberPresence(session, contactsOnly);
			if (available.size() == 0) {
				available.add(realName);
			}
		} else {
			available.add(display);
			available.add(realName);
		}
		return this.peer.getOnlineForVectorOfStrings(available, session);
	}
}
