/**
 * 
 */
package ss.framework.networking2;

import ss.common.DateUtils;

/**
 *
 */
public class CommandExecuteTimeoutException extends CommandExecuteException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3469966322447520468L;

	/**
	 * @param message
	 */
	public CommandExecuteTimeoutException( Command command, long timeout, long startTime ) {
		super( command, "Reply wait timeout. Used timeout " + timeout + 
					( startTime > 0 ? (", start time " + DateUtils.dateToCanonicalString( startTime ) ) : "") );		
	}

	/**
	 * @param command
	 * @param usedTimeOut
	 */
	public CommandExecuteTimeoutException(Command command, long usedTimeOut) {
		this( command, usedTimeOut, -1 );
	}

}
