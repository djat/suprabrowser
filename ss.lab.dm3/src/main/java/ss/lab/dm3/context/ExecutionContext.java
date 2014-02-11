package ss.lab.dm3.context;


public class ExecutionContext {

	private final ThreadLocal<ExecutionValues> threadValues = new ThreadLocal<ExecutionValues>(); 
	
	public void push(Object value, Object key ) {
		ExecutionValues values = getOrCreateValues();
		values.push( value, key );
	}
	
	public Object find( Object key ) {
		ExecutionValues values = this.threadValues.get();
		if ( values == null ) {
			return null;
		}
		else {
			return values.find(key);
		}
	}
	
	/**
	 * @return 
	 * 
	 */
	private ExecutionValues getValues() {
		final ExecutionValues executionValues = this.threadValues.get();
		if ( executionValues == null ) {
			throw new IllegalStateException( "Thread values is null" );
		}
		return executionValues;
	}

	private ExecutionValues getOrCreateValues() { 
		ExecutionValues values = this.threadValues.get();
		if ( values == null ) {
			values = new ExecutionValues();
			this.threadValues.set( values );
		}
		return values;
	}

	public Object find( Object key, Object defaultValue ) {
		Object value = find( key );
		return value != null ? value : defaultValue;
	}
	
	public void pop( Object key ) {
		ExecutionValues values = getValues();
		values.pop( key );
		if ( values.isEmpty() ) {
			this.threadValues.set( null );
		}
	}
}
