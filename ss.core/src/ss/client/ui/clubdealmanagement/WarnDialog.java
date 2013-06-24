/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * @author roman
 *
 */
public abstract class WarnDialog extends Dialog {
	public WarnDialog() {
		super(Display.getDefault().getActiveShell());
	}
	@Override
	public abstract void okPressed();
	@Override
	public abstract void cancelPressed();
	
	public abstract String getMessage();

	@Override
	protected Control createContents(Composite parent) {
		parent.setLayout(new GridLayout());
		
		Label label = new Label(parent, SWT.CENTER);
		label.setText(getMessage());
		label.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, true));
		buttonComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		
		Button okButton = new Button(buttonComposite, SWT.PUSH);
		okButton.setText("OK");
		okButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				okPressed();
			}
		});
		Button cancelButton = new Button(buttonComposite, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancelPressed();
			}
		});
		return parent;
	}

	@Override
	protected void configureShell(Shell shell) {
		shell.setText("Warning");
	}
}
