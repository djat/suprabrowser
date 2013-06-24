package ss.server.db;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;

import ss.common.SphereDefinitionCreator;
import ss.common.UnexpectedRuntimeException;
import ss.common.VerifyAuth;
import ss.common.XmlDocumentUtils;
import ss.common.domain.service.ISupraSphereFacade;
import ss.domainmodel.LoginSphere;
import ss.domainmodel.SphereEmail;
import ss.domainmodel.SphereItem;
import ss.domainmodel.Statement;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.SupraSphereStatement;
import ss.refactor.Refactoring;
import ss.refactor.supraspheredoc.SupraSphereRefactor;
import ss.server.networking.DialogsMainPeer;
import ss.util.VariousUtils;
import ss.util.XMLSchemaTransform;

public class XmldbUtils {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(XmldbUtils.class);

	private final XMLDB xmldbOwner;

	/**
	 * @param xmldbOwner
	 */
	public XmldbUtils(final XMLDB xmldbOwner) {
		super();
		this.xmldbOwner = xmldbOwner;
	}

	/**
	 * Gets the inheritedName attribute of the XMLDB object Used to check to see
	 * if it uses the same table but overlays its own privileges and type
	 * masking
	 */
	public String getInheritedName(String sphereCore, String inherited) {
		Document sphere_document = getSphereDefinition(sphereCore, inherited);
		if (sphere_document == null) {
			return inherited;
		}
		String inherited_name = null;
		Element inherit = sphere_document.getRootElement().element("inherit");
		if (inherit == null) {
			return inherited;
		} else {
			inherited_name = inherit.element("data").attributeValue("value");
		}
		if (sphere_document == null) {
			return inherited;
		}
		return inherited_name;
	}

	public String getInheritedName(String inherited) {

		Document sphere_document = getSphereDefinition((String) getSession()
				.get("supra_sphere"), inherited);

		if (sphere_document == null) {
			return inherited;
		}
		String inherited_name = null;

		Element inherit = sphere_document.getRootElement().element("inherit");

		if (inherit == null) {
			return inherited;
		} else {
			inherited_name = inherit.element("data").attributeValue("value");
		}
		return inherited_name;

	}

	/**
	 * Gets the sphereDefinition attribute of the XMLDB object
	 * 
	 * @param supra_sphere
	 *            Description of the Parameter
	 * @param sphere
	 *            Description of the Parameter
	 * @return The sphereDefinition value
	 */
	public String replaceChars(String string) {

		String newString = string.replace("&", "&amp;");

		return newString;

	}

	/**
	 * Description of the Method
	 * 
	 * @param sql
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public String quote(String sql) {
		return DbUtils.escapeQuotes(sql);
	}

	public static List<String> getTableNameMask() {
		List<String> mask = new ArrayList<String>();
		mask.add("spheres");
		mask.add("supraspheres");
		return mask;
	}

	@Refactoring( classify=SupraSphereRefactor.class, message = "Move to SupraSphereFacade implementation"	)
	public synchronized String getSphereDomain() {
		try {
			return getSupraSphere().getDomains();
		} catch (NullPointerException exc) {
			logger.error("NPE in getSphereDomain", exc);
		}
		return null;
	}

	public String getCurrentMoment() {

		Date current = new Date();
		return DateFormat.getTimeInstance(DateFormat.LONG).format(current)
				+ " "
				+ DateFormat.getDateInstance(DateFormat.MEDIUM).format(current);

	}

	/**
	 * Gets the loginSphere attribute of the XMLDB object
	 * 
	 * @param userSession
	 *            Description of the Parameter
	 * @return The loginSphere value
	 */
	@Refactoring(classify=SupraSphereRefactor.class, message="Refactor clients of this method")
	public synchronized LoginSphere findLoginSphereElement(String username) {
		SupraSphereMember member = getSupraSphere().findMemberByLogin(username);
		return new LoginSphere( member.getLoginSphereSystemName(), member.getLoginDisplayName() );
//		try {
//			final String xpath = "//suprasphere/member[@login_name=\"" + username
//					+ "\"]/login_sphere";
//			return XmlDocumentUtils.selectElementByXPath(getSupraSphereDocument(), xpath);
//		} catch (DocumentException exc) {
//			logger.error("DocumentException in getLoginSphere", exc);
//		} catch (ClassCastException exc) {
//			logger.error("Class Cast Exception in getLoginSphere", exc);
//		}
//		return null;
	}
	
	/**
	 * Gets the loginSphere system name from suprasphere document
	 * 
	 * @param userSession
	 *            Description of the Parameter
	 * @return The loginSphere value
	 */
	public synchronized String getLoginSphereSystemName(String username) {
		LoginSphere loginSphereElement = findLoginSphereElement(username);
		if ( loginSphereElement == null ) {
			throw new UnexpectedRuntimeException( "Cannot find login sphere for " + username );
		}
		return loginSphereElement.getSystemName();
	}

	@Refactoring( classify=SupraSphereRefactor.class, message = "Move to SupraSphereFacade implementation"	)
	public synchronized String getPersonalSphere(String contact,
			String contactName) {
		return getSupraSphere().getP2PSphere(contact, contactName);		
	}

	public synchronized String getSphereCore(Hashtable session) {
		String contact = (String) session.get("real_name");
		return getSphereCore(contact);
	}

	/**
	 * @param contactName
	 * @return
	 */
	private String getSphereCore(String contactName) {
		SupraSphereMember supraSphereMember = getSupraSphere().findMemberByContactName(contactName);
		return supraSphereMember != null ? supraSphereMember.getSphereCoreSystemName() : null;
	}
	
	public synchronized String getHomeSphereFromLogin(String loginName) {
		SupraSphereMember supraSphereMember = getSupraSphere().findMemberByLogin( loginName );
		return getHomeSphereFromLogin(supraSphereMember);
	}

	/**
	 * @param supraSphereMember
	 * @return
	 */
	private String getHomeSphereFromLogin(SupraSphereMember supraSphereMember) {
		if ( supraSphereMember != null ) {
			SphereItem item = supraSphereMember.getSphereByDisplayName( supraSphereMember.getContactName() );
			return item != null && item.isEnabled() ? item.getSystemName() : null;
		}
		else {
			return null;
		}
	}

	public synchronized boolean addMemberToSphereDefinition(String sphereId,
			String contactName, String loginName, String systemName,
			String displayName) {

		logger.info("Adding member to Sphere Definition: " + sphereId + " , "
				+ contactName + " , " + loginName + " , " + systemName + " , "
				+ displayName);
		Document sphereDoc = getSphereDefinition(sphereId, systemName);
		if ( sphereDoc != null ) {
			if ( XmlDocumentUtils.selectElementListByXPath(sphereDoc, "//sphere/member[@login_name = \"" + loginName + "\"]").size() == 0 ) {
				sphereDoc.getRootElement().addElement("member").addAttribute(
						"contact_name", contactName).addAttribute("login_name",
						loginName);
				replaceDoc(sphereDoc, sphereId);
				return true;
			}			
		}
		return false;
	}

	/**
	 * Description of the Method
	 * 
	 * @param contact_name
	 *            Description of the Parameter
	 * @param login_name
	 *            Description of the Parameter
	 * @param crossreference
	 *            Description of the Parameter
	 * @param decisiveUsers
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	@Refactoring( classify=SupraSphereRefactor.class, message="Move this method to ssedit facade" )
	public synchronized Document crossreferenceSpheres(String contact_name,
			String login_name, Hashtable crossreference, Hashtable decisiveUsers) {
		AcrossTableUtils across = new AcrossTableUtils(this.xmldbOwner);

		logger.warn("startign crossreferene: " + contact_name + " : "
				+ login_name);

		Document eSpheres = DocumentHelper.createDocument();
		eSpheres.addElement("eSpheres");

		Hashtable spheres = across.getAllSpheresFromSupraspheresTable();

		for (Enumeration enumerate = spheres.keys(); enumerate
				.hasMoreElements();) {
			String key = (String) enumerate.nextElement();

			logger.warn("Checking key: " + key);
			Document doc = (Document) spheres.get(key);
			String display_name = doc.getRootElement().attributeValue(
					"display_name");

			for (Enumeration enumer = decisiveUsers.keys(); enumer
					.hasMoreElements();) {
				String another = (String) enumer.nextElement();

				if (another.equals(display_name)) {
					try {
						Element dec = (Element) doc.getRootElement().element(
								"voting_model").element("specific");

						if (dec != null) {
							Element mem = (Element) doc.getRootElement()
									.element("voting_model")
									.element("specific").element("member");

							if (mem != null) {
								dec.remove(mem);
							}

							dec.addElement("member").addAttribute(
									"contact_name",
									(String) decisiveUsers.get(another));
						}
					} catch (NullPointerException exc) {
						logger.error("NPE in crossreferenceSpheres", exc);
					}
				}
			}

			String enabled = (String) crossreference.get(display_name);

			if (enabled == null) {
				logger.warn("Enabled was null");
				eSpheres.getRootElement().addElement("sphere").addAttribute(
						"display_name",
						doc.getRootElement().attributeValue("display_name"))
						.addAttribute(
								"system_name",
								doc.getRootElement().attributeValue(
										"system_name")).addAttribute(
								"sphere_type", "group").addAttribute(
								"default_delivery",
								doc.getRootElement()
										.element("default_delivery")
										.attributeValue("value")).addAttribute(
								"enabled", "false");
			} else if (enabled.equals("true")) {
				eSpheres.getRootElement().addElement("sphere").addAttribute(
						"display_name",
						doc.getRootElement().attributeValue("display_name"))
						.addAttribute(
								"system_name",
								doc.getRootElement().attributeValue(
										"system_name")).addAttribute(
								"sphere_type", "group").addAttribute(
								"default_delivery",
								doc.getRootElement()
										.element("default_delivery")
										.attributeValue("value")).addAttribute(
								"enabled", "true");

			} else {
				eSpheres.getRootElement().addElement("sphere").addAttribute(
						"display_name",
						doc.getRootElement().attributeValue("display_name"))
						.addAttribute(
								"system_name",
								doc.getRootElement().attributeValue(
										"system_name")).addAttribute(
								"sphere_type", "group").addAttribute(
								"default_delivery",
								doc.getRootElement()
										.element("default_delivery")
										.attributeValue("value")).addAttribute(
								"enabled", "false");

			}

			String apath = "//sphere/member[@contact_name=\"" + contact_name
					+ "\"]";

			try {
				logger.warn("APATH: " + apath);
				Element elem = (Element) doc.selectObject(apath);

				if (elem == null) {
					if (enabled != null) {
						if (enabled.equals("true")) {
							doc.getRootElement().addElement("member")
									.addAttribute("contact_name", contact_name)
									.addAttribute("login_name", login_name);
						}
					} else if (enabled.equals("false")) {
						doc.getRootElement().remove(elem);
					}
				}
			} catch (ClassCastException npe) {
				if (enabled != null) {
					if (enabled.equals("true")) {
						doc.getRootElement().addElement("member").addAttribute(
								"contact_name", contact_name).addAttribute(
								"login_name", login_name);
					}
				}
			}

			logger.warn("Before replacing doc " + doc.asXML());
			replaceDoc(doc, (String) getSession().get("supra_sphere"));

		}

		return eSpheres;

	}
	
	@Refactoring(classify=SupraSphereRefactor.class, message="ready to move to ss.server.networking.protocol.ssdoc")
	public synchronized Document addEmailSphereNode(SphereEmail sphereEmailNode)
			throws DocumentException {
		ISupraSphereFacade supra = getSupraSphere();
		supra.getSpheresEmails().put(sphereEmailNode);
		Document returndocument = replaceDoc(supra.getBindedDocumentForSaveToDb(),
				(String) getSession().get("supra_sphere"));

		return returndocument;
	}

	
	public Document getRootDoc(Document doc, String sphere_id) {
		if (doc != null) {
			Document thedoc = (Document) doc.clone();
			while (thedoc.getRootElement().element("response_id") != null) {
				Document cloned = (Document) thedoc.clone();
				Document parent = getParentDoc(cloned, sphere_id);

				if (parent == null) {
					break;
				} else {
					thedoc = parent;
				}
			}
			return thedoc;
		} else {
			return null;
		}
	}

	/**
	 * Gets the allOfThread attribute of the XMLDB object
	 * 
	 * @param parent_doc
	 *            Description of the Parameter
	 * @param sphere_id
	 *            Description of the Parameter
	 * @return The allOfThread value
	 */
	@SuppressWarnings("unchecked")
	public Hashtable getAllOfThread(Document parent_doc, String sphere_id) {
		Statement statement = Statement.wrap(parent_doc);

		Hashtable whole_thread = new Hashtable();

//		MessagesMutableTreeNode thread = new MessagesMutableTreeNode(null,
//				statement.getSubject(), statement.getMessageId(), null, statement.getType());

		// String new_message_id = message_id;
		// Start with a group of responses
		// For each responses, get any of its responses, and
		// theirs until each
		// of its responses doesn't have any more
		// Then, get its parent, and see if it has any more
		// responses, for each
		// of those, get all of its children

		Vector children = getChildren(sphere_id, statement.getMessageId());

		Vector newchildren = new Vector();

		newchildren.addAll(children);
		Vector onelevel = new Vector();

		onelevel.addAll(children);

		Vector nextlevel = new Vector();

		if (onelevel.size() > 0) {
			while (true) {

				Statement oneLevelStatement = Statement.wrap((Document) onelevel.elementAt(0));

				oneLevelStatement.setCurrentSphere(sphere_id);
				
//				oneLevelStatement.setResponseId();
//				doc.getRootElement().addElement("current_sphere").addAttribute(
//						"value", sphere_id);
//				Element view = doc.getRootElement();

//				String response_id = view.element("response_id")
//						.attributeValue("value");

//				try {
//
//					Enumeration enumer = thread.breadthFirstEnumeration();
//
//					while (enumer.hasMoreElements()) {
//
//						// Get the next element in the enumereration,
//						// add it to
//						// a temporary node this enumer actually
//						// represents the
//						// usenode asset, is not a copy
//						MessagesMutableTreeNode tempnode = (MessagesMutableTreeNode) enumer
//								.nextElement();
//
//						// Get the filename associated with the node
//						String message_test = tempnode.getMessageId();
//
//						// Now loop through all of the responses to see
//						// if the
//						// response_id from any of them equal the
//						// message_id
//						// from the temporary node
//
//						if (message_test.equals(oneLevelStatement.getResponseId())) {
//							MessagesMutableTreeNode tmp = new MessagesMutableTreeNode(
//									null, oneLevelStatement.getSubject(),
//									oneLevelStatement.getMessageId(), null, oneLevelStatement.getType());
//							tempnode.add(tmp);
//						}
//					}
//				} catch (NullPointerException ex) {
//
//				}

				String child_id = oneLevelStatement.getMessageId();

				Vector level_two = new Vector();
				level_two = getChildren(sphere_id, child_id);

				nextlevel.addAll(level_two);

				if (onelevel.size() == 1) {

					onelevel = new Vector();

					onelevel.addAll(nextlevel);
					newchildren.addAll(nextlevel);
					nextlevel = new Vector();

				} else {
					onelevel.removeElementAt(0);
				}

				if (onelevel.size() == 0) {
					break;
				}
			}
		}

		//whole_thread.put(statement.getMessageId(), thread);

		whole_thread.put("responses", newchildren);

		whole_thread.put("remove", "false");

		return whole_thread;

	}

	public boolean containsDocument(List all, Document doc) {
		boolean result = false;
		String message_id = doc.getRootElement().element("message_id")
				.attributeValue("value");

		for (int i = 0; i < all.size(); i++) {

			Document test = (Document) all.get(i);
			String test_id = test.getRootElement().element("message_id")
					.attributeValue("value");
			if (test_id.equals(message_id)) {

				result = true;
			}

		}

		return result;

	}

	@SuppressWarnings("unchecked")
	public Document checkAgainstCurrentVersions(Document versionsDoc) {
		Vector currentClientVersions = new Vector(versionsDoc.getRootElement()
				.elements());

		Document toDownload = DocumentHelper.createDocument();
		toDownload.addElement("dummyToCreateRootElement");

		File file = VariousUtils.getSupraFile("dyn_server.xml");

		/*
		 * File file = new File(System.getProperty("user.dir") + fsep +
		 * "dyn_server.xml");
		 */

		SAXReader reader1 = new SAXReader();

		try {
			Document doc = reader1.read(file);

			String updateSpecificName = null;
			String updateSpecificVersion = null;
			Vector versions = new Vector(doc.getRootElement().element(
					"versions").elements());

			for (int i = 0; i < versions.size(); i++) {
				Element one = (Element) versions.get(i);
				one.detach();

				if (one.element("update_specific_version_only") != null) {
					Element check = one.element("update_specific_version_only");
					updateSpecificName = check.attributeValue("name");
					updateSpecificVersion = check.attributeValue("version");

				}

				String name = one.attributeValue("name");
				boolean found = false;
				for (int j = 0; j < currentClientVersions.size(); j++) {

					Element two = (Element) currentClientVersions.get(j);
					if (two.attributeValue("name").equals(name)) {
						if (two.attributeValue("current_version").equals(
								one.attributeValue("current_version"))) {
							found = true;
						}
					}
				}
				if (found == false) {

					if (updateSpecificName != null) {

						for (int j = 0; j < currentClientVersions.size(); j++) {

							Element two = (Element) currentClientVersions
									.get(j);
							if (two.attributeValue("name").equals(
									updateSpecificName)) {
								if (two.attributeValue("current_version")
										.equals(updateSpecificVersion)) {
									toDownload.getRootElement().add(one);
								}
							}
						}

					} else {
						toDownload.getRootElement().add(one);

					}
				}

			}
		} catch (Exception ex) {
			logger.error(ex);
		}

		return toDownload;
	}

	@SuppressWarnings("unchecked")
	@Refactoring( classify=SupraSphereRefactor.class, message = "Move to SupraSphereFacade implementation"	)
	public Vector getHomeSphereForAllMembers(String sphereId)
			throws DocumentException {
		Vector<String> memberHomeSpheres = new Vector<String>();
		for( SupraSphereMember member : getSupraSphere().getSupraMembers() ) {
			if ( member.getSpheres().isEnabled( sphereId ) ) {
				String loginSphereId = getHomeSphereFromLogin(member);
				if ( loginSphereId != null ) {
					memberHomeSpheres.add(loginSphereId);
				}
			}
		}
		return memberHomeSpheres;
	}

	
	
	
	

	/**
	 * @param supraSphere
	 *            NOT USED, WHY?
	 */
	@SuppressWarnings("unchecked")
	public Vector getEmailForwardingRulesForSphere(String supraSphere,
			String sphereId) {
		Document sphereDefinition = getSphereDefinition(sphereId, sphereId);

		if (sphereDefinition != null) {
			Element emailForwarding = sphereDefinition.getRootElement()
					.element("email_forwarding");
			if (emailForwarding != null) {
				return new Vector(emailForwarding.elements());
			}
		}
		return new Vector();
	}

	public Document replaceUsernameInMembership(String loginSphere,
			String oldUsername, String newUsername, String salt, String verifier) {
		Document membershipDoc = getMembershipDoc(loginSphere, oldUsername);
		membershipDoc = XMLSchemaTransform
				.removeMachineVerifiers(membershipDoc);

		logger.info("done with transform");

		membershipDoc.getRootElement().element("login_name").addAttribute(
				"value", newUsername);
		membershipDoc.getRootElement().element("verifier").addAttribute("salt",
				salt).setText(verifier);

		try {
			membershipDoc.getRootElement().element(
					"change_passphrase_next_login").detach();
		} catch (NullPointerException npe) {
		}

		return membershipDoc;
	}

	/**
	 * Returns sphere definition. If definition not found, it will created.
	 */
	public Document getOrCreateSphereDefinition(VerifyAuth verifyAuth,
			String sphereId) {
		Document sphereDefinition = getSphereDefinition(sphereId, sphereId);
		if (sphereDefinition == null) {
			sphereDefinition = createSphereDefinition(verifyAuth
					.getDisplayName(sphereId), sphereId);
		}
		return sphereDefinition;
	}

	public Document createSphereDefinition(String sphereDisplayName,
			String sphereId) {
		if (sphereDisplayName == null) {
			throw new NullPointerException("sphereDisplayName is null");
		}
		if (sphereId == null) {
			throw new NullPointerException("sphereId is null");
		}
		SphereDefinitionCreator sdc = new SphereDefinitionCreator();
		Document sphereDefinition = sdc.createDefinition(sphereDisplayName,
				sphereId);
		insertDoc(sphereDefinition, sphereId);
		return sphereDefinition;
	}

	/**
	 * @param sphereDefinition
	 * @param sphereId
	 */
	private void insertDoc(Document doc, String sphereId) {
		this.xmldbOwner.insertDoc(doc, sphereId);
	}

	/**
	 * @param doc
	 * @param string
	 * @return
	 */
	private Document replaceDoc(Document doc, String sphereId) {
		return this.xmldbOwner.replaceDoc(doc, sphereId);
	}

	/**
	 * @param sphereId
	 * @param sphereId2
	 * @return
	 */
	private Document getSphereDefinition(String sphereId, String sphereId2) {
		return this.xmldbOwner.getSphereDefinition(sphereId, sphereId2);
	}

	/**
	 * @return
	 */
	private Hashtable getSession() {
		return this.xmldbOwner.getSession();
	}

	/**
	 * @return
	 * @throws DocumentException
	 */
	private ISupraSphereFacade getSupraSphere() {
		return this.xmldbOwner.getSupraSphere();
	}
	
	/**
	 * @param cloned
	 * @param sphere_id
	 * @return
	 */
	private Document getParentDoc(Document doc, String sphere_id) {
		return this.xmldbOwner.getParentDoc(doc, sphere_id);
	}

	/**
	 * @param loginSphereSystem
	 * @param loginName
	 * @return
	 */
	private Document getContactDoc(String loginSphereSystem, String loginName)
			throws DocumentException {
		return this.xmldbOwner.getContactDoc(loginSphereSystem, loginName);
	}

	/**
	 * @param supraSphere
	 * @return
	 */
	private Vector selectMembers(String supraSphere) {
		return this.xmldbOwner.selectMembers(supraSphere);
	}

	/**
	 * @return
	 */
	private long getNextTableId() {
		return this.xmldbOwner.getNextTableId();
	}

	/**
	 * @param sphere_id
	 * @param message_id
	 * @return
	 */
	private Vector getChildren(String sphere_id, String message_id) {
		return this.xmldbOwner.getChildren(sphere_id, message_id);
	}

	/**
	 * @param loginSphere
	 * @param oldUsername
	 * @return
	 */
	private Document getMembershipDoc(String loginSphere, String oldUsername) {
		return this.xmldbOwner.getMembershipDoc(loginSphere, oldUsername);
	}

	/**
	 * @param sphereId
	 * @param contactName
	 * @param loginName
	 * @param systemName
	 * @param displayName
	 */
	public void removeMemberFromSphereDefinition(String sphereId, String contactName, String loginName, String systemName, String displayName) {
		logger.info("Removing member from Sphere Definition: " + sphereId + " , "
				+ contactName + " , " + loginName + " , " + systemName + " , "
				+ displayName);
		Document sphereDoc = getSphereDefinition(sphereId, systemName);
		if ( sphereDoc != null ) {
			final String xpath = "//member[@login_name=\"" + loginName+ "\"]";
			for( Element member : XmlDocumentUtils.selectElementListByXPath(sphereDoc, xpath ) ) {
				member.detach();
			}				
			replaceDoc(sphereDoc, sphereId);
		}
	}
}
