/**
 * 
 */
package ss.client.ui.spheremanagement;

import ss.client.networking.DialogsMainCli;
import ss.domainmodel.SphereStatement;

/**
 *
 */
public class SphereDefinitionProviderForPreferences extends SphereDefinitionProvider {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereDefinitionProviderForPreferences.class);
	
	/**
	 * @param clientProtocol
	 */
	public SphereDefinitionProviderForPreferences(final DialogsMainCli clientProtocol) {
		super( clientProtocol ); 
	}

	@Override
	public boolean isSphereVisible(SphereStatement sphere) {
		return !sphere.isDeleted() && (isAdminUser() || super.isSphereVisible(sphere));
	}


}
