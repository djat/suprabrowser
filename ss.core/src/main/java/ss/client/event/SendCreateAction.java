/*
 * Created on Apr 10, 2005
 *
 */
package ss.client.event;

import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;

import org.dom4j.Document;

import ss.client.event.createevents.CreateBookmarkAction;
import ss.client.networking.SupraClient;
import ss.client.ui.ControlPanel;
import ss.client.ui.ISphereView;
import ss.client.ui.MessagesPane;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.email.EmailController;
import ss.client.ui.viewers.NewBinarySWT;
import ss.client.ui.viewers.NewContact;
import ss.client.ui.viewers.NewMessage;
import ss.client.ui.viewers.NewSphere;
import ss.client.ui.viewers.NewWeblink;
import ss.common.UiUtils;
import ss.domainmodel.ResultStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.TerseStatement;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.util.VariousUtils;

/**
 * @author david
 * 
 */
public class SendCreateAction {

	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SendCreateAction.class);

	private boolean cleared = false;

	private final ISphereView sphereView;
	
	public SendCreateAction( ISphereView sendCreationProvider )  {
		this.sphereView = sendCreationProvider;
	}

	public static void clearTextAndChecks(final Hashtable session) {

		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				SupraSphereFrame.INSTANCE.setTagChecked(false);
				SupraSphereFrame.INSTANCE.setSendText("");
			}
		});

	}

	public void doSendCreateAction(final SupraSphereFrame sF,
			final Hashtable session) {
		this.cleared = false;

		Thread t = new Thread() {
			@SuppressWarnings("unchecked")
			public void run() {
				final String sendText = sF.getSendText( );
				final AbstractDelivery delivery = sF.getDefaultDelivery(session);

				final MessagesPane mp = sF.tabbedPane.getSelectedMessagesPane();
				if (mp == null) {
					logger.warn("Selected message pane is null");
					return;
				}
				logger.info("qualified");

				final Hashtable send_session = (Hashtable) session.clone();

				Document tempSelectedDoc = null;
				if (mp.getLastSelectedDoc() != null) {
					tempSelectedDoc = (Document) mp.getLastSelectedDoc()
							.clone();
				}

				final Document lastSelectedDoc = tempSelectedDoc;

				String selectedType = sF.getMessageType(session);

				logger.info("selected type " + selectedType);

				if (selectedType.equals(ControlPanel.getTypeKeywords())) {
					SendCreateAction.this.cleared = true;
					try {
						mp.doTagAction(lastSelectedDoc, sendText);
					} catch (Exception e) {
						logger.warn("Tag exception", e);
					}

					clearTextAndChecks(session);

				} else if (selectedType.equals(ControlPanel.getTypeEmail())) {
					(new EmailController(mp, mp.getRawSession()))
							.createEmail(sendText);
				} else if (selectedType.equals(ControlPanel.getTypeRss()) 
							|| selectedType.equals(ControlPanel.getTypeBookmark())) {
						NewWeblink weblinkWindow = new NewWeblink(send_session, mp,
								lastSelectedDoc, delivery, sF.isReplyChecked(), sendText, null);
						weblinkWindow.setIsRssCreation(selectedType.equals(ControlPanel.getTypeRss()));
				}  else if (selectedType.equals(ControlPanel.getTypeMessage())) {

					if (sF.isReplyChecked()) {
						NewMessage nm = new NewMessage(send_session, mp, Statement.wrap(lastSelectedDoc), delivery, null);
						nm.addKeyListener(mp.getInputListener());
					} else {
						logger.info("MAKE IT A MESSAGE: " + delivery);

						NewMessage nm = new NewMessage(send_session, mp, null,
								delivery, sendText);

						nm.addKeyListener(mp.getInputListener());
					}

				} else if (selectedType.equals(ControlPanel.getTypeSphere())) {

					logger.info("IT WAS SPHERE");
					final List<String> selectedMembersNames = SendCreateAction.this.sphereView.getSelectedMembersNames();
					
					if (sF.isReplyChecked()) {
						
						Statement statement = Statement.wrap(lastSelectedDoc);

						if (selectedMembersNames.size() > 0) {
							new NewSphere(session, mp, statement.getMessageId(),
									statement.getThreadId(),
									selectedMembersNames, sF
											.getSendText());
						} else {
							new NewSphere(session, mp, statement.getMessageId()
									, statement.getThreadId(), null, sF
									.getSendText());
							

						}

					} else {
						if (selectedMembersNames.size() > 0) {

							new NewSphere(session, mp, null, null,
									selectedMembersNames, sF
											.getSendText());

						} else {
							new NewSphere(session, mp, null, null, null, sF
									.getSendText());
						}
					}

				} else if (selectedType.equals(ControlPanel.getTypeFile())) {

					if (sF.isReplyChecked()) {
						String fname = NewBinarySWT.createFileName();
						
						if(fname == null || fname.equals("/")) {
							logger.warn("no file choosed");
							return;
						}
						
						NewBinarySWT binary = new NewBinarySWT(send_session, mp,
								fname, lastSelectedDoc);

						binary.setSubject( sendText );
						binary.addButtons();
						binary.addKeyListener(mp.getInputListener());
						if (sendText.length() > 0) {
							binary.giveBodyFocus();
						}
					} else {
						String fname = NewBinarySWT.createFileName();
						
						if(fname == null || fname.equals("/")) {
							logger.warn("no file choosed");
							return;
						}
						
						NewBinarySWT binary = new NewBinarySWT(send_session, mp,
								fname, false);
						binary.setSubject( sendText );
						binary.addButtons();
						binary.addKeyListener(mp.getInputListener());
						if (sendText.length() > 0) {
							binary.giveBodyFocus();
						} else {
							binary.setFocusToSubjectField();
						}
					}

//				} else if (selectedType
//						.equals(ControlPanel.getTypeFilesystem())) {
//
//					// GenericXMLDocument generic = new GenericXMLDocument();
//					// generic.XMLDoc()
//
//					final Display disp = new Display();
//
//					Thread t = new Thread() {
//						public void run() {
//							Shell shell = new Shell(disp);
//							// shell.open ();
//							DirectoryDialog dialog = new DirectoryDialog(shell);
//							// dialog.setFilterPath ("c:\\"); //Windows specific
//							// System.out.println ("RESULT=" + dialog.open ());
//							String result = dialog.open();
//							File resFile = new File(result);
//							boolean isDir = false;
//
//							if (resFile.isDirectory()) {
//								isDir = true;
//							}
//
//							Document doc = GenericXMLDocument.XMLDoc(result,
//									"", (String) session.get("real_name"));
//
//							if (isDir) {
//
//								doc.getRootElement().addElement("type")
//										.addAttribute("value", "filesystem");
//
//								doc.getRootElement().addElement("thread_type")
//										.addAttribute("value", "filesystem");
//								doc.getRootElement().addElement("status")
//										.addAttribute("value", "confirmed");
//								doc.getRootElement().addElement("confirmed")
//										.addAttribute("value", "true");
//
//								String fsep = System
//										.getProperty("file.separator");
//
//								if (fsep.lastIndexOf("\\") != -1) {
//
//									fsep = "backwards";
//
//								} else {
//									fsep = "forwards";
//
//								}
//
//								doc.getRootElement().addElement(
//										"file_separator").addAttribute("value",
//										fsep);
//								doc.getRootElement().addElement(
//										"physical_location").addAttribute(
//										"value",
//										(String) session.get("profile_id"));
//
//								sF.getDC((String) session.get("sphereURL"))
//										.publishTerse(session, doc);
//
//							}
//
//							while (!shell.isDisposed()) {
//								if (!disp.readAndDispatch())
//									disp.sleep();
//							}
//							disp.dispose();
//						}
//					};
//					disp.asyncExec(t);

				}  else if (selectedType.equals(ControlPanel.getTypeContact())) {

					final NewContact newUI = new NewContact(send_session, mp);

					newUI.setTextArea(sendText);
					newUI.createNewToolBar();
					newUI.runEventLoop();

				} else if (selectedType.equals(ControlPanel.getTypeTerse())) {

					SendCreateAction.this.cleared = true;
					if (VariousUtils.isDomain(sendText)) {
						CreateBookmarkAction.saveAsBookmark(null, sendText, null, delivery, session);

					} else if (sF.getSendText().toLowerCase()
							.startsWith("invite::")) {

						clearTextAndChecks(session);
						String inviteURL = sF.getSendText();

						logger.info("before machine verifier requiest");
						Hashtable machineVerifier = sF.client
								.getMachineVerifierForProfile(session,
										inviteURL);

						VariousUtils.printContentsOfSession(machineVerifier);

						String machineSalt = (String) machineVerifier
								.get("machineSalt");
						String machineVer = (String) machineVerifier
								.get("machineVerifier");
						String machineProfile = (String) machineVerifier
								.get("machineProfile");

						Hashtable newSession = (Hashtable) session.clone();

						String sphereID = null;
						String address = null;
						String port = null;
						String homeSphere = inviteURL;
						String first = homeSphere.substring(8, homeSphere
								.length());

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

						SupraClient sClient = new SupraClient(
								(String) newSession.get("address"),
								(String) newSession.get("port"));

						sClient.setSupraSphereFrame(sF);
						sClient.startZeroKnowledgeAuth(newSession,
								"DialogsMainCli");

					}

					else {

						clearTextAndChecks(session);

						final TerseStatement terse = mp.returnTerseStatement(sendText);

						if (lastSelectedDoc != null && sF.isReplyChecked()) {
							try {
								Statement lastSt = Statement.wrap(lastSelectedDoc);
								terse.setType("terse");
								terse.setResponseId(lastSt.getMessageId());
								terse.setThreadId(lastSt.getThreadId());
							} catch (NullPointerException npe) {
							}
						}

						if (!((String) send_session.get("sphere_id"))
								.equals((String) session.get("sphere_id"))) {
							send_session.put("suprasphere", "false");
						} else {
							send_session.put("suprasphere", "true");
						}
						ResultStatement resultMessage = delivery.prepareStatement( terse );
						if (terse.getSubject().length() > 0) {
							sF.getDC((String) session.get("sphereURL"))
									.publishTerse(send_session, terse.getBindedDocument());
						}
						if(resultMessage != null) {
							sF.getDC((String) session.get("sphereURL"))
							.publishTerse(send_session, resultMessage.getBindedDocument());
						}
					}
					if (!SendCreateAction.this.cleared) {
						clearTextAndChecks(session);
					}

				}
				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						if (mp.getControlPanel() instanceof ControlPanel) {
							((ControlPanel) mp.getControlPanel())
							.getReplyBox().setSelection(false);
						}
					}
				});
				UiUtils.swtBeginInvoke(new Runnable() {
					public void run() {
						//CreateAbstractAction.performed();
            // Test out difference under use
					}
				});
			}
		};
		t.start();

	}

	@SuppressWarnings("unchecked")
	public void doInvitationAcceptAction(SupraSphereFrame sF,
			Hashtable session, String inviteURL, Document contactDoc) {

		logger.warn("starting doinvitationaccept action");

		logger.warn("It was this: " + (String) session.get("sphereURL"));

		logger.info("before machine verifier requiest: " + inviteURL);
		Hashtable machineVerifier = sF.client.getMachineVerifierForProfile(
				session, inviteURL);

		String machineSalt = (String) machineVerifier.get("machineSalt");
		String machineVer = (String) machineVerifier.get("machineVerifier");
		String machineProfile = (String) machineVerifier.get("machineProfile");

		Hashtable newSession = (Hashtable) session.clone();

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
		newSession.put("use_machine_verifier", "profile");
		newSession.put("sphere_id", sphereID);
		newSession.put("supra_sphere", sphereID);
		newSession.put("port", port);
		newSession.put("temp_session_id", session_id);
		newSession.put("changePw", "false");
		newSession.put("invite", "transferCredentials");
		newSession.put("sphereURL", (String) session.get("sphereURL"));
		newSession.put("machineSalt", machineSalt);
		newSession.put("machineVerifier", machineVer);
		newSession.put("machineProfile", machineProfile);
		newSession.put("inviteURL", inviteURL);
		newSession.put("contactDoc", contactDoc);

		SupraClient sClient = new SupraClient((String) newSession
				.get("address"), (String) newSession.get("port"));

		sClient.setSupraSphereFrame(sF);
		sClient.startZeroKnowledgeAuth(newSession, "DialogsMainCli");

	}

}
