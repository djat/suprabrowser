package ss.server.networking;

import java.util.Hashtable;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import ss.common.ListUtils;
import ss.common.SSProtocolConstants;
import ss.common.VerifyAuth;

public class GroupSphereSender extends AbstractSphereSender {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(GroupSphereSender.class);

	/**
	 * @param dialogMainPeerOwner
	 * @param session
	 * @param sphereDefinition
	 * @param verifyAuth
	 * @param openBackground
	 */
	public GroupSphereSender(DialogsMainPeer dialogMainPeerOwner,
			Hashtable session, Document sphereDefinition,
			VerifyAuth verifyAuth, String openBackground) {
		super(dialogMainPeerOwner, session, sphereDefinition, verifyAuth,
				openBackground);
	}

	@Override
	protected Vector<String> generateSurrounding(String sphere_id,
			DialogsMainPeer handler) {
		final String data_id = handler.getXmldb().getUtils().getInheritedName(
				sphere_id);
		logger.info("DATA ID from get inhereitd: " + data_id);
		/* NOT_USED String sphereCore = */handler.getXmldb().getUtils()
				.getSphereCore(this.session);

		final Element sphereScope = this.sphereDefinition.getRootElement().element(
				"scope");
		Vector<String> surrounding = new Vector<String>();
		surrounding.add(data_id);
		surrounding = (sphereScope != null) ? processSphereScope(this.session,
				this.sphereDefinition, surrounding, data_id, sphereScope
						.attributeValue("value")) : surrounding;
		return surrounding;
	}

	@Override
	protected void prepareDmpResponse(String sphere_id, DmpResponse dmpResponse) {
		super.prepareDmpResponse(sphere_id, dmpResponse);
		Element create_spheres = this.sphereDefinition.getRootElement()
				.element("create_spheres");

		if (create_spheres != null) {

			String create_id = create_spheres.element("sphere").attributeValue(
					"system_name");
			Document create = getXmldb().getSphereDefinition(sphere_id,
					create_id);

			dmpResponse.setDocumentValue(SC.CREATE_DEFINITION, create);
		}
	}


	protected void processSupraQuery(DialogsMainPeer handler,
			final DmpResponse dmpResponse, Vector surrounding) {
		String sphere_id = (String) this.session.get(SC.SPHERE_ID);
		Document firstDoc = (Document) this.session.get(SC.FIRST_DOC);
		String realName = (String) this.session.get(SC.REAL_NAME);
		String supraSphere = (String) this.session.get(SC.SUPRA_SPHERE);
		String username = (String) this.session.get(SC.USERNAME);
		Object queryId = this.session.get(SC.QUERY_ID);

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

				processReallyAll(dmpResponse, this.sphereDefinition,
						noReallyAll, allOrder, contactsOnly, reallyAll);

			}

			if (firstDoc != null) {
				// This is a hack work around...cant figure
				// out
				// why it doesnt commit the document when
				// publishing in time for the reqest to
				// openanothersphere to get this doc

				String messageId = firstDoc.getRootElement().element(
						"message_id").attributeValue("value");
				if (!noReallyAll.containsKey(messageId)) {
					noReallyAll.put(messageId, firstDoc);
					allOrder.add(messageId);
				}

			}

			Vector presenceInfo = new Vector();

			logHandler(handler);

			logHandlerVerifyAuth(handler);

			logger.info("spherid bvefore quey: " + sphere_id);
			logger.info("sphere code id: "+ handler.getVerifyAuth().getSphereCoreId());
			String sphereType = handler.getVerifyAuth()
					.getSphereType(sphere_id);
			if (sphereType != null) {
				boolean isGroup = sphereType.equals("group");
				final DmpResponse presenceUpdateDmpResponse = new DmpResponse();
				presenceUpdateDmpResponse.setStringValue(SC.PROTOCOL,
						SSProtocolConstants.REFRESH_PRESENCE);
				presenceUpdateDmpResponse.setStringValue(SC.CONTACT_NAME,
						realName);
				presenceUpdateDmpResponse.setBooleanValue(SC.IS_ONLINE, true);
				if (isGroup) {
					presenceUpdateDmpResponse.setStringValue(
							SC.CHANGE_PRESENCE_ONLY, "false");
				} else {
					// TODO temp2.put(SC.CHANGE_PRESENCE_ONLY, "false");
					// needed or not?
				}

				logger.warn("SPHERE ID HERE: " + realName + " : " + sphere_id);

				if (isGroup) {
					presenceInfo = setUpPresenceInfoForGroups(handler,
							this.session, sphere_id);
					if ((sphere_id.equals(supraSphere) || sphere_id
							.equals(handler.getVerifyAuth().getSphereCoreId()))
							&& queryId == null) {

						logger
								.info("NEED TO NOTIFY OTHERS THAT I HAVE LOGGED IN: "
										+ presenceInfo.size()
										+ " : "
										+ realName);
						sendPresenceUpdate(presenceInfo,
								presenceUpdateDmpResponse, true);
						if (true) {
							String personalSphere = this.verifyAuth
									.getSystemName(realName);
							Vector membersWithLoginSphere = getXmldb()
									.getMembersWithLoginSphere(supraSphere,
											personalSphere);
							sendPresenceUpdate(membersWithLoginSphere,
									presenceUpdateDmpResponse, true);

							Vector membersWithAccessibleSphereCore = getXmldb()
									.getMembersWithAccessibleSphereCore(
											supraSphere, username);
							sendPresenceUpdate(membersWithAccessibleSphereCore,
									presenceUpdateDmpResponse, true);
						}

					} else {
						logger
								.info("could not notify people that I logged in this time");
					}
				} else {

					String display = handler.getVerifyAuth().getDisplayNameWithoutRealName(
							sphere_id);
					if ( display == null ) {
						logger.error( "Can't get display name for " + sphere_id );
					}
					presenceInfo = setUpPresenceInfoForNonGroups(this.session,
							contactsOnly, realName, display);
					if (sphere_id.equals(handler.getVerifyAuth()
							.getSphereCoreId())) {

						logger
								.info("I DOOOOOO NEED TO NOTIFY OTHERS THAT I HAVE LOGGED IN: "
										+ presenceInfo.size()
										+ " : "
										+ realName);
						sendPresenceUpdate(presenceInfo,
								presenceUpdateDmpResponse, true);
					}
				}
			} else {
				logger.info("was necessary but didnt do it");
			}
			logger.info("Got here at least");
			logger.warn("Sending as node!!!!!!");
			if (logger.isDebugEnabled()) {
				logger.debug("Send xml search result "
						+ ListUtils.valuesToString(allOrder));
			}
			sendResults(handler, dmpResponse, sphere_id, noReallyAll, allOrder, contactsOnly, presenceInfo);
		} catch (DocumentException exc) {
			logger.error("Document Exception", exc);
		}
	}

	

}
