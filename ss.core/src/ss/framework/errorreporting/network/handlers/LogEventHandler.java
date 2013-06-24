/**
 * 
 */
package ss.framework.errorreporting.network.handlers;

import ss.framework.errorreporting.ILogStorage;
import ss.framework.errorreporting.network.LogEvent;
import ss.framework.networking2.EventHandler;
import ss.framework.networking2.EventHandlingContext;


/**
 *
 */
public class LogEventHandler extends EventHandler<LogEvent>{

	private final ILogStorage logStorage;
	
	/**
	 * @param notificationClass
	 * @param logStorage
	 */
	public LogEventHandler(ILogStorage logStorage) {
		super(LogEvent.class);
		this.logStorage = logStorage;
	}



	/* (non-Javadoc)
	 * @see ss.framework.networking2.EventHandler#handleEvent(ss.framework.networking2.EventHandlingContext)
	 */
	@Override
	protected void handleEvent(EventHandlingContext<LogEvent> context) {
		LogEvent event = context.getMessage();
		this.logStorage.store(event);		
	}

}
