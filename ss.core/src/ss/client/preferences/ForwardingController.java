/**
 * 
 */
package ss.client.preferences;

import ss.client.ui.widgets.UserMessageDialogCreator;
import ss.common.domainmodel2.SsDomain;
import ss.domainmodel.preferences.SphereOwnPreferences;
import ss.domainmodel.preferences.UserPersonalPreferences;
import ss.domainmodel.preferences.UserSpherePreferences;
import ss.domainmodel.preferences.emailforwarding.EmailForwardingPreferencesSphere;
import ss.domainmodel.preferences.emailforwarding.EmailForwardingPreferencesUser;
import ss.domainmodel.preferences.emailforwarding.EmailForwardingPreferencesUserSphere;
import ss.domainmodel.preferences.emailforwarding.CommonEmailForwardingPreferences.ForwardingModes;
import ss.domainmodel.preferences.emailforwarding.EmailForwardingPreferencesSphere.SphereForwardingModes;

/**
 * @author zobo
 *
 */
public class ForwardingController {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ForwardingController.class);
	
	private UserSpherePreferences userSphere;
	
	private UserPersonalPreferences personalPreferences;
	
	private SphereOwnPreferences spherePreferences;
	
	private String username;

	private String sphereuserId = "";
	
	private String sphereId = "";
	
	public ForwardingController(String username) {
		super();
		this.username = username;
	}
	
	public void preloadData(String sphereId2) {
		this.personalPreferences = SsDomain.MEMBER_HELPER.getMemberPreferences( this.username );
		if ( logger.isDebugEnabled() ) 		{
			 logger.debug( "Getting from server UserSpherePreferences for sphereId: " + sphereId2 );
		}
		this.sphereuserId  = sphereId2;
		this.userSphere = SsDomain.INVITED_MEMBER_HELPER.getInvitedMemberPreferences( this.sphereuserId, this.username );
		if ( logger.isDebugEnabled() ) 		{
			 logger.debug( "Getting from server SphereOwnPreferences for sphereId: " + sphereId2 );
		}
		this.sphereId  = sphereId2;
		this.spherePreferences = SsDomain.SPHERE_HELPER.getSpherePreferences( this.sphereId );		
		getUserSpherePreferences(sphereId2);
		getSpherePreferences(sphereId2);
	}
	
	private UserSpherePreferences getUserSpherePreferences(String sphereId){
		if (!this.sphereuserId.equals(sphereId)){
			if ( logger.isDebugEnabled() ) 		{
				 logger.debug( "Getting from server UserSpherePreferences for sphereId: " + sphereId );
			}
			this.sphereuserId  = sphereId;
			this.userSphere = SsDomain.INVITED_MEMBER_HELPER.getInvitedMemberPreferences( this.sphereuserId, this.username );
		}
		return this.userSphere;
	}
	
	private SphereOwnPreferences getSpherePreferences(String sphereId){
		if (!this.sphereId.equals(sphereId)){
			if ( logger.isDebugEnabled() ) 		{
				 logger.debug( "Getting from server SphereOwnPreferences for sphereId: " + sphereId );
			}
			this.sphereId  = sphereId;
			this.spherePreferences = SsDomain.SPHERE_HELPER.getSpherePreferences( this.sphereId );		
		}
		return this.spherePreferences;
	}
	
	private UserPersonalPreferences getPresonalPreferences(){
		return this.personalPreferences;
	}

	/**
	 * @return
	 */
	public ForwardingModes getGlobalMode() {
		return getPresonalPreferences().getEmailForwardingPreferences().getMode();
	}

	/**
	 * @return
	 */
	public String getGlobalEmailAdress() {
		return getPresonalPreferences().getEmailForwardingPreferences().getEmails();
	}

	/**
	 * @param email
	 * @param mode
	 */
	public void applyGlobalForwarding(String emails, ForwardingModes mode) {
		UserPersonalPreferences prefs = getPresonalPreferences();
		if (logger.isDebugEnabled()){
			logger.debug("Saving user global settings: " + prefs.getBindedDocument().asXML());
		}
		EmailForwardingPreferencesUser forwardingPrefs = prefs.getEmailForwardingPreferences();
		forwardingPrefs.setEmails(emails);
		forwardingPrefs.setMode(mode);
		if (logger.isDebugEnabled()){
			logger.debug("Saving user global settings: " + prefs.getBindedDocument().asXML());
		}
		SsDomain.MEMBER_HELPER.setMemberPreferences(this.username, prefs);
	}

	/**
	 * @param sphereId2
	 * @return
	 */
	public String getEmailAdress(String sphereId2) {
		return getUserSpherePreferences(sphereId2).getEmailForwardingPreferences().getEmails();
	}

	/**
	 * @param sphereId2
	 * @return
	 */
	public ForwardingModes getMode(String sphereId2) {
		return getUserSpherePreferences(sphereId2).getEmailForwardingPreferences().getMode();
	}

	/**
	 * @param email
	 * @param mode
	 */
	public void overrideForwarding(final String email, final ForwardingModes mode) {
		if (logger.isDebugEnabled()){
			logger.debug("Override forwarding performed");
		}
		if (UserMessageDialogCreator.overridePerSphereForwardingSettings()){
			
		}
	}

	/**
	 * @param email
	 * @param mode
	 * @param setted
	 */
	public void applySphereUserForwarding(String emails, ForwardingModes mode, boolean setted, String sphereId) {
		UserSpherePreferences prefs = getUserSpherePreferences(sphereId);
		if (logger.isDebugEnabled()){
			logger.debug("Saving sphereUser settings: " + prefs.getBindedDocument().asXML());
		}
		EmailForwardingPreferencesUserSphere forwardingPrefs = prefs.getEmailForwardingPreferences();
		forwardingPrefs.setEmails(emails);
		forwardingPrefs.setMode(mode);
		forwardingPrefs.setSetted(setted);
		if (logger.isDebugEnabled()){
			logger.debug("Saving sphereUser settings: " + prefs.getBindedDocument().asXML());
		}
		SsDomain.INVITED_MEMBER_HELPER.setInvitedMemberPreferences(sphereId, this.username, prefs);
	}

	/**
	 * @param sphereId2
	 * @return
	 */
	public boolean getSetted(String sphereId2) {
		return getUserSpherePreferences(sphereId2).getEmailForwardingPreferences().isSetted();
	}

	/**
	 * @param sphereId2
	 * @return
	 */
	public SphereForwardingModes getSphereMode(String sphereId2) {
		return getSpherePreferences(sphereId2).getEmailForwardingPreferences().getMode();
	}
	
	/**
	 * @param sphereId2
	 * @return
	 */
	public String getSphereEmailAdress(String sphereId2) {
		return getSpherePreferences(sphereId2).getEmailForwardingPreferences().getEmails();
	}

	/**
	 * @param emails
	 * @param mode
	 * @param sphereId2
	 * @param additional 
	 */
	public void applySphereForwarding(String emails, SphereForwardingModes mode, String sphereId2, boolean additional) {
		SphereOwnPreferences preferences = getSpherePreferences(sphereId2);
		if (logger.isDebugEnabled()){
			logger.debug("Saving sphere settings: " + preferences.getBindedDocument().asXML());
		}
		EmailForwardingPreferencesSphere emailForwarding = preferences.getEmailForwardingPreferences();
		emailForwarding.setEmails(emails);
		emailForwarding.setMode(mode);
		emailForwarding.setAdditional(additional);
		if (logger.isDebugEnabled()){
			logger.debug("Saving sphere settings: " + preferences.getBindedDocument().asXML());
		}
		SsDomain.SPHERE_HELPER.setSpherePreferences(sphereId2, preferences);
	}

	/**
	 * @param sphereId2
	 * @return
	 */
	public boolean getAddAdditional(String sphereId2) {
		return getSpherePreferences(sphereId2).getEmailForwardingPreferences().getAdditional();
	}
}
