/**
 * 
 */
package ss.framework.networking2;

/**
 *
 */
public class CommandExecuteException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1046097208140831133L;
	
	private final Command command;

	/**
	 * @param details
	 */
	public CommandExecuteException(Command command, String details ) {
		super( "Command " + command + " failed. Details: " + details );
		this.command = command;
	}

	/**
	 * @return the command
	 */
	public final Command getCommand() {
		return this.command;
	}
	
	


	
}
