/**
 * 
 */
package ss.client.ui.spheremanagement;

import java.util.EventListener;

/**
 * 
 */
public interface SphereActionListener extends EventListener {

	/**
	 * @param selectedSphere
	 */
	void selectedSphereChanged(ManagedSphere selectedSphere);

	void showContextMenu(ManagedSphere selectedSphere);
}
