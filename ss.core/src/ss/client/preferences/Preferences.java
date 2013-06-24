/**
 * 
 */
package ss.client.preferences;

import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.preferences.SphereOwnPreferences;
import ss.domainmodel.preferences.UserPersonalPreferences;
import ss.domainmodel.preferences.UserSpherePreferences;
import ss.framework.entities.xmlentities.XmlEntityUtils;

/**
 * @author zobo
 *
 */
public class Preferences {
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(Preferences.class);
	
	private UserPersonalPreferences personalPreferences;
	
	private SpherePreferences spherePreferences;
	
	private String username;
	
	private String sphereId = "";
	
	private class SpherePreferences {
		public SpherePreferences(){
			
		}
		
		public UserSpherePreferences user;
		
		public SphereOwnPreferences own;
		
		public boolean isNewMessageShouldOpenTab(){
			if (this.own.isNewMessageShouldOpenTabModify()){
				return this.user.isNewMessageShouldOpenTab();
			}
			return this.own.isNewMessageShouldOpenTab();
		}

		/**
		 * @return
		 */
		public boolean isSystemTrayNotificationOfFirstTimeSphereModify() {
			return this.own.isSystemTrayNotificationOfFirstTimeSphereModify();
		}

		/**
		 * @return
		 */
		public boolean isSystemTrayNotificationOfReplyModify() {
			return this.own.isSystemTrayNotificationOfReplyModify();
		}

		/**
		 * @return
		 */
		public boolean isReplyIsAlsoAPopUpToPopUpModify() {
			return this.own.isReplyIsAlsoAPopUpToPopUpModify();
		}

		/**
		 * @return
		 */
		public boolean isNewMessageShouldOpenTabModify() {
			return this.own.isNewMessageShouldOpenTabModify();
		}

		/**
		 * @return
		 */
		public boolean isReplyIsAlsoAPopUpToPopUp() {
			if (this.own.isReplyIsAlsoAPopUpToPopUpModify()){
				return this.user.isReplyIsAlsoAPopUpToPopUp();
			}
			return this.own.isReplyIsAlsoAPopUpToPopUp();
		}

		/**
		 * @return
		 */
		public boolean isSystemTrayNotificationOfFirstTimeSphere() {
			if (this.own.isSystemTrayNotificationOfFirstTimeSphereModify()){
				return this.user.isSystemTrayNotificationOfFirstTimeSphere();
			}
			return this.own.isSystemTrayNotificationOfFirstTimeSphere();
		}

		/**
		 * @return
		 */
		public boolean isSystemTrayNotificationOfReply() {
			if (this.own.isSystemTrayNotificationOfReplyModify()){
				return this.user.isSystemTrayNotificationOfReply();
			}
			return this.own.isSystemTrayNotificationOfReply();
		}
	}
	
	public void init(String username, String sphereId){
		this.username = username;
		this.sphereId = sphereId;
		this.personalPreferences = SsDomain.MEMBER_HELPER.getMemberPreferences( this.username );
		this.spherePreferences = new SpherePreferences();
		this.spherePreferences.user = SsDomain.INVITED_MEMBER_HELPER.getInvitedMemberPreferences( this.sphereId, this.username );
		this.spherePreferences.own  = SsDomain.SPHERE_HELPER.getSpherePreferences(this.sphereId);
		if ( logger.isDebugEnabled() ) 		{
			 logger.debug(XmlEntityUtils.entityToString(this.personalPreferences));
		}		
	}
	
	private SpherePreferences getSpherePreferences(String sphereId){
		if (!this.sphereId.equals(sphereId)){
			if ( logger.isDebugEnabled() ) 		{
				 logger.debug( "Getting from server SpherePreferences for sphereId: " + sphereId );
			}	
			this.sphereId = sphereId;
			this.spherePreferences.user = SsDomain.INVITED_MEMBER_HELPER.getInvitedMemberPreferences( this.sphereId, this.username );
			this.spherePreferences.own  = SsDomain.SPHERE_HELPER.getSpherePreferences(this.sphereId);
		}
		return this.spherePreferences;
	}
	
	public UserSpherePreferences getSpherePreferencesCurrentToSave(){
		return this.spherePreferences.user;
	}

	/**
	 * @param sphereID
	 */
	public boolean isNewMessageShouldOpenTab(String sphereId) {
		return getSpherePreferences(sphereId).isNewMessageShouldOpenTab();
	}

	/**
	 * @return
	 */
	public boolean isCanChangeDefaultTypeForSphere() {
		return this.personalPreferences.isCanChangeDefaultTypeForSphere();
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isReplyIsAlsoAPopUpToPopUp(String sphereId) {
		return getSpherePreferences(sphereId).isReplyIsAlsoAPopUpToPopUp();
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isSystemTrayNotificationOfFirstTimeSphere(String sphereId) {
		return getSpherePreferences(sphereId).isSystemTrayNotificationOfFirstTimeSphere();
	}

	/**
	 * @return
	 */
	public boolean isSystemTrayNotificationOfReply(String sphereId) {
		return getSpherePreferences(sphereId).isSystemTrayNotificationOfReply();
	}

	/**
	 * @return
	 */
	public boolean isConfirmRecieptMessageSoundPlay() {
		return this.personalPreferences.isConfirmRecieptMessageSoundPlay();
	}

	/**
	 * @return
	 */
	public boolean isNormalMessageSoundPlay() {
		return this.personalPreferences.isNormalMessageSoundPlay();
	}

	/**
	 * @return the globalPreferences
	 */
	public UserPersonalPreferences getPersonalPreferences() {
		return this.personalPreferences;
	}

	/**
	 * @param globalPreferences the globalPreferences to set
	 */
	public void setPersonalPreferences(UserPersonalPreferences globalPreferences) {
		this.personalPreferences = globalPreferences;
	}

	/**
	 * @return
	 */
	public boolean isNormalMessageSoundPlayModify() {
		return this.personalPreferences.isNormalMessageSoundPlayModify();
	}

	/**
	 * @return
	 */
	public boolean isConfirmRecieptMessageSoundPlayModify() {
		return this.personalPreferences.isConfirmRecieptMessageSoundPlayModify();
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isSystemTrayNotificationOfFirstTimeSphereModify(String sphereId) {
		return getSpherePreferences(sphereId).isSystemTrayNotificationOfFirstTimeSphereModify();
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isSystemTrayNotificationOfReplyModify(String sphereId) {
		return getSpherePreferences(sphereId).isSystemTrayNotificationOfReplyModify();
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isReplyIsAlsoAPopUpToPopUpModify(String sphereId) {
		return getSpherePreferences(sphereId).isReplyIsAlsoAPopUpToPopUpModify();
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isNewMessageShouldOpenTabModify(String sphereId) {
		return getSpherePreferences(sphereId).isNewMessageShouldOpenTabModify();
	}

	/**
	 * @return
	 */
	public boolean isCanChangeDefaultDeliveryForP2PSphere() {
		return this.personalPreferences.isP2pSpheresDefaultDeliveryTypeModify();
	}

	/**
	 * @return
	 */
	public String getDefaultDeliveryTypeForP2PSphere() {
		return this.personalPreferences.getP2pSpheresDefaultDeliveryType();
	}

	/**
	 * @return
	 */
	public boolean getPopUpBehaviorValue() {
		return this.personalPreferences.ispopUpOnTop();
	}

	/**
	 * @return
	 */
	public boolean isPopUpBehaviorModify() {
		return this.personalPreferences.ispopUpOnTopModify();
	}
}
