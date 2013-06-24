/**
 * 
 */
package ss.common.domainmodel2;

import ss.framework.domainmodel2.CriteriaFactory;
import ss.framework.domainmodel2.DomainObjectCollection;

/**
 *
 */
public class MemberSphereCollection extends DomainObjectCollection<InvitedMember>{

	private final Member memberOwner;
	
	/**
	 * @param memberOwner
	 */
	public MemberSphereCollection(final Member memberOwner) {
		super( memberOwner.getSpaceOwner(), CriteriaFactory.createEqual( InvitedMember.class, InvitedMember.MemberDescriptor.class, memberOwner ) );
		this.memberOwner = memberOwner;
	}

	/**
	 * @param sphereByName
	 */
	public InvitedMember add(Sphere sphere) {
		if ( contains( sphere ) ) {
			return getInvitedMember( sphere ) ;
		}
		InvitedMember invitedMember = InvitedMember.createNew( getSpaceOwner(), this.memberOwner, sphere);
		return invitedMember;		
	}

	/**
	 * @param sphere
	 * @return
	 */
	public InvitedMember getInvitedMember(Sphere sphere) {
		return getFirst(InvitedMember.class, InvitedMember.SphereDescriptor.class, sphere );
	}

	/**
	 * @param sphere
	 * @return
	 */
	private boolean contains(Sphere sphere) {
		return contains(InvitedMember.class, InvitedMember.SphereDescriptor.class, sphere );
	}
	
}
