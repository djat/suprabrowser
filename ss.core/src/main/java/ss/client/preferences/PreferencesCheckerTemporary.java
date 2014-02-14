/**
 * 
 */
package ss.client.preferences;

/**
 * @author zobo
 *
 */
public class PreferencesCheckerTemporary extends PreferencesChecker {

	/**
	 * @param username
	 * @param admin
	 */
	public PreferencesCheckerTemporary(String username, boolean admin) {
		super(username, admin);
	}

	/* (non-Javadoc)
	 * @see ss.client.preferences.PreferencesChecker#isCanChangeDefaultTypeForSphere()
	 */
	@Override
	public boolean isCanChangeDefaultTypeForSphere() {
		return true;
	}

	/* (non-Javadoc)
	 * @see ss.client.preferences.PreferencesChecker#isConfirmRecieptMessageSoundPlay()
	 */
	@Override
	public boolean isConfirmRecieptMessageSoundPlay() {
		return true;
	}

	/* (non-Javadoc)
	 * @see ss.client.preferences.PreferencesChecker#isNewMessageShouldOpenTab(java.lang.String)
	 */
	@Override
	public boolean isNewMessageShouldOpenTab(String sphereId) {
		return true;
	}

	/* (non-Javadoc)
	 * @see ss.client.preferences.PreferencesChecker#isNormalMessageSoundPlay()
	 */
	@Override
	public boolean isNormalMessageSoundPlay() {
		return true;
	}

	/* (non-Javadoc)
	 * @see ss.client.preferences.PreferencesChecker#isReplyIsAlsoAPopUpToPopUp(java.lang.String)
	 */
	@Override
	public boolean isReplyIsAlsoAPopUpToPopUp(String sphereId) {
		return true;
	}

	/* (non-Javadoc)
	 * @see ss.client.preferences.PreferencesChecker#isSystemTrayNotificationOfFirstTimeSphere(java.lang.String)
	 */
	@Override
	public boolean isSystemTrayNotificationOfFirstTimeSphere(String sphereId) {
		return true;
	}

	/* (non-Javadoc)
	 * @see ss.client.preferences.PreferencesChecker#isSystemTrayNotificationOfReply(java.lang.String)
	 */
	@Override
	public boolean isSystemTrayNotificationOfReply(String sphereId) {
		return true;
	}

	
}
