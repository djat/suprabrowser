package ss.client.ui.messagedeliver.popup;

/*
 *  This is a popup dialog that takes over the screen. It is activated when a message is sent with
 *  the option "Confirm Receipt" set.
 */

import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.DeviceData;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ss.client.event.SOptionPaneButtonListener;
import ss.client.localization.LocalizationLinks;
import ss.client.networking.DialogsMainCli;
import ss.client.preferences.PreferencesChecker;
import ss.client.ui.SupraSphereFrame;
import ss.common.OsUtils;
import ss.common.UiUtils;
import ss.domainmodel.FileStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.TerseStatement;
import ss.domainmodel.workflow.DeliveryFactory;
import ss.global.SSLogger;
import ss.util.VariousUtils;

/**
 * Description of the Class
 * 
 * @author david
 * @created December 21, 2003
 */
public class SOptionPane extends Dialog {

	private Shell shell;

	private String message = "";

	private Document doc = null;

	private Statement statement = null;

	private Text tf = null;

	private String linktext = null;

	// /new String("http://www.suprasphere.com");
	private String type = null;

	private final Display display;

	private Shell currentShell = null;

	private GridData gridData = null;

	private final DialogsMainCli client;

	private SOptionPaneChoicePane choicePane = null;

	private ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_SOPTIONPANE);

	private static final Logger logger = SSLogger.getLogger(SOptionPane.class);

	private static final String SIM = "SOPTIONPANE.SIM";

	private static final String FWD_SIM_FROM = "SOPTIONPANE.FWD_SIM_FROM";

	private static final String SIM_FROM = "SOPTIONPANE.SIM_FROM";

	private static final String MOMENT = "SOPTIONPANE.MOMENT";

	@SuppressWarnings("unused")
	private static final String SITE = "SOPTIONPANE.SITE";

	private static final String FILE = "SOPTIONPANE.FILE";

	private static final String SUBJECT = "SOPTIONPANE.SUBJECT";

	private static final String RE = "SOPTIONPANE.RE";

	private static final String ACKNOWLEDGE = "SOPTIONPANE.ACKNOWLEDGE";

	boolean once = false;

	private final Hashtable session;

	/**
	 * Constructor for the SOptionPane object
	 * 
	 * @param doc
	 *            Description of the Parameter
	 * @param mp
	 *            Description of the Parameter
	 * @param main
	 *            Description of the Parameter
	 */
	public SOptionPane( Document doc, Hashtable paneSession ) {
		super(SupraSphereFrame.INSTANCE.getShell());

		this.client = SupraSphereFrame.INSTANCE.client;
		this.display = Display.getDefault();

		this.doc = doc;
		this.session = paneSession;
		this.statement = Statement.wrap(doc);
		this.type = this.statement.getType();

		String dir = System.getProperty("user.dir");

		System.setProperty("java.library.path", dir);
	}

	/**
	 * Description of the Method
	 */
	public void openSleak() {

		DeviceData data = new DeviceData();
		data.tracking = true;

	}

	/**
	 * Sets the doc attribute of the SOptionPane object
	 * 
	 * @param doc
	 *            The new doc value
	 */
	public void setDoc(Document doc) {

		this.doc = doc;
		this.statement = Statement.wrap(doc);
		this.type = this.statement.getType();

		this.once = true;

	}

	/**
	 * Description of the Method
	 */

	public void closeFromWithin() {

		synchronized (PopUpController.INSTANCE.popupsMutex) {
			logger.info("Closing from withing");
			Thread t = new Thread() {
				public void run() {
					try {
						SOptionPane.this.currentShell.dispose();
					} catch (NullPointerException ex) {
						logger.error("current shell is disposed");
					}
				}
			};
			UiUtils.swtBeginInvoke(t);
		}
	}

	protected void configureShell(final Shell shell) {
		shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent de) {
				PopUpController.INSTANCE.closedOptionPane();
			}
		});

		shell.setSize(450, 450);

		shell.setLayout(new GridLayout());

		Monitor primary = SOptionPane.this.display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;

		if (PopUpController.INSTANCE.getCurrent_popup() == null) {
			shell.setLocation(x, y);
		} else {
			shell.setLocation(PopUpController.INSTANCE.getCurrent_popup());
		}

		shell.setText(this.bundle.getString(SOptionPane.SIM));
		shell.layout();
		shell.setFocus();


		shell.setVisible(true);
	}

	/**
	 * Description of the Method
	 */
	protected Control createContents(Composite parent) {
		try {
			if (this.statement.isTerse()) {
				this.message = this.statement.getSubject();
			} else if (this.statement.isBookmark()) {

				this.linktext = this.statement.getAddress();

				if (this.statement.getBody().length() > 0) {
					this.message = this.statement.getBody();
				} else {
					this.message = this.statement.getSubject();
				}

			} else if (this.statement.isFile()) {
				String internal = (FileStatement.wrap(this.statement
						.getBindedDocument())).getDataId();

				StringTokenizer st = new StringTokenizer(internal, "_____");
				st.nextToken();
				this.linktext = st.nextToken();

				if (this.statement.getBody().length() > 0) {
					this.message = this.statement.getBody();
				} else {
					this.message = this.statement.getSubject();
				}

			} else {
				if (this.statement.getBody().length() > 0) {
					this.message = this.statement.getBody();
				} else {
					this.message = this.statement.getSubject();
				}
			}

			parent.setLayoutData(new GridData(GridData.FILL_BOTH));

			GridLayout layout = new GridLayout();
			layout.numColumns = 2;
			layout.makeColumnsEqualWidth = false;
			parent.setLayout(layout);

			String sphere = this.statement.getCurrentSphere();

			String display_name = this.client.getVerifyAuth().getDisplayName(
					sphere);

			final Label one = new Label(parent, SWT.NONE);
			this.gridData = new GridData();
			one.setLayoutData(this.gridData);

			if (this.statement.getForwardedBy() != null) {

				one.setText(this.bundle.getString(SOptionPane.FWD_SIM_FROM));

			} else {
				one.setText(this.bundle.getString(SOptionPane.SIM_FROM));
			}

			final Label author = new Label(parent, SWT.NONE);
			this.gridData = new GridData();
			author.setLayoutData(this.gridData);

			author.setText(this.statement.getGiver() + " @ " + display_name);

			FontData fontData = one.getFont().getFontData()[0];
			fontData.setStyle(SWT.BOLD | fontData.getStyle());

			Font font = new Font(this.display, fontData);
			author.setFont(font);

			final Label moment = new Label(parent, SWT.NONE);
			moment.setText(this.bundle.getString(SOptionPane.MOMENT));
			this.gridData = new GridData();
			moment.setLayoutData(this.gridData);

			final Label time = new Label(parent, SWT.NONE);
			time.setText(this.statement.getMoment());
			this.gridData = new GridData();
			time.setLayoutData(this.gridData);

			if (this.statement.isBookmark() || this.statement.isFile()) {
				Label site = new Label(parent, SWT.NONE);
				this.gridData = new GridData();
				site.setLayoutData(this.gridData);

				if (this.statement.isBookmark()) {
					site.setText(this.bundle.getString(SOptionPane.MOMENT));
				} else {
					site.setText(this.bundle.getString(SOptionPane.FILE));
				}
				Label link = new Label(parent, SWT.BOLD);
				this.gridData = new GridData();
				link.setLayoutData(this.gridData);
				Font f = link.getFont();
				FontData fd = f.getFontData()[0];
				// fd.data.lfUnderline = 1;
				link.setFont(new Font(link.getDisplay(), fd));

				int standardColor = SWT.COLOR_BLUE;

				link.setForeground(link.getDisplay().getSystemColor(
						standardColor));
				link.setText(this.linktext);
				// link.addMouseTrackListener(new Link(display,
				// linktext, so));
				// link.addMouseListener(new Link(display, linktext,
				// so));

			}

			if (this.statement.isBookmark() || this.statement.isMessage()
					|| this.statement.isReply()) {

				if (this.statement.getBody().length() > 0) {
					Label sub = new Label(parent, SWT.NONE);
					this.gridData = new GridData();
					sub.setLayoutData(this.gridData);
					sub.setText(this.bundle.getString(SOptionPane.SUBJECT));

					Label two = new Label(parent, SWT.NONE);
					this.gridData = new GridData();
					two.setLayoutData(this.gridData);
					two.setText(this.statement.getSubject());
					two.setFont(font);
				}
			} else if (this.type.equals("terse")) {

				String resp = this.statement.getResponseId();

				if (resp != null) {
					String orig = this.doc.getRootElement().element("body")
							.element("orig_body").getText();

					if (orig.length() > 50) {

						orig = orig.substring(0, 50 - 1) + "...";

					}

					Label sub = new Label(parent, SWT.NONE);
					this.gridData = new GridData();
					sub.setLayoutData(this.gridData);
					sub.setText(this.bundle.getString(SOptionPane.SUBJECT));

					Label two = new Label(parent, SWT.NONE);
					this.gridData = new GridData();
					two.setLayoutData(this.gridData);
					two.setText(this.bundle.getString(SOptionPane.RE) + " "
							+ orig);
					two.setFont(font);

				}
			}
			this.gridData = new GridData();

			final Text status = new Text(parent, SWT.WRAP
					| SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL);
			status.setSize(150, 150);
			status.setBackground(new Color(null, 255, 255, 255));

			this.gridData = new GridData(GridData.VERTICAL_ALIGN_FILL
					| GridData.HORIZONTAL_ALIGN_FILL);
			status.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			this.gridData = new GridData();

			this.gridData.verticalAlignment = GridData.FILL;
			this.gridData.horizontalAlignment = GridData.FILL;

			this.gridData.horizontalSpan = 2;
			this.gridData.grabExcessHorizontalSpace = true;
			this.gridData.grabExcessVerticalSpace = true;

			status.setLayoutData(this.gridData);
			status.setText(this.message);

//			if (this.statement.isComment() || this.statement.isReply()) {
//				StyleRange styleRange = new StyleRange();
//				styleRange.start = 0;
//				styleRange.length = this.message.length();
//				styleRange.foreground = this.display
//						.getSystemColor(SWT.COLOR_BLUE);
//
//				this.message = this.message
//						+ "\n\n-----Comment Below-----\n"
//						+ this.doc.getRootElement().element("body").element(
//								"comment").getText();
//				status.setText(this.message);
//				status.setStyleRange(styleRange);
//			} else {
//				status.setText(this.message);
//			}

			if (DeliveryFactory.INSTANCE.isTextInputReply(this.statement
					.getDeliveryType())) {
				createInputField(parent);
			} else {
				this.choicePane = new SOptionPaneChoicePane(parent);
			}

			final Button button = new Button(parent, SWT.PUSH);

			button.setText(SOptionPane.this.bundle
					.getString(SOptionPane.ACKNOWLEDGE));

			SOptionPaneButtonListener performListener = new SOptionPaneButtonListener(
					this);
			button.addListener(SWT.Selection, performListener);

			if (this.tf != null) {
				this.tf.addKeyListener(performListener);
			}

			// boolean returnFocus = false;
			// if (main.sF.hasFocus()) {
			// tf.setFocus();
			// returnFocus = true;
			// }

			

			if (this.tf != null)
				this.tf.setFocus();
		} catch (Exception e) {

			logger.error("problem in so : " + e.getMessage(), e);
		}
		parent.layout();
		getShell().layout();
    processBringToTop(getShell(), tf);

		getShell().setFocus();

		return parent;
	}

	/**
	 * Description of the Method
	 * 
	 * @return Description of the Return Value
	 */
	public Document messageDoc() {

		TerseStatement terseSt = new TerseStatement();
		final String contact = SupraSphereFrame.INSTANCE.client.getContact();

		terseSt.setGiver(contact);
		terseSt.setGiverUsername(SupraSphereFrame.INSTANCE.client.getLogin());

		if (this.tf != null) {
			terseSt.setSubject(this.tf.getText());
		} else {
			terseSt.setSubject(terseSt.getGiver() + ": " + this.getChoice());
			terseSt.setBody(this.getChoice());
		}

		terseSt.setLastUpdatedBy(contact);
		terseSt.setType("terse");
		terseSt.setThreadType(this.statement.getThreadType());
		terseSt.setResponseId(this.statement.getMessageId());
		terseSt.setThreadId(this.statement.getThreadId());
		terseSt.setMessageId(VariousUtils.createMessageId());
		terseSt.setOriginalId(terseSt.getMessageId());

		terseSt.setOrigBody(this.message);

		terseSt.setVersion("3000");
		terseSt.setVotingModelDesc("Absolute without qualification");
		terseSt.setVotingModelType("absolute");
		terseSt.setTallyNumber("0.0");
		terseSt.setTallyValue("0.0");

		return terseSt.getBindedDocument();
	}

	/**
	 * Description of the Method
	 * 
	 * @param title
	 *            Description of the Parameter
	 * @return Description of the Return Value
	 */
	public boolean showShell(String title) {
		return true;
		/*
		 * if (title == null) { return false; } TCHAR windowClass = new TCHAR(0,
		 * "SWT_Window0", true); TCHAR lpWindowName = new TCHAR(0, title, true);
		 * int hWnd = OS.FindWindow(windowClass, lpWindowName);
		 * 
		 * //hWnd |= 0x01; //} else { //if (OS.IsIconic(hWnd))
		 * 
		 * 
		 * OS.ShowWindow(hWnd, OS.SC_RESTORE);
		 * 
		 * OS.BringWindowToTop(hWnd); return OS.SetForegroundWindow(hWnd);
		 * //return OS.BringWindowToTop (hWnd);
		 * 
		 */
	}

	private void createInputField(final Composite comp) {
		this.tf = new Text(comp, SWT.BORDER);
		this.tf.setBackground(new Color(null, 255, 255, 255));
		this.tf.setSize(12, 100);

		this.gridData = new GridData();

		this.gridData.verticalAlignment = GridData.FILL;
		this.gridData.horizontalAlignment = GridData.FILL;

		this.gridData.horizontalSpan = 2;
		this.gridData.grabExcessHorizontalSpace = true;
		this.gridData.grabExcessVerticalSpace = false;

		this.tf.setLayoutData(this.gridData);
	}

	public Text getInputField() {
		return this.tf;
	}

	public Statement getStatement() {
		return this.statement;
	}

	public Document getDoc() {
		return this.doc;
	}

	public Display getDisplay() {
		return this.display;
	}

	public String getChoice() {
		if (this.choicePane != null)
			return this.choicePane.getChoice();
		return null;
	}

	private void processBringToTop(Shell shell, Text tf) {
		PreferencesChecker checker = null;
		try {
			checker = this.client.getPreferencesChecker();
		} catch (Exception ex) {
			logger.error(ex);
			try {
				checker = SupraSphereFrame.INSTANCE.client
						.getPreferencesChecker();
			} catch (Exception ex1) {
				logger.error(ex1);
			}
		}
		if (checker == null)
			return;
		if (checker.isPopUpOnTop()) {
			logger.warn("pop up on top of all applications");
			//setShellStyle(getShellStyle() | SWT.ON_TOP);
      OsUtils.restoreWindow(shell, tf);
		} else {
			logger.warn("pop up on top of current application only");
		}
	}

	/**
	 * @return
	 */
	public Hashtable getMessagesPaneSession() {
		return this.session;
	}

//	@Override
//	public boolean close() {
//		final boolean result = super.close();
//		PopUpController.INSTANCE.closedOptionPane();
//		return result;
//	}
}
