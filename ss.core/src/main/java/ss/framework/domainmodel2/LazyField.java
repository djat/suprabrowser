/**
 * 
 */
package ss.framework.domainmodel2;


/**
 *
 */
public abstract class LazyField<T> extends Field {
	
	@SuppressWarnings("unused")
	private static org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(LazyField.class);
	
	private boolean resolved = false;
	
	protected T value;
	
	protected String loadedStrValue = null;
	
	/**
	 * @param objectOwner
	 * @param descriptor
	 */
	public LazyField(LazyFieldDescriptor<?,T> descriptor,FieldMap fieldMap) {
		super(descriptor,fieldMap);
	}

	/**
	 * @return the value
	 */
	public synchronized T get() {
		if ( !isResolved() ) {
			this.value = resolve( this.loadedStrValue );
			this.resolved = true;
		}
		return this.value;
	}

	/**
	 * @return
	 */
	public final synchronized boolean isResolved() {
		return this.resolved;
	}
	
	/**
	 * @param loadedStrValue the loadedStrValue to set
	 */
	final synchronized void setLoadedValue(String loadedStrValue) {
		this.loadedStrValue = loadedStrValue;
		resetResolved();
	}
		
	private synchronized final void markResolved() {
		this.resolved = true;
	}
	
	private synchronized final void resetResolved() {
		this.value = null;
		this.resolved = false;
		if ( logger.isDebugEnabled() ) {
			logger.debug( this + " reseted");
		}
	}
	
	/**
	 * @param value the value to set
	 */
	public synchronized final void set(T value) {
		markDirty();
		setSilently(value);
	}
	
	/**
	 * @param value the value to set
	 */
	public synchronized final void setSilently(T value) {
		markResolved();
		this.value = value;
	}

	protected final T resolve( String loadedStrValue ) {		
		LazyFieldDescriptor<?,T> lazyFieldDescriptor = (LazyFieldDescriptor<?,T>) this.descriptor; 
		if ( logger.isDebugEnabled() ) {
			logger.debug( "resolving by " + lazyFieldDescriptor + " string value " + loadedStrValue );
		}
		return lazyFieldDescriptor.resolve( getSpaceOwner(), loadedStrValue );
	}

	/* (non-Javadoc)
	 * @see ss.framework.domainmodel2.Field#getComparableValue()
	 */
	public abstract Object getComparableValue();

}



