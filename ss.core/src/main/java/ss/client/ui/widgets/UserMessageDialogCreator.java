/**
 * 
 */
package ss.client.ui.widgets;

import java.util.Hashtable;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;

import org.dom4j.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import ss.client.localization.LocalizationLinks;
import ss.client.networking.DialogsMainCli;
import ss.client.ui.SupraSphereFrame;
import ss.client.ui.browser.SupraBrowser;
import ss.client.ui.widgets.warningdialogs.ExtendedWarningDialogListener;
import ss.client.ui.widgets.warningdialogs.RemoveSphereDialog;
import ss.client.ui.widgets.warningdialogs.WarningDialog;
import ss.client.ui.widgets.warningdialogs.WarningDialogAdapter;
import ss.client.ui.widgets.warningdialogs.WarningDialogDeleteForAllMessage;
import ss.client.ui.widgets.warningdialogs.WarningDialogListener;
import ss.client.ui.widgets.warningdialogs.WarningOkCancelNoDialog;
import ss.client.ui.widgets.warningdialogs.WarningOkCancelNoDialogListener;
import ss.client.ui.widgets.warningdialogs.WarningOkNoCancelDialogNotClosingParent;
import ss.common.ArgumentNullPointerException;
import ss.common.UiUtils;

/**
 * @author zobo
 * 
 */
public class UserMessageDialogCreator {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(UserMessageDialogCreator.class);
	
	/**
	 * Default message for information dialog box
	 */
	private static final String INFORMATION_DEFAULT_MESSAGE = "USERMESSAGEDIALOGCREATOR.INFORMATION";

	/**
	 * Default message for warning dialog box
	 */
	private static final String WARNING_DEFAULT_MESSAGE = "USERMESSAGEDIALOGCREATOR.WARNING";

	/**
	 * Default message for error dialog box
	 */
	private static final String ERROR_DEFAULT_MESSAGE = "USERMESSAGEDIALOGCREATOR.ERROR";
	
	private static final String OVERRIDE_ALL_PER_SPHERE_FORWARDING_SETTING = "USERMESSAGEDIALOGCREATOR.OVERRIDE_ALL_PER_SPHERE_FORWARDING_SETTING";
	
	private static final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_WIDGETS_USERMESSAGEDIALOGCREATOR);
	
	private static boolean firstShow = true;

	/**
	 * show up error Dialog box
	 * 
	 * @param error
	 *            String message to be displayed
	 */
	public static void error(String error ) {
		error(error, null );
	}
	
	public static void error(String error, String title ) {
		error( getParentShellForMessage(), error, title );
	}

	/**
	 * @return
	 */
	private static Shell getParentShellForMessage() {
		Shell shell = Display.getDefault().getActiveShell();
		if ( shell == null || shell.isDisposed() ) {
			SupraSphereFrame supraSphereFrame = SupraSphereFrame.INSTANCE;
			if ( supraSphereFrame != null ) {
				shell = supraSphereFrame.getShell();
			}
		}
		return shell;
	}
	
	public static void error(Shell parent, String error, String title ) {
		if ( parent == null ) {
			throw new ArgumentNullPointerException( "parent" );
		}
		MessageBox box = new MessageBox( parent,
				SWT.ICON_ERROR | SWT.OK);
		box.setMessage(error);
		if (title != null) {
			box.setText(title);
		}
		box.open();
		resizeBrowser();
	}

	/**
	 * show up default error Dialog box
	 */
	public static void error() {
		error(bundle.getString(ERROR_DEFAULT_MESSAGE));
	}

	/**
	 * show up warning Dialog box
	 * 
	 * @param warning
	 *            String message to be displayed
	 */
	public static void warning(String warning) {
		MessageBox box = new MessageBox(getParentShellForMessage(),
				SWT.ICON_WARNING | SWT.OK);
		box.setMessage(warning);
		box.open();
		resizeBrowser();
	}
	
	/**
	 * show up warning Dialog box
	 * 
	 * @param warning
	 *            String message to be displayed
	 */
	public static void warning( final String warning, final String title ) {
		MessageBox box = new MessageBox(getParentShellForMessage(),
				SWT.ICON_WARNING | SWT.OK);
		box.setMessage(warning);
		box.setText(title);
		box.open();
		resizeBrowser();
	}
	
	
	/**
	 * show up info Dialog box
	 * 
	 * @param warning
	 *            String message to be displayed
	 */
	public static void warningYesCancelButton(final String warning) {
		UiUtils.swtBeginInvoke(new Runnable(){
			public void run() {
				new WarningDialog(getParentShellForMessage(), warning).open();
			}
		});
		resizeBrowser();
	}
	
	/**
	 * show up info Dialog box with WarningDialogListener
	 * 
	 * @param warning
	 *            String message to be displayed
	 */
	public static void warningYesCancelButton(final String warning, final WarningDialogListener listener, final boolean closeParentOnOK) {
		UiUtils.swtBeginInvoke(new Runnable(){
			public void run() {
				new WarningDialog(getParentShellForMessage(), warning, listener, closeParentOnOK).open();
			}
		});
		resizeBrowser();
		
	}
	
	/**
	 * show up info Dialog box with WarningDialogListener
	 * 
	 * @param warning
	 *            String message to be displayed
	 */
	public static void warningYesCancelNOButton(final String warning, final WarningOkCancelNoDialogListener listener) {
		UiUtils.swtBeginInvoke(new Runnable(){
			public void run() {
				new WarningOkCancelNoDialog(getParentShellForMessage(), warning, listener).open();
			}
		});
		resizeBrowser();	
	}
	
	public static void warningManageSpheresAccess(final String warning, final WarningOkCancelNoDialogListener listener) {
		UiUtils.swtInvoke(new Runnable(){
			public void run() {
				new WarningOkNoCancelDialogNotClosingParent(getParentShellForMessage(), warning, listener).open();
			}
		});
		resizeBrowser();	
	}
	
	public static void warningRemoveSphere(final String warning, final DialogsMainCli client, final Hashtable session, final Document doc) {
		UiUtils.swtBeginInvoke(new Runnable(){
			public void run() {
				new RemoveSphereDialog(warning, client, session, doc).open();
			}
		});
		resizeBrowser();
	}
	
	public static void warningDeleteMessage(final Shell parent, final String warning, final WarningDialogListener listener, final boolean isCloseParentShell) {
		UiUtils.swtBeginInvoke(new Runnable(){
			public void run() {
				new WarningDialog(parent, warning, listener, isCloseParentShell).open();
			}
		});
		resizeBrowser();
	}
	
	public static void warningDialogWithForAllButton(final Shell parent, final String warning, final ExtendedWarningDialogListener listener) {
		UiUtils.swtInvoke(new Runnable(){
			public void run() {
				new WarningDialogDeleteForAllMessage(parent, warning, listener).open();
			}
		});
		resizeBrowser();
	}


	/**
	 * show up default warning Dialog box
	 */
	public static void warning() {
		warning(bundle.getString(WARNING_DEFAULT_MESSAGE));
	}

	/**
	 * show up info Dialog box
	 * 
	 * @param info
	 *            String message to be displayed
	 */
	public static void info(String info, String title) {
		info(getParentShellForMessage(), info, title );
	}
	
	public static void info(Shell parent, String info, String title) {
		if ( parent == null ) {
			throw new ArgumentNullPointerException( "parent" );
		}
		final MessageBox box = new MessageBox(parent,
				SWT.ICON_INFORMATION | SWT.OK);
		box.setMessage(info);
		if (title != null) {
			box.setText(title);
		}
		box.open();
		resizeBrowser();
	}
	
	/**
	 * show up info Dialog box
	 * 
	 * @param info
	 *            String message to be displayed
	 */
	public static void info(String info) {
		info(info, null);
	}

	/**
	 * show up default info Dialog box
	 */
	public static void info() {
		info(bundle.getString(INFORMATION_DEFAULT_MESSAGE), null);
	}

//	/**
//	 * show up error Dialog box in SWING
//	 * 
//	 * @param error
//	 *            String message to be displayed
//	 */
//	public static void errorSWING(String error) {
//		JOptionPane.showMessageDialog(null, error, bundle.getString(ALERT_DEFAULT_MESSAGE),
//				JOptionPane.ERROR_MESSAGE);
//	}

//	/**
//	 * show up default error Dialog box in SWING
//	 */
//	public static void errorSWING() {
//		errorSWING(bundle.getString(ERROR_DEFAULT_MESSAGE));
//	}

//	/**
//	 * show up warning Dialog box in SWING
//	 * 
//	 * @param warning
//	 *            String message to be displayed
//	 */
//	public static void warningSWING(String warning) {
//		JOptionPane.showMessageDialog(null, warning, bundle.getString(WARNING_DEFAULT_MESSAGE),
//				JOptionPane.WARNING_MESSAGE);
//	}

//	/**
//	 * show up default warning Dialog box in SWING
//	 */
//	public static void warningSWING() {
//		errorSWING(bundle.getString(WARNING_DEFAULT_MESSAGE));
//	}

	/**
	 * @return
	 */
	public static boolean overridePerSphereForwardingSettings() {
		final AtomicBoolean bool = new AtomicBoolean();
		warningYesCancelButton(bundle.getString(OVERRIDE_ALL_PER_SPHERE_FORWARDING_SETTING), new WarningDialogAdapter(){
			public void performCancel() {
				bool.set(false);
			}

			public void performOK() {
				bool.set(true);
			}
		}, false);
		return bool.get();
	}

	/**
	 * @param displayName
	 */
	public static void errorSphereAccessDenied(String displayName) {
		String errorMessage = "Cannot open "+displayName+". \nAccess denied.";
		
		error(errorMessage);
	}
	
	private static void resizeBrowser() {
//		if(!firstShow) {
//			return;
//		}
//		firstShow = false;
//		UiUtils.swtBeginInvoke(new Runnable() {
//			public void run() {
//				if ( SupraSphereFrame.INSTANCE != null ) {
//					for (SupraBrowser browser : SupraSphereFrame.INSTANCE
//							.getAllBrowsers()) {
//						if(browser==null || browser.isDisposed()) {
//							continue;
//						}
//						Point point = browser.getParent().getSize();
//						browser.getParent().setSize(point.x + 1,
//								point.y + 1);
//						browser.getParent().setSize(point);
//						browser.getParent().layout();
//					}
//				}
//			}
//		});
	}
}
