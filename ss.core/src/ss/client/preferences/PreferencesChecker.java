/**
 * 
 */
package ss.client.preferences;

import java.util.Hashtable;

import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.preferences.SphereOwnPreferences;
import ss.domainmodel.preferences.UserPersonalPreferences;
import ss.domainmodel.preferences.UserSpherePreferences;

/**
 * @author zobo
 *
 */
public class PreferencesChecker {

	private Hashtable< String , SphereOwnPreferences> sphereOwnPreferences;
	
	private Hashtable< String , UserSpherePreferences> userSpherePreferences;
	
	private UserPersonalPreferences personalPreferences = null;
	
	private String username;
	
	private boolean admin;
	
	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PreferencesChecker.class);
	
	public PreferencesChecker(String username, boolean admin){
		this.username = username;
		this.admin = admin;
		this.personalPreferences = null;
		this.sphereOwnPreferences = new Hashtable< String , SphereOwnPreferences>();
		this.userSpherePreferences = new Hashtable< String , UserSpherePreferences>();
	}
	
	private SphereOwnPreferences getSphereOwnPreferences(String sphereId){
		SphereOwnPreferences pref = this.sphereOwnPreferences.get(sphereId);
		if (pref != null) {
			return pref;
		}
		return addSphereOwnPreferences(sphereId);
	}
	
	private UserSpherePreferences getUserSpherePreferences(String sphereId, String username){
		UserSpherePreferences pref = this.userSpherePreferences.get(sphereId);
		if (pref != null) {
			return pref;
		}
		return addUserSpherePreferences(sphereId,username);
	}
	
	private UserPersonalPreferences getPersonalPreferences(){
		if (this.personalPreferences == null){
			this.personalPreferences = SsDomain.MEMBER_HELPER.getMemberPreferences( this.username );
		}
		return this.personalPreferences;
	}
	
	private SphereOwnPreferences addSphereOwnPreferences(String sphereId){
		SphereOwnPreferences pref = SsDomain.SPHERE_HELPER.getSpherePreferences( sphereId );
		this.sphereOwnPreferences.put( sphereId , pref );
		return pref;
	}
	
	private UserSpherePreferences addUserSpherePreferences(String sphereId, String username){
		UserSpherePreferences pref = SsDomain.INVITED_MEMBER_HELPER.getInvitedMemberPreferences(sphereId, username);
		this.userSpherePreferences.put(sphereId, pref);
		return pref;
	}
	
	public boolean isCanChangeDefaultTypeForSphere() {
		if (this.admin) {
			return true;
		}
		return getPersonalPreferences().isCanChangeDefaultTypeForSphere();
	}

	public boolean isNewMessageShouldOpenTab(String sphereId) {
		SphereOwnPreferences pref = getSphereOwnPreferences(sphereId);
		if (!pref.isNewMessageShouldOpenTabModify()){
			return pref.isNewMessageShouldOpenTab();
		}
		return getUserSpherePreferences(sphereId, this.username).isNewMessageShouldOpenTab();
	}

	public boolean isReplyIsAlsoAPopUpToPopUp(String sphereId) {
		SphereOwnPreferences pref = getSphereOwnPreferences(sphereId);
		if (!pref.isReplyIsAlsoAPopUpToPopUpModify()){
			return pref.isReplyIsAlsoAPopUpToPopUp();
		}
		return getUserSpherePreferences(sphereId, this.username).isReplyIsAlsoAPopUpToPopUp();
	}

	public boolean isSystemTrayNotificationOfFirstTimeSphere(String sphereId) {
		SphereOwnPreferences pref = getSphereOwnPreferences(sphereId);
		if (!pref.isSystemTrayNotificationOfFirstTimeSphereModify()){
			return pref.isSystemTrayNotificationOfFirstTimeSphere();
		}
		return getUserSpherePreferences(sphereId, this.username).isSystemTrayNotificationOfFirstTimeSphere();
	}

	public boolean isSystemTrayNotificationOfReply(String sphereId) {
		SphereOwnPreferences pref = getSphereOwnPreferences(sphereId);
		if (!pref.isSystemTrayNotificationOfReplyModify()){
			return pref.isSystemTrayNotificationOfReply();
		}
		return getUserSpherePreferences(sphereId, this.username).isSystemTrayNotificationOfReply();
	}

	public boolean isConfirmRecieptMessageSoundPlay() {
		return getPersonalPreferences().isConfirmRecieptMessageSoundPlay();
	}

	public boolean isNormalMessageSoundPlay() {
		return getPersonalPreferences().isNormalMessageSoundPlay();
	}

	/**
	 * @return
	 */
	public String getDefaultDeliveryP2PSpheres() {
		return getPersonalPreferences().getP2pSpheresDefaultDeliveryType();
	}

	/**
	 * @return
	 */
	public boolean getDefaultDeliveryP2PSpheresModify() {
		return getPersonalPreferences().isP2pSpheresDefaultDeliveryTypeModify();
	}

	/**
	 * @return
	 */
	public boolean isPopUpOnTop() {
		return getPersonalPreferences().ispopUpOnTop();
	}
}
