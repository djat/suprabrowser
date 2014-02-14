package ss.common.domain.service;

import ss.domainmodel.SupraSphereMember;

public abstract class SupraSphereFacade implements ISupraSphereFacade {
	

	public final Iterable<SupraSphereMember> getSupraMembers() {
		return this.getAllMembers();
	}

}
