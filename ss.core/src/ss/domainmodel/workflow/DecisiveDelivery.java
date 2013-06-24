/**
 * 
 */
package ss.domainmodel.workflow;

import java.util.List;
import java.util.Vector;

import ss.client.ui.messagedeliver.popup.SOptionPaneChoicePane;
import ss.client.ui.preferences.delivery.ConfigureDeliveryDialog;
import ss.client.ui.preferences.delivery.DecesiveConfigureDialog;
import ss.client.ui.preferences.delivery.EditDeliveryPreferencesComposite;
import ss.common.protocolobjects.MemberVisibilityProtocolObject.SphereMember;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.MemberReference;
import ss.domainmodel.ResultStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.WorkflowResponse;
import ss.domainmodel.WorkflowResponseCollection;


/**
 * @author roman
 *
 */
public class DecisiveDelivery extends AbstractDelivery {
	
	private static final String AFFIRMATIVE_REPLIES = "ABSTRACTDELIVERY.AFFIRMATIVE_REPLIES";
	private static final String NEGATIVE_REPLIES = "ABSTRACTDELIVERY.NEGATIVE_REPLIES";
	private static final String UNCERTAIN_REPLIES = "ABSTRACTDELIVERY.UNCERTAIN_REPLIES";
	private static final String INCLUDING = "ABSTRACTDELIVERY.INCLUDING";
	private static final String TRADERS = "ABSTRACTDELIVERY.TRADERS";
	private static final String PORTFOLIO_MANAGER = "ABSTRACTDELIVERY.PORTFOLIO_MANAGER";
	private static final String TRADER = "ABSTRACTDELIVERY.TRADER";
	private static final String DECISIVE = "ABSTRACTDELIVERY.DECISIVE";
	
	private static final Role[] ALL_ROLES = new Role[]{new Role(BUNDLE.getString(PORTFOLIO_MANAGER)), new Role(BUNDLE.getString(TRADER))};
	
	
	public DecisiveDelivery() {
		super();
	}
	
	public Role getDefaultRole() {
		return ALL_ROLES[1];
	}

	
	@Override
	public boolean validate() {
		boolean hasManager = false;
		for(ModelMemberEntityObject member : this.getMemberCollection()) {
			if(member.getRoleName().equals(ALL_ROLES[0].getTitle())) {
				hasManager = true;
			}
		}
		return hasManager;
	}

	public static Role[] getAllRoles() {
		return ALL_ROLES;
	}
	
	public static Role getManagerRole() {
		return ALL_ROLES[0];
	}
	
	public static Role getTraderRole() {
		return ALL_ROLES[1];
	}

	@Override
	public String getDefaultDisplayName() {
		return BUNDLE.getString(DECISIVE);
	}
	
	
	@Override
	public ConfigureDeliveryDialog createConfigurationDialog(EditDeliveryPreferencesComposite editComposite) {
		return new DecesiveConfigureDialog(this, editComposite);
	}
	
	@Override
	public void fillMemberCollection(List<MemberReference> allContacts) {
		for(MemberReference memberRef : allContacts) {
			ModelMemberEntityObject member = new ModelMemberEntityObject();
			member.setContactName(memberRef.getContactName());
			member.setUserName(memberRef.getLoginName());
			member.setRoleName(this.getDefaultRole());
			getMemberCollection().add(member);
		}
	}

	/**
	 * @return
	 */
	public static String[] getAllRolesNames() {
		String[] names = new String[ALL_ROLES.length];
		int i = 0;
		for(Role role : ALL_ROLES) {
			names[i] = role.getTitle();
			i++;
		}
		return names;
	}
	
	/**
	 * @param sphereMember
	 */
	public void addNewMember(SphereMember sphereMember) {
		ModelMemberEntityObject member = new ModelMemberEntityObject();
		member.setContactName(sphereMember.getMemberContactName());
		member.setUserName(sphereMember.getMemberLogin());
		member.setRoleName(getDefaultRole());
		getMemberCollection().add(member);
	}
	
	/**
	 * @param sphereMember
	 */
	public void addNewMember(ss.domainmodel.SphereMember sphereMember) {
		ModelMemberEntityObject member = new ModelMemberEntityObject();
		member.setContactName(sphereMember.getContactName());
		member.setUserName(sphereMember.getLoginName());
		member.setRoleName(getDefaultRole());
		getMemberCollection().add(member);
	}
	
	@Override
	public ResultStatement prepareStatement(Statement newTerse) {
		ResultStatement result = new ResultStatement();
		
		newTerse.setConfirmed(false);
		newTerse.setPassed(false);
		newTerse.setWorkflowType(getType());
		
		result = createResultMessage(newTerse);
		result.setParentWorkflowType(getType());
		
		return result;
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
		for(ModelMemberEntityObject member : getMemberCollection()) {
			if(member.getRoleName().equals(DecisiveDelivery.getManagerRole().getTitle())) {
				managers.add(member.getUserName());
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



	/* (non-Javadoc)
	 * @see ss.domainmodel.workflow.AbstractDelivery#canPassed(ss.domainmodel.ResultStatement)
	 */
	@Override
	public boolean canPassed(ResultStatement result) {
		if(result.getGiverUsername()==null)
			throw new IllegalArgumentException("Cannot find giver username");
		
		int count = 0;
		
		WorkflowResponseCollection collection = result.getResponseCollection();
		
		Vector<String> managers = new Vector<String>();
		for(ModelMemberEntityObject member : getMemberCollection()) {
			if(member.getRoleName().equals(DecisiveDelivery.getManagerRole().getTitle())) {
				managers.add(member.getUserName());
			}
		}
		Vector<String> replied = new Vector<String>();
		Vector<String> notReplied = new Vector<String>();
		
		for(WorkflowResponse response : collection) {
			replied.add(response.getLoginName());
		}
		
		for(ModelMemberEntityObject member : getMemberCollection()) {
			if(!replied.contains(member.getUserName()) && !member.getUserName().equals(result.getGiverUsername())) {
				notReplied.add(member.getUserName());
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
			for(WorkflowResponse response : collection) {
				if(response.getValue().equals(SOptionPaneChoicePane.YES)) {
					count++;
				}
			}
		}
		return count+notReplied.size()>1;
	}



	/* (non-Javadoc)
	 * @see ss.domainmodel.workflow.AbstractDelivery#isPassed(ss.domainmodel.ResultStatement)
	 */
	@Override
	public boolean isPassed(ResultStatement result) {
		if(result.getGiverUsername()==null)
			throw new IllegalArgumentException("Cannot find giver username");
		
		int count = 0;
		
		WorkflowResponseCollection collection = result.getResponseCollection();
		
		Vector<String> managers = new Vector<String>();
		
		for(ModelMemberEntityObject member : getMemberCollection()) {
			if(member.getRoleName().equals(DecisiveDelivery.getManagerRole().getTitle())) {
				managers.add(member.getUserName());
			}
		}
		
		for(WorkflowResponse response : collection) {
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
	public boolean isConfigurable() {
		return true;
	}
	
	/**
	 * @param contact
	 */
	public void addNewMember(ContactStatement contact) {
		ModelMemberEntityObject member = new ModelMemberEntityObject();
		member.setContactName(contact.getFirstName()+" "+contact.getLastName());
		member.setUserName(contact.getLogin());
		member.setRoleName(getDefaultRole());
		getMemberCollection().add(member);
	}
	
	/**
	 * @param contact
	 */
	public void addNewMember(MemberReference contact) {
		ModelMemberEntityObject member = new ModelMemberEntityObject();
		member.setContactName(contact.getContactName());
		member.setUserName(contact.getLoginName());
		member.setRoleName(getDefaultRole());
		getMemberCollection().add(member);
	}

}
