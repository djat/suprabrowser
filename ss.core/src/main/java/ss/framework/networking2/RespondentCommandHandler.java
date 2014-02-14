/**
 * 
 */
package ss.framework.networking2;

import java.io.Serializable;

/**
 * Handler should be stateless. 
 * method .handle() can be executed by different threads in same time.
 * 
 * 
 */
public abstract class RespondentCommandHandler<C extends Command, R extends Serializable> extends CommandHandler<C> {
	
	/**
	 * @param acceptableCommandClass
	 */
	public RespondentCommandHandler(final Class<C> acceptableCommandClass) {
		super( acceptableCommandClass );
	}

	/**
	 * Perform command handling
	 * @param command not null packet
	 */
	public final SuccessReply handle( C command ) throws CommandHandleException {
		final R result = evaluate( command );
		return new ReturnObjectReply<R>( result );
	}

	protected abstract R evaluate(C command ) throws CommandHandleException;
	

}
