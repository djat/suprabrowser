/**
 * 
 */
package ss.client.ui.root;


import ss.client.ui.spheremanagement.ISphereDefinitionProvider;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereHierarchyBuilder;

/**
 *
 */
public class RootSphereHierarchyBuilder extends SphereHierarchyBuilder{

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RootSphereHierarchyBuilder.class);
	
	private final RootSphereComparator managedSphereComparator = new RootSphereComparator();
	
	/**
	 * @param sphereDefinitionProvider
	 */
	public RootSphereHierarchyBuilder(ISphereDefinitionProvider sphereDefinitionProvider) {
		super(sphereDefinitionProvider);
	}

	@Override
	public int compare(ManagedSphere x, ManagedSphere y) {
		return this.managedSphereComparator.compare(x, y);
	}
	
}
