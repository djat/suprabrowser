/**
 * 
 */
package ss.common.domain.model.workflow;

import ss.client.ui.messagedeliver.popup.SOptionPaneChoicePane;
import ss.client.ui.preferences.delivery.ConfigureDeliveryDialog;
import ss.client.ui.preferences.delivery.EditDeliveryPreferencesComposite;
import ss.common.domain.model.ResultObject;
import ss.common.domain.model.collections.DeliveryResponseCollection;
import ss.common.domain.model.enums.DeliveryType;
import ss.common.domain.model.message.MessageObject;
import ss.domainmodel.ResultStatement;
import ss.domainmodel.WorkflowResponse;


/**
 * @author roman
 *
 */
public class PollDeliveryObject extends AbstractDeliveryObject {

	private static final String AFFIRMATIVE_REPLIES = "ABSTRACTDELIVERY.AFFIRMATIVE_REPLIES";

	private static final String NEGATIVE_REPLIES = "ABSTRACTDELIVERY.NEGATIVE_REPLIES";

	private static final String UNCERTAIN_REPLIES = "ABSTRACTDELIVERY.UNCERTAIN_REPLIES";

	private static final String CURRENT_COUNT = "ABSTRACTDELIVERY.CURRENT_COUNT";

	private static final String REQUIRED_COUNT = "ABSTRACTDELIVERY.REQUIRED_COUNT";
	
	private static final String POLL = "ABSTRACTDELIVERY.POLL";
	
	private double requiredPercentage;
	
	public PollDeliveryObject() {
		this(50.0);
	}
	
	public PollDeliveryObject(double percentage) {
		super();
		setRequiredPercentage(percentage);
	}
	
	@Override
	public DeliveryType getType() {
		return DeliveryType.POLL;
	}

	@Override
	public boolean canPassed(ResultObject result) {
		DeliveryResponseCollection collection = result.getResponses();

		double noCount = 0;
		double memberCount = getMembers().getCount();
		for (ResponseObject response : collection) {
			if (!response.getValue().equals(SOptionPaneChoicePane.YES)) {
				noCount++;
			}
		}
		double currentPercent = (noCount / (memberCount - 1));
		double requiredPercent = result.getRequiredPercentage();

		return (1 - currentPercent)*100 >= requiredPercent;
	}

	@Override
	public boolean isConfigurable() {
		return true;
	}

	@Override
	public boolean isPassed(final ResultObject result) {
		DeliveryResponseCollection collection = result.getResponses();
		
		double yesCount = 0;
		double memberCount = getMembers().getCount();
		
		for (ResponseObject response : collection) {
			if (response.getValue().equals(SOptionPaneChoicePane.YES)) {
				yesCount++;
			}
		}
		double currentPercent = yesCount / (memberCount - 1);
		double requiredPercent = result.getRequiredPercentage();

		return currentPercent*100 >= requiredPercent;
	}

	@Override
	public boolean validate() {
		return getMembers().getCount()>0;
	}

	/**
	 * @return the requiredPercentage
	 */
	public double getRequiredPercentage() {
		return this.requiredPercentage;
	}

	/**
	 * @param requiredPercentage the requiredPercentage to set
	 */
	public void setRequiredPercentage(double requiredPercentage) {
		this.requiredPercentage = requiredPercentage;
	}
	
	@Override
	public String getDefaultDisplayName() {
		return BUNDLE.getString(POLL);
	}
	
	@Override
	public ConfigureDeliveryDialog createConfigurationDialog(
			EditDeliveryPreferencesComposite editComposite) {
		//return new PollConfigureDialog(this, editComposite);
		//TODO
		return null;
	}
	
	@Override
	protected ResultObject createResult(MessageObject message) {
		ResultObject result = super.createResult(message);
		result.setRequiredPercentage(getRequiredPercentage());
		return result;
	}

	public String computeResultStatistics(ResultStatement result) {
		StringBuffer textBuffer = new StringBuffer();
		int aff = 0;
		int neg = 0;
		int unc = 0;
		for (WorkflowResponse response : result.getResponseCollection()) {
			if (response.getValue().equals(SOptionPaneChoicePane.YES)) {
				aff++;
			} else if (response.getValue().equals(SOptionPaneChoicePane.NO)) {
				neg++;
			} else if (response.getValue().equals(SOptionPaneChoicePane.UNSURE)) {
				unc++;
			}
		}
		textBuffer.append("<div>" + BUNDLE.getString(AFFIRMATIVE_REPLIES)
				+ ": " + aff + "</div>");
		textBuffer.append("<div>" + BUNDLE.getString(NEGATIVE_REPLIES)
				+ ": " + neg + "</div>");
		textBuffer.append("<div>" + BUNDLE.getString(UNCERTAIN_REPLIES)
				+ ": " + unc + "</div>");
		textBuffer.append("<div>" + BUNDLE.getString(REQUIRED_COUNT)
				+ ": " + getRequiredPercentage() + " %</div>");

		float floatAff = (float) aff;
		float floatAll = (float) (getMembers().getCount() - 1);
		float current = (float) ((floatAff / floatAll) * 100.0);

		textBuffer.append("<div>" + BUNDLE.getString(CURRENT_COUNT) + ": "
				+ current + " %</div>");

		return textBuffer.toString();
	}
}
