package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.server.networking.SC;
import ss.util.SessionConstants;

public class SetEmailForwardingRulesHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SetEmailForwardingRulesHandler.class);

	private final DialogsMainCli cli;

	public SetEmailForwardingRulesHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.SET_EMAIL_FORWARDING_RULES;
	}

	public void setEmailForwardingRules(Hashtable session2,
			String emailAddresses, String enabled) {

		setEmailForwardingRules(session2, (String) session2.get(SC.SPHERE_ID),
				emailAddresses, enabled);

	}

	@SuppressWarnings("unchecked")
	public void setEmailForwardingRules(Hashtable session2,
			String targetSphereId, String emailAddresses, String enabled) {

		Hashtable sessionToSend = (Hashtable) session2.clone();
		Hashtable updateValues = new Hashtable();

		updateValues.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.SET_EMAIL_FORWARDING_RULES);
		updateValues.put(SessionConstants.SESSION, sessionToSend);
		if (targetSphereId != null) {
			updateValues.put(SC.TARGET_SPHERE_ID, targetSphereId);
		} else {
			logger.warn("TARGET_SPHERE_ID is null");
		}
		updateValues.put(SC.EMAIL_ADDRESSES, emailAddresses);
		updateValues.put(SC.ENABLED, enabled);
		this.cli.sendFromQueue(updateValues);
	}

}
