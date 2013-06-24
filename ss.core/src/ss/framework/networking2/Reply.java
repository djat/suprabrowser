/**
 * 
 */
package ss.framework.networking2;

/**
 *
 */
abstract class Reply extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6471321071081647957L;
	
	private String initiatiorSendId; 
	
	/**
	 * Send reply to the initiator command
	 * @param protocol target protocol 
	 * @param initiatorCommand initiator command, that require reply
	 */
	final void replyTo( Protocol protocol, Command initiatorCommand ) {
		this.initiatiorSendId = initiatorCommand.getSendId();
		protocol.beginExecute( new FireAndForgetMessageSendingContext(this) ); 
	}

	/**
	 * @return the initiator send id
	 */
	public final String getInitiatiorSendId() {
		return this.initiatiorSendId;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return super.toString() + " reply to [" + this.initiatiorSendId + "]";		
	}
	
	
	
}
