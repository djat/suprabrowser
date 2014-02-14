/**
 * 
 */
package ss.client.ui.spheremanagement;

import ss.client.networking.DialogsMainCli;
import ss.domainmodel.SphereStatement;

/**
 * @author roman
 *
 */
public class NoEmailBoxDefinitionProvider extends SphereDefinitionProvider {

	public NoEmailBoxDefinitionProvider(DialogsMainCli cli) {
		super(cli);
	}

	@Override
	public boolean isSphereVisible(SphereStatement sphere) {
		return !sphere.isEmailBox() && !sphere.isDeleted();
	}
}
