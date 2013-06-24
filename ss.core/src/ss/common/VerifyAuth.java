package ss.common;

/*


 Stored on both the client and server to know what methods and assets are available
 to a given person based on their persona. If a method is not available, such as "reply",
 "vote", "delete" as a possible action, then the interface will not display it. This is
 also checked on the server side as well, although there can always be more stringent checks

 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.ui.SupraMenuBar;
import ss.common.domain.service.ISupraSphereFacade;
import ss.common.domain.service.SupraSphereFacadeFactory;
import ss.common.privileges.PrivilegesManager;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.MemberReference;
import ss.domainmodel.SphereEmailCollection;
import ss.domainmodel.SphereItem;
import ss.domainmodel.SphereReference;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.SupraSphereStatement;
import ss.framework.entities.xmlentities.SingletonWrapper;
import ss.global.SSLogger;
import ss.smtp.reciever.EmailProcessor;
import ss.util.EmailUtils;
import ss.util.SessionConstants;

@ss.refactor.Refactoring(classify = ss.refactor.supraspheredoc.SupraSphereRefactor.class)
public class VerifyAuth implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4581120329996735035L;

	private static final Logger logger = SSLogger.getLogger(VerifyAuth.class);

	static SingletonWrapper SINGLETON_WRAPPER = new SingletonWrapper();
	
	@SuppressWarnings("unused")
	private final Document unusedDocToBackwardCompatibility = null;

	public Document internalSphereDocument = null;

	private Document contactDocument = null;

	private final Hashtable session;

	private transient ISupraSphereFacade supraSphere;

	private transient UserSession userSession;

	/**
	 * First counstructor.
	 * 
	 * TODO: clarify when object recived supraSphereDocument
	 * 
	 */
	public VerifyAuth(Hashtable sessionForSerialize) {
		this.session = sessionForSerialize;
	}

	/**
	 * Second constructor.
	 * 
	 * TODO: clarify when object recive session
	 * 
	 */
	public VerifyAuth(Document supraSphereDocument) {
		this( new Hashtable() );
		setSphereDocument(supraSphereDocument);
	}

	/**
	 * Gets the session
	 * 
	 * TODO: clarify what will be with caller if session is null
	 * 
	 * @return sessiong hashtable
	 */
	public Hashtable getSession() {
		return this.session;
	}

	/**
	 * Set contact document
	 */
	public void setContactDocument(Document contactDocument) {
		this.contactDocument = contactDocument;
	}

	/**
	 * Get contact document
	 */
	public ContactStatement getContactStatement() {
		return SINGLETON_WRAPPER.wrap(this.contactDocument,
				ContactStatement.class);
	}

	/**
	 * Returns window position element or null
	 * 
	 * TODO: move out from this class, replace Elememt value by something more
	 * significant
	 * 
	 * @param systemName
	 *            don't know
	 * @return window position element or null
	 */
	@SuppressWarnings("unchecked")
	public Element getWindowPositionFor(String systemName) {
		Element order = null;
		String apath = "//contact/window_position/order[@system_name=\""
				+ systemName + "\"]";
		logger.debug("trying get build order for : " + apath);
		try {
			try {
				order = (Element) this.contactDocument.selectObject(apath);
			} catch (ClassCastException exep) {

				Vector vec = new Vector((List) this.contactDocument
						.selectObject(apath));

				order = (Element) vec.get(vec.size() - 1);

			}
		} catch (Exception e) {

		}
		return order;

	}

	/**
	 * Returns build order element or null
	 * 
	 * TODO: move out from this class, replace Elememt value by something more
	 * significant
	 * 
	 */
	public Element getBuildOrder() {

		Element order = null;
		String apath = "//contact/build_order";

		try {
			try {
				order = (Element) this.contactDocument.selectObject(apath);
			} catch (ClassCastException exep) {

			}
		} catch (Exception e) {

		}
		return order;

	}

	/**
	 * Return true if shperer with checkSphereId id is personal for given
	 * loginName and contantName
	 */
	public boolean isPersonal(String checkSphereId, String loginName,
			String contactName) {
		return this.getSupraSphere().isPersonal(checkSphereId, loginName,
				contactName);
	}

	/**
	 * Returns personal shpere or null
	 * 
	 */
	public String getPersonalSphereFromLogin(String loginName) {
		return this.getSupraSphere().getPersonalSphereFromLogin(loginName);
	}

	/*
	 * Returns personal shpere or null
	 * 
	 */
	public SphereReferenceList getAllSpheres() {
		return this.getSupraSphere().getAllSpheres();
	}
	
	public String getFormattedStringWithAllSpheresIncludedForSQLQuery(){
		String result = "";
		SphereReferenceList list = getAllSpheres();
		if (list == null) {
			return result;
		}
		boolean addComma = false;
		for ( SphereReference ref : list ) {
			if ( addComma ) {
				result += ",";
			} else {
				addComma = true;
			}
			result += "'" + ref.getSystemName() + "'";
		}
		return result;
	}

	public SphereReferenceList getAllSpheresByLoginName(String login) {
		return this.getSupraSphere().getAllEnabledSpheresByLogin(login);
	}

	public SphereReferenceList getAllSpheresByContactName(String contact) {
		return this.getSupraSphere().getAllEnabledSpheresByContactName(contact);
	}

	public SphereEmailCollection getSpheresEmails() {
		return getSupraSphere().getSpheresEmails();
	}

	/**
	 * Returns true if given contactName and loginName is administrator for
	 * sphere
	 * 
	 * @param contactName
	 * @param loginName
	 * @return
	 */
	public boolean isAdmin(String contactName, String loginName) {
		return getSupraSphere().isAdmin(contactName, loginName);
	}

	public String getRealName(String loginName) {
		return getSupraSphere().getContactNameByLogin(loginName);
	}

	public synchronized ISupraSphereFacade getSupraSphere() {
		if (this.supraSphere == null) {
			this.supraSphere = new SupraSphereFacadeFactory().create(this);
		}
		return this.supraSphere;
	}

//	public boolean checkAuth(String apath, Document sphereDoc) {
//		return XmlDocumentUtils.selectElementListByXPath(sphereDoc, apath)
//				.size() == 1;
//	}

	public String getPrivateForSomeoneElse(String contactName) {
		return this.getSupraSphere().getPrivateForSomeoneElse(contactName);
	}

	public String getLoginForContact(String contactName) {
		return this.getSupraSphere().getLoginForContact(contactName);
	}

	public String getSharedSphereIdForContactPair(String firstContact,
			String secondContact) {
		return this.getSupraSphere().getSharedSphereIdForContactPair(
				firstContact, secondContact);
	}

	public Vector<String> getLoginsForMembersEnabled1(String sphereId) {
		return this.getSupraSphere().getLoginsForMemberEnabled(sphereId);
	}

	public Vector<String> getContactsForMembersEnabled1(String contactName) {
		return this.getSupraSphere().getContactsForMemberEnabled(contactName);
	}

	public synchronized void setSphereDocument(Document new_sphere_doc) {
		if (logger.isDebugEnabled()) {
			logger.debug("Update suprasphere document "
					+ XmlDocumentUtils.toPrettyString(new_sphere_doc));
		}
		if ( this.internalSphereDocument != new_sphere_doc ) {
			this.internalSphereDocument = new_sphere_doc;
			this.supraSphere = null;
		}
	}
	
	/**
	 * Don't use this method directly. 
	 * It's should be used only SupraSphereFacadeFactory.
	 * Don't rename or move it w\o mirror changes in SupraSphereFacadeFactory.
	 * @return
	 */
	protected SupraSphereStatement getInteralSupraSphere() {
		return SINGLETON_WRAPPER.wrap( this.internalSphereDocument, SupraSphereStatement.class );
	}

	public String getSphereType(String system_name) {
		String contactName = (String) getSession().get("real_name");
		return this.getSupraSphere().getSphereType(system_name, contactName);
	}

	/**
	 * Returns syste, name for user shpere. Only for user located in root sphere
	 */
	public String getSystemName(String display_name) {
		return getSystemName(display_name, getContactName());
	}

	public String getSystemName(String display_name, String contact_name) {
		return this.getSupraSphere().getSystemName(display_name, contact_name);
	}

	public String getEmailSphere(String loginName, String contact) {
		if (logger.isDebugEnabled()) {
			logger.debug("In getEmailSphere, loginName: " + loginName
					+ ", contact: " + contact);
		}
		String sphereId;
		String displayName = EmailUtils.getEmailSphereOnLogin(loginName);
		if (logger.isDebugEnabled()) {
			logger.debug("displayName of Email Box: " + displayName);
		}
		try {
			sphereId = getSystemName(displayName);
			if (sphereId == null) {
				sphereId = getSystemName(displayName, contact);
			}
			if (sphereId != null) {
				return sphereId;
			}
		} catch (Exception ex) {
			logger.error("Error getting sphere system name for email box", ex);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getSphereSystemNameByContactAndDisplayName");
		}
		sphereId = getSphereSystemNameByContactAndDisplayName(displayName,
				contact);
		if (logger.isDebugEnabled()) {
			logger.debug("sphereId is "
					+ ((sphereId == null) ? ("null") : (sphereId)));
		}
		return sphereId;
	}

	public String getLoginSphere(String loginName) {
		return this.getSupraSphere().getLoginSphere(loginName);
	}

	public boolean getEnabled(String displayName) {
		String contactName = getContactName();
		return this.getSupraSphere().isSphereEnabledForContact(displayName, contactName);
	}

	public String getSphereSystemNameByContactAndDisplayName(
			String display_name, String contact_name) {
		return getSupraSphere().getSphereSystemNameByContactAndDisplayName(
				display_name, contact_name);
	}

	public String getDisplayName(String system_name) {
		final String contactName = getContactName();
		return getDisplayName(contactName, system_name);
	}

	public String getDisplayName(String contactNameThatOwnerForSphere,
			String sphereSystemName) {
		return getSupraSphere().getSphereDisplayName(
				contactNameThatOwnerForSphere, sphereSystemName);
	}

	@SuppressWarnings("unchecked")
	public String getDisplayNameWithoutRealName(String system_name) {
		return this.getSupraSphere().getDisplayNameWithoutRealName(system_name);
	}

	public String getSphereCore() {
		final String contactName = getContactName();
		return this.getSupraSphere().getSphereCoreDisplayNameFor(contactName);
	}

	public String getSupraSphereName() {
		return getSupraSphere().getSupraSphereName();
	}

	public String getSphereCoreId() {
		String contactName = getContactName();
		return getSupraSphere().getSphereCoreId(contactName);
	}

	public Vector<String> getAvailableGroupSpheres() {
		final String contactName = getContactName();
		return getSupraSphere().getAvailableGroupSpheres(contactName);
	}

	/**
	 * @return
	 */
	protected String getContactName() {
		return (String) this.session.get("real_name");
	}

	@SuppressWarnings("unchecked")
	public Vector<String> getAvailableSpheres() {
		final String contactName = getContactName();
		return getSupraSphere().getAvailableSpheres(contactName);
	}

	public Vector<String> getMembersFor(String contact_name) {
		return getSupraSphere().getMembersFor(contact_name);
	}

	public synchronized Vector<String> getEnabledSpheres(String contactName) {
		if (logger.isDebugEnabled()) {
			logger.debug("Trying get enabled: " + contactName);
		}
		Vector<String> result = new Vector<String>();
		SupraSphereMember member = getSupraSphere()
				.getSupraMemberByLoginName(getLoginForContact(contactName));
		for (SphereItem sphere : member.getSpheres()) {
			if (sphere.isEnabled()) {
				result.add(sphere.getSystemName());
			}
		}
		return result;
	}

	public synchronized Vector<String> getCurrentMemberEnabledSpheres() {
		return getEnabledSpheres(getContactName());
	}

	public synchronized boolean isCurentSphereEnabledForContact(
			UserSession userSession, String contactName) {
		final String sphereId = userSession.getSphereId();
		final String memberLogin = getLoginForContact(contactName);
		return getSupraSphere().isSphereEnabledForMember(sphereId, memberLogin);
	}

	/**
	

	public boolean isUserExist(String login) {
		return this.getSupraSphere().isUserExists(login);
	}

	/**
	 * @return Returns privileges manager
	 */
	public PrivilegesManager getPrivilegesManager() {
		// @TODO: resolve user name usage
		// String userName = "";
		return new PrivilegesManager(this);
	}

	public synchronized UserSession getUserSession() {
		if ( this.userSession == null ) {
			this.userSession = new UserSession( this, this.session );
		}
		return this.userSession;
	}

	public String getDomain() {
		return getSupraSphere().getDomains();
	}

	public List<SupraSphereMember> getAllMembers() {
		return getSupraSphere().getAllMembers();
	}

	public List<SphereReference> getAllAvailablePrivateSpheres(String login) {
		return getSupraSphere().getAllAvailablePrivateSpheres(login);
	}

	public List<MemberReference> getMembersForSphere(String sphereId) {
		return getSupraSphere().getMembersForSphere(sphereId);
	}

	public boolean isSphereEnabledForMember(String sphereId, String memberLogin) {
		return getSupraSphere().isSphereEnabledForMember(sphereId, memberLogin);
	}

	/**
	 * @param sphere
	 * @return
	 */
	public boolean isSpherePersonal(SphereStatement sphere) {
		return this.getSupraSphere().isSpherePersonal(sphere);
	}

	/**
	 * @param login
	 * @return
	 */
	public String getPrivateSphereId(String login) {
		SupraSphereMember member = getSupraSphere().getSupraMemberByLoginName(
				login);
		SphereItem sphere = member.getSphereByDisplayName(member
				.getContactName());
		return sphere.getSystemName();
	}

	/**
	 * @param displayName
	 * @return
	 */
	public boolean isSphereExists(String display_name) {
		return this.getSupraSphere().isSphereExists(display_name,
				getContactName());
	}

	/**
	 * @return
	 */
	public boolean isAdmin() {
		String contactName = (String) this.session
				.get(SessionConstants.REAL_NAME);
		String loginName = (String) this.session.get(SessionConstants.USERNAME);
		return isAdmin(contactName, loginName);
	}

	/**
	 * 
	 */
	public List<SphereItem> getAllGroupSpheres() {
		List<SphereItem> spheres = new ArrayList<SphereItem>();
		SupraSphereMember member = getSupraSphere().getSupraMemberByLoginName(
				getContactStatement().getLogin());
		for (SphereItem sphere : member.getSpheres()) {
			if (sphere.getSphereType() == SphereItem.SphereType.GROUP
					&& !sphere.getDisplayName().contains("Email Box")) {
				spheres.add(sphere);
			}
		}
		return spheres;
	}

	/**
	 * @return
	 */
	public List<String> getAvailableGroupSpheresId() {
		String contactName = getContactName();
		return this.getSupraSphere().getAvailableGroupSpheresId(contactName);
	}

	/**
	 * @return
	 */
	public String getSupraSphereInformationForDump() {
		return getSupraSphere().getInformationForDump();
	}

	/**
	 */
	public boolean isUserExist(String login) {
		return getSupraSphere().findMemberByLogin( login ) != null;
	}
	
	/**
	 * @return
	 */
	public List<String> getOwnSpheres() {
		List<String> list = new ArrayList<String>();
		list.add( getContactName() );
		return list;
	}

	/**
	 * @return
	 */
	public List<String> getAvailablePrivateSpheres() {
		return getSupraSphere().getSupraMemberByLoginName( getUserSession().getUserLogin() ).getMembersSpheresDisplayNamesWoOwn();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "VA " + XmlDocumentUtils.toPrettyString( this.internalSphereDocument);
	}

	/**
	 * @return
	 */
	public String getAdminLogin() {
		return getSupraSphere().getAdminLogin();
	}

	/**
	 * @return
	 */
	public String getAdminName() {
		return getSupraSphere().getAdminName();
	}

	/**
	 * @param contact
	 * @return
	 */
	public boolean isAdmin(ContactStatement contact) {
		return isAdmin(contact.getContactNameByFirstAndLastNames(), contact.getLogin());
	}
	
	public boolean isMember(final ContactStatement contact) {
		return isMember(contact.getContactNameByFirstAndLastNames());
	}
	
	public boolean isMember(final String contactName) {
		return getSupraSphere().findMemberByContactName(contactName)!=null;
	}

	/**
	 * @return
	 */
	public boolean isPrimaryAdmin() {
		String contactName = (String) this.session
			.get(SessionConstants.REAL_NAME);
		String loginName = (String) this.session.get(SessionConstants.USERNAME);
		return isPrimaryAdmin(contactName, loginName);
	}
	
	public boolean isPrimaryAdmin( String contactName, String loginName ) {
		return getSupraSphere().isPrimaryAdmin(contactName, loginName);
	}
}


