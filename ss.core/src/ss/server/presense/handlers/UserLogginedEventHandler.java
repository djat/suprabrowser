/**
 * 
 */
package ss.server.presense.handlers;

import java.util.Calendar;

import ss.common.presence.UserLogginedEvent;
import ss.domainmodel.UserActivity;
import ss.framework.networking2.EventHandlingContext;
import ss.server.db.dataaccesscomponents.UserActivityDac;

/**
 *
 */
public class UserLogginedEventHandler extends AbstractPresenceEventHandler<UserLogginedEvent>{

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(UserLogginedEventHandler.class);
	
	/**
	 * @param commandClass
	 */
	public UserLogginedEventHandler() {
		super(UserLogginedEvent.class);
	}

	/* (non-Javadoc)
	 * @see ss.framework.networking2.EventHandler#handleEvent(ss.framework.networking2.EventHandlingContext)
	 */
	@Override
	protected void handleEvent(EventHandlingContext<UserLogginedEvent> context) {
		UserLogginedEvent event = context.getMessage();
		logger.info("Update User Login by " +  event + ", login " + getUserLogin() );
		final UserActivity userActivity = new UserActivity( event.getSphereId(), getUserLogin() );
		userActivity.setLastLoginDate( Calendar.getInstance().getTime().getTime() );
		UserActivityDac.INSTANCE.update( userActivity );
	}

}
