/**
 * 
 */
package ss.server.networking;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;

import ss.common.SSProtocolConstants;
import ss.common.VerifyAuth;
import ss.domainmodel.SphereStatement;

/**
 * @author roman
 *
 */
public class PrivateSphereSender extends AbstractSphereSender {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PrivateSphereSender.class);
	/**
	 * @param dialogMainPeerOwner
	 * @param session
	 * @param sphereDefinition
	 * @param verifyAuth
	 * @param openBackground
	 */
	public PrivateSphereSender(final DialogsMainPeer dialogMainPeerOwner, final Hashtable session, final Document sphereDefinition, final VerifyAuth verifyAuth, final String openBackground) {
		super(dialogMainPeerOwner, session, sphereDefinition, verifyAuth, openBackground);
	}
	
	@Override
	protected Vector<String> generateSurrounding(String sphere_id, DialogsMainPeer handler) {
		final String data_id = handler.getXmldb().getUtils().getInheritedName(
				sphere_id);

		Vector<String> surrounding = new Vector<String>();
		surrounding.add(data_id);
		return surrounding;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void prepareSession() {
		super.prepareSession();
		String sphere_id = SphereStatement.wrap(this.sphereDefinition).getSystemName();
		this.session.put("sphere_id", sphere_id);
	}


	protected void processSupraQuery(DialogsMainPeer handler, final DmpResponse dmpResponse,
			Vector surrounding) {
		String sphere_id = (String) this.session.get(SC.SPHERE_ID);
		Document firstDoc = (Document) this.session.get(SC.FIRST_DOC);
		String realName = (String) this.session.get(SC.REAL_NAME);

		Hashtable<String, Document> noReallyAll = new Hashtable<String, Document>();
		final Vector<String> allOrder = new Vector<String>();

		Vector<String> contactsOnly = new Vector<String>();

		try {
			for (int i = 0; i < surrounding.size(); i++) {
				String dataId = (String) surrounding.get(i);
				logger.info("Adding this one data id....some kind of problem: "
						+ dataId);

				Hashtable<String, Document> reallyAll = getReallyAll(handler,
						this.session, this.sphereDefinition, dataId);

				processReallyAll(dmpResponse, this.sphereDefinition, noReallyAll,
						allOrder, contactsOnly, reallyAll);
			}

			if (firstDoc != null) {
				String messageId = firstDoc.getRootElement().element(
				"message_id").attributeValue("value");
				if (!noReallyAll.containsKey(messageId)) {
					noReallyAll.put(messageId, firstDoc);
					allOrder.add(messageId);
				}
			}

			Vector presenceInfo = new Vector();

			final DmpResponse presenceUpdateDmpResponse = new DmpResponse();
			presenceUpdateDmpResponse.setStringValue(SC.PROTOCOL,
					SSProtocolConstants.REFRESH_PRESENCE);
			presenceUpdateDmpResponse.setStringValue(SC.CONTACT_NAME,
					realName);
			presenceUpdateDmpResponse.setBooleanValue(SC.IS_ONLINE, true);


			String display = SphereStatement.wrap(this.sphereDefinition).getDisplayName();
			presenceInfo = setUpPresenceInfoForNonGroups(this.session,
					contactsOnly, realName, display);
			if (sphere_id.equals(handler.getVerifyAuth()
					.getSphereCoreId())) {
				sendPresenceUpdate(presenceInfo,
						presenceUpdateDmpResponse, true);
			}

			sendResults(handler, dmpResponse, sphere_id, noReallyAll, allOrder, contactsOnly, presenceInfo);
		} catch (DocumentException exc) {
			logger.error("Document Exception", exc);
		}
	}

	
}
