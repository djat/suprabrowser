/**
 * 
 */
package ss.server.debug;

/**
 *
 */
public class RemoteCommandContext {

	private final String args;

	/**
	 * @param args
	 */
	public RemoteCommandContext(final String args) {
		super();
		this.args = args != null ? args : "";
	}

	/**
	 * @return the args
	 */
	public String getArgs() {
		return this.args;
	}
	
	
}
