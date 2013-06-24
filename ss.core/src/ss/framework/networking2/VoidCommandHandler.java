/**
 * 
 */
package ss.framework.networking2;

/**
 *
 */
public abstract class VoidCommandHandler<C extends Command> extends CommandHandler<C> {

	/**
	 * @param messageClass
	 */
	public VoidCommandHandler(Class<C> messageClass) {
		super(messageClass);
	}

	

	/* (non-Javadoc)
	 * @see ss.common.networking2.CommandHandler#handle(ss.common.networking2.Command)
	 */
	@Override
	protected final SuccessReply handle(C command) throws CommandHandleException {
		execute( command );
		return new VoidReply();
	}
	
	/**
	 * @param command
	 */
	protected abstract void execute(C command) throws CommandHandleException;
	
}
