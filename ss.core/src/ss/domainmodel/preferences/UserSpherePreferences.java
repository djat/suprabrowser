/**
 * 
 */
package ss.domainmodel.preferences;

import ss.domainmodel.preferences.emailforwarding.EmailForwardingPreferencesUserSphere;
import ss.framework.entities.IComplexEntityProperty;
import ss.framework.entities.ISimpleEntityProperty;
import ss.framework.entities.xmlentities.XmlEntityObject;

/**
 * @author zobo
 * 
 */
public class UserSpherePreferences extends XmlEntityObject {

	private final ISimpleEntityProperty sphereId = super
			.createAttributeProperty("@sphere-id");

	private final ISimpleEntityProperty newMessageShouldOpenTab = super
			.createAttributeProperty("newMessageShouldOpenTab/@value");
	
	private final ISimpleEntityProperty newMessageShouldOpenTabModify = super
	.createAttributeProperty("newMessageShouldOpenTab/@modify");

	private final ISimpleEntityProperty canChangeEmailForwardingRules = super
			.createAttributeProperty("canChangeEmailForwardingRules/@value");

	private final ISimpleEntityProperty replyIsAlsoAPopUpToPopUp = super
			.createAttributeProperty("replyIsAlsoAPopUpToPopUp/@value");
	
	private final ISimpleEntityProperty replyIsAlsoAPopUpToPopUpModify = super
	.createAttributeProperty("replyIsAlsoAPopUpToPopUp/@modify");

	private final ISimpleEntityProperty systemTrayNotificationOfFirstTimeSphere = super
			.createAttributeProperty("systemTrayNotificationOfFirstTimeSphere/@value");
	
	private final ISimpleEntityProperty systemTrayNotificationOfFirstTimeSphereModify = super
			.createAttributeProperty("systemTrayNotificationOfFirstTimeSphere/@modify");

	private final ISimpleEntityProperty systemTrayNotificationOfReply = super
			.createAttributeProperty("systemTrayNotificationOfReply/@value");
	
	private final ISimpleEntityProperty systemTrayNotificationOfReplyModify = super
			.createAttributeProperty("systemTrayNotificationOfReply/@modify");
	
	private final IComplexEntityProperty<EmailForwardingPreferencesUserSphere> emailForwardingPreferences = super
		.createComplexProperty("emailForwardingPreferences",
				EmailForwardingPreferencesUserSphere.class);

	/**
	 * @return the emailForwardingPreferences
	 */
	public EmailForwardingPreferencesUserSphere getEmailForwardingPreferences() {
		return this.emailForwardingPreferences.getValue();
	}

	public UserSpherePreferences() {
		super();
	}

	/**
	 * @return the canChangeEmailForwardingRules
	 */
	public boolean isCanChangeEmailForwardingRules() {
		return this.canChangeEmailForwardingRules.getBooleanValue( false );
	}

	/**
	 * @param canChangeEmailForwardingRules
	 *            the canChangeEmailForwardingRules to set
	 */
	public void setCanChangeEmailForwardingRules(
			boolean canChangeEmailForwardingRules) {
		this.canChangeEmailForwardingRules
				.setBooleanValue(canChangeEmailForwardingRules);
	}

	/**
	 * @return the newMessageShouldOpenTab
	 */
	public boolean isNewMessageShouldOpenTab() {
		return this.newMessageShouldOpenTab.getBooleanValue( true );
	}

	/**
	 * @param newMessageShouldOpenTab
	 *            the newMessageShouldOpenTab to set
	 */
	public void setNewMessageShouldOpenTab(boolean newMessageShouldOpenTab) {
		this.newMessageShouldOpenTab.setBooleanValue(newMessageShouldOpenTab);
	}

	/**
	 * @return the replyIsAlsoAPopUpToPopUp
	 */
	public boolean isReplyIsAlsoAPopUpToPopUp() {
		return this.replyIsAlsoAPopUpToPopUp.getBooleanValue( true );
	}

	/**
	 * @param replyIsAlsoAPopUpToPopUp
	 *            the replyIsAlsoAPopUpToPopUp to set
	 */
	public void setReplyIsAlsoAPopUpToPopUp(boolean replyIsAlsoAPopUpToPopUp) {
		this.replyIsAlsoAPopUpToPopUp.setBooleanValue(replyIsAlsoAPopUpToPopUp);
	}

	/**
	 * @return the systemTrayNotificationOfFirstTimeSphere
	 */
	public boolean isSystemTrayNotificationOfFirstTimeSphere() {
		return this.systemTrayNotificationOfFirstTimeSphere.getBooleanValue( true );
	}

	/**
	 * @param systemTrayNotificationOfFirstTimeSphere
	 *            the systemTrayNotificationOfFirstTimeSphere to set
	 */
	public void setSystemTrayNotificationOfFirstTimeSphere(
			boolean systemTrayNotificationOfFirstTimeSphere) {
		this.systemTrayNotificationOfFirstTimeSphere
				.setBooleanValue(systemTrayNotificationOfFirstTimeSphere);
	}

	/**
	 * @return the systemTrayNotificationOfReply
	 */
	public boolean isSystemTrayNotificationOfReply() {
		return this.systemTrayNotificationOfReply.getBooleanValue( true );
	}

	/**
	 * @param systemTrayNotificationOfReply
	 *            the systemTrayNotificationOfReply to set
	 */
	public void setSystemTrayNotificationOfReply(
			boolean systemTrayNotificationOfReply) {
		this.systemTrayNotificationOfReply
				.setBooleanValue(systemTrayNotificationOfReply);
	}

	/**
	 * @return the sphereId
	 */
	public String getSphereId() {
		return this.sphereId.getValue();
	}

	/**
	 * @param sphereId
	 *            the sphereId to set
	 */
	public void setSphereId(String sphereId) {
		this.sphereId.setValue(sphereId);
	}

	/**
	 * @return the newMessageShouldOpenTabModify
	 */
	public boolean isNewMessageShouldOpenTabModify() {
		return this.newMessageShouldOpenTabModify.getBooleanValue( true );
	}

	/**
	 * @param newMessageShouldOpenTabModify the newMessageShouldOpenTabModify to set
	 */
	public void setNewMessageShouldOpenTabModify(
			boolean modify) {
		this.newMessageShouldOpenTabModify.setBooleanValue(modify);
	}

	/**
	 * @return the replyIsAlsoAPopUpToPopUpModify
	 */
	public boolean isReplyIsAlsoAPopUpToPopUpModify() {
		return this.replyIsAlsoAPopUpToPopUpModify.getBooleanValue( false );
	}

	/**
	 * @param replyIsAlsoAPopUpToPopUpModify the replyIsAlsoAPopUpToPopUpModify to set
	 */
	public void setReplyIsAlsoAPopUpToPopUpModify(
			boolean modify) {
		this.replyIsAlsoAPopUpToPopUpModify.setBooleanValue(modify);
	}

	/**
	 * @return the systemTrayNotificationOfFirstTimeSphereModify
	 */
	public boolean isSystemTrayNotificationOfFirstTimeSphereModify() {
		return this.systemTrayNotificationOfFirstTimeSphereModify.getBooleanValue( true );
	}

	/**
	 * @param systemTrayNotificationOfFirstTimeSphereModify the systemTrayNotificationOfFirstTimeSphereModify to set
	 */
	public void setSystemTrayNotificationOfFirstTimeSphereModify(
			boolean modify) {
		this.systemTrayNotificationOfFirstTimeSphereModify.setBooleanValue(modify);
	}

	/**
	 * @return the systemTrayNotificationOfReplyModify
	 */
	public boolean isSystemTrayNotificationOfReplyModify() {
		return this.systemTrayNotificationOfReplyModify.getBooleanValue( true );
	}

	/**
	 * @param systemTrayNotificationOfReplyModify the systemTrayNotificationOfReplyModify to set
	 */
	public void setSystemTrayNotificationOfReplyModify(
			boolean modify) {
		this.systemTrayNotificationOfReplyModify.setBooleanValue(modify);
	}
}
