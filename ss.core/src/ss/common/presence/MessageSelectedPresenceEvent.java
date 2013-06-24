package ss.common.presence;

/**
 * 
 */
public class MessageSelectedPresenceEvent extends AbstractPresenceEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5551009148516575694L;
	
	/**
	 * @param sphereId
	 */
	public MessageSelectedPresenceEvent(String sphereId) {
		super(sphereId);
	}

	/* (non-Javadoc)
	 * @see ss.common.presence.AbstractActivityCommand#isActivityUpdate()
	 */
	@Override
	public boolean isActivityUpdate() {
		return true;
	}
}
