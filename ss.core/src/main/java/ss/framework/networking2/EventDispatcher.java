package ss.framework.networking2;


public class EventDispatcher extends ActiveMessageDispatcher {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(EventDispatcher.class);
	
	private final IMessageHandlingResultListener resultListener = new EventHandlingResultListener();
	
	private final class EventHandlingResultListener extends LoggerMessageHandingResultListener {

		/**
		 * @param logger
		 */
		public EventHandlingResultListener() {
			super(logger);
		}

		/* (non-Javadoc)
		 * @see ss.common.networking2.LoggerMessageHandingResultListener#finished(ss.common.networking2.MessageHandlerRunnableAdaptor)
		 */
		@SuppressWarnings("unchecked")
		@Override
		public synchronized void finished(MessageHandlerRunnableAdaptor runner) {
			super.finished(runner);
			//
			// #TODO test cascade event executing
			// Check that message calling running under corect executors
			//
			EventHandlingContext<Event> context = (EventHandlingContext<Event>) runner.getContext();
			EventHandler<Event> handler = (EventHandler<Event>) runner.getHandler();
			if ( !context.isCancelBuble() ) {
				final Class currentAcceptableClass = handler.getAcceptableMessageClass();
				final Class nextAcceptableClass = currentAcceptableClass.getSuperclass(); 
				EventDispatcher nextDispatcher = (EventDispatcher) getManagerOwner().findDispatcher( nextAcceptableClass );
				if ( nextDispatcher != null ) {
					nextDispatcher.executeHandler(context);
				}						
			}			
		}
		
		
		
	}
	/**
	 * @param manager
	 * @param handler
	 * @param handlerExecutor
	 */
	public EventDispatcher(ActiveMessageHandlingManager manager, EventHandler handler, IHandlerExecutor handlerExecutor) {
		super(manager, handler, handlerExecutor);
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.ActiveMessageDispatcher#dispach(ss.common.networking2.ActiveMessage)
	 */
	@Override
	public void dispachMessage(ActiveMessage message) {
		EventHandlingContext<Event> context = new EventHandlingContext<Event>((Event)message);
		super.executeHandler( context );
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.ActiveMessageDispatcher#getDefaultResultListener()
	 */
	@Override
	protected IMessageHandlingResultListener getResultListener() {
		return this.resultListener;
	}

	


	
	
}
