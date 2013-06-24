/**
 * 
 */
package ss.domainmodel.workflow;

import ss.domainmodel.ResultStatement;
import ss.domainmodel.Statement;



/**
 * @author roman
 *
 */
public class ConfirmReceiptDelivery extends AbstractDelivery {

	private static final String CONFIRM_RECEIPT = "ABSTRACTDELIVERY.CONFIRM_RECEIPT";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
		.getLogger(ConfirmReceiptDelivery.class);
	
	
	public ConfirmReceiptDelivery() {
		super();
	}

	public String getDisplayName() {
		return getDefaultDisplayName();
	}
	
	public String getDefaultDisplayName() {
		return BUNDLE.getString(CONFIRM_RECEIPT);
	}

	/**
	 * @return sets the delivery displayName
	 */
	public void setDisplayName(String value) {
	}

	/* (non-Javadoc)
	 * @see ss.domainmodel.workflow.AbstractDelivery#getEnabled()
	 */
	@Override
	public boolean isEnabled() {
		return this.enabled.getBooleanValue(true);
	}

	@Override
	public ResultStatement prepareStatement(Statement newTerse) {
		newTerse.setConfirmed(false);
		newTerse.setPassed(false);
		newTerse.setWorkflowType(getType());
		return null;
	}
	
	@Override
	public boolean checkMessagesToPopup(Statement statement,
			ResultStatement result, String contactName, String username) {
		return !statement.getGiver().equals(contactName)
				&& !statement.hasVoted(contactName);
	}

	@Override
	public boolean isNotConfirmedOrNotPassed(Statement statement, ResultStatement result, String contactName, String username) {
		return statement.getVotedMembers().getCount()<2;
	}
}
