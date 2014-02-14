/**
 * 
 */
package ss.client.ui.tempComponents;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author roman
 *
 */
public class LockInfoDialog extends Dialog {

	final Shell parentShell;
	/**
	 * @param parentShell
	 */
	public LockInfoDialog(Shell parentShell) {
		super(parentShell);
		this.parentShell = parentShell;
	}

	@Override
	protected Control createContents(Composite parent) {
		parent.setLayout(new GridLayout());
		Label infoLabel = new Label(parent, SWT.LEFT);
		infoLabel.setText("You cannot log in! Your membership is locked!");
		infoLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		
		Composite buttonComp = new Composite(parent, SWT.NONE);
		buttonComp.setLayout(new GridLayout());
		buttonComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		
		Button okButton = new Button(buttonComp, SWT.PUSH);
		okButton.setText("OK");
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				LockInfoDialog.this.parentShell.close();
			}
		});
		
		return parent;
	}

	@Override
	protected ShellListener getShellListener() {
		return new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				LockInfoDialog.this.parentShell.close();
			}
		};
	}
}
