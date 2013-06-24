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
public class NormalDelivery extends AbstractDelivery {

	private static final String NORMAL = "ABSTRACTDELIVERY.NORMAL";
	
	public NormalDelivery() {
		super();
	}

	public String getDisplayName() {
		return getDefaultDisplayName();
	}
	
	public String getDefaultDisplayName() {
		return BUNDLE.getString(NORMAL);
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
		newTerse.setConfirmed(true);
		newTerse.setPassed(true);
		newTerse.setWorkflowType(getType());
		return null;
	}

	@Override
	public boolean checkMessagesToPopup(Statement statement,
			ResultStatement result, String contactName, String username) {
		return false;
	}

	@Override
	public boolean isNotConfirmedOrNotPassed(Statement statement,
			ResultStatement result, String contactName, String username) {
		return false;
	}

}
