package ss.common.operations;

public abstract class AbstractLoopOperation extends AbstractOperation {

	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(AbstractLoopOperation.class);
	
	protected static final int DONT_SLEEP_TIME = 0;
	
	private final int sleepTime;	
	
	/**
	 * @param sleepTime
	 */
	public AbstractLoopOperation(int sleepTime) {
		super();
		this.sleepTime = sleepTime;
	}
	
	public AbstractLoopOperation() {
		this( DONT_SLEEP_TIME );
	}

	/* (non-Javadoc)
	 * @see ss.common.operations.AbstractOperation#performRun()
	 */
	@Override
	protected final void performRun() throws OperationBreakException {
		while( true ) {
			checkBroke();
			try {
				performLoopAction();
			}
			catch(RuntimeException ex ) {
				handleRuntimeException(ex);
				checkBroke();
			}
			if ( this.sleepTime != DONT_SLEEP_TIME ) {
				try {
					Thread.sleep( this.sleepTime );
				} catch (InterruptedException e) {
					checkBroke();
				}
			}
		}
	}
	
	/**
	 * @param ex
	 */
	private void handleRuntimeException(RuntimeException ex) {
		logger.error( "Loop action failed", ex );
		// TODO: think about extended reation
	}

	/**
	 * Implementation on loop action
	 */
	protected abstract void performLoopAction() throws OperationBreakException;
	 
	
	
}
