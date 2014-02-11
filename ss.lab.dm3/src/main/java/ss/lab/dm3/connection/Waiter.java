package ss.lab.dm3.connection;

public class Waiter {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	/**
	 */
	public void await(WaiterCheckpoint checkpoint, long timeout) {
		try {
			checkpoint.wait(timeout);
		}
		catch (InterruptedException ex) {
			this.log.warn( "Await interrupted " + this, ex );
		}
	}
	
	
	
}
