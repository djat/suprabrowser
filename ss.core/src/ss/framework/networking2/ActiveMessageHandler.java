/**
 * 
 */
package ss.framework.networking2;

/**
 *
 */
public abstract class ActiveMessageHandler<M extends Message> implements IMessageHandler<M> {

	private final Class<M> acceptableMessageClass; 
	
	/**
	 * @param messageClass
	 */
	public ActiveMessageHandler(final Class<M> messageClass) {
		super();
		this.acceptableMessageClass = messageClass;
	}

	/**
	 * @return the commandClass
	 */
	public Class<M> getAcceptableMessageClass() {
		return this.acceptableMessageClass;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + " for " + this.acceptableMessageClass;
	}
	
	
}
