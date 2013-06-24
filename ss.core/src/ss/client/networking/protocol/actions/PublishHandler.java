package ss.client.networking.protocol.actions;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

public class PublishHandler extends AbstractOldActionBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PublishHandler.class);

	private final DialogsMainCli cli;
	
	public PublishHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public String getProtocol() {
		return SSProtocolConstants.PUBLISH;
	}

	@SuppressWarnings("unchecked")
	public void publishTerse(final Hashtable session, final Document sendDoc) {
		Hashtable toSend = (Hashtable) session.clone();
		// System.out.println("when publishing, the sphere_id is: " +
		// (String) toSend.get("sphere_id"));
		toSend.remove(SessionConstants.PASSPHRASE);
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL, SSProtocolConstants.PUBLISH);
		update.put(SessionConstants.SESSION, toSend);
		update.put(SessionConstants.DOCUMENT, sendDoc);
		

		logger.info("Publishing in sphere: "
				+ session.get(SessionConstants.SPHERE_ID) );

		if (this.cli.getSession().get(SessionConstants.EXTERNAL_CONNECTION) != null) {

			update.put(SessionConstants.EXTERNAL_CONNECTION, "true");

			logger.warn("it has an external connection...good");
			String localSphereURL = (String) this.cli.getSession().get(
					SessionConstants.LOCAL_SPHERE);
			if (localSphereURL == null) {
				localSphereURL = (String) this.cli.getSession().get(
						SessionConstants.SPHERE_URL);
			}

			logger.warn("it was not null " + localSphereURL);

			try {
				// update.put("remoteSphereURL",localSphereURL);
				update.put(SessionConstants.REMOTE_USERNAME, this.cli
						.getSession().get(SessionConstants.USERNAME));
				// update.put("remoteSphereId",localSphereId);
			} catch (Exception e) {

			}

			DialogsMainCli cli = this.cli.getSF()
					.getActiveConnections().getActiveConnection(localSphereURL);

			Hashtable sendSession = (Hashtable) session.clone();
			String before = (String) sendSession
					.get(SessionConstants.SPHERE_ID);

			String localSphereId = (String) cli.getSession().get(
					SessionConstants.LOCAL_SPHERE_ID);

			sendSession.put(SessionConstants.SPHERE_ID, localSphereId);

			Hashtable localUpdate = (Hashtable) update.clone();

			logger
					.warn("Will send local update to this sphere, this is the critical part: "
							+ localSphereId);

			if (!localSphereId.equals(before)) {
				localUpdate.put(SessionConstants.SESSION, sendSession);
				localUpdate.put(SessionConstants.SPHERE, localSphereId);
				localUpdate.put(SessionConstants.REPRESS_NOTIFICATION, "true");
				// TODO so for what is this?
				// sF.client.sendFromQueue(localUpdate);

				logger.warn("sending also to this sphere: " + localSphereId);
			}

			logger.info(" Send from : "
					+ toSend.get(SessionConstants.SPHERE_ID));
		} else {
			logger.info("It does not have an external connection variable");
		}
		this.cli.sendFromQueue(update);
	}

}
