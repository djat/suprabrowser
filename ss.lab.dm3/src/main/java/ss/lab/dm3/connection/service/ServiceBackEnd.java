package ss.lab.dm3.connection.service;

import ss.lab.dm3.connection.service.backend.BackEndContext;

/**
 * @author Dmitry Goncharov
 */
public abstract class ServiceBackEnd implements Service {
	
	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	protected BackEndContext context = null;
	
	private boolean disposed = true;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected final void finalize() throws Throwable {
		dispose();
		super.finalize();		
	}
	
	public synchronized final void dispose() {	
		if ( !this.disposed ) {
			this.disposed = true;
			disposing();
		}
	}

	/**
	 * 
	 */
	protected void disposing() {
	}

	/**
	 * @return the context
	 */
	public final BackEndContext getContext() {
		return this.context;
	}

	public synchronized final void initialize( BackEndContext context ) {
		if ( context == null ) {
			throw new NullPointerException( "context" );
		}
		if ( this.context != null ) {
			throw new IllegalStateException( "Service backend already initialized " + this );			
		}
		this.context = context;
		this.disposed = false;
		initializing();
	}
	
	protected void initializing() {		
	}

	protected void checkInitialed() {
		if ( this.disposed ) {
			throw new IllegalStateException( "Service back end is not initialized " + this );
		}
	}
}