/**
 * 
 */
package ss.common.domain.model.workflow;

import java.util.Vector;

import ss.client.ui.messagedeliver.popup.SOptionPaneChoicePane;
import ss.client.ui.preferences.delivery.ConfigureDeliveryDialog;
import ss.client.ui.preferences.delivery.EditDeliveryPreferencesComposite;
import ss.common.domain.model.ResultObject;
import ss.common.domain.model.collections.DeliveryResponseCollection;
import ss.common.domain.model.enums.Role;
import ss.common.domain.model.enums.DeliveryType;
import ss.domainmodel.ResultStatement;
import ss.domainmodel.WorkflowResponse;
import ss.domainmodel.workflow.DecisiveDelivery;

/**
 * @author roman
 *
 */
public class DecisiveDeliveryObject extends AbstractDeliveryObject {

	private static final String AFFIRMATIVE_REPLIES = "ABSTRACTDELIVERY.AFFIRMATIVE_REPLIES";
	private static final String NEGATIVE_REPLIES = "ABSTRACTDELIVERY.NEGATIVE_REPLIES";
	private static final String UNCERTAIN_REPLIES = "ABSTRACTDELIVERY.UNCERTAIN_REPLIES";
	private static final String INCLUDING = "ABSTRACTDELIVERY.INCLUDING";
	private static final String TRADERS = "ABSTRACTDELIVERY.TRADERS";
	private static final String DECISIVE = "ABSTRACTDELIVERY.DECISIVE";
	
	@Override
	public DeliveryType getType() {
		return DeliveryType.DECISIVE;
	}

	@Override
	public boolean isConfigurable() {
		return true;
	}

	@Override
	public boolean isPassed(final ResultObject result) {
		if(result.getGiverUsername()==null) {
			throw new IllegalArgumentException("Cannot find giver username");
		}
		
		int count = 0;
		DeliveryResponseCollection collection = result.getResponses();
		Vector<String> managers = new Vector<String>();
		
		for(DeliveryMember member : getMembers()) {
			if(member.getRole().equals(DecisiveDelivery.getManagerRole().getTitle())) {
				managers.add(member.getLoginName());
			}
		}
		
		for(ResponseObject response : collection) {
			if(!managers.contains(result.getGiverUsername())) {
				if(managers.contains(response.getLoginName()) && response.getValue().equals(SOptionPaneChoicePane.YES)) {
					return true;				
				}
			} else {
				if(response.getValue().equals(SOptionPaneChoicePane.YES)) {
					count++;
				}
			}
		}
		return count>1;
	}

	@Override
	public boolean validate() {
		boolean hasManager = false;
		for(DeliveryMember member : getMembers()) {
			if(member.getRole().equals(Role.MANAGER)) {
				hasManager = true;
			}
		}
		return hasManager;
	}

	@Override
	public Role getDefaultRole() {
		return Role.TRADER;
	}
	
	public static Role[] getAllRoles() {
		return new Role[]{Role.TRADER, Role.MANAGER};
	}

	@Override
	public String getDefaultDisplayName() {
		return BUNDLE.getString(DECISIVE);
	}
	
	@Override
	public ConfigureDeliveryDialog createConfigurationDialog(EditDeliveryPreferencesComposite editComposite) {
		//return new DecesiveConfigureDialog(this, editComposite);
		//TODO
		return null;
	}
	
	@Override
	public String computeResultStatistics(ResultStatement result) {
		int affMan = 0;
		int negMan = 0;
		int uncMan = 0;
		int affTr = 0;
		int negTr = 0;
		int uncTr = 0;
		
		StringBuffer textBuffer = new StringBuffer();
		
		Vector<String> managers = new Vector<String>();
		for(DeliveryMember member : getMembers()) {
			if(member.getRole().equals(Role.MANAGER)) {
				managers.add(member.getLoginName());
			}
		}
		
		for(WorkflowResponse response : result.getResponseCollection()) {
			if(response.getValue().equals(SOptionPaneChoicePane.YES)) {
				if(managers.contains(response.getLoginName())) {
					affMan++;
				} else {
					affTr++;
				}
			} else if(response.getValue().equals(SOptionPaneChoicePane.NO)) {
				if(managers.contains(response.getLoginName())) {
					negMan++;
				} else {
					negTr++;
				}
			} else if(response.getValue().equals(SOptionPaneChoicePane.UNSURE)) {
				if(managers.contains(response.getLoginName())) {
					uncMan++;
				} else {
					uncTr++;
				}
			}
		}
		
		textBuffer.append("<div>"+BUNDLE.getString(AFFIRMATIVE_REPLIES)+": " + (affMan+affTr) + "</div>");
		if(affMan+affTr>0) {
			textBuffer.append("<div>- "+BUNDLE.getString(INCLUDING)+"-"+affMan+", "+BUNDLE.getString(TRADERS)+"-"+affTr+"</div>");
		}
		
		textBuffer.append("<div>"+BUNDLE.getString(NEGATIVE_REPLIES)+": " + (negMan+negTr) + "</div>");
		if(negMan+negTr>0) {
			textBuffer.append("<div>- "+BUNDLE.getString(INCLUDING)+"-"+negMan+", "+BUNDLE.getString(TRADERS)+"-"+negTr+"</div>");
		}
		
		textBuffer.append("<div>"+BUNDLE.getString(UNCERTAIN_REPLIES)+": " + (uncMan+uncTr) + "</div>");
		if(uncMan+uncTr>0) {
			textBuffer.append("<div>- "+BUNDLE.getString(INCLUDING)+"-"+uncMan+", "+BUNDLE.getString(TRADERS)+"-"+uncTr+"</div>");
		}
		
		return textBuffer.toString();
	}

	@Override
	public boolean canPassed(ResultObject result) {
		if(result.getGiverUsername()==null) {
			throw new IllegalArgumentException("Cannot find giver username");	
		}
		
		
		int count = 0;
		
		DeliveryResponseCollection collection = result.getResponses();
		
		Vector<String> managers = new Vector<String>();
		for(DeliveryMember member : getMembers()) {
			if(member.getRole().equals(Role.MANAGER)) {
				managers.add(member.getLoginName());
			}
		}
		Vector<String> replied = new Vector<String>();
		Vector<String> notReplied = new Vector<String>();
		
		for(ResponseObject response : collection) {
			replied.add(response.getLoginName());
		}
		
		for(DeliveryMember member : getMembers()) {
			if(!replied.contains(member.getLoginName()) && !member.getLoginName().equals(result.getGiverUsername())) {
				notReplied.add(member.getLoginName());
			}
		}
		
		if(!managers.contains(result.getGiverUsername())) {
			boolean hasManagersInNotReplied = false;
			for(String member : notReplied) {
				if(managers.contains(member)) {
					hasManagersInNotReplied = true;
				}
			}
			return hasManagersInNotReplied;
		} else {
			for(ResponseObject response : collection) {
				if(response.getValue().equals(SOptionPaneChoicePane.YES)) {
					count++;
				}
			}
		}
		return count+notReplied.size()>1;
	}
}
