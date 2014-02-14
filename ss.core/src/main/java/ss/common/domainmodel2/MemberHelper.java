package ss.common.domainmodel2;

import ss.domainmodel.preferences.UserPersonalPreferences;
import ss.framework.domainmodel2.AbstractDomainSpace;
import ss.framework.domainmodel2.AbstractHelper;
import ss.framework.domainmodel2.EditingScope;

public final class MemberHelper extends AbstractHelper {


	/**
	 * @param spaceOwnerRef
	 */
	public MemberHelper(AbstractDomainSpace spaceOwner) {
		super(spaceOwner);
	}

	/**
	 * @param login
	 * @return
	 */
	public synchronized Member getMemberByLogin(String login) {
		return Member.getByLogin(super.getSpaceOwner(), login);
	}

	/**
	 * @param login
	 * @return
	 */
	public synchronized Member createNew(String login) {
		return Member.createNew(getSpaceOwner(), login);
	}

	public synchronized Member getMemberOrCreate(String login) {
		Member member = getMemberByLogin(login);
		if (member != null) {
			return member;
		}
		EditingScope scope = getSpaceOwner().createEditingScope();
		try {
			return createNew(login);
		} finally {
			scope.dispose();
		}
	}
	
	public synchronized void setMemberPreferences(String login, UserPersonalPreferences preferences) {
		EditingScope editingScope = getSpaceOwner().createEditingScope();
		try {
			getMemberOrCreate(login).setPreferences( preferences );
		}
		finally {
			editingScope.dispose();
		}
	}
	
	public synchronized UserPersonalPreferences getMemberPreferences(String login) {
		return getMemberOrCreate( login).getPreferences();		
	}


}
