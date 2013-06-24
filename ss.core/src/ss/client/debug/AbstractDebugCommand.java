package ss.client.debug;

import java.util.*;

import ss.client.ui.MessagesPane;
import ss.common.debug.DebugUtils;


/**
 * Abstract debug command 
 * @author d1
 */
public abstract class AbstractDebugCommand {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractDebugCommand.class);
	
	private List<String> names = new ArrayList<String>();		
	
	private String displayName;
	
	private IDebugCommandConext currentContext = null;
	
	private IDebugCommandOutput currentCommandOutput = null;

	protected AbstractDebugCommand( String mainCommandName, String displayName ) {
		this( displayName );
		this.addCommandName(mainCommandName );		
	}
	
	protected AbstractDebugCommand( String displayName ) {
		this.displayName = displayName;
	}
	/**
	 * @return the list of names
	 */
	public final Iterable<String> getNames() {
		return this.names;
	}

	/**
	 * @param name the name to set
	 */
	protected final void addCommandName(String name) {
		if ( this.names.contains( name ) ) {
			return;
		}
		this.names.add( name );
	}
	
	
	/**
	 * @return the displayName
	 */
	public final String getDisplayName() {
		return this.displayName;
	}
	
	/**
	 * Returns true if command shoul executed on input 
	 * @return
	 */
	public final boolean match( ParsedDebugCommandLine parsedCommand) {
		for( String name : this.getNames() ) {
			if ( name.equals( parsedCommand.getCommandName() ) ) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Command implementation 
	 */
	protected abstract void performExecute() throws DebugCommanRunntimeException;
	
	
	
	/**
	 * Execute the command
	 * @param parsedDebugCommandLine
	 */
	public final void execute(IDebugCommandConext debugCommandConext ) {
		if ( debugCommandConext == null ) {
			throw new NullPointerException( "debugCommandConext is null" );
		}
		try {
			logger.info( "executing debug command " + this.getDisplayName() );
			this.currentContext = debugCommandConext;
			this.currentCommandOutput = new DebugCommandOutput();
			this.addCommandResultPrefix();
			processCommandLine( debugCommandConext.getParsedDebugCommandLine() );			
			performExecute();
			this.addCommandResultSufix();
		}
		catch( CommandConditionFailedException ex ) {
			getCommandOutput().appendln( ex.getMessage() );
		}
		catch( Throwable ex ) {
			logger.warn( "commmand " + this + " failed", ex );
			getCommandOutput().appendln( DebugUtils.getExceptionInfo( ex ) );
		}
		finally {
			this.currentContext.handleOutput( this.currentCommandOutput.toString() );
			this.currentContext = null;
			this.currentCommandOutput = null;
		}		
	}
	

	/**
	 * Add command result prefix
	 */
	private void addCommandResultPrefix() {
		if ( !isSystem() ) {
			this.getCommandOutput().append( "Executing " )
			.append( getDisplayName() )
			.append( " [" )
			.append( getMajorName() )
			.appendln( "]" )
			.appendln( "---" );
		}		
	}
	
	/**
	 * Returns true if this is system command
	 */
	protected final boolean isSystem() {
		return this.getMajorName() == null;
	}
	/**
	 * Add command result sufix
	 *
	 */
	private void addCommandResultSufix() {
		if ( !isSystem() ) {
			this.getCommandOutput().appendln( "---" );
		}
	}
	
	
	/**
	 * Returns command major name
	 * @return
	 */
	public final String getMajorName() {
		return this.names.size() > 0 ? this.names.get(0) : null;
	}
	
	/**
	 * Process command line
	 */
	protected void processCommandLine(ParsedDebugCommandLine parsedDebugCommandLine) {
	}
	
	/**
	 * Returns messages pane owner
	 */
	public final MessagesPane getMessagesPageOwner() throws DebugCommanRunntimeException {
		MessagesPane messagesPane = this.currentContext.getMessagesPageOwner();
		if ( messagesPane  == null ) {
			throw new CommandConditionFailedException( "Command require messages pane" );
		}
		return messagesPane;
	}
	
	/**
	 * Returns command output
	 * @return
	 */
	public final IDebugCommandOutput getCommandOutput() {
		return this.currentCommandOutput;
	}
	
	/**
	 * Returns command context
	 * @return
	 */
	protected final IDebugCommandConext getCurrentContext() {
		return this.currentContext;
	}
	
	
	
}
