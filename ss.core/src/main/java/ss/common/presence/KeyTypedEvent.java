/**
 * 
 */
package ss.common.presence;

import ss.common.debug.DebugUtils;

/**
 *
 */
public class KeyTypedEvent extends AbstractKeyTypedEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6724073815989188740L;

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(KeyTypedEvent.class);
	
	private final String replyId;

	/**
	 * @param userContactName
	 * @param replyId
	 */
	public KeyTypedEvent(String sphereId, String userContactName, String replyId) {
		super(sphereId, userContactName);
		this.replyId = replyId;
		if ( logger.isDebugEnabled() ) {
			logger.debug( "Creating key typed command " + DebugUtils.getCurrentStackTrace() );
		}
		
	}

	public String getReplyId() {
		return this.replyId;
	}

	/* (non-Javadoc)
	 * @see ss.common.presence.AbstractActivityCommand#isActivityUpdate()
	 */
	@Override
	public boolean isActivityUpdate() {
		return true;
	}
	

}
