/**
 * 
 */
package ss.refactor.supraspheredoc.old;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;

import ss.common.StringUtils;
import ss.common.VerifyAuth;
import ss.common.XmlDocumentUtils;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.MemberRelation;
import ss.domainmodel.PrivateSphereReference;
import ss.domainmodel.Statement;
import ss.domainmodel.SupraSphereStatement;
import ss.domainmodel.preferences.SphereOwnPreferences;
import ss.domainmodel.workflow.WorkflowConfiguration;
import ss.refactor.Refactoring;
import ss.refactor.supraspheredoc.SupraSphereRefactor;
import ss.server.db.XMLDB;
import ss.server.domain.service.IEntitleContactForSphere;
import ss.server.domain.service.ISupraSphereEditFacade;
import ss.server.domain.service.SupraSphereProvider;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.util.Expression;
import ss.server.networking.util.Filter;
import ss.server.networking.util.FilteredHandlers;
import ss.server.networking.util.HandlerKey;
import ss.util.XMLSchemaTransform;

/**
 *
 */
public class SsDocSupraSphereEditFacade implements ISupraSphereEditFacade {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SsDocSupraSphereEditFacade.class);
	
	private final XMLDB xmldb;

	private final Utils utils;
	
	/**
	 * @param xmldb
	 */
	public SsDocSupraSphereEditFacade(XMLDB xmldb) {
		super();
		if ( xmldb == null ) {
			throw new NullPointerException( "xmldb" );
		}
		this.xmldb = xmldb;
		this.utils = Utils.getUtils(xmldb);
	}

	/* (non-Javadoc)
	 * @see ss.server.db.ISupraSphereEditFacade#registerMember(java.lang.String, org.dom4j.Document, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Hashtable)
	 */
	public void registerMember(String supraSphere, Document contactDoc,
			String inviteContact, String inviteUsername, String sphereName,
			String sphereId, String realName, String username,
			String inviteSphereType, Hashtable activeSession) {
		Hashtable thisSession = new Hashtable();
		Hashtable thisUpdate = new Hashtable();
		
		thisSession.put("supra_sphere", supraSphere);
		thisSession.put("sphere_id", sphereId);
		thisSession.put("displayName", sphereName);
		thisSession.put("inviteContact", inviteContact);
		thisSession.put("inviteUsername", inviteUsername);
		thisSession.put("real_name", realName);
		thisSession.put("inviteSphereType", inviteSphereType);

		thisSession.put("realName", realName);
		thisSession.put("username", username);
		thisUpdate.put("realName", realName);
		thisUpdate.put("username", username);
		thisUpdate.put("contactDoc", contactDoc);
		
		List<DialogsMainPeer> handlers = (List<DialogsMainPeer>) DialogsMainPeerManager.INSTANCE.getHandlers();
		if ( handlers.size() > 0 ) {
			try {
				doRegister( activeSession, handlers, handlers.get(0), thisSession, thisUpdate );
			}
			catch (Exception ex) {
				logger.error( "Can't peform doRegister.", ex );
			}
		}
		else {
			logger.error( "Can't peformUserRegistering because DMP collection is empty." );
		}	


	}

	/* (non-Javadoc)
	 * @see ss.server.db.ISupraSphereEditFacade#updateUserLogin(java.lang.String, java.lang.String)
	 */
	public void updateUserLogin(String tempUsername, String username, String loginSphere, Document contactDoc ) {
		final String apath = "//suprasphere/member[@login_name=\""
				+ tempUsername + "\"]";
		if (logger.isDebugEnabled()) {
			logger.debug("TEMP APTH: " + apath);
		}
		
		final Document supraSphereDocument = getSupraSphereDocument();
		try {
			Element member = (Element) supraSphereDocument
					.selectObject(apath);

			member.addAttribute("login_name", username);

			this.xmldb.replaceDoc(supraSphereDocument, getSupraSphereName() );

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		DialogsMainPeerManager.INSTANCE.updateViryAuthAndSupraSphereDocument(loginSphere, contactDoc, supraSphereDocument );
	}

	/**
	 * @return
	 */
	private String getSupraSphereName() {
		return this.utils.getSupraSphere().getName();
	}

	/**
	 * @return
	 */
	private Document getSupraSphereDocument() {
		return this.utils.getSupraSphereDocument();
	}

	public void doRegister(Hashtable activeSession,
			Iterable<DialogsMainPeer> handlers, DialogsMainPeer registeringHandler, Hashtable thisSession, Hashtable thisUpdate)
			throws DocumentException {
		try {
			doRegisterImpl(activeSession, handlers, registeringHandler, thisSession, thisUpdate);
		} catch (DocumentException ex) {
			logger.warn("Document exception in doRegister", ex);
			throw ex;
		} catch (RuntimeException ex) {
			logger.warn("Runtime exception in doRegister", ex);
			throw ex;
		}
	}

	@SuppressWarnings("unchecked")
	private void doRegisterImpl(Hashtable activeSession,
			Iterable<DialogsMainPeer> handlers, DialogsMainPeer registeringHandler, Hashtable thisSession, Hashtable thisUpdate)
			throws DocumentException {
		
		final XMLDB xmldb = new XMLDB(activeSession);
		// the contact (real name) being registered 
		final String realName = (String) thisUpdate.get("realName"); 

		// The username of the contact being registered
		final String username = (String) thisUpdate.get("username"); 
		
		if (logger.isDebugEnabled()) {
			logger.debug("realName: " + realName);
			logger.debug("username: " + username);
		}

		// The contact document of the contact being registered
		Document contactDoc = (Document) thisUpdate.get("contactDoc");
		
		if (logger.isDebugEnabled()) {
			logger.debug("contactDoc : " + contactDoc.asXML());
		}

		final Hashtable crossreference = new Hashtable();
		crossreference.put(realName, "enabled");
		final Hashtable decisiveUsers = new Hashtable();

		final VerifyAuth verify = new VerifyAuth(thisSession);
		
		final String sphereCoreName = (String) thisSession.get("displayName");
		final String sphereId = (String) thisSession.get("sphere_id");
		if (logger.isDebugEnabled()) {
			logger.debug("sphereCoreName: " + sphereCoreName);
			logger.debug("sphereId: " + sphereId);
		}
		
		String sphereCoreId = sphereId;

		final String inviteContact = (String) thisSession.get("inviteContact");
		final String inviteUsername = (String) thisSession.get("inviteUsername");
		final String inviteSphereType = (String) thisSession.get("inviteSphereType");

		final String invitingContactLoginSphere = xmldb.getUtils().getLoginSphereSystemName(inviteUsername);
		final String displayName = (String) thisSession.get("displayName");
		
		if (logger.isDebugEnabled()) {
			logger.debug("inviteContact: " + inviteContact);
			logger.debug("inviteUsername: " + inviteUsername);
			logger.debug("inviteSphereType: " + inviteSphereType);
			logger.debug("invitingContactLoginSphere: " + invitingContactLoginSphere);
			logger.debug("displayName: " + displayName);
		}

		final boolean sphereTypeGroup = inviteSphereType.equals("group");

		final String supraSphere = (String) thisSession.get("supra_sphere");
		final Document returnMembers = registerMember(
				supraSphere, sphereId, displayName, realName,
				username, invitingContactLoginSphere);
		
		// Will return the spheres (groups) that are known to the system
		final Document eSpheres = xmldb.getUtils().crossreferenceSpheres(realName,
				username, crossreference, decisiveUsers); 
		
		String apath;
		final Vector<Document> enabledMemberDocs = new Vector<Document>();
		if (sphereTypeGroup) { 
			if (logger.isDebugEnabled()) {
				logger.debug("sphereTypeGroup is true");
			}
			// Enable the sphere in which the contact was
			// entitled/created for that contact
			apath = "//sphere[@display_name=\"" + displayName + "\"]";
			Element selectElem = (Element) eSpheres.selectObject(apath);
			selectElem.addAttribute("enabled", "true");
			// logger.info("Will entitle for group sphere");
			
			verify.setSphereDocument(returnMembers);
			Vector enabledMembers = verify.getLoginsForMembersEnabled1(sphereId);

			for (int i = 0; i < enabledMembers.size(); i++) {
				String member = (String) enabledMembers.get(i);
				// logger.info("checking member: " + member);
				String loginSphere = xmldb.getUtils().getLoginSphereSystemName(member);
				// logger.info("their login sphere: " + loginSphere);
				Document doc = xmldb.getContactDoc(loginSphere, member);
				// logger.info("their contact docs; " + doc.asXML());
				if (doc != null) {
					enabledMemberDocs.add(doc);
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("enabledMemberDocs size: " + enabledMemberDocs.size());
		}

		apath = "//suprasphere/member[@contact_name=\"" + inviteContact
				+ "\"]/sphere[@display_name=\""
				+ (String) thisSession.get("real_name") + "\"]";

		String enabledPrivateSystem = null;
		try {
			Element selectElem = (Element) returnMembers.selectObject(apath);
			enabledPrivateSystem = selectElem.attributeValue("system_name");
			if (sphereTypeGroup == false) {
				sphereCoreId = enabledPrivateSystem;
			}
		} catch (Exception ex) {
			logger.error(ex);
		}

		// Now need to set that as the sphere core, enable it for both of
		// them
		apath = "//suprasphere/member[@contact_name=\"" + realName
				+ "\"]/sphere";
		if (logger.isDebugEnabled()) {
			logger.debug("apath: " + apath);
		}
		List real = (ArrayList) returnMembers.selectObject(apath);
		Document enabledDoc = DocumentHelper.createDocument();
		Element enabled = enabledDoc.addElement("enabled_spheres");
		Vector regVec = new Vector(real);
		if (logger.isDebugEnabled()) {
			logger.debug("regVec size: " + regVec.size());
		}
		for (int j = 0; j < regVec.size(); j++) {
			Element elem = (Element) ((Element) regVec.get(j)).clone(); 
			// Remove it from the root node
			String testSystem = elem.attributeValue("system_name");
			if (testSystem.equals(enabledPrivateSystem)) {
				if (logger.isDebugEnabled()) {
					logger.debug("--------------");
				}
				elem.addAttribute("enabled", "true");
				String loginSphereSystem = xmldb.getUtils().getLoginSphereSystemName(username);
				String loginSphereName = xmldb.getUtils().findLoginSphereElement(username).getDisplayName();
				if (logger.isDebugEnabled()) {
					logger.debug("loginSphereSystem: " + loginSphereSystem);
					logger.debug("loginSphereName: " + loginSphereName);
				}
				Document existDoc = xmldb.getContactDoc(loginSphereSystem,
						username);
				if ( existDoc != null ) {
					if (logger.isDebugEnabled()) {
						logger.debug("existDoc name is: " + ContactStatement.wrap(existDoc).getContactNameByFirstAndLastNames());
					}
					// existDoc =
					// XMLSchemaTransform.addOneLocationToDoc(existDoc,existDoc,(String)activeSession.get("sphereURL"),enabledPrivateSystem,inviteContact);

					if (existDoc != null) {
						existDoc = XMLSchemaTransform.addLocationToDoc(existDoc,
								existDoc, (String) activeSession.get("sphereURL"),
								loginSphereSystem, loginSphereName,
								enabledPrivateSystem, inviteContact);
					}
					// contactDoc =
					// XMLSchemaTransform.addLocationToDoc(existDoc,existDoc,(String)activeSession.get("sphereURL"),loginSphereSystem,loginSphereName,enabledPrivateSystem,inviteContact);
					xmldb.insertDoc(existDoc, enabledPrivateSystem);
					Vector list = new Vector(existDoc.getRootElement().element(
							"locations").elements());
					for (int l = 0; l < list.size(); l++) {
						Element oneElem = (Element) list.get(l);
						String systemElem = oneElem.attributeValue("ex_system");
						xmldb.replaceDoc(existDoc, systemElem);
					}
				} else {
					logger.error("existDoc is null");
				}
			}
			enabled.add(elem);
		}
		// After reg vec
		// logger.info("enabledDoc : "+enabledDoc.asXML());
		/*
		 * logger.info("now register with members...enabled doc is: " +
		 * enabledDoc.asXML());
		 */
		// logger.warn("Enabled doc: "+enabledDoc.asXML());
		String id = contactDoc.getRootElement().element("message_id")
				.attributeValue("value");
		if (logger.isDebugEnabled()) {
			logger.debug("(sphereId,id): (" + sphereId +"," + id + ")");
		}
		contactDoc = xmldb.getSpecificID(sphereId, id);
		if (logger.isDebugEnabled()) {
			logger.debug("contactDoc: " + ((contactDoc!=null) ? contactDoc.asXML(): "null"));
		}
		logger.warn("about to go: " + realName + " : " + username + " : "
				+ sphereCoreName + " : " + sphereCoreId);
		Document returnSphereDoc = registerWithMembers(
				handlers, activeSession, realName, enabledDoc, eSpheres,
				sphereCoreName, sphereCoreId, "member");
		// registeringHandler.sendUpdateToAllMembersOfSphere(contactDoc,(String)session.get("sphere_id"));
		id = contactDoc.getRootElement().element("message_id").attributeValue(
				"value");
		contactDoc = xmldb.getSpecificID(sphereId, id);
		logger.warn("new one: " + contactDoc.asXML());

		if (sphereTypeGroup) {
			// logger.warn("ALREADY HAS IT: "+contactDoc.asXML());
			returnSphereDoc = SupraSphereProvider.INSTANCE.get(registeringHandler, IEntitleContactForSphere.class ).entitleContactForGroupSphere(
					thisSession, contactDoc, realName, username,
					sphereId, displayName, enabledMemberDocs, xmldb);
		}

		// contactDoc = xmldb.getSpecificID((String)
		// session.get("sphere_id"),id);
		// xmldb.commitDoc(contactDoc,(String)session.get("sphere_id"));
		for (DialogsMainPeer handler : handlers) {
			// logger.info("SENDING AUTH");
			handler.getVerifyAuth().setSphereDocument(
					(Document) returnSphereDoc.clone());
			final DmpResponse dmpResponse = new DmpResponse();
			dmpResponse.setStringValue("protocol", "updateVerify");
			dmpResponse.setVerifyAuthValue("verifyAuth", handler
					.getVerifyAuth());
			handler.sendFromQueue(dmpResponse);
		}
		
		logger.info("send presence update");
		DialogsMainPeer.sendForAllRefreshPresence( realName, sphereId );
		
		
		addContactToSphereWorkflowConfiguration(contactDoc, sphereId);
		
		
		final String personalSphere = xmldb.getUtils().getPersonalSphere(
				contactDoc.getRootElement().element("login").attributeValue(
						"value"), realName);

		if (inviteSphereType.equals("group")) {
			Document sphereDoc = xmldb.getSphereDefinition(
					sphereId, sphereCoreId);
			xmldb.insertDoc(sphereDoc, personalSphere);
			Filter f = new Filter();
			f.add(new Expression(HandlerKey.USERNAME, realName));
			FilteredHandlers filteredHandlers = new FilteredHandlers(f,
					handlers);
			for (DialogsMainPeer handler : filteredHandlers) {
				/*
				 * handler.sendFromQueue(temp,handler.getName());
				 */
				final DmpResponse dmpResponse = new DmpResponse();
				dmpResponse.setStringValue("protocol", "update");
				dmpResponse.setStringValue("sort", "true");
				sphereDoc.getRootElement().addElement("current_sphere")
						.addAttribute("value", sphereCoreId);
				sphereDoc.getRootElement().addElement("confirmed")
						.addAttribute("value", "true");
				dmpResponse.setDocumentValue("document", sphereDoc);
				dmpResponse.setStringValue("sphere", sphereId);

				dmpResponse.setStringValue("delivery_type", "normal");

				handler.sendFromQueue(dmpResponse);

			}

		}

	}
	

	

	private void addContactToSphereWorkflowConfiguration(Document contactDoc, final String sphereId) {
		SphereOwnPreferences preferences = SsDomain.SPHERE_HELPER.getSpherePreferences(sphereId);
		WorkflowConfiguration configuration = preferences.getWorkflowConfiguration();
		ContactStatement contact = ContactStatement.wrap(contactDoc);
		configuration.sphereMemberAdded(contact);
		SsDomain.SPHERE_HELPER.setSpherePreferences(sphereId, preferences);
	}
	
	
	@Refactoring(classify=SupraSphereRefactor.class, message="Used only from SsDocSupraSphereEditFacade")
	private Document registerWithMembers(Iterable<DialogsMainPeer> handlers,
			Hashtable session, String cname, Document enabledDoc,
			Document eSpheres, String sphereName, String sphereId,
			String sphereType) throws DocumentException {
		Document doc = getSupraSphereDocument();

		Vector elems = new Vector(enabledDoc.getRootElement().elements());
		Vector spheres = new Vector(eSpheres.getRootElement().elements());
		String apath = "//suprasphere/member[@contact_name=\"" + cname + "\"]";

		Element cname_elm = null;
		try {

			cname_elm = (Element) doc.selectObject(apath);

			cname_elm.addElement("sphere_core").addAttribute("display_name",
					sphereName).addAttribute("system_name", sphereId)
					.addAttribute("sphere_type", sphereType);
		} catch (ClassCastException ex) {
			logger.error("ClassCastException in registerWithMembers", ex);
		}

		for (Element elem : XmlDocumentUtils.selectElementListByXPath(doc,
				"//suprasphere/member[@contact_name=\"" + cname
						+ "\"]/sphere[@sphere_type=\"" + "member" + "\"]")) {
			cname_elm.remove(elem);
		}

		for (int i = 0; i < elems.size(); i++) {
			Element one = (Element) elems.get(i);
			String enabled = one.attributeValue("enabled");
			String contact_name = one.attributeValue("display_name");

			Element add = (Element) one.clone();
			add.addAttribute("display_name", contact_name);

			cname_elm.add(add);

			if (enabled.equals("true")) {

				String type = add.attributeValue("sphere_type");
				if (type.equals("member")) {

					// String systemName = add.attributeValue("system_name");
					// String displayFor = add.attributeValue("display_name");
				}
			}
		}

		for (int i = 0; i < elems.size(); i++) {

			Element one = (Element) elems.get(i);
			String enabled = one.attributeValue("enabled");
			String contact_name = one.attributeValue("display_name");
			apath = "//suprasphere/member[@contact_name=\"" + contact_name
					+ "\"]/sphere[@display_name=\"" + cname + "\"]";

			logger.warn("First apath: " + apath);

			Element add = (Element) one.clone();
			add.addAttribute("display_name", cname);

			try {
				Element elem = (Element) doc.selectObject(apath);
				apath = "//suprasphere/member[@contact_name=\"" + contact_name
						+ "\"]";
				Element two = (Element) doc.selectObject(apath);
				two.remove(elem);
				two.add(add);

				if (enabled.equals("true")) {
					logger.warn("it was enabled: " + apath);

					String systemName = add.attributeValue("system_name");

					String loginName = two.attributeValue("login_name");
					String loginSphereSystem = two.element("login_sphere")
							.attributeValue("system_name");
					String loginSphereName = two.element("login_sphere")
							.attributeValue("display_name");

					logger.warn("getting contact doc: " + loginSphereSystem
							+ " : " + loginName);
					Document document = this.xmldb.getContactDoc(loginSphereSystem,
							loginName);

					if (document != null) {
						if (document.getRootElement().element("locations") == null) {

							document = XMLSchemaTransform.addLocationToDoc(
									document, document, (String) session
											.get("sphereURL"),
									loginSphereSystem, loginSphereName,
									systemName, cname);
							document = XMLSchemaTransform.addLocationToDoc(
									document, document, (String) session
											.get("sphereURL"), null, null,
									sphereId, sphereName);

							document = this.xmldb.replaceDoc(document, loginSphereSystem);
						} else {
							Vector list = new Vector(document.getRootElement()
									.element("locations").elements());

							document = XMLSchemaTransform.addLocationToDoc(
									document, document, (String) session
											.get("sphereURL"),
									loginSphereSystem, loginSphereName,
									systemName, cname);

							for (int j = 0; j < list.size(); j++) {

								Element oneElem = (Element) list.get(j);

								String systemElem = oneElem
										.attributeValue("ex_system");

								xmldb.replaceDoc(document, systemElem);
							}
						}

						Vector locations = null;
						if (document.getRootElement().element("locations") != null) {
							locations = new Vector(document.getRootElement()
									.element("locations").elements());
						}
						for (int k = 0; k < locations.size(); k++) {

							Element loc = (Element) locations.get(k);
							String locSphereId = loc
									.attributeValue("ex_system");

							logger.info("Sending to this loc sphere id: "
									+ locSphereId);

							Hashtable temp = new Hashtable();
							temp.put("document", document);
							temp.put("protocol", "update_document");

							for (DialogsMainPeer handler : handlers) {

								String name = handler.getName();

								StringTokenizer st = new StringTokenizer(name,
										",");

								st.nextToken();
								String contact = st.nextToken();

								apath = "//suprasphere/member[@contact_name=\""
										+ contact + "\"]/sphere[@system_name='"
										+ locSphereId
										+ "' and @enabled='true']";

								if (handler.getVerifyAuth().isSphereEnabledForMember(locSphereId,contact)) {

									// String type = document.getRootElement()
									// .element("type").attributeValue(
									// "value");
									
								}
							}

						}

						Document other = (Document) document.clone();

						xmldb.insertDoc(other, systemName);
					}
				} else {
					logger.warn("it was NOT enabled: " + apath + " : "
							+ one.asXML());
				}

			} catch (ClassCastException ex) {
				logger.error("third ClassCastException in registerWithMembers");
			}
		}

		apath = "//suprasphere/member[@contact_name=\"" + cname + "\"]";

		Element rootcontact = (Element) doc.selectObject(apath);

		apath = "//suprasphere/member[@contact_name=\"" + cname
				+ "\"]/sphere[@sphere_type=\"" + "group" + "\"]";

		try {
			String newpath = "//suprasphere/member[@contact_name=\"" + cname
					+ "\"]/sphere[@sphere_type=\"group\"]";

			List real = (ArrayList) doc.selectObject(newpath);

			for (int i = 0; i < real.size(); i++) {
				Element one = (Element) real.get(i);

				try {
					String type = one.attributeValue("sphere_type");

					if (type.equals("group")) {
						rootcontact.remove(one);
					}
				} catch (Exception ex) {
					logger.error("exception in registerWithMembers", ex);
				}
			}
		} catch (Exception ex) {
			logger.error("second exception in registerWithMembers", ex);
		}

		for (int i = 0; i < spheres.size(); i++) {
			Element one = (Element) spheres.get(i);
			Element add = (Element) one.clone();

			try {
				rootcontact.add(add);
			} catch (Exception exc) {
				logger.error("exception in registerWithMembers 2", exc);
			}
		}

		Document returnSphereDoc = this.xmldb.replaceDoc(doc, (String) session
				.get("supra_sphere"));

		return returnSphereDoc;
	}


	/**
	 * Description of the Method
	 * 
	 * @param sphere
	 *            Description of the Parameter
	 * @param contact_name
	 *            Description of the Parameter
	 * @param login_name
	 *            Description of the Parameter
	 */
	@SuppressWarnings("unchecked")
	@Refactoring(classify=SupraSphereRefactor.class, message="Used only from SsDocSupraSphereEditFacade")
	private synchronized Document registerMember(String supraSphere,
			String sphereId, String sphereName, String contact_name,
			String login_name, String invitingContactsLoginSphere) {
		Document doc = null;

		try {
			doc = getSupraSphereDocument();

			String check = "//suprasphere/member[@contact_name=\""
					+ contact_name + "\"]";

			try {
				Element existingMember = (Element) doc.selectObject(check);

				if (existingMember != null) {

					existingMember.detach();

				}
			} catch (ClassCastException ex) {
				Vector all = new Vector((ArrayList) doc.selectObject(check));

				for (int i = 0; i < all.size(); i++) {

					Element one = (Element) all.get(i);

					one.detach();
				}
			}

			DefaultElement newelm = new DefaultElement("member");

			newelm.addAttribute("contact_name", contact_name).addAttribute(
					"login_name", login_name);

			newelm.addElement("login_sphere").addAttribute("display_name",
					sphereName).addAttribute("system_name", sphereId)
					.addAttribute("sphere_type", "group");

			// newelm.addElement("persona").addAttribute("name",persona_name);
			newelm.addElement("perspective").addAttribute("name", supraSphere)
					.addAttribute("value", "default");
			newelm.element("perspective").addElement("thread_types");
			newelm.element("perspective").element("thread_types").addElement(
					"message");
			newelm.element("perspective").element("thread_types").addElement(
					"bookmark");
			newelm.element("perspective").element("thread_types").addElement(
					"file");

			newelm.element("perspective").addElement("keyword").addAttribute(
					"value", contact_name);
			newelm.element("perspective").addElement("recent").addAttribute(
					"value", "false");
			newelm.element("perspective").addElement("active").addAttribute(
					"value", "false");
			newelm.element("perspective").addElement("mark").addAttribute(
					"value", "");

			long longer = this.xmldb.getNextTableId();

			String per_sphere_id = (Long.toString(longer));
			newelm.addElement("sphere").addAttribute("display_name",
					supraSphere).addAttribute("system_name", supraSphere)
					.addAttribute("default_delivery", "normal").addAttribute(
							"sphere_type", "group").addAttribute("enabled",
							"false");

			newelm.addElement("sphere").addAttribute("display_name",
					contact_name).addAttribute("system_name", per_sphere_id)
					.addAttribute("default_delivery", "confirm_receipt")
					.addAttribute("sphere_type", "member").addAttribute(
							"enabled", "true");

			// String sphereCoreId = getSphereCore(getSession()); // was
			// getSphereCore(contact_name)

			Vector privateMembers = this.xmldb.selectMembers(invitingContactsLoginSphere);

			Vector pairs = this.xmldb.selectMembers(supraSphere);

			if (!invitingContactsLoginSphere.equals(supraSphere)) {
				pairs.addAll(privateMembers);
			}
			Vector new_spheres = new Vector();

			for (int i = 0; i < pairs.size(); i++) {

				String coll_name = (String) pairs.get(i);

				// Now go through and add the

				if (!coll_name.equals(contact_name)
						&& !coll_name.equals(supraSphere)) {
					long longnum = this.xmldb.getNextTableId();

					String sphere_id = (Long.toString(longnum));

					new_spheres.add(sphere_id);

					newelm
							.addElement("sphere")
							.addAttribute("display_name", coll_name)
							.addAttribute("system_name", sphere_id)
							.addAttribute("default_delivery", "confirm_receipt")
							.addAttribute("sphere_type", "member")
							.addAttribute("enabled", "false");

					// logger.info("here is the status...adding it
					// here:
					// "+newelm.asXML());

					final String cpath = "//suprasphere/member[@contact_name=\""
							+ coll_name
							+ "\"]/sphere[@display_name=\""
							+ contact_name + "\"]";

					logger.info("registerMember try xpath:" + cpath);
					Object result = doc.selectObject(cpath);
					if (result != null) {
						if (result instanceof Element) {
							((Element) result).detach();
						} else {
							Vector<Element> removeThese = new Vector<Element>(
									(List) result);
							for (Element elem : removeThese) {
								elem.detach();
							}
						}
					}
					try {

						final String apath = "//suprasphere/member[@contact_name=\""
								+ coll_name + "\"]";
						Element elem = (Element) doc.selectObject(apath);

						elem.addElement("sphere").addAttribute("display_name",
								contact_name).addAttribute("system_name",
								sphere_id).addAttribute("default_delivery",
								"confirm_receipt").addAttribute("sphere_type",
								"member").addAttribute("enabled", "false");

						if (elem == null) {
							// logger.info("null element in
							// getavailsphere");
						}

						// Setting Email aliases for person-to-person sphere.
//						VerifyAuth auth = new VerifyAuth(doc);
//						String login = auth.getLoginForContact(coll_name);
//						SphereEmail sphereEmail = new SphereEmail();
//						sphereEmail.setSphereId(sphere_id);
//						List<String> aliases = EmailAliasesCreator.createAddressStringOfPersonToPersonShpere(
//								login, coll_name, login_name, contact_name, sphere_id, auth);
//						SpherePossibleEmailsSet set = new SpherePossibleEmailsSet(aliases);
//						sphereEmail.setEmailNames(set);
//						sphereEmail.setEnabled(true);
//						sphereEmail.setIsMessageIdAdd(true);
//						SupraSphereStatement.wrap(doc).getSpheresEmails().put(
//								sphereEmail);

					} catch (ClassCastException npe) {
						// logger.info("the problem was with adding
						// contact name
						// sphere id");
						// npe.printStackTrace();
					}
				}
			}

			doc.getRootElement().add(newelm);

			this.xmldb.replaceDoc(doc, supraSphere);
		} catch (Exception exc) {
			logger.error("Can't register memeber", exc);
		}

		return doc;
	}
	
	
	public void addMemberToGroupSphereLight(String parentSystemName,
			String systemName, String displayName, String loginName,
			String contactName) {
		final SupraSphereStatement supraSphere = getSupraSphere();
		supraSphere.enableSphereForMember(systemName, loginName);
		final List<PrivateSphereReference> createdPrivateSpheres = supraSphere
				.createMissedPrivateSpheres(systemName);
		try {
			this.xmldb.replaceDoc(supraSphere.getBindedDocument(), supraSphere
					.getSystemName());
			if (parentSystemName != null) {
				logger
						.debug("Adding member to sphere definition in the parent "
								+ parentSystemName + "." + systemName);
				this.xmldb.getUtils().addMemberToSphereDefinition(
						parentSystemName, contactName, loginName, systemName,
						displayName);
			}
			this.xmldb.getUtils().addMemberToSphereDefinition(systemName,
					contactName, loginName, systemName, displayName);
			ensureContactDocumentExists(systemName, loginName, contactName, supraSphere);
			for (PrivateSphereReference sphereReferece : createdPrivateSpheres) {
				final MemberRelation relation = sphereReferece
						.getForwardRelation();
				final String firstName = supraSphere
						.getLoginByContactName(relation.getFirstContactName());
				final String secondName = supraSphere
						.getLoginByContactName(relation.getSecondContactName());
				ensureContactDocumentExists(sphereReferece.getSphereId(),
						firstName, contactName, supraSphere);
				ensureContactDocumentExists(sphereReferece.getSphereId(),
						secondName, contactName, supraSphere);
			}
		} catch (Throwable ex) {
			logger.error("Exception in entitleContactForGroupSphere", ex);
		}
	}

	/**
	 * @param sphereId
	 * @param loginName
	 * @param contactName 
	 * @param supraSphere
	 */
	private void ensureContactDocumentExists(String sphereId, String loginName,
			String contactName, final SupraSphereStatement supraSphere) {
		Document existedDoc = this.xmldb.getContactDoc(sphereId, loginName);
		if (existedDoc == null) {
			try {
				existedDoc = this.xmldb.getContactDocOnContactName( sphereId, contactName );
			} catch (Exception ex) {
				logger.error("Error in getting contact by contact name",ex);
			}
			if (existedDoc != null) {
				logger.error("existedDoc by contact name taken: " + contactName);
				ContactStatement contactSt = ContactStatement.wrap(existedDoc);
				logger.error("login: " + loginName);
				logger.error("sphereId: " + sphereId);
				logger.error("Contact login name: " + contactSt.getLogin());
				if ( (loginName != null) && (StringUtils.isBlank(contactSt.getLogin())) ) {
					contactSt.setLogin(loginName);
					logger.error("setting login: " + loginName);
					logger.error("sphereId: " + sphereId);
					this.xmldb.replaceDoc(contactSt.getBindedDocument(), sphereId);
				}
			} else {
				final String memberCoreSphereId = supraSphere.getSupraMembers()
						.findCoreSphereFor(loginName);
				if (memberCoreSphereId != null) {
					Document coreContactDocument = this.xmldb.getContactDoc(
							memberCoreSphereId, loginName);
					if (coreContactDocument != null) {
						this.xmldb.insertCopy(Statement.wrap(coreContactDocument),
								sphereId);
					} else {
						logger.error("Can't find contact document for " + loginName
								+ " in " + memberCoreSphereId);
					}
				} else {
					logger.error("Can't find core sphere for " + loginName);
				}
			}
		}
	}

	public void removeMemberForGroupSphereLight(String parentSystemName,
			String systemName, String displayName, String loginName,
			String contactName) {

		SupraSphereStatement supraSphere = getSupraSphere();
		final Document supraSphereDoc = getSupraSphereDocument();

		String apath = "//suprasphere/member[@contact_name=\"" + contactName
				+ "\"]/sphere[@system_name=\"" + systemName + "\"]";

		Element cname_elm = null;
		try {
			cname_elm = (Element) supraSphereDoc.selectObject(apath);
			cname_elm.addAttribute("enabled", "false");
			this.xmldb.replaceDoc(supraSphereDoc, supraSphere.getSystemName());
			if (parentSystemName != null) {
				logger
						.debug("Removing member from sphere definition in the parent "
								+ parentSystemName + "." + systemName);
				this.xmldb.getUtils().removeMemberFromSphereDefinition(
						parentSystemName, contactName, loginName, systemName,
						displayName);
			}
			this.xmldb.getUtils().removeMemberFromSphereDefinition(systemName,
					contactName, loginName, systemName, displayName);
		} catch (RuntimeException ex) {
			logger.error("RuntimeException in removeMemberForGroupSphereLight",
					ex);
		}
	}

	/**
	 * @return
	 */
	private SupraSphereStatement getSupraSphere() {
		return this.utils.getSupraSphere();
	}

	/* (non-Javadoc)
	 * @see ss.server.db.ISupraSphereEditFacade#makeCurrentSphereCore(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public Document makeCurrentSphereCore(String supraSphereName, String login,
			String sphereId, String sphereName, String sphereType) {
		try {
			Document sphereDoc = getSupraSphereDocument();

			String apath = "//suprasphere/member[@login_name=\"" + login
					+ "\"]/login_sphere";

			try {
				Element elem = (Element) sphereDoc.selectObject(apath);

				if (elem != null) {
					elem.addAttribute("system_name", sphereId).addAttribute(
							"display_name", sphereName).addAttribute(
							"sphere_type", sphereType);

					logger.info("ELEM> " + elem.asXML());
				}

				apath = "//suprasphere/member[@login_name=\"" + login
						+ "\"]/sphere_core";
				Element sphere = (Element) sphereDoc.selectObject(apath);
				sphere.addAttribute("system_name", sphereId).addAttribute(
						"display_name", sphereName).addAttribute("sphere_type",
						sphereType);
				return this.xmldb.replaceDoc(sphereDoc, supraSphereName);
			} catch (ClassCastException cce) {
				logger.error(cce.getMessage(), cce);
				return null;
			}
		} catch (NullPointerException de) {
			logger.error(de.getMessage(), de);
			return null;
		}

	}

}
