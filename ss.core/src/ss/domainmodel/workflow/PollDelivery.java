/**
 * 
 */
package ss.domainmodel.workflow;

import ss.client.ui.messagedeliver.popup.SOptionPaneChoicePane;
import ss.client.ui.preferences.delivery.ConfigureDeliveryDialog;
import ss.client.ui.preferences.delivery.EditDeliveryPreferencesComposite;
import ss.client.ui.preferences.delivery.PollConfigureDialog;
import ss.domainmodel.ResultStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.WorkflowResponse;
import ss.domainmodel.WorkflowResponseCollection;
import ss.framework.entities.ISimpleEntityProperty;

/**
 * @author roman
 *
 */
public class PollDelivery extends AbstractDelivery {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
	.getLogger(PollDelivery.class);

	private static final String AFFIRMATIVE_REPLIES = "ABSTRACTDELIVERY.AFFIRMATIVE_REPLIES";

	private static final String NEGATIVE_REPLIES = "ABSTRACTDELIVERY.NEGATIVE_REPLIES";

	private static final String UNCERTAIN_REPLIES = "ABSTRACTDELIVERY.UNCERTAIN_REPLIES";

	private static final String CURRENT_COUNT = "ABSTRACTDELIVERY.CURRENT_COUNT";

	private static final String REQUIRED_COUNT = "ABSTRACTDELIVERY.REQUIRED_COUNT";
	
	private static final String POLL = "ABSTRACTDELIVERY.POLL";

	protected final ISimpleEntityProperty percent = super
	.createAttributeProperty("type/@percent");

	@Override
	public boolean canPassed(ResultStatement result) {
		WorkflowResponseCollection collection = result.getResponseCollection();

		double noCount = 0;
		double memberCount = getMemberCollection().getCount();
		for (WorkflowResponse response : collection) {
			if (!response.getValue().equals(SOptionPaneChoicePane.YES)) {
				noCount++;
			}
		}
		double currentPercent = (noCount / (memberCount - 1));
		double requiredPercent = result.getRequiredPercentDouble();

		return (1 - currentPercent)*100 >= requiredPercent;
	}

	@Override
	public boolean isPassed(ResultStatement statement) {
		WorkflowResponseCollection collection = statement.getResponseCollection();
		
		double yesCount = 0;
		double memberCount = getMemberCollection().getCount();
		
		for (WorkflowResponse response : collection) {
			if (response.getValue().equals(SOptionPaneChoicePane.YES)) {
				yesCount++;
			}
		}
		double currentPercent = yesCount / (memberCount - 1);
		double requiredPercent = statement.getRequiredPercentDouble();

		return currentPercent*100 >= requiredPercent;
	}



	public PollDelivery() {
		this(50.0);
	}

	/**
	 * @param text
	 */
	public PollDelivery(double percent) {
		super();
		setPercent(percent);
	}

	public double getPercent() {
		double percent = this.percent.getDoubleValue();
		try {
			return percent;
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return 0;
	}

	public void setPercent(double percent) {
		this.percent.setDoubleValue(percent);
	}

	@Override
	public String getDefaultDisplayName() {
		return BUNDLE.getString(POLL);
	}

	@Override
	public ConfigureDeliveryDialog createConfigurationDialog(
			EditDeliveryPreferencesComposite editComposite) {
		return new PollConfigureDialog(this, editComposite);
	}

	@Override
	public ResultStatement prepareStatement(Statement newTerse) {
		ResultStatement result = new ResultStatement();

		newTerse.setConfirmed(false);
		newTerse.setPassed(false);
		newTerse.setWorkflowType(getType());
		newTerse.setRequiredPercent(getPercent());

		result = createResultMessage(newTerse);
		result.setRequiredPercent(getPercent());
		result.setParentWorkflowType(getType());

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
				+ ": " + getPercent() + " %</div>");

		float floatAff = (float) aff;
		float floatAll = (float) (getMemberCollection().getCount() - 1);
		float current = (float) ((floatAff / floatAll) * 100.0);

		textBuffer.append("<div>" + BUNDLE.getString(CURRENT_COUNT) + ": "
				+ current + " %</div>");

		return textBuffer.toString();
	}


	@Override
	public boolean isConfigurable() {
		return true;
	}

}
