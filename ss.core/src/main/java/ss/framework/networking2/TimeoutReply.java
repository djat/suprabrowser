package ss.framework.networking2;

public final class TimeoutReply extends FailedReply {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6946964556036964361L;
		
	private final Command command;
	
	private final int usedTimeOut; 
	/**
	 * @param i 
	 * @param command 
	 * @param timeoutException
	 */
	public TimeoutReply(Command command, int usedTimeOut ) {
		super( "Time out" );
		this.command = command;
		this.usedTimeOut = usedTimeOut;
	}

	public CommandExecuteTimeoutException createTimeoutException() {
		return new CommandExecuteTimeoutException( this.command, this.usedTimeOut );
	}

	
}
