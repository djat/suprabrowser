/**
 * 
 */
package ss.framework.networking2;

/**
 *
 */
public final class ReplyHandlingContext<R extends Reply> extends MessageHandlingContext<R> {


	private final Command intiatorCommand;
	/**
	 * @param reply
	 */
	public ReplyHandlingContext(final R reply, Command intiatorCommand) {
		super( reply );
		this.intiatorCommand = intiatorCommand;
	}
	/**
	 * @return the intiatorCommand
	 */
	public final Command getIntiatorCommand() {
		return this.intiatorCommand;
	}
	
	

}
