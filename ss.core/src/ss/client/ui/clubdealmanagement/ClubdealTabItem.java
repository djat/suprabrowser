/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import org.eclipse.swt.widgets.Control;


/**
 * @author roman
 *
 */
public class ClubdealTabItem extends AbstractClubdealItem {
	
	public ClubdealTabItem(ClubdealFolder folder) {
		super(folder, "Spheres");
	}

	@Override
	protected Control getMainControl() {
		return new ManageByClubdealComposite(getFolder());
	}
	
	@Override
	public void saveChanges() {
		((ManageByClubdealComposite)this.mainControl).saveChanges();
	}

	@Override
	public void refresh() {
		((ManageByClubdealComposite)this.mainControl).refreshViewer();
	}
}
