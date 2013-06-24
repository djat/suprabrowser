/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

import ss.client.localization.LocalizationLinks;

/**
 * @author roman
 *
 */
public class SavePromptDialog extends Dialog {

	private final static ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_CLUBDEALMANAGEMENT_SAVEPROMPTDIALOG);
	
	private final ClubdealWindow window;
	
	private Composite buttonComp = null;
	
	private final static String YOU_HAVE_NOT_SAVED = "SAVEPROMPTDIALOG.YOU_HAVE_NOT_SAVED";
	
	private final static String NO = "SAVEPROMPTDIALOG.NO";
	
	private final static String OK = "SAVEPROMPTDIALOG.OK";
	
	private final static String CANCEL = "SAVEPROMPTDIALOG.CANCEL";
	
	protected SavePromptDialog(final ClubdealWindow window) {
		super(window.getShell());
		this.window = window;
	}
	
	@Override
	protected Control createContents(Composite parent) {
		parent.setLayout(new GridLayout());
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label label = new Label(composite, SWT.CENTER);
		label.setText(bundle.getString(YOU_HAVE_NOT_SAVED));
		
		this.buttonComp = new Composite(parent, SWT.NONE);
		this.buttonComp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		this.buttonComp.setLayout(new GridLayout(3, true));
		createOkButton();
		createNoButton();
		createCancelButton();
		return parent;
	}
	
	/**
	 * 
	 */
	private void createNoButton() {
		Button button = new Button(this.buttonComp, SWT.PUSH);
		button.setText(bundle.getString(NO));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				noPressed();
			}
		});
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	/**
	 * 
	 */
	protected void noPressed() {		
		this.window.close();
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 100);
	}

	private void createCancelButton() {
		Button button = new Button(this.buttonComp, SWT.PUSH);
		button.setText(bundle.getString(CANCEL));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cancelPressed();
			}
		});
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void createOkButton() {
		Button button = new Button(this.buttonComp, SWT.PUSH);
		button.setText(bundle.getString(OK));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				okPressed();
			}
		});
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	@Override
	protected Layout getLayout() {
		return new GridLayout();
	}

	@Override
	protected void okPressed() {
		this.window.getFolder().totalSave();
		this.window.getManager().saveToServer();
		this.window.close();
	}
	
	@Override
	protected void cancelPressed() {
		close();
	}
}
