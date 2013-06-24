package ss.framework.networking2;

public class CommandDispatcher extends ActiveMessageDispatcher {

	@SuppressWarnings("unused")
	private static final org.apache.log4j.Logger logger = ss.global.SSLogger
			.getLogger(CommandDispatcher.class);
	
	private final IMessageHandlingResultListener resultListener = new CommandHandingResultListener();
	
	private static class CommandHandingResultListener extends LoggerMessageHandingResultListener {

		/**
		 * @param logger
		 */
		public CommandHandingResultListener() {
			super(logger);
		}

		/* (non-Javadoc)
		 * @see ss.common.networking2.LoggerCommandHandingResultListener#error(ss.common.networking2.CommandHandlerRunner, ss.common.networking2.CommandHandleException)
		 */
		@Override
		public void error(MessageHandlerRunnableAdaptor runnable, CommandHandleException ex) {
			// Don't print exception // super.error(runnable, ex);
			replyFailed( runnable, ex, ErrorReporter.Level.ERROR );
		}

		/* (non-Javadoc)
		 * @see ss.common.networking2.LoggerCommandHandingResultListener#finished(ss.common.networking2.CommandHandlerRunner)
		 */
		@Override
		public void finished(MessageHandlerRunnableAdaptor runnable) {
			super.finished(runnable);
			CommandHandlingContext context = (CommandHandlingContext)runnable.getContext();
			if ( !context.isReplySent() ) {
				if ( logger.isDebugEnabled() ) {
					logger.debug( "Send void reply to " + context.getMessage() );
				}
				context.reply(new VoidReply());
			}
		}

		/* (non-Javadoc)
		 * @see ss.common.networking2.LoggerCommandHandingResultListener#unexpectedError(ss.common.networking2.CommandHandlerRunner, java.lang.RuntimeException)
		 */
		@Override
		public void unexpectedError(MessageHandlerRunnableAdaptor runnable, RuntimeException ex) {
			// Don't print exception // super.unexpectedError(runnable, ex);
			replyFailed( runnable, ex, ErrorReporter.Level.UNEXPECTED_ERROR );			
		}	
		
		/**
		 * @param command
		 * @param ex
		 */
		@SuppressWarnings("unchecked")
		private void replyFailed(MessageHandlerRunnableAdaptor runnable, Exception ex, ErrorReporter.Level level  ) {
			CommandHandlingContext<Command> context = (CommandHandlingContext<Command>) runnable.getContext();
			context.replyFailed( ErrorReporter.INSTANCE.report( context.getMessage(), ex, level ) );
		}
	}
	
	 
	
	/**
	 * @param manager
	 * @param handler
	 * @param handlerExecutor
	 */
	public CommandDispatcher(ActiveMessageHandlingManager manager, CommandHandler handler, IHandlerExecutor handlerExecutor) {
		super(manager, handler, handlerExecutor);
	}

	/* (non-Javadoc)
	 * @see ss.common.networking2.ActiveMessageDispatcher#dispach(ss.common.networking2.ActiveMessage)
	 */
	@Override
	public void dispachMessage(ActiveMessage message) {
		CommandHandlingContext<Command> context = new CommandHandlingContext<Command>( getProtocolOwner(), (Command)message);
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
