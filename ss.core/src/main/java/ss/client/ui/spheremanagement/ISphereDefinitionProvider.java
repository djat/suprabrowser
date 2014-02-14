/**
 * 
 */
package ss.client.ui.spheremanagement;

import java.util.Collection;

import ss.domainmodel.SphereStatement;

/**
 *
 */
public interface ISphereDefinitionProvider {

	String getRootId();
	
	/**
	 * Returns ALL sphere definitions. Can contains definitions invisible for caller user. 
	 * @return
	 */
	Collection<SphereStatement> getAllSpheres();
	
	/**
	 * Returns true if user can see sphere (what he can do with it depends on why is it) 
	 * @param sphere
	 * @return
	 */
	boolean isSphereVisible( SphereStatement sphere );
	
	void checkOutOfDate();

	void outOfDate();
	
}
