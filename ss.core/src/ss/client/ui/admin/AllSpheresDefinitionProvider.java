/**
 * 
 */
package ss.client.ui.admin;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.spheremanagement.SphereDefinitionProvider;
import ss.domainmodel.SphereStatement;

/**
 *
 */
public class AllSpheresDefinitionProvider extends SphereDefinitionProvider {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AllSpheresDefinitionProvider.class);
	
	public AllSpheresDefinitionProvider() {
		super( SupraSphereFrame.INSTANCE.client );
	}
	
	@Override
	public String getRootId() {
		return this.clientProtocol.getVerifyAuth().getSupraSphereName();
	}

	@Override
	public boolean isSphereVisible(SphereStatement sphere) {
		return true;
	}

}
