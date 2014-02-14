/**
 * 
 */
package ss.common.domain.model.collections;

import ss.common.domain.model.message.VotingModelMemberObject;

/**
 * @author roman
 *
 */
public class VotingModelMemberCollection extends DomainObjectList<VotingModelMemberObject> {

	/**
	 * @param contactName
	 * @return
	 */
	public boolean containsContactName(final String contactName) {
		for(VotingModelMemberObject member : this) {
			if(member.getContactName().equals(contactName)) {
				return true;
			}
		}
		return false;
	}

	
}
