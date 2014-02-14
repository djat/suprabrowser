/**
 * 
 */
package ss.client.preferences;

import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.preferences.SphereOwnPreferences;
import ss.domainmodel.preferences.UserPersonalPreferences;

/**
 * @author zobo
 *
 */
public class PreferencesAdmin {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PreferencesAdmin.class);
	
	private SphereOwnPreferences sphereOwnPreferences;
	
	private String sphereId = "";
	
	private UserPersonalPreferences userPreferences;
	
	private String username = "";
	
	public PreferencesAdmin(){
		
	}
	
	/**
	 * @param sphereId2
	 * @param user
	 */
	public void init(String sphereId2, String user) {
		if (sphereId2 != null){
			if (logger.isDebugEnabled()){
				logger.debug("Getting from server SphereOwnPreferences for sphereId: " + sphereId2);
			}
			this.sphereId = sphereId2;
			this.sphereOwnPreferences = SsDomain.SPHERE_HELPER.getSpherePreferences( this.sphereId );
		}
		if (user != null){
			if (logger.isDebugEnabled()){
				logger.debug("Getting from server UserPersonalPreferences for username: " + user);
			}
			this.username = user;
			this.userPreferences = SsDomain.MEMBER_HELPER.getMemberPreferences( this.username );
		}
	}

	
	private SphereOwnPreferences getForSphere(String sphereId){
		if (!this.sphereId.equals(sphereId)){
			if (logger.isDebugEnabled()){
				logger.debug("Getting from server SphereOwnPreferences for sphereId: " + sphereId);
			}
			this.sphereId = sphereId;
			this.sphereOwnPreferences = SsDomain.SPHERE_HELPER.getSpherePreferences( this.sphereId );
		}
		return this.sphereOwnPreferences;
	}
	
	private UserPersonalPreferences getUserPreferences(String username){
		if (!this.username.equals(username)){
			if (logger.isDebugEnabled()){
				logger.debug("Getting from server UserPersonalPreferences for username: " + username);
			}
			this.username = username;
			this.userPreferences = SsDomain.MEMBER_HELPER.getMemberPreferences( this.username );
		}
		return this.userPreferences;
	}
	
	public boolean isNewMessageShouldOpenTab(String sphereId) {
		return getForSphere(sphereId).isNewMessageShouldOpenTab();
	}
	
	public boolean isNewMessageShouldOpenTabModify(String sphereId) {
		return getForSphere(sphereId).isNewMessageShouldOpenTab();
	}
	
	public boolean isReplyIsAlsoAPopUpToPopUp(String sphereId) {
		return getForSphere(sphereId).isReplyIsAlsoAPopUpToPopUp();
	}
	
	public boolean isReplyIsAlsoAPopUpToPopUpModify(String sphereId) {
		return getForSphere(sphereId).isReplyIsAlsoAPopUpToPopUpModify();
	}
	
	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isSystemTrayNotificationOfReplyModify(String sphereId) {
		return getForSphere(sphereId).isSystemTrayNotificationOfReplyModify();
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isSystemTrayNotificationOfFirstTimeSphere(String sphereId) {
		return getForSphere(sphereId).isSystemTrayNotificationOfFirstTimeSphere();
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isSystemTrayNotificationOfReply(String sphereId) {
		return getForSphere(sphereId).isSystemTrayNotificationOfReply();
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isSystemTrayNotificationOfFirstTimeSphereModify(String sphereId) {
		return getForSphere(sphereId).isSystemTrayNotificationOfFirstTimeSphereModify();
	}

	/**
	 * @param userName
	 * @return
	 */
	public boolean isCanChangeDefaultTypeForSphere(String userName) {
		return getUserPreferences(userName).isCanChangeDefaultTypeForSphere();
	}

	/**
	 * @param userName
	 * @return
	 */
	public boolean isNormalMessageSoundPlay(String userName) {
		return getUserPreferences(userName).isNormalMessageSoundPlay();
	}

	/**
	 * @param userName
	 * @return
	 */
	public boolean isNormalMessageSoundPlayModify(String userName) {
		return getUserPreferences(userName).isNormalMessageSoundPlayModify();
	}

	/**
	 * @param userName
	 * @return
	 */
	public boolean isConfirmRecieptMessageSoundPlay(String userName) {
		return getUserPreferences(userName).isConfirmRecieptMessageSoundPlay();
	}

	/**
	 * @param userName
	 * @return
	 */
	public boolean isConfirmRecieptMessageSoundPlayModify(String userName) {
		return getUserPreferences(userName).isConfirmRecieptMessageSoundPlayModify();
	}

	/**
	 * @return the sphereOwnPreferences
	 */
	public SphereOwnPreferences getSphereOwnPreferences() {
		return this.sphereOwnPreferences;
	}

	/**
	 * @return the userPreferences
	 */
	public UserPersonalPreferences getUserPreferences() {
		return this.userPreferences;
	}

	/**
	 * @return the sphereId
	 */
	public String getSphereId() {
		return this.sphereId;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @param userName2
	 * @return
	 */
	public boolean isCanChangeDefaultDeliveryForP2PSphere(String userName) {
		return getUserPreferences(userName).isP2pSpheresDefaultDeliveryTypeModify();
	}

	/**
	 * @param userName2
	 * @return
	 */
	public String getDefaultDeliveryTypeForP2PSphere(String userName) {
		return getUserPreferences(userName).getP2pSpheresDefaultDeliveryType();
	}

	/**
	 * @param userName2
	 * @return
	 */
	public boolean isPopUpBehaviorModify(String userName) {
		return getUserPreferences(userName).ispopUpOnTopModify();
	}

	/**
	 * @param userName2
	 * @return
	 */
	public boolean getPopUpBehaviorValue(String userName) {
		return getUserPreferences(userName).ispopUpOnTop();
	}
}
