/**
 * 
 */
package ss.common.domain.model.workflow;

import java.util.ResourceBundle;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.preferences.delivery.ConfigureDeliveryDialog;
import ss.client.ui.preferences.delivery.EditDeliveryPreferencesComposite;
import ss.common.domain.model.ContactObject;
import ss.common.domain.model.DomainObject;
import ss.common.domain.model.ResultObject;
import ss.common.domain.model.collections.DeliveryMemberCollection;
import ss.common.domain.model.collections.MemberCollection;
import ss.common.domain.model.collections.DeliveryResponseCollection;
import ss.common.domain.model.enums.Role;
import ss.common.domain.model.enums.DeliveryType;
import ss.common.domain.model.message.MessageObject;
import ss.common.domain.model.suprasphere.MemberReferenceObject;
import ss.common.protocolobjects.MemberVisibilityProtocolObject.SphereMember;
import ss.domainmodel.ResultStatement;

/**
 * @author roman
 *
 */
public abstract class AbstractDeliveryObject extends DomainObject {

	protected static final ResourceBundle BUNDLE = ResourceBundle.getBundle(LocalizationLinks.DOMAINMODEL_WORKFLOW_ABSTRACTDELIVERY);
	
	private String description;
	
	private boolean enabled;
	
	private String displayName;
	
	private final DeliveryMemberCollection members = new DeliveryMemberCollection();

	public abstract DeliveryType getType();
	
	public abstract boolean isConfigurable();
	
	public abstract boolean canPassed(final ResultObject result);
	
	public abstract boolean validate();
	
	public abstract boolean isPassed(final ResultObject result);
	
	public abstract String getDefaultDisplayName();
	
	public Role getDefaultRole() {
		return Role.NONE;
	}
	
	public String computeResultStatistics(ResultStatement result) {
		return "";
	}
	
	public ConfigureDeliveryDialog createConfigurationDialog( EditDeliveryPreferencesComposite editComposite) {
		return null;
	}
	
	public final ResultObject prepareMessage(final MessageObject message) {
		message.setPassed(getType().equals(DeliveryType.NORMAL) || getType().equals(DeliveryType.CONFIRM_RECEIPT));
		message.setConfirmed(!getType().equals(DeliveryType.CONFIRM_RECEIPT));
		message.setWorkflowType(getType());
		
		ResultObject result = createResult(message);

		return result;
	}
	
	protected ResultObject createResult(final MessageObject message) {
		if(getType().equals(DeliveryType.CONFIRM_RECEIPT) || getType().equals(DeliveryType.NORMAL)) {
			return null;
		}
		ResultObject result = new ResultObject();
		result.setParentWorkflowType(getType());
		return result;
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return this.enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the members
	 */
	public DeliveryMemberCollection getMembers() {
		return this.members;
	}
	
	public boolean containsMember(DeliveryMember member) {
		return getMembers().contains(member);
	}
	
	public boolean checkMessagesToPopup(MessageObject message, ResultObject result, String contactName, String username) {
		if(!message.getGiverUsername().equals(username)) {
			boolean isReplied = false;

			DeliveryResponseCollection collection = result.getResponses();
			if(collection.getCount()>0) {
				for(ResponseObject wr : collection) {
					if(wr.getLoginName().equals(username)) {
						isReplied = true;
					}
				}
			}
			if(!isReplied && result.isVotingOn()) {
				return true;
			}
		}
		return false;
	}
	
	public void fillMemberCollection(final MemberCollection allContacts) {
		for(MemberReferenceObject memberRef : allContacts) {
			DeliveryMember member = new DeliveryMember();
			member.setContactName(memberRef.getContactName());
			member.setLoginName(memberRef.getLoginName());
			member.setRole(getDefaultRole());
			getMembers().add(member);
		}
	}
	
	public void addNewMember(final MemberReferenceObject member) {
		DeliveryMember modelMember = new DeliveryMember();
		modelMember.setContactName(member.getContactName());
		modelMember.setLoginName(member.getLoginName());
		modelMember.setRole(getDefaultRole());
		getMembers().add(modelMember);
	}
	
	public void addNewMember(SphereMember sphereMember) {
		DeliveryMember member = new DeliveryMember();
		member.setContactName(sphereMember.getMemberContactName());
		member.setLoginName(sphereMember.getMemberLogin());
		member.setRole(getDefaultRole());
		getMembers().add(member);
	}
	
	public void addNewMember(ContactObject contact) {
		DeliveryMember member = new DeliveryMember();
		member.setContactName(contact.getFirstName()+" "+contact.getLastName());
		member.setLoginName(contact.getLogin());
		member.setRole(getDefaultRole());
		getMembers().add(member);
	}
	
	public boolean isNotConfirmedOrNotPassed(MessageObject message,
			ResultObject result, String contactName, String username) {
		if (result == null) {
			return true;
		}
		return !isPassed(result);
	}
}
