package ss.lab.dm3.events.services;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import ss.lab.dm3.Dm3Const;
import ss.lab.dm3.connection.service.ServiceBackEnd;
import ss.lab.dm3.connection.service.ServiceException;
import ss.lab.dm3.connection.service.backend.BackEndContext;
import ss.lab.dm3.events.Category;
import ss.lab.dm3.events.Event;
import ss.lab.dm3.events.EventList;
import ss.lab.dm3.events.EventListener;
import ss.lab.dm3.events.backend.EventNotificatorProvider;
import ss.lab.dm3.events.backend.IEventManagerBackEnd;
/**
 * @author Dmitry Goncharov
 */
public class EventProviderBackEnd extends ServiceBackEnd implements
		EventProvider, EventNotificatorProvider  {

	private static final long WAIT_EVENTS_TIME = Dm3Const.BLOCK_EVENT_CALLER_MAXTIME;

	private volatile EventList pendingEvents = null;

	private final Set<Category<?>> subscription = new HashSet<Category<?>>();
	
	private final HashMap<Category<?>,EventListener> categoryToNotificator = new HashMap<Category<?>,EventListener>();

	/**
	 * 
	 */
	public EventProviderBackEnd() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.connection.service.ServiceBackEnd#initializing()
	 */
	@Override
	protected void initializing() {
		super.initializing();
		final IEventManagerBackEnd eventManagerBackEnd = getContext().getEventManagerBackEnd();
		if (this.log.isDebugEnabled()) {
			this.log.debug("Initializing " + this + " in " + eventManagerBackEnd );
		}
		eventManagerBackEnd.registerDispatcher(this);
	}

	/**
	 * @param event
	 */
	private synchronized void addToPendingEvents(Event event) {
		if (match(event)) {
			if (this.pendingEvents == null) {
				if (this.log.isDebugEnabled()) {
					this.log.debug("Peding events is null, so creates it");
				}
				this.pendingEvents = new EventList();
			}
			this.pendingEvents.add(event);
			this.notifyAll();
		} else {
			if (this.log.isDebugEnabled()) {
				this.log.debug("Event not mutch to subsciption. Event " + event
						+ ". Subscription " + this.subscription.size());
			}
		}
	}
	

	/**
	 * @param event
	 * @return
	 */
	private boolean match(Event event) {
		for (Category<?> category : this.subscription) {
			if (category.equals(event.getCategory())) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.events.EventProviderFrontEnd#fetchEvents()
	 */
	public synchronized EventList fetchEvents() throws ServiceException {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Fetching events " + this.pendingEvents);
		}
		if (this.pendingEvents == null && WAIT_EVENTS_TIME > 0 ) {			
			// Try to wait events
			try {
				this.wait(WAIT_EVENTS_TIME);
			} catch (InterruptedException ex) {
				// Ignore interruption, debug message only
				if (this.log.isDebugEnabled()) {
					this.log.debug( "Wait interrupted for " + this );
				}
			}
		}
		EventList ret = this.pendingEvents;
		this.pendingEvents = null;
		if (this.log.isDebugEnabled()) {
			this.log.debug("Returning events " + ret);
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	protected <T extends EventListener> T createEventNotificator( Category<T> category ) {		
		final Object proxy = Proxy.newProxyInstance( category.getEventListenerClass().getClassLoader(), new Class<?>[] {category.getEventListenerClass()}, new EventConverter ( category ) );
		return (T) proxy;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.connection.service.ServiceBackEnd#disposing()
	 */
	@Override
	protected void disposing() {
		super.disposing();
		if (this.log.isDebugEnabled()) {
			this.log.debug("Disposing " + this );
		}
		synchronized( this ) {
			this.notifyAll();
		}
		BackEndContext context = getContext();
		if (context != null) {
			context.getEventManagerBackEnd().unregisterDispatcher(this);
		} else {
			this.log.error("Back end context is null for " + this);
		}		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.events.EventProviderFrontEnd#subscribe(ss.lab.dm3.events.Category)
	 */
	public synchronized void subscribe(Category<?> category)
			throws ServiceException {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Add subscription to " + category);
		}
		this.subscription.add(category);
		this.categoryToNotificator.put(category, createEventNotificator(category));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ss.lab.dm3.events.EventProviderFrontEnd#unsubscribe(ss.lab.dm3.events.Category)
	 */
	public synchronized void unsubscribe(Category<?> category)
			throws ServiceException {
		if (this.log.isDebugEnabled()) {
			this.log.debug("Remove subscription to " + category);
		}
		this.subscription.remove(category);
		this.categoryToNotificator.remove(category);
	}
	

	/* (non-Javadoc)
	 * @see ss.lab.dm3.events.backend.EventNotificatorProvider#getEventNotificator(ss.lab.dm3.events.Category)
	 */
	public synchronized <T extends EventListener> T getEventNotificator(Category<T> category) {
		return category.getEventListenerClass().cast( this.categoryToNotificator.get(category) );
	}
	
	public class EventConverter implements InvocationHandler {

		private final Category<?> category;
		
		/**
		 * @param eventBackEnd
		 * @param category
		 */
		EventConverter(Category<?> category) {
			super();
			this.category = category;
		}

		/* (non-Javadoc)
		 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
		 */
		public Object invoke(Object proxy, Method method, Object[] objArgs)
				throws Throwable {
			if ( method.getDeclaringClass().equals( Object.class ) ) {
				return method.invoke( this, objArgs );
			}
			else {
				this.category.checkEventListenerClass( method.getDeclaringClass() );
				Serializable[] args = null;
				if ( objArgs != null ) {
					args = new Serializable[ objArgs.length ];
					for (int n = 0; n < objArgs.length; n++) {
						args[ n ] = (Serializable) objArgs[ n ];
					}
				}
				addToPendingEvents( new Event( this.category, method, args ) );
				return null;
			}
		}
		
	}

}
