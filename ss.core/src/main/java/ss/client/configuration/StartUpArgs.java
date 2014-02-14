/**
 * 
 */
package ss.client.configuration;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class StartUpArgs {
	
	public static final String INVITE_STARTUP_TYPE = "invite";
	
	public static final String CLIENT_STARTUP_TYPE = "client";
	
	public static final String DEFAULT_STARTUP_TYPE = CLIENT_STARTUP_TYPE;
	
	private final List<String> args = new ArrayList<String>();
	
	private boolean showInvitationGUI = false;
	
	private Integer port = null;

	private boolean autoLogin = false;

	private String startType = CLIENT_STARTUP_TYPE;

	private boolean showVersionDefined;

	/**
	 * @param args
	 */
	public StartUpArgs(String[] args) {
		if ( args != null ) {
			for(String arg : args ) {
				if ( arg != null && arg.length() > 0 ) {
					this.args.add( arg );
				}
			}
		}
		parse();		
	}

	/**
	 * @param args
	 */
	private void parse() {
		int argsCount = this.args.size();
		if ( argsCount == 1 ) {
			if ( !tryParsePort( 0 ) ) {
				tryParseStartType( 0 );
			}
		}
		else if ( argsCount == 2 ) {
			tryParseStartType( 0 );
			tryParsePort( 1 );
		}
		else if ( argsCount == 3 ) {
			tryParseStartType( 0 );
			tryParsePort( 1 );
			tryParseAutoLogin( 2 );
		}
		if ( this.args.contains( "-v" ) || this.args.contains( "-version" ) ) {
			this.showVersionDefined = true;
		}		
	}

	/**
	 */
	private boolean tryParseAutoLogin(int index) {
		String value = this.args.get(index);
		if ("autologin".equals(value)) {
			this.autoLogin = true;
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * 
	 */
	private boolean tryParseStartType(int index) {
		final String value = this.args.get(index);
		if ( INVITE_STARTUP_TYPE.equals(value) ) {
			this.startType = INVITE_STARTUP_TYPE;
			return true;
		}
		else if (CLIENT_STARTUP_TYPE.equals(value)) {
			this.startType = CLIENT_STARTUP_TYPE;
			return true;
		}
		else {
			this.startType = CLIENT_STARTUP_TYPE;
			return false;
		}
	}

	/**
	 */
	private boolean tryParsePort(int index) {
		final String value = this.args.get(index);
		try	{
			this.port = new Integer( Integer.parseInt(value) );
			return true;
		}
		catch( Throwable ex ) {
			this.port = null;
			return false;
		}
	}

	/**
	 * @return
	 */
	public boolean isAutoLogin() {
		return this.autoLogin;
	}

	/**
	 */
	public void setAutoLogin(boolean value) {
		this.autoLogin = value;
	}

	/**
	 * @return
	 */
	public boolean isShowInvitationUi() {
		return this.showInvitationGUI;
	}

	/**
	 * @return
	 */
	public String getStartType() {
		return this.startType;
	}

	/**
	 * @return
	 */
	public boolean isPortDefined() {
		return this.port != null;
	}

	/**
	 * @return
	 */
	public int getPort() {
		return this.port != null ? this.port.intValue() : -1;
	}

	/**
	 * @return
	 */
	public boolean hasCorrectStartUpType() {
		return this.startType.equals( CLIENT_STARTUP_TYPE ) || 
		 		this.startType.equals( INVITE_STARTUP_TYPE);
	}

	/**
	 * @return
	 */
	public boolean isShowVersionDefined() {
		return this.showVersionDefined;
	}

}
