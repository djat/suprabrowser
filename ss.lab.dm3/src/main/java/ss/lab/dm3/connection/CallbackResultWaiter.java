package ss.lab.dm3.connection;

import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.persist.Domain;

/**
 * @author Dmitry Goncharov
 */
public class CallbackResultWaiter extends CallbackHandler {

	private static final long DEFAULT_TIMEOUT = 20000;

	private final Object service;
	
	private final long timeout;
	
	private final WaiterCheckpoint checkpoint = new WaiterCheckpoint();
	
	private volatile Throwable ex = null;
	
	private volatile Object result = null;

	
	/**
	 * 
	 */
	public CallbackResultWaiter() {
		this( null );
	}

	/**
	 * @param service
	 */
	public CallbackResultWaiter(Object service) {
		this( service, DEFAULT_TIMEOUT );
	}
	
	public CallbackResultWaiter(long timeout) {
		this( null, timeout );
	}

	/**
	 * @param service
	 * @param timeout
	 */
	public CallbackResultWaiter(Object service, long timeout) {
		super();
		this.service = service;
		this.timeout = timeout;
	}

	@Override
	public void onSuccess(Object result) throws CallbackHandlerException {
		synchronized( this.checkpoint ) {
			this.result = result;
			release();
		}
	}

	@Override
	public void onFail(Throwable ex) {
		synchronized( this.checkpoint ) { 
			this.ex = ex;
			release();
		}
	}

	private void release() {
		this.checkpoint.pass();		
	}

	/**
	 * @return the handled
	 */
	public boolean isHandled() {
		return this.checkpoint.isPassed();
	}

	public Object waitToResult() throws ServiceException {
		return waitToResult( this.timeout );
	}
	
	public Object waitToResult(long timeout) throws ServiceException {
		synchronized( this.checkpoint ) { 
			if ( !isHandled() ) {
				Waiter waiter = Domain.createResponseWaiter(); // TODO
				waiter.await( this.checkpoint, timeout );			
			}
			if ( isHandled() ) {
				if ( this.ex != null ) {
					if ( this.ex instanceof ServiceException ) {
						throw (ServiceException) this.ex;
					}
					else {
						throw new ServiceException( "Calling " + ( this.service == null ? "" : this.service.toString()) + " failed", this.ex );
					}
				}
				else {
					// All is OK, returns
					return this.result;
				}
			}
			else {
				throw new CallbackTimeOutException( this.service, timeout );
			}		
		}
	}

	/**
	 * @param expectedResultType
	 * @return
	 */
	public <T> T waitToResult(Class<T> expectedResultType) {
		Object result = waitToResult();
		return expectedResultType.cast( result );
	}
}
