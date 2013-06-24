package ss.client.debug;


public class NoCommandFoundDebugCommand extends AbstractDebugCommand {

	public NoCommandFoundDebugCommand() {
		super("No command found handler."); 
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		super.getCommandOutput().append( super.getCurrentContext().getParsedDebugCommandLine().getCommandName() )
		.appendln( " command not found" )
		.appendln( "Please type '-?' to get help for commands." );
	}


}
