/**
 * 
 */
package ss.server.db.dataaccesscomponents;

import java.util.Calendar;

import org.dom4j.Document;

import ss.domainmodel.UserActivity;

/**
 * @author d!ma
 *
 */
public class UserActivityDac extends AbstractDac {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(UserActivityDac.class);
	
	/**
	 * Singleton instance
	 */
	public final static UserActivityDac INSTANCE = new UserActivityDac();

	private UserActivityDac() {
	}

	/**
	 * @param userActivity
	 */
	public void update(UserActivity userActivity) {
		super.updateDocument( userActivity.getSphereId(), userActivity.getType(), userActivity.getDocumentCopy(), new UserLoginCondition( userActivity.getUserLogin() ) );		
	}

	/**
	 * @param login
	 * @return
	 */
	public Document getUserActivityDocument(String sphereId, String login) {
		return super.findFirstDocument(sphereId, UserActivity.ITEM_TYPE, new UserLoginCondition( login ) );
	}
	
	/**
	 * @param login
	 * @return
	 */
	public UserActivity getOrCreate(String sphereId, String login) {
		final Document userActivityDocument = getUserActivityDocument(sphereId, login);
		if ( userActivityDocument != null ) {
			return UserActivity.wrap(userActivityDocument );
		}
		logger.info( "user activity not found, create new. SphereId " + sphereId + " login " + login );
		final UserActivity userActivity = new UserActivity( sphereId, login );
		userActivity.setLastLoginDate( Calendar.getInstance().getTimeInMillis() );
		update(userActivity);
		return userActivity;
	}
}
