/**
 * 
 */
package ss.lab.dm3.events.backend;

import ss.lab.dm3.connection.service.backend.BackEndContext;
import ss.lab.dm3.events.EventListener;

/**
 * @author Dmitry Goncharov
 * 
 * TODO: implement this kind of event
 * Child class should implements T interface by call delegation to object from getImpl()
 *  
 */
public abstract class AbstractNotificatorFilter<T extends EventListener> implements EventListener {

	private T impl;
	
	private BackEndContext context;
	
	public void initialize( BackEndContext context, T impl ) {
		if (context == null) {
			throw new NullPointerException("context");
		}
		if (impl == null) {
			throw new NullPointerException("impl");
		}
		this.context = context;
		this.impl = impl;
	}
		
	protected final T getImpl() {
		return this.impl;
	}	
	
	protected final BackEndContext getContext() {
		return this.context;
	}
	
}
