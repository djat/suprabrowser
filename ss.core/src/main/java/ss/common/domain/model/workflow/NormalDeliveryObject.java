/**
 * 
 */
package ss.common.domain.model.workflow;

import ss.common.domain.model.ResultObject;
import ss.common.domain.model.enums.DeliveryType;
import ss.common.domain.model.message.MessageObject;

/**
 * @author roman
 *
 */
public class NormalDeliveryObject extends AbstractDeliveryObject {

	private static final String NORMAL = "ABSTRACTDELIVERY.NORMAL";
	
	public String getDisplayName() {
		return getDefaultDisplayName();
	}
	
	public String getDefaultDisplayName() {
		return BUNDLE.getString(NORMAL);
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public DeliveryType getType() {
		return DeliveryType.NORMAL;
	}

	@Override
	public boolean canPassed(ResultObject result) {
		return true;
	}

	@Override
	public boolean isConfigurable() {
		return false;
	}

	@Override
	public boolean validate() {
		return true;
	}

	@Override
	public boolean isPassed(ResultObject result) {
		return true;
	}
	
	@Override
	public boolean checkMessagesToPopup(MessageObject message,
			ResultObject result, String contactName, String username) {
		return false;
	}

	@Override
	public boolean isNotConfirmedOrNotPassed(MessageObject message,
			ResultObject result, String contactName, String username) {
		return false;
	}
}
