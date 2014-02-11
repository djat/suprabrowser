package ss.lab.dm3.connection;


/**
 * @author Dmitry Goncharov
 */
public abstract class CallbackTypedHandler<T> extends CallbackHandler {
	
	private final Class<T> expectedResultClazz;
	
	/**
	 * @param expectedResultClazz
	 */
	public CallbackTypedHandler(Class<T> expectedResultClazz) {
		super();
		this.expectedResultClazz = expectedResultClazz;
	}

	@Override
	public void onFail(Throwable ex) {
		this.log.error( "Remove service failed", ex);
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.transientobjects.objs.IRemoteServiceHandler#onSuccess(java.lang.Object)
	 */
	@Override
	public final void onSuccess(Object result) throws CallbackHandlerException {
		if ( result != null ) {
			if ( !this.expectedResultClazz.isInstance( result ) ) {
				throw new CallbackHandlerException( "Unexpected result " + result + " excpected result type is " + this.expectedResultClazz );
			}
		}
		typedOnSuccess( this.expectedResultClazz.cast(result) );
	}
	
	protected void typedOnSuccess( T result ) throws CallbackHandlerException {
		if (this.log.isDebugEnabled()) {
			this.log.debug( "Execution returns " + result + " successfully." );
		}
	}

}
