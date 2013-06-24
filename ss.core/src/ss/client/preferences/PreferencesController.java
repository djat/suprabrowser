/**
 * 
 */
package ss.client.preferences;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.preferences.GlobalPreferencesSimpleUserComposite;
import ss.client.ui.preferences.SpheresPreferencesComposite;
import ss.common.VerifyAuth;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.SphereItem;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.preferences.UserPersonalPreferences;
import ss.domainmodel.preferences.UserSpherePreferences;

/**
 * @author zobo
 * 
 */
public class PreferencesController {
	private Preferences preferences;

	private String username = "";

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PreferencesController.class);

	public PreferencesController() {
		this.preferences = new Preferences();
	}

	public boolean isCanChangeDefaultTypeForSphere() {
		return this.preferences.isCanChangeDefaultTypeForSphere();
	}

	public boolean isNewMessageShouldOpenTab(String sphereId) {
		return this.preferences.isNewMessageShouldOpenTab(sphereId);
	}

	public boolean isReplyIsAlsoAPopUpToPopUp(String sphereId) {
		return this.preferences.isReplyIsAlsoAPopUpToPopUp(sphereId);
	}

	public boolean isSystemTrayNotificationOfFirstTimeSphere(String sphereId) {
		return this.preferences
				.isSystemTrayNotificationOfFirstTimeSphere(sphereId);
	}

	public boolean isSystemTrayNotificationOfReply(String sphereId) {
		return this.preferences.isSystemTrayNotificationOfReply(sphereId);
	}

	public boolean isConfirmRecieptMessageSoundPlay() {
		return this.preferences.isConfirmRecieptMessageSoundPlay();
	}

	public boolean isNormalMessageSoundPlay() {
		return this.preferences.isNormalMessageSoundPlay();
	}

	public void init(String username) {
		logger.info("Username is: " + username);
		this.username = username;
		// this.preferences.init( this.username);
	}

	public void preloadData(final ForwardingController controller) {
		List<SphereItem> spheresReferences = getGroupSpheres();
		String supraSphere = SupraSphereFrame.INSTANCE.client.getVerifyAuth()
				.getSupraSphereName();
		String sphereId = null;
		for (SphereItem sItem : spheresReferences) {
			String sphereDisplayName = sItem.getDisplayName();
			if (!(supraSphere.equals(sphereDisplayName))) {
				sphereId = sItem.getSystemName();
				break;
			}
		}
		this.preferences.init(this.username, sphereId);
		controller.preloadData(sphereId);
	}

	/**
	 * @param value
	 * @param value2
	 */
	public void applyGlobal(
			GlobalPreferencesSimpleUserComposite preferencesComposite) {
		UserPersonalPreferences prefs = this.preferences
				.getPersonalPreferences();
		logger.info("Saving settings: " + prefs.getBindedDocument().asXML());
		prefs.setConfirmRecieptMessageSoundPlay(preferencesComposite
				.getConfirmRecieptMessageSoundPlay());
		prefs.setConfirmRecieptMessageSoundPlayModify(preferencesComposite
				.getConfirmRecieptMessageSoundPlayModify());
		prefs.setNormalMessageSoundPlay(preferencesComposite
				.getNormalMessageSoundPlay());
		prefs.setNormalMessageSoundPlayModify(preferencesComposite
				.getNormalMessageSoundPlayModify());
		prefs.setP2pSpheresDefaultDeliveryType(preferencesComposite
				.getDefaultDeliveryTypeForP2P());
		prefs.setP2pSpheresDefaultDeliveryTypeModify(preferencesComposite
				.getDefaultDeliveryTypeForP2PModify());
		prefs.setpopUpOnTop(preferencesComposite.getPopUpOnTop());
		prefs.setpopUpOnTopModify(preferencesComposite.getPopUpOnTopModify());
		logger.info("Saving settings: " + prefs.getBindedDocument().asXML());
		SsDomain.MEMBER_HELPER.setMemberPreferences(this.username, prefs);
	}

	/**
	 * @param composite
	 */
	public void applySphere(
			SpheresPreferencesComposite spheresPreferencesComposite,
			String sphereId) {
		logger.info("Saving settings for sphere: " + sphereId + ", and user: "
				+ this.username);
		UserSpherePreferences pref = this.preferences
				.getSpherePreferencesCurrentToSave();
		logger.info("Sattings was: " + pref.getBindedDocument().asXML());

		pref.setSphereId(sphereId);

		pref.setNewMessageShouldOpenTab(spheresPreferencesComposite
				.getNewMessageShouldOpenTab());
		pref.setNewMessageShouldOpenTabModify(spheresPreferencesComposite
				.getNewMessageShouldOpenTabModify());

		pref.setReplyIsAlsoAPopUpToPopUp(spheresPreferencesComposite
				.getReplyIsAlsoAPopUpToPopUp());
		pref.setReplyIsAlsoAPopUpToPopUpModify(spheresPreferencesComposite
				.getReplyIsAlsoAPopUpToPopUpModify());

		pref
				.setSystemTrayNotificationOfFirstTimeSphere(spheresPreferencesComposite
						.getSystemTrayNotificationOfFirstTimeSphere());
		pref
				.setSystemTrayNotificationOfFirstTimeSphereModify(spheresPreferencesComposite
						.getSystemTrayNotificationOfFirstTimeSphereModify());

		pref.setSystemTrayNotificationOfReply(spheresPreferencesComposite
				.getSystemTrayNotificationOfReply());
		pref.setSystemTrayNotificationOfReplyModify(spheresPreferencesComposite
				.getSystemTrayNotificationOfReplyModify());

		logger.info("Sattings to save: " + pref.getBindedDocument().asXML());
		SsDomain.INVITED_MEMBER_HELPER.setInvitedMemberPreferences(
				sphereId, this.username, pref);
	}

	public List<SphereItem> getGroupSpheres() {
		return SupraSphereFrame.INSTANCE.client.getVerifyAuth()
				.getAllGroupSpheres();
	}

	/**
	 * @return
	 */
	public boolean isNormalMessageSoundPlayModify() {
		return this.preferences.isNormalMessageSoundPlayModify();
	}

	/**
	 * @return
	 */
	public boolean isConfirmRecieptMessageSoundPlayModify() {
		return this.preferences.isConfirmRecieptMessageSoundPlayModify();
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isNewMessageShouldOpenTabModify(String sphereId) {
		return this.preferences.isNewMessageShouldOpenTabModify(sphereId);
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isReplyIsAlsoAPopUpToPopUpModify(String sphereId) {
		return this.preferences.isReplyIsAlsoAPopUpToPopUpModify(sphereId);
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isSystemTrayNotificationOfFirstTimeSphereModify(
			String sphereId) {
		return this.preferences
				.isSystemTrayNotificationOfFirstTimeSphereModify(sphereId);
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isSystemTrayNotificationOfReplyModify(String sphereId) {
		return this.preferences.isSystemTrayNotificationOfReplyModify(sphereId);
	}

	/**
	 * @return
	 */
	public List<String> getUsers() {
		Hashtable session = SupraSphereFrame.INSTANCE.getMainRawSession();
		String userName = (String) session.get("username");
		VerifyAuth verify = SupraSphereFrame.INSTANCE.client.getVerifyAuth();
		List<String> users = new ArrayList<String>();
		String tempUser;
		for (SupraSphereMember supraMember : verify.getSupraSphere()
				.getSupraMembers()) {
			tempUser = supraMember.getLoginName();
			if (!(userName.equals(tempUser))) {
				users.add(tempUser);
			}
		}
		return users;
	}

	/**
	 * @return
	 */
	public boolean isCanChangeDefaultDeliveryForP2PSphere() {
		return this.preferences.isCanChangeDefaultDeliveryForP2PSphere();
	}

	/**
	 * @return
	 */
	public String getDefaultDeliveryTypeForP2PSphere() {
		return this.preferences.getDefaultDeliveryTypeForP2PSphere();
	}

	/**
	 * @return
	 */
	public boolean getPopUpBehaviorValue() {
		return this.preferences.getPopUpBehaviorValue();
	}

	/**
	 * @return
	 */
	public boolean isPopUpBehaviorModify() {
		return this.preferences.isPopUpBehaviorModify();
	}
}
