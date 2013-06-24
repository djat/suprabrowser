/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import org.eclipse.swt.widgets.Control;

/**
 * @author roman
 *
 */
public class TypeTabItem extends AbstractClubdealItem {

	public TypeTabItem(final ClubdealFolder folder) {
		super(folder, "Types");
	}
	
	@Override
	protected Control getMainControl() {
		return new ManageContactTypeComposite(getFolder() , getFolder());
	}

	@Override
	public void saveChanges() { 
		((ManageContactTypeComposite)this.mainControl).saveChanges();
	}
	
	@Override
	public void refresh() {
		
	}
}
