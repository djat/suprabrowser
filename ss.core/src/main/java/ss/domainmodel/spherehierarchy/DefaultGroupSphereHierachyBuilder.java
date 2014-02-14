/**
 * 
 */
package ss.domainmodel.spherehierarchy;

import ss.domainmodel.SphereStatement;

/**
 *
 */
public class DefaultGroupSphereHierachyBuilder extends AbstractGroupSphereHierarchyBuilder {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(DefaultGroupSphereHierachyBuilder.class);
	
	/**
	 * @param rootSphereName
	 */
	public DefaultGroupSphereHierachyBuilder() {
		super();
	}

	/* (non-Javadoc)
	 * @see ss.client.ui.models.spherehierarchy.AbstractGroupSphereHierarchyBuilder#shouldAddSphere(ss.domainmodel.SphereStatement)
	 */
	@Override
	protected boolean shouldAddToResult(SphereStatement sphere) {	
		return true;
	}

}
