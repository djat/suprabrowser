package ss.common.domainmodel2;

import ss.framework.domainmodel2.CriteriaFactory;
import ss.framework.domainmodel2.DomainObjectCollection;

public final class SphereMemberCollection extends DomainObjectCollection<InvitedMember> {

	private final Sphere sphereOwner;
	
	/**
	 * @param sphereOwner
	 */
	public SphereMemberCollection(Sphere sphereOwner) {
		super( sphereOwner.getSpaceOwner(), CriteriaFactory.createEqual( InvitedMember.class, InvitedMember.SphereDescriptor.class, sphereOwner ) );
		this.sphereOwner = sphereOwner;
	}


	/**
	 * @param member
	 */
	public final synchronized InvitedMember add(Member member) {
		if ( contains( member ) ) {
			return getInvitedMember( member );
		}
		return InvitedMember.createNew( getSpaceOwner(), member, this.sphereOwner );		
	}


	/**
	 * @param member
	 * @return
	 */
	public final InvitedMember getInvitedMember(Member member) {
		return getFirst( InvitedMember.class, InvitedMember.MemberDescriptor.class, member );
	}


	/**
	 * @param member
	 * @return
	 */
	public final boolean contains(Member member) {
		return contains( InvitedMember.class, InvitedMember.MemberDescriptor.class, member );
	}


}
