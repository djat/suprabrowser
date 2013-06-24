/**
 * 
 */
package ss.client.ui.preferences.changesdetector;

import ss.client.preferences.PreferencesAdmin;
import ss.client.ui.preferences.SpheresPreferencesManagerComposite;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.preferences.SphereOwnPreferences;

/**
 * @author roman
 *
 */
public class SpherePreferencesChangesDetector extends AbstractChangesDetector {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(SpherePreferencesChangesDetector.class);
	
	private SpheresPreferencesManagerComposite composite;
	
	private PreferencesAdmin preferences = null;
	
	public SpherePreferencesChangesDetector(SpheresPreferencesManagerComposite composite) {
		this.composite = composite;
		this.preferences = this.composite.getPreferencesAdmin();
	}
	
	public void collectChangesAndUpdate() {
		SphereOwnPreferences pref = this.preferences.getSphereOwnPreferences();
		String sphereId = this.preferences.getSphereId();
		pref.setSphereId(sphereId);

		pref.setNewMessageShouldOpenTab(this.composite.getNewMessageShouldOpenTab());
		pref.setNewMessageShouldOpenTabModify(this.composite
				.getNewMessageShouldOpenTabModify());

		pref.setReplyIsAlsoAPopUpToPopUp(this.composite
				.getReplyIsAlsoAPopUpToPopUp());
		pref.setReplyIsAlsoAPopUpToPopUpModify(this.composite
				.getReplyIsAlsoAPopUpToPopUpModify());

		pref.setSystemTrayNotificationOfFirstTimeSphere(this.composite
				.getSystemTrayNotificationOfFirstTimeSphere());
		pref.setSystemTrayNotificationOfFirstTimeSphereModify(this.composite
				.getSystemTrayNotificationOfFirstTimeSphereModify());

		pref.setSystemTrayNotificationOfReply(this.composite
				.getSystemTrayNotificationOfReply());
		pref.setSystemTrayNotificationOfReplyModify(this.composite
				.getSystemTrayNotificationOfReplyModify());

		SsDomain.SPHERE_HELPER.setSpherePreferences(sphereId, pref);
		
		setChanged(false);
	}

	public void rollbackChanges() {
		this.composite.performFinalAction();
		setChanged(false);
	}

	@Override
	protected String getWarningString() {
		return this.bundle.getString(SPHERE_PREFERENCES);
	}
}
