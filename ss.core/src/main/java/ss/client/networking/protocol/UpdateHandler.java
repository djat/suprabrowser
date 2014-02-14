/**
 * Jul 5, 2006 : 5:20:28 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import org.dom4j.Document;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.messagedeliver.DeliverersManager;
import ss.client.ui.sphereopen.SphereOpenManager;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class UpdateHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(UpdateHandler.class);

	private final DialogsMainCli cli;

	public UpdateHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	@SuppressWarnings("unchecked")
	public void handleUpdate(final Hashtable update) {
		if (logger.isDebugEnabled()){
			logger.debug("Got update");
		}

		if (update.get(SessionConstants.EXTERNAL_CONNECTION) != null) {
			if (((String) update.get(SessionConstants.EXTERNAL_CONNECTION))
					.equals("true")) {

				logger.warn("Got update where external connection is : "
						+ (String) update.get(SessionConstants.SPHERE));

				// String remoteSphereId =
				// (String)update.get("remoteSphereId");
				logger.warn("Sphere Id: "
						+ (String) this.cli.session
								.get(SessionConstants.SPHERE_ID));
				logger.warn("Update sphere: "
						+ (String) update.get(SessionConstants.SPHERE));
				// logger.warn("remote sphere id:
				// "+remoteSphereId);

				// TODO maybe we need to use DialogsMainCli.publishTerse()
				Hashtable newUpdate = new Hashtable();
				newUpdate.put(SessionConstants.PROTOCOL,
						SSProtocolConstants.PUBLISH);

				Hashtable newSession = (Hashtable) this.cli.session.clone();

				String localSphereId = null;
				try {
					localSphereId = (String) this.cli.session
							.get(SessionConstants.LOCAL_SPHERE_ID);
					logger.warn("local 1: " + localSphereId);
				} catch (Exception e) {
					logger.error(e);
				}

				if (localSphereId != null) {
					newSession.put(SessionConstants.SPHERE_ID, localSphereId);

					if (!localSphereId.equals((String) this.cli.session
							.get(SessionConstants.SPHERE_ID))) {
						logger.warn("WILL SEND IT!: " + localSphereId);
						newUpdate.put(SessionConstants.SESSION, newSession);
						newUpdate.put(SessionConstants.REPRESS_NOTIFICATION,
								"true");
						newUpdate.put(SessionConstants.DOCUMENT,
								(Document) update
										.get(SessionConstants.DOCUMENT));

						this.cli.getSF().client.sendFromQueue(newUpdate);
					}
				} else {
					logger.warn("local sphere id was null....");
				}

				// sendFromQueue(newUpdate);
			} else {
				logger.warn("external connection does not equal true! what???");
			}
		} else {
			logger.info("External connection is null");
		}

		Document doc = (Document) update.get(SessionConstants.DOCUMENT);
		//final Statement st = Statement.wrap(doc);
		final String systemName = (String) update.get(SessionConstants.SPHERE);
		final String typeOfUpdate = (String) update.get(SessionConstants.IS_UPDATE);
		
		SphereOpenManager.INSTANCE.requestPeek(systemName);
		DeliverersManager.INSTANCE.insert(DeliverersManager.FACTORY
				.createSimple(doc, typeOfUpdate, true, false, systemName));
	}

	public String getProtocol() {
		return SSProtocolConstants.UPDATE;
	}

	public void handle(Hashtable update) {
		handleUpdate(update);
	}

}
