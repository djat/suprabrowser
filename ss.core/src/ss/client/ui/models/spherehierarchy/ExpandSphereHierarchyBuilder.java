/**
 * 
 */
package ss.client.ui.models.spherehierarchy;

import ss.common.CompareUtils;
import ss.domainmodel.SphereStatement;
import ss.domainmodel.spherehierarchy.AbstractGroupSphereHierarchyBuilder;

/**
 * 
 */
public class ExpandSphereHierarchyBuilder extends
		AbstractGroupSphereHierarchyBuilder {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ExpandSphereHierarchyBuilder.class);
	
	
	private final String supraSphereName;

	private SphereStatement rootSphere;

	/**
	 * @param supraSphereName
	 */
	public ExpandSphereHierarchyBuilder(final String supraSphereName) {
		super();
		this.supraSphereName = supraSphereName;
	}
	
	
	/* (non-Javadoc)
	 * @see ss.domainmodel.spherehierarchy.AbstractGroupSphereHierarchyBuilder#checkBuildConditions()
	 */
	@Override
	protected void checkBuildConditions() {
		super.checkBuildConditions();
		if ( this.rootSphere == null ) {
			throw new NullPointerException( "Root sphere is null" );		
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.client.ui.models.spherehierarchy.AbstractGroupSphereHierarchyBuilder#prepareToBuildHierarchy()
	 */
	@Override
	protected void prepareToBuildHierarchy() {
		super.prepareToBuildHierarchy();
		for (SphereStatement sphere : getSpheresToProcess()) {
			if (CompareUtils.equals(sphere.getSystemName(),
					this.supraSphereName)) {
				this.rootSphere = sphere;
				if ( logger.isDebugEnabled() ) {
					logger.debug( "found root sphere"+ this.rootSphere );
				}
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.domainmodel.spherehierarchy.AbstractGroupSphereHierarchyBuilder#shouldAddToResult(ss.domainmodel.SphereStatement)
	 */
	@Override
	protected boolean shouldAddToResult(SphereStatement sphere) {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.domainmodel.spherehierarchy.AbstractGroupSphereHierarchyBuilder#findCoreSphere
	 */
	@Override
	protected SphereStatement findCoreSphere(SphereStatement sphere) {
		final SphereStatement coreSphere = super.findCoreSphere(sphere);
		if ( coreSphere == null ) {
			if ( !CompareUtils.equals( sphere.getSystemName(), this.supraSphereName ) ) {
				return this.rootSphere;
			}	
		}
		return coreSphere;
	}

}
