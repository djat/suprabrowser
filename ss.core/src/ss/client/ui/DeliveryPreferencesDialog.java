/*
 * Created on Feb 28, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ss.client.ui;

import java.io.IOException;
import java.util.ResourceBundle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ss.client.localization.LocalizationLinks;
import ss.util.ImagesPaths;

public class DeliveryPreferencesDialog extends BaseDialog {

	String bdir = System.getProperty("user.dir");

	String fsep = System.getProperty("file.separator");

	GridLayout gridLayout = null;

	Text text = null;

	ToolItem cancel = null;

	ToolItem ok = null;

	Button checkButton = null;

	MessagesPane messagesPane = null;

	String emailList = null;
	
	String sphereId = null;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DeliveryPreferencesDialog.class);

	private ResourceBundle bundle = ResourceBundle
			.getBundle(LocalizationLinks.CLIENT_UI_DELIVERYPREFERENCESDIALOG);

	private static final String FORWARD_ALL_MESSAGES = "DELIVERYPREFERENCESDIALOG.FORWARD_ALL_MESSAGES";

	private static final String OK = "DELIVERYPREFERENCESDIALOG.OK";

	private static final String CANCEL = "DELIVERYPREFERENCESDIALOG.CANCEL";
	
	
	public static void main(String args[]) {
		new DeliveryPreferencesDialog();
	}

	public DeliveryPreferencesDialog() {
	}

//	public void setMP(MessagesPane mP) {
//		this.messagesPane = mP;
//
//	}

//	public void setCurrentForwarding(String emailList) {
//		this.emailList = emailList;
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.BaseDialog#getStartUpDialogStyle()
	 */
	@Override
	protected int getStartUpDialogStyle() {
		// TODO Auto-generated method stub
		return SWT.BORDER | SWT.TITLE | SWT.APPLICATION_MODAL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.BaseDialog#initializeControls()
	 */
	@Override
	protected void initializeControls() {
		super.initializeControls();
		/*
		 * sShell = new Shell(SWT.BORDER | SWT.SHELL_TRIM | SWT.LEFT_TO_RIGHT);
		 */
		Image im;
		try {
			im = new Image(Display.getDefault(), getClass()
					.getResource(ImagesPaths.SUPRA_ICON).openStream());
			getShell().setImage(im);
		} catch (IOException ex) {
			logger.error("can't create supra ico", ex);
		}

		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.numColumns = 2;

		getShell().setLayout(gridLayout);
		// sShell.setSize(new org.eclipse.swt.graphics.Point(622, 533));

		Label label = new Label(getShell(), SWT.LEFT);
		label.setText(DeliveryPreferencesDialog.this.bundle
				.getString(DeliveryPreferencesDialog.FORWARD_ALL_MESSAGES));

		this.text = new Text(getShell(), SWT.BORDER);
		this.text.setSize(new org.eclipse.swt.graphics.Point(200, 30));
		if (this.emailList != null) {
			this.text.setText(this.emailList);
		}

		GridData gdText = new GridData(GridData.FILL, GridData.CENTER, true,
				false);
		gdText.horizontalSpan = 2;
		this.text.setLayoutData(gdText);

		ToolBar toolBar = new ToolBar(getShell(), SWT.NONE);

		this.ok = new ToolItem(toolBar, SWT.PUSH);
		this.ok.setText(DeliveryPreferencesDialog.this.bundle
				.getString(DeliveryPreferencesDialog.OK));
		addOKActionListener();

		this.cancel = new ToolItem(toolBar, SWT.PUSH);
		this.cancel.setText(DeliveryPreferencesDialog.this.bundle
				.getString(DeliveryPreferencesDialog.CANCEL));

		addCancelActionListener();

		GridData okData = new GridData();
		okData.horizontalAlignment = GridData.END;
		okData.horizontalSpan = 4;
		okData.grabExcessHorizontalSpace = true;
		okData.grabExcessVerticalSpace = false;

		toolBar.setLayoutData(okData);

//		// sShell.setSize(500,119);
//		if (this.messagesPane != null) {
//
//			Rectangle parentBounds = messagesPane.sF.getShell().getBounds();
//
//			Rectangle childBounds = getShell().getBounds();
//			int x = parentBounds.x + (parentBounds.width - childBounds.width)
//					/ 2;
//
//			int y = parentBounds.y + (parentBounds.height - childBounds.height)
//					/ 2;
//
//			getShell().setLocation(x, y);
//		}
		getShell().pack();
	}

	public void addOKActionListener() {

		this.ok.addListener(SWT.Selection, new Listener() {
			
			DeliveryPreferencesDialog dpd = DeliveryPreferencesDialog.this;

			public void handleEvent(Event event) {

				if (!this.dpd.messagesPane.sF.client.getVerifyAuth().getPrivilegesManager()
						.getSetDeliveryOptionPrivilege()
						.hasModifyPermissionInCurrentSphere()) {
					// TODO: show error
					return;
				}
				String emailAddresses = this.dpd.text.getText();
				this.dpd.messagesPane.sF.client.setEmailForwardingRules(this.dpd.messagesPane.getRawSession(),
						DeliveryPreferencesDialog.this.sphereId, emailAddresses, "true");
				DeliveryPreferencesDialog.this.close();
			}
		});
	}

	public void addCancelActionListener() {

		this.cancel.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				DeliveryPreferencesDialog.this.close();

			}
		});
	}
	
	public void show(final Shell parentShell, String sphereId, String emailList, MessagesPane messagesPane) {
		if ( sphereId == null ) {
			throw new NullPointerException( "sphereId is null" );
		}
		this.sphereId = sphereId;
		this.emailList = emailList;
		this.messagesPane = messagesPane;	
		super.show( parentShell );
	}
}
