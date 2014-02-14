/**
 * 
 */
package ss.common.presence;

/**
 *
 */
public abstract class AbstractKeyTypedEvent extends AbstractPresenceEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3621237983140215260L;
	
	private final String userContactName;
	
	/**
	 * @param userContactName
	 * @param reply
	 */
	public AbstractKeyTypedEvent(String sphereId, String userContactName) {
		super( sphereId );
		this.userContactName = userContactName;
	}

	/**
	 * Returns typing user
	 * @return
	 */
	public String getUserContactName() {
		return this.userContactName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName()  + ", user " + getUserContactName();
	}

}
