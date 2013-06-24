/**
 * 
 */
package ss.client.ui.clubdealmanagement;

import java.util.Hashtable;

import org.apache.log4j.Logger;

import ss.domainmodel.ContactStatement;
import ss.domainmodel.clubdeals.ClubdealWithContactsObject;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class ContactChangesCollector extends AbstractChangesCollector<ContactStatement, ClubdealWithContactsObject > {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger
			.getLogger(ContactChangesCollector.class);

	public ContactChangesCollector(
			final ManageByContactComposite manageComposite) {
		super(manageComposite);
	}

	
	@Override
	protected void collectChanges() {
		Hashtable<ClubdealWithContactsObject, Boolean> stateList = getTableItemsFromManageComposite();
		for(ClubdealWithContactsObject cd : stateList.keySet()) {
			if(isInStartCheckedList(cd) && !stateList.get(cd).booleanValue()) {
				addToList(REMOVE, cd);
			} else if(!isInStartCheckedList(cd) && stateList.get(cd).booleanValue()) {
				addToList(ADD, cd);
			}
		}
	}

	@Override
	protected void saveChanges() {
		logger.debug("member:"+getManageCompositeSelection());
		for (final ClubdealWithContactsObject item : getRemoveList()) {
			logger.debug("removed from "+item);
			getManageComposite().getManager().putContactToRemoveList(item.getClubdealSystemName(),
					getManageCompositeSelection());
		}
		for (final ClubdealWithContactsObject item : getAddList()) {
			logger.debug("added to "+item);
			getManageComposite().getManager().putContactToAddList(item.getClubdealSystemName(), getManageCompositeSelection());
		}
		initStartList();
	}
}
