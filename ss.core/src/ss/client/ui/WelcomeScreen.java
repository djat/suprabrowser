package ss.client.ui;

/*
 * 
 * The main login screen, passes session, including passphrase, on to
 * MessagePane.java
 *  
 */

import java.io.IOException;
import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.configuration.ApplicationConfiguration;
import ss.client.configuration.LastLoginConfigurarion;
import ss.client.configuration.StartUpArgs;
import ss.client.debug.DebugConsoleWindow;
import ss.client.debug.deadlock.DeadlockMessagesListener;
import ss.client.debug.deadlock.UiDeadLockGuard;
import ss.client.install.update.FilesDownloader;
import ss.client.install.update.UpdateProtocolFactory;
import ss.client.install.update.UpdateResultObserver;
import ss.client.localization.LocalizationLinks;
import ss.common.ArgumentNullPointerException;
import ss.common.UiUtils;
import ss.common.debug.DebugUtils;
import ss.framework.install.update.ApplicationUpdater;
import ss.framework.install.update.IFilesDownloader;
import ss.framework.install.update.IUpdateProtocolFactory;
import ss.framework.install.update.UpdateProcessListener;
import ss.util.ImagesPaths;

// import load;
// This file loads the initial login screen and builds the various tabs on the
// interface upon login. With the exception of the "Memo"
// tab and the "Files" tab, the other JPanels are contained within their own
// classes

public class WelcomeScreen {

	@SuppressWarnings("unused")
	static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(WelcomeScreen.class);

	private static final String INVOKING_XALER = "WELCOMESCREEN.INVOKING_XALER";

	private static final String SPHERE_ADDRESS = "WELCOMESCREEN.SPHERE_ADDRESS";

	private static final String PASSPHRASE = "WELCOMESCREEN.PASSPHRASE";

	private static final String WELCOME_TO_THE_SUPRA_SPHERE = "WELCOMESCREEN.WELCOME_TO_THE_SUPRA_SPHERE";

	private static final String USERNAME = "WELCOMESCREEN.USERNAME";

	private static final String PLEASE_PASTE_THE_INVITATION_STRING_INTO_THE_WINDOW = "WELCOMESCREEN.PLEASE_PASTE_THE_INVITATION_STRING_INTO_THE_WINDOW";

	private static final String LOGIN = "WELCOMESCREEN.LOGIN";

	private static final String SUPRASPHERE = "WELCOMESCREEN.SUPRASPHERE";

	private ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_WELCOMESCREEN);

	boolean verifier;

	String sphereValue = null;

	boolean isChangePw = false;

	boolean changePassphraseNextLogin = false;

	String tempUsername = null;

	String loginSphere;

	String originalUsername;

	String originalPassphrase;

	String firstSessionId;

	private Label usernameLabel;

	final StartUpArgs startUpArgs;

	private final LastLoginConfigurarion loginCfg;

	private final ApplicationConfiguration connectionCfg;

	private Label passphraseLabel;

	private Text usernameText;

	private Button loginButton;

	private Button inviteLoginButton;

	private Label sphereLabel;

	private Text sphereText = null;

	private SupraSphereFrame sF = null;

	private Text passphraseText;

	private Display display;

	private Shell shell;

	private Label inviteLabel = null;

	private Text inviteText = null;

	private Label messageLabel = null;

	private Label mainLabel = null;

	private String promptPassphrase = null;

	private boolean verifierLoaded = false;

	private final UiDeadLockGuard deadLockGuard = new UiDeadLockGuard(
			new DeadlockMessagesListener());

	public WelcomeScreen(StartUpArgs startUpArgs) {
		if (startUpArgs == null) {
			throw new ArgumentNullPointerException("startUpArgs");
		}
		this.startUpArgs = startUpArgs;
		this.connectionCfg = loadConnectionCgf();
		if ( this.startUpArgs.isPortDefined() ) {
			this.connectionCfg.setPort( this.startUpArgs.getPort() );
			this.connectionCfg.save();
		}
		this.loginCfg = loadLoginCfg();
		if (this.startUpArgs.isShowInvitationUi() || this.connectionCfg.isShowInvitationUi() ) {
			layoutInvitation();
		} else {
			createDisplay();
			layoutGUI();
		}
	}

	/**
	 * @return
	 */
	private LastLoginConfigurarion loadLoginCfg() {
		try {
			return LastLoginConfigurarion.loadUserConfiguration();
		} catch (RuntimeException ex) {
			this.startUpArgs.setAutoLogin(false);
			return LastLoginConfigurarion.createBlankUserConfiguration();
		}
	}

	private ApplicationConfiguration loadConnectionCgf() {
		try {
			return ApplicationConfiguration.loadUserConfiguration();
		} catch (RuntimeException ex) {
			this.startUpArgs.setAutoLogin(false);
			return ApplicationConfiguration.createBlankUserConfiguration();
		}
	}

	public void loadVerifierOnly() {
		this.verifierLoaded = true;
		try {
			this.loginCfg.reload();
		} catch (Exception e) {
			this.startUpArgs.setAutoLogin(false);
		}
	}

	public void setFirstSessionId(String sessionId) {
		this.firstSessionId = sessionId;
	}

	public void saveNewUrl(String sphereURL) {
		this.connectionCfg.setConnectionUrl(sphereURL);
		this.connectionCfg.save();
	}

	public void setPromptPassphrase(String text) {
		this.promptPassphrase = text;
	}

	private void layoutInvitation() {
		logger.info("layoutInvitation");
		this.display = SDisplay.display.get();
		this.deadLockGuard.start(this.display);

		this.shell = new Shell(this.display);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		this.shell.setLayout(gridLayout);

		this.inviteLabel = new Label(this.shell, SWT.NONE);
		this.inviteLabel.setText(this.bundle
				.getString(PLEASE_PASTE_THE_INVITATION_STRING_INTO_THE_WINDOW));

		GridData labelData = new GridData();

		labelData.verticalAlignment = GridData.FILL;
		labelData.horizontalAlignment = GridData.FILL;

		labelData.horizontalSpan = 1;
		labelData.grabExcessHorizontalSpace = false;
		labelData.grabExcessVerticalSpace = false;

		this.inviteLabel.setLayoutData(labelData);

		this.inviteText = new Text(this.shell, SWT.BORDER);
		this.inviteText.setSize(100, 15);

		GridData textData = new GridData();

		textData.verticalAlignment = GridData.FILL;
		textData.horizontalAlignment = GridData.FILL;

		textData.horizontalSpan = 3;
		textData.grabExcessHorizontalSpace = true;
		textData.grabExcessVerticalSpace = false;

		this.inviteText.setLayoutData(textData);

		this.inviteLoginButton = new Button(this.shell, SWT.PUSH);
		this.inviteLoginButton.setText(this.bundle.getString(LOGIN));

		this.inviteText.addKeyListener(new KeyAdapter() {
			private WelcomeScreen welcome = WelcomeScreen.this;

			public void keyPressed(KeyEvent evt) {

				if (evt.character == SWT.CR) {
					this.welcome.setUser("username");
					this.welcome.setPass("passphrase");
					this.welcome.sphereValue = this.welcome.inviteText
							.getText();

					logger.info("spherevalue: " + this.welcome.sphereValue);

					StringTokenizer st = new StringTokenizer(
							this.welcome.sphereValue, ",");
					st.nextToken();
					String sphereMessageId = st.nextToken();
					st = new StringTokenizer(sphereMessageId, ".");
					st.nextToken();
					String messageId = st.nextToken();
					this.welcome.setUser(messageId);
					this.welcome.setPass(messageId);
					this.welcome.doLoginAction();
				}
			}
		});
		this.inviteLoginButton.addListener(SWT.Selection, new Listener() {
			private WelcomeScreen welcome = WelcomeScreen.this;

			public void handleEvent(Event event) {

				this.welcome.setUser("username");
				this.welcome.setPass("passphrase");
				this.welcome.sphereValue = this.welcome.inviteText.getText();
				logger.info("spherevalue: " + this.welcome.sphereValue);

				StringTokenizer st = new StringTokenizer(
						this.welcome.sphereValue, ",");

				st.nextToken();

				String sphereMessageId = st.nextToken();
				st = new StringTokenizer(sphereMessageId, ".");

				st.nextToken();

				String messageId = st.nextToken();

				this.welcome.setUser(messageId);
				this.welcome.setPass(messageId);

				this.welcome.doLoginAction();

			}
		});
		this.shell.setSize(400, 115);
		Monitor primary = this.display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = this.shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		this.shell.setLocation(x, y);
		this.shell.setVisible(true);
		runEventLoop();
	}

	/**
	 * @param value
	 */
	protected void setPass(String value) {
		this.loginCfg.setPass(value);
	}

	public void disposeInvitation() {
		UiUtils.swtBeginInvoke(new Runnable() {
			private WelcomeScreen welcome = WelcomeScreen.this;

			public void run() {
				try {
					this.welcome.inviteText.dispose();
					this.welcome.inviteLoginButton.dispose();
					this.welcome.inviteLabel.dispose();
				} catch (Exception e) {
					logger.error("disposeInvitation failed", e);
				}
			}
		});
	}

	public void setIsChangePw(boolean value) {
		this.isChangePw = value;
	}

	public void setTempUsername(String tempUsername) {
		this.tempUsername = tempUsername;
	}

	public void setLoginSphere(String loginSphere) {
		this.loginSphere = loginSphere;
	}

	public void createDisplay() {
		if (logger.isDebugEnabled()) {
			logger.debug("createDisplay");
		}
		this.display = SDisplay.display.get();
		this.deadLockGuard.start(this.display);

		this.shell = new Shell(this.display);
		this.shell.setText(this.bundle.getString(SUPRASPHERE));

		// InputStream imageInputStream = Thread.currentThread()
		// .getContextClassLoader().getResourceAsStream(
		// WelcomeScreen.SUPRA_ICON_FILENAME);

		// / String bdir = System.getProperty("user.dir");
		Image im;
		try {
			im = new Image(Display.getDefault(), getClass().getResource(
					ImagesPaths.SUPRA_ICON).openStream());
			this.shell.setImage(im);
		} catch (IOException ex) {
			logger.error("can't create supra icon", ex);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("createDisplay finished");
		}
	}

	public void setSupraSphereFrame(SupraSphereFrame sF) {
		this.sF = sF;
	}

	public void layoutGUI() {
		if (logger.isDebugEnabled()) {
			logger.debug("layoutGUI started. Called from: "
					+ DebugUtils.getCurrentStackTrace());
		}
		try {
			UiUtils.swtInvoke(new Runnable() {
				public void run() {
					WelcomeScreen.this.layoutGUIBody();
				}
			});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void showMessage(String message) {
		try {
			if (this.messageLabel != null) {
				this.messageLabel.dispose();
			}
			this.messageLabel = new Label(this.shell, SWT.NONE);
			this.messageLabel.setFont(new Font(this.display, "Verdana", 7, 9));
			this.messageLabel.setText(message);
			GridData messageData = new GridData();
			messageData.verticalAlignment = GridData.CENTER;
			messageData.horizontalAlignment = GridData.CENTER;
			messageData.grabExcessHorizontalSpace = true;
			messageData.grabExcessVerticalSpace = true;
			this.messageLabel.setLayoutData(messageData);

			this.shell.layout();
		} catch (Exception e) {
		}
	}

	public void disposeAll() {
		UiUtils.swtInvoke(new Runnable() {
			public void run() {
				WelcomeScreen.this.disposeAllBody();
			}
		});
	}

	public void setSphereText(String text) {
		this.sphereText.setText(text);
	}

	public void setUsernameText(String text) {
		this.usernameText.setText(text);
	}

	public void setPassphraseText(String text) {
		this.passphraseText.setText(text);
	}

	private void addFocusListeners() {
		if (!this.startUpArgs.isAutoLogin()) {
			this.passphraseText.addFocusListener(new FocusListener() {

				private WelcomeScreen welcome = WelcomeScreen.this;

				public void focusGained(FocusEvent arg0) {
					this.welcome.passphraseText.selectAll();
				}

				public void focusLost(FocusEvent arg0) {
				}

			});

			this.usernameText.addFocusListener(new FocusListener() {
				private WelcomeScreen welcome = WelcomeScreen.this;

				public void focusGained(FocusEvent arg0) {
					this.welcome.usernameText.selectAll();
				}

				public void focusLost(FocusEvent arg0) {
				}
			});
		}
	}

	public void doLoginAction() {
		final SessionBuilder sessionBuilder = new SessionBuilder(this);
		final Hashtable<String, String> session = sessionBuilder.getResult();
		// FIXME session test: Can user login or not?
		final UpdateProcessListener updateProccessListener = new UpdateResultObserver(
				session, this);
		final IUpdateProtocolFactory updateProtocolFactory = new UpdateProtocolFactory(
				session);
		final IFilesDownloader filesDownloader = new FilesDownloader(session);
		ApplicationUpdater updater = new ApplicationUpdater(
				updateProccessListener, updateProtocolFactory, filesDownloader);
		updater.start();
	}

	public void closeFromWithin() {
		Thread t = new Thread() {
			@Override
			public void run() {
				WelcomeScreen.this.shell.dispose();
			}
		};
		UiUtils.swtInvoke(t);
	}

	public void runEventLoop() {
		while (!this.shell.isDisposed()) {
			if (!this.display.readAndDispatch()) {
				this.display.sleep();
			}
		}
		this.display.dispose();
	}

	/**
	 * @param string
	 */
	public void setInitialSphereUrl(String value) {
		this.connectionCfg.setConnectionUrl( value);
	}

	public void setChangePassphraseNextLogin(boolean b) {
		this.changePassphraseNextLogin = b;
	}

	public void setOriginalUsernameBeforeChange(String originalUsername) {
		this.originalUsername = originalUsername;
	}

	public void setOriginalPassphraseBeforeChange(String originalPassphrase) {
		this.originalPassphrase = originalPassphrase;
	}

	/**
	 * 
	 */
	private void layoutGUIBody() {
		if (logger.isDebugEnabled()) {
			logger.debug("layoutGUIBody started");
		}
		if (this.messageLabel != null) {
			this.messageLabel.dispose();
		}

		this.mainLabel = new Label(this.shell, SWT.NONE);
		if (this.promptPassphrase != null) {
			// mainLabel.setText("Please supply your preferred
			// username and passphrase");
			this.mainLabel.setText(this.promptPassphrase);
		} else {
			this.mainLabel.setText(this.bundle
					.getString(WELCOME_TO_THE_SUPRA_SPHERE));
		}

		this.mainLabel.setFont(new Font(this.display, "Verdana", 7, 9));

		GridData mainData = new GridData();
		// mainData.horizontalAlignment = GridData.FILL;
		// /mainData.verticalAlignment = GridData.CENTER;
		mainData.horizontalAlignment = GridData.BEGINNING;

		mainData.horizontalSpan = 5;
		mainData.grabExcessHorizontalSpace = false;
		mainData.grabExcessVerticalSpace = false;

		this.mainLabel.setLayoutData(mainData);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		this.shell.setLayout(gridLayout);

		this.usernameLabel = new Label(this.shell, SWT.NONE);
		this.usernameLabel.setText(this.bundle.getString(USERNAME));
		this.usernameText = new Text(this.shell, SWT.BORDER);

		// usernameText.setSize(100,15);
		if (this.getUser() != null && this.promptPassphrase == null) {
			this.usernameText.setText(this.getUser());
		} else if (this.promptPassphrase != null) {
			if (this.promptPassphrase
					.equals("Please choose a *different* username and passphrase")
					&& this.getUser() != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("user name" + this.getUser());
				}
				this.usernameText.setText(this.getUser());
				this.usernameText.setEditable(false);
			}
		} else if (this.verifierLoaded) {
			this.usernameText.setText(this.getUser());
		}

		// GridData gridData = new GridData(GridData.VERTICAL_ALIGN_FILL
		// | GridData.HORIZONTAL_ALIGN_FILL);

		GridData userData = new GridData();

		// gridData.verticalAlignment = GridData.FILL;
		userData.horizontalAlignment = GridData.FILL;

		userData.horizontalSpan = 4;
		userData.grabExcessHorizontalSpace = true;
		userData.grabExcessVerticalSpace = false;

		this.usernameText.setLayoutData(userData);

		this.passphraseLabel = new Label(this.shell, SWT.NONE);
		this.passphraseLabel.setText(this.bundle.getString(PASSPHRASE));

		this.passphraseText = new Text(this.shell, SWT.BORDER | SWT.PASSWORD);

		GridData passphraseData = new GridData();

		passphraseData.verticalAlignment = GridData.FILL;
		passphraseData.horizontalAlignment = GridData.FILL;

		passphraseData.horizontalSpan = 4;

		passphraseData.grabExcessHorizontalSpace = false;
		passphraseData.grabExcessVerticalSpace = false;

		this.passphraseText.setLayoutData(passphraseData);

		// long longnum = System.currentTimeMillis();

		// if (!isChangePw) {

		this.sphereLabel = new Label(this.shell, SWT.NONE);
		this.sphereLabel.setText(this.bundle.getString(SPHERE_ADDRESS));

		GridData sphereLabelData = new GridData();

		sphereLabelData.verticalAlignment = GridData.CENTER;
		sphereLabelData.horizontalAlignment = GridData.BEGINNING;

		sphereLabelData.horizontalSpan = 1;
		sphereLabelData.grabExcessHorizontalSpace = false;
		sphereLabelData.grabExcessVerticalSpace = false;

		this.sphereLabel.setLayoutData(sphereLabelData);

		// ToolBar toolBar = new ToolBar(shell,SWT.NONE);
		// toolBar.setLayoutData(loginData);

		this.sphereText = new Text(this.shell, SWT.BORDER);
		if (this.sphereText == null) {

		}
		this.sphereText.setText(this.getInitialSphereUrl() );

		GridData sphereData = new GridData();
		sphereData.verticalAlignment = GridData.FILL;
		sphereData.horizontalAlignment = GridData.FILL;

		sphereData.horizontalSpan = 3;
		sphereData.widthHint = 220;
		sphereData.grabExcessHorizontalSpace = true;
		sphereData.grabExcessVerticalSpace = false;

		this.sphereText.setLayoutData(sphereData);

		// }
		GridData loginData = new GridData();
		loginData.horizontalAlignment = GridData.END;
		loginData.verticalAlignment = GridData.FILL;
		loginData.horizontalSpan = 1;
		loginData.grabExcessVerticalSpace = true;
		loginData.grabExcessHorizontalSpace = false;

		this.loginButton = new Button(this.shell, SWT.PUSH);
		this.loginButton.setText(this.bundle.getString(LOGIN));
		this.loginButton.setLayoutData(loginData);

		this.usernameText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {
				if (evt.character == SWT.CR) {
					WelcomeScreen.this.passphraseText.setFocus();
				}
				if (evt.character == '~'
						&& "debug".equals(WelcomeScreen.this.usernameText
								.getText())) {
					DebugConsoleWindow consoleWnd = new DebugConsoleWindow();
					consoleWnd.open();
				}
			}

		});

		this.passphraseText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent evt) {

				if (evt.character == SWT.CR) {
					disposeAll();
					showMessage(WelcomeScreen.this.bundle
							.getString(INVOKING_XALER));
					doLoginAction();
				}
			}
		});

		this.loginButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				disposeAll();
				showMessage(WelcomeScreen.this.bundle.getString(INVOKING_XALER));
				doLoginAction();
			}
		});

		this.shell.setSize(425, 140);

		Monitor primary = this.display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();

		Rectangle rect = this.shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		this.shell.setLocation(x, y);

		this.shell.pack();
		this.shell.layout();
		this.shell.setVisible(true);

		this.usernameText.setFocus();
		this.usernameText.selectAll();

		if (this.loginCfg.getPass() != null && this.promptPassphrase == null) {
			this.passphraseText.setText(this.loginCfg.getPass());
		} else if (this.verifierLoaded == true) {
			this.passphraseText.setText(this.loginCfg.getPass());
		}

		if (this.sF != null) {
			this.sF.closeFromWithin();
		}

		if (this.startUpArgs.isAutoLogin()) {
			disposeAll();
			showMessage(this.bundle.getString(INVOKING_XALER));
			doLoginAction();

		} else {

		}
		if (logger.isDebugEnabled()) {
			logger.debug("layoutGUIBody finished");
		}
		addFocusListeners();
		runEventLoop();
	}

	/**
	 * @return
	 */
	String getInitialSphereUrl() {
		return this.connectionCfg.getConnectionUrl();
	}

	/**
	 * 
	 */
	private void disposeAllBody() {
		if (this.mainLabel != null) {
			this.mainLabel.dispose();
		}
		this.setPass( this.passphraseText.getText() );
		this.passphraseText.dispose();
		this.passphraseLabel.dispose();
		this.setUser(this.usernameText.getText());
		this.usernameText.dispose();
		this.usernameLabel.dispose();
		this.sphereLabel.dispose();
		this.sphereValue = this.sphereText.getText();
		this.sphereText.dispose();
		this.loginButton.dispose();
	}

	/**
	 * @return
	 */
	public Shell getShell() {
		return this.shell;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	void setUser(String user) {
		if (logger.isDebugEnabled()) {
			logger.debug("new user value : " + user + " "
					+ DebugUtils.getCurrentStackTrace());
		}
		if (user == null || user.trim().equals("")) {
			return;
		}
		this.loginCfg.setUser(user);
	}

	/**
	 * @return the user
	 */
	String getUser() {
		return this.loginCfg.getUser();
	}

	/**
	 * @return
	 */
	public String getPass() {
		return this.loginCfg.getPass();
	}

	/**
	 * @return
	 */
	public String getProfileId() {
		return this.loginCfg.getProfileId();
	}

	/**
	 * @return
	 */
	public boolean isRememberedUserNameAndPass() {
		return this.loginCfg.isUserNameAndPasswordLoaded();
	}

}
