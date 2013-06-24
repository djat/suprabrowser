/**
 * 
 */
package ss.client.ui.spheremanagement.memberaccess;

import java.util.List;

import ss.client.ui.spheremanagement.ISphereDefinitionProvider;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereHierarchyBuilder;
import ss.domainmodel.MemberReference;
import ss.domainmodel.SupraSphereMember;

/**
 *
 */
public class SphereHierarchyBuilderWithMembers extends SphereHierarchyBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereHierarchyBuilderWithMembers.class);
	
	private final IMemberDefinitionProvider memberDefinitionProvider;
	
	private List<SupraSphereMember> members = null;

	public SphereHierarchyBuilderWithMembers(ISphereDefinitionProvider sphereDefinitionProvider, final IMemberDefinitionProvider memberDefinitionProvider) {
		super(sphereDefinitionProvider);
		this.memberDefinitionProvider = memberDefinitionProvider;
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.spheremanagement.SphereHierarchyBuilder#configureSphere(ss.client.ui.spheremanagement.ManagedSphere)
	 */
	@Override
	protected void configureSphere(ManagedSphere managedSphere) {
		super.configureSphere(managedSphere);
		for( MemberReference memberStatement : getAllMembers() ) {
			managedSphere.getMembers().add( new MemberAccess( memberStatement, this.memberDefinitionProvider, managedSphere ) );
		}
	}

	/**
	 * @return
	 */
	private List<SupraSphereMember> getAllMembers() {
		if ( this.members == null ) {
			this.members = this.memberDefinitionProvider.getMembers();
		}
		if (logger.isDebugEnabled()) {
			logger.debug("All members size " + this.members.size() );
		}
		return this.members;
	}	
}
