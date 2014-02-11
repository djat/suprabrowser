package ss.lab.dm3.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ExecutionValues {

	private final Map<Object,Stack<Object>> values = new HashMap<Object,Stack<Object>>();

	/**
	 * @param key
	 * @return
	 */
	public Object find(Object key) {
		Stack<Object> stack = this.values.get(key);
		if ( stack == null ) {
			return null;
		}
		return stack.peek();
	}

	public void push(Object value, Object key) {
		Stack<Object> stack = this.values.get( key );
		if ( stack == null ) {
			stack = new Stack<Object>();
			this.values.put( key, stack);
		}
		stack.push(value);
	}

	/**
	 * @param key
	 * @return 
	 */
	public Object pop(Object key) {
		final Stack<Object> stack = this.values.get( key );
		if ( stack == null ) {
			throw new IllegalStateException( "Values does not contains " + key );
		}
		final Object result = stack.pop();
		if ( stack.isEmpty() ) {
			this.values.remove(key);
		}
		return result;
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return this.values.isEmpty();
	}
	
	
}
