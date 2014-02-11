package ss.lab.dm3.events;

import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.TestCase;

import ss.lab.dm3.connection.Connection;
import ss.lab.dm3.events.EventListener;
import ss.lab.dm3.events.EventManager;
import ss.lab.dm3.testsupport.TestSystemConnectionProvider;
import ss.lab.dm3.MockHelper;

/**
 * @author Dmitry Goncharov
 *
 */
public class ProxyTest extends TestCase {

	/**
	 * 
	 */
	private static final int TRY_COUNT = 100;
	private static final boolean DISABLED = true;

	public static interface TestListener extends EventListener {
		void execute();
	}
	
	public void testRefelction() {
		if ( DISABLED ) {
			return;
		}
		final AtomicInteger count = new AtomicInteger();
		TestListener testListener = null;/*BroadCastEventNotificator.create( new EventPumpBackEnd() {
			public void add(Event event) {
				count.incrementAndGet();
				
			}
			
		}, new Category<TestListener>(TestListener.class) );*/
		if ( testListener != null ) { 
			long startTime = System.currentTimeMillis();
			for( int n = 0; n < 10000; ++ n ) {
				testListener.execute();
			}
			long endTime = System.currentTimeMillis();
			System.out.println( (endTime - startTime) + ", Count " + count.get() );
		}
	}
	
	public void testEvent() throws InterruptedException {
		if ( DISABLED ) {
			return;
		}
		Connection connection = TestSystemConnectionProvider.INSTANCE.get();
		EventManager eventManager = connection.getEventManager();
		final AtomicInteger count = new AtomicInteger();
		TestListener frontend = new TestListener() {
			public void execute()  {
				count.incrementAndGet();
				try {
					Thread.sleep( 10 );
				} catch (InterruptedException ex) {
				}
			}
		};
		eventManager.addListener( TestListener.class, frontend );
		Thread.sleep( 1000 );
		TestListener backend = MockHelper.getEventManagerBackEnd().getEventNotificator( TestListener.class );
		long startTime = System.currentTimeMillis();
		for( int n = 0; n < TRY_COUNT; ++ n ) {
			backend.execute();
		}
		while( count.get() != TRY_COUNT ) {
			Thread.sleep( 10 );
		}
		long endTime = System.currentTimeMillis();
		System.out.println( (endTime - startTime) + ", Count " + count.get() );
		Thread.sleep( 1000 );
	}
}
