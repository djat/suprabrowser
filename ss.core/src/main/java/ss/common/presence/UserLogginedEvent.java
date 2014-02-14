/**
 * 
 */
package ss.common.presence;

/**
 *
 */
public final class UserLogginedEvent extends AbstractPresenceEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2471129291926374177L;

	/**
	 * @param sphereId
	 */
	public UserLogginedEvent(String sphereId) {
		super(sphereId);
	}

	/* (non-Javadoc)
	 * @see ss.common.presence.AbstractPresenceEvent#isActivityUpdate()
	 */
	@Override
	public boolean isActivityUpdate() {
		return false;
	}
	
}
