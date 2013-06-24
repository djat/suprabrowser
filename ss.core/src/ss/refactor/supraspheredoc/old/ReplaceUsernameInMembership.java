package ss.refactor.supraspheredoc.old;

import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.common.XmlDocumentUtils;
import ss.domainmodel.LoginSphere;
import ss.server.db.XMLDB;
import ss.server.domain.service.IReplaceUsernameInMembership;
import ss.server.networking.DialogsMainPeer;
import ss.util.NameTranslation;

public class ReplaceUsernameInMembership extends AbstractSsDocFeature implements IReplaceUsernameInMembership {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ReplaceUsernameInMembership.class);
	
	private static final String VALUE = "value";

	private static final String LOGIN = "login";

	private static final String SYSTEM_NAME = "system_name";
	
	/**
	 * @param session
	 * @param oldUsername
	 * @param newUsername
	 * @param newSalt
	 * @param newVerifier
	 * @param username
	 * @param supraSphere
	 * @param sSession
	 * @param loginSphere
	 */
	public void replaceUsernameInMembership(Hashtable session,
			String oldUsername, String newUsername, String newSalt,
			String newVerifier, String username, String supraSphere,
			String sSession, LoginSphere loginSphere) {
		logger.warn("existing username was false or same;");
		Document supraSphereDoc = getUtils()
				.replaceUsernameInSupraSphereDoc(supraSphere, oldUsername,
						newUsername);

		// logger.info("CHANGED SUPRASPHEREODC:
		// "+supraSphereDoc.asXML());
		this.peer.getVerifyAuth().setSphereDocument(supraSphereDoc);

		final Document currentContactDocument = this.peer.getXmldb()
				.getContactDoc(loginSphere.getSystemName(),
						username);
		if (currentContactDocument != null) {
			this.peer.getVerifyAuth().setContactDocument(
					currentContactDocument);

			sendUpdateVerify(supraSphereDoc, sSession);

			Document membershipDoc = this.peer.getXmldb().getUtils()
					.replaceUsernameInMembership(
							loginSphere.getSystemName(),
							oldUsername, newUsername, newSalt, newVerifier);
			this.peer.getXmldb().replaceDoc(membershipDoc,
					loginSphere.getSystemName());

			Document contactDoc = this.peer.getXmldb().getContactDoc(
					loginSphere.getSystemName(), oldUsername);

			contactDoc.getRootElement().element(LOGIN).addAttribute(VALUE,
					newUsername);

			this.peer.getXmldb().replaceDoc(contactDoc,
					loginSphere.getSystemName());

			this.peer.replaceAndUpdateAllLocations(session, contactDoc);
		}
	}

	
	/**
	 * @param xmldb
	 * @param loginSphere
	 * @param session
	 * @param oldUsername
	 * @param newUsername
	 * @param newSalt
	 * @param newVerifier
	 * @param cont
	 */
	public void replaceUserNameInMembership2(XMLDB xmldb,
			LoginSphere loginSphere, final Hashtable session,
			String oldUsername, String newUsername, String newSalt,
			String newVerifier, DialogsMainPeer cont) {
		logger.info("OLD USERNAME: " + oldUsername
				+ " new: " + newUsername + " : "
				+ newSalt + " : " + newVerifier);

		Document contactDoc = xmldb
				.getContactDoc(
						loginSphere.getSystemName(),
						oldUsername);

		// Document supraSphereDoc =
		// xmldb.getSphereDefinition((String)session.get("supra_sphere"),(String)session.get("supra_sphere"));

		Document supraSphereDoc = getUtils()
				.replaceUsernameInSupraSphereDoc(
						(String) session
								.get("supra_sphere"),
						oldUsername, newUsername);

		String contactName = NameTranslation
				.createContactNameFromContactDoc(contactDoc);

		if (cont.getVerifyAuth().isAdmin(
				contactName, oldUsername)) {
			String apath = "//suprasphere/admin/supra[@contact_name=\""
					+ contactName
					+ "\" and @login_name=\""
					+ oldUsername + "\"]";

			Element element = (Element) supraSphereDoc
					.selectObject(apath);
			element.addAttribute("login_name",
					newUsername);

		}
		// supraSphereDoc =
		// xmldb.replaceDoc(supraSphereDoc,(String)session.get("supra_sphere"));

		session.put("username", newUsername);
		Document membershipDoc = xmldb
				.getUtils()
				.replaceUsernameInMembership(
						loginSphere.getSystemName(),
						oldUsername, newUsername,
						newSalt, newVerifier);

		xmldb.replaceDoc(membershipDoc, loginSphere.getSystemName() );

		xmldb.replaceDoc(supraSphereDoc,
				(String) session
						.get("supra_sphere"));

		if ( logger.isDebugEnabled() ) {
			logger.debug("CHANGED SUPRASPHEREODC: "
					+ XmlDocumentUtils.toPrettyString( supraSphereDoc) );
		}

		contactDoc.getRootElement()
				.element("login").addAttribute(
						"value", newUsername);

		xmldb.replaceDoc(contactDoc, loginSphere.getSystemName());

		cont.getVerifyAuth().setContactDocument(
				contactDoc);
		cont.sendUpdateVerify(supraSphereDoc,
				(String) session.get("session"));
	}
	
}
