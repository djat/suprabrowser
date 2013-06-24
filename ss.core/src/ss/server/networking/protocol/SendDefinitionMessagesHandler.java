package ss.server.networking.protocol;

import java.util.Hashtable;

import org.dom4j.Document;
import org.dom4j.Element;

import ss.common.GenericXMLDocument;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.common.SphereDefinitionCreator;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.SC;

public class SendDefinitionMessagesHandler implements ProtocolHandler {

	private static final String LAST_QUERY = "last_query";

	private static final String ORIGINAL_ID = "original_id";

	private static final String CURRENT_SPHERE = "current_sphere";

	private static final String MESSAGE_ID = "message_id";

	private static final String THREAD_ID = "thread_id";

	private static final String SINCE_LAST_LAUNCHED = "since_last_launched";

	private static final String _0 = "0";

	private static final String SINCE_MARK = "since_mark";

	private static final String REPLIES_TO_MINE = "replies_to_mine";

	private static final String TOTAL_IN_SPHERE = "total_in_sphere";

	private static final String ID = "id";

	private static final String SINCE_LOCAL_MARK = "since_local_mark";

	private static final String LAST_LAUNCHED = "last_launched";

	private static final String MOMENT = "moment";

	private static final String CONTACT_NAME = "contact_name";

	private static final String USERNAME = "username";

	private static final String SPHERE_ID = "sphere_id";

	private static final String LAUNCHED = "launched";

	private static final String THREAD_TYPE = "thread_type";

	private static final String STATS = "stats";

	private static final String VALUE = "value";

	private static final String TYPE = "type";

	private DialogsMainPeer peer;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SendDefinitionMessagesHandler.class);

	public SendDefinitionMessagesHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.SEND_DEFINITION_MESSAGES;
	}

	public void handle(Hashtable update) {
		handleSendDefinitionMessages(update);
	}

	@SuppressWarnings("unchecked")
	public void handleSendDefinitionMessages(final Hashtable update) {
		
		try {
			Thread.sleep(250);
		} catch (InterruptedException ex) {
		}
		
		Hashtable session = (Hashtable) update.get(SC.SESSION);

		String supraSphere = (String) session.get(SC.SUPRA_SPHERE);
		String sphereId = (String) session.get(SC.SPHERE_ID);
		String realName = (String) session.get(SC.REAL_NAME);
		String username = (String) session.get(SC.USERNAME);
		// TODO it shouldnot be recieved?
		String passphrase = (String) session.get(SC.PASSPHRASE);

		logger.info("supraSphere: " + supraSphere);
		logger.info("sphereId: " + sphereId);
		logger.info("realName: " + realName);
		logger.info("username: " + username);
		logger.info("passphrase: " + passphrase);
		logger.info("Before getsphere core..");
		String sphereCore = this.peer.getXmldb().getUtils().getSphereCore(
				session);

		logger
				.info("SPHERE CORE....get summary from here and create blank def if null: "
						+ sphereCore + sphereId);

		if (sphereCore == null) {
			sphereCore = sphereId;
		} else {
			session.put(SC.SPHERE_CORE, sphereCore);
			session.put(SC.SPHERE_ID, sphereCore);
		}
		sphereId = (String) session.get(SC.SPHERE_ID);

		Document sphereDefinition = this.peer.getXmldb().getSphereDefinition(
				sphereId, sphereCore);

		if (sphereDefinition == null) {
			logger
					.info("sphere definition is null.....not sure what it does otherwise "
							+ this.peer.getVerifyAuth().getDisplayName(
									sphereCore) + " : " + sphereCore);
			SphereDefinitionCreator sdc = new SphereDefinitionCreator();

			sphereDefinition = sdc.createDefinition(this.peer.getVerifyAuth()
					.getDisplayName(sphereCore), sphereCore);

		}

		String personalSphere = this.peer.getVerifyAuth().getSystemName(
				realName);
		Document statsDoc = this.peer.getXmldb().getStatisticsDoc(
				personalSphere, sphereCore);

		String currentMoment = DialogsMainPeer.getCurrentMoment();
		if (statsDoc == null) {

			GenericXMLDocument genericDoc = new GenericXMLDocument();
			Document newStatsDoc = genericDoc.XMLDoc(realName + "'s Stats");
			newStatsDoc.getRootElement().addElement(TYPE).addAttribute(VALUE,
					STATS);
			newStatsDoc.getRootElement().addElement(THREAD_TYPE).addAttribute(
					VALUE, STATS);
			newStatsDoc.getRootElement().addElement(LAUNCHED).addAttribute(
					SPHERE_ID, sphereId).addAttribute(USERNAME, username)
					.addAttribute(CONTACT_NAME, realName).addAttribute(MOMENT,
							currentMoment);
			newStatsDoc.getRootElement().addElement(LAST_LAUNCHED)
					.addAttribute(SPHERE_ID, sphereId).addAttribute(USERNAME,
							username).addAttribute(CONTACT_NAME, realName)
					.addAttribute(MOMENT, currentMoment);

			String repliesToMine = _0;
			newStatsDoc.getRootElement().addElement(SINCE_LOCAL_MARK)
					.addElement(ID).addAttribute(VALUE, sphereCore);
			newStatsDoc.getRootElement().element(SINCE_LOCAL_MARK)
					.addAttribute(
							TOTAL_IN_SPHERE,
							(new Integer(this.peer.getXmldb().countDocs(
									sphereCore))).toString()).addAttribute(
							REPLIES_TO_MINE, repliesToMine).addAttribute(
							SINCE_MARK, _0).addAttribute(SINCE_LAST_LAUNCHED,
							_0);

			Element sphereDef = (Element) sphereDefinition.getRootElement()
					.clone();

			if (sphereDef.element(THREAD_TYPE) != null) {
				sphereDef.element(THREAD_TYPE).detach();
			}
			if (sphereDef.element(TYPE) != null) {
				sphereDef.element(TYPE).detach();
			}
			if (sphereDef.element(THREAD_ID) != null) {
				sphereDef.element(THREAD_ID).detach();
			}
			if (sphereDef.element(MESSAGE_ID) != null) {
				sphereDef.element(MESSAGE_ID).detach();
			}
			if (sphereDef.element(CURRENT_SPHERE) != null) {
				sphereDef.element(CURRENT_SPHERE).detach();
			}
			if (sphereDef.element(ORIGINAL_ID) != null) {
				sphereDef.element(ORIGINAL_ID).detach();
			}
			newStatsDoc.getRootElement().addElement(LAST_QUERY).add(sphereDef);
			this.peer.getXmldb().insertDoc(newStatsDoc, personalSphere);

		} else {

			statsDoc.getRootElement().addElement(LAUNCHED).addAttribute(
					SPHERE_ID, sphereId).addAttribute(USERNAME, username)
					.addAttribute(CONTACT_NAME, realName).addAttribute(MOMENT,
							currentMoment);
			if (statsDoc.getRootElement().element(LAST_LAUNCHED) != null) {
				statsDoc.getRootElement().element(LAST_LAUNCHED).detach();

			}
			if (statsDoc.getRootElement().element(LAST_QUERY) != null) {
				statsDoc.getRootElement().element(LAST_QUERY).detach();

			}
			Element sphereDef = (Element) sphereDefinition.getRootElement()
					.clone();

			if (sphereDef.element(THREAD_TYPE) != null) {
				sphereDef.element(THREAD_TYPE).detach();
			}
			if (sphereDef.element(TYPE) != null) {
				sphereDef.element(TYPE).detach();
			}
			if (sphereDef.element(THREAD_ID) != null) {
				sphereDef.element(THREAD_ID).detach();
			}
			if (sphereDef.element(MESSAGE_ID) != null) {
				sphereDef.element(MESSAGE_ID).detach();
			}
			if (sphereDef.element(CURRENT_SPHERE) != null) {
				sphereDef.element(CURRENT_SPHERE).detach();
			}
			if (sphereDef.element(ORIGINAL_ID) != null) {
				sphereDef.element(ORIGINAL_ID).detach();
			}

			statsDoc.getRootElement().element(SINCE_LOCAL_MARK).addAttribute(
					SINCE_LAST_LAUNCHED, _0);
			statsDoc.getRootElement().addElement(LAST_QUERY).add(sphereDef);
			statsDoc.getRootElement().addElement(LAST_LAUNCHED).addAttribute(
					SPHERE_ID, sphereId).addAttribute(USERNAME, username)
					.addAttribute(CONTACT_NAME, realName).addAttribute(MOMENT,
							currentMoment);
			this.peer.getXmldb().replaceDoc(statsDoc, personalSphere);

		}
		logger.warn("Sphere definiton: " + sphereDefinition.asXML());
		this.peer.sendDefinitionMessages(session, sphereDefinition, this.peer
				.getVerifyAuth(), "false");

		this.peer.sendDefaultSpheres(session);

	}

}
