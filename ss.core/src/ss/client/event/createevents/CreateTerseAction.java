/**
 * 
 */
package ss.client.event.createevents;

import java.io.IOException;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.eclipse.swt.graphics.Image;

import ss.client.networking.SupraClient;
import ss.client.ui.MessagesPane;
import ss.client.ui.SDisplay;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.viewers.NewWeblink;
import ss.domainmodel.ResultStatement;
import ss.domainmodel.TerseStatement;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.rss.RSSParser;
import ss.util.ImagesPaths;
import ss.util.VariousUtils;

/**
 * @author zobo
 * 
 */
public class CreateTerseAction extends CreateAbstractAction {

	private static Image image;

	public static final String TERSE_TITLE = "Terse";

	private final static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CreateTerseAction.class);

	private Hashtable session = null;

	/**
	 * 
	 */
	public CreateTerseAction(Hashtable session) {
		super();
		this.session = session;
		try {
			image = new Image(SDisplay.display.get(), getClass()
					.getResource(ImagesPaths.TERSE).openStream());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void performImpl() {

		final String sendText = SupraSphereFrame.INSTANCE.getSendText();
		final AbstractDelivery delivery = SupraSphereFrame.INSTANCE.getDefaultDelivery(this.session);

		final Hashtable send_session = (Hashtable) this.session.clone();

		final MessagesPane mp = (MessagesPane) SupraSphereFrame.INSTANCE.tabbedPane
				.getSelectedMessagesPane();
		if (mp == null) {
			logger.warn("Selected message pane is null");
			return;
		}
		
		final Document lastSelectedDoc = (mp.getLastSelectedDoc() == null) ? null : ((Document) mp.getLastSelectedDoc().clone());

		Thread t = new Thread() {

			private CreateTerseAction terse = CreateTerseAction.this;

			@SuppressWarnings("unchecked")
			public void run() {

				if (VariousUtils.isDomain(SupraSphereFrame.INSTANCE
						.getSendText())) {

					String saveText = sendText;

					if (saveText.toLowerCase().startsWith("feed://")) {
						saveText = saveText.replace("feed://", "http://");
					}
					if (saveText.toLowerCase().startsWith("www")) {
						saveText = "http://" + saveText;
					} else if (!saveText.toLowerCase().startsWith("http")) {
						saveText = "http://www." + saveText;
					}
					final String text = saveText;

					Thread t = new Thread() {

						private CreateTerseAction terse = CreateTerseAction.this;

						@Override
						@SuppressWarnings("unchecked")
						public void run() {

							String url = text;

							String mainTitle = null;

							if (mainTitle == null) {
								mainTitle = RSSParser.getTitleFromURL(text);
							}

							this.terse.session.put("sphere_id",mp.getSphereId());
							Hashtable temp = (Hashtable) SupraSphereFrame.INSTANCE
									.getRegisteredSession(
											(String) this.terse.session
													.get("supra_sphere"),
											"DialogsMainCli");
							String sessionId = (String) temp.get("session");
							this.terse.session.put("session", sessionId);
							
							String desiredSubject = (mainTitle != null) ? mainTitle : url;
							
							NewWeblink nw = new NewWeblink(this.terse.session,
									null, "normal", url, desiredSubject, null);

							nw.doPublishAction();

							SupraSphereFrame.INSTANCE.setReplyChecked(false);

							SupraSphereFrame.INSTANCE.setSendText( "");

						}
					};
					t.start();

				} else if (SupraSphereFrame.INSTANCE.getSendText()
						.toLowerCase().startsWith("invite::")) {

					String inviteURL = SupraSphereFrame.INSTANCE
							.getSendText();

					logger.info("before machine verifier requiest");
					Hashtable machineVerifier = SupraSphereFrame.INSTANCE.client
							.getMachineVerifierForProfile(this.terse.session,
									inviteURL);

					VariousUtils.printContentsOfSession(machineVerifier);

					String machineSalt = (String) machineVerifier
							.get("machineSalt");
					String machineVer = (String) machineVerifier
							.get("machineVerifier");
					String machineProfile = (String) machineVerifier
							.get("machineProfile");

					Hashtable newSession = (Hashtable) this.terse.session
							.clone();

					String sphereID = null;
					String address = null;
					String port = null;
					String homeSphere = inviteURL;
					String first = homeSphere.substring(8, homeSphere.length());

					StringTokenizer st = new StringTokenizer(first, ":");

					address = st.nextToken();
					logger.info("address: " + address);
					String portST = st.nextToken();
					logger.info("port: " + portST);
					st = new StringTokenizer(portST, ",");
					port = st.nextToken();
					logger.info("remaining port:" + port);
					sphereID = st.nextToken();

					logger.info("SPHERE ID...impt: " + sphereID);

					long long_num = System.currentTimeMillis();
					String session_id = (Long.toString(long_num));

					newSession.put("address", address);
					newSession.put("sphere_id", sphereID);
					newSession.put("supra_sphere", sphereID);
					newSession.put("port", port);
					newSession.put("temp_session_id", session_id);
					newSession.put("changePw", "false");
					newSession.put("invite", "transferCredentials");
					newSession.put("sphereURL", homeSphere);
					newSession.put("machineSalt", machineSalt);
					newSession.put("machineVerifier", machineVer);
					newSession.put("machineProfile", machineProfile);
					newSession.put("inviteURL", inviteURL);

					VariousUtils.printContentsOfSession(newSession);

					SupraClient sClient = new SupraClient((String) newSession
							.get("address"), (String) newSession.get("port"));

					sClient.setSupraSphereFrame(SupraSphereFrame.INSTANCE);
					sClient
							.startZeroKnowledgeAuth(newSession,
									"DialogsMainCli");

				}

				else {

					final TerseStatement newTerse = mp.returnTerseStatement(SupraSphereFrame.INSTANCE
							.getSendText());

					if (lastSelectedDoc != null
							&& SupraSphereFrame.INSTANCE.isReplyChecked()) {

						try {

							newTerse.setType("terse");
							newTerse.setResponseId(lastSelectedDoc.getRootElement().element("message_id")
													.attributeValue("value"));
							newTerse.setThreadId(lastSelectedDoc.getRootElement()
									.element("thread_id")
									.attributeValue("value"));
						} catch (NullPointerException npe) {

						}
					}

					if (!((String) send_session.get("sphere_id"))
							.equals((String) this.terse.session
									.get("sphere_id"))) {

						send_session.put("suprasphere", "false");
					} else {
						send_session.put("suprasphere", "true");
					}
					
					ResultStatement result = delivery.prepareStatement(newTerse);					
					if (newTerse.getSubject().length() > 0) {

						SupraSphereFrame.INSTANCE.getDC(
								(String) this.terse.session.get("sphereURL"))
								.publishTerse(send_session, newTerse.getBindedDocument());
					}
					
					if(result != null) {
						SupraSphereFrame.INSTANCE.getDC(
								(String) this.terse.session.get("sphereURL"))
								.publishTerse(send_session, result.getBindedDocument());
					}

					SupraSphereFrame.INSTANCE.setReplyChecked(false);
					SupraSphereFrame.INSTANCE.setSendText("");
				}

			}
		};
		t.start();

		super.performImpl();
	}

	public String getName() {
		return TERSE_TITLE;
	}

	public Image getImage() {
		return image;
	}

}
