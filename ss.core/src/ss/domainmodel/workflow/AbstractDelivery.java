/**
 * 
 */
package ss.domainmodel.workflow;

import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import ss.client.localization.LocalizationLinks;
import ss.client.ui.preferences.delivery.ConfigureDeliveryDialog;
import ss.client.ui.preferences.delivery.EditDeliveryPreferencesComposite;
import ss.common.protocolobjects.MemberVisibilityProtocolObject.SphereMember;
import ss.domainmodel.ContactStatement;
import ss.domainmodel.MemberReference;
import ss.domainmodel.ResultStatement;
import ss.domainmodel.Statement;
import ss.domainmodel.WorkflowResponse;
import ss.domainmodel.WorkflowResponseCollection;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;
import ss.global.SSLogger;
import ss.util.SupraXMLConstants;
import ss.util.VariousUtils;

/**
 * @author roman
 *
 */
public abstract class AbstractDelivery extends XmlEntityObject {
	
	@SuppressWarnings("unused")
	private static final Logger logger = SSLogger.getLogger(AbstractDelivery.class);
	
	protected static ResourceBundle BUNDLE = ResourceBundle.getBundle(LocalizationLinks.DOMAINMODEL_WORKFLOW_ABSTRACTDELIVERY);
	
	public static String ROOT_ELEMENT_NAME = "delivery_model";
	
	private final ISimpleEntityProperty description = super
    	.createTextProperty( "description" );
	
	private final ISimpleEntityProperty displayName = super
		.createAttributeProperty( "display_name/@value" );
	
	protected final ISimpleEntityProperty enabled = super
		.createAttributeProperty( "enabled/@value" );
		
	private final ModelMemberCollection modelMembers = super
		.bindListProperty( new ModelMemberCollection() );
	
	public AbstractDelivery() {
		super(ROOT_ELEMENT_NAME);
	}
	
	protected abstract String getDefaultDisplayName() ;

	protected String getType() {
		return DeliveryFactory.INSTANCE.getDeliveryTypeByDeliveryClass(getClass());
	}
	
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return this.displayName.getValueOrDefault(getDefaultDisplayName());
	}
		
	/**
	 * @return sets the delivery displayName
	 */
	public void setDisplayName(String value) {
		this.displayName.setValue(value);
	}

	public void setDescription(String value) {
		this.description.setValue(value);
	}
	
	public String getDescription() {
		return this.description.getValue();
	}
	
	public void setEnabled(boolean value) {
		this.enabled.setBooleanValue(value);
	}
	
	public boolean isEnabled() {
		return this.enabled.getBooleanValue(false);
	}
	
	public ModelMemberCollection getMemberCollection() {
		return this.modelMembers;
	}
	
	public boolean validate() {	
		return true;
	}

	/**
	 * @param member
	 * @return
	 */
	public boolean containsMember(ModelMemberEntityObject member) {
		for(ModelMemberEntityObject tempMember : getMemberCollection()) {
			if(tempMember.getUserName().equals(member.getUserName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param editComposite
	 */
	public ConfigureDeliveryDialog createConfigurationDialog( EditDeliveryPreferencesComposite editComposite) {
		return null;
	}

	/**
	 * @param statement
	 * @return
	 */
	public boolean isPassed(ResultStatement statement) {
		return false;
	}

	/**
	 * @param statement
	 */
	public String computeResultStatistics(ResultStatement statement) {
		return "";
	}

	/**
	 * @param resultSt
	 * @return
	 */
	public boolean canPassed(ResultStatement resultSt) {
		return false;
	}

	/**
	 * @param newTerse
	 */
	public ResultStatement prepareStatement(Statement newStatement) {
		ResultStatement result = new ResultStatement();
		
		newStatement.setConfirmed(false);
		newStatement.setPassed(false);
			
		newStatement.setWorkflowType("abstract_delivery");
			
		result = createResultMessage(newStatement);
		return result;
	}

	/**
	 * @param allContacts
	 */
	public void fillMemberCollection(final List<MemberReference> allContacts) {
		for(MemberReference memberRef : allContacts) {
			ModelMemberEntityObject member = new ModelMemberEntityObject();
			member.setContactName(memberRef.getContactName());
			member.setUserName(memberRef.getLoginName());
			getMemberCollection().add(member);
		}
	}

	/**
	 * @param sphereMember
	 */
	public void addNewMember(SphereMember sphereMember) {
		ModelMemberEntityObject member = new ModelMemberEntityObject();
		member.setContactName(sphereMember.getMemberContactName());
		member.setUserName(sphereMember.getMemberLogin());
		getMemberCollection().add(member);
	}
	
	/**
	 * @param newStatement
	 */
	public static ResultStatement createResultMessage(Statement newStatement) {
		ResultStatement result = new ResultStatement();
		result.setMessageId(VariousUtils.createMessageId());
		result.setGiver(newStatement.getGiver());
		result.setGiverUsername(newStatement.getGiverUsername());
		result.setSubject(ResultStatement.VOTING_IS_ON_STRING);
		result.setOriginalId(result.getMessageId());
		result.setResponseId(newStatement.getMessageId());
		result.setThreadId(newStatement.getThreadId());
		result.setThreadType(newStatement.getThreadType());
		result.setType(SupraXMLConstants.TYPE_VALUE_RESULT);
		result.setOrigBody(newStatement.getSubject());

		result.setConfirmed(true);
		result.setVersion("3000");
		result.setVotingModelDesc("Absolute without qualification");
		result.setVotingModelType("absolute");
		result.setTallyNumber("0.0");
		result.setTallyValue("0.0");

		newStatement.setResultId(result.getMessageId());

		return result;
	}

	/**
	 * @return
	 */
	public boolean checkMessagesToPopup(Statement statement, ResultStatement result, String contactName, String username) {

		if(!statement.getGiverUsername().equals(username)) {
			boolean isReplied = false;

			WorkflowResponseCollection collection = result.getResponseCollection();
			if(collection.getCount()>0) {
				for(WorkflowResponse wr : collection) {
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
	
	public boolean isConfigurable() {
		return false;
	}

	/**
	 * @param sphereMember
	 */
	public void addNewMember(ss.domainmodel.SphereMember sphereMember) {
		ModelMemberEntityObject member = new ModelMemberEntityObject();
		member.setContactName(sphereMember.getContactName());
		member.setUserName(sphereMember.getLoginName());
		getMemberCollection().add(member);
	}

	/**
	 * @param statement
	 * @param result
	 * @param contactName
	 * @param username
	 * @return
	 */
	public boolean isNotConfirmedOrNotPassed(Statement statement,
			ResultStatement result, String contactName, String username) {
		if (result == null) {
			return true;
		}
		return !result.getSubject().equals(ResultStatement.PASSED);
	}

	/**
	 * @param contact
	 */
	public void addNewMember(ContactStatement contact) {
		ModelMemberEntityObject member = new ModelMemberEntityObject();
		member.setContactName(contact.getFirstName()+" "+contact.getLastName());
		member.setUserName(contact.getLogin());
		getMemberCollection().add(member);
	}

	/**
	 * @param sphereMember
	 */
	public void addNewMember(MemberReference sphereMember) {
		ModelMemberEntityObject member = new ModelMemberEntityObject();
		member.setContactName(sphereMember.getContactName());
		member.setUserName(sphereMember.getLoginName());
		getMemberCollection().add(member);
	}
	
}
