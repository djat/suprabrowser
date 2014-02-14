package ss.client.debug;

import java.util.*;

public class DebugCommandCollection {

	private List<AbstractDebugCommand> commands = new ArrayList<AbstractDebugCommand>();
	
	private HistoryDebugCommand historyDebugCommand = new HistoryDebugCommand();
	
	private NoCommandFoundDebugCommand noCommandFoundCommand = new NoCommandFoundDebugCommand();
	/**
	 * Loads default commands
	 */
	public void loadDefault() {
		this.commands.add( new HelpDebugCommand() );
		this.commands.add( new SessionDebugCommand() );
		this.commands.add( new SphereDefinitionDebugCommand() );
		this.commands.add( new SelectedNodeDocumentDebugCommand() );
		this.commands.add( new SupraSphereDebugCommand() );
		this.commands.add( new SendMailDebugCommand() );
		this.commands.add( new DomainSpaceDebugCommand() );
		this.commands.add( new ServerStateDebugCommand() );
		this.commands.add( new ServerThreadsDumpDebugCommand() );
		this.commands.add( new ClientThreadsDumpDebugCommand() );
		this.commands.add( new SetLoginParamsDebugCommand() );
		this.commands.add( new RemoteRunDebugCommand() );
		this.commands.add( new DumpSupraSphereDebugCommand() );
		this.commands.add( this.historyDebugCommand );
	}

	/**
	 * Process command
	 */
	public boolean processCommand(IDebugCommandConext context ) {
		AbstractDebugCommand selectedCommand = null;  
		for( AbstractDebugCommand command : this.commands ) {
			if ( command.match( context.getParsedDebugCommandLine()) ) {
				selectedCommand = command;
				//TODO: ambigous command processing
				break;
			}
		}
		if ( selectedCommand != null ) {
			this.historyDebugCommand.addMatchedCommandLine(context.getParsedDebugCommandLine() );
			selectedCommand.execute( context );
			return true;
		}
		else 
		{			
			this.noCommandFoundCommand.execute( context );
			return false;
		}
	}


	/**
	 * Returns list of all visible command
	 * @return
	 */
	public Iterable<AbstractDebugCommand> listAll() {
		return this.commands;
	}

}
