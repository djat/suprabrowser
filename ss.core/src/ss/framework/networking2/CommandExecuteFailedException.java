/**
 * 
 */
package ss.framework.networking2;

/**
 *
 */
public class CommandExecuteFailedException extends CommandExecuteException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9130131706938668457L;

	/**
	 * @param failedReply
	 */
	public CommandExecuteFailedException(Command command, FailedReply failedReply) {
		super( command, " reply " + failedReply.toString() );
	}

}
