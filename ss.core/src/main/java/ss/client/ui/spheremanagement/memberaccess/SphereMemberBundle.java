/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess;

import ss.common.protocolobjects.MemberVisibilityProtocolObject.SphereMember;
import ss.domainmodel.MemberReference;
import ss.domainmodel.SphereStatement;

/**
 *
 */
public class SphereMemberBundle {

	protected final SphereStatement sphere;
	
	protected final MemberReference member;

	/**
	 * @param sphere
	 * @param member
	 */
	public SphereMemberBundle(final SphereStatement sphere, final MemberReference member) {
		super();
		this.sphere = sphere;
		this.member = member;
	}

	/**
	 * @return the member
	 */
	public MemberReference getMember() {
		return this.member;
	}

	/**
	 * @return the sphere
	 */
	public SphereStatement getSphere() {
		return this.sphere;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.sphere.getDisplayName() + " -> " + this.member.getContactName();
	}

	/**
	 * @return
	 */
	public SphereMember toSphereMember() {
		String parentSphereId = this.sphere.getSphereCoreId();
		return new SphereMember( parentSphereId, this.sphere.getSystemName(),  this.sphere.getDisplayName(), 
				this.member.getLoginName(), this.member.getContactName() );
	}
	
	
	
}
