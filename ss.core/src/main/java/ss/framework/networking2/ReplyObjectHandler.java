/**
 * 
 */
package ss.framework.networking2;

import java.io.Serializable;

import ss.common.ArgumentNullPointerException;

/**
 *
 */
public abstract class ReplyObjectHandler<T extends Serializable> extends ReplyHandler {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(ReplyObjectHandler.class);
	
	private final Class<T> expectedReplyObjectClass;
	
	/**
	 * @param expectedSuccessReplyClass
	 */
	public ReplyObjectHandler(Class<T> expectedReplyObjectClass) {
		super( ReturnObjectReply.class );
		if ( expectedReplyObjectClass  == null ) {
			throw new ArgumentNullPointerException( "expectedReplyObject" );
		}
		this.expectedReplyObjectClass = expectedReplyObjectClass; 
	}
	
	/* (non-Javadoc)
	 * @see ss.common.networking2.PassiveCommandHandler#handleSuccess(ss.common.networking2.SuccessPpc)
	 */
	
	protected abstract void objectReturned( T reply );

	/* (non-Javadoc)
	 * @see ss.common.networking2.ReplyHandler#commandSuccessfullyExecuted(ss.common.networking2.Command, ss.common.networking2.SuccessReply)
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected final void commandSuccessfullyExecuted(Command command, SuccessReply successReply) {
		ReturnObjectReply<T> objectSuccessPpc = (ReturnObjectReply<T>)successReply;
		T replyObject = objectSuccessPpc.getObject( this.expectedReplyObjectClass );
		objectReturned( replyObject );		
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + " for " + this.expectedReplyObjectClass;
	}
	
}
