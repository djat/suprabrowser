package ss.server.networking.protocol;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import ss.common.DmpFilter;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.server.networking.DialogsMainPeer;
import ss.server.networking.DialogsMainPeerManager;
import ss.server.networking.DmpResponse;
import ss.server.networking.SC;
import ss.server.networking.util.HandlerKey;
import ss.util.VariousUtils;

public class SetEmailForwardingRulesHandler implements ProtocolHandler {

//	private static final String VALUE = "value";

//	private static final String EMAIL_ADDRESS = "email_address";

	private static final String EMAIL_FORWARDING = "email_forwarding";

	private DialogsMainPeer peer;

	private Logger logger = SSLogger.getLogger(this.getClass());

	public SetEmailForwardingRulesHandler(DialogsMainPeer peer) {
		this.peer = peer;
	}

	public String getProtocol() {
		return SSProtocolConstants.SET_EMAIL_FORWARDING_RULES;
	}

	public void handle(Hashtable update) {
		handleSetEmailForwardingRules(update);
	}

	public void handleSetEmailForwardingRules(final Hashtable update) {

		Hashtable session = (Hashtable) update.get(SC.SESSION);
		String emailAddresses = (String) update.get(SC.EMAIL_ADDRESSES);
		String enabled = (String) update.get(SC.ENABLED);
		
		//First try to recived explicit sphere id 
		String sphereId = (String) update.get(SC.TARGET_SPHERE_ID);
		if ( sphereId == null ) {
			this.logger.warn( "TARGET_SPHERE_ID is null, use SPHERE_ID instead" );
			sphereId = (String) session.get(SC.SPHERE_ID );
		}

		this.logger.warn("IN set emailforwarding: " + emailAddresses);
		Document sphereDefinition = setEmailForwardingRules(sphereId,
				emailAddresses, enabled);		
		final DmpResponse dmpResponse = new DmpResponse();
		dmpResponse.setDocumentValue(SC.SPHERE_DEFINITION2, sphereDefinition);//RC
		dmpResponse.setStringValue(SC.PROTOCOL, SSProtocolConstants.UPDATE_SPHERE_DEFINITION);
		dmpResponse.setMapValue(SC.SESSION, session);
		DmpFilter.sendToMembers(dmpResponse, sphereId);
	}

	public Document setEmailForwardingRules(String sphereId,
			String emailAddresses, String enabled) {

		this.logger.debug( String.format( "Get sphere definition, sphereId %s", sphereId ) );
		
		Document sphereDefinition = this.peer.getXmldb().getUtils().getOrCreateSphereDefinition( this.peer.getVerifyAuth(), sphereId);

		if (sphereDefinition != null) {
			Element forwarding = sphereDefinition.getRootElement().element(
					EMAIL_FORWARDING);

			if (forwarding != null) {
				forwarding.detach();

				// forwarding.addElement("email_address").addAttribute("value",emailAddresses);

			}

			Element newForwarding = sphereDefinition.getRootElement()
					.addElement(EMAIL_FORWARDING);
			Vector emails = VariousUtils
					.createVectorFromCommaSeparatedEmails(emailAddresses);

			for (int i = 0; i < emails.size(); i++) {
				Element oneEmail = (Element) emails.get(i);

				newForwarding.add(oneEmail);
			}
			// sphereDefinition.getRootElement().addElement("email_forwarding").addElement("email_address").addAttribute("value",emailAddresses);

			this.peer.getXmldb().replaceDoc(sphereDefinition, sphereId);
		}
//		else {
//			SphereDefinitionCreator sdc = new SphereDefinitionCreator();
//			String displayName = this.peer.getVerifyAuth().getDisplayName(
//					sphereId);
//			sphereDefinition = sdc.createDefinition(displayName, sphereId);
//			sphereDefinition.getRootElement().addElement(EMAIL_FORWARDING)
//					.addElement(EMAIL_ADDRESS).addAttribute(VALUE,
//							emailAddresses);
//			this.peer.getXmldb().insertDoc(sphereDefinition, sphereId);
//
//		}

		return sphereDefinition;

	}

}
