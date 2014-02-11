/**
 * 
 */
package ss.lab.dm3.events;

import ss.lab.dm3.connection.service.backend.BackEndContext;
import ss.lab.dm3.connection.service.backend.BackEndFeatures;
import ss.lab.dm3.events.backend.AbstractNotificatorFilter;
import ss.lab.dm3.events.backend.EventManagerBackEnd;
import ss.lab.dm3.events.services.EventProviderBackEnd;

/**
 * @author Dmitry Goncharov
 */
public class FilterTestCase {


	public static void main(String[] args) {
		final EventManagerBackEnd eventManagerBackEnd = new EventManagerBackEnd();
		final BackEndContext context1 = new TestBackEndContext( eventManagerBackEnd, "Context#1");
		final EventProviderBackEnd eventProvider1 = new EventProviderBackEnd() {

			/* (non-Javadoc)
			 * @see ss.lab.dm3.events.services.EventProviderBackEnd#createEventNotificator(ss.lab.dm3.events.Category)
			 */
			@Override
			protected <T extends EventListener> T createEventNotificator(
					Category<T> category) {
				return category.getEventListenerClass().cast( 
					new TestEventListener() {
						public void hello(String text) throws EventExcpetion {
							System.out.println( text );
						}
					}
				);
			}
		};
		eventProvider1.initialize( context1 );
		final Category<TestEventListener> category = Category.create(TestEventListener.class);
		TestEventListener notificator = eventManagerBackEnd.getEventNotificator(category);
		eventManagerBackEnd.addEventFilter( category, TestEventFilter.class );
		eventProvider1.subscribe(category);
		notificator.hello( "Hello,World!" );
	}
	
	public static class TestBackEndFeatures extends BackEndFeatures {

		/**
		 * @param configuation
		 */
		public TestBackEndFeatures( EventManagerBackEnd eventManagerBackEnd ) {
			super(null);
			setEventManagerBackEnd(eventManagerBackEnd);
		}
		
		
	}
	
	public static class TestBackEndContext extends BackEndContext {

		/**
		 * @param eventManagerBackEnd 
		 * @param eventManagerBackEnd
		 * @param securityManagerBackEnd
		 * @param dataManagerBackEnd
		 * @param authentication
		 */
		public TestBackEndContext( EventManagerBackEnd eventManagerBackEnd, String id ) {
			super( id, new TestBackEndFeatures( eventManagerBackEnd ), null	);
		}
		
	}
	
	public static class TestEventFilter extends AbstractNotificatorFilter<TestEventListener> implements TestEventListener {
		/* (non-Javadoc)
		 * @see ss.lab.dm3.events.TestEventListener#hello(java.lang.String)
		 */
		public void hello(String text) throws EventExcpetion {
			getImpl().hello( "Filtered " + text + " by " + ((TestBackEndContext)getContext()).getId() );
		}
	}
	
	
}
