package ss.client.debug;

public class HelpDebugCommand extends AbstractDebugCommand {

	/**
	 * @param mainCommandName
	 */
	public HelpDebugCommand() {
		super( "help", "Show console help" );
		addCommandName( "-h" );
 		addCommandName( "-?" );
		addCommandName( "/?" );
		addCommandName( "?" );
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		DebugCommandCollection commands = super.getCurrentContext().getAllCommands();
		for( AbstractDebugCommand command : commands.listAll() ) {
			super.getCommandOutput().append( command.getMajorName() )
			.append( "\t" )
			.appendln( command.getDisplayName() );
		}		
	}

	

}
