/**
 * 
 */
package ss.client.ui.root;

import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereActionAdaptor;

/**
 *
 */
class RootSphereHierarchyListener extends SphereActionAdaptor {

	private final RootSphereHierarchyComposite composite;
	
	public RootSphereHierarchyListener(RootSphereHierarchyComposite composite) {
		this.composite = composite;
	}
	
	@Override
	public void selectedSphereChanged(ManagedSphere selectedSphere) {
		this.composite.refreshPeopleList(selectedSphere);
	}

	@Override
	public void showContextMenu(ManagedSphere selectedSphere) {
		super.showContextMenu(selectedSphere);
		this.composite.handleRightClickTree();
	}	
	
}
