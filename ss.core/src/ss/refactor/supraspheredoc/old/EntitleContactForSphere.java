/**
 * 
 */
package ss.refactor.supraspheredoc.old;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import ss.common.TimeLogWriter;
import ss.server.db.XMLDB;
import ss.server.domain.service.IEntitleContactForSphere;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.util.NameTranslation;
import ss.util.XMLSchemaTransform;

/**
 *
 */
public class EntitleContactForSphere extends AbstractSsDocFeature implements IEntitleContactForSphere {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EntitleContactForSphere.class);

	private static final String GROUP = "group";

	private static final String MEMBER = "member";

	private static final String VALUE = "value";

	private static final String LOGIN = "login";
	
	/**
	 * @param session
	 * @param inviteSphereType
	 * @param contactDoc
	 * @param tableId
	 * @param sphereId
	 * @param xmldb
	 */
	public void entitleContactForSphere(Hashtable session,
			String inviteSphereType, Document contactDoc, String tableId,
			String sphereId) {
		try {
			// Will assume contact has already been registered
			// for
			// now....
			if (contactDoc == null) {
				this.logger.warn("contact doc null: ");
			}
			String loginName = contactDoc.getRootElement().element(LOGIN)
					.attributeValue(VALUE);

			String sphereName = this.peer.getVerifyAuth().getDisplayName(
					sphereId);
			String contactName = NameTranslation
					.createContactNameFromContactDoc(contactDoc);

			this.logger.info("Will entitle for group sphere");

			Vector enabledMembers = this.peer.getVerifyAuth()
					.getLoginsForMembersEnabled1(sphereId);

			Vector enabledMemberDocs = new Vector();
			for (int i = 0; i < enabledMembers.size(); i++) {

				String member = (String) enabledMembers.get(i);

				this.logger.info("checking member: " + member);
				String loginSphere = xmldb.getUtils().getLoginSphereSystemName(member);
				this.logger.info("their login sphere: " + loginSphere);
				Document doc = xmldb.getContactDoc(loginSphere, member);
				this.logger.info("their contact docs; " + doc.asXML());
				if (doc != null) {
					if (inviteSphereType.equals(MEMBER)) {
						getUtils().entitleContactForMemberSphere(member,
								NameTranslation
										.createContactNameFromContactDoc(doc),
								contactName, tableId);
					} else if (inviteSphereType.equals(GROUP)) {
						enabledMemberDocs.add(doc);
					}
				}
			}
			if (inviteSphereType.equals(GROUP)) {
				this.entitleContactForGroupSphere(session,
						contactDoc, contactName, loginName, sphereId,
						sphereName, enabledMemberDocs, xmldb);
			}

			String entitledSphereId = sphereId;

			
			for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE.getHandlers()) {
				Document supraSphereDoc = getUtils().getSupraSphereDocument();
				handler.sendUpdateVerifyToAll(supraSphereDoc);
			}

			DialogsMainPeer.sendForAllRefreshPresence( contactName,	sphereId );
			final String sphereId1 = sphereId;
			
			if (inviteSphereType.equals(GROUP)) {

				String personalSphere = this.peer.getXmldb().getUtils().getPersonalSphere(
						contactDoc.getRootElement().element(LOGIN)
								.attributeValue(VALUE), contactName);

				this.logger
						.warn("HERE was the personal sphere of the contact being entitled: "
								+ personalSphere);

				Document sphereDoc = this.peer.getXmldb()
						.getRegularSphereDocument(sphereId1, entitledSphereId);

				if (sphereDoc == null) {

					this.logger.warn("This : " + entitledSphereId
							+ " was not found here: " + sphereId1);

				}

				this.logger.warn("Committing to their personal sphere: "
						+ personalSphere);

				this.peer.getXmldb().insertDoc(sphereDoc, personalSphere);
				for (DialogsMainPeer handler : DialogsMainPeerManager.INSTANCE
						.getHandlers()) {
					String apath1 = "//suprasphere/member[@login_name=\""
							+ contactDoc.getRootElement().element(LOGIN)
									.attributeValue(VALUE)
							+ "\"]/sphere[@system_name='" + personalSphere
							+ "' and @enabled='true']";
					// TODO ????
				}
			}
		} catch (DocumentException exc) {
			this.logger.error("Document Exception", exc);
		}
	}
	
	@SuppressWarnings( { "deprecation", "unchecked" })
	public Document entitleContactForGroupSphere(Hashtable session,
			Document contactBeingRegistered, String contactName,
			String loginName, String systemName, String displayName,
			Vector enabledMemberDocs, XMLDB xmldb) throws DocumentException {

		final TimeLogWriter timeLogWriter = new TimeLogWriter(
				DialogsMainPeer.class, "entitleContactForGroupSphere method");

		// Document returnDoc = null;
		Document supraSphereDoc = getSupraSphereDocument();

		String apath = "//suprasphere/member[@contact_name=\"" + contactName
				+ "\"]/sphere[@system_name=\"" + systemName + "\"]";

		Element cname_elm = null;
		try {
			cname_elm = (Element) supraSphereDoc.selectObject(apath);
			cname_elm.addAttribute("enabled", "true");
			supraSphereDoc = xmldb.replaceDoc(supraSphereDoc, (String) session
					.get("supra_sphere"));

			xmldb.getUtils().addMemberToSphereDefinition(
					(String) session.get("sphere_id"), contactName, loginName,
					systemName, displayName);

			supraSphereDoc = getSupraSphereDocument();

			timeLogWriter
					.logAndRefresh("addMemberToSphereDefinition performed");

			// For the group, this will add that member to that
			// sphere,
			// particularly systemname

			String tableId = new Long(xmldb.getNextTableId()).toString();
			supraSphereDoc = getUtils()
					.entitleContactForMemberSphereLight(loginName, contactName,
							contactName, tableId, supraSphereDoc); // Create
			// the
			// personal
			// sphere for
			timeLogWriter
					.logAndRefresh("entitleContactForMemberSphere performed, enabledMemberDocs.size: "
							+ enabledMemberDocs.size());

			for (int i = 0; i < enabledMemberDocs.size(); i++) {
				Document existingMember = (Document) enabledMemberDocs.get(i);

				String existLoginName = existingMember.getRootElement()
						.element("login").attributeValue("value");

				String existContactName = NameTranslation
						.createContactNameFromContactDoc(existingMember);

				tableId = new Long(xmldb.getNextTableId()).toString();

				supraSphereDoc = getUtils()
						.entitleContactForMemberSphereLight(existLoginName,
								existContactName, contactName, tableId,
								supraSphereDoc);

				existingMember = XMLSchemaTransform.addLocationToDoc(
						existingMember, existingMember, (String) session
								.get("sphereURL"), null, null, tableId,
						contactName);

				xmldb.insertDoc(existingMember, tableId);

				Vector list = new Vector(existingMember.getRootElement()
						.element("locations").elements());

				for (int j = 0; j < list.size(); j++) {

					Element oneElem = (Element) list.get(j);

					String systemElem = oneElem.attributeValue("ex_system");

					xmldb.replaceDoc(existingMember, systemElem);
				}

				contactBeingRegistered = XMLSchemaTransform.addLocationToDoc(
						contactBeingRegistered, contactBeingRegistered,
						(String) session.get("sphereURL"), null, null, tableId,
						contactName);

				xmldb.insertDoc(contactBeingRegistered, tableId);

				list = new Vector(contactBeingRegistered.getRootElement()
						.element("locations").elements());

				// for (int j = 0; j < list.size(); j++) {
				//
				// Element oneElem = (Element) list.get(j);
				//
				// /* #NOT_USED String systemElem = */oneElem
				// .attributeValue("ex_system");
				// }

				this.peer.replaceAndUpdateAllLocations(session, contactBeingRegistered);

				supraSphereDoc = getUtils()
						.entitleContactForMemberSphereLight(loginName,
								contactName, existContactName, tableId,
								supraSphereDoc);
			}
			supraSphereDoc = xmldb.replaceDoc(supraSphereDoc,
					getSupraSphereSystemId() );
			timeLogWriter.logTime("entitleContactForGroupSphere finished");
		} catch (ClassCastException ex) {
			logger.error("ClassCastException in entitleContactForGroupSphere",
					ex);
		}
		return supraSphereDoc;
	}


	

	
}
