/**
 * 
 */
package ss.framework.networking2;

/**
 *
 */
public abstract class Event extends ActiveMessage implements Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3390082771756183530L;
	
	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(Event.class);
	/**
	 * Put notification to the protocol sending queue. 
	 * Don't wait send time or reply to the command.
	 * @param protocol target protocols
	 */
	public final void fireAndForget(Protocol protocol) {
		protocol.beginExecute( new FireAndForgetMessageSendingContext(this) );
	}
	
	/**
	 * Put command to the protocol sending queue. 
	 * Don't wait send time or reply to the command.
	 * @param protocols target protocols 
	 */
	public final void fireAndForget(Iterable<Protocol> protocols) {
		checkFrozen();
		for( Protocol protocol : protocols ) {
			if (logger.isDebugEnabled()) {
				logger.debug( "Firing event "+ this + " to " + protocol );
			}
			Event eventToFire = this.duplicate();
			eventToFire.fireAndForget( protocol );
		}
	}
	
	private Event duplicate() {
		try {
			return (Event)this.clone();
		} catch (CloneNotSupportedException ex) {
			throw new RuntimeException( "Can't duplicate event", ex);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected final Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	/**
	 * Create copy of event that can be fired
	 * @return
	 */
	public Event reuse() {
		Event reusable = duplicate();
		reusable.clearSendId();
		return reusable;
	}
	

}
