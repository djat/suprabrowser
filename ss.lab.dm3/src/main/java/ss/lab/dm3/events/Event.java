package ss.lab.dm3.events;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Dmitry Goncharov
 */
public class Event implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1025705524664457558L;

	private final Category<? extends EventListener> category;

	private String methodName;

	private Class<?>[] parameterTypes;

	private final Serializable[] args;

	/**
	 * @param listenerClass
	 * @param args
	 */
	public Event(Category<? extends EventListener> category, Method method,
			Serializable[] args) {
		super();
		this.category = category;
		this.methodName = method.getName();
		this.parameterTypes = method.getParameterTypes();
		this.args = args;
	}

	public Category<? extends EventListener> getCategory() {
		return this.category;
	}

	public void disptachTo(Iterable<? extends EventListener> listeners) {
		final Method method = getMethod();
		dispatchTo(method, listeners);
	}

	private void dispatchTo(final Method method, Iterable<? extends EventListener> listeners) {
		for( EventListener listener : listeners ) {
			dispatchTo(method, listener);
		}
	}
	/**
	 * @param listener
	 * @param method
	 */
	private void dispatchTo(Method method, EventListener listener) {
		this.category.checkEventListener(listener);
		final Object[] objArgs = this.args != null ? new Object[this.args.length]
				: null;
		if (this.args != null) {
			System.arraycopy(this.args, 0, objArgs, 0, this.args.length);
		}
		try {
			method.invoke(listener, objArgs);
		} catch (IllegalArgumentException ex) {
			throw new CantDispatchEventException(this, ex);
		} catch (IllegalAccessException ex) {
			throw new CantDispatchEventException(this, ex);
		} catch (InvocationTargetException ex) {
			throw new CantDispatchEventException(this, ex);
		}
	}
	
	/**
	 * @param listener
	 * @return
	 */
	private Method getMethod() {
		final Method method;
		try {
			method = this.category.getEventListenerClass().getMethod(this.methodName,
					this.parameterTypes);
		} catch (SecurityException ex) {
			throw new MethodNotFoundException(this.methodName, ex);
		} catch (NoSuchMethodException ex) {
			throw new MethodNotFoundException(this.methodName, ex);
		}
		return method;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		return tsb
		.append( "category", this.category )
		.append( "methodName", this.methodName )
		.toString();
	}

}
