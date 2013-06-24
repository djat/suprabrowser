/**
 * 
 */
package ss.client.ui.root;

import ss.client.ui.spheremanagement.ISphereDefinitionProvider;
import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereHierarchyBuilder;
import ss.client.ui.spheremanagement.SphereManager;
import ss.client.ui.sphereopen.SphereOpenManager;


/**
 *
 */
public class RootSphereManager extends SphereManager {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RootSphereManager.class);
	
	/**
	 * @param sphereDefinitionProvider
	 */
	public RootSphereManager(ISphereDefinitionProvider sphereDefinitionProvider ) {
		super(sphereDefinitionProvider);
	}

	@Override
	protected SphereHierarchyBuilder createSphereHierarchyBuilder() {
		return new RootSphereHierarchyBuilder( this.sphereDefinitionProvider );
	}
	
	/**
	 * @param item
	 */
	public void openSphere(ManagedSphere item) {
		SphereOpenManager.INSTANCE.request(item.getId());
	}

}
