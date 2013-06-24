/**
 * 
 */
package ss.client.preferences;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import ss.client.ui.SupraSphereFrame;
import ss.client.ui.preferences.SpheresPreferencesManagerComposite;
import ss.client.ui.preferences.UsersPreferencesComposite;
import ss.client.ui.spheremanagement.ISphereDefinitionProvider;
import ss.client.ui.spheremanagement.NoEmailBoxDefinitionProvider;
import ss.client.ui.spheremanagement.SphereDefinitionProviderForPreferences;
import ss.client.ui.spheremanagement.memberaccess.ClientMemberDefinitionProvider;
import ss.client.ui.spheremanagement.memberaccess.MemberAccessManager;
import ss.common.TimeLogWriter;
import ss.common.VerifyAuth;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.SphereItem;
import ss.domainmodel.SupraSphereMember;
import ss.domainmodel.preferences.SphereOwnPreferences;
import ss.domainmodel.preferences.UserPersonalPreferences;

/**
 * @author zobo
 * 
 */
public class PreferencesAdminController {

	private PreferencesAdmin preferences;
	
	private SphereDefinitionProviderForPreferences sphereDefinitionProvider;
	
	private NoEmailBoxDefinitionProvider noEmailBoxProvider;

	private MemberAccessManager memberAccessManager;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(PreferencesAdminController.class);

	public PreferencesAdminController() {
		this.preferences = new PreferencesAdmin();
	}

	public void preloadData(final ForwardingController fcontroller) {
		final TimeLogWriter timeLogWriter = new TimeLogWriter( PreferencesAdminController.class, "PreloadData started" );
		List<SphereItem> spheresItems = getGroupSpheres();
		timeLogWriter.logAndRefresh("getGroupSpheres finished");
		String supraSphere = SupraSphereFrame.INSTANCE.client.getVerifyAuth()
				.getSupraSphereName();
		String sphereId = null;
		for (SphereItem item : spheresItems) {
			String sphereDisplayName = item.getDisplayName();
			if (!(supraSphere.equals(sphereDisplayName))) {
				sphereId = item.getSystemName();
				break;
			}
		}
		List<String> users = getUsers();
		String user = null;
		if (!users.isEmpty()){
			user = users.get(0);
		}
		if (logger.isDebugEnabled()){
			logger.debug("Loading for sphereId: " + sphereId + " and user: " + user);
		}
		timeLogWriter.logAndRefresh("before preferences.init");
		this.preferences.init(sphereId, user);
		timeLogWriter.logAndRefresh("preferences.init finished");
		this.sphereDefinitionProvider = new SphereDefinitionProviderForPreferences(SupraSphereFrame.INSTANCE.client);
		timeLogWriter.logAndRefresh("sphereDefinitionProvider loaded");
		this.sphereDefinitionProvider.checkOutOfDate();
		timeLogWriter.logAndRefresh("sphereDefinitionProvider checked");
		this.noEmailBoxProvider = new NoEmailBoxDefinitionProvider(SupraSphereFrame.INSTANCE.client);
		timeLogWriter.logAndRefresh("NoEmailBoxDefinitionProvider loaded");
		this.noEmailBoxProvider.checkOutOfDate();
		timeLogWriter.logAndRefresh("NoEmailBoxDefinitionProvider checked");
		ClientMemberDefinitionProvider memberDefinitionProvider = new ClientMemberDefinitionProvider( SupraSphereFrame.INSTANCE.client );
		timeLogWriter.logAndRefresh("ClientMemberDefinitionProvider loaded");
		memberDefinitionProvider.checkOutOfDate();
		timeLogWriter.logAndRefresh("ClientMemberDefinitionProvider checked");
		this.memberAccessManager = new MemberAccessManager( this.sphereDefinitionProvider, memberDefinitionProvider );
		timeLogWriter.logAndRefresh("MemberAccessManager loaded");
		this.memberAccessManager.checkOutOfDate();
		timeLogWriter.logAndRefresh("MemberAccessManager checked");
		fcontroller.preloadData(sphereId);
		timeLogWriter.logAndRefresh("PreloadData finished");
	}

	@SuppressWarnings("unchecked")
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

	public List<SphereItem> getGroupSpheres() {
		return SupraSphereFrame.INSTANCE.client.getVerifyAuth().getAllGroupSpheres();
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isSystemTrayNotificationOfReplyModify(String sphereId) {
		try {
			return this.preferences
					.isSystemTrayNotificationOfReplyModify(sphereId);
		} catch (Exception ex) {
			logger.error("cannot optain preference for sphere " + sphereId, ex);
			return false;
		}
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isSystemTrayNotificationOfFirstTimeSphere(String sphereId) {
		try {
			return this.preferences
					.isSystemTrayNotificationOfFirstTimeSphere(sphereId);
		} catch (Exception ex) {
			logger.error("cannot optain preference for sphere " + sphereId, ex);
			return false;
		}
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isSystemTrayNotificationOfReply(String sphereId) {
		try {
			return this.preferences.isSystemTrayNotificationOfReply(sphereId);
		} catch (Exception ex) {
			logger.error("cannot optain preference for sphere " + sphereId, ex);
			return false;
		}
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isSystemTrayNotificationOfFirstTimeSphereModify(
			String sphereId) {
		try {
			return this.preferences
					.isSystemTrayNotificationOfFirstTimeSphereModify(sphereId);
		} catch (Exception ex) {
			logger.error("cannot optain preference for sphere " + sphereId, ex);
			return false;
		}
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isReplyIsAlsoAPopUpToPopUpModify(String sphereId) {
		try {
			return this.preferences.isReplyIsAlsoAPopUpToPopUpModify(sphereId);
		} catch (Exception ex) {
			logger.error("cannot optain preference for sphere " + sphereId, ex);
			return false;
		}
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isReplyIsAlsoAPopUpToPopUp(String sphereId) {
		try {
			return this.preferences.isReplyIsAlsoAPopUpToPopUp(sphereId);
		} catch (Exception ex) {
			logger.error("cannot optain preference for sphere " + sphereId, ex);
			return false;
		}
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isNewMessageShouldOpenTabModify(String sphereId) {
		try {
			return this.preferences.isNewMessageShouldOpenTabModify(sphereId);
		} catch (Exception ex) {
			logger.error("cannot optain preference for sphere " + sphereId, ex);
			return false;
		}
	}

	/**
	 * @param sphereId
	 * @return
	 */
	public boolean isNewMessageShouldOpenTab(String sphereId) {
		try {
			return this.preferences.isNewMessageShouldOpenTab(sphereId);
		} catch (Exception ex) {
			logger.error("cannot optain preference for sphere " + sphereId, ex);
			return false;
		}
	}

	/**
	 * @param userName
	 * @return
	 */
	public boolean isCanChangeDefaultTypeForSphere(String userName) {
		try {
			return this.preferences.isCanChangeDefaultTypeForSphere(userName);
		} catch (Exception ex) {
			logger.error("cannot optain preference for username " + userName,
					ex);
			return false;
		}
	}

	/**
	 * @param userName
	 * @return
	 */
	public boolean isNormalMessageSoundPlay(String userName) {
		try {
			return this.preferences.isNormalMessageSoundPlay(userName);
		} catch (Exception ex) {
			logger.error("cannot optain preference for username " + userName,
					ex);
			return false;
		}
	}

	/**
	 * @param userName
	 * @return
	 */
	public boolean isNormalMessageSoundPlayModify(String userName) {
		try {
			return this.preferences.isNormalMessageSoundPlayModify(userName);
		} catch (Exception ex) {
			logger.error("cannot optain preference for username " + userName,
					ex);
			return false;
		}
	}

	/**
	 * @param userName
	 * @return
	 */
	public boolean isConfirmRecieptMessageSoundPlay(String userName) {
		try {
			return this.preferences.isConfirmRecieptMessageSoundPlay(userName);
		} catch (Exception ex) {
			logger.error("cannot optain preference for username " + userName,
					ex);
			return false;
		}
	}

	/**
	 * @param userName
	 * @return
	 */
	public boolean isConfirmRecieptMessageSoundPlayModify(String userName) {
		try {
			return this.preferences
					.isConfirmRecieptMessageSoundPlayModify(userName);
		} catch (Exception ex) {
			logger.error("cannot optain preference for username " + userName,
					ex);
			return false;
		}
	}

	/**
	 * @param composite
	 */
	public void applySingleSphere(SpheresPreferencesManagerComposite composite) {
		SphereOwnPreferences pref = this.preferences.getSphereOwnPreferences();
		String sphereId = this.preferences.getSphereId();
		pref.setSphereId(sphereId);

		pref.setNewMessageShouldOpenTab(composite.getNewMessageShouldOpenTab());
		pref.setNewMessageShouldOpenTabModify(composite
				.getNewMessageShouldOpenTabModify());

		pref.setReplyIsAlsoAPopUpToPopUp(composite
				.getReplyIsAlsoAPopUpToPopUp());
		pref.setReplyIsAlsoAPopUpToPopUpModify(composite
				.getReplyIsAlsoAPopUpToPopUpModify());

		pref.setSystemTrayNotificationOfFirstTimeSphere(composite
				.getSystemTrayNotificationOfFirstTimeSphere());
		pref.setSystemTrayNotificationOfFirstTimeSphereModify(composite
				.getSystemTrayNotificationOfFirstTimeSphereModify());

		pref.setSystemTrayNotificationOfReply(composite
				.getSystemTrayNotificationOfReply());
		pref.setSystemTrayNotificationOfReplyModify(composite
				.getSystemTrayNotificationOfReplyModify());

		SsDomain.SPHERE_HELPER.setSpherePreferences(sphereId, pref);
	}

	/**
	 * @param composite
	 */
	public void applySingleUser(UsersPreferencesComposite composite) {
		UserPersonalPreferences pref = this.preferences.getUserPreferences();
		String login = this.preferences.getUsername();

		pref.setCanChangeDefaultTypeForSphere(composite
				.getCanChangeDefaultTypeForSphere());

		pref.setConfirmRecieptMessageSoundPlay(composite
				.getConfirmRecieptMessageSoundPlay());
		pref.setConfirmRecieptMessageSoundPlayModify(composite
				.getConfirmRecieptMessageSoundPlayModify());

		pref.setNormalMessageSoundPlay(composite.getNormalMessageSoundPlay());
		pref.setNormalMessageSoundPlayModify(composite
				.getNormalMessageSoundPlayModify());

		pref.setP2pSpheresDefaultDeliveryType(composite
				.getP2PSpheresDefaultDeliveryType());
		pref.setP2pSpheresDefaultDeliveryTypeModify(composite
				.getP2PSpheresDefaultDeliveryTypeModify());

		pref.setpopUpOnTop(composite.getPopUpOnTop());
		pref.setpopUpOnTopModify(composite.getPopUpOnTopModify());

		SsDomain.MEMBER_HELPER.setMemberPreferences(login, pref);
	}

	/**
	 * @param userName
	 * @return
	 */
	public boolean isCanChangeDefaultDeliveryForP2PSphere(String userName) {
		try {
			return this.preferences
					.isCanChangeDefaultDeliveryForP2PSphere(userName);
		} catch (Exception ex) {
			logger.error("cannot optain preference for username " + userName,
					ex);
			return false;
		}
	}

	/**
	 * @param userName
	 * @return
	 */
	public String getDefaultDeliveryTypeForP2PSphere(String userName) {
		try {
			return this.preferences
					.getDefaultDeliveryTypeForP2PSphere(userName);
		} catch (Exception ex) {
			logger.error("cannot optain preference for username " + userName,
					ex);
			return null;
		}
	}

	/**
	 * @param userName
	 * @return
	 */
	public boolean isPopUpBehaviorModify(String userName) {
		return this.preferences.isPopUpBehaviorModify(userName);
	}

	/**
	 * @param userName
	 * @return
	 */
	public boolean getPopUpBehaviorValue(String userName) {
		return this.preferences.getPopUpBehaviorValue(userName);
	}

	/**
	 * @return the sphereDefProvider
	 */
	public ISphereDefinitionProvider getSphereDefinitionProvider() {
		return this.sphereDefinitionProvider;
	}
	
	/**
	 * @return the sphereDefProvider without email boxes
	 */
	public ISphereDefinitionProvider getNoEmailBoxProvider() {
		return this.noEmailBoxProvider;
	}

	/**
	 * 
	 */
	public MemberAccessManager getMemberAccessManager() {
		return this.memberAccessManager;
	}
	
	public PreferencesAdmin getPreferencesAdmin() {
		return this.preferences;
	}
}
