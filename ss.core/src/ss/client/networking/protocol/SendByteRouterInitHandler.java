/**
 * Jul 5, 2006 : 12:09:53 PM
 */
package ss.client.networking.protocol;

import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import ss.client.networking.ByteRouterClient;
import ss.client.networking.DialogsMainCli;
import ss.client.networking.SupraClient;
import ss.client.ui.SupraSphereFrame;
import ss.common.ProtocolHandler;
import ss.common.SSProtocolConstants;
import ss.global.SSLogger;
import ss.util.SessionConstants;

/**
 * @author dankosedin
 * 
 */
public class SendByteRouterInitHandler implements ProtocolHandler {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(SendByteRouterInitHandler.class);
	
	// TODO move
	private static final String VALUE = "value";

	private static final String PHYSICAL_LOCATION = "physical_location";

	private static final String BYTE_ROUTER_CLIENT = "ByteRouterClient";

	private static final String START_BYTE_ROUTER = "StartByteRouter";

	private static final String SENDER = "sender";

	private final DialogsMainCli cli;

	public SendByteRouterInitHandler(DialogsMainCli cli) {
		this.cli = cli;
	}

	public void handleSendByteRouterInit(final Hashtable update) {
		Thread t = new Thread() {

			private Logger logger = SSLogger.getLogger(this.getClass());

			private DialogsMainCli cli = SendByteRouterInitHandler.this.cli;

			public void run() {

				Document doc = (Document) update.get(SessionConstants.DOC);

				Hashtable updateSession = (Hashtable) update
						.get(SessionConstants.SESSION);

				this.logger
						.info("The request to start should have the same session information twice: "
								+ (String) updateSession
										.get(SessionConstants.SESSION));

				Element physicalLocation = doc.getRootElement().element(
						PHYSICAL_LOCATION);

				String locationName = null;

				if (physicalLocation != null) {
					locationName = physicalLocation.attributeValue(VALUE);
				}

				this.logger.info("Got request to start!: " + doc.asXML());

				String currentProfileId = this.cli.getSF().getMainVerbosedSession().getProfileId();

				this.logger.info("currentProfileId: " + currentProfileId);

				if (locationName.equals(currentProfileId)) {

					final Hashtable sendSession = this.cli.getSF().getMainVerbosedSession().rawClone();
					Hashtable brcSession = this.cli
							.getSF()
							.getRegisteredSession(
									(String) this.cli.session
											.get(SessionConstants.SUPRA_SPHERE),
									BYTE_ROUTER_CLIENT);
					ByteRouterClient br = null;

					if (brcSession != null) {
						br = this.cli.getSF().getActiveByteRouters()
								.getLatestByteRouter(doc);
					}
					if (br == null) {
						this.logger
								.info("byte router was null when trying to instantiate from remote");
						newByteRouterClient(doc, sendSession);
					} else {
						this.logger
								.info("see if you can use the existing byte router from remote site...will be hard...may have to change it");
						if (br.isReusable()) {
							this.logger
									.info("ITS Reusable at remote.....try sending bytes??");
							br.setReusable(false);
							br.writeInitBytesToEndpoint();
							br.writeBytesToEndpoint(doc, null);
						} else {
							this.logger
									.info("COUld not reuse it, establish another connection...");
							newByteRouterClient(doc, sendSession);
						}
					}
				}
			}
		};
		t.start();
	}

	public String getProtocol() {
		return SSProtocolConstants.SEND_BYTE_ROUTER_INIT;
	}

	public void handle(Hashtable update) {
		handleSendByteRouterInit(update);
	}

	@SuppressWarnings("unchecked")
	private void newByteRouterClient(Document doc, final Hashtable sendSession) {
		// TODO organize bootStrapInfo constants

		SupraSphereFrame sf = SendByteRouterInitHandler.this.cli.getSF();
		String supraSphere = ((String) sendSession
				.get(SessionConstants.SUPRA_SPHERE));
		String tempPW = sf.getTempPasswords().getTempPW(supraSphere);

		Hashtable bootStrapInfo = new Hashtable();
		bootStrapInfo.put(SessionConstants.DOC, doc);
		bootStrapInfo.put(SessionConstants.SENDER_OR_RECEIVER, SENDER);

		sendSession.put(SessionConstants.BOOT_STRAP_INFO, bootStrapInfo);
		sendSession.put(SessionConstants.PASSPHRASE, tempPW);

		String address = (String) sendSession.get(SessionConstants.ADDRESS);
		String port = (String) sendSession.get(SessionConstants.PORT);
		SupraClient sClient = new SupraClient(address, port);
		sClient.setSupraSphereFrame(this.cli.getSF());
		sClient.startZeroKnowledgeAuth(sendSession, START_BYTE_ROUTER);
	}

	@SuppressWarnings("unchecked")
	public void sendByteRouterInit(Hashtable session2, Document doc) {
		Hashtable toSend = (Hashtable) session2.clone();
		Hashtable update = new Hashtable();
		update.put(SessionConstants.PROTOCOL,
				SSProtocolConstants.SEND_BYTE_ROUTER_INIT);

		update.put(SessionConstants.DOC, doc);
		update.put(SessionConstants.SESSION, toSend);
		this.cli.sendFromQueue(update);
	}

}
