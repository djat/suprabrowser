/**
 * 
 */
package ss.client.ui.widgets.warningdialogs;

import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import ss.client.localization.LocalizationLinks;

/**
 * @author roman
 *
 */
public class WarningDialog extends MessageDialog{
	
	private static final ResourceBundle bundle = ResourceBundle.getBundle(
													LocalizationLinks.CLIENT_UI_WIDGETS_WARNINGDIALOGS_WARNINGDIALOG);
	
	public static final String YES = "WARNINGDIALOG.YES";
	public static final String CANCEL = "WARNINGDIALOG.CANCEL";
	public static final String WARNING = "WARNINGDIALOG.WARNING";
	protected static final String[] buttons = new String[]{bundle.getString(YES), bundle.getString(CANCEL)};
	
	protected Shell parentShell = null;

	protected final WarningDialogListener listener;

	protected boolean closeParent = true;
	
	
	public WarningDialog(Shell shell, String messageString, WarningDialogListener listener, boolean closeParent) {
		super(shell, null, null, messageString, MessageDialog.WARNING, buttons, 0);
		this.parentShell = shell;
		this.listener = listener;
		this.closeParent  = closeParent;
	}
	
	public WarningDialog(Shell shell, String messageString) {
		super(shell, null, null, messageString, MessageDialog.WARNING, buttons, 0);
		this.parentShell = shell;
		this.listener = null;
	}

	/**
	 * 
	 */
	protected void buttonPressed(int i) {
		switch(i){
		case 0 :
			if (this.listener != null){
				this.listener.performOK();
			}
			if (this.closeParent){
				getParentShell().dispose();
			} else {
				getShell().dispose();
			}
		case 1 :
			if (this.listener != null){
				this.listener.performCancel();
			}
			getShell().dispose();
		}
	}

	@Override
	protected void configureShell(Shell shell) {
		shell.setSize(320, 150);
		shell.setText(bundle.getString(WARNING));
	}
}