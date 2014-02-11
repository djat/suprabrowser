package ss.lab.dm3.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Dmitry Goncharov
 */
public class ListenerList<T> {

	private List<T> items = new ArrayList<T>();

	private final Class<T> notificatorInterface;

	private T notificator;

	private List<T> notifyingItems = null;
	
	/**
	 *
	 */
	public static <E> ListenerList<E> create( Class<E> listenerClass ) {
		return new ListenerList<E>( listenerClass );
	}
	
	/**
	 * @param notificatorInterface
	 */
	public ListenerList(Class<T> notificatorInterface) {
		super();
		if (notificatorInterface == null) {
			throw new NullPointerException("notificatorInterface");
		}
		if (!notificatorInterface.isInterface()) {
			throw new IllegalArgumentException(
				"notificatorInterface should be interface "
						+ notificatorInterface);
		}
		this.notificatorInterface = notificatorInterface;
	}

	public synchronized void add(T listener) {
		if (listener == null) {
			throw new NullPointerException("listener");
		}
		final List<T> itemsSafeForModify = this.getItemsSafeForModify();
		if (!itemsSafeForModify.contains(listener)) {
			itemsSafeForModify.add(listener);
		}
	}

	public synchronized void remove(T listener) {
		this.getItemsSafeForModify().remove(listener);
	}

	public synchronized void clear() {
		this.getItemsSafeForModify().clear();
	}

	public synchronized T getNotificator() {
		if (this.notificator == null) {
			this.notificator = createNotificator();
		}
		return this.notificator;
	}

	/**
	 * @return
	 */
	private T createNotificator() {
		return this.notificatorInterface.cast(Proxy.newProxyInstance(
			this.notificatorInterface.getClassLoader(),
			new Class<?>[] { this.notificatorInterface },
			new NotificatorImplementation() ) );
	}

	class NotificatorImplementation implements InvocationHandler {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object,
		 *      java.lang.reflect.Method, java.lang.Object[])
		 */
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			invokeAll(method, args);
			return null;
		}

		/**
		 * @param method
		 * @param args
		 */
		private void invokeAll(Method method, Object[] args) {
			synchronized (ListenerList.this) {
				ListenerList.this.notifyingItems = ListenerList.this.items;
				try {
					for (T item : ListenerList.this.notifyingItems ) {
						try {
							method.invoke(item, args);
						} catch (IllegalArgumentException ex) {
							throw new CantNotifyException(item, method, ex);
						} catch (IllegalAccessException ex) {
							throw new CantNotifyException(item, method, ex);
						} catch (InvocationTargetException ex) {
							throw new CantNotifyException(item, method, ex);
						}
					}
				}
				finally {
					ListenerList.this.notifyingItems = null;					
				}
			}
		}

	}

	/**
	 * @return
	 */
	public synchronized int size() {
		return this.items.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder( this );
		return tsb.append( "size", size() ).toString();
	}

	/**
	 * @return the items
	 */
	private List<T> getItemsSafeForModify() {
		if ( this.notifyingItems == this.items ) {
			this.items = new ArrayList<T>( this.items );
		}
		return this.items;
	}
	
	
}
