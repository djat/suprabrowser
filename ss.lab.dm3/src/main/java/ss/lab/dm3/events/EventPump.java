package ss.lab.dm3.events;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import ss.lab.dm3.Dm3Const;
import ss.lab.dm3.connection.CallbackHandlerException;
import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.connection.CallbackTypedHandler;
import ss.lab.dm3.events.services.EventProviderAsync;

/**
 * @author Dmitry Goncharov
 */
public class EventPump {

	protected final org.apache.commons.logging.Log log = org.apache.commons.logging.LogFactory
			.getLog(getClass());
	
	private static final long SLEEP_TIME = 0; // 1000

	private final EventManager manager;
	
	private final EventProviderAsync eventProvider;
	
	private final Thread thread = new Thread( new Runnable() {
		public void run() {
			while( isPumping() ) {
				pump();				
			}
		}
	});

	private final AtomicBoolean pumping = new AtomicBoolean( true );
	
	final FetchEventsHandler fetchEventsHandler = new FetchEventsHandler(); 
	
	/**
	 * @param eventProvider
	 */
	public EventPump(EventManager manager, EventProviderAsync eventProvider) {
		super();
		this.manager = manager;
		this.eventProvider = eventProvider;
		this.thread.setName( "EventPump" );
		this.thread.setDaemon(true);
		this.thread.start();
	}

	/**
	 * @return
	 */
	protected boolean isPumping() {
		return this.pumping.get();
	}

	/**
	 * 
	 */
	protected void pump() {
		if ( SLEEP_TIME > 0 ) {
			synchronized( this ) {
				try {
					wait( SLEEP_TIME );
				} catch (InterruptedException ex) {			
				}
			}
		}
		if ( isPumping() ) {
			this.fetchEventsHandler.reset();
			this.eventProvider.fetchEvents( this.fetchEventsHandler );
			this.fetchEventsHandler.waitToResult();
		}
	}
	


	/**
	 * @param events
	 */
	protected void dispatch(EventList events) {
		if ( events != null ) {
			for( Event event : events ) {
				this.manager.dispatch( event );
			}
		}
	}

	public synchronized void dispose() {
		this.pumping.set( false );
		this.notifyAll();
	}
	
	//TODO rewrite wait functionality
	class FetchEventsHandler extends
		CallbackTypedHandler<EventList> {

		final AtomicReference<CallbackResultWaiter> waiterRef = new AtomicReference<CallbackResultWaiter>();
		
		public FetchEventsHandler() {
			super(EventList.class);
		}

		/**
		 * 
		 */
		public void reset() {
			this.waiterRef.set( new CallbackResultWaiter( Dm3Const.EVENT_PUMP_TIMEOUT ) );
		}

		@Override
		protected void typedOnSuccess(EventList result)
				throws CallbackHandlerException {
			if (EventPump.this.log.isDebugEnabled()) {
				EventPump.this.log.debug("Gets event " + result );
			}
			super.typedOnSuccess(result);
			final CallbackResultWaiter waiter = this.waiterRef.get();
			if ( waiter != null ) {
				waiter.onSuccess(result);
			}
			// Check again before dispatch
			if ( isPumping() ) {
				dispatch( result );
			}
		}
		
		/**
		 * 
		 */
		public void waitToResult() {
			CallbackResultWaiter waiter = this.waiterRef.get();
			waiter.waitToResult();	
		}
	}
}
