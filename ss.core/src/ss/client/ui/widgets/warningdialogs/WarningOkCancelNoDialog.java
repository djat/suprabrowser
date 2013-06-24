/**
 * 
 */
package ss.client.ui.widgets.warningdialogs;

import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import ss.client.localization.LocalizationLinks;

/**
 * @author zobo
 *
 */
public class WarningOkCancelNoDialog extends MessageDialog{
	
	private static final ResourceBundle bundle = ResourceBundle.getBundle(
													LocalizationLinks.CLIENT_UI_WIDGETS_WARNINGDIALOGS_WARNINGOKCANCELNODIALOG);
	
	public static final String YES = "WARNINGDIALOG.YES";
	public static final String NO = "WARNINGDIALOG.NO";
	public static final String CANCEL = "WARNINGDIALOG.CANCEL";
	public static final String WARNING = "WARNINGDIALOG.WARNING";
	public static final String[] buttons = new String[]{bundle.getString(YES), bundle.getString(NO), bundle.getString(CANCEL)};
	
	protected Shell parentShell = null;

	protected final WarningOkCancelNoDialogListener listener;
	
	
	public WarningOkCancelNoDialog(Shell shell, String messageString, WarningOkCancelNoDialogListener listener) {
		super(shell, null, null, messageString, MessageDialog.WARNING, buttons, 0);
		this.parentShell = shell;
		this.listener = listener;
	}

	/**
	 * 
	 */
	protected void buttonPressed(int i) {
		switch(i){
		case 0 :
			this.listener.performOK();
			getParentShell().dispose();
			break;
		case 1 :
			this.listener.performNO();
			getShell().dispose();
			break;
		case 2:
			this.listener.performCancel();
			getShell().dispose();
			break;
		}
	}

	@Override
	protected void configureShell(Shell shell) {
		shell.setSize(320, 150);
		shell.setText(bundle.getString(WARNING));
	}
}