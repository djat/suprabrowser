/**
 * 
 */
package ss.common.domain.model.preferences;

import ss.common.domain.model.DomainObject;
import ss.common.domain.model.DomainReference;

/**
 * @author roman
 *
 */
public class UserSpherePreferences extends DomainObject {

	private String sphereId;
	
	private boolean newMessageShouldOpenTab;
	
	private boolean newMessageShouldOpenTabModify;
	
	private boolean canChangeEmailForwardingRules;
	
	private boolean replyIsAlsoAPopUpToPopUp;
	
	private boolean replyIsAlsoAPopUpToPopUpModify;
	
	private boolean systemTrayNotificationOfFirstTimeSphere;
	
	private boolean systemTrayNotificationOfFirstTimeSphereModify;
	
	private boolean systemTrayNotificationOfReply;
	
	private boolean systemTrayNotificationOfReplyModify;
	
	private final DomainReference<EmailForwardingPreferencesUserSphere> emailForwardingRef = DomainReference.create(EmailForwardingPreferencesUserSphere.class);

	/**
	 * @return the sphereId
	 */
	public String getSphereId() {
		return this.sphereId;
	}

	/**
	 * @param sphereId the sphereId to set
	 */
	public void setSphereId(String sphereId) {
		this.sphereId = sphereId;
	}

	/**
	 * @return the newMessageShouldOpenTab
	 */
	public boolean isNewMessageShouldOpenTab() {
		return this.newMessageShouldOpenTab;
	}

	/**
	 * @param newMessageShouldOpenTab the newMessageShouldOpenTab to set
	 */
	public void setNewMessageShouldOpenTab(boolean newMessageShouldOpenTab) {
		this.newMessageShouldOpenTab = newMessageShouldOpenTab;
	}

	/**
	 * @return the newMessageShouldOpenTabModify
	 */
	public boolean isNewMessageShouldOpenTabModify() {
		return this.newMessageShouldOpenTabModify;
	}

	/**
	 * @param newMessageShouldOpenTabModify the newMessageShouldOpenTabModify to set
	 */
	public void setNewMessageShouldOpenTabModify(
			boolean newMessageShouldOpenTabModify) {
		this.newMessageShouldOpenTabModify = newMessageShouldOpenTabModify;
	}

	/**
	 * @return the canChangeEmailForwardingRules
	 */
	public boolean isCanChangeEmailForwardingRules() {
		return this.canChangeEmailForwardingRules;
	}

	/**
	 * @param canChangeEmailForwardingRules the canChangeEmailForwardingRules to set
	 */
	public void setCanChangeEmailForwardingRules(
			boolean canChangeEmailForwardingRules) {
		this.canChangeEmailForwardingRules = canChangeEmailForwardingRules;
	}

	/**
	 * @return the replyIsAlsoAPopUpToPopUp
	 */
	public boolean isReplyIsAlsoAPopUpToPopUp() {
		return this.replyIsAlsoAPopUpToPopUp;
	}

	/**
	 * @param replyIsAlsoAPopUpToPopUp the replyIsAlsoAPopUpToPopUp to set
	 */
	public void setReplyIsAlsoAPopUpToPopUp(boolean replyIsAlsoAPopUpToPopUp) {
		this.replyIsAlsoAPopUpToPopUp = replyIsAlsoAPopUpToPopUp;
	}

	/**
	 * @return the replyIsAlsoAPopUpToPopUpModify
	 */
	public boolean isReplyIsAlsoAPopUpToPopUpModify() {
		return this.replyIsAlsoAPopUpToPopUpModify;
	}

	/**
	 * @param replyIsAlsoAPopUpToPopUpModify the replyIsAlsoAPopUpToPopUpModify to set
	 */
	public void setReplyIsAlsoAPopUpToPopUpModify(
			boolean replyIsAlsoAPopUpToPopUpModify) {
		this.replyIsAlsoAPopUpToPopUpModify = replyIsAlsoAPopUpToPopUpModify;
	}

	/**
	 * @return the systemTrayNotificationOfFirstTimeSphere
	 */
	public boolean isSystemTrayNotificationOfFirstTimeSphere() {
		return this.systemTrayNotificationOfFirstTimeSphere;
	}

	/**
	 * @param systemTrayNotificationOfFirstTimeSphere the systemTrayNotificationOfFirstTimeSphere to set
	 */
	public void setSystemTrayNotificationOfFirstTimeSphere(
			boolean systemTrayNotificationOfFirstTimeSphere) {
		this.systemTrayNotificationOfFirstTimeSphere = systemTrayNotificationOfFirstTimeSphere;
	}

	/**
	 * @return the systemTrayNotificationOfFirstTimeSphereModify
	 */
	public boolean isSystemTrayNotificationOfFirstTimeSphereModify() {
		return this.systemTrayNotificationOfFirstTimeSphereModify;
	}

	/**
	 * @param systemTrayNotificationOfFirstTimeSphereModify the systemTrayNotificationOfFirstTimeSphereModify to set
	 */
	public void setSystemTrayNotificationOfFirstTimeSphereModify(
			boolean systemTrayNotificationOfFirstTimeSphereModify) {
		this.systemTrayNotificationOfFirstTimeSphereModify = systemTrayNotificationOfFirstTimeSphereModify;
	}

	/**
	 * @return the systemTrayNotificationOfReply
	 */
	public boolean isSystemTrayNotificationOfReply() {
		return this.systemTrayNotificationOfReply;
	}

	/**
	 * @param systemTrayNotificationOfReply the systemTrayNotificationOfReply to set
	 */
	public void setSystemTrayNotificationOfReply(
			boolean systemTrayNotificationOfReply) {
		this.systemTrayNotificationOfReply = systemTrayNotificationOfReply;
	}

	/**
	 * @return the systemTrayNotificationOfReplyModify
	 */
	public boolean isSystemTrayNotificationOfReplyModify() {
		return this.systemTrayNotificationOfReplyModify;
	}

	/**
	 * @param systemTrayNotificationOfReplyModify the systemTrayNotificationOfReplyModify to set
	 */
	public void setSystemTrayNotificationOfReplyModify(
			boolean systemTrayNotificationOfReplyModify) {
		this.systemTrayNotificationOfReplyModify = systemTrayNotificationOfReplyModify;
	}

	/**
	 * @return the emailForwardingRef
	 */
	public DomainReference<EmailForwardingPreferencesUserSphere> getEmailForwardingRef() {
		return this.emailForwardingRef;
	}
	
	public EmailForwardingPreferencesUserSphere getEmailForwardingPreferences() {
		return this.emailForwardingRef.get();
	}
	
	public void setEmailForwardingPreferences(EmailForwardingPreferencesUserSphere preferences) {
		this.emailForwardingRef.set(preferences);
	}
}
