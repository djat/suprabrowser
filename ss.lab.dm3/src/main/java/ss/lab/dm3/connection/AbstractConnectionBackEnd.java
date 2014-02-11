/**
 * 
 */
package ss.lab.dm3.connection;

/**
 * @author Dmitry Goncharov
 */
public abstract class AbstractConnectionBackEnd {
	
	private boolean disposed = false;
	
	public void dispose() {
		if ( !this.disposed ) {
			disposing();
			this.disposed = true;
		}
	}

	/**
	 * 
	 */
	protected void disposing() {
	}
	
}
