package ss.refactor.supraspheredoc.old;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import ss.common.XmlDocumentUtils;
import ss.domainmodel.SupraSphereStatement;
import ss.refactor.Refactoring;
import ss.refactor.supraspheredoc.SupraSphereRefactor;
import ss.server.db.XMLDB;
import ss.server.db.suprasphere.SupraSphereSingleton;
import ss.server.networking.DialogsMainPeer;
import ss.util.VariousUtils;

public class Utils {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(Utils.class);

	private XMLDB xmldb;

	/**
	 * @param xmldb
	 */
	public Utils(XMLDB xmldb) {
		super();
		if ( xmldb == null ) {
			throw new NullPointerException( "xmldb" );
		}
		this.xmldb = xmldb;
	}

	public Document entitleContactForMemberSphere(String loginName,
			String contactName, String newContactName, String tableId)
			throws DocumentException {

		Document returnDoc = null;
		Document supraSphereDoc;

		supraSphereDoc = getSupraSphereDocumentImpl();

		String apath = "//suprasphere/member[@contact_name=\"" + contactName
				+ "\"][@login_name=\"" + loginName
				+ "\"]/sphere[@display_name=\"" + newContactName + "\"]";

		Element cname_elm = null;
		try {
			cname_elm = (Element) supraSphereDoc.selectObject(apath);
			cname_elm.addAttribute("enabled", "true");
			returnDoc = replaceDoc(supraSphereDoc, (String) getSession().get(
					"supra_sphere"));
		} catch (ClassCastException exc) {
			logger.error("ClassCastException in entitleContactForMemberSphere",
					exc);

			apath = "//suprasphere/member[@contact_name=\"" + contactName
					+ "\"][@login_name=\"" + loginName + "\"]";

			Element contactElem = null;
			try {
				contactElem = (Element) supraSphereDoc.selectObject(apath);

				contactElem.addElement("sphere").addAttribute("display_name",
						newContactName).addAttribute("system_name", tableId)
						.addAttribute("sphere_type", "member").addAttribute(
								"default_delivery", "false").addAttribute(
								"enabled", "true");

				returnDoc = replaceDoc(supraSphereDoc, (String) getSession()
						.get("supra_sphere"));
			} catch (ClassCastException exc2) {
				logger
						.error(
								"second ClassCastException in entitleContactForMemberSphere",
								exc2);
			}
		}

		return returnDoc;

	}

	/**
	 * Description of the Method
	 * 
	 * @param cname
	 *            Description of the Parameter
	 * @param lname
	 *            Description of the Parameter
	 * @param enabledDoc
	 *            Description of the Parameter
	 * @param eSpheres
	 *            Description of the Parameter
	 * @return
	 */
	public Document entitleContactForMemberSphereLight(final String loginName,
			final String contactName, final String newContactName,
			final String tableId, final Document supraSphereDoc) {
		// Document returnDoc = null;

		String apath = "//suprasphere/member[@contact_name=\"" + contactName
				+ "\"][@login_name=\"" + loginName
				+ "\"]/sphere[@display_name=\"" + newContactName + "\"]";

		Element cname_elm = null;
		try {
			cname_elm = (Element) supraSphereDoc.selectObject(apath);
			cname_elm.addAttribute("enabled", "true");
		} catch (ClassCastException exc) {
			logger.error("ClassCastException in entitleContactForMemberSphere",
					exc);

			apath = "//suprasphere/member[@contact_name=\"" + contactName
					+ "\"][@login_name=\"" + loginName + "\"]";

			Element contactElem = null;
			try {
				contactElem = (Element) supraSphereDoc.selectObject(apath);

				contactElem.addElement("sphere").addAttribute("display_name",
						newContactName).addAttribute("system_name", tableId)
						.addAttribute("sphere_type", "member").addAttribute(
								"default_delivery", "false").addAttribute(
								"enabled", "true");
			} catch (ClassCastException exc2) {
				logger
						.error(
								"second ClassCastException in entitleContactForMemberSphere",
								exc2);
			}
		}

		return supraSphereDoc;
	}

	/**
	 * @return
	 */
	private Hashtable getSession() {
		return this.xmldb.getSession();
	}

	/**
	 * @param supraSphereDoc
	 * @param string
	 * @return
	 */
	private Document replaceDoc(Document document, String sphereId) {
		return this.xmldb.replaceDoc(document, sphereId);
	}

	/**
	 * @return
	 */
	private Document getSupraSphereDocumentImpl() {
		try {
			return SupraSphereSingleton.INSTANCE.getDocument();
		} catch (Exception ex) {
			throw new RuntimeException("Can't get suprasphere document", ex);
		}
	}
	
	
	/**
	 * Description of the Method
	 * 
	 * @param members
	 *            Description of the Parameter
	 * @param system_name
	 *            Description of the Parameter
	 * @param display_name
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 * @throws DocumentException
	 */
	@Refactoring(classify=SupraSphereRefactor.class, message="Used only from RegisterSphereWithMembersHandler, that is not in use")
	public synchronized Document registerSphereWithMembers(Vector members,
			String system_name, String display_name) throws DocumentException {
		final Document doc = getSupraSphereDocumentImpl();
		String apath = "//suprasphere/member";

		try {
			Element elem = (Element) doc.selectObject(apath);
			String contact_name = elem.attributeValue("contact_name");

			if (VariousUtils.vectorContains(contact_name, members)) {

				elem.addElement("sphere").addAttribute("display_name",
						display_name).addAttribute("system_name", system_name)
						.addAttribute("default_delivery", "normal")
						.addAttribute("sphere_type", "group").addAttribute(
								"enabled", "true");

			} else {

				elem.addElement("sphere").addAttribute("display_name",
						display_name).addAttribute("system_name", system_name)
						.addAttribute("default_delivery", "normal")
						.addAttribute("sphere_type", "group").addAttribute(
								"enabled", "false");
				;
			}

		} catch (ClassCastException npe) {
			List real = (ArrayList) doc.selectObject(apath);

			for (int i = 0; i < real.size(); i++) {
				Element one = (Element) real.get(i);
				String contact_name = one.attributeValue("contact_name");

				boolean found = false;
				for (int j = 0; j < members.size(); j++) {
					String test = (String) members.get(j);

					if (test.equals(contact_name)) {
						found = true;
					}
				}
				if (found == true) {
					one.addElement("sphere").addAttribute("display_name",
							display_name).addAttribute("system_name",
							system_name).addAttribute("default_delivery",
							"normal").addAttribute("sphere_type", "group")
							.addAttribute("enabled", "true");
				} else {
					one.addElement("sphere").addAttribute("display_name",
							display_name).addAttribute("system_name",
							system_name).addAttribute("default_delivery",
							"normal").addAttribute("sphere_type", "group")
							.addAttribute("enabled", "false");
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Creating new sphere. Suprasphere " + XmlDocumentUtils.toPrettyString( doc ) );
		}
		
		Document returndocument = replaceDoc(doc, (String) getSession().get(
				"supra_sphere"));

		if (logger.isDebugEnabled()) {
			logger.debug("Creating new sphere result. Suprasphere " + XmlDocumentUtils.toPrettyString( returndocument ) );
		}
		return returndocument;
	}
	
	
	public Document replaceUsernameInSupraSphereDoc(String supraSphereName,
			String oldUsername, String newUsername) {
		// This class should really be in "schema transform".
		// Doesn't really do
		// anything.

		logger.warn("starting replaceusernameinspherepspehre");

		Document supraSphereDoc = null;

		try {
			supraSphereDoc = getSupraSphereDocumentImpl();

			String apath = "//suprasphere/member[@login_name=\"" + oldUsername
					+ "\"]";
			logger.warn("try to find apath: " + apath);

			try {
				Element elem = (Element) supraSphereDoc.selectObject(apath);

				if (elem == null) {
					logger.warn("it was not there!!");
					// logger.info("null element in
					// getavailsphere");
				} else {

					// logger.info("not null element in
					// getavailsphere");

					elem.addAttribute("login_name", newUsername);

					logger.warn("NEW USERNAME: " + newUsername);
					logger.warn("ELEM> " + elem.asXML());

				}

			} catch (ClassCastException cce) {
				logger.error( "Can't update user login name for " + oldUsername, cce);

			}

			replaceDoc(supraSphereDoc, supraSphereName);
		} catch (Exception exc) {
			logger.error("Exception", exc);
		}

		return supraSphereDoc;

	}

	/**
	 * @return
	 */
	public Document getSupraSphereDocument() {
		return getSupraSphereDocumentImpl();
	}

	/**
	 * @param xmldb
	 * @return
	 */
	public static Utils getUtils(XMLDB xmldb) {
		return new Utils(xmldb);
	}

	/**
	 * @param peer
	 * @return
	 */
	public static Utils getUtils(DialogsMainPeer peer) {
		XMLDB xmldb = null;
		if (peer != null ) {
			if (peer.getXmldb()!=null) {
				xmldb = peer.getXmldb();
			}
		}
		if ( xmldb == null ) {
			xmldb = new XMLDB();  
		}
		return getUtils(xmldb);
	}

	/**
	 * @return
	 */
	public SupraSphereStatement getSupraSphere() {
		return SupraSphereStatement.wrap(getSupraSphereDocument());
	}

}
