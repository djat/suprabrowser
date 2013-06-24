/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import ss.common.ArgumentNullPointerException;

/**
 *
 */
public abstract class AbstractAccessManagerComposite extends Composite implements IMemberAccessUiOwner {

	protected final MemberAccessManager manager;
	/**
	 * @param parent
	 */
	public AbstractAccessManagerComposite(Composite parent, MemberAccessManager manager) {
		super(parent, SWT.NONE );
		if (manager == null) {
			throw new ArgumentNullPointerException("manager");
		}
		this.manager = manager;
	}

	
	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.memberaccess.IApplyButtonFactory#create(org.eclipse.swt.widgets.Composite)
	 */
	public Button createApplyButton(Composite parent) {
		Button applyButton = new Button(parent, SWT.PUSH);
		applyButton.setLayoutData(new GridData( SWT.RIGHT, SWT.CENTER, false, false ) );
		applyButton.setText( "Apply" );
		applyButton.setEnabled(true);
		applyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent se) {
				applyChanges();
			}
		});
		return applyButton;
	}

	/**
	 * 
	 */
	protected void applyChanges() {
		this.manager.collectChangesAndUpdate();
	}
	
	protected void rollbackChanges() {
		this.manager.rollbackChanges();
	}


	/**
	 * @return the manager
	 */
	public MemberAccessManager getManager() {
		return this.manager;
	}
	
	
}
