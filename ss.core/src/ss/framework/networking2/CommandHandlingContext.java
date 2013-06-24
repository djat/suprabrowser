/**
 * 
 */
package ss.framework.networking2;

/**
 *
 */
public final class CommandHandlingContext<C extends Command> extends ActiveMessageHandlingContext<C> {

	private final Protocol protocolOwner;
		
	private boolean replySent = false;
	
	/**
	 * @param command
	 */
	public CommandHandlingContext(Protocol protocolOwner, C command) {
		super( command );
		this.protocolOwner = protocolOwner;
	}
	
	/**
	 * @param string
	 */
	public void replyFailed(String errorMessage ) {
		reply( new FailedReply( errorMessage ) );		
	}
	
	/**
	 * @return
	 */
	public boolean isReplySent() {
		return this.replySent;
	}

	/**
	 * Send reply to the command
	 * @param reply
	 */
	public void reply( Reply reply ) {
		synchronized( this ) { 
			if ( isReplySent() ) {
				throw new IllegalStateException( "Reply to " + getMessage() + " alredy sent." );
			}
			this.replySent = true;
		}
		reply.replyTo( this.protocolOwner, getMessage() );
	}

}
