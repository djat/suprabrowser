/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Shell;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.SDisplay;
import ss.common.StringUtils;

/**
 * @author roman
 *
 */
public abstract class AbstractClubdealDialog extends Dialog {
	
	private static final ResourceBundle bundle = ResourceBundle.getBundle(LocalizationLinks.CLIENT_UI_CLUBDEALMANAGEMENT_ABSTRACTCLUBDEALDIALOG);
	
	private Button saveButton = null;
	
	protected Text text = null;

	protected Composite fieldComp;
	
	private static final String SAVE = "ABSTRACTCLUBDEALDIALOG.SAVE";
	
	private static final String CANCEL = "ABSTRACTCLUBDEALDIALOG.CANCEL";
	
	
	public AbstractClubdealDialog() {
		super(SDisplay.display.get().getActiveShell());
	}
	
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(getTitle());
	}


	public abstract String getTitle();


	@Override
	protected  Control createContents(Composite parent) {
		parent.setLayout(new GridLayout());
		
		this.fieldComp = new Composite(parent, SWT.NONE);
		this.fieldComp.setLayout(new GridLayout(2, false));
		this.fieldComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Label label = new Label(this.fieldComp, SWT.LEFT);
		label.setText(getLabelText());
		
		this.text = new Text(this.fieldComp, SWT.SINGLE | SWT.WRAP | SWT.BORDER);
		this.text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.text.setText(getFieldText());
		this.text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String name = ((Text)e.widget).getText();
				setSaveButtonEnabled(name);
			}
		});
		
		Composite buttonComp = new Composite(parent, SWT.NONE);
		buttonComp.setLayout(new GridLayout(2, false));
		buttonComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.saveButton = new Button(buttonComp, SWT.PUSH);
		this.saveButton.setText(bundle.getString(SAVE));
		this.saveButton.setEnabled(false);
		this.saveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if ( savePressed() ) {
					close();
				}
			}
		});
		
		Button cancelButton = new Button(buttonComp, SWT.PUSH);
		cancelButton.setText(bundle.getString(CANCEL));
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();
			}
		});
		
		this.text.setFocus();
		
		
		return parent;
	}

	/**
	 * @return
	 */
	protected final String getName() {
		return this.text.getText();		
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 117);
	}

	/**
	 * @param name
	 */
	protected final void setSaveButtonEnabled(String name) {
		this.saveButton.setEnabled(!StringUtils.isBlank(name));
	}
	
	protected abstract String getLabelText();
	
	protected abstract boolean savePressed();
	
	protected String getFieldText() { return "";}
}
