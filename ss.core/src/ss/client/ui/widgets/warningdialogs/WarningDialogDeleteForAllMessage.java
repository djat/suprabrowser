/**
 * 
 */
package ss.client.ui.widgets.warningdialogs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * @author roman
 *
 */
public class WarningDialogDeleteForAllMessage extends MessageDialog {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(WarningDialogDeleteForAllMessage.class);

	protected static String[] buttons = new String[]{"Yes", "Yes For All", "No", "No For All", "Cancel"};
	
	private ExtendedWarningDialogListener listener;
	
	/**
	 * @param shell
	 * @param messageString
	 * @param listener
	 * @param closeParent
	 */
	public WarningDialogDeleteForAllMessage(Shell shell, String messageString, ExtendedWarningDialogListener listener) {
		super(shell, null, null, messageString, MessageDialog.WARNING, buttons, 0);
		this.listener = listener;
	}

	
	@Override
	protected void buttonPressed(int i) {
		switch(i){
		case 0 :
			if (this.listener != null){
				this.listener.performOK();
			}
			break;
		case 1 :
			if (this.listener != null){
				this.listener.performYesForAll();
			}
			break;
		case 2 :
			if (this.listener != null){
				this.listener.performNo();
			}
			break;
		case 3 :
			if (this.listener != null){
				this.listener.performNoForAll();
			}
			break;
		case 4 :
			if (this.listener != null){
				this.listener.performCancel();
			}
		}
		getShell().dispose();
	}
}
