package ss.client.debug;

import java.util.*;

public class HistoryDebugCommand extends AbstractDebugCommand {
	
	private List<String> matchedCommandLines = new LinkedList<String>(); 
	/**
	 * @param mainCommandName
	 */
	public HistoryDebugCommand() {
		super( "history", "Show console history" );
		addCommandName( "prev" );
 		addCommandName( "self" ); 		
	}

	/* (non-Javadoc)
	 * @see ss.client.debug.AbstractDebugCommand#performExecute()
	 */
	@Override
	protected void performExecute() throws DebugCommanRunntimeException {
		for( String commandLine : this.matchedCommandLines ) {
			super.getCommandOutput().appendln( commandLine );
		}		
	}

	/**
	 * Add item to history
	 */
	public void addMatchedCommandLine(ParsedDebugCommandLine parsedDebugCommandLine) {
		if ( this.matchedCommandLines.size() > 0 &&
			 this.matchedCommandLines.get( 0 ).equals( parsedDebugCommandLine.getCommandLine() ) ) 
		{
			return;
		}
		this.matchedCommandLines.add( 0, parsedDebugCommandLine.getCommandLine() );		
	}
	
}
