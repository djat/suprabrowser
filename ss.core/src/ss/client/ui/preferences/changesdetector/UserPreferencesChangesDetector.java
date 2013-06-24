/**
 * 
 */
package ss.client.ui.preferences.changesdetector;

import ss.client.preferences.PreferencesAdmin;
import ss.client.ui.preferences.UsersPreferencesComposite;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.preferences.UserPersonalPreferences;

/**
 * @author roman
 *
 */
public class UserPreferencesChangesDetector extends AbstractChangesDetector {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(UserPreferencesChangesDetector.class);
	
	private UsersPreferencesComposite userComposite;
	
	private PreferencesAdmin preferences;
	
	public UserPreferencesChangesDetector(UsersPreferencesComposite userComposite) {
		this.userComposite = userComposite;
		this.preferences = this.userComposite.getPreferncesAdmin();
	}
	
	public void collectChangesAndUpdate() {
		UserPersonalPreferences pref = this.preferences.getUserPreferences();
		String login = this.preferences.getUsername();

		pref.setCanChangeDefaultTypeForSphere(this.userComposite
				.getCanChangeDefaultTypeForSphere());

		pref.setConfirmRecieptMessageSoundPlay(this.userComposite
				.getConfirmRecieptMessageSoundPlay());
		pref.setConfirmRecieptMessageSoundPlayModify(this.userComposite
				.getConfirmRecieptMessageSoundPlayModify());

		pref.setNormalMessageSoundPlay(this.userComposite.getNormalMessageSoundPlay());
		pref.setNormalMessageSoundPlayModify(this.userComposite
				.getNormalMessageSoundPlayModify());

		pref.setP2pSpheresDefaultDeliveryType(this.userComposite
				.getP2PSpheresDefaultDeliveryType());
		pref.setP2pSpheresDefaultDeliveryTypeModify(this.userComposite
				.getP2PSpheresDefaultDeliveryTypeModify());

		pref.setpopUpOnTop(this.userComposite.getPopUpOnTop());
		pref.setpopUpOnTopModify(this.userComposite.getPopUpOnTopModify());

		SsDomain.MEMBER_HELPER.setMemberPreferences(login, pref);
		
		setChanged(false);
	}

	public void rollbackChanges() {
		this.userComposite.setInitialValuesToComboUnits();
		if(!isLocalTransit()) {
			this.userComposite.setInitialValuesToComboUnits();
			this.userComposite.rollbackChanges();
		}
		setChanged(false);
	}

	@Override
	protected String getWarningString() {
		return this.bundle.getString(USER_PREFERENCES);
	}
}
