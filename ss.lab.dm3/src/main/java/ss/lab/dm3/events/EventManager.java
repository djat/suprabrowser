package ss.lab.dm3.events;

import java.util.Hashtable;
import ss.lab.dm3.connection.ICallbackHandler;
import ss.lab.dm3.connection.service.AbstractServiceProvider;
import ss.lab.dm3.connection.service.ServiceProviderAdaptor;
import ss.lab.dm3.events.services.EventProviderAsync;

/**
 * @author Dmitry Goncharov
 */
public class EventManager {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final EventProviderAsync eventProvider; 
	
	private final Hashtable<Category<?>,CategoryListeners<?>> categoryToListeners = new Hashtable<Category<?>, CategoryListeners<?>>();
	
	private final EventPump eventPump;
	
	private volatile UnhandledEventHandler unhandledEventHandler = null;
	
	/**
	 * @param connection
	 */
	public EventManager(AbstractServiceProvider serviceProvider) {
		this.eventProvider = serviceProvider.getAsyncService( EventProviderAsync.class );
		this.eventPump = new EventPump(this, this.eventProvider);
		serviceProvider.addServiceProviderListener( new ServiceProviderAdaptor() {
			/* (non-Javadoc)
			 * @see ss.lab.dm3.connection.service.ServiceProviderAdaptor#disposed()
			 */
			@Override
			public void disposing() {
				EventManager.this.eventPump.dispose();
			}
		} );
	}
	
	public synchronized <T extends EventListener> void addListener( Class<T> listenerClazz, T listener) {
		addListener( listenerClazz, listener, null );
	}
	
	public synchronized <T extends EventListener> void addListener( Class<T> listenerClazz, T listener, ICallbackHandler handler ) {
		final Category<T> category = new Category<T>( listenerClazz );
		CategoryListeners<T> listeners = getListeners( category );
		if ( listeners == null ) {
			listeners = createListeners(category, handler);			
		}
		else {
			if ( handler != null ) {
				handler.onSuccess(null);
			}
		}		
		listeners.add( listener );
	}
	
	public synchronized <T extends EventListener> void removeListener(Class<T> listenerClazz, T listener) {
		Category<T> category = new Category<T>( listenerClazz );
		final CategoryListeners<T> listeners = getListeners( category );
		if ( listeners != null ) {
			listeners.remove( listener );
			if ( listeners.size() == 0 ) {
				this.eventProvider.unsubscribe( category, null );
				this.categoryToListeners.remove( category );
			}
		}
	}

	/**
	 * @param category
	 */
	private <T extends EventListener> CategoryListeners<T> createListeners(Category<T> category, ICallbackHandler handler ) {
		CategoryListeners<T> listeners = new CategoryListeners<T>( category );
		this.categoryToListeners.put( listeners.getCategory(), listeners );
		this.eventProvider.subscribe( category, handler );		
		return listeners;		
	}

	/**
	 * @param event
	 */
	synchronized void dispatch(Event event) {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Dispatching event " + event );
		}		
		CategoryListeners<? extends EventListener> listeners = getListeners( event.getCategory() );
		if ( listeners != null ) {
			event.disptachTo( listeners );
		}
		else {
			if ( this.unhandledEventHandler != null ) {
				this.unhandledEventHandler.unhandledEvent(event);
			}	
		}
	}

	/**
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	private <T extends EventListener> CategoryListeners<T> getListeners(Category<T> category) {
		return (CategoryListeners<T>) this.categoryToListeners.get( category );
	}

	public void dispose() {
		this.eventPump.dispose();
	}

	public synchronized UnhandledEventHandler getUnhandledEventHandler() {
		return this.unhandledEventHandler;
	}

	public synchronized void setUnhandledEventHandler(UnhandledEventHandler unhandledEventHandler) {
		this.unhandledEventHandler = unhandledEventHandler;
	}
	
	
}
