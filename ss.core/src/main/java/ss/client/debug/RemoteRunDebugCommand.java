package ss.client.debug;

import ss.common.debug.RunRemoteCommand;

public class RemoteRunDebugCommand extends AbstractRemoteDebugCommand {

	private String commandName;
	private String commandArgs;
	

	/**
	 */
	protected RemoteRunDebugCommand() {
		super( "remote-run", "Run remote command." );
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		RunRemoteCommand command = new RunRemoteCommand( this.commandName, this.commandArgs );
		super.beginExecute( command );		
	}


	@Override
	protected void processCommandLine(ParsedDebugCommandLine parsedDebugCommandLine) {
		super.processCommandLine(parsedDebugCommandLine);
		this.commandName = parsedDebugCommandLine.getArg0();
		this.commandArgs = parsedDebugCommandLine.getFlatenArgs( 1 );
	}
	
	

}
