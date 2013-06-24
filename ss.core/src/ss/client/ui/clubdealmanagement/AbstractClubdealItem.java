/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Control;

/**
 * @author roman
 *
 */
public abstract class AbstractClubdealItem extends CTabItem {

	private final ClubdealFolder folder;
	
	protected Control mainControl;
	
	protected AbstractClubdealItem(final ClubdealFolder folder, final String title) {
		super(folder, SWT.NONE);
		this.folder = folder;
		createContent();
		setText(title);
	}
	
	protected abstract Control getMainControl();
	
	/**
	 * 
	 */
	private void createContent() {
		setControl(getMainControl()); 
	}

	public ClubdealFolder getFolder() {
		return this.folder;
	}

	@Override
	public void setControl(final Control control) {
		super.setControl(control);
		this.mainControl = control;
	}

	/**
	 * 
	 */
	public abstract void saveChanges();
	
	public abstract void refresh();
}
