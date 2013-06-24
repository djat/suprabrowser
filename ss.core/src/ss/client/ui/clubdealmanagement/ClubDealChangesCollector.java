/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import org.apache.log4j.Logger;

import ss.domainmodel.ContactStatement;
import ss.domainmodel.clubdeals.ClubdealWithContactsObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ClubDealChangesCollector extends AbstractChangesCollector<ClubdealWithContactsObject, ContactStatement> {
	
	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(ContactChangesCollector.class);
	
	public ClubDealChangesCollector(final ManageByClubdealComposite manageComposite) {
		super(manageComposite);
	}
	
	@Override
	public void saveChanges() {
		for(ContactStatement member : getRemoveList()) {
			getManageComposite().getManager().putContactToRemoveList(getSelection().getClubdealSystemName(), member);
		}
		for(ContactStatement member : getAddList()) {
			getManageComposite().getManager().putContactToAddList(getSelection().getClubdealSystemName(), member);
		}
		initStartList();
	}
}
