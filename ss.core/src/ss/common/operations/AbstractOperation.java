package ss.common.operations;

import javax.swing.event.EventListenerList;

import ss.common.IdentityUtils;
import ss.common.UnexpectedRuntimeException;


/**
 * Base class for standart operations
 *
 */
public abstract class AbstractOperation implements IOperation, IStoppableOperation {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractOperation.class);
	
	private final EventListenerList listenerList = new EventListenerList();
	
	private volatile boolean broke = false;
	
	private boolean neverBeenUsed = true;
	
	private volatile Thread operationThread = null;
	
	private String displayName = getClass().getName();
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public final void run() {
		if ( !this.neverBeenUsed ) {
			//TODO: thing about multiple execution
			logger.error( "Operation " + this + " dont support mulitple execution." );
			throw new UnexpectedRuntimeException( "Operation " + this + " dont support mulitple execution." );
		}
		this.operationThread = Thread.currentThread();
		this.neverBeenUsed = false;
		try {			
			onSetup( new OperationProgressEvent( this ) );
			performRun();			
		}
		catch( OperationBreakException e ) {
			if ( logger.isDebugEnabled() ) {
				logger.debug( "Operation break " + this, e );
			}			
			this.broke = true;
			onBroke( new OperationProgressEvent( this ) );
		}
		catch( Throwable e ) {
			//TODO: think about exception handling
			logger.error( "Operation " + this +  " failed", e );
		}
		finally {
			this.operationThread = null;
			try {
				onTeardown( new OperationProgressEvent( this ) );
			}
			finally {
				
			}			
		}		
	}

	/**
	 * Operation implementation
	 */
	protected abstract void performRun() throws OperationBreakException;
		
	/**
	 * Notify listeners about operation progress
	 * @param message progress message 
	 * @param progress propress value (between 0 and 1.0)
	 */
	protected final void notifyProgress( String message, double progress ) throws OperationBreakException {
		onProgress( new OperationProgressEvent( this, message, progress ) );
	}

	/* (non-Javadoc)
	 * @see ss.common.operations.IOperation#addProgressListener(ss.common.operations.OperationProgressListener)
	 */
	public final void addProgressListener(OperationProgressListener listener) {
		this.listenerList.add( OperationProgressListener.class, listener );
	}

	/* (non-Javadoc)
	 * @see ss.common.operations.IOperation#removeProgressListener(ss.common.operations.OperationProgressListener)
	 */
	public final void removeProgressListener(OperationProgressListener listener) {
		this.listenerList.remove( OperationProgressListener.class, listener );		
	}
	
	/**
	 * Returns list of progress listener
	 * @return
	 */
	protected final OperationProgressListener[] getProgressListeners() {
		return this.listenerList.getListeners( OperationProgressListener.class ); 
	}
	
	/**
	 * Called to notify about operation start
	 */
	protected void onSetup( OperationProgressEvent e ) {
		for( OperationProgressListener listener : getProgressListeners() ) {
			listener.setupped(e);
		}
	}
	
	/**
	 * Called to notify about operation abort
	 */
	protected void onBroke( OperationProgressEvent e ) {		
		for( OperationProgressListener listener : getProgressListeners() ) {
			listener.broke(e);
		}		
	}
	
	/**
	 * Called to notify about operation progress
	 */	
	protected void onProgress( OperationProgressEvent e ) throws OperationBreakException {
		checkBroke();
		for( OperationProgressListener listener : getProgressListeners() ) {
			listener.progress(e);
		}
	}
	
	/**
	 * Called to notify about operation end
	 */
	protected void onTeardown( OperationProgressEvent e ) {
		for( OperationProgressListener listener : getProgressListeners() ) {
			listener.teardowned(e);
		}		
	}

	/* (non-Javadoc)
	 * @see ss.common.operations.IAbortableOperation#abort()
	 */
	public final void queryBreak() {
		this.broke = true;
		final Thread threadToInterrupt = this.operationThread;
		if ( threadToInterrupt != null && threadToInterrupt.isAlive() ) {
			threadToInterrupt.interrupt();
			this.operationThread = null;
		}
	}
	
	/**
	 * @throws OperationBreakException is operation was aborted
	 */
	protected final void checkBroke() throws OperationBreakException {
		if ( isBroke() ) {
			throw new OperationBreakException( this );
		}
	}

	
	/* (non-Javadoc)
	 * @see ss.common.operations.IOperation#createOperationThread()
	 */
	private final Thread createOperationThread() {
		Thread operationThread = new Thread( this );
		operationThread.setName( getNextRuntimeId() );
		operationThread.setDaemon( isDeamonDesired() );		
		return operationThread;
	}

	/**
	 * @return
	 */
	protected boolean isDeamonDesired() {
		return false;
	}

	/**
	 * @return
	 */
	private String getNextRuntimeId() {
		return IdentityUtils.getNextRuntimeId( "T#" + getDisplayName() );
	}
	
	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return this.displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Start operation in separate thread
	 */
	public final synchronized Thread start() {
		if ( isAlive() ) {
			throw new IllegalStateException( "Operation already running " + this );
		}
		if ( isBroke() ) {
			throw new IllegalStateException( "Operation is broke " + this );
		}	
		this.operationThread = createOperationThread();
		this.operationThread.start();
		return this.operationThread;
	}

	/**
	 * @return the stopped
	 */
	public final boolean isBroke() {
		return this.broke;
	}
	
	/**
	 * @return true if operation is running and not broke
	 */
	public boolean isAlive() {
		return this.operationThread != null;
	}

	/**
	 * @return the firstRun
	 */
	public final boolean isNeverBeenUsed() {
		return this.neverBeenUsed;
	}
	
	
	
}
