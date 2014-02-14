/**
 * 
 */
package ss.common.domain.model.message;

import java.util.Date;

import ss.common.domain.model.DomainObject;
import ss.common.domain.model.DomainReference;
import ss.common.domain.model.enums.MessageType;
import ss.common.domain.model.enums.DeliveryType;

/**
 * @author roman
 *
 */
public class MessageObject extends DomainObject {

	private long referenceId;
	
	private MessageType type;
	
	private String threadType;
	
	private String subject;
	
	private String giver;
	
	private String giverUsername;
	
	private BodyObject body;
	
	private final DomainReference<VotingModelObject> votingModelRef = DomainReference.create(VotingModelObject.class);
	
	private long originalId;
	
	private long messageId;
	
	private long threadId;
	
	private long responseId;
	
	private Date lastUpdated;
	
	private Date moment;
	
	private String currentSphere;
	
	private boolean passed;
	
	private boolean confirmed;
	
	private DeliveryType workflowType;
	
	private String lastUpdatedBy;

	/**
	 * @return the type
	 */
	public MessageType getType() {
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(MessageType type) {
		this.type = type;
	}

	/**
	 * @return the threadType
	 */
	public String getThreadType() {
		return this.threadType;
	}

	/**
	 * @param threadType the threadType to set
	 */
	public void setThreadType(String threadType) {
		this.threadType = threadType;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return this.subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the giver
	 */
	public String getGiver() {
		return this.giver;
	}

	/**
	 * @param giver the giver to set
	 */
	public void setGiver(String giver) {
		this.giver = giver;
	}

	/**
	 * @return the body
	 */
	public BodyObject getBody() {
		return this.body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(BodyObject body) {
		this.body = body;
	}
	
	/**
	 * @return the votingModelRef
	 */
	public DomainReference<VotingModelObject> getVotingModelRef() {
		return this.votingModelRef;
	}

	/**
	 * @return the votingModel
	 */
	public VotingModelObject getVotingModel() {
		return this.votingModelRef.get();
	}

	/**
	 * @param votingModel the votingModel to set
	 */
	public void setVotingModel(VotingModelObject votingModel) {
		this.votingModelRef.set(votingModel);
	}

	/**
	 * @return the originalId
	 */
	public long getOriginalId() {
		return this.originalId;
	}

	/**
	 * @param originalId the originalId to set
	 */
	public void setOriginalId(long originalId) {
		this.originalId = originalId;
	}

	/**
	 * @return the messageId
	 */
	public long getMessageId() {
		return this.messageId;
	}

	/**
	 * @param messageId the messageId to set
	 */
	public void setMessageId(long messageId) {
		this.messageId = messageId;
	}

	/**
	 * @return the threadId
	 */
	public long getThreadId() {
		return this.threadId;
	}

	/**
	 * @param threadId the threadId to set
	 */
	public void setThreadId(long threadId) {
		this.threadId = threadId;
	}

	/**
	 * @return the lastUpdated
	 */
	public Date getLastUpdated() {
		return this.lastUpdated;
	}

	/**
	 * @param lastUpdated the lastUpdated to set
	 */
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	/**
	 * @return the moment
	 */
	public Date getMoment() {
		return this.moment;
	}

	/**
	 * @param moment the moment to set
	 */
	public void setMoment(Date moment) {
		this.moment = moment;
	}

	/**
	 * @return the giverUsername
	 */
	public String getGiverUsername() {
		return this.giverUsername;
	}

	/**
	 * @param giverUsername the giverUsername to set
	 */
	public void setGiverUsername(String giverUsername) {
		this.giverUsername = giverUsername;
	}

	/**
	 * @return the currentSphere
	 */
	public String getCurrentSphere() {
		return this.currentSphere;
	}

	/**
	 * @param currentSphere the currentSphere to set
	 */
	public void setCurrentSphere(String currentSphere) {
		this.currentSphere = currentSphere;
	}

	/**
	 * @return the passed
	 */
	public boolean isPassed() {
		return this.passed;
	}

	/**
	 * @param passed the passed to set
	 */
	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	/**
	 * @return the confirmed
	 */
	public boolean isConfirmed() {
		return this.confirmed;
	}

	/**
	 * @param confirmed the confirmed to set
	 */
	public void setConfirmed(boolean confirmed) {
		this.confirmed = confirmed;
	}

	/**
	 * @return the workflowType
	 */
	public DeliveryType getWorkflowType() {
		return this.workflowType;
	}

	/**
	 * @param workflowType the workflowType to set
	 */
	public void setWorkflowType(DeliveryType workflowType) {
		this.workflowType = workflowType;
	}

	/**
	 * @return the responseId
	 */
	public long getResponseId() {
		return this.responseId;
	}

	/**
	 * @param responseId the responseId to set
	 */
	public void setResponseId(long responseId) {
		this.responseId = responseId;
	}

	/**
	 * @return the lastUpdatedBy
	 */
	public String getLastUpdatedBy() {
		return this.lastUpdatedBy;
	}

	/**
	 * @param lastUpdatedBy the lastUpdatedBy to set
	 */
	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	/**
	 * @param contactName
	 * @return
	 */
	public boolean hasVoted(String contactName) {
		return getVotingModel().getMemberCollection().containsContactName(contactName);
	}

	/**
	 * @return the referenceId
	 */
	public long getReferenceId() {
		return this.referenceId;
	}

	/**
	 * @param referenceId the referenceId to set
	 */
	public void setReferenceId(long referenceId) {
		this.referenceId = referenceId;
	}
}
