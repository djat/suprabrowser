/**
 * 
 */
package ss.common.presence;

/**
 * 
 */
public class StoppedTypingEvent extends AbstractKeyTypedEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1474703113130625337L;

	/**
	 * @param userContactName
	 * @param reply
	 */
	public StoppedTypingEvent(String sphereId,String userContactName) {
		super(sphereId, userContactName);
	}

	/* (non-Javadoc)
	 * @see ss.common.presence.AbstractActivityCommand#isActivityUpdate()
	 */
	@Override
	public boolean isActivityUpdate() {
		return false;
	}

}
