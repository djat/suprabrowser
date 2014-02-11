package ss.lab.dm3.connection;

import org.apache.commons.logging.impl.NoOpLog;
import org.apache.log4j.Logger;

/**
 * @author Dmitry Goncharov
 */
public class CallbackHandler implements ICallbackHandler {

	protected final org.apache.commons.logging.Log log;

	private ICallbackHandler targetHandler;
	
	public CallbackHandler() {
		this( null );
	}
	
	/**
	 * @param internalHandler
	 */
	public CallbackHandler(ICallbackHandler internalHandler) {
		super();
		org.apache.commons.logging.Log log;
		final Class<? extends CallbackHandler> clazz = getClass();
		try {
			log = org.apache.commons.logging.LogFactory.getLog(clazz);
		}
		catch ( NullPointerException ex) {
			Logger.getRootLogger().error( "Can't get log by " + clazz, ex );
			log = new NoOpLog(); 
		}
		this.log = log;
		this.targetHandler = internalHandler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.snapshoots.ICallbackHandler#onFail(java.lang.Throwable)
	 */
	public void onFail(Throwable ex) {
		if ( this.targetHandler != null ) {
			this.targetHandler.onFail(ex);
		}
		else {
			this.log.error("Execution failed", ex);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.snapshoots.ICallbackHandler#onSuccess(java.lang.Object)
	 */
	public void onSuccess(Object result) throws CallbackHandlerException {
		if ( this.targetHandler != null ) {
			this.targetHandler.onSuccess(result);
		}
		else {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Execution returns " + result + " successfully.");
			}
		}
	}

}
