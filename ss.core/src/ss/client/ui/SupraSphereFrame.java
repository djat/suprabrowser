package ss.client.ui;

/*
 * Created on 21-Apr-2004
 * 
 * 
 * Preferences - Java - Code Generation - Code and Comments
 */
/**
 * @author Administrator
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */

import java.awt.SystemColor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import org.dom4j.Document;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.event.TabChangeListener;
import ss.client.event.TabMouseListener;
import ss.client.event.executors.StatementExecutor;
import ss.client.event.executors.StatementExecutorFactory;
import ss.client.hotkeys.HotKeysManager;
import ss.client.networking.DialogsMainCli;
import ss.client.networking.NetworkConnectionFactory;
import ss.client.networking.SupraClient;
import ss.client.networking2.ClientProtocolManager;
import ss.client.ui.browser.BrowserDataSource;
import ss.client.ui.browser.SimpleBrowserDataSource;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.controllers.ActiveByteRoutersController;
import ss.client.ui.controllers.ActiveConnectionsController;
import ss.client.ui.controllers.MessagePanesController;
import ss.client.ui.controllers.PendingSpheresController;
import ss.client.ui.controllers.TempPasswordsController;
import ss.client.ui.docking.ISearchable;
import ss.client.ui.docking.PreviewAreaDocking;
import ss.client.ui.root.RootTab;
import ss.client.ui.root.SupraTab;
import ss.client.ui.tempComponents.SupraCTabItem;
import ss.client.ui.tempComponents.SupraCTabTable;
import ss.client.ui.tempComponents.SupraSphereShellController;
import ss.client.ui.tempComponents.SupraTrayItemController;
import ss.client.ui.viewers.comment.CommentWindowController;
import ss.common.LocationUtils;
import ss.common.MapUtils;
import ss.common.OsUtils;
import ss.common.SphereDefinitionCreator;
import ss.common.UiUtils;
import ss.domainmodel.BookmarkStatement;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.workflow.AbstractDelivery;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.server.networking.SC;
import ss.util.ImagesPaths;
import ss.util.NameTranslation;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;

public class SupraSphereFrame extends ApplicationWindow {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SupraSphereFrame.class);

	private static Random tableIdGenerator = new Random();

	private final SupraSphereShellController shellController;

	private final Display display;

	public DialogsMainCli client = null;

	private Hashtable session = new Hashtable();

	public SupraCTabTable tabbedPane = null;

	public Hashtable cpTracker = new Hashtable();

	private SupraTrayItemController trayItem = null;

	private Hashtable clientSessions = new Hashtable();

	private final WelcomeScreen welcomeScreen;

	private SupraMenuBar smb = null;

	private TempPasswordsController tempPasswords = new TempPasswordsController();

	private PendingSpheresController pendingSpheres = new PendingSpheresController();

	private ActiveByteRoutersController activeByteRouters = new ActiveByteRoutersController();

	private final MessagePanesController messagePanesController = new MessagePanesController();

	private ActiveConnectionsController activeConnections = new ActiveConnectionsController();

	private CommentWindowController commentController = new CommentWindowController();

	private SupraTab rootTab;

	private MessagesPane mainMessagePane = null;

	private volatile long timeDifference;

	public static SupraSphereFrame INSTANCE = null;

	public SupraSphereFrame(Display display, WelcomeScreen welcomeScreen) {
		super(null);
		this.shellController = new SupraSphereShellController(this);
		this.welcomeScreen = welcomeScreen;
		NetworkConnectionFactory.INSTANCE.setSupraSphereFrame(this);
		addMenuBar();
		try {
			UIManager.setLookAndFeel(new WindowsLookAndFeel());
		} catch (Exception e) {
		}

		this.display = display;
		LocationUtils.init();
		if (INSTANCE != null) {
			INSTANCE.disposeButDontRemove();
		}
		INSTANCE = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#getInitialSize()
	 */
	@Override
	protected Point getInitialSize() {
		// TODO Auto-generated method stub
		return new Point(1024, 768);
	}

	/*
	 * public void setSpheres(Hashtable spheres) { this.spheres = spheres; }
	 */
	public void setSystemColors() {
		ColorUIResource control;
		ColorUIResource controlShadow;
		ColorUIResource controlHighlight;
		ColorUIResource controlDkShadow;
		// control = new ColorUIResource(new Color(242,243,244));
		control = new ColorUIResource(new java.awt.Color(239, 239, 239));
		controlHighlight = new ColorUIResource(SystemColor.controlLtHighlight);
		ColorUIResource controlDarkHighlight = new ColorUIResource(
				java.awt.Color.lightGray);
		controlShadow = new ColorUIResource(SystemColor.controlShadow);
		controlDkShadow = new ColorUIResource(java.awt.Color.darkGray);
		UIDefaults defaults = UIManager.getDefaults();
		// defaults.put("Label.background",Panel.);
		// defaults.put("Panel.background",SystemColor.control);
		// defaults.put("")
		// defaults.put("Panel.background",new ColorUIResource(new
		// java.awt.Color(255,255,255)));
		defaults.put("ScrollBar.background", control); // Background to slider
		defaults.put("ScrollBar.foreground", control); // Dots, I think
		defaults.put("ScrollBar.track", controlDarkHighlight);
		defaults.put("ScrollBar.trackHighlight", controlDkShadow);
		defaults.put("ScrollBar.thumb", new ColorUIResource(new java.awt.Color(
				214, 214, 206))); // Actual slider
		defaults.put("ScrollBar.thumbHighlight", controlHighlight);
		defaults.put("ScrollBar.thumbDarkShadow", controlDkShadow);
		defaults.put("ScrollBar.thumbLightShadow", controlShadow);
	}

	private void centerComponent(Composite comp) {
		Monitor primary = this.display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = comp.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		comp.setLocation(x, y);
	}

	public WelcomeScreen getWelcomeScreen() {
		return this.welcomeScreen;
	}

	public void toFrontOnTopAndSelectMessage(final Document doc,
			final Hashtable session) {

		if (logger.isDebugEnabled()) {
			logger.debug("to front on top and select");
		}

		this.client.voteDocument(session, doc.getRootElement().element(
				"message_id").attributeValue("value"), doc);

		if (doc != null) {

			Thread inner = new Thread() {
				private SupraSphereFrame supra = SupraSphereFrame.this;

				public void run() {

					this.supra.getShell().setMinimized(false);
					this.supra.getShell().setActive();
					this.supra.getShell().setVisible(true);

					String sphereId = (String) session.get("sphere_id");
					Statement statement = Statement.wrap(doc);
					String messageId = statement.getMessageId();

					if (sphereId != null) {

						MessagesPane mp = this.supra
								.getMessagesPaneFromSphereId(sphereId,
										(String) session.get("unique_id"));

						MessagesPane messagesPane = this.supra.tabbedPane
								.findMessagesPane(sphereId, (String) session
										.get("unique_id"));
						this.supra.tabbedPane.selectTabByPane(messagesPane);
						this.supra.getMenuBar().checkAddRemoveEnabled();
						if (mp != null && messageId != null) {
							logger
									.warn("neither mp nor messageId were null...");

							mp.setLastSelected(doc);
							mp.getMessagesTree().selectMessage(
									statement.getMessageId());
							mp.selectItemInTable(statement);
							mp.getControlPanelDocking().setFocusToTextField();

							if (!statement.isTerse()) {
								mp.setPreviewDocumentText(doc);
							} else {
								if (mp.getLastSelectedDoc() != null) {
									if (!(mp.getLastSelectedDoc()
											.getRootElement().element("type")
											.attributeValue("value")
											.equals("terse"))) {
										mp.loadWindow(statement);
										logger.info("to front on top");
									}

								}

							}

							this.supra.setReplyChecked(true);
						} else {
							logger.warn("something went wrong");
						}
					}

				}

			};
			UiUtils.swtBeginInvoke(inner);

		}

	}

	public void toFrontOnTopAndDoClick(final Document doc,
			final Hashtable session) {

		if (logger.isDebugEnabled()) {
			logger.debug("to front on top and do click");
		}
		final Statement statement = Statement.wrap(doc);
		this.client.voteDocument(session, statement.getMessageId(), doc);

		if (doc != null) {
			doc.getRootElement().addElement("from_balloon");
			Thread inner = new Thread() {
				private SupraSphereFrame supra = SupraSphereFrame.this;

				public void run() {

					this.supra.getShell().setMinimized(false);
					this.supra.getShell().setActive();
					this.supra.getShell().setVisible(true);

					String sphereId = (String) session.get("sphere_id");
					String messageId = statement.getMessageId();

					if (sphereId != null) {
						MessagesPane mp = this.supra
								.getMessagesPaneFromSphereId(sphereId,
										(String) session.get("unique_id"));

						this.supra.tabbedPane.selectTabByPane(mp);
						this.supra.getMenuBar().checkAddRemoveEnabled();
						if (mp != null && messageId != null) {
							logger
									.warn("neither mp nor messageId were null...");

							mp.getControlPanelDocking().setFocusToTextField();

							if (!statement.isTerse()) {
								StatementExecutor exec = StatementExecutorFactory
										.createExecutor(mp, statement);
								exec.performExecute(true, false);
								// mp.getMessagesTree().getListener()
								// .singleLeftMouseClicked(statement);
							} else {
								if (!mp.isInsertable()) {
									mp.loadWindow(statement);
								} else {
									mp.selectMessage(statement);
								}
							}
							mp.setLastSelected(doc);
						}
						this.supra.setReplyChecked(true);
					} else {
						logger.warn("something went wrong");
					}
				}
			};
			UiUtils.swtBeginInvoke(inner);
		}
	}

	public MessagesPane getMessagesPaneFromSphereId(String sphereId) {
		return this.messagePanesController
				.findFirstMessagePaneBySphereId(sphereId);
	}

	public MessagesPane getMessagesPaneWithoutQueryIdFromSphereId(
			String sphereId) {
		return this.messagePanesController
				.findFirstMessagePaneBySphereIdAndWithoutQuery(sphereId);
	}

	public MessagesPane getMessagesPaneFromSphereId(String sphereId,
			String uniqueId) {
		return this.messagePanesController.findMessagePane(sphereId, uniqueId);
	}

	/*
	 * public void createDownloadBar(final int maximum) { }
	 */
	private boolean startMainController(Hashtable session) {
		logger.info("Starting main controller in SSFrame...");
		SupraClient sClient = new SupraClient((String) session.get("address"),
				(String) session.get("port"));
		// this.getShell().setText((String) session.get("supra_sphere"));

		sClient.setSupraSphereFrame(this);
		Object dmcObject = sClient.startZeroKnowledgeAuth(session, "DialogsMainCli"); 

		if (dmcObject != null && dmcObject instanceof DialogsMainCli) {
			DialogsMainCli dialogsMainCli = (DialogsMainCli)dmcObject;
			this.getActiveConnections().putActiveConnection(
					((String) dialogsMainCli.getSession().get("sphereURL")),
					dialogsMainCli);

			this.client = dialogsMainCli;

			if (logger.isDebugEnabled()) {
				logger.debug("DMC is not null: " + this.client);
			}
		}
		return (dmcObject instanceof DialogsMainCli);
	}

	public void startConnection(Document sphereDoc, Hashtable localSession,
			String sphereURL, String username, String localSphereId) {

		DialogsMainCli cli = this.getDC(sphereURL);

		if (cli == null) {

			logger.warn("sphereURL: " + sphereURL + " : " + username + " : "
					+ (String) localSession.get("username"));

			String machinePass = this.client.getMachinePass(localSession,
					sphereURL);

			// logger.warn("machine pass: "+machinePass);

			// String machineVerifier = (String) machine.get("machineVerifier");
			// String machineProf = (String) machine.get("machineProfile");

			// logger.info("machine verifier: " + machineVerifier);
			// logger.info("machine profile :" + machineProf);

			Hashtable<String, Object> newSession = new Hashtable<String, Object>();
			String sphereID = null;
			String address = null;
			String port = null;
			String homeSphere = sphereURL;
			String first = homeSphere.substring(8, homeSphere.length());

			StringTokenizer st = new StringTokenizer(first, ":");

			address = st.nextToken();
			logger.info("address: " + address);
			String portST = st.nextToken();
			logger.info("port: " + portST);
			st = new StringTokenizer(portST, ",");
			port = st.nextToken();
			sphereID = st.nextToken();

			long long_num = System.currentTimeMillis();
			String session_id = (Long.toString(long_num));

			newSession.put("username", username);
			newSession.put("address", address);
			newSession.put("localSphereId", localSphereId);

			newSession.put("sphere_id", sphereID);
			newSession.put("supra_sphere", sphereID);
			newSession.put("port", port);
			newSession.put("temp_session_id", session_id);
			newSession.put("changePw", "false");
			newSession.put("invite", "false");
			newSession.put("sphereURL", homeSphere);
			newSession.put("passphrase", machinePass);

			newSession.put("use_machine_verifier", "false");

			newSession.put("externalConnection", "true");

			logger.warn("got here in start connetion...");

			SupraClient sClient = new SupraClient((String) newSession
					.get("address"), (String) newSession.get("port"));
			sClient.setSupraSphereFrame(this);

			// sClient.setSupraSphereFrame(this);
			// sClient.startZeroKnowledgeAuth(newSession, "DialogsMainCli")
			Object dmcObject = sClient.startZeroKnowledgeAuth(newSession, "DialogsMainCli");
			
			if (dmcObject!=null && dmcObject instanceof DialogsMainCli) {
				DialogsMainCli dialogsMainCli = (DialogsMainCli)dmcObject;
				this.getActiveConnections()
						.putActiveConnection(
								((String) dialogsMainCli.getSession().get(
										"sphereURL")), dialogsMainCli);

				// this.client = dialogsMainCli;

				// dialogsMainCli.order = 4;

			}
			// dialogsMainCli.setVerifyAuth(client.getVerifyAuth());

			// logger.info("started connection:
			// "+(String)dialogsMainCli.getSession().get("sphereURL"));
			// putActiveConnection((String)dialogsMainCli.getSession().get("sphereURL"),dialogsMainCli);

			// return dialogsMainCli;
		} else {

			Hashtable<String, Object> newSession = new Hashtable<String, Object>();
			String sphereID = null;
			String address = null;
			String port = null;
			String homeSphere = sphereURL;
			String first = homeSphere.substring(8, homeSphere.length());

			StringTokenizer st = new StringTokenizer(first, ":");

			address = st.nextToken();
			logger.info("address: " + address);
			String portST = st.nextToken();
			logger.info("port: " + portST);
			st = new StringTokenizer(portST, ",");
			port = st.nextToken();
			sphereID = st.nextToken();

			// long long_num = System.currentTimeMillis();

			newSession.put("username", username);
			newSession.put("address", address);
			newSession.put("sphereURL", homeSphere);

			newSession.put("supra_sphere", sphereID);
			newSession.put("port", port);

			newSession.put("changePw", "false");
			newSession.put("invite", "false");
			newSession.put("localSphereId", localSphereId);

			String sessionID = (String) cli.getSession().get("session");
			String sphereId = (String) cli.getSession().get("sphere_id");

			newSession.put("externalConnection", "true");

			newSession.put("session", sessionID);

			newSession.put("sphere_id", sphereId);

			newSession.put("use_machine_verifier", "false");

			// logger.info("Doc was ind eed null....create a new one: ");
			SphereDefinitionCreator sdc = new SphereDefinitionCreator();

			String name = cli.getVerifyAuth().getDisplayName(sphereId);
			String type = cli.getVerifyAuth().getSphereType(sphereId);

			String realName = cli.getVerifyAuth().getRealName(username);

			logger.warn("real name: " + realName);
			newSession.put("real_name", realName);
			newSession.put("sphere_type", type);
			Document doc = null;
			if (sphereDoc == null) {
				doc = sdc.createDefinition(name, sphereId);
			} else {
				doc = sphereDoc;
			}

			logger.warn("getting another definitioN: " + name + " : " + type);

			logger.warn("username: " + username);
			cli.searchSphere(newSession, doc, "false");

		}

	}

	// Commented becuase NOT USED
	// public DialogsMainCli startConnection(String sphereURL,
	// boolean isInvitation, boolean fromInviteURL) {
	//
	// Hashtable<String, Object> session = new Hashtable<String, Object>();
	// String sphereID = null;
	// String address = null;
	// String port = null;
	// String homeSphere = sphereURL;
	// String first = homeSphere.substring(8, homeSphere.length());
	//
	// StringTokenizer st = new StringTokenizer(first, ":");
	//
	// address = st.nextToken();
	// logger.info("address: " + address);
	// String portST = st.nextToken();
	// logger.info("port: " + portST);
	// st = new StringTokenizer(portST, ",");
	// port = st.nextToken();
	// sphereID = st.nextToken();
	//
	// long long_num = System.currentTimeMillis();
	// String session_id = (Long.toString(long_num));
	//
	// session.put("address", address);
	// session.put("sphere_id", sphereID);
	// session.put("supra_sphere", sphereID);
	// session.put("port", port);
	// session.put("temp_session_id", session_id);
	// session.put("changePw", "false");
	// session.put("invite", "false");
	// session.put("sphereURL", homeSphere);
	// // VariousUtils.printContentsOfSession(session);
	// // if (verifier == true) {
	// // if (passphraseValue.length()>150) {
	// try {
	// final Document loginDoc = XmlDocumentUtils.load(LocationUtils
	// .getLastLoginFile());
	// if (loginDoc.getRootElement().element("prev_logins") != null) {
	// session.put("username", loginDoc.getRootElement().element(
	// "prev_logins").element("login").attributeValue(
	// "username"));
	//
	// session.put("passphrase", loginDoc.getRootElement().element(
	// "prev_logins").element("login").attributeValue(
	// "passphrase"));
	// session.put("use_machine_verifier", "false");
	// }
	// } catch (DocumentException ex) {
	// ss.common.ExceptionHandler.handleException(this, ex);
	// }
	//
	// SupraClient sClient = new SupraClient((String) session.get("address"),
	// (String) session.get("port"));
	//
	// sClient.setSupraSphereFrame(this);
	// DialogsMainCli dialogsMainCli = (DialogsMainCli) sClient
	// .startZeroKnowledgeAuth(session, "OnlyOpenConnection");
	// dialogsMainCli.setVerifyAuth(this.client.getVerifyAuth());
	//
	// logger.info("started connection: "
	// + (String) dialogsMainCli.getSession().get("sphereURL"));
	// this.getActiveConnections().putActiveConnection(
	// ((String) dialogsMainCli.getSession().get("sphereURL")),
	// dialogsMainCli);
	//
	// return dialogsMainCli;
	// }

	public void onTop() {
		if (logger.isDebugEnabled()) {
			logger.debug("on top");
		}
		Thread t = new Thread() {
			private SupraSphereFrame supra = SupraSphereFrame.this;

			public void run() {
				OsUtils.setWindowPos(this.supra.getShell());
			}
		};
		UiUtils.swtInvoke(t);
	}

	public void toFrontOnTop() {
		if (logger.isDebugEnabled()) {
			logger.debug("to front on top");
		}
		UiUtils.swtBeginInvoke(new Runnable() {
			private final SupraSphereFrame supra = SupraSphereFrame.this;

			public void run() {
				this.supra.getShell().setMinimized(false);
				this.supra.getShell().setActive();
				this.supra.getShell().setVisible(true);
			}
		});
	}

	public void hide() {
		UiUtils.swtBeginInvoke(new Runnable() {
			private SupraSphereFrame supra = SupraSphereFrame.this;

			public void run() {
				this.supra.getShell().setVisible(false);
			}
		});
	}

	// public void giveFocus() {
	//
	// this.getShell().forceFocus();
	//
	// int index = this.tabbedPane.getSelectedIndex();
	//
	// String name = this.tabbedPane.getTitleAt(index);
	//
	// String sphere_id = this.client.getVerifyAuth().getSystemName(name);
	//
	// ControlPanel controlPanel = (ControlPanel) this.cpTracker.get(name
	// + "." + sphere_id);
	//
	// // controlPanel.getSendField().grabFocus();
	// controlPanel.setFocusToSendField();
	//
	// }

	@SuppressWarnings("unchecked")
	public void registerSession(String clientName, Hashtable session) {
		this.clientSessions.put((String) session.get("supra_sphere") + "."
				+ clientName, session);

	}

	// public void registerByteRouter(String )

	public Hashtable getRegisteredSession(String supraSphereFrame,
			String clientName) {

		return (Hashtable) this.clientSessions.get(supraSphereFrame + "."
				+ clientName);

	}

	public void setFocusToSendField() {
		Shell activeShell = SDisplay.display.get().getActiveShell();
		boolean isShellActive = activeShell.equals(SupraSphereFrame.INSTANCE
				.getShell());
		if (!isShellActive) {
			return;
		}
		this.tabbedPane.setFocusToCurrentSendField();
	}

	public boolean isReplyChecked() {
		return this.tabbedPane.isReplyChecked();
	}

	public boolean isTagChecked() {
		return this.tabbedPane.isTagChecked();
	}

	public void setReplyChecked(boolean value) {
		this.tabbedPane.setReplyChecked(value);
	}

	public void setTagChecked(boolean value) {
		this.tabbedPane.setTagChecked(value);
	}

	public String getSendText() {
		MessagesPane mP = (MessagesPane) this.tabbedPane
				.getSelectedMessagesPane();
		if (mP != null && mP.getControlPanel() instanceof ControlPanel) {
			final ControlPanel controlPanel = (ControlPanel) mP
					.getControlPanel();
			return UiUtils.swtEvaluate(new Callable<String>() {
				public String call() throws Exception {
					return controlPanel.getTextOfSendField();
				}
			});
		}
		return null;
	}

	public void setSendText(final String text) {
		this.tabbedPane.setSendText(text);
	}

	public AbstractDelivery getDefaultDelivery(Hashtable session) {
		String sphereId = (String) session.get("sphere_id");

		if (logger.isDebugEnabled()) {
			logger.info("sphereId: " + sphereId);
			logger.info("queryId: " + (String) session.get("query_id"));
			logger.info("It was null: ");
		}

		MessagesPane mP = (MessagesPane) this.tabbedPane
				.getSelectedMessagesPane();

		if (mP == null || mP.isRootView()) {
			return DeliveryFactory.INSTANCE.getDeliveryTypeNormal(sphereId);
		}

		final AbstractControlPanel controlPanel = mP.getControlPanel();

		for (Enumeration enumer = this.cpTracker.keys(); enumer
				.hasMoreElements();) {
			logger.info("ENUM: " + enumer.nextElement());
		}

		return UiUtils.swtEvaluate(new Callable<AbstractDelivery>() {
			public AbstractDelivery call() throws Exception {
				return controlPanel.getDeliveryType();
			}
		});
	}

	public String getMessageType(Hashtable session) {

		MessagesPane mP = (MessagesPane) this.tabbedPane
				.getSelectedMessagesPane();
		if (mP == null) {
			logger.warn("Selected message pane is null");
			return null;
		}
		/*
		 * final ControlPanel controlPanel = (ControlPanel)
		 * this.cpTracker.get(mP .getUniqueId());
		 */
		final AbstractControlPanel controlPanel = mP.getControlPanel();

		return UiUtils.swtEvaluate(new Callable<String>() {
			public String call() throws Exception {
				return controlPanel.getDropDownCreateItem()
						.getSelectedActionName();
			}
		});
	}

	public void selectTabByMessagesPane(final MessagesPane mP) {
		UiUtils.swtBeginInvoke(new Runnable() {
			private SupraSphereFrame supra = SupraSphereFrame.this;

			public void run() {
				this.supra.tabbedPane.selectTabByPane(mP);
				this.supra.getMenuBar().checkAddRemoveEnabled();
			}
		});
	}

	public DialogsMainCli getDC(String unique) {
		DialogsMainCli cli = this.getActiveConnections().getActiveConnection(
				unique);
		return cli;
	}

	public void addJTab(final String displayName, final MessagesPane mP,
			Vector presenceInfo, final boolean isSphereWasRequested) {
		logger.info("addJTab " + displayName);
		mP.beginSetMembers(presenceInfo);

		UiUtils.swtBeginInvoke(new Runnable() {
			final String queryId = NameTranslation.returnQueryId(mP
					.getSphereDefinition());

			private SupraSphereFrame supra = SupraSphereFrame.this;

			public void run() {
				Image img = null;
				try {
					img = new Image(Display.getDefault(), getClass()
							.getResource(ImagesPaths.SPHERE).openStream());
				} catch (IOException ex) {
					logger.error(ex.getMessage(), ex);
				}
				final String sphere_id = (String) (mP.getRawSession())
						.get("sphere_id");
				logger.info("Sphere id......" + sphere_id);
				final String sphere_type = this.supra.client.getVerifyAuth()
						.getSphereType(sphere_id);
				boolean group = false;
				if (sphere_type == null) {
					group = false;
				} else if (sphere_type.equals("group")) {
					group = true;
				}
				if (sphere_type.equals("member")) {
					mP.activateP2PDeliveryCheck();
				}
				addHistory(mP);
				if (sphere_id.equals((String) (mP.getRawSession())
						.get("supra_sphere"))) {
					final SupraCTabItem tabItem = this.supra.tabbedPane.addTab(
							displayName, img, mP);
					trySelectTab(isSphereWasRequested, displayName, tabItem);
					adjustSphereDef(mP);
				} else {
					if (NameTranslation.returnQueryId(mP.getSphereDefinition()) != null) {
						logger.info("Adding here");
						try {
							Image imgs = new Image(Display.getDefault(),
									getClass().getResource(ImagesPaths.SEARCH)
											.openStream());
							final SupraCTabItem tabItem = this.supra.tabbedPane
									.addTab(displayName, imgs, mP);
							try {
								Document sphereDefinition = mP
										.getSphereDefinition();
								final String keywords = sphereDefinition
										.getRootElement().element("search")
										.element("keywords").attributeValue(
												"value");
								tabItem.safeSetToolTipText(keywords);
							} catch (Exception e) {
								// e.printStackTrace();
							}
							trySelectTab(isSphereWasRequested, displayName,
									tabItem);
							this.supra.getMenuBar().checkAddRemoveEnabled();

						} catch (Exception e) {
							logger.error(e.getMessage(), e);
						}

					} else {
						SupraCTabItem createdTab;
						if (this.queryId == null) {
							if (group) {
								createdTab = this.supra.tabbedPane.addTab(
										displayName, img, mP);
							} else {
								Image imgs = null;
								try {
									imgs = new Image(Display.getDefault(),
											getClass().getResource(
													ImagesPaths.SPHERE_PEOPLE)
													.openStream());
								} catch (IOException e) {
									logger.error("Can't load image", e);
								}
								createdTab = this.supra.tabbedPane.addTab(
										displayName, imgs, mP);
							}
							adjustSphereDef(mP);
						} else {

							Image imgs = null;
							try {
								imgs = new Image(Display.getDefault(),
										getClass().getResource(
												ImagesPaths.SEARCH)
												.openStream());
							} catch (IOException e) {
								logger.error("Can't load image: "
										+ ImagesPaths.SEARCH, e);
							}
							createdTab = this.supra.tabbedPane.addTab(
									displayName, imgs, mP);
						}
						if (createdTab != null) {
							trySelectTab(isSphereWasRequested, displayName,
									createdTab);
						}
					}
				}

				logger.info("done with adding jtab");

			}

			private void adjustSphereDef(MessagesPane mp) {
				SphereStatement sphere = mp.getSphereStatement();
				
				sphere.getBindedDocument().getRootElement().addElement("search");
				sphere.setExpiration("all");
				mp.setSphereDefinition(sphere.getBindedDocument());
			}

			private void addHistory(final MessagesPane mP) {
				mP.setSearchSphere(true);
				PreviewAreaDocking previewAreaDocking = mP
						.getPreviewAreaDocking();
				if (previewAreaDocking != null) {
					previewAreaDocking.computeAndSetToolCompSize();
				}
			}
		});
	}

	private void trySelectTab(boolean isSphereWasRequested, String displayName,
			SupraCTabItem tabItem) {
		if (isSphereWasRequested) {
			tabItem.select();
		} else {
			if (!(this.tabbedPane.isFirstOpeningFromTabOrder(displayName))) {
				tabItem.mark();
			}
		}
	}

	public SupraCTabItem addMozillaTab(final Hashtable session,
			final String title, final BrowserDataSource browserDataSource,
			final boolean selectBrowser, final BookmarkStatement bookmark) {
		/*
		 * final AtomicReference<BrowserControlPanel> item = new
		 * AtomicReference<BrowserControlPanel>();
		 * Display.getDefault().syncExec(new Runnable() { private
		 * SupraSphereFrame s = SupraSphereFrame.this; public void run() {
		 * item.set(new BrowserControlPanel(this.s,this.s.controlPanelComp)); }
		 * });
		 */
		final AtomicReference<SupraCTabItem> item = new AtomicReference<SupraCTabItem>();
		logger.warn("session : " + session);
		UiUtils.swtInvoke(new Runnable() {
			private SupraSphereFrame s = SupraSphereFrame.this;

			public void run() {

				SupraCTabItem createdTabItem = this.s.tabbedPane.loadURL(
						session, title, browserDataSource, this.s, true,
						bookmark);

				if (browserDataSource.getURL().length() > 0) {
					createdTabItem.getBrowserPane().getControlPanel()
							.getAddressField().setText(
									browserDataSource.getURL());
				}
				if (selectBrowser) {
					createdTabItem.select();
					createdTabItem.getMBrowser().setFocus();
				}
				item.set(createdTabItem);
			}
		});
		return item.get();

		// this.controlPanels.put(tab,item.get());
		// this.stackLayout.topControl = controlPanel;
		// this.controlPanelComp.layout();
	}

	public SupraCTabItem addSupraSearchTab(final Hashtable session,
			final String title, final BrowserDataSource browserDataSource,
			final boolean selectBrowser) {
		final AtomicReference<SupraCTabItem> item = new AtomicReference<SupraCTabItem>();
		logger.warn("session : " + session);
		UiUtils.swtInvoke(new Runnable() {
			private SupraSphereFrame s = SupraSphereFrame.this;

			public void run() {

				SupraCTabItem createdTabItem = this.s.tabbedPane
						.loadSupraSearchResult(session, title,
								browserDataSource, this.s);

				if (selectBrowser) {
					createdTabItem.select();
					createdTabItem.getMBrowser().setFocus();
				}
				item.set(createdTabItem);
			}
		});
		return item.get();

		// this.controlPanels.put(tab,item.get());
		// this.stackLayout.topControl = controlPanel;
		// this.controlPanelComp.layout();
	}

	public SupraCTabItem addBlankMozillaTab(final Hashtable session,
			final String title) {
		final String aboutBlank = "about:blank";
		return addSimpleMozillaTab(session, title, new SimpleBrowserDataSource(
				aboutBlank), true, aboutBlank, true);
	}

	public SupraCTabItem addSimpleMozillaTab(final Hashtable session,
			final String title, final BrowserDataSource browserDataSource,
			final boolean selectBrowser, final String addressField, final boolean highlightAddressField) {
		final AtomicReference<SupraCTabItem> item = new AtomicReference<SupraCTabItem>();

		UiUtils.swtBeginInvoke(new Runnable() {
			private SupraSphereFrame s = SupraSphereFrame.this;

			public void run() {

				SupraCTabItem createdTabItem = this.s.tabbedPane.loadURL(
						session, title, browserDataSource, this.s, false, null);

				if (selectBrowser){
					createdTabItem.select();
				}
				
				final Text addressTextField = createdTabItem.getBrowserPane().getControlPanel().getAddressField();
				if (addressField != null) {
					addressTextField.setText(addressField);
				}
				
				addressTextField.setFocus();
				
				if (highlightAddressField) {
					addressTextField.selectAll();
				}

				item.set(createdTabItem);
			}
		});
		return item.get();
	}

	public void closeFromWithin() {
		logger.warn("CLOSE APPLICATION");
		UiUtils.swtInvoke(new Runnable() {
			final SupraSphereFrame supra = SupraSphereFrame.this;

			public void run() {
				this.supra.trayItem.dispose();
				this.supra.close();
				// shells.removeAllElements();
				// shell.dispose();
			}
		});
		if (this.client != null) {
			this.client.beginClose();
		}
		LocationUtils.clean();
		ClientProtocolManager.INSTANCE.beginCloseAll();
	}

	public void disposeButDontRemove() {
		UiUtils.swtBeginInvoke(new Runnable() {
			SupraSphereFrame supra = SupraSphereFrame.this;

			public void run() {
				if (this.supra.trayItem != null) {
					this.supra.trayItem.dispose();
				}
				if (this.supra.getShell() != null) {
					this.supra.getShell().dispose();
				}
			}
		});
	}

	public void setTitle(final String title) {
		UiUtils.swtBeginInvoke(new Runnable() {
			SupraSphereFrame supra = SupraSphereFrame.this;

			public void run() {
				this.supra.getShell().setText(title);
			}
		});
	}

	public void setSession(Hashtable session) {
		this.session = session;
	}

	public void addMessagesPane(String displayName, MessagesPane messagesPane) {
		this.messagePanesController.addMessagesPane(displayName, messagesPane);
	}

	public void removeMessagesPane(String displayName, Hashtable session) {
		this.getMessagePanesController().removeMessagesPane(displayName,
				session);
	}

	public void addDragDropListener(Composite comp) {
		// Allow data to be copied or moved to the drop target
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT;
		DropTarget target = new DropTarget(comp, operations);

		// Receive data in Text or File format
		final TextTransfer textTransfer = TextTransfer.getInstance();
		final FileTransfer fileTransfer = FileTransfer.getInstance();
		Transfer[] types = new Transfer[] { fileTransfer, textTransfer };
		target.setTransfer(types);

		target.addDropListener(new DropTargetListener() {
			public void dragEnter(DropTargetEvent event) {
				if (event.detail == DND.DROP_DEFAULT) {
					if ((event.operations & DND.DROP_COPY) != 0) {
						event.detail = DND.DROP_COPY;
					} else {
						event.detail = DND.DROP_NONE;
					}
				}
				// will accept text but prefer to have files dropped
				for (int i = 0; i < event.dataTypes.length; i++) {
					if (fileTransfer.isSupportedType(event.dataTypes[i])) {
						event.currentDataType = event.dataTypes[i];
						// files should only be copied
						if (event.detail != DND.DROP_COPY) {
							event.detail = DND.DROP_NONE;
						}
						break;
					}
				}
			}

			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
				if (textTransfer.isSupportedType(event.currentDataType)) {
					// NOTE: on unsupported platforms this will return null
					String t = (String) (textTransfer
							.nativeToJava(event.currentDataType));
					if (t != null) {
						logger.info(t);
					}
				}
			}

			public void dragOperationChanged(DropTargetEvent event) {
				/*
				 * if (event.detail == DND.DROP_DEFAULT) { event.detail =
				 * (event.operations & DND.DROP_COPY) != 0) { event.detail =
				 * DND.DROP_COPY; } else { event.detail = DND.DROP_NONE; }
				 */

				// allow text to be moved but files should only be copied
				if (fileTransfer.isSupportedType(event.currentDataType)) {
					if (event.detail != DND.DROP_COPY) {
						event.detail = DND.DROP_NONE;
					}
				}
			}

			public void dragLeave(DropTargetEvent event) {
			}

			public void dropAccept(DropTargetEvent event) {
			}

			public void drop(DropTargetEvent event) {

				if (textTransfer.isSupportedType(event.currentDataType)) {
					String text = (String) event.data;
					logger.info("GOT THAT TEXT: " + text);
				}
				if (fileTransfer.isSupportedType(event.currentDataType)) {
					String[] files = (String[]) event.data;
					for (int i = 0; i < files.length; i++) {
						logger.info("FILES: " + (files[i]));
					}
				}
			}
		});
	}

	/**
	 * @param shell2
	 */
	public void disposeExternalShell(final Shell shell2) {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				shell2.dispose();
			}
		});
	}

	/**
	 * @return Returns the passController.
	 */
	public TempPasswordsController getTempPasswords() {
		return this.tempPasswords;
	}

	/**
	 * @return Returns the pendingController.
	 */
	public PendingSpheresController getPendingSpheres() {
		return this.pendingSpheres;
	}

	/**
	 * @return Returns the activeByteRoutersController.
	 */
	public ActiveByteRoutersController getActiveByteRouters() {
		return this.activeByteRouters;
	}

	/**
	 * @return Returns the messagePaneController.
	 */
	public MessagePanesController getMessagePanesController() {
		return this.messagePanesController;
	}

	/**
	 * @return Returns the activeConnectionsController.
	 */
	public ActiveConnectionsController getActiveConnections() {
		return this.activeConnections;
	}

	/**
	 * @return Returns the trayItem.
	 */
	public SupraTrayItemController getTrayItem() {
		return this.trayItem;
	}

	public CommentWindowController getCommentWindowController() {
		return this.commentController;
	}

	public SupraBrowser getActiveBrowser() {
		return this.tabbedPane.getSelectedSupraItem().getMBrowser();
//		final MessagesPane selectedMessagesPane = this.tabbedPane
//				.getSelectedMessagesPane();
//		final BrowserPane selectedBrowserPane = this.tabbedPane
//				.getSelectedBrowserPane();
//		final ExternalEmailPane selectedEmailPane = this.tabbedPane
//				.getSelectedEmailPane();
//		if (selectedMessagesPane != null) {
//			return selectedMessagesPane.getSmallBrowser();
//		} else if (selectedBrowserPane != null) {
//			return selectedBrowserPane.getBrowser();
//		} else if (selectedEmailPane != null) {
//			return selectedEmailPane.getBrowserDocking().getBrowser();
//		}
//		return null;
	}

	/**
	 * @return the display
	 */
	public Display getDisplay() {
		return this.display;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.ApplicationWindow#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		this.shellController.init(shell);
		// shell.setMinimized(true);

		String os = System.getProperty("os.name");
		if (os.startsWith("Mac")) {
			shell.setLayout(new FillLayout());
		}
		shell.layout();
		centerComponent(shell);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#createContents(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		logger.info("Creating Content for SupraSphereFrame");
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new FillLayout());

		this.trayItem = new SupraTrayItemController(this);
		this.tabbedPane = new SupraCTabTable(composite, SWT.EMBEDDED);
		addDragDropListener(this.tabbedPane);
		GridData mainEmbedData = new GridData();
		mainEmbedData.grabExcessHorizontalSpace = true;
		mainEmbedData.grabExcessVerticalSpace = true;
		mainEmbedData.verticalAlignment = GridData.FILL;
		mainEmbedData.horizontalAlignment = GridData.FILL;

		// this.tabbedPane.setLayoutData(mainEmbedData);
		// this.tabbedPane.setLayout(gridLayout);
		this.tabbedPane.setVisible(true);

		this.tabbedPane.addChangeListener(new TabChangeListener(
				this.tabbedPane, this));
		this.tabbedPane.addMouseListener(new TabMouseListener(this.session,
				this, null));

		if (getMenuBarManager() != null) {
			this.smb = new SupraMenuBar(getMenuBarManager().getMenu(), this);
		}
		logger.info("Content for SupraSphereFrame created");
		return composite;
	}

	public void initializeRootTab() {
		UiUtils.swtBeginInvoke(new Runnable() {
			public void run() {
				unsafeInitializeRootTab();
			}
		});
	}

	/**
	 * 
	 */
	private void unsafeInitializeRootTab() {
		if (this.rootTab == null) {
			this.rootTab = new RootTab(this.tabbedPane);
			this.tabbedPane.addRootTab(this.rootTab);
		}
	}

	/**
	 * 
	 */
	public void openAndBlock(Hashtable startUpSession) {
		if (logger.isDebugEnabled()) {
			logger.debug("Start up session. "
					+ MapUtils.allValuesToString(startUpSession));
		}
		boolean needOpen = startMainController(startUpSession);

		if (needOpen) {
			open();

			this.getShell().setText(
					(String) this.client.getSession().get("supra_sphere"));

			HotKeysManager.getInstance().beginMonitor(this);
			runUntilDispose();
		}
	}

	/**
	 * 
	 */
	private void runUntilDispose() {
		final Shell shell = getShell();
		final Display display = shell.getDisplay();
		if (display == null) {
			return;
		}
		try {
			while (!shell.isDisposed() && !display.isDisposed()) {
				try {
					if (!display.readAndDispatch()) {
						// If no more entries in
						// event queue
						display.sleep();
					}
				} catch (Exception ex) {
					logger.error("SWT error", ex);
				}
			}
			this.trayItem.dispose();
		} finally {
			display.dispose();
			System.exit(0);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#getShellListener()
	 */
	@Override
	protected ShellListener getShellListener() {
		return this.shellController.getShellListener();
	}

	public SupraMenuBar getMenuBar() {
		return this.smb;
	}

	public Hashtable<String, String> getFavouriteSpheres() {
		Hashtable<String, String> spheres = new Hashtable<String, String>();
		ContactStatement contact = this.client.getVerifyAuth().getContactStatement();

		for (int i = 0; i < contact.getFavouritesCount(); i++) {
			String systemName = contact.getFavourites().get(i).getSystemName();
			String displayName = contact.getFavourites().get(i)
					.getDisplayName();
			spheres.put(systemName, displayName);
		}
		logger.info("favorites : " + spheres);
		return spheres;
	}

	public ISearchable getActiveSearchable() {
		if (this.tabbedPane.getSelectedBrowserDocking() != null) {
			return this.tabbedPane.getSelectedBrowserDocking();
		} else {
			final MessagesPane selectedMessagesPane = this.tabbedPane
					.getSelectedMessagesPane();
			if (selectedMessagesPane == null) {
				logger.warn("Selected message pane is null");
				return null;
			}
			return selectedMessagesPane.getPreviewAreaDocking();
		}
	}

	public String getUserLogin() {
		final Hashtable session = this.client.getSession();
		return (String) session.get(SC.USERNAME);
	}

	/**
	 * 
	 * @return
	 */
	public Vector<String> getLargeMemberList() {
		Vector<String> members = getRootTab().getPeopleTable().getMembers();
		if ((members == null) || (members.isEmpty())) {
			logger.error("Member list is null or empty");
			return new Vector<String>();
		}
		// TODO#fix this feature by selecting memeber from root tab
		// MessagesPane main = (MessagesPane) this.tabbedPane
		// .getSelectedMessagesPane1();
		//
		// final Vector<String> memberList = main.getMembers();
		// main = (MessagesPane) this.tabbedPane.getComponentAt(0);
		// main.getPeopleTable().extractNotExistedMembers(memberList);
		return members;
	}

	/**
	 * @return
	 */
	public String getUserContactName() {
		return (String) this.client.getSession().get(SC.REAL_NAME);
	}

	/**
	 * @return
	 */
	public String getSurpaSphereId() {
		return (String) this.client.getSession().get(SC.SUPRA_SPHERE);
	}

	/**
	 * @param memberContactName
	 * @return
	 */
	public boolean selectTabByTitle(String title) {
		return this.tabbedPane.selectTabByTitle(title);
	}

	/**
	 * @return
	 */
	public Hashtable getMainRawSession() {
		if (this.mainMessagePane != null) {
			return this.mainMessagePane.getRawSession();
		} else if (this.client != null) {
			return this.client.session;
		} else {
			return this.session;
		}
	}

	/**
	 * 
	 */
	public MessagesPane getMainMessagesPane() {
		return this.tabbedPane.getComponentAt1(1);// this.mainMessagePane;
	}

	/**
	 * @return
	 */
	public VerbosedSession getMainVerbosedSession() {
		return new VerbosedSession(getMainRawSession());
	}

	/**
	 * 
	 */
	public void beforeCreateMessagesPane() {
		initializeRootTab();
	}

	/**
	 * @param client
	 */
	public void ensureClientInitialized(DialogsMainCli client) {
		if (this.client == null) {
			logger.error("Dialogs Main Client is null");
			this.client = client;
		} else if (this.client != client) {
			logger.error("Have TWO different Dialogs Main Clients");
		}
	}

	/**
	 * @return
	 */
	public SupraTab getRootTab() {
		return this.rootTab;
	}

	/**
	 * 
	 */
	public List<SupraBrowser> getAllBrowsers() {
		List<SupraBrowser> browsers = new ArrayList<SupraBrowser>();
		for (CTabItem item : this.tabbedPane.getItems()) {
			SupraBrowser sb = ((SupraCTabItem) item).getMBrowser();
			if (sb != null && !sb.isDisposed()) {
				browsers.add(sb);
			}
		}
		return browsers;
	}

	/**
	 * @param difference
	 */
	public synchronized void setTimeDifference(long difference) {
		this.timeDifference = difference;
	}

	public long getTimeDifference() {
		return this.timeDifference;
	}

	/**
	 * @return
	 */
	public boolean isPlayPopupSound() {
		return this.client.getPreferencesChecker()
				.isConfirmRecieptMessageSoundPlay();
	}

	/**
	 * @return
	 */
	public Date getCurrentDateTime() {
		Date date = new Date();

		date.setTime(date.getTime() + this.timeDifference);

		return date;
	}

	/**
	 * 
	 */
	public void closeAllTabs() {
		for (SupraCTabItem item : this.tabbedPane.getSupraItems() ) {
			if ( item.isClosable() ) { 
				item.safeClose(); 
			}
		}
		getMenuBar().checkAddRemoveEnabled();		
	}

}
