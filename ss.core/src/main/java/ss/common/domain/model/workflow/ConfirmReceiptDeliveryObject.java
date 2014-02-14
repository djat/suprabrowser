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
public final class ConfirmReceiptDeliveryObject extends AbstractDeliveryObject {

	private static final String CONFIRM_RECEIPT = "ABSTRACTDELIVERY.CONFIRM_RECEIPT";
	
	@Override
	public DeliveryType getType() {
		return DeliveryType.CONFIRM_RECEIPT;
	}

	public void setDisplayName(String value) {
	}
	
	@Override
	public boolean isEnabled() {
		return true;
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
	public boolean isPassed(final ResultObject result) {
		return true;
	}
	
	@Override
	public boolean validate() {
		return true;
	}
	
	@Override
	public boolean isNotConfirmedOrNotPassed(MessageObject message, ResultObject result, String contactName, String username) {
		return message.getVotingModel().getMemberCollection().getCount()<2;
	}

	@Override
	public String getDefaultDisplayName() {
		return BUNDLE.getString(CONFIRM_RECEIPT);
	}
	
	@Override
	public boolean checkMessagesToPopup(MessageObject message,
			ResultObject result, String contactName, String username) {
		return !message.getGiver().equals(contactName)
				&& !message.hasVoted(contactName);
	}
}
