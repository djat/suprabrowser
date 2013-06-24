/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import org.eclipse.swt.widgets.Control;


/**
 * @author roman
 *
 */
public class ContactTabItem extends AbstractClubdealItem {

	public ContactTabItem(ClubdealFolder folder) {
		super(folder, "Contacts");
	}

	@Override
	protected Control getMainControl() {
		return new ManageByContactComposite(getFolder());
	}

	@Override
	public void saveChanges() {
		((ManageByContactComposite)this.mainControl).saveChanges();
	}

	@Override
	public void refresh() {
		((ManageByContactComposite)this.mainControl).refreshViewer();
	}
}
