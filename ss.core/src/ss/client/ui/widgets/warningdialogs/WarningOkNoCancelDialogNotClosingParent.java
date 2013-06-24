/**
 * 
 */
package ss.client.ui.widgets.warningdialogs;

import org.eclipse.swt.widgets.Shell;

/**
 * @author roman
 *
 */
public class WarningOkNoCancelDialogNotClosingParent extends
		WarningOkCancelNoDialog {

	public WarningOkNoCancelDialogNotClosingParent(Shell shell, String messageString, WarningOkCancelNoDialogListener listener) {
		super(shell, messageString, listener);
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.widgets.warningdialogs.WarningOkCancelNoDialog#buttonPressed(int)
	 */
	@Override
	protected void buttonPressed(int i) {
		switch(i){
		case 0 :
			this.listener.performOK();
			getShell().dispose();
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
		super.configureShell(shell);
		shell.setSize(370, 150);
	}
}
