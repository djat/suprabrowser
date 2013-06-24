package ss.refactor.supraspheredoc.old;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.common.SphereReferenceList;
import ss.common.XmlDocumentUtils;
import ss.common.domain.service.ISupraSphereFacade;
import ss.common.domain.service.SupraSphereFacade;
import ss.domainmodel.MemberReference;
import ss.domainmodel.SphereEmailCollection;
import ss.domainmodel.SphereItem;
import ss.domainmodel.SphereReference;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.SupraSphereMemberCollection;
import ss.domainmodel.SupraSphereStatement;
import ss.framework.entities.xmlentities.XmlEntityUtils;
import ss.global.SSLogger;

public class SsDocSurpaSphereFacade extends SupraSphereFacade {

	@SuppressWarnings("unused")
	protected final org.apache.log4j.Logger logger = SSLogger
			.getLogger(getClass());

	private final SupraSphereStatement supraSphere;

	/**
	 * @param supraSphereStatement
	 */
	public SsDocSurpaSphereFacade(SupraSphereStatement supraSphereStatement) {
		this.supraSphere = supraSphereStatement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#duplicate()
	 */
	public ISupraSphereFacade duplicate() {
		Document supraSphereDocument = getSupraSphereDocument();
		SupraSphereStatement statementClone = null;
		if (supraSphereDocument != null) {
			statementClone = SupraSphereStatement
					.wrap((Document) supraSphereDocument.clone());
		}
		return new SsDocSurpaSphereFacade(statementClone);
	}

	/**
	 * @return
	 */
	private SupraSphereStatement getSupraSphere() {
		return this.supraSphere;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#findMemberByLogin(java.lang.String)
	 */
	public SupraSphereMember findMemberByLogin(String memberLogin) {
		return getSupraSphere().getSupraMembers()
				.findMemberByLogin(memberLogin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getAllAvailablePrivateSpheres(java.lang.String)
	 */
	public List<SphereReference> getAllAvailablePrivateSpheres(String login) {
		final String xpath = "//suprasphere/member[@login_name=\"" + login
				+ "\"]/sphere[@sphere_type=\"member\" and @enabled=\"true\"]";
		List<Element> elements = XmlDocumentUtils.selectElementListByXPath(this
				.getSupraSphereDocument(), xpath);
		return XmlEntityUtils.wrapList(elements, SphereReference.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getAllEnabledSpheresByContactName(java.lang.String)
	 */
	public SphereReferenceList getAllEnabledSpheresByContactName(String contact) {
		final String xpath = "//suprasphere/member[@contact_name=\"" + contact
				+ "\"]/sphere[@enabled='true']";
		return getAllSpheresByXPath(xpath);
	}

	/**
	 * @param xpath
	 * @return
	 */
	private SphereReferenceList getAllSpheresByXPath(String xpath) {
		if (this.logger.isDebugEnabled() ) {
			this.logger.debug( "getAllSpheresByXPath by " + xpath + " in " + XmlDocumentUtils.toPrettyString( getSupraSphereDocument() ) );			
		}
		List<SphereReference> references = new ArrayList<SphereReference>();
		for (Element elem : XmlDocumentUtils.selectElementListByXPath(this
				.getSupraSphereDocument(), xpath)) {
			references.add(SphereReference.wrap(elem));
		}
		return new SphereReferenceList( references );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getAllEnabledSpheresByLogin(java.lang.String)
	 */
	public SphereReferenceList getAllEnabledSpheresByLogin(String login) {
		final String xpath = "//suprasphere/member[@login_name=\"" + login
				+ "\"]/sphere[@enabled='true']";
		return getAllSpheresByXPath(xpath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getAllMembers()
	 */
	public List<SupraSphereMember> getAllMembers() {
		final String xpath = "//suprasphere/member";
		List<Element> elements = XmlDocumentUtils.selectElementListByXPath(this
				.getSupraSphereDocument(), xpath);
		return XmlEntityUtils.wrapList(elements, SupraSphereMember.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getAllSpheres()
	 */
	public SphereReferenceList getAllSpheres() {
    	SphereReferenceList result = null;
		for ( SupraSphereMember member : getAllMembers() ){
        	SphereReferenceList next = getAllSpheresByXPath( 
        			"//suprasphere/member[@login_name=\"" + member.getLoginName()
        			+ "\"]/sphere" );
        	if ( next != null ) {
        		if (result == null) {
        			result = next;
        		} else {
        			result.addAll( next );
        		}
        	}
        }
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getAvailableGroupSpheres(java.lang.String)
	 */
	public Vector<String> getAvailableGroupSpheres(String contactName) {
		Vector<String> result = new Vector<String>();
		String apath = "//suprasphere/member[@contact_name=\"" + contactName
				+ "\"]/sphere[@sphere_type='group' and @enabled='true']";
		try {
			Element elem = (Element) getSupraSphereDocument().selectObject(
					apath);

			if (elem == null) {
				this.logger.debug("null element in getavailsphere");
			}

			// logger.debug("NAME: "+elem.attributeValue("name"));
			result.add(elem.attributeValue("display_name"));

		} catch (ClassCastException npe) {

			// authentic_pers = new
			// Vector(((ArrayList)doc.selectObject(appath)))

			List real = (List) getSupraSphereDocument().selectObject(apath);

			for (int i = 0; i < real.size(); i++) {

				Element one = (Element) real.get(i);

				// logger.debug("NAME:
				// "+one.attributeValue("display_name"));
				result.add(one.attributeValue("display_name"));

			}

		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getAvailableGroupSpheresId()
	 */
	public List<String> getAvailableGroupSpheresId(String contactName) {
		List<String> result = new Vector<String>();

		String apath = "//suprasphere/member[@contact_name=\"" + contactName
				+ "\"]/sphere[@sphere_type='group' and @enabled='true']";
		try {
			Element elem = (Element) getSupraSphereDocument().selectObject(
					apath);

			if (elem == null) {
				this.logger.debug("null element in getavailsphere");
			}
			result.add(elem.attributeValue("system_name"));
		} catch (ClassCastException npe) {
			List real = (List) getSupraSphereDocument().selectObject(apath);
			for (int i = 0; i < real.size(); i++) {
				Element one = (Element) real.get(i);
				result.add(one.attributeValue("system_name"));
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getAvailableSpheres(java.lang.String)
	 */
	public Vector<String> getAvailableSpheres(String contactName) {

		Vector<String> result = new Vector<String>();

		String apath = "//suprasphere/member[@contact_name=\"" + contactName
				+ "\"]/sphere[@enabled='true']";
		try {
			Element elem = (Element) getSupraSphereDocument().selectObject(
					apath);
			if (elem == null) {
				this.logger.debug("null element in getavailsphere");
			}
			result.add(elem.attributeValue("system_name"));
		} catch (ClassCastException npe) {
			List<Element> real = (List<Element>) getSupraSphereDocument()
					.selectObject(apath);
			for (int i = 0; i < real.size(); i++) {
				Element one = (Element) real.get(i);
				result.add(one.attributeValue("system_name"));
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getBindedDocumentForSaveToDb()
	 */
	public Document getBindedDocumentForSaveToDb() {
		return getSupraSphereDocument();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getContactNameByLogin(java.lang.String)
	 */
	public String getContactNameByLogin(String loginName) {
		final String apath = "//suprasphere/member[@login_name=\"" + loginName
				+ "\"]";
		try {
			Element elem = (Element) this.getSupraSphereDocument()
					.selectObject(apath);
			return elem.attributeValue("contact_name");

		} catch (Exception e) {
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getContactsForMemberEnabled(java.lang.String)
	 */
	public Vector<String> getContactsForMemberEnabled(String contactName) {
		final String apath = "//suprasphere/member/sphere[@display_name=\""
				+ contactName + "\" and @enabled='true']";
		Vector<String> enabledMembers = new Vector<String>();
		for (Element elem : XmlDocumentUtils.selectElementListByXPath(
				getSupraSphereDocument(), apath)) {
			Element member = elem.getParent();
			enabledMembers.add(member.attributeValue("contact_name"));
		}
		this.logger.info("Enabled size returning: " + enabledMembers.size());
		return enabledMembers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getDisplayNameWithoutRealName(java.lang.String)
	 */
	public String getDisplayNameWithoutRealName(String system_name) {
		SupraSphereMemberCollection members = this.supraSphere.getSupraMembers();
		if ( members == null ) {
			return null;
		}
		for (SupraSphereMember member : members) {
			SphereItem item = member.getSphereBySystemName(system_name);
			if ( item != null ) {
				return item.getDisplayName();
			}
		}
		if (true) {
			return null;
		}
		String display_name = null;
		String apath = "//suprasphere/member/sphere[@system_name=\""
				+ system_name + "\"]";
		// String apath = "//sphere";
		this.logger.debug("apath in getDisplayNameworealanme:" + apath);
		// display_name =
		// getSphereDocument().getRootElement().attributeValue("display_name");
		try {
			Element elem = (Element) this.getSupraSphereDocument()
					.selectObject(apath);
			if (elem == null) {
				this.logger.debug("null element in getsystemname");
			}
			display_name = elem.attributeValue("display_name");
		} catch (ClassCastException npe) {
			Vector results = new Vector((List) this.getSupraSphereDocument()
					.selectObject(apath));
			for (int i = 0; i < results.size(); i++) {
				Element elem = (Element) results.get(i);
				display_name = elem.attributeValue("display_name");
			}
		}
		this.logger.debug("Returing disp name: " + display_name);
		return display_name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getDomains()
	 */
	public String getDomains() {
		return SupraSphereStatement.wrap(getSupraSphereDocument())
				.getSphereDomain();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getInformationForDump()
	 */
	public String getInformationForDump() {
		return getSupraSphere().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getLoginForContact(java.lang.String)
	 */
	public String getLoginForContact(String contactName) {
		String apath = "//suprasphere/member[@contact_name=\"" + contactName
				+ "\"]";
		try {
			Element elem = XmlDocumentUtils.selectElementByXPath(this
					.getSupraSphereDocument(), apath);
			return elem != null ? elem.attributeValue("login_name") : null;
		} catch (Exception e) {
			this.logger.error(e.getMessage(), e);
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getLoginSphere(java.lang.String)
	 */
	public String getLoginSphere(String loginName) {
		String apath = "//suprasphere/member[@login_name=\"" + loginName
				+ "\"]/login_sphere";
		this.logger.debug("APATH IN GETLOGINSPHERE: " + apath);
		try {
			Element elem = (Element) this.getSupraSphereDocument()
					.selectObject(apath);
			if (elem == null) {
				// logger.debug("null element in
				// getavailsphere");
			} else {
				// logger.debug("not null element in
				// getavailsphere");
				return elem.attributeValue("system_name");
			}
			// string = elem.attributeValue("system_name");
		} catch (ClassCastException cce) {
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getLoginsForMemberEnablder(java.lang.String)
	 */
	public Vector<String> getLoginsForMemberEnabled(String sphereId) {
		final String apath = "//suprasphere/member/sphere[@system_name='"
				+ sphereId + "' and @enabled='true']";
		Vector<String> enabledMembers = new Vector<String>();
		for (Element elem : XmlDocumentUtils.selectElementListByXPath(
				getSupraSphereDocument(), apath)) {
			Element member = elem.getParent();
			enabledMembers.add(member.attributeValue("login_name"));
		}
		this.logger.info("Enabled size returning: " + enabledMembers.size());
		return enabledMembers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getMembersFor(java.lang.String)
	 */
	public Vector<String> getMembersFor(String contact_name) {
		Vector<String> result = new Vector<String>();
		String apath = "//suprasphere/member[@contact_name=\"" + contact_name
				+ "\"]/sphere[@sphere_type=\"member\"]";
		try {
			Element elem = (Element) getSupraSphereDocument().selectObject(
					apath);
			if (elem == null) {
				this.logger.debug("null element in getavailsphere");
			}
			// logger.debug("NAME: "+elem.attributeValue("name"));
			result.add(elem.attributeValue("display_name"));
		} catch (ClassCastException npe) {
			List real = (List) getSupraSphereDocument().selectObject(apath);
			for (int i = 0; i < real.size(); i++) {
				Element one = (Element) real.get(i);
				result.add(one.attributeValue("display_name"));
			}
		}
		this.logger.debug("returning result of size: " + result.size());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getMembersForSphere(java.lang.String)
	 */
	public List<MemberReference> getMembersForSphere(String sphereId) {
		final String xpath = "//suprasphere/member[sphere[@system_name = \""
				+ sphereId + "\" and @enabled=\"true\"]]";
		List<Element> elements = XmlDocumentUtils.selectElementListByXPath(this
				.getSupraSphereDocument(), xpath);
		return XmlEntityUtils.wrapList(elements, MemberReference.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getName()
	 */
	public String getName() {
		return getSupraSphere().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getPersonalSphereFromLogin(java.lang.String)
	 */
	public String getPersonalSphereFromLogin(String loginName) {
		final String realName = getContactNameByLogin(loginName);
		final String apath = "//suprasphere/member[@login_name=\"" + loginName
				+ "\"]/sphere[@display_name=\"" + realName + "\"]";
		try {
			Element elem = (Element) this.getSupraSphereDocument()
					.selectObject(apath);
			return elem.attributeValue("system_name");

		} catch (ClassCastException exc) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getPrivateForSomeoneElse(java.lang.String)
	 */
	public String getPrivateForSomeoneElse(String contactName) {

		final String apath = "//suprasphere/member[@contact_name=\""
				+ contactName + "\"]/sphere[@display_name=\"" + contactName
				+ "\"]";
		try {
			Element elem = (Element) this.getSupraSphereDocument()
					.selectObject(apath);
			return elem.attributeValue("system_name");
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getSharedSphereIdForContactPair(java.lang.String,
	 *      java.lang.String)
	 */
	public String getSharedSphereIdForContactPair(String firstContact,
			String secondContact) {
		try {
			String apath = "//suprasphere/member[@contact_name=\""
					+ firstContact + "\"]/sphere[@display_name=\""
					+ secondContact + "\"]";
			Element elem = (Element) this.getSupraSphereDocument()
					.selectObject(apath);
			return elem.attributeValue("system_name");
		} catch (ClassCastException cce) {
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getSphereCoreDisplayNameFor(java.lang.String)
	 */
	public String getSphereCoreDisplayNameFor(String contactName) {
		String display_name = null;
		String apath = "//suprasphere/member[@contact_name=\"" + contactName
				+ "\"]/sphere_core";
		// String apath = "//sphere";
		// display_name =
		// getSphereDocument().getRootElement().attributeValue("display_name");

		try {
			Element elem = (Element) this.getSupraSphereDocument()
					.selectObject(apath);

			display_name = elem.attributeValue("display_name");

		} catch (ClassCastException npe) {
		}
		return display_name;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getSphereCoreId()
	 */
	public String getSphereCoreId(String contactName) {
		String id = null;

		String apath = "//suprasphere/member[@contact_name=\"" + contactName
				+ "\"]/sphere_core";
		try {
			Element elem = (Element) this.getSupraSphereDocument()
					.selectObject(apath);

			id = elem.attributeValue("system_name");

		} catch (ClassCastException npe) {
			this.logger.debug("problem right here now");
		}
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getSphereDisplayName(java.lang.String,
	 *      java.lang.String)
	 */
	public String getSphereDisplayName(String contactName, String system_name) {
		String display_name = null;

		this.logger.info("real name : " + contactName);
		String apath = "//suprasphere/member[@contact_name=\"" + contactName
				+ "\"]/sphere[@system_name=\"" + system_name + "\"]";
		// String apath = "//sphere";

		// Logger logger = Logger.getLogger("SS");
		// logger.info("apth: "+apath);
		// logger.debug("apath in getDisplayName:" + apath);

		// display_name =
		// getSphereDocument().getRootElement().attributeValue("display_name");

		try {
			Element elem = (Element) this.getSupraSphereDocument()
					.selectObject(apath);

			display_name = elem.attributeValue("display_name");

		} catch (ClassCastException npe) {

			return null;
		}
		return display_name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getSphereSystemNameByContactAndDisplayName(java.lang.String,
	 *      java.lang.String)
	 */
	public String getSphereSystemNameByContactAndDisplayName(
			String display_name, String contact_name) {
		return XmlDocumentUtils.selectAttibuteValueByXPath(
				getSupraSphereDocument(),
				"//suprasphere/member[@contact_name=\"" + contact_name
						+ "\"]/sphere[@display_name=\"" + display_name
						+ "\"]/@system_name");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getSphereType(java.lang.String)
	 */
	public String getSphereType(String system_name, String contactName) {
		String apath = "//suprasphere/member[@contact_name=\"" + contactName
				+ "\"]/sphere[@system_name=\"" + system_name + "\"]";
		this.logger.debug("apath in getspheretype:" + apath);
		try {
			Element elem = (Element) this.getSupraSphereDocument()
					.selectObject(apath);
			return elem.attributeValue("sphere_type");
		} catch (ClassCastException npe) {
			return "member";
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getSpheresEmails()
	 */
	public SphereEmailCollection getSpheresEmails() {
		return getSupraSphere().getSpheresEmails();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getSupraMemberByLoginName(java.lang.String)
	 */
	public SupraSphereMember getSupraMemberByLoginName(String userLogin) {
		return getSupraSphere().getSupraMemberByLoginName(userLogin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getSupraSphereName()
	 */
	public String getSupraSphereName() {
		return this.getSupraSphereDocument().getRootElement().attributeValue(
				"name");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getSystemName()
	 */
	public String getSystemName() {
		return getSupraSphere().getSystemName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#getSystemName(java.lang.String,
	 *      java.lang.String)
	 */
	public String getSystemName(String display_name, String contact_name) {
		String apath = "//suprasphere/member[@contact_name=\""
				+ contact_name
				+ "\"]/sphere[@display_name=\"" + display_name + "\"]";
		final String system_name = selectSystemName(apath);
		if (system_name != null) {
			return system_name;
		}

		this.logger.info("SYStem name was null....try again");
		apath = "//suprasphere/member[@contact_name=\""
				+ contact_name
				+ "\"]/sphere[@display_name=\"" + display_name
				+ "\" and @sphere_type=\"member\"]";
		this.logger.info("xpath: " + apath);
		return selectSystemName(apath);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#isAdmin(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean isAdmin(String contactName, String loginName) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Checking admin for : " + contactName + " : "
					+ loginName);
		}
		return getSupraSphere().getAdmins().isAdmin(loginName, contactName);
	}
	
	public String getAdminName() {
		return getSupraSphere().getAdmins().getPrimaryAdmin().getContact();
	}
	
	public String getAdminLogin() {
		return getSupraSphere().getAdmins().getPrimaryAdmin().getLogin();	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#isPeronal(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public boolean isPersonal(String checkSphereId, String loginName,
			String contactName) {
		String apath = "//suprasphere/member[@login_name=\"" + loginName
				+ "\"]/sphere[@display_name=\"" + contactName + "\"]";

		try {
			Element elem = (Element) this.getSupraSphereDocument()
					.selectObject(apath);
			if (elem == null) {

			} else {
			}
			String sphereId = elem.attributeValue("system_name");
			if (checkSphereId.equals(sphereId)) {
				this.logger.debug("ITS TRUE...");
				return true;

			} else {
				this.logger.debug("RETURNING FALSE: " + checkSphereId + " :"
						+ apath);
				return false;
			}

		} catch (ClassCastException cce) {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#isSphereEnabled(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean isSphereEnabledForContact(String displayName,
			String contactName) {
		final String apath = "//suprasphere/member[@contact_name=\""
				+ contactName + "\"]/sphere[@display_name=\"" + displayName
				+ "\"]";
		final String enabled = XmlDocumentUtils.selectAttibuteValueByXPath(this
				.getSupraSphereDocument(), apath);
		return enabled != null && enabled.equalsIgnoreCase("true");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#isSphereEnabledForMember(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean isSphereEnabledForMember(String sphereId, String memberLogin) {
		final String xpath = "//suprasphere/member[@login_name=\""
				+ memberLogin + "\"]/sphere[@system_name=\"" + sphereId
				+ "\" and @enabled=\"true\"]";
		return XmlDocumentUtils.selectElementListByXPath(
				this.getSupraSphereDocument(), xpath).size() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#isSphereExists(java.lang.String)
	 */
	public boolean isSphereExists(String display_name, String contactName) {
		String apath = "//suprasphere/member[@contact_name=\"" + contactName
				+ "\"]/sphere[@display_name=\"" + display_name + "\"]";
		final String system_name = selectSystemName(apath);
		if (system_name != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param apath
	 * @return
	 */
	private String selectSystemName(String xpath) {
		Element elem = XmlDocumentUtils.selectElementByXPath(this
				.getSupraSphereDocument(), xpath);
		return elem != null ? elem.attributeValue("system_name") : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#isSpherePersonal(ss.domainmodel.SphereStatement)
	 */
	public boolean isSpherePersonal(SphereStatement sphere) {
		String systemName = sphere.getSystemName();
		String xpath = "//suprasphere/member/sphere[@system_name=\""
				+ systemName + "\" and @sphere_type=\"member\"]";
		return XmlDocumentUtils.selectElementListByXPath(
				this.getSupraSphereDocument(), xpath).size() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.common.ISupraSphereFacade#isUserExists(java.lang.String)
	 */
	public boolean isUserExists(String login) {
		final String xpath = "//suprasphere/member[@login_name=\"" + login
				+ "\"]";
		List<Element> elements = XmlDocumentUtils.selectElementListByXPath(this
				.getSupraSphereDocument(), xpath);
		return elements.size() > 0;
	}

	/**
	 * @return
	 */
	private Document getSupraSphereDocument() {
		return this.supraSphere != null ? this.supraSphere.getBindedDocument()
				: null;
	}

	/* (non-Javadoc)
	 * @see ss.common.ISupraSphereFacade#findMemberByContactName(java.lang.String)
	 */
	public SupraSphereMember findMemberByContactName(String contactName) {
		String memberLogin = getLoginForContact(contactName);
		return findMemberByLogin(memberLogin);
	}

	/* (non-Javadoc)
	 * @see ss.common.ISupraSphereFacade#getP2PSphere(java.lang.String, java.lang.String)
	 */
	public String getP2PSphere(String login, String peerContactName) {
		String sphereId = null;
		Document doc = null;

		try {
			doc = getSupraSphereDocument();

			String apath = "//suprasphere/member[@login_name=\"" + login
					+ "\"]/sphere[@display_name=\"" + peerContactName + "\"]";

			Element elem = (Element) doc.selectObject(apath);

			sphereId = elem.attributeValue("system_name");
		} catch (ClassCastException exc) {
			logger.error("ClassCast Exception in getPersonalSphere", exc);
		}
		return sphereId;
	}

	/* (non-Javadoc)
	 * @see ss.common.domain.service.ISupraSphereFacade#isPrimaryAdmin(java.lang.String, java.lang.String)
	 */
	public boolean isPrimaryAdmin(String contactName, String loginName) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug("Checking primary admin for : " + contactName + " : "
					+ loginName);
		}
		return getSupraSphere().getAdmins().isPrimaryAdmin(loginName, contactName);
	}

}
