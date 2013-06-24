/**
 * 
 */
package ss.common.domainmodel2;

import ss.domainmodel.preferences.UserSpherePreferences;
import ss.framework.domainmodel2.AbstractDomainSpace;
import ss.framework.domainmodel2.AbstractHelper;
import ss.framework.domainmodel2.EditingScope;

/**
 *
 */
public final class InvitedMemberHelper extends AbstractHelper {

	@SuppressWarnings("unused")
	private final static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(InvitedMemberHelper.class);
	
	/**
	 * @param spaceOwnerRef
	 */
	public InvitedMemberHelper(AbstractDomainSpace spaceOwner) {
		super(spaceOwner);
	}
	/**
	 * @param string
	 * @param string2
	 */
	public synchronized InvitedMember getOrCreateInvitedMember(String sphereSystemName, String login) {
		logger.debug( "Creating editing scope." );
		EditingScope editingScope = getSpaceOwner().createEditingScope();
		try {
			logger.debug( "Getting member." );
			Member member = getSpaceOwner().getHelper( MemberHelper.class ).getMemberOrCreate(login);
			logger.debug( "Getting sphere." );
			Sphere sphere = getSpaceOwner().getHelper( SphereHelper.class ).getSphereOrCreate(sphereSystemName);
			InvitedMember invitedMember = member.getSpheres().getInvitedMember(sphere);
			if ( invitedMember != null ) {
				logger.debug( "Found invited member for " + sphere + " " +  member);	
				return invitedMember;				
			}
			else if ( logger.isDebugEnabled() ) {
				logger.debug( "Invited member not found for " + sphere + " " + member );
			}
			return member.getSpheres().add( sphere );
		}
		finally {
			logger.debug( "Disposing editing scope." );
			editingScope.dispose();
		}				
	}
	
	/**
	 * @param string
	 * @param string2
	 */
	public synchronized void setInvitedMemberPreferences(String sphereSystemName, String login, UserSpherePreferences settings) {
		EditingScope editingScope = getSpaceOwner().createEditingScope();
		try {
			getOrCreateInvitedMember(sphereSystemName, login).setPreferences( settings );
		}
		finally {
			editingScope.dispose();
		}
	}
	
	/**
	 * @param string
	 * @param string2
	 */
	public synchronized UserSpherePreferences getInvitedMemberPreferences(String sphereSystemName, String login) {
		return getOrCreateInvitedMember(sphereSystemName, login).getPreferences();		
	}

}
