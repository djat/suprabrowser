/**
 * 
 */
package ss.client.ui.preferences.delivery;

import org.apache.log4j.Logger;

import ss.client.ui.spheremanagement.ManagedSphere;
import ss.client.ui.spheremanagement.SphereActionAdaptor;
import ss.global.SSLogger;

/**
 * @author roman
 *
 */
public class SphereDeliveryChangedListener extends SphereActionAdaptor {

	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(SphereDeliveryChangedListener.class);
	
	private CommonDeliveryComposite editComposite;
	
	
	public SphereDeliveryChangedListener(CommonDeliveryComposite editComposite) {
		this.editComposite = editComposite;
	}

	@Override
	public void selectedSphereChanged(ManagedSphere selectedSphere) {
		if(selectedSphere==null) {
			return;
		}
		this.editComposite.performFinalAction();
	}

}
