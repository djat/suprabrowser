/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;

/**
 * @author roman
 *
 */
public class ModeratorsTabItem extends AbstractClubdealItem {

	
	
	public ModeratorsTabItem(ClubdealFolder folder) {
		super(folder, "Administrative Privileges");
	}
	
	@Override
	protected Control getMainControl() {
		return new ModeratorsComposite(getFolder(), SWT.NONE);
	}

	@Override
	public void refresh() {
		((ModeratorsComposite)this.mainControl).refresh();
	}

	@Override
	public void saveChanges() {
		((ModeratorsComposite)this.mainControl).saveChanges();
	}
}
