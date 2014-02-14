/**
 * 
 */
package ss.client.ui.preferences.changesdetector;

import ss.client.ui.preferences.delivery.CommonDeliveryComposite;

/**
 * @author roman
 *
 */
public class SphereDeliveryChangesDetector extends AbstractChangesDetector {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SphereDeliveryChangesDetector.class);
	
	private CommonDeliveryComposite deliveryComposite;
	
	public SphereDeliveryChangesDetector(CommonDeliveryComposite deliveryComposite) {
		this.deliveryComposite = deliveryComposite;
	}
	
	public void collectChangesAndUpdate() {
		try {
			this.deliveryComposite.getEditComposite().applyPressed();
		} catch(NullPointerException ex) {
			logger.error("Edit delivery composite is null. Can't collect and save changes");
		}
	}

	public void rollbackChanges() {
		this.deliveryComposite.performFinalAction();
	}

	@Override
	protected String getWarningString() {
		return this.bundle.getString(SPHERE_DELIVERY);
	}
}
