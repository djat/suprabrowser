package ss.lab.dm3.persist;

import java.util.Stack;

final class ContextStack {

	private final Stack<Context> items = new Stack<Context>();

	public synchronized boolean contains(Object o) {
		return this.items.contains(o);
	}

	public synchronized Context push(Context item) {
		return this.items.push(item);
	}
	
	public synchronized Context getCurrent() {
		return this.items.size() > 0 ? this.items.peek() : null;
	}

	public synchronized void pop(Context context) {
		final Context peek = this.items.peek();
		if ( peek != context ) {
			throw new IllegalArgumentException( "Peek " + peek + " is not same as context " + context );
		}
		this.items.pop();
	}	
	
}
