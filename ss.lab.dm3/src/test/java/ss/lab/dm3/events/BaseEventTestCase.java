package ss.lab.dm3.events;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;
import ss.lab.dm3.connection.CallbackResultWaiter;
import ss.lab.dm3.connection.Connection;
import ss.lab.dm3.events.backend.IEventManagerBackEnd;
import ss.lab.dm3.events.services.EventProviderAsync;
import ss.lab.dm3.testsupport.TestSystemConnectionProvider;

/**
 * @author Dmitry Goncharov
 *
 */
public class BaseEventTestCase extends TestCase {
	
	/**
	 * 
	 */
	private static final int NORMAL_TIME = 500;
	
	/**
	 * 
	 */
	private static final int EXTRA_TIME = 1000;

	
	public IEventManagerBackEnd getEventManagerBackEnd() {
		return getSystemConnectionProvider().getBackEndContextProvider().getSystemBackEndContext().getEventManagerBackEnd();		
	}
	
	public void test() throws InterruptedException {
		EventManager eventManager = getEventManager();
		final CallbackResultWaiter eventWaiter = new CallbackResultWaiter();
		TestEventListener frontend = new TestEventListener() {
			public void hello(String text) throws EventExcpetion {
				assertEquals( "World", text );
				eventWaiter.onSuccess( null );
			}
		};
		eventManager.addListener( TestEventListener.class, frontend );
		TestEventListener backend = getEventManagerBackEnd().getEventNotificator( TestEventListener.class );
		Thread.sleep( NORMAL_TIME );
		backend.hello( "World" );
		eventWaiter.waitToResult();
		eventManager.removeListener( TestEventListener.class, frontend );
	}

	/**
	 * @return
	 */
	private TestSystemConnectionProvider getSystemConnectionProvider() {
		return TestSystemConnectionProvider.INSTANCE;
	}
	
	public void testAddRemove() throws InterruptedException {
		EventManager eventManager = getEventManager();
		final AtomicInteger occurenceCount = new AtomicInteger();
		TestEventListener frontend = new TestEventListener() {
			public void hello(String text) throws EventExcpetion {
				occurenceCount.incrementAndGet();		
				assertEquals( "World", text );
				System.out.println( text );
			}
		};
		eventManager.addListener( TestEventListener.class, frontend );
		eventManager.addListener( TestEventListener.class, frontend );
		TestEventListener backend = getEventManagerBackEnd().getEventNotificator( TestEventListener.class );
		Thread.sleep( NORMAL_TIME );
		backend.hello( "World" );
		// Wait to dispatch
		Thread.sleep( NORMAL_TIME );
		assertEquals( 1, occurenceCount.get() );
		// Remove event listener
		eventManager.removeListener( TestEventListener.class, frontend );
		backend.hello( "World" );
		// Wait to dispatch
		Thread.sleep( EXTRA_TIME );
		// Check that nothing happens
		assertEquals( 1, occurenceCount.get() );		 
	}

	/**
	 * @return
	 */
	private EventManager getEventManager() {
		return getSystemConnectionProvider().get().getEventManager();
	}
	
	public void testUnhandled() throws InterruptedException {
		final Connection connection = getSystemConnectionProvider().get();
		EventManager eventManager = connection.getEventManager();
		final AtomicInteger occurenceCount = new AtomicInteger();
		eventManager.setUnhandledEventHandler( new UnhandledEventHandler() {
			public void unhandledEvent(Event event) {
				occurenceCount.incrementAndGet();
			}
		});
		final Category<TestEventListener> category = new Category<TestEventListener>(TestEventListener.class);
		// Subscribe
		connection.getAsyncService( EventProviderAsync.class ).subscribe( category, null );
		Thread.sleep( NORMAL_TIME );
		TestEventListener backend = getEventManagerBackEnd().getEventNotificator( TestEventListener.class );
		backend.hello( null );
		Thread.sleep( NORMAL_TIME );
		// Check that happens
		assertEquals( 1, occurenceCount.get() );
		// Unsubscribe
		connection.getAsyncService( EventProviderAsync.class ).unsubscribe( category, null );
		Thread.sleep( NORMAL_TIME);
		backend.hello( null );
		Thread.sleep( EXTRA_TIME );
		// Check that nothing happens
		assertEquals( 1, occurenceCount.get() );
		eventManager.setUnhandledEventHandler( null);
	}	

}
