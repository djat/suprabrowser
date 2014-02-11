package ss.lab.dm3.utils;

import java.util.HashMap;

public class ComplexListenerList {

	private final HashMap<Class<?>, ListenerList<?>> classToListeners = new HashMap<Class<?>, ListenerList<?>>();
	
	public synchronized <T> T getNotificator(Class<T> listenerClass ) {
		ListenerList<?> listenerList = getOrCreateListenerList(listenerClass);
		T existedNotificator = listenerClass.cast( listenerList.getNotificator() );
		return existedNotificator;
	}

	@SuppressWarnings("unchecked")
	private <T> ListenerList<T> getOrCreateListenerList(Class<T> listenerClass) {
		ListenerList<?> listenerList = this.classToListeners.get( listenerClass );
		if ( listenerList == null ) {
			listenerList = new ListenerList<T>( listenerClass );
			this.classToListeners.put( listenerClass, listenerList );
		}
		return (ListenerList)listenerList;
	}
	
	public <T> void add(Class<T> listenerClass, T listener) {
		getOrCreateListenerList(listenerClass).add(listener);
	}
	
	public <T> void remove(Class<T> listenerClass, T listener) {
		getOrCreateListenerList(listenerClass).remove(listener);
	}
}
