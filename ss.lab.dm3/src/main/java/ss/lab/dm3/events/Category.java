package ss.lab.dm3.events;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Dmitry Goncharov
 */
public class Category<T extends EventListener> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9171481923293189567L;
	
	private Class<T> eventListenerClass;

	/**
	 * @param eventListenerClass
	 */
	public Category(Class<T> eventListenerClass) {
		super();
		if ( !eventListenerClass.isInterface() ) {
			throw new IllegalArgumentException( "Event listener should be interface " + eventListenerClass );
		}
		this.eventListenerClass = eventListenerClass;
	}

	public Class<T> getEventListenerClass() {
		return this.eventListenerClass;
	}
	
	public void checkEventListener(EventListener listener) {
		if ( !this.eventListenerClass.isInstance( listener ) ) {
			throw new ClassCastException( "Expected event listener for " + this + ", but found unsupported listener " + listener );
		}
	}

	/**
	 * @param listenerClazz
	 */
	public void checkEventListenerClass(Class<?> listenerClazz) {
		if ( !this.eventListenerClass.isAssignableFrom( listenerClazz ) ) {
			throw new ClassCastException( "Expected event listener for " + this + ", but found unsupported listener class " + listenerClazz );			
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((this.eventListenerClass == null) ? 0
						: this.eventListenerClass.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Category<?> other = (Category<?>) obj;
		if (this.eventListenerClass == null) {
			if (other.eventListenerClass != null)
				return false;
		} else if (!this.eventListenerClass.equals(other.eventListenerClass))
			return false;
		return true;
	}

	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		return tsb
		.append( "eventListenerClass", this.eventListenerClass )
		.toString();
	}
	
	public static <E extends EventListener> Category<E> create(Class<E> eventListenerClass) {
		return new Category<E>(eventListenerClass);
	}
	

}
