package ss.client.debug;

import java.util.*;

public class ParsedDebugCommandLine {

	
	private String commandLine;
	
	private String commandName;
	
	private List<String> commandArgs = new ArrayList<String>();

	public ParsedDebugCommandLine( String commandLine ) {
		if ( commandLine == null ) {
			commandLine = "";
		}
		commandLine = commandLine.trim();
		this.commandLine = commandLine;
		parse();		
	}
	
	/**
	 * Parse command line 
	 *
	 */
	private void parse() {
		String [] splittedCommandLine = this.commandLine.split(" ");
		if ( splittedCommandLine.length >= 1 ) {
			this.commandName = splittedCommandLine[ 0 ];			
		}		
		this.commandArgs.clear();
		for( String arg : splittedCommandLine  ) {
			this.commandArgs.add( arg );
		}
		if ( this.commandArgs.size() > 0 ) {
			this.commandArgs.remove( 0 );		
		}
	}
	/**
	 * Retruns parse command name
	 */
	public String getCommandName() {
		return this.commandName;
	}

	/**
	 * @return the commandLine
	 */
	protected String getCommandLine() {
		return this.commandLine;
	}

	/**
	 * Returns first arg
	 * @return
	 */
	public String getArg0() {
		return this.getArg( 0 );		
	}

    /**
     * Returns first arg
     * @return
     */
    public String getArg0( String defaultValue ) {
        return this.getArg( 0, defaultValue );        
    }

	/**
	 * Returns arg by index or null if no args was found.
	 * @param index
	 * @return
	 */
	public String getArg( int index ) { 
		return this.commandArgs.size() > index ? this.commandArgs.get( index ) : null;
	}
    
    /**
     * Returns arg by index or null if no args was found.
     * @param index
     * @return
     */
    public String getArg( int index, String defaultValue ) { 
        final String ret = getArg( index );
        return ret != null ? ret : defaultValue;
    }

    /**
     * @return return arguments count
     */
    public int getCount() {
        return this.commandArgs.size();
    }

    /**
     * Returns second arg
     * @return
     */
    public String getArg1() {
        return this.getArg( 1 );
    }
    
    /**
     * Returns second arg
     * @return
     */
    public String getArg1( String defaultValue ) {
        return this.getArg( 1, defaultValue );
    }
    
    /**
     * Returns third arg
     * @return
     */
    public String getArg2() {
        return this.getArg( 2 );
    }
	
    /**
     * Returns third arg
     * @return
     */
    public String getArg2( String defaultValue ) {
        return this.getArg( 2, defaultValue );
    }

	public String getFlatenArgs(int offset) {
		final StringBuilder sb = new StringBuilder();
		for( int n = offset; n < getCount(); ++ n ){
			if ( n > offset ) {
				sb.append( " " );
			}
			sb.append( getArg( n ) );
		}
		return sb.toString();
	}

}
