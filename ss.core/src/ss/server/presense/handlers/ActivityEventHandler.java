package ss.server.presense.handlers;

import java.util.Calendar;

import ss.common.presence.AbstractPresenceEvent;
import ss.domainmodel.UserActivity;
import ss.framework.networking2.EventHandlingContext;
import ss.server.db.dataaccesscomponents.UserActivityDac;

public class ActivityEventHandler extends AbstractPresenceEventHandler<AbstractPresenceEvent> {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ActivityEventHandler.class);
	
	
	/**
	 * @param commandClass
	 */
	public ActivityEventHandler() {
		super(AbstractPresenceEvent.class);
	}


	/* (non-Javadoc)
	 * @see ss.framework.networking2.EventHandler#handleEvent(ss.framework.networking2.EventHandlingContext)
	 */
	@Override
	protected void handleEvent(EventHandlingContext<AbstractPresenceEvent> context) {
		final AbstractPresenceEvent event = context.getMessage();
		if (logger.isDebugEnabled()) {
			logger.debug( "Handle activity event " + event );
		}
		if ( event.isActivityUpdate() ) {
			final String sphereId = event.getSphereId();
			final String userLogin = super.getUserLogin();
			updateUserActivity(sphereId, userLogin);
			final String supraSphereId = super.getSupraSphereId();
			if ( !supraSphereId.equals( sphereId ) ) {
				updateUserActivity(supraSphereId, userLogin);
			}
		}				
	}

	/**
	 * @param sphereId
	 * @param userLogin
	 */
	private void updateUserActivity(final String sphereId, final String userLogin) {
		final UserActivity userActivity = UserActivityDac.INSTANCE.getOrCreate( sphereId, userLogin );
		userActivity.setLastActivityDate( Calendar.getInstance().getTimeInMillis() );
		UserActivityDac.INSTANCE.update(userActivity);
		if ( logger.isDebugEnabled() ) {
			logger.debug( "update userActivity " + userActivity );
		}
	}

}
