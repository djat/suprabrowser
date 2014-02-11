package ss.lab.dm3.events.backend;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;

import ss.lab.dm3.connection.service.backend.BackEndContext;
import ss.lab.dm3.events.Category;
import ss.lab.dm3.events.EventListener;


/**
 * @author Dmitry Goncharov
 */
public class EventManagerBackEnd implements IEventManagerBackEnd {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private final HashMap<Category<?>, EventListener> categoryToNotificator = new HashMap<Category<?>, EventListener>();
	
	private final List<EventNotificatorProvider> eventNotificatorProviders = new ArrayList<EventNotificatorProvider>();
	
	private final HashMap<Category<?>,Class<? extends AbstractNotificatorFilter<?>>> categoryToFilter = new HashMap<Category<?>, Class<? extends AbstractNotificatorFilter<?>>>();

	private final WeakHashMap<EventListener,EventListener> notificatorToFilteredNotificator = new WeakHashMap<EventListener,EventListener>(); 
	
	public EventManagerBackEnd() {
	}
	
	/* (non-Javadoc)
	 * @see ss.lab.dm3.events.backend.IEventManagerBackEnd#getEventNotificator(ss.lab.dm3.events.Category)
	 */
	public synchronized <T extends EventListener> T getEventNotificator( Category<T> category ) {
		T notificator = getExistEventNotificator(category);
		if ( notificator == null ) {
			notificator = createNotificator( category );
			this.categoryToNotificator.put( category, notificator );
		}
		return notificator;
	}

	/**
	 * @param <T>
	 * @param category
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private <T extends EventListener> T getExistEventNotificator(Category<T> category) {
		return (T) this.categoryToNotificator.get( category );
	}

	/* (non-Javadoc)
	 * @see ss.lab.dm3.events.backend.IEventManagerBackEnd#getEventNotificator(java.lang.Class)
	 */
	public synchronized <T extends EventListener> T getEventNotificator(Class<T> eventClazz) {
		return getEventNotificator( new Category<T>( eventClazz ) );
	}

	public synchronized void registerDispatcher( EventNotificatorProvider  eventDispatcher ) {
		if ( !this.eventNotificatorProviders.contains( eventDispatcher ) ) { 
			this.eventNotificatorProviders.add( eventDispatcher );
		}	
	}
	
	public synchronized void unregisterDispatcher(EventNotificatorProvider eventProviderBackEnd) {
		this.eventNotificatorProviders.remove( eventProviderBackEnd );
	}

	public synchronized <T extends EventListener> void addEventFilter(Category<T> category, Class<? extends AbstractNotificatorFilter<T>> filteredClazz ) {
		if (!category.getEventListenerClass().isAssignableFrom( filteredClazz ) ) {
			throw new IllegalArgumentException( "Filter should implement category event listener. Expected event listener interface " + category.getEventListenerClass() + ", filter class " + filteredClazz );
		}
		this.categoryToFilter.put( category, filteredClazz );
	}
	
	@SuppressWarnings("unchecked")
	private <T extends EventListener> T createNotificator( Category<T> category ) {		
		final Object proxy = Proxy.newProxyInstance( category.getEventListenerClass().getClassLoader(), new Class<?>[] {category.getEventListenerClass()}, new BroadCastEventNotificator( category ) );
		return (T) proxy;
	}
	

	/**
	 * @param method
	 * @param args
	 */
	private synchronized void invokeToAll(Category<?> category, Method method, Object[] args) {
		if ( this.eventNotificatorProviders.size() == 0 ) {
			if (this.log.isDebugEnabled()) {
				this.log.debug("eventNotificatorProviders is empty" );
			}
		}
		for( EventNotificatorProvider provider : this.eventNotificatorProviders ) {
			EventListener notificator = provider.getEventNotificator(category);
			if ( notificator != null ) {
				EventListener filteredNotificator = getFilteredNotificator( category, notificator, provider.getContext() );
				try {
					if (this.log.isDebugEnabled()) {
						this.log.debug("Invoke to all notificator " + filteredNotificator + " by " + method + ",  args " + args );
					}
					method.invoke( filteredNotificator, args );
				} catch (IllegalArgumentException ex) {
					throw new CantDispatchEventExcpetion( category, method, filteredNotificator, args ,  ex );
				} catch (IllegalAccessException ex) {
					throw new CantDispatchEventExcpetion( category, method, filteredNotificator, args ,  ex );
				} catch (InvocationTargetException ex) {
					throw new CantDispatchEventExcpetion( category, method, filteredNotificator, args ,  ex );
				}
			}
			else {
				if (this.log.isDebugEnabled()) {
					this.log.debug( "Provider " + provider + " does not support " + category );
				}
			}
		}
	}
	
	/**
	 * @param category 
	 * @param notificator
	 * @param backEndContext 
	 */
	private EventListener getFilteredNotificator(Category<?> category, EventListener notificator, BackEndContext backEndContext) {
		Class<? extends AbstractNotificatorFilter<?>> filterClazz = this.categoryToFilter.get(category);
		if ( filterClazz == null ) {
			return notificator;
		}
		else {
			EventListener filteredNotificator = this.notificatorToFilteredNotificator.get( notificator );
			if ( filteredNotificator == null ) {
				filteredNotificator = createFilteredNotificator( filterClazz, notificator, backEndContext );
				this.notificatorToFilteredNotificator.put(notificator, filteredNotificator);
			}
			return filteredNotificator;
		}
	}

	/**
	 * @param filterClazz
	 * @param notificator
	 * @param backEndContext 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private EventListener createFilteredNotificator(
			Class<? extends AbstractNotificatorFilter<?>> filterClazz,
			EventListener notificator, BackEndContext backEndContext) {
		try {
			AbstractNotificatorFilter filteredNotificator = filterClazz.newInstance();
			filteredNotificator.initialize( backEndContext, notificator );
			return filteredNotificator;
		} catch (InstantiationException ex) {
			throw new CantCreateFilteredNotificatorExcpetion( filterClazz,  ex );
		} catch (IllegalAccessException ex) {
			throw new CantCreateFilteredNotificatorExcpetion( filterClazz,  ex );
		}
	}

	class BroadCastEventNotificator implements InvocationHandler {

		protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
				.getLog(getClass());
		
		private final Category<?> category;
		
		/**
		 * @param eventBackEnd
		 * @param category
		 */
		BroadCastEventNotificator(Category<?> category) {
			super();
			this.category = category;
		}

		/* (non-Javadoc)
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
		 */
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if (this.log.isDebugEnabled()) {
				this.log.debug("invoke " + method.getClass().getSimpleName() + "." + method.getName() + " with " + args );
			}
			this.category.checkEventListenerClass( method.getDeclaringClass() );
			invokeToAll( this.category, method, args );
			return null;
		}
		
	}


	
}
