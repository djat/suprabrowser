/**
 * 
 */
package ss.client.ui.root;

import ss.client.networking.DialogsMainCli;
import ss.client.ui.spheremanagement.SphereDefinitionProvider;

/**
 *
 */
public class RootSphereDefinitionProvider extends SphereDefinitionProvider {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(RootSphereDefinitionProvider.class);
	
	public RootSphereDefinitionProvider(final DialogsMainCli clientProtocol) {
		super( clientProtocol );
	}

}
