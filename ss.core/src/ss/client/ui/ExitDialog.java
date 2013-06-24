package ss.client.ui;

import java.io.IOException;
import java.util.Hashtable;
import java.util.ResourceBundle;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.eclipse.swt.SWT;
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

import ss.client.localization.LocalizationLinks;
import ss.common.UiUtils;
import ss.common.XmlDocumentUtils;
import ss.util.ImagesPaths;
import ss.util.LocationUtils;

public class ExitDialog {
	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ExitDialog.class);

	String message = null;

	String response_id = null;

	String fsep = System.getProperty("file.separator");

	SupraSphereFrame sF = null;

	Label label;

	MessagesPane mP1 = null;

	Display display;

	Button cb;

	Shell shell;

	private ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_EXITDIALOG);

	private static final String ARE_YOU_SURE = "EXITDIALOG.ARE_YOU_SURE";

	private static final String SAVE_IDENTITY_ON_THIS_COMPUTER = "EXITDIALOG.SAVE_IDENTITY_ON_THIS_COMPUTER";

	private static final String SUPRASPHERE = "EXITDIALOG.SUPRASPHERE";

	private static final String YES = "EXITDIALOG.YES";

	private static final String CANCEL = "EXITDIALOG.CANCEL";

	private static final String RESTART = "EXITDIALOG.RESTART";

	private static final String HIDE_APPLICATION = "EXITDIALOG.HIDE_APPLICATION";

	private static final String EXIT = "EXITDIALOG.EXIT";

	private static final String RECONNECT = "EXITDIALOG.RECONNECT";

	public ExitDialog(SupraSphereFrame sF) {
		this.sF = sF;
		createExitDialogBox(sF);
	}

	public ExitDialog(SupraSphereFrame sF, String message) {
		this.sF = sF;
		this.message = message;
		createNewDialogBox(message);
	}

	private void createExitDialogBox(final SupraSphereFrame sF) {

		final String desc = this.bundle.getString(ARE_YOU_SURE);
		this.display = Display.getDefault();// new Display();
		UiUtils.swtBeginInvoke(new Runnable() {

			public void run() {

				ExitDialog.this.shell = new Shell(ExitDialog.this.display);
				GridLayout gridLayout = new GridLayout();
				gridLayout.numColumns = 4;
				ExitDialog.this.shell.setLayout(gridLayout);

				Label lbl = new Label(ExitDialog.this.shell, SWT.CENTER);
				GridData labelData = new GridData();
				labelData.verticalAlignment = GridData.FILL;
				labelData.horizontalAlignment = GridData.FILL;
				labelData.horizontalSpan = 4;
				labelData.grabExcessHorizontalSpace = false;
				labelData.grabExcessVerticalSpace = false;
				lbl
						.setFont(new Font(ExitDialog.this.display, "Verdana",
								12, 12));

				lbl.setLayoutData(labelData);

				if (userExist() == false) {
					GridData saveData = new GridData();
					saveData.verticalAlignment = GridData.FILL;
					saveData.horizontalAlignment = GridData.FILL;
					saveData.horizontalSpan = 4;
					saveData.grabExcessHorizontalSpace = false;
					saveData.grabExcessVerticalSpace = false;
					ExitDialog.this.cb = new Button(ExitDialog.this.shell,
							SWT.CHECK);
					ExitDialog.this.cb.setFont(new Font(
							ExitDialog.this.display, "Verdana", 7, 7));
					ExitDialog.this.cb.setText(ExitDialog.this.bundle
							.getString(SAVE_IDENTITY_ON_THIS_COMPUTER));
					ExitDialog.this.cb.setLayoutData(saveData);

					ExitDialog.this.cb.setSelection(true);
					ExitDialog.this.cb.pack();
				}
				ExitDialog.this.shell.setSize(400, 115);

				Monitor primary = ExitDialog.this.display.getPrimaryMonitor();
				Rectangle bounds = primary.getBounds();
				Rectangle rect = ExitDialog.this.shell.getBounds();
				int x = bounds.x + (bounds.width - rect.width) / 2;
				int y = bounds.y + (bounds.height - rect.height) / 2;
				ExitDialog.this.shell.setText("SupraSphere");

//				String bdir = System.getProperty("user.dir");
				Image im;
				try {
					im = new Image(Display.getDefault(), getClass()
							.getResource(ImagesPaths.SUPRA_ICON).openStream());
					ExitDialog.this.shell.setImage(im);
				} catch (IOException ex) {
					logger.error("can't create supra icon", ex);
				}
				ExitDialog.this.shell.setLocation(x, y);
				lbl.setText(desc);
				lbl.pack();

				/*
				 * if (userExist() == false){ cb = new Button(shell, SWT.CHECK);
				 * cb.setText("Save profile on this computer?");
				 * cb.setSelection(true); cb.pack(); }
				 */

				Button exit = new Button(ExitDialog.this.shell, SWT.PUSH);
				exit
						.setFont(new Font(ExitDialog.this.display, "Verdana",
								8, 8));
				exit.setText(ExitDialog.this.bundle.getString(YES));
				exit.pack();

				Button cancel = new Button(ExitDialog.this.shell, SWT.PUSH);
				cancel.setFont(new Font(ExitDialog.this.display, "Verdana", 8,
						8));
				cancel.setText(ExitDialog.this.bundle.getString(CANCEL));
				cancel.pack();

				Button restart = new Button(ExitDialog.this.shell, SWT.PUSH);
				restart.setFont(new Font(ExitDialog.this.display, "Verdana", 8,
						8));
				restart.setText(ExitDialog.this.bundle.getString(RESTART));
				restart.pack();

				Button hide = new Button(ExitDialog.this.shell, SWT.PUSH);
				hide
						.setFont(new Font(ExitDialog.this.display, "Verdana",
								8, 8));
				hide
						.setText(ExitDialog.this.bundle
								.getString(HIDE_APPLICATION));
				hide.pack();

				ExitDialog.this.shell.pack();
				ExitDialog.this.shell.open();

				hide.addListener(SWT.Selection, new Listener() {

					public void handleEvent(Event event) {

						sF.hide();
						display_dispose();

					}

				}

				);

				exit.addListener(SWT.Selection, new Listener() {

					public void handleEvent(Event event) {
						if (!userExist()) {
							if (cb_status()) {
								saveProfile();
							}
						}
						sF.closeFromWithin();
						ExitDialog.this.shell.dispose();
						ExitDialog.this.display.dispose();
						System.exit(0);
					}
				});

				cancel.addListener(SWT.Selection, new Listener() {

					public void handleEvent(Event event) {
						display_dispose();
					}
				});

				restart.addListener(SWT.Selection, new Listener() {

					public void handleEvent(Event event) {
						sF.client.restartOnly(true);
					}
				});

				while (!ExitDialog.this.shell.isDisposed()) {
					if (!ExitDialog.this.display.readAndDispatch()) {
						ExitDialog.this.display.sleep();
					}
				}
				ExitDialog.this.shell.dispose();
			}
		});

	}

	public void display_dispose() {

		ExitDialog.this.shell.dispose();

	}

	// method to get the status of check box
	public boolean cb_status() {
		return this.cb.getSelection();
	}

	// method to save the profile to xml file
	private void saveProfile() {

		ProfileManager pm = new ProfileManager(ExitDialog.this.sF);

		pm.saveProfile(this.sF.getMainRawSession(), "Linux Desktop");
	}

	// method to check whether the user is existing or not
	private boolean userExist() {
		final Document doc;
		try {
			doc = XmlDocumentUtils.load(LocationUtils.getLastLoginFile());
		} catch (DocumentException ex) {
			logger.info("Cannot read last login file", ex);
			return false;
		}
		// String apath =
		// "//login_info/prev_logins/login[@real_name=\""+(String)mP.getSession().get("real_name")+"\"]";
		Hashtable session = this.sF.getMainRawSession();

		for (Object objItem : doc.getRootElement().element("prev_logins")
				.elements()) {
			final Element elem = (Element) objItem;
			String username = elem.attributeValue("username");
			if (username != null) {
				if (username.equals((String) session.get("username"))) {
					return true;
				}
			}
		}

		return false;
	}

	private void createNewDialogBox(String message) {

		this.display = Display.getDefault();
		this.shell = new Shell(this.display);

		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		this.shell.setLayout(gridLayout);

		Label lbl = new Label(this.shell, SWT.CENTER);
		GridData labelData = new GridData();
		labelData.verticalAlignment = GridData.FILL;
		labelData.horizontalAlignment = GridData.FILL;
		labelData.horizontalSpan = 4;
		labelData.grabExcessHorizontalSpace = false;
		labelData.grabExcessVerticalSpace = false;

		lbl.setLayoutData(labelData);
		this.shell.setSize(400, 115);

		Monitor primary = this.display.getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = this.shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		this.shell.setText(this.bundle.getString(SUPRASPHERE));

//		String bdir = System.getProperty("user.dir");
		Image im;
		try {
			im = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SUPRA_ICON).openStream());
			this.shell.setImage(im);
		} catch (IOException ex) {
			logger.error("can't create supra icon", ex);
		}
		this.shell.setLocation(x, y);

		lbl.pack();

		if (message != null) {

			lbl.setText(message);

			/*
			 * if (userExist() == false){ cb = new Button(shell, SWT.CHECK);
			 * cb.setText("Save profile on this computer?");
			 * cb.setSelection(true); cb.pack(); }
			 */

			Button exit = new Button(this.shell, SWT.PUSH);
			exit.setText(this.bundle.getString(EXIT));
			exit.pack();

			exit.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					ExitDialog.this.sF.closeFromWithin();
				}
			});

			Button reconnect = new Button(this.shell, SWT.PUSH);
			reconnect.setText(this.bundle.getString(RECONNECT));
			reconnect.pack();

			reconnect.addListener(SWT.Selection, new Listener() {

				public void handleEvent(Event event) {
					ExitDialog.this.sF.client.restartOnly(true);

				}
			});

		} else {

			Button yes = new Button(this.shell, SWT.PUSH);
			yes.setText(this.bundle.getString(YES));
			yes.pack();

			Button no = new Button(this.shell, SWT.PUSH);
			no.setText(this.bundle.getString(CANCEL));
			no.pack();

			Button restart = new Button(this.shell, SWT.PUSH);
			restart.setText(this.bundle.getString(RESTART));
			restart.pack();

			yes.addListener(SWT.Selection, new Listener() {

				public void handleEvent(Event event) {
					if (!userExist()) {
						if (cb_status()) {
							saveProfile();
						}
					}
				}
			});

			no.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					display_dispose();
				}
			});

			restart.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					ExitDialog.this.sF.client.restartOnly(true);
				}
			});

		}
		this.shell.pack();
		this.shell.open();
		while (!this.shell.isDisposed()) {
			if (!this.display.readAndDispatch()) {
				this.display.sleep();
			}
		}
		this.display.dispose();

	}

	// number of buttons *must* be even

}
